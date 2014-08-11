package com.todpop.sweetenglish;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.TypefaceFragmentActivity;

public class StudyLevel extends TypefaceFragmentActivity {

	// Slide Level Page
	ViewPager pageView;
	// PageAdapter for pageView
	PagerAdapter pagerAdapter;
	// Fragment attached to pageView
	LevelFragment levelFragment;

	static SharedPreferences studyInfo;
	static SharedPreferences.Editor studyInfoEdit;
	SharedPreferences rgInfo;
	
	
	// TMP page number and stage information
	int levelLast = 1;
	int stage = 0;
	
	//study category parameter
	Bundle bundle;
	static int levelStart;
	//page point index count
	int pageViewPointIndexCount;
	int currentViewCount;
	ImageView[] pointView;
	int fregmentCount = 1;
	
	SharedPreferences checkStageIsNew;
	
	static String userId = "0";
	static int category = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_level);
		
		rgInfo = getSharedPreferences("rgInfo",0);		// User ID
		studyInfo = getSharedPreferences("studyInfo",0);;
		studyInfoEdit = studyInfo.edit();

		userId = rgInfo.getString("mem_id", "0");
		studyInfo = getSharedPreferences("studyInfo",0);
		
		// levelCount could be 1, 16, 61, 121 etc... 
		category = studyInfo.getInt("lastCategory", 1);
		if (category == 1)		{levelStart = 1;}
		else if (category == 2)	{levelStart = 16;}
		else if (category == 3)	{levelStart = 61;}
		else					{levelStart = 121;}		// category 4
		
		pageView = (ViewPager)findViewById(R.id.studylearn_id_pager);
		pagerAdapter = new PagerAdapter(getSupportFragmentManager());	
		pageView.setAdapter(pagerAdapter);
		
		// 1. Calculate Page Count for each Section. 
		if(category == 1) {
			levelLast = studyInfo.getInt("levelLast1", 1);
			pageView.setCurrentItem((levelLast-1)/3);
			currentViewCount = (levelLast-1)/3;
			pageViewPointIndexCount = 5;
			Log.e("SL1",String.valueOf(levelLast));
		} else if(category == 2) {
			levelLast = studyInfo.getInt("levelLast2", 16);
			currentViewCount=(levelLast-16)/3;
			if (currentViewCount <0)	currentViewCount = 0;
			pageView.setCurrentItem(currentViewCount);
			pageViewPointIndexCount = 15;
			Log.e("SL2",String.valueOf(levelLast));
		} else if(category == 3) {
			levelLast = studyInfo.getInt("levelLast3", 61);
			currentViewCount=(levelLast-61)/3;
			if (currentViewCount <0)	currentViewCount = 0;
			pageView.setCurrentItem(currentViewCount);
			pageViewPointIndexCount = 20;
			Log.e("SL3",String.valueOf(levelLast));
		} else if(category == 4) {
			levelLast = studyInfo.getInt("levelLast4", 121);
			currentViewCount=(levelLast-121)/3;
			if (currentViewCount <0) 	currentViewCount = 0;
			pageView.setCurrentItem(currentViewCount);
			pageViewPointIndexCount = 20;
			Log.e("SL4",String.valueOf(levelLast));
		}
		
		
		pageView.setOnPageChangeListener(new OnPageChangeListener() {    
		     @Override public void onPageSelected(int position) {
		    	 changePointerViewIndicator(position);
		         
		     }
		    @Override public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {}
		    @Override public void onPageScrollStateChanged(int state) {}
		});
		
		
		LinearLayout pointViewLayout=(LinearLayout)findViewById(R.id.studylearn_id_pagepointview);
		//LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		pointView = new ImageView[pageViewPointIndexCount];
 		for(int i = 0; i < pageViewPointIndexCount; i++) {
 			pointView[i] = new ImageView(this);

			if (i == currentViewCount) {
				pointView[i].setImageResource(R.drawable.study_4_img_indicator_yellow_on);
			} else {
				pointView[i].setImageResource(R.drawable.study_4_img_indicator_white_off);
			}
			pointView[i].setPadding(5, 5, 5, 5);
			pointViewLayout.addView(pointView[i]);
		}

		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Study Level");
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	@Override
	protected void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
	private void changePointerViewIndicator(int position) {
		for(int i = 0; i < pageViewPointIndexCount; i++) {
			if (i == position) {
				pointView[i].setImageResource(R.drawable.study_4_img_indicator_yellow_on);
			} else {
				pointView[i].setImageResource(R.drawable.study_4_img_indicator_white_off);
			}
		}
	}

	public  class PagerAdapter extends FragmentStatePagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}
		@Override
		public Fragment getItem(int i) {

			Fragment fragment = new LevelFragment();
			Bundle args = new Bundle();

			args.putInt("page",i);

			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			if(category == 1)		{fregmentCount = 5;}
			else if(category ==2 )	{fregmentCount = 15;}
			else if(category == 3)	{fregmentCount = 20;}
			else if(category == 4)	{fregmentCount = 20;}
			return fregmentCount;
		}
	}

	public static class LevelFragment extends Fragment {


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_study_level, container, false);



			TextView levelText1 = (TextView)rootView.findViewById(R.id.studylearn_id_level_text1);
			TextView levelText2 = (TextView)rootView.findViewById(R.id.studylearn_id_level_text2);
			TextView levelText3 = (TextView)rootView.findViewById(R.id.studylearn_id_level_text3);
			
			setFont(levelText1);
			setFont(levelText2);
			setFont(levelText3);

			Bundle args = getArguments();
			
			int labelNumber = levelStart + 3 * (args.getInt("page"));

			levelText1.setText("level  "+Integer.toString(labelNumber));
			levelText2.setText("level  "+Integer.toString(labelNumber+1));
			levelText3.setText("level  "+Integer.toString(labelNumber+2));
			
			adjustLevelStageButtonStatus(rootView, labelNumber);
			
			return rootView;
		}
		
		public void adjustLevelStageButtonStatus(View rootView, final int firstLevel)
		{
			String firstStageInfo;
			if(firstLevel == levelStart){
				firstStageInfo = studyInfo.getString("level" + firstLevel, "oxxxxxxxxx");
				Editor stdInfoEdit = studyInfo.edit();
				stdInfoEdit.putString("level" + firstLevel, firstStageInfo);
				stdInfoEdit.apply();
			}
			else{
				firstStageInfo = studyInfo.getString("level" + firstLevel, "xxxxxxxxxx");
			}
			final String stageInfoFirst = firstStageInfo;
			final String stageInfoSecond = studyInfo.getString("level" + (firstLevel + 1), "xxxxxxxxxx");
			final String stageInfoThird = studyInfo.getString("level" + (firstLevel + 2), "xxxxxxxxxx");
			
			if (!stageInfoFirst.equals("xxxxxxxxxx")) {
				
				Button button1 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_1);
				Button button2 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_2);
				Button button3 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_3);
				Button button4 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_4);
				Button button5 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_5);
				Button button6 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_6);      
				Button button7 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_7);               
				Button button8 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_8);                
				Button button9 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_9); 
				Button button10 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_10);
				
				button1.setTag((firstLevel-1)*10+1);
				button2.setTag((firstLevel-1)*10+2);
				button3.setTag((firstLevel-1)*10+3);
				button4.setTag((firstLevel-1)*10+4);
				button5.setTag((firstLevel-1)*10+5);
				button6.setTag((firstLevel-1)*10+6);
				button7.setTag((firstLevel-1)*10+7);
				button8.setTag((firstLevel-1)*10+8);
				button9.setTag((firstLevel-1)*10+9);
				button10.setTag(firstLevel*10);
				
				if(stageInfoFirst.charAt(0)=='1')		{button1.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_1_s);}
				else if(stageInfoFirst.charAt(0)=='2')	{button1.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_1_g);}
				
				if(stageInfoFirst.charAt(1)=='1')		{button2.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_2_s);}
				else if(stageInfoFirst.charAt(1)=='2')	{button2.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_2_g);}

				if(stageInfoFirst.charAt(2)=='1')		{button3.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_3_s);}
				else if(stageInfoFirst.charAt(2)=='2')	{button3.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_3_g);}

				if(stageInfoFirst.charAt(3)=='1')		{button4.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_4_s);}
				else if(stageInfoFirst.charAt(3)=='2')	{button4.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_4_g);}

				if(stageInfoFirst.charAt(4)=='1')		{button5.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_5_s);}
				else if(stageInfoFirst.charAt(4)=='2')	{button5.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_5_g);}

				if(stageInfoFirst.charAt(5)=='1')		{button6.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_6_s);}
				else if(stageInfoFirst.charAt(5)=='2')	{button6.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_6_g);}

				if(stageInfoFirst.charAt(6)=='1')		{button7.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_7_s);}
				else if(stageInfoFirst.charAt(6)=='2')	{button7.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_7_g);}

				if(stageInfoFirst.charAt(7)=='1')		{button8.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_8_s);}
				else if(stageInfoFirst.charAt(7)=='2')	{button8.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_8_g);}

				if(stageInfoFirst.charAt(8)=='1')		{button9.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_9_s);}
				else if(stageInfoFirst.charAt(8)=='2')	{button9.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_9_g);}

				if(stageInfoFirst.charAt(9)=='1')		{button10.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_10_s);}
				else if(stageInfoFirst.charAt(9)=='2')	{button10.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_10_g);}


				button1.setOnClickListener(clickListener);
				button2.setOnClickListener(clickListener);
				button3.setOnClickListener(clickListener);
				button4.setOnClickListener(clickListener);
				button5.setOnClickListener(clickListener);
				button6.setOnClickListener(clickListener);
				button7.setOnClickListener(clickListener);
				button8.setOnClickListener(clickListener);
				button9.setOnClickListener(clickListener);
				button10.setOnClickListener(clickListener);



				
				Log.d("------ ", "-------------------------------------------");
				Log.d("Level --- ", stageInfoFirst);
				Log.d("------ ", "-------------------------------------------");

				char s1 = stageInfoFirst.charAt(0);
				char s2 = stageInfoFirst.charAt(1);
				char s3 = stageInfoFirst.charAt(2);
				char s4 = stageInfoFirst.charAt(3);
				char s5 = stageInfoFirst.charAt(4);
				char s6 = stageInfoFirst.charAt(5);
				char s7 = stageInfoFirst.charAt(6);
				char s8 = stageInfoFirst.charAt(7);
				char s9 = stageInfoFirst.charAt(8);
				char s10 = stageInfoFirst.charAt(9);
				
				if (s1!='x')	{	button1.setEnabled(true);}
				if (s2!='x')	{	button2.setEnabled(true);}
				if (s3!='x')	{	button3.setEnabled(true);}
				if (s4!='x')	{	button4.setEnabled(true);}
				if (s5!='x')	{	button5.setEnabled(true);}
				if (s6!='x')	{	button6.setEnabled(true);}
				if (s7!='x')	{	button7.setEnabled(true);}
				if (s8!='x')	{	button8.setEnabled(true);}
				if (s9!='x')	{	button9.setEnabled(true);}
				if (s10!='x')	{	button10.setEnabled(true);}
			} 

			
			if (!stageInfoSecond.equals("xxxxxxxxxx")) {

				Button button1 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_1);
				Button button2 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_2);
				Button button3 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_3);
				Button button4 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_4);
				Button button5 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_5);
				Button button6 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_6);      
				Button button7 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_7);               
				Button button8 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_8);                
				Button button9 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_9); 
				Button button10 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_10);
				
				button1.setTag((firstLevel)*10+1);
				button2.setTag((firstLevel)*10+2);
				button3.setTag((firstLevel)*10+3);
				button4.setTag((firstLevel)*10+4);
				button5.setTag((firstLevel)*10+5);
				button6.setTag((firstLevel)*10+6);
				button7.setTag((firstLevel)*10+7);
				button8.setTag((firstLevel)*10+8);
				button9.setTag((firstLevel)*10+9);
				button10.setTag((firstLevel + 1)*10);
				
				if(stageInfoSecond.charAt(0)=='1')		{button1.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_1_s);}
				else if(stageInfoSecond.charAt(0)=='2')	{button1.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_1_g);}
				
				if(stageInfoSecond.charAt(1)=='1')		{button2.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_2_s);}
				else if(stageInfoSecond.charAt(1)=='2')	{button2.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_2_g);}

				if(stageInfoSecond.charAt(2)=='1')		{button3.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_3_s);}
				else if(stageInfoSecond.charAt(2)=='2')	{button3.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_3_g);}

				if(stageInfoSecond.charAt(3)=='1')		{button4.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_4_s);}
				else if(stageInfoSecond.charAt(3)=='2')	{button4.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_4_g);}

				if(stageInfoSecond.charAt(4)=='1')		{button5.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_5_s);}
				else if(stageInfoSecond.charAt(4)=='2')	{button5.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_5_g);}

				if(stageInfoSecond.charAt(5)=='1')		{button6.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_6_s);}
				else if(stageInfoSecond.charAt(5)=='2')	{button6.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_6_g);}

				if(stageInfoSecond.charAt(6)=='1')		{button7.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_7_s);}
				else if(stageInfoSecond.charAt(6)=='2')	{button7.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_7_g);}

				if(stageInfoSecond.charAt(7)=='1')		{button8.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_8_s);}
				else if(stageInfoSecond.charAt(7)=='2')	{button8.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_8_g);}

				if(stageInfoSecond.charAt(8)=='1')		{button9.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_9_s);}
				else if(stageInfoSecond.charAt(8)=='2')	{button9.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_9_g);}

				if(stageInfoSecond.charAt(9)=='1')		{button10.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_10_s);}
				else if(stageInfoSecond.charAt(9)=='2')	{button10.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_10_g);}

				button1.setOnClickListener(clickListener);
				button2.setOnClickListener(clickListener);
				button3.setOnClickListener(clickListener);
				button4.setOnClickListener(clickListener);
				button5.setOnClickListener(clickListener);
				button6.setOnClickListener(clickListener);
				button7.setOnClickListener(clickListener);
				button8.setOnClickListener(clickListener);
				button9.setOnClickListener(clickListener);
				button10.setOnClickListener(clickListener);


				//String levelCountStr = "Level" + Integer.toString(firstLevel+1);
				//int levelStageCount = prefs.getInt(levelCountStr, 1);
				//Log.d("Level --- ", levelCountStr);
				//Log.d("Count --- ", Integer.toString(levelStageCount));
				
				Log.d("------ ", "-------------------------------------------");
				Log.d("Level --- ", stageInfoSecond);
				Log.d("------ ", "-------------------------------------------");

				char s1 = stageInfoSecond.charAt(0);
				char s2 = stageInfoSecond.charAt(1);
				char s3 = stageInfoSecond.charAt(2);
				char s4 = stageInfoSecond.charAt(3);
				char s5 = stageInfoSecond.charAt(4);
				char s6 = stageInfoSecond.charAt(5);
				char s7 = stageInfoSecond.charAt(6);
				char s8 = stageInfoSecond.charAt(7);
				char s9 = stageInfoSecond.charAt(8);
				char s10 = stageInfoSecond.charAt(9);
				
				if (s1!='x')	{	button1.setEnabled(true);}
				if (s2!='x')	{	button2.setEnabled(true);}
				if (s3!='x')	{	button3.setEnabled(true);}
				if (s4!='x')	{	button4.setEnabled(true);}
				if (s5!='x')	{	button5.setEnabled(true);}
				if (s6!='x')	{	button6.setEnabled(true);}
				if (s7!='x')	{	button7.setEnabled(true);}
				if (s8!='x')	{	button8.setEnabled(true);}
				if (s9!='x')	{	button9.setEnabled(true);}
				if (s10!='x')	{	button10.setEnabled(true);}

			}

			if (!stageInfoThird.equals("xxxxxxxxxx")) {

				Button button1 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_1);
				Button button2 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_2);
				Button button3 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_3);
				Button button4 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_4);
				Button button5 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_5);
				Button button6 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_6);      
				Button button7 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_7);               
				Button button8 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_8);                
				Button button9 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_9); 
				Button button10 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_10);
				
				button1.setTag((firstLevel+2-1)*10+1);
				button2.setTag((firstLevel+2-1)*10+2);
				button3.setTag((firstLevel+2-1)*10+3);
				button4.setTag((firstLevel+2-1)*10+4);
				button5.setTag((firstLevel+2-1)*10+5);
				button6.setTag((firstLevel+2-1)*10+6);
				button7.setTag((firstLevel+2-1)*10+7);
				button8.setTag((firstLevel+2-1)*10+8);
				button9.setTag((firstLevel+2-1)*10+9);
				button10.setTag((firstLevel+2)*10);
				
				if(stageInfoThird.charAt(0)=='1')		{button1.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_1_s);}
				else if(stageInfoThird.charAt(0)=='2')	{button1.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_1_g);}
				
				if(stageInfoThird.charAt(1)=='1')		{button2.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_2_s);}
				else if(stageInfoThird.charAt(1)=='2')	{button2.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_2_g);}

				if(stageInfoThird.charAt(2)=='1')		{button3.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_3_s);}
				else if(stageInfoThird.charAt(2)=='2')	{button3.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_3_g);}

				if(stageInfoThird.charAt(3)=='1')		{button4.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_4_s);}
				else if(stageInfoThird.charAt(3)=='2')	{button4.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_4_g);}

				if(stageInfoThird.charAt(4)=='1')		{button5.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_5_s);}
				else if(stageInfoThird.charAt(4)=='2')	{button5.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_5_g);}

				if(stageInfoThird.charAt(5)=='1')		{button6.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_6_s);}
				else if(stageInfoThird.charAt(5)=='2')	{button6.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_6_g);}

				if(stageInfoThird.charAt(6)=='1')		{button7.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_7_s);}
				else if(stageInfoThird.charAt(6)=='2')	{button7.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_7_g);}

				if(stageInfoThird.charAt(7)=='1')		{button8.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_8_s);}
				else if(stageInfoThird.charAt(7)=='2')	{button8.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_8_g);}

				if(stageInfoThird.charAt(8)=='1')		{button9.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_9_s);}
				else if(stageInfoThird.charAt(8)=='2')	{button9.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_9_g);}

				if(stageInfoThird.charAt(9)=='1')		{button10.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_10_s);}
				else if(stageInfoThird.charAt(9)=='2')	{button10.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_10_g);}


				button1.setOnClickListener(clickListener);
				button2.setOnClickListener(clickListener);
				button3.setOnClickListener(clickListener);
				button4.setOnClickListener(clickListener);
				button5.setOnClickListener(clickListener);
				button6.setOnClickListener(clickListener);
				button7.setOnClickListener(clickListener);
				button8.setOnClickListener(clickListener);
				button9.setOnClickListener(clickListener);
				button10.setOnClickListener(clickListener);


				
				Log.d("------ ", "-------------------------------------------");
				Log.d("Level --- ", stageInfoThird);
				Log.d("------ ", "-------------------------------------------");

				char s1 = stageInfoThird.charAt(0);
				char s2 = stageInfoThird.charAt(1);
				char s3 = stageInfoThird.charAt(2);
				char s4 = stageInfoThird.charAt(3);
				char s5 = stageInfoThird.charAt(4);
				char s6 = stageInfoThird.charAt(5);
				char s7 = stageInfoThird.charAt(6);
				char s8 = stageInfoThird.charAt(7);
				char s9 = stageInfoThird.charAt(8);
				char s10 = stageInfoThird.charAt(9);
				
				if (s1!='x')	{	button1.setEnabled(true);}
				if (s2!='x')	{	button2.setEnabled(true);}
				if (s3!='x')	{	button3.setEnabled(true);}
				if (s4!='x')	{	button4.setEnabled(true);}
				if (s5!='x')	{	button5.setEnabled(true);}
				if (s6!='x')	{	button6.setEnabled(true);}
				if (s7!='x')	{	button7.setEnabled(true);}
				if (s8!='x')	{	button8.setEnabled(true);}
				if (s9!='x')	{	button9.setEnabled(true);}
				if (s10!='x')	{	button10.setEnabled(true);}

			}
			
		}
		
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				int currentBtnClickStage = (Integer)(v.getTag());
				int selectedStageNo = (currentBtnClickStage - 1) % 10 + 1;
				
				studyInfoEdit.putInt("tmpStageAccumulated", currentBtnClickStage);
				studyInfoEdit.apply();
				
				if(selectedStageNo != 10){
					Intent myIntent = new Intent(getActivity(), StudyBegin.class);
					getActivity().startActivity(myIntent);
				}
				else{
					Intent myIntent = new Intent(getActivity(), StudyTest10.class);
					getActivity().startActivity(myIntent);
				}
				
			}
		};
		
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	public void onClickBack(View view)
	{
		finish();
	}
	
	public void goBackToHome(View v) 
	{	
		Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	@Override
	public void onResume() 
	{
		super.onResume();
	}
	
	@Override
	public void onRestart()
	{
		super.onRestart();
		startActivity(new Intent(this, StudyLevel.class));
		finish();
	}

}
