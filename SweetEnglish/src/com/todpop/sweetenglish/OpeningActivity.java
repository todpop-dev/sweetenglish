package com.todpop.sweetenglish;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;
import com.todpop.api.TypefaceActivity;
import com.todpop.sweetenglish.SweetEnglish.TrackerName;
import com.todpop.sweetenglish.db.WordDBHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
		
		SharedPreferences studyInfo = getSharedPreferences("studyInfo", 0);
		Editor studyInfoEditor = studyInfo.edit();
		
		SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
		didSawHomeTuto = userInfo.getBoolean("homeTuto", false);
		
		String lastStudiedDate = studyInfo.getString("lastStudiedDate", "");
		String yesterday = getDate(1);
		String today = getDate(0);

		if(!lastStudiedDate.equals(yesterday) && !lastStudiedDate.equals(today)){	//if last studied date is neither yesterday nor today
			studyInfoEditor.putInt("continuousStudy", 0);
			studyInfoEditor.apply();
		}

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
		
		startLockService();
		
		dbHelper = new WordDBHelper(this);
		new Thread(new Runnable(){
			public void run(){
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				try{
					db.execSQL("CREATE TABLE word_groups ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
							"name TEXT NOT NULL UNIQUE);");
					db.execSQL("CREATE TABLE dic ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
							"name TEXT, mean TEXT, example_en TEXT, example_ko TEXT, phonetics TEXT, picture INTEGER, image_url TEXT, stage INTEGER, xo TEXT);");
					db.execSQL("CREATE TABLE dailygoal ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
							"date TEXT NOT NULL UNIQUE, count INTEGER);");
					db.execSQL("CREATE TABLE mywords ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
							"group_name TEXT NOT NULL, name TEXT NOT NULL UNIQUE, mean TEXT);");
					db.execSQL("CREATE TABLE flip ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
							"name TEXT, mean TEXT, xo TEXT);");
					db.execSQL("CREATE TABLE mywordtest ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
							"name TEXT, mean TEXT, xo TEXT);");		
				}catch(Exception e){
					
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
	private void startLockService(){
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
