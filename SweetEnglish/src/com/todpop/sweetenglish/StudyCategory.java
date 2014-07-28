package com.todpop.sweetenglish;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.TypefaceActivity;

public class StudyCategory extends TypefaceActivity {
	ViewPager categoryPager;
	
	SharedPreferences studyInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_category);
		
		categoryPager = (ViewPager)findViewById(R.id.category_viewpager);
		
		categoryPager.setAdapter(new StudyCategoryPagerAdapter(this));
		categoryPager.setOffscreenPageLimit(2);
		int margin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 88, getResources().getDisplayMetrics());
		categoryPager.setPageMargin(-margin);
		
		studyInfo = getSharedPreferences("studyInfo", 0);
		
		categoryPager.setCurrentItem(studyInfo.getInt("lastCategory", 1) - 1);
		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
	}
	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Study Category");
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	@Override
	protected void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
	public void onClickInfiniteTest(View v){
		Intent intent = new Intent(getApplicationContext(), StudyTestInfinite.class);
		startActivity(intent);
	}
	public void onClickStart(View v){
		saveInfoAndStudy(categoryPager.getCurrentItem() + 1);
	}
	private void saveInfoAndStudy(int category){
		SharedPreferences.Editor studyInfoEditor = studyInfo.edit();
		studyInfoEditor.putInt("lastCategory", category);
		studyInfoEditor.apply();
		
		Intent intent = new Intent(getApplicationContext(), StudyLevel.class);
		startActivity(intent);
	}
	public void onClickBack(View v){
		finish();
	}
}
