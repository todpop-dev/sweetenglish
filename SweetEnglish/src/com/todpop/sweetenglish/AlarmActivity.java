package com.todpop.sweetenglish;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.todpop.sweetenglish.SweetEnglish.TrackerName;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AlarmActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_alarm);		
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Alarm Activity");
	}
	@Override
	protected void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	public void onClickClose(View v){
		finish();
	}
	
	public void onClickStart(View v){
		Intent intent = new Intent(this, OpeningActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}
	
}
