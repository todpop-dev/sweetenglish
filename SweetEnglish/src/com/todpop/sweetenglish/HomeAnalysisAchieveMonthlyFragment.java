package com.todpop.sweetenglish;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.todpop.sweetenglish.db.AnalysisDBHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeAnalysisAchieveMonthlyFragment extends Fragment{
	
	ArrayList<ProgressBar> progressBarList;
	ArrayList<Integer> progressList;
	
	private int maxProgress = 0;
	
	private int year;
	
	private AnalysisDBHelper aHelper;
	private SQLiteDatabase mDB;
	
	static HomeAnalysisAchieveMonthlyFragment init(){
		return new HomeAnalysisAchieveMonthlyFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		progressBarList = new ArrayList<ProgressBar>();
		progressList = new ArrayList<Integer>();
		
		getProgress();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_goal_monthly, container, false);

		for(int i = 0; i < 12; i++){
			progressBarList.add((ProgressBar)v.findViewById(R.id.anal_goal_monthly_month_1 + i));
		}
		
		TextView titleYear = (TextView)(v.findViewById(R.id.anal_goal_monthly_year));
		titleYear.setText(year + getResources().getString(R.string.com_year));
		
		return v;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		new Thread(new Runnable(){
			public void run(){
				for(int i = 0; i < maxProgress - 1; i++){
					try {
						Thread.sleep(15);
						yearlyHandler.sendEmptyMessage(i + 1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	private void getProgress(){
		aHelper = new AnalysisDBHelper(getActivity().getApplicationContext());
		mDB = aHelper.getReadableDatabase();
		
		SimpleDateFormat yyyyMM = new SimpleDateFormat("yyyyMM", Locale.getDefault());
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);		
		
		year = cal.get(Calendar.YEAR);
		
		for(int i = 1; i < 13; i++){
			int maxWeek = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
			Cursor cursor = mDB.rawQuery("SELECT SUM(achieve) FROM weekly_achieve WHERE yymmweek >= " + yyyyMM.format(cal.getTime()) + 1 
										+ " AND yymmweek <= " + yyyyMM.format(cal.getTime()) + maxWeek, null);
			if(cursor.moveToFirst()){
				int progress = cursor.getInt(0) / maxWeek;
				progressList.add(progress);
				
				if(maxProgress < progress){
					maxProgress = progress;
				}
			}
			else{
				progressList.add(0);
			}
			cal.add(Calendar.MONTH, 1);
		}
	}
	
	Handler yearlyHandler = new Handler(){
		public void handleMessage(Message msg){
			for(int i = 0; i < 12; i++){
				int progress = progressList.get(i);
				
				if(msg.what <= progress){
					progressBarList.get(i).incrementProgressBy(1);
				}
			}
		}
	};	
}
