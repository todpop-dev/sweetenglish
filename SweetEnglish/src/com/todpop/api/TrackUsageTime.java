package com.todpop.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import com.todpop.sweetenglish.db.UsageTimeDBHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

public class TrackUsageTime {
	private static TrackUsageTime tTime;
	
	private UsageTimeDBHelper uHelper;
	
	private SimpleDateFormat dateFormat;
	private String date;
	
	private Queue<Long> startTimeQueue;
	private long stopTime;
	public static TrackUsageTime getInstance(Context c){
		if(tTime == null){
			tTime = new TrackUsageTime(c);
		}
		return tTime;
	}
	
	private TrackUsageTime(Context c){
		uHelper = new UsageTimeDBHelper(c);
		dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		startTimeQueue = new LinkedList<Long>();
	}
	
	public void attend(){
		date = dateFormat.format(Calendar.getInstance().getTime());
		
		new AttendToDB().execute(date);
	}
	
	public void start(){
		startTimeQueue.offer(SystemClock.elapsedRealtime());
	}
	public void stop(){		
		stopTime = SystemClock.elapsedRealtime();

		date = dateFormat.format(Calendar.getInstance().getTime());
		int activeTime = (int)((stopTime - startTimeQueue.poll()) / 1000);
		new SaveTimeToDB().execute(date, String.valueOf(activeTime));
	}
	
	private class AttendToDB extends AsyncTask<String, Void, Void>{
		@Override
		protected Void doInBackground(String... arg0) {
			SQLiteDatabase db = uHelper.getWritableDatabase();
			
			db.execSQL("INSERT OR REPLACE INTO daily_usage (date, usage_time, attend) " +
						"VALUES (" + arg0[0] + ", " +
								"COALESCE((SELECT usage_time FROM daily_usage WHERE date = " + arg0[0] + "), 0), " +
								"COALESCE((SELECT attend FROM daily_usage WHERE date = " + arg0[0] + ") + 1, 1)" +
								")");
			uHelper.close();
			return null;
		}
	}
	private class SaveTimeToDB extends AsyncTask<String, Void, Void>{
		@Override
		protected Void doInBackground(String... arg0) {
			SQLiteDatabase db = uHelper.getWritableDatabase();
			
			db.execSQL("INSERT OR REPLACE INTO daily_usage (date, usage_time, attend) " +
						"VALUES (" + arg0[0] + ", " +
								"COALESCE((SELECT usage_time FROM daily_usage WHERE date = " + arg0[0] + ") + " + arg0[1] + ", " + arg0[1] + "), " +
								"COALESCE((SELECT attend FROM daily_usage WHERE date = " + arg0[0] + "), 1)" +
								")");
			uHelper.close();
			return null;
		}
	}
}
