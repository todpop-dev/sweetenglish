package com.todpop.sweetenglish;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.todpop.sweetenglish.R;
import com.todpop.sweetenglish.R.id;
import com.todpop.sweetenglish.R.layout;
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

public class HomeDailyGraphFragment extends Fragment{
	
	private ArrayList<ProgressBar> progressBarList;
	private ArrayList<Integer> progressList;
	private int maxProgress;
	
	AnalysisDBHelper aHelper;
	
	static HomeDailyGraphFragment init(){
		HomeDailyGraphFragment graphFragment = new HomeDailyGraphFragment();
		return graphFragment;
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		progressBarList = new ArrayList<ProgressBar>();
		progressList = new ArrayList<Integer>();
		
		aHelper = new AnalysisDBHelper(getActivity());
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View layoutView = inflater.inflate(R.layout.fragment_home_graph, container, false);
		
		progressBarList.clear();
		
		for(int i = 0; i < 7; i++){
			progressBarList.add((ProgressBar)layoutView.findViewById(R.id.home_progress_mon + i));
		}
		
		return layoutView;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		new Thread(new Runnable(){
			public void run(){
				setProgressList();
				
				for(int i = 0; i < 7; i++){
					progressBarList.get(i).setProgress(0);
				}
				
				for(int i = 0; i < maxProgress; i++){
					try {
						Thread.sleep(20);
						progressHandler.sendEmptyMessage(i);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	private void setProgressList(){
		
		progressList.clear();
		
		maxProgress = 0;
		
		SQLiteDatabase db = aHelper.getReadableDatabase();
		
		SimpleDateFormat form = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		
		String today = form.format(calendar.getTime());
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		
		if(dayOfWeek == Calendar.MONDAY){
			Cursor cursor = db.rawQuery("SELECT achieve FROM daily_achieve WHERE date = " + today,  null);
			
			if(cursor.moveToFirst()){
				progressList.add(cursor.getInt(0));
				maxProgress = cursor.getInt(0);
			}
		}
		else{
			calendar.setFirstDayOfWeek(Calendar.MONDAY);
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			String weekStart = form.format(calendar.getTime());
			int current = Integer.valueOf(weekStart);
			
			Cursor cursor = db.rawQuery("SELECT date, achieve FROM daily_achieve WHERE date >= " + weekStart + " AND date <= " + today, null);
			
			while(cursor.moveToNext()){
				while(cursor.getInt(0) != current){
					progressList.add(0);	
					current++;
				}
				
				progressList.add(cursor.getInt(1));
				
				if(maxProgress < cursor.getInt(1))
					maxProgress = cursor.getInt(1);
				
				current++;
			}
		}
		
		while(progressList.size() < 7){
			progressList.add(0);
		}
	}
	
	
	private Handler progressHandler = new Handler(){
		public void handleMessage(Message msg){
			for(int i = 0; i < 7; i++){
				int progress = progressList.get(i);
				
				if(msg.what < progress){
					progressBarList.get(i).incrementProgressBy(1);
				}
			}
		}
	};
}
