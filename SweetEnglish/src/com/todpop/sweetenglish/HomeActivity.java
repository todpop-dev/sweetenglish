package com.todpop.sweetenglish;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;
import com.todpop.api.KakaoObject;
import com.todpop.api.StudyHistoryAnalysis;
import com.todpop.api.TrackUsageTime;
import com.todpop.api.TypefaceActivity;
import com.todpop.api.TypefaceFragmentActivity;
import com.todpop.api.request.GetKakao;
import com.todpop.api.request.GetNotice;
import com.todpop.sweetenglish.SweetEnglish.TrackerName;
import com.todpop.sweetenglish.db.AnalysisDBHelper;
import com.todpop.sweetenglish.db.UsageTimeDBHelper;
import com.todpop.sweetenglish.db.WordDBHelper;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeActivity extends TypefaceFragmentActivity{
	private static final int IS_MAJOR_UPDATE = 0;
	private static final int IS_MINOR_UPDATE = 1;
	private static final int ANALYSIS_ATTEND = 0;
	private static final int ANALYSIS_MEMORIZE = 1;
	private static final int ANALYSIS_ACHIEVE = 2;
	private DrawerLayout drawerLayout;
	private ListView drawerMenu;
	
	//drawer
	private String userNick;
	private int userLevel;
	
	private TextView attendanceText;
	private static TextView memorizeText;
	private TextView goalText;
	
	private WordDBHelper dbHelper;
	private SQLiteDatabase db;
	private AnalysisDBHelper aHelper;
	private SQLiteDatabase aDB;

	private ViewPager analysisViewPager;
	private LinearLayout analysisText;
	private ImageView indi1;
	private ImageView indi2;
	private ImageView indi3;
	
	private Integer userGoal;
	
	//for analysis fragments
	private int totalStudied;
	private int memorized;
	
	private KakaoObject kakaoObj;
	
	private SharedPreferences userInfo;
	private SharedPreferences studyInfo;
	private SharedPreferences missionInfo;
	private Editor missionEditor;
	
	private PopupWindow exitPopup;
	private static PopupWindow updatePopup;
	private static boolean isMajor = false;
	
	private DialogFragment dialogFragment;
	
	private TrackUsageTime tTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_home);
		
		drawerLayout = (DrawerLayout)findViewById(R.id.home_drawer_layout);
		drawerMenu = (ListView)findViewById(R.id.home_drawer_menu);

		userInfo = getSharedPreferences("userInfo", 0);
		studyInfo = getSharedPreferences("studyInfo", 0);
		missionInfo = getSharedPreferences("missionInfo", 0);
		missionEditor = missionInfo.edit();
		
		drawerMenu.setOnItemClickListener(new HomeDrawerItemClickListener());
		
		attendanceText = (TextView)findViewById(R.id.home_text_attendance);
		memorizeText = (TextView)findViewById(R.id.home_text_memorize);
		goalText = (TextView)findViewById(R.id.home_text_goal);
        
        dbHelper = new WordDBHelper(this);
        
        analysisViewPager = (ViewPager)findViewById(R.id.home_analysis_pager);
        analysisText = (LinearLayout)findViewById(R.id.home_analysis_text_explain);
        indi1 = (ImageView)findViewById(R.id.home_indicator_1);
        indi2 = (ImageView)findViewById(R.id.home_indicator_2);
        indi3 = (ImageView)findViewById(R.id.home_indicator_3);
        analysisViewPager.setOffscreenPageLimit(3);
        
        View exitView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_exit_app, null);
        ImageView popupCancel = (ImageView)exitView.findViewById(R.id.iv_exit_app_popup_cancel);
        popupCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				exitPopup.dismiss();
				finish();
			}
        });
        
        exitPopup = new PopupWindow(exitView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        exitPopup.setFocusable(false);

        setAnalysisDialog();
        setUpdatePopup();
        
		kakaoObj = new KakaoObject();
		new GetNotice(getApplicationContext(), new NoticeHandler()).execute();
		new GetKakao(kakaoObj).execute();
		
		checkMissionOnCreate();
		
		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);

        analysisViewPager.setAdapter(new HomeDailyFragmentPagerAdapter(getSupportFragmentManager()));
        analysisViewPager.setOnPageChangeListener(new HomeOnPageChangeListener());
        
        tTime = TrackUsageTime.getInstance(this);
	}
	@Override
	protected void onResume(){
		super.onResume();
		
		userNick = userInfo.getString("userNick", "No_Nickname");
		userLevel = userInfo.getInt("userLevel", 1);
		
		drawerMenu.setAdapter(new HomeDrawerAdapter(this, userNick, userLevel));

		userGoal = userInfo.getInt("userGoal", 30);	
    	new StudyHistoryAnalysis(this).saveTodayAchieveRate(userGoal); //update AnalysisDB
    	
        db = dbHelper.getReadableDatabase();
        
        setContinuousStudyPercentage();
        setMemorizePercentage();
        
		setGoalProgress();

        analysisViewPager.getAdapter().notifyDataSetChanged();
        
        checkMissionOnResume();
        
		db.close();
		dbHelper.close();
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Home Activity");
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
	@Override
	protected void onPause(){
		super.onPause();
	}
	private void checkMissionOnCreate(){
		boolean newComer = missionInfo.getBoolean("newComer", false);
		if(!newComer){
			missionEditor.putBoolean("newComer", true);
			missionEditor.apply();
			
			dialogFragment = MissionGetDialogFragment.newInstance(MissionGetDialogFragment.NEW_COMER);
			dialogFragment.show(getSupportFragmentManager(), "newComer");
		}
		else{
			int attend = studyInfo.getInt("continuousStudy", 0);
			
			if(attend >= 30){
				boolean attend1Month = missionInfo.getBoolean("attend1Month", false);
				if(!attend1Month){
					missionEditor.putBoolean("attend1Month", true);
					missionEditor.putBoolean("attend10", true);
					missionEditor.putBoolean("attend7", true);
					missionEditor.putBoolean("attend3", true);
					missionEditor.apply();
					
					//show
					dialogFragment = MissionGetDialogFragment.newInstance(MissionGetDialogFragment.ATTEND_1MONTH);
					dialogFragment.show(getSupportFragmentManager(), "attend1Month");
				}
			}
			else if(attend >= 10){
				boolean attend10 = missionInfo.getBoolean("attend10", false);
				if(attend10){
					missionEditor.putBoolean("attend10", true);
					missionEditor.putBoolean("attend7", true);
					missionEditor.putBoolean("attend3", true);
					missionEditor.apply();
					
					//show
					dialogFragment = MissionGetDialogFragment.newInstance(MissionGetDialogFragment.ATTEND_10);
					dialogFragment.show(getSupportFragmentManager(), "attend10");
				}
			}
			else if(attend >= 7){
				boolean attend7 = missionInfo.getBoolean("attend7", false);
				if(attend7){
					missionEditor.putBoolean("attend7", true);
					missionEditor.putBoolean("attend3", true);
					missionEditor.apply();
					
					//show
					dialogFragment = MissionGetDialogFragment.newInstance(MissionGetDialogFragment.ATTEND_7);
					dialogFragment.show(getSupportFragmentManager(), "attend7");
				}
			}
			else if(attend >= 3){
				boolean attend3 = missionInfo.getBoolean("attend3", false);
				if(attend3){
					missionEditor.putBoolean("attend3", true);
					missionEditor.apply();
					
					//show
					dialogFragment = MissionGetDialogFragment.newInstance(MissionGetDialogFragment.ATTEND_3);
					dialogFragment.show(getSupportFragmentManager(), "attend3");
				}
			}
		}
	}
	private void checkMissionOnResume(){
		boolean hasGoalSetHistory = missionInfo.getBoolean("goalSetHistory", false);
		int inviteHistory =  missionInfo.getInt("inviteHistory", 0);
		boolean hasTime5History = missionInfo.getBoolean("time5", false);
		boolean hasEarlyBirdHistory = missionInfo.getBoolean("timeEarlyBird", false);
		boolean hasOwlHistory = missionInfo.getBoolean("timeOwl", false);
		
		if(hasGoalSetHistory){
			boolean plan = missionInfo.getBoolean("plan", false);
			if(plan == false){
				missionEditor.putBoolean("plan", true);
				missionEditor.apply();
				
				//show
				dialogFragment = MissionGetDialogFragment.newInstance(MissionGetDialogFragment.PLAN);
				dialogFragment.show(getSupportFragmentManager(), "plan");
			}
		}
		else if(inviteHistory >= 10){
			boolean invite = missionInfo.getBoolean("invite", false);
			if(invite == false){
				missionEditor.putBoolean("invite", true);
				missionEditor.apply();
				
				//show
				dialogFragment = MissionGetDialogFragment.newInstance(MissionGetDialogFragment.INVITE);
				dialogFragment.show(getSupportFragmentManager(), "invite");
			}
		}
		else if(hasTime5History == false){
			new CheckUsageTime().execute();
		}
		else if(hasEarlyBirdHistory == false){
			int attendTotal = studyInfo.getInt("attendHour6", 0) + studyInfo.getInt("attendHour7", 0) + studyInfo.getInt("attendHour8", 0);
			if(attendTotal >= 30){
				missionEditor.putBoolean("timeEarlyBird", true);
				missionEditor.apply();
				
				dialogFragment = MissionGetDialogFragment.newInstance(MissionGetDialogFragment.TIME_EARLY);
				dialogFragment.show(getSupportFragmentManager(), "timeEarlyBird");
			}
		}
		else if(hasOwlHistory == false){
			int attendTotal = studyInfo.getInt("attendHour22", 0) + studyInfo.getInt("attendHour23", 0) + studyInfo.getInt("attendHour0", 0);
			if(attendTotal >= 30){
				missionEditor.putBoolean("timeOwl", true);
				missionEditor.apply();
				
				dialogFragment = MissionGetDialogFragment.newInstance(MissionGetDialogFragment.TIME_OWL);
				dialogFragment.show(getSupportFragmentManager(), "timeOwl");
			}
		}
	}
	public void onClickDrawer(View v){
		drawerLayout.openDrawer(drawerMenu);
	}
	public void onClickFAQ(View v){
		 Intent intent = new Intent(getApplicationContext(), HomeMoreFAQ.class);
		 startActivity(intent);
	}
	public void onClickUserImg(View v){
		 Intent intent = new Intent(getApplicationContext(), HomeMoreSetting.class);
		 startActivity(intent);
	}
	public void onClickWordBook(View v){
		if(!userInfo.getBoolean("wordbookTuto", false)){
			Intent intent = new Intent(getApplicationContext(), TutorialActivity.class);
			intent.putExtra("tutoType", 2);
			startActivity(intent);
		}
		else{
			Intent intent = new Intent(getApplicationContext(), HomeWordListGroup.class);
			startActivity(intent);
		}
	}
	public void onClickStudy(View v){
		 Intent intent = new Intent(getApplicationContext(), StudyCategory.class);
		 startActivity(intent);
	}
    public void onClickAttendance(View v){
    	dialogFragment = HomeAnalysisDialogFragment.newInstance(ANALYSIS_ATTEND, null, null);
		dialogFragment.show(getSupportFragmentManager(), "attend");   
    }
    public void onClickMemorizeRate(View v){
    	dialogFragment = HomeAnalysisDialogFragment.newInstance(ANALYSIS_MEMORIZE, totalStudied, memorized);
		dialogFragment.show(getSupportFragmentManager(), "memorize");    	
    }
    public void onClickAchieveRate(View v){
    	dialogFragment = HomeAnalysisDialogFragment.newInstance(ANALYSIS_ACHIEVE, null, null);
		dialogFragment.show(getSupportFragmentManager(), "achieve");
    }
	private void setContinuousStudyPercentage(){
		int continuousStudy = studyInfo.getInt("continuousStudy", 0);
		
		attendanceText.setText(String.valueOf(continuousStudy));
	}
	private void setMemorizePercentage(){
		Cursor memorizedCursor = db.rawQuery("SELECT count(*) FROM dic WHERE xo=\'O\'", null);
		Cursor totalCursor = db.rawQuery("SELECT count(*) FROM dic", null);
		
		memorizedCursor.moveToFirst();
		totalCursor.moveToFirst();
		
		memorized = memorizedCursor.getInt(0);
		totalStudied = totalCursor.getInt(0);
		
		int memorizedRate = (int)((double)memorized / (double)totalStudied * 100.0);
		
		memorizeText.setText(String.valueOf(memorizedRate));
	}
	private void setGoalProgress(){    	
    	aHelper = new AnalysisDBHelper(this);
    	aDB = aHelper.getReadableDatabase();    	
    	
		SimpleDateFormat form = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		
		String today = form.format(calendar.getTime());
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		
		int progressSum = 0;
		
		if(dayOfWeek == Calendar.MONDAY){
			progressSum = addTodayData(today);
		}
		else{
			calendar.setFirstDayOfWeek(Calendar.MONDAY);
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			String weekStart = form.format(calendar.getTime());
				
			progressSum = addPeriodData(weekStart, today);
		}

		aHelper.close();
		
		int weeklyGoal = progressSum / 7;
		
		goalText.setText(String.valueOf(weeklyGoal));
	}	
	private void setAnalysisDialog(){
		//analysisDialog = HomeAnalysisDialogFragment.newInstance();
	}
	private void setUpdatePopup(){
		 View updateView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_update_app, null);
	     ImageView popupOk = (ImageView)updateView.findViewById(R.id.iv_update_app_popup_cancel);
	     popupOk.setOnClickListener(new OnClickListener(){
	    	 @Override
	    	 public void onClick(View v) {
	    		 if(isMajor){
	    			 finish();
	    		 }
	    		 else{
	    			 updatePopup.dismiss();
	    		 }
	    	 }
	     });
	        
	     updatePopup = new PopupWindow(updateView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
	}
	private int addTodayData(String today){
		int result = 0;
		
		Cursor cursor = aDB.rawQuery("SELECT achieve FROM daily_achieve WHERE date = " + today,  null);
		
		if(cursor.moveToFirst()){
			result = cursor.getInt(0);
		}
		
		return result;
	}
	
	private int addPeriodData(String start, String end){
		int result = 0;
		
		Cursor cursor = aDB.rawQuery("SELECT SUM(achieve) FROM daily_achieve WHERE date >= " + start + " AND date <= " + end, null);
		
		if(cursor.moveToFirst()){
			result += cursor.getInt(0);
		}
		
		return result;
	}
	
	class HomeDrawerItemClickListener implements ListView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
			Intent intent = null;
			switch(position){
			case 1:
				 intent = new Intent(getApplicationContext(), HomeMoreGoal.class);
				startActivity(intent);
				break;
			case 2:
				sendKakaoLink();
				break;
			case 3:
				 intent = new Intent(getApplicationContext(), HomeMoreLockerSetting.class);
				 startActivity(intent);
				break;
			case 4:
				 intent = new Intent(getApplicationContext(), HomeMoreNotice.class);
				 startActivity(intent);
				break;
			case 5:
				 intent = new Intent(getApplicationContext(), HomeMoreSetting.class);
				 startActivity(intent);
				break;
			case 6:
				 intent = new Intent(getApplicationContext(), HomeMoreMission.class);
				 startActivity(intent);
				break;
			case 7:
				 intent = new Intent(getApplicationContext(), HomeMoreContact.class);
				 startActivity(intent);
				break;
			}
		}
	}

	private void sendKakaoLink(){
		try {
			final KakaoLink kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
			final KakaoTalkLinkMessageBuilder kaKaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
			final String linkContents;
			if(kakaoObj.getImgSrc() != null){
				linkContents = kaKaoTalkLinkMessageBuilder.addText(kakaoObj.getMent())
															.addImage(kakaoObj.getImgSrc(), kakaoObj.getImgWidth(), kakaoObj.getImgHeight())
															.addAppButton(kakaoObj.getBtnText())
															.build();
			}
			else{
				linkContents = kaKaoTalkLinkMessageBuilder.addText(kakaoObj.getMent())
															.addAppButton(kakaoObj.getBtnText())
															.build();
			}
			
			kakaoLink.sendMessage(linkContents, this);
			int inviteHistory = missionInfo.getInt("inviteHistory", 0);
			missionEditor.putInt("inviteHistory", inviteHistory + 1);
			missionEditor.apply();
		} catch (KakaoParameterException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed(){
		if(drawerLayout.isDrawerOpen(drawerMenu)){
			drawerLayout.closeDrawer(drawerMenu);
		}
		else{
			if(exitPopup != null && !exitPopup.isShowing()){
				exitPopup.showAtLocation(memorizeText, Gravity.CENTER, 0, 0);
			}
			else if(exitPopup != null && exitPopup.isShowing()){
				exitPopup.dismiss();
			}
		}
	}
	
	private static class NoticeHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case IS_MAJOR_UPDATE:
				isMajor = true;
				updatePopup.showAtLocation(memorizeText, Gravity.CENTER, 0, 0);
				break;
			case IS_MINOR_UPDATE:
				isMajor = false;
				updatePopup.showAtLocation(memorizeText, Gravity.CENTER, 0, 0);
				break;
			}
		}
	};
	
	private class HomeOnPageChangeListener implements OnPageChangeListener{
		Animation in = null;
		Animation out = null;

		boolean isFromPageOne = true;
		
		@Override
		public void onPageScrollStateChanged(int arg0) {}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}

		@Override
		public void onPageSelected(int position) {
			if(position == 0){		
				if(out == null){
					out = AnimationUtils.loadAnimation(HomeActivity.this, android.R.anim.fade_out);
					out.setFillAfter(true);
				}
				analysisText.startAnimation(out);
				indi1.setImageResource(R.drawable.tutorial_img_indicator_white_on);
				indi2.setImageResource(R.drawable.tutorial_img_indicator_navy_off);
				isFromPageOne = true;
			}
			else if(position == 1){
				if(in == null){
					in = AnimationUtils.loadAnimation(HomeActivity.this, android.R.anim.fade_in);
					in.setFillAfter(true);
					analysisText.setVisibility(View.VISIBLE);
				}
				if(isFromPageOne){
					analysisText.startAnimation(in);
					isFromPageOne = false;
				}
				indi1.setImageResource(R.drawable.tutorial_img_indicator_navy_off);
				indi2.setImageResource(R.drawable.tutorial_img_indicator_white_on);
				indi3.setImageResource(R.drawable.tutorial_img_indicator_navy_off);
			}
			else{
				indi2.setImageResource(R.drawable.tutorial_img_indicator_navy_off);
				indi3.setImageResource(R.drawable.tutorial_img_indicator_white_on);
			}
		}
	}
	
	private class CheckUsageTime extends AsyncTask<Void, Void, Integer>{
		@Override
		protected Integer doInBackground(Void... arg0) {
			UsageTimeDBHelper uHelper = new UsageTimeDBHelper(HomeActivity.this);
			SQLiteDatabase db = uHelper.getReadableDatabase();
			
			Cursor cursor = db.rawQuery("SELECT SUM(usage_time) FROM daily_usage", null);
			
			int sum = 0;
			if(cursor.moveToFirst())
				sum = cursor.getInt(0);
			
			
			return sum;
		}
		@Override
		protected void onPostExecute(Integer result){
			super.onPostExecute(result);

			boolean hasTime1History = missionInfo.getBoolean("time1", false);
			boolean hasTime3History = missionInfo.getBoolean("time3", false);
			
			if(hasTime1History == false){
				missionEditor.putBoolean("time1", true);
				missionEditor.apply();
				
				dialogFragment = MissionGetDialogFragment.newInstance(MissionGetDialogFragment.TIME_1);
				dialogFragment.show(getSupportFragmentManager(), "time1");
			}
			else if(hasTime3History == false){
				missionEditor.putBoolean("time3", true);
				missionEditor.apply();

				dialogFragment = MissionGetDialogFragment.newInstance(MissionGetDialogFragment.TIME_3);
				dialogFragment.show(getSupportFragmentManager(), "time3");
			}
			else{
				missionEditor.putBoolean("time5", true);
				missionEditor.apply();

				dialogFragment = MissionGetDialogFragment.newInstance(MissionGetDialogFragment.TIME_5);
				dialogFragment.show(getSupportFragmentManager(), "time5");
			}
		}
	}
}
