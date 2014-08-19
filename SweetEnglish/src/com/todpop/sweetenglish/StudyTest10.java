package com.todpop.sweetenglish;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.TrackUsageTime;
import com.todpop.api.TypefaceActivity;
import com.todpop.api.request.DownloadAndPlayPronounce;
import com.todpop.sweetenglish.db.WordDBHelper;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StudyTest10 extends TypefaceActivity{
	private final static boolean LEFT = true;
	private final static boolean RIGHT = false;

	ImageView tuto;
	
	private LinearLayout leftBackground;
	private LinearLayout rightBackground;
	
	private RelativeLayout leftTouch;
	private RelativeLayout rightTouch;
	
	private boolean isTouching = false;
	private boolean touchingArea = LEFT;
	
	private ImageView timer;
	
	private TextView korean;
	private TextView leftEng;
	private TextView rightEng;
	
	private ImageView leftCnt;
	private ImageView rightCnt;
	
	int tmpStageAccumulated;
	
	int quizCount = 0;
	
	ArrayList<KorEngSet> quizList;
	
	String todayDate;
	
	WordDBHelper mHelper;
	SQLiteDatabase db;
	
	DownloadAndPlayPronounce pronounce;
	
	boolean random;
	
	Animation rotateAni;
	
	Editor userInfoEdit;
	
	private TrackUsageTime tTime;
	
	private class KorEngSet{
		public KorEngSet(String kor, String ans, String wro, boolean his){
			korean = kor;
			answer = ans;
			wrong = wro;
			hasHistory = his;
		}
		String korean;
		String answer;
		String wrong;
		boolean hasHistory;
	}
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_study_test_10);
		
		leftBackground = (LinearLayout) findViewById(R.id.study_test_10_bg_left);
		rightBackground = (LinearLayout) findViewById(R.id.study_test_10_bg_right);

		leftTouch = (RelativeLayout) findViewById(R.id.study_test_10_touch_left);
		rightTouch = (RelativeLayout) findViewById(R.id.study_test_10_touch_right);
		
		TouchListener tListener = new TouchListener();
		leftTouch.setOnTouchListener(tListener);
		rightTouch.setOnTouchListener(tListener);
		
		timer = (ImageView) findViewById(R.id.study_test_10_img_timer);
		
		korean = (TextView) findViewById(R.id.study_test_10_text_korean);
		leftEng = (TextView) findViewById(R.id.study_test_10_text_left_english);
		rightEng = (TextView) findViewById(R.id.study_test_10_text_right_english);
		
		leftCnt = (ImageView) findViewById(R.id.study_test_10_img_left_count);
		rightCnt = (ImageView) findViewById(R.id.study_test_10_img_right_count);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		Calendar cal = Calendar.getInstance();
		
		todayDate = dateFormat.format(cal.getTime());
		
		mHelper = new WordDBHelper(this);
		db = mHelper.getWritableDatabase();

		SharedPreferences studyInfo = getSharedPreferences("studyInfo", 0);
		tmpStageAccumulated = studyInfo.getInt("tmpStageAccumulated", 1);
		
		SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
		
		initAni();
		initWords();
		if(!userInfo.getBoolean("test10tuto", false)){
			userInfoEdit = userInfo.edit();
			tuto = (ImageView)findViewById(R.id.study_test_10_tutorial);
			tuto.setBackgroundResource(R.drawable.test_1_img_10tutorial);
			tuto.setVisibility(View.VISIBLE);
		}
		else{
			setQuiz();
		}

		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);		
		tTime = TrackUsageTime.getInstance(this);
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Study Test 10");
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
	private void initAni(){
		rotateAni = AnimationUtils.loadAnimation(this, R.anim.test_10_rotate);
		
		rotateAni.setAnimationListener(new TimerAniListener());
	}

	private void initWords(){
		quizList = new ArrayList<KorEngSet>();
		
		Log.i("STEVEN","start stage is : " + (((tmpStageAccumulated - 1) / 10) * 10) + "    end stage is : " + (tmpStageAccumulated - 1));
		
		Cursor cursor = db.rawQuery("SELECT DISTINCT name,  mean, memorized_date FROM dic WHERE stage > " + (((tmpStageAccumulated - 1) / 10) * 10) + " AND stage <= " + (tmpStageAccumulated - 1) + " ORDER BY RANDOM()", null);
		if (cursor.getCount()>0) {
			while(cursor.moveToNext()){
				// get wrong eng word randomly except same word
				Cursor otherCursor = db.rawQuery("SELECT DISTINCT name FROM dic WHERE name <> '" + cursor.getString(0) + "' ORDER BY RANDOM() LIMIT 1", null);
				otherCursor.moveToNext();
				
				Boolean hasHistory = false;
				if(cursor.getString(2) != null)
					hasHistory = true;
				
				quizList.add(new KorEngSet(cursor.getString(1), cursor.getString(0), otherCursor.getString(0), hasHistory));
			}
		}
		Log.i("STEVEN","cursor size is : " + cursor.getCount() + "    and quizList size is : " + quizList.size());
	}
	private void setQuiz(){
		if(quizList.size() > quizCount){	//quiz left
			KorEngSet korEng = quizList.get(quizCount);
			korean.setText(korEng.korean);
			random = new Random().nextBoolean();
			if(random == LEFT){
				leftEng.setText(korEng.answer);
				rightEng.setText(korEng.wrong);
			}
			else{
				leftEng.setText(korEng.wrong);
				rightEng.setText(korEng.answer);			
			}
			
			int qLeft = quizList.size() - quizCount;
			
			if(qLeft > 99){
				leftCnt.setImageResource(R.drawable.test_2_text_9);
				rightCnt.setImageResource(R.drawable.test_2_text_9);
			}
			else{
				leftCnt.setImageResource(R.drawable.test_2_text_0 + (qLeft / 10));
				rightCnt.setImageResource(R.drawable.test_2_text_0 + (qLeft % 10));
			}
			
			enableTouch();
			timer.startAnimation(rotateAni);
		}
		else{								//no more quiz
			db.close();
			mHelper.close();
			Intent intent = new Intent(this, StudyTestResult.class);
			startActivity(intent);
			finish();
		}
	}
	
	private void checkAnswer(boolean leftOrRight){
		quizCount++;
		
		if(random == leftOrRight){		//correct
			correct();
		}
		else{							//incorrect
			incorrect();
		}
		
		setQuiz();
	}
	
	private void correct(){
		ContentValues values = new ContentValues();

		values.put("xo", "O");
		
		if(!quizList.get(quizCount - 1).hasHistory){
			values.put("memorized_date", todayDate);
		}
		
		db.update("dic", values, "mean = ? AND stage > ? AND stage <= ?", new String[]{quizList.get(quizCount - 1).korean, String.valueOf(((tmpStageAccumulated - 1) / 10) * 10), String.valueOf(tmpStageAccumulated)});
	}
	
	private void incorrect(){
		ContentValues values = new ContentValues();
		
		values.put("xo", "X");
		
		db.update("dic", values, "mean = ? AND stage > ? AND stage <= ?", new String[]{quizList.get(quizCount - 1).korean, String.valueOf(((tmpStageAccumulated - 1) / 10) * 10), String.valueOf(tmpStageAccumulated)});
	}
	
	private void enableTouch(){
		leftTouch.setClickable(true);
		rightTouch.setClickable(true);
	}
	private void disableTouch(){
		leftTouch.setClickable(false);
		rightTouch.setClickable(false);
	}
	
	private class TimerAniListener implements AnimationListener{

		@Override
		public void onAnimationEnd(Animation arg0) {
			quizCount++;
			
			incorrect();
			
			setQuiz();
		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
		}

		@Override
		public void onAnimationStart(Animation arg0) {
		}
	}
	
	private class TouchListener implements OnTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent event) {			
			int action = event.getAction();
			
			if(v == leftTouch){
				switch(action){
				case MotionEvent.ACTION_DOWN:
					if(!isTouching){
						isTouching = true;
						touchingArea = LEFT;
						leftBackground.setBackgroundResource(R.color.sweet_orange);
					}
					break;
				case MotionEvent.ACTION_UP:
					if(isTouching && (touchingArea == LEFT)){
						isTouching = false;
						leftBackground.setBackgroundResource(R.color.sweet_blue);
						checkAnswer(LEFT);
						disableTouch();
					}
					break;
				}
			}
			else{
				switch(action){
				case MotionEvent.ACTION_DOWN:
					if(!isTouching){
						isTouching = true;
						touchingArea = RIGHT;
						rightBackground.setBackgroundResource(R.color.sweet_orange_darker);
					}
					break;
				case MotionEvent.ACTION_UP:
					if(isTouching && (touchingArea == RIGHT)){
						isTouching = false;
						rightBackground.setBackgroundResource(R.color.sweet_blue_darker);
						checkAnswer(RIGHT);
						disableTouch();
					}
					break;
				}				
			}
			return true;
		}
	}

	public void closeTutorial(View v){
		userInfoEdit.putBoolean("test10tuto", true);
		userInfoEdit.apply();
		tuto.setVisibility(View.GONE);
		tuto.setBackground(null);
		setQuiz();
	}
}
