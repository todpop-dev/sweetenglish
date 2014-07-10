package com.todpop.sweetenglish;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;
import com.todpop.api.KakaoObject;
import com.todpop.api.TypefaceActivity;
import com.todpop.api.request.GetKakao;
import com.todpop.sweetenglish.db.WordDBHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeActivity extends TypefaceActivity{
	
	private DrawerLayout drawerLayout;
	private ListView drawerMenu;
	
	//drawer
	private String userNick;
	private int userLevel;
	
	private TextView attendanceText;
	private TextView memorizeText;
	private TextView goalText;
	
	private WordDBHelper dbHelper;
	private SQLiteDatabase db;
	
	private static ArrayList<Integer> progressList;
	private int weekMemorized;
	
	private static int handlerCnt = 0;
	
	private KakaoObject kakaoObj;
	
	SharedPreferences userInfo;
	SharedPreferences studyInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_home);
		
		drawerLayout = (DrawerLayout)findViewById(R.id.home_drawer_layout);
		drawerMenu = (ListView)findViewById(R.id.home_drawer_menu);

		userInfo = getSharedPreferences("userInfo", 0);
		studyInfo = getSharedPreferences("studyInfo", 0);
		
		drawerMenu.setOnItemClickListener(new HomeDrawerItemClickListener());
		
		attendanceText = (TextView)findViewById(R.id.home_text_attendance);
		memorizeText = (TextView)findViewById(R.id.home_text_memorize);
		goalText = (TextView)findViewById(R.id.home_text_goal);
        
        dbHelper = new WordDBHelper(this);
		
        progressList = new ArrayList<Integer>();
        
		kakaoObj = new KakaoObject();
		new GetKakao(kakaoObj).execute();
	}
	@Override
	protected void onResume(){
		super.onResume();

		userNick = userInfo.getString("userNick", "No_Nickname");
		userLevel = userInfo.getInt("userLevel", 1);
		
		drawerMenu.setAdapter(new HomeDrawerAdapter(this, userNick, userLevel));

        db = dbHelper.getReadableDatabase();
        
        setContinuousStudyPercentage();
        setMemorizePercentage();
		setGoalProgress();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
	}
	
	private String getDate(int minusDate){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		Calendar cal = Calendar.getInstance();
		if(minusDate > 0)
			cal.add(Calendar.DATE, -minusDate);
		return dateFormat.format(cal.getTime());
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
		 Intent intent = new Intent(getApplicationContext(), HomeWordListGroup.class);
		 startActivity(intent);
	}
	public void onClickStudy(View v){
		 Intent intent = new Intent(getApplicationContext(), StudyCategory.class);
		 startActivity(intent);
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
		
		int memorized = (int)((double)memorizedCursor.getInt(0) / (double)totalCursor.getInt(0) * 100.0);
		
		memorizeText.setText(String.valueOf(memorized));
	}
	private void setGoalProgress(){
		int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int userGoal = userInfo.getInt("userGoal", 30);
        progressList.clear();
        weekMemorized = 0;
        handlerCnt = 0;
        
		String currentDate;
        
        for(int i = 0; i < 7; i++){    	        
			ProgressBar pBar = (ProgressBar)findViewById(R.id.home_progress_mon + i);
			pBar.setProgress(0);
			
    		if(dayOfWeek == 1){	//if today is sunday
    			currentDate = getDate(6 - i);

	    		Cursor cursor = db.rawQuery("SELECT count(DISTINCT name) FROM dic WHERE memorized_date = " + Integer.valueOf(currentDate), null);
	       		cursor.moveToFirst();
	       		
	       		weekMemorized += cursor.getInt(0);
	       		progressList.add((int)((double)cursor.getInt(0) / (double)userGoal * 100.0));
    		}
    		else{				//if today is not sunday
    			if(i <= dayOfWeek - 2){
    				currentDate = getDate(dayOfWeek - 2 - i);
    				
    	    		Cursor cursor = db.rawQuery("SELECT count(DISTINCT name) FROM dic WHERE memorized_date = " + Integer.valueOf(currentDate), null);
    	       		cursor.moveToFirst();

    	       		weekMemorized += cursor.getInt(0);
    	       		progressList.add((int)((double)cursor.getInt(0) / (double)userGoal * 100.0));
    			}
    			else{
    				progressList.add(0);
    			}
    		}
        }

		int weeklyGoal = (int)((double)weekMemorized / ((double)userGoal * 7.0) * 100.0);
		goalText.setText(String.valueOf(weeklyGoal));
		
		new Thread(new Runnable(){
			public void run(){
				for(int i = 0; i < 100; i++){
					try {
						handlerCnt++;
						Thread.sleep(20);
						progressHandler.sendEmptyMessage(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	Handler progressHandler = new Handler(){
		public void handleMessage(Message msg){
			for(int i = 0; i < 7; i++){
				int progress = progressList.get(i);
				
				if(handlerCnt < progress){
					ProgressBar pBar = (ProgressBar)findViewById(R.id.home_progress_mon + i);
	    		
					pBar.incrementProgressBy(1);
				}
			}
		}
	};
	
	class HomeDrawerItemClickListener implements ListView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
			drawerLayout.closeDrawer(drawerMenu);
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
				//TODO change it to contact
				 intent = new Intent(getApplicationContext(), LockScreen.class);
				 startActivity(intent);
				break;
			}
		}
	}

	private void sendKakaoLink(){
		try {
			final KakaoLink kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
			final KakaoTalkLinkMessageBuilder kaKaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

			final String linkContents = kaKaoTalkLinkMessageBuilder.addText(kakaoObj.getMent())
																	.addAppButton(kakaoObj.getBtnText())
																	.build();
			
			kakaoLink.sendMessage(linkContents, this);
		} catch (KakaoParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
