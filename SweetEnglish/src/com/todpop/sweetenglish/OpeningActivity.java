package com.todpop.sweetenglish;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.StudyHistoryAnalysis;
import com.todpop.api.TypefaceActivity;
import com.todpop.sweetenglish.db.WordDBHelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class OpeningActivity extends TypefaceActivity {
	WordDBHelper dbHelper;
	
	boolean didSawHomeTuto;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opening);
		


		final ImageView todpop = (ImageView) findViewById(R.id.opening_img_todpop);

		final Animation fadeOut = AnimationUtils.loadAnimation(this,
				R.anim.opening_fade_out);
		
		fadeOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
				if(!didSawHomeTuto){
					Intent intent = new Intent(getApplicationContext(), SetNicknameActivity.class);
					startActivity(intent);
					finish();
				}
				else{
					Intent intent = new Intent(getApplicationContext(),
							HomeActivity.class);
					startActivity(intent);
					finish();
				}
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationStart(Animation arg0) {
			}

		});
		
		todpop.startAnimation(fadeOut);
		
		checkAndStartLockService();
		
		new Thread(new Runnable(){
			public void run(){
				SharedPreferences studyInfo = getSharedPreferences("studyInfo", 0);
				Editor studyInfoEditor = studyInfo.edit();
				
				SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
				didSawHomeTuto = userInfo.getBoolean("homeTuto", false);
				
				String lastStudiedDate = studyInfo.getString("lastStudiedDate", "");
				String yesterday = getDate(1);
				String today = getDate(0);

				if(lastStudiedDate.equals("")){
					studyInfoEditor.putString("lastStudiedDate", today);
					studyInfoEditor.apply();
				}
				else if(!lastStudiedDate.equals(today)){
					if(!lastStudiedDate.equals(yesterday)){					//if last studied date is not yesterday
						studyInfoEditor.putInt("continuousStudy", 0);
					}
					else{
						int lastContinuousStudy = studyInfo.getInt("continuousStudy", 0);
						studyInfoEditor.putInt("continuousStudy", lastContinuousStudy + 1);
					}

					studyInfoEditor.putString("lastStudiedDate", today);
					studyInfoEditor.apply();
					
					new StudyHistoryAnalysis(OpeningActivity.this).saveAchieveRate(studyInfo.getInt("userGoal", 30), lastStudiedDate);
				}
			}
		}).start();

		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Opening");
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	@Override
	protected void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
	private void checkAndStartLockService(){
		if(!LockScreenService.isRunning){	//if service is not running
			SharedPreferences setting = getSharedPreferences("setting", 0);
			if(setting.getBoolean("lockerEnabled", true)){
				Intent i = new Intent(this, LockScreenService.class);
				startService(i);
			}
		}
	}
	
	private String getDate(int minusDate){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		Calendar cal = Calendar.getInstance();
		if(minusDate > 0)
			cal.add(Calendar.DATE, -minusDate);
		return dateFormat.format(cal.getTime());
	}
}
