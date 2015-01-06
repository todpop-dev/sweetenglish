package com.todpop.sweetenglish;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.TrackUsageTime;
import com.todpop.api.TypefaceActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class HomeMoreMissionBox extends TypefaceActivity {
	ImageView basicPlan;
	ImageView basicInvite;
	ImageView basicAlarm;
	ImageView basicAttend3;
	ImageView basicAttend7;
	ImageView basicAttend10;
	ImageView basicAttend1Month;
	
	ImageView studyClearBasic;
	ImageView studyClearMiddle;
	ImageView studyClearHigh;
	ImageView studyClearToeic;
	ImageView studyMasterBasic;
	ImageView studyMasterMiddle;
	ImageView studyMasterHigh;
	ImageView studyMasterToeic;
	ImageView studyInfinite80;
	ImageView studyInfinite100;
	
	ImageView goal10;
	ImageView goal30;
	ImageView goal50;
	ImageView goal1Month;
	
	ImageView time1Hour;
	ImageView time3Hour;
	ImageView time5Hour;
	ImageView timeEarlyBird;
	ImageView timeOwl;
	
	ImageView wordbookGroup;
	ImageView wordbookAddWord;
	ImageView wordbookCellophane;
	ImageView wordbookTest;
	
	ImageView lockScreenBG;
	ImageView lockScreenCategory;
	ImageView lockScreenWordbook;
	
	TrackUsageTime tTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_mission_box);
		
		initBadgeImages();
		setBadgeImages();

		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
		tTime = TrackUsageTime.getInstance(this);
	}

	private void initBadgeImages(){
		basicPlan = (ImageView)findViewById(R.id.mission_box_img_plan);
		basicInvite = (ImageView)findViewById(R.id.mission_box_img_invite);
		basicAlarm = (ImageView)findViewById(R.id.mission_box_img_alarm);
		basicAttend3 = (ImageView)findViewById(R.id.mission_box_img_attend_3);
		basicAttend7 = (ImageView)findViewById(R.id.mission_box_img_attend_7);
		basicAttend10 = (ImageView)findViewById(R.id.mission_box_img_attend_10);
		basicAttend1Month = (ImageView)findViewById(R.id.mission_box_img_attend_1month);
		
		studyClearBasic = (ImageView)findViewById(R.id.mission_box_img_clear_basic);
		studyClearMiddle = (ImageView)findViewById(R.id.mission_box_img_clear_middle);
		studyClearHigh = (ImageView)findViewById(R.id.mission_box_img_clear_high);
		studyClearToeic = (ImageView)findViewById(R.id.mission_box_img_clear_toeic);
		studyMasterBasic = (ImageView)findViewById(R.id.mission_box_img_clear2_basic);
		studyMasterMiddle = (ImageView)findViewById(R.id.mission_box_img_clear2_middle);
		studyMasterHigh = (ImageView)findViewById(R.id.mission_box_img_clear2_high);
		studyMasterToeic = (ImageView)findViewById(R.id.mission_box_img_clear2_toeic);
		studyInfinite80 = (ImageView)findViewById(R.id.mission_box_img_infi_80);
		studyInfinite100 = (ImageView)findViewById(R.id.mission_box_img_infi_100);
		
		goal10 = (ImageView)findViewById(R.id.mission_box_img_goal_10);
		goal30 = (ImageView)findViewById(R.id.mission_box_img_goal_30);
		goal50 = (ImageView)findViewById(R.id.mission_box_img_goal_50);
		goal1Month  = (ImageView)findViewById(R.id.mission_box_img_goal_1month);
		
		time1Hour = (ImageView)findViewById(R.id.mission_box_img_time_1);
		time3Hour = (ImageView)findViewById(R.id.mission_box_img_time_3);
		time5Hour = (ImageView)findViewById(R.id.mission_box_img_time_5);
		timeEarlyBird = (ImageView)findViewById(R.id.mission_box_img_time_earlybird);
		timeOwl = (ImageView)findViewById(R.id.mission_box_img_time_owl);
		
		wordbookGroup = (ImageView)findViewById(R.id.mission_box_img_wordbook_3group);
		wordbookAddWord = (ImageView)findViewById(R.id.mission_box_img_wordbook_30);
		wordbookCellophane = (ImageView)findViewById(R.id.mission_box_img_wordbook_cellophane);
		wordbookTest = (ImageView)findViewById(R.id.mission_box_img_wordbook_test);
	
		lockScreenBG = (ImageView)findViewById(R.id.mission_box_img_screenlock_bgchange);
		lockScreenCategory = (ImageView)findViewById(R.id.mission_box_img_screenlock_category);
		lockScreenWordbook = (ImageView)findViewById(R.id.mission_box_img_screenlock_word);
	}
	private void setBadgeImages(){
		
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Home More Goal");
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
		tTime.start();
	}
	@Override
	protected void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
		tTime.stop();
	}
	
	public void onClickBack(View view)
	{
		finish();
	}
}