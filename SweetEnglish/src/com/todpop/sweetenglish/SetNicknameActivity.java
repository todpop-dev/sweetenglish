package com.todpop.sweetenglish;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.TypefaceActivity;

public class SetNicknameActivity extends TypefaceActivity{
	private EditText nickText;
	
	private Editor userInfoEdit;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_set_nickname);
		
		nickText = (EditText)findViewById(R.id.set_nickname_edit_text);
		
		SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
		userInfoEdit = userInfo.edit();
		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("SetNickNameActivity");
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	@Override
	protected void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
	public void goTutorial(View v){
		userInfoEdit.putString("userNick", nickText.getText().toString());
		userInfoEdit.apply();
		Intent intent = new Intent(getApplicationContext(), TutorialActivity.class);
		intent.putExtra("tutoType", 1);
		startActivity(intent);
		finish();
	}
}