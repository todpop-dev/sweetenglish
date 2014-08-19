package com.todpop.sweetenglish;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.TrackUsageTime;
import com.todpop.api.TypefaceActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class HomeMoreGoal extends TypefaceActivity {

	ArrayList<String> titleArray = new ArrayList<String>() ;
	ArrayList<String> itemArray = new ArrayList<String>();

	private int curFrontNum;
	private int curBackNum;
	private ImageButton ibFrontNum;
	private ImageButton ibBackNum;
	private int goalDailyWords;
	private SharedPreferences sp;

	TrackUsageTime tTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_goal);

		sp = getSharedPreferences("userInfo", 0);
		goalDailyWords= sp.getInt("userGoal", 30);

		ibFrontNum = (ImageButton)findViewById(R.id.ib_goal_front_num);
		ibBackNum= (ImageButton)findViewById(R.id.ib_goal_back_num);

		initGoalNumber();

		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
		tTime = TrackUsageTime.getInstance(this);
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
	private void initGoalNumber() {
		int frontNum = goalDailyWords / 10;
		int backNum = goalDailyWords % 10;

		curFrontNum = frontNum;
		curBackNum = backNum;

		updateGoalNumb();
	}

	public void addGoal(View v){
		if(getGoalNum() != 99){
			curBackNum++;
			if(curBackNum == 10){
				curFrontNum++;
				curBackNum = 0;
			}
			updateGoalNumb();
		}
	}

	public void subGoal(View v){
		if(getGoalNum() != 0){
			curBackNum--;
			if(curBackNum == -1){
				curFrontNum--;
				curBackNum = 9;
			}
			updateGoalNumb();
		}
	}

	public void updateGoalNumb(){
		ibFrontNum.setImageResource(R.drawable.goal_img_number_0+curFrontNum);
		ibBackNum.setImageResource(R.drawable.goal_img_number_0+curBackNum);
	}

	// on click
	public void onClickBack(View view)
	{
		finish();
	}

	public void setGoal(View v){
		Editor editor = sp.edit();
		editor.putInt("userGoal", getGoalNum());
		editor.apply();
		finish();
	}

	int getGoalNum(){
		return (curFrontNum*10)+curBackNum;
	}

}



