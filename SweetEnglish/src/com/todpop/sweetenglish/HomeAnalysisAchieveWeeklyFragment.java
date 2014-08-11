package com.todpop.sweetenglish;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.todpop.sweetenglish.db.AnalysisDBHelper;
import com.todpop.sweetenglish.db.WordDBHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeAnalysisAchieveWeeklyFragment extends Fragment{
	
	private ArrayList<View> topViewList;
	private ArrayList<ImageView> bottomViewList;
	private ArrayList<Integer> progressList;
	
	private View viewForWeek5;
	private LinearLayout llForWeek5;
	private View viewForWeek6;
	private LinearLayout llForWeek6;

	private ArrayList<TextView> periodList;
	
	private AnalysisDBHelper aHelper;
	private SQLiteDatabase mDB;
	
	private Calendar cal;
	private String month;
	private int maxWeek; 
	
	static HomeAnalysisAchieveWeeklyFragment init(){
		return new HomeAnalysisAchieveWeeklyFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		topViewList = new ArrayList<View>();
		bottomViewList = new ArrayList<ImageView>();
		progressList = new ArrayList<Integer>();
		
		periodList = new ArrayList<TextView>();

		cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		
		getProgress();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_goal_weekly, container, false);
		
		SimpleDateFormat monthForm = new SimpleDateFormat("M", Locale.getDefault());
		TextView titleMonth = (TextView)v.findViewById(R.id.anal_goal_weekly_month);
		month = monthForm.format(cal.getTime());
		titleMonth.setText(month + getResources().getString(R.string.com_month));
		
		viewForWeek5 = v.findViewById(R.id.anal_goal_weekly_week_5_empty);
		viewForWeek6 = v.findViewById(R.id.anal_goal_weekly_week_6_empty);
		
		llForWeek5 = (LinearLayout)v.findViewById(R.id.anal_goal_weekly_week_5_layout);
		llForWeek6 = (LinearLayout)v.findViewById(R.id.anal_goal_weekly_week_6_layout);
		
		topViewList.add(v.findViewById(R.id.anal_goal_weekly_week_1_text));
		topViewList.add(v.findViewById(R.id.anal_goal_weekly_week_2_text));
		topViewList.add(v.findViewById(R.id.anal_goal_weekly_week_3_text));
		topViewList.add(v.findViewById(R.id.anal_goal_weekly_week_4_text));
		topViewList.add(v.findViewById(R.id.anal_goal_weekly_week_5_text));
		topViewList.add(v.findViewById(R.id.anal_goal_weekly_week_6_text));
			
		bottomViewList.add((ImageView)v.findViewById(R.id.anal_goal_weekly_week_1_bar));
		bottomViewList.add((ImageView)v.findViewById(R.id.anal_goal_weekly_week_2_bar));
		bottomViewList.add((ImageView)v.findViewById(R.id.anal_goal_weekly_week_3_bar));
		bottomViewList.add((ImageView)v.findViewById(R.id.anal_goal_weekly_week_4_bar));
		bottomViewList.add((ImageView)v.findViewById(R.id.anal_goal_weekly_week_5_bar));
		bottomViewList.add((ImageView)v.findViewById(R.id.anal_goal_weekly_week_6_bar));
		
		periodList.add((TextView)v.findViewById(R.id.anal_goal_weekly_week_1_period));
		periodList.add((TextView)v.findViewById(R.id.anal_goal_weekly_week_2_period));
		periodList.add((TextView)v.findViewById(R.id.anal_goal_weekly_week_3_period));
		periodList.add((TextView)v.findViewById(R.id.anal_goal_weekly_week_4_period));
		periodList.add((TextView)v.findViewById(R.id.anal_goal_weekly_week_5_period));
		periodList.add((TextView)v.findViewById(R.id.anal_goal_weekly_week_6_period));
		
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

		LinearLayout.LayoutParams initBottomParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
		initBottomParams.weight = 0;
		
		LinearLayout.LayoutParams initTopParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
		initTopParams.weight = 119;
		
		switch(maxWeek){
		case 4:
			periodList.get(3).setGravity(Gravity.CENTER);
			break;
		case 6:
			viewForWeek6.setVisibility(View.VISIBLE);
			llForWeek6.setVisibility(View.VISIBLE);
			periodList.get(5).setVisibility(View.VISIBLE);
			periodList.get(5).setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		case 5:
			viewForWeek5.setVisibility(View.VISIBLE);
			llForWeek5.setVisibility(View.VISIBLE);
			periodList.get(4).setVisibility(View.VISIBLE);
			if(maxWeek != 6)
				periodList.get(4).setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
			break;
		}
			
		for(int i = 0; i < maxWeek; i++){
			topViewList.get(i).setLayoutParams(initTopParams);
			((TextView)topViewList.get(i)).setText("0");
			bottomViewList.get(i).setLayoutParams(initBottomParams);
		}
		
		setPeriod();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		new Thread(new Runnable(){
			public void run(){
				for(int i = 0; i < 100; i++){
					try {
						Thread.sleep(15);
						weeklyHandler.sendEmptyMessage(i + 1);
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
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM", Locale.getDefault());
		
		String yearMonth = dateFormat.format(cal.getTime());
		maxWeek = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
		
		int yymmweek = Integer.valueOf(yearMonth + 1);
		
		Cursor cursor = mDB.rawQuery("SELECT yymmweek, achieve FROM weekly_achieve WHERE yymmweek >= " + yymmweek + " AND yymmweek <= " + yearMonth + maxWeek, null);
		
		while(cursor.moveToNext()){
			while(yymmweek != cursor.getInt(0)){
				progressList.add(0);
				yymmweek++;
			}
			progressList.add(cursor.getInt(1));
			yymmweek++;
		}
		while(progressList.size() < maxWeek){
			progressList.add(0);
		}
		
		aHelper.close();
	}
	
	private void setPeriod(){
		Calendar period = Calendar.getInstance();
		period.setFirstDayOfWeek(Calendar.MONDAY);
		period.set(Calendar.WEEK_OF_MONTH, 1);
		period.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		
		int date = period.get(Calendar.DAY_OF_MONTH);
		
		periodList.get(0).setText(month + ".1-" + month + "." + date);
		period.add(Calendar.DAY_OF_MONTH, 1);
		
		for(int i = 1; i < maxWeek -1; i++){
			periodList.get(i).setText(month + "." + (++date) + "-" + month + "." + (date += 6));
		}
		periodList.get(maxWeek - 1).setText(month + "." + (++date) + "-" + month + "." + period.getActualMaximum(Calendar.DAY_OF_MONTH));
	}
	
	private Handler weeklyHandler = new Handler(){
		public void handleMessage(Message msg){
			LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
			LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
			for(int i = 0; i < maxWeek; i++){
				int progress = progressList.get(i);
				
				if(msg.what <= progress){
					txtParams.weight = 119 - msg.what;
					imgParams.weight = msg.what;
					
					TextView pTxt = (TextView)topViewList.get(i);
					ImageView pImg = bottomViewList.get(i);
					
					pTxt.setText(String.valueOf(msg.what));
					
					pTxt.setLayoutParams(txtParams);
					pImg.setLayoutParams(imgParams);
				}
			}
		}
	};
}
