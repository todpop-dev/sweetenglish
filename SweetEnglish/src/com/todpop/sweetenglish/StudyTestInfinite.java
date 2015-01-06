package com.todpop.sweetenglish;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.LoadingDialog;
import com.todpop.api.TrackUsageTime;
import com.todpop.api.TypefaceActivity;
import com.todpop.api.request.DownloadAndPlayPronounce;
import com.todpop.api.request.GetInfiniteWord;
import com.todpop.sweetenglish.db.WordDBHelper;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
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

public class StudyTestInfinite extends TypefaceActivity{
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
	
	private TextView english;
	private TextView leftEng;
	private TextView rightEng;
	
	private ImageView leftCnt;
	private ImageView rightCnt;
	
	int quizCount = 0;
	
	ArrayList<StudyTestInfiniteEngKorSet> quizList;
	
	String todayDate;
	
	WordDBHelper mHelper;
	SQLiteDatabase db;
	
	DownloadAndPlayPronounce pronounce;
	
	boolean random;
	
	Animation rotateAni;
	BackCounterTimer backCounterTimer;
	
	Editor userInfoEdit;
	private boolean sawTutorial = false;
	
	private LoadingDialog loadingDialog;
	
	private TrackUsageTime tTime;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_study_test_infinite);
		
		leftBackground = (LinearLayout) findViewById(R.id.study_test_infinite_bg_left);
		rightBackground = (LinearLayout) findViewById(R.id.study_test_infinite_bg_right);

		leftTouch = (RelativeLayout) findViewById(R.id.study_test_infinite_touch_left);
		rightTouch = (RelativeLayout) findViewById(R.id.study_test_infinite_touch_right);
		
		TouchListener tListener = new TouchListener();
		leftTouch.setOnTouchListener(tListener);
		rightTouch.setOnTouchListener(tListener);
		
		timer = (ImageView) findViewById(R.id.study_test_infinite_img_timer);
		
		english = (TextView) findViewById(R.id.study_test_infinite_text_english);
		leftEng = (TextView) findViewById(R.id.study_test_infinite_text_left_korean);
		rightEng = (TextView) findViewById(R.id.study_test_infinite_text_right_korean);
		
		leftCnt = (ImageView) findViewById(R.id.study_test_infinite_img_left_count);
		rightCnt = (ImageView) findViewById(R.id.study_test_infinite_img_right_count);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		Calendar cal = Calendar.getInstance();
		
		todayDate = dateFormat.format(cal.getTime());
		
		mHelper = new WordDBHelper(this);
		db = mHelper.getWritableDatabase();
		
		db.delete("mywordtest", null, null);

		SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
		sawTutorial = userInfo.getBoolean("testInfiniteTuto", false);
		if(!sawTutorial){
			userInfoEdit = userInfo.edit();
			tuto = (ImageView)findViewById(R.id.study_test_infinite_tutorial);
			tuto.setBackgroundResource(R.drawable.test_infinite_img_tutorial);
			tuto.setVisibility(View.VISIBLE);
			tuto.setClickable(false);
		}
		backCounterTimer = new BackCounterTimer(60000, 1000);
		initAni();
		initWords();
		
		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
		tTime = TrackUsageTime.getInstance(this);
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Study Test Infinite");
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
		rotateAni = AnimationUtils.loadAnimation(this, R.anim.test_infinite_rotate);
		
		rotateAni.setAnimationListener(new TimerAniListener());
	}

	private void initWords(){
		quizList = new ArrayList<StudyTestInfiniteEngKorSet>();
		
		loadingDialog = new LoadingDialog(this);
		loadingDialog.show();
		
		new GetInfiniteWord(quizList, handler).execute();
	}
	
	final Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			loadingDialog.dissmiss();
			if(!sawTutorial){
				tuto.setClickable(true);
			}
			else{
				timer.startAnimation(rotateAni);
				backCounterTimer.start();
				setQuiz();
			}
		}
	};
	private void setQuiz(){
		if(quizList.size() > quizCount){	//quiz left
			StudyTestInfiniteEngKorSet engKor = quizList.get(quizCount);
			english.setText(engKor.english);
			random = new Random().nextBoolean();
			if(random == LEFT){
				leftEng.setText(engKor.answer);
				rightEng.setText(engKor.wrong);
			}
			else{
				leftEng.setText(engKor.wrong);
				rightEng.setText(engKor.answer);			
			}
			
			enableTouch();
		}
		else{								//no more quiz
			leftTouch.setOnTouchListener(null);
			rightTouch.setOnTouchListener(null);
			db.close();
			mHelper.close();
			Intent intent = new Intent(this, WordListTestResult.class);
			intent.putExtra("fromWhere", WordListTestResult.FROM_INFI);
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
		values.put("name", quizList.get(quizCount - 1).english);
		values.put("mean", quizList.get(quizCount - 1).answer);
	
		db.insert("mywordtest", null, values);
	}
	
	private void incorrect(){
		ContentValues values = new ContentValues();
		
		values.put("xo", "X");
		values.put("name", quizList.get(quizCount - 1).english);
		values.put("mean", quizList.get(quizCount - 1).answer);
	
		db.insert("mywordtest", null, values);
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
			Intent intent = new Intent(getApplicationContext(), WordListTestResult.class);
			startActivity(intent);
			finish();
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
		userInfoEdit.putBoolean("testInfiniteTuto", true);
		userInfoEdit.apply();
		tuto.setVisibility(View.GONE);
		tuto.setBackground(null);
		timer.startAnimation(rotateAni);
		backCounterTimer.start();
		setQuiz();
	}
	
	private class BackCounterTimer extends CountDownTimer{

		public BackCounterTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {	//time up
			
		}

		@Override
		public void onTick(long millisUntilFinished) {
			int secLeft =  (int) (millisUntilFinished / 1000);
			leftCnt.setImageResource(R.drawable.test_2_text_0 + (secLeft / 10));
			rightCnt.setImageResource(R.drawable.test_2_text_0 + (secLeft % 10));
		}
		
	}
}
