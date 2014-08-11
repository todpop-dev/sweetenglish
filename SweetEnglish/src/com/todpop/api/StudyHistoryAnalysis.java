package com.todpop.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.todpop.sweetenglish.db.AnalysisDBHelper;
import com.todpop.sweetenglish.db.DailyHistoryDBHelper;
import com.todpop.sweetenglish.db.WordDBHelper;

public class StudyHistoryAnalysis {
	WordDBHelper wHelper;
	AnalysisDBHelper aHelper;
	DailyHistoryDBHelper dHelper;
	
	SQLiteDatabase wordDB;
	SQLiteDatabase analysisDB;
	
	String lastWeekStart;
	String lastWeekEnd;
	
	public StudyHistoryAnalysis(Context c){
		wHelper = new WordDBHelper(c);
		aHelper = new AnalysisDBHelper(c);
		dHelper = new DailyHistoryDBHelper(c);
	}
	
	public void saveAchieveRate(int userGoal, String lastDay){
		wordDB = wHelper.getReadableDatabase();
		analysisDB = aHelper.getWritableDatabase();
		
		findAndSaveDaily(userGoal, lastDay);
		
		String lastWeek = checkNewWeek(lastDay);
		if(lastWeek != null){		//save weekly achieve rate
			new Thread(new Runnable(){
				@Override
				public void run() {
					SQLiteDatabase db = dHelper.getWritableDatabase();
					
					db.delete("history", null, null);
					dHelper.close();
				}
			}).start();
			
			findAndSaveWeekly(lastWeekStart, lastWeekEnd, lastWeek);
		}
		
		wordDB.close();
		analysisDB.close();
	}
	
	public void saveTodayAchieveRate(int userGoal){
		wordDB = wHelper.getReadableDatabase();
		analysisDB = aHelper.getWritableDatabase();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		
		String today = dateFormat.format(cal.getTime());
		
		if(cal.get(Calendar.WEEK_OF_MONTH) != 1){
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		}
		else{
			cal.set(Calendar.DAY_OF_MONTH, 1);
		}
		String firstDayOfWeek = dateFormat.format(cal.getTime());
		
		String weekNo = today.substring(0, 6) + cal.get(Calendar.WEEK_OF_MONTH);
		
		findAndSaveDaily(userGoal, today);
		findAndSaveWeekly(firstDayOfWeek, today, weekNo);
		
		wordDB.close();
		analysisDB.close();
	}
	
	private void findAndSaveDaily(int userGoal, String date){
		Cursor cntCursor = wordDB.rawQuery("SELECT count(DISTINCT name) FROM dic WHERE memorized_date = " + date, null);
		
		if(cntCursor.moveToFirst()){
			int achieveRate = (int)((double)cntCursor.getInt(0) / (double)userGoal * 100.0);
			
			if(achieveRate > 100){
				achieveRate = 100;
			}

			ContentValues values = new ContentValues();
			values.put("achieve", achieveRate);
			
			if(analysisDB.update("daily_achieve", values, "date = " + date, null) < 1){
				values.put("date", date);
				analysisDB.insert("daily_achieve", null, values);
			}
		}
	}
	
	private void findAndSaveWeekly(String startDate, String endDate, String weekNo){
		Cursor cursor = analysisDB.rawQuery("SELECT SUM(achieve) FROM daily_achieve WHERE date >= " + startDate + " AND date <= " + endDate, null);
		
		if(cursor.moveToFirst()){
			int weeklyAchieve = (int)((double)cursor.getInt(0) / 7);
			
			ContentValues values = new ContentValues();
			values.put("achieve", weeklyAchieve);
			
			if(analysisDB.update("weekly_achieve", values, "yymmweek = " + weekNo, null) < 1){
				values.put("yymmweek", weekNo);
				analysisDB.insert("weekly_achieve", null, values);
			}
		}
	}
	
	private String checkNewWeek(String lastDay){
		int lastYear = Integer.valueOf(lastDay.substring(0, 4));
		int lastMonth = Integer.valueOf(lastDay.substring(4, 6));
		int lastDate = Integer.valueOf(lastDay.substring(6, 8));
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		
		Calendar last = Calendar.getInstance();
		last.set(lastYear, lastMonth - 1, lastDate);
		last.setFirstDayOfWeek(Calendar.MONDAY);
		
		int lastWeek = last.get(Calendar.WEEK_OF_YEAR);
		int lastWeekMonth = last.get(Calendar.WEEK_OF_MONTH);
		
		last.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		lastWeekStart = dateFormat.format(last.getTime());
		
		last.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		lastWeekEnd = dateFormat.format(last.getTime());
		
		Calendar to = Calendar.getInstance();
		to.setFirstDayOfWeek(Calendar.MONDAY);
		
		int thisWeek = to.get(Calendar.WEEK_OF_YEAR);
		
		return lastWeek != thisWeek ? lastDay.substring(0, 6) + lastWeekMonth : null;
	}
	
}
