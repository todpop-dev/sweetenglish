package com.todpop.sweetenglish;

import java.util.ArrayList;
import java.util.Random;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.TrackUsageTime;
import com.todpop.api.TypefaceActivity;
import com.todpop.sweetenglish.db.WordDBHelper;


public class WordListTestRenewal extends TypefaceActivity {

	View redBack;
	
	TextView english;

	//answer Buttons
	Button option1;
	Button option2;
	Button option3;
	Button option4;

	ImageView timeImg;
	Animation timeAni;

	ProgressBar circularProgress; 

	ImageView correctImg;
	ImageView incorrectImg;

	ImageView comboImg;
	Animation comboAni;

	TotalTimer timer;
	ProgressTimer progressTimer;

	private static long TIME = 10000;
	private long timeLeft = 0;

	private ArrayList<Word> wordList;

	private static int qTotal;
	private int qCount = -1;
	private int cntCorrect = 0;
	private int cntSolvedAnswer= 0;

	private int comboCount = -1;

	private String lastHigh;

	private static Runnable resetWord;
	private static Runnable redTime;
	private static Runnable goNext;

	//RelativeLayout tutorial;

	LinearLayout timesUp;
	ImageView timesUpImg;
	Animation timesUpAni;

	WordDBHelper mHelper;
	SQLiteDatabase db;

	private String finalAnswerForRequest = "";

	private String groupName;

	private int testSize;

	private TrackUsageTime tTime;
	
	private class Word{
		public Word(String word, String mean, String incorrect1, String incorrect2, String incorrect3){
			this.word = word;
			this.mean = mean;
			this.incorrect1 = incorrect1;
			this.incorrect2 = incorrect2;
			this.incorrect3 = incorrect3;
		}
		public String getWord(){
			return word;
		}
		public String getMean(){
			return mean;
		}
		public String getIncorrect1(){
			return incorrect1;
		}
		public String getIncorrect2(){
			return incorrect2;
		}
		public String getIncorrect3(){
			return incorrect3;
		}

		private String word;
		private String mean;
		private String incorrect1;
		private String incorrect2;
		private String incorrect3;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		groupName = extras.getString("testGroupName");
		testSize = extras.getInt("testListSize");
		Log.e("testGroupName",groupName+"");
		Log.e("testSize",testSize+"");

		setContentView(R.layout.activity_study_test_renewal);

		redBack = (View)findViewById(R.id.v_test_renewal_light_box);

		english = (TextView)findViewById(R.id.tv_test_renewal_eng);

		option1 = (Button)findViewById(R.id.btn_test_renewal_opt1);
		option2 = (Button)findViewById(R.id.btn_test_renewal_opt2);
		option3 = (Button)findViewById(R.id.btn_test_renewal_opt3);
		option4 = (Button)findViewById(R.id.btn_test_renewal_opt4);

		option1.setOnClickListener(new ButtonListener());
		option2.setOnClickListener(new ButtonListener());
		option3.setOnClickListener(new ButtonListener());
		option4.setOnClickListener(new ButtonListener());

		timeImg = (ImageView)findViewById(R.id.iv_test_renewal_time);
		timeAni = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.weekly_pop_time_ani);
		timeImg.setAnimation(timeAni);

		circularProgress = (ProgressBar)findViewById(R.id.pb_test_renewal_progress);
		circularProgress.setMax((int)TIME/10);

		correctImg = (ImageView)findViewById(R.id.iv_test_renewal_o);
		incorrectImg = (ImageView)findViewById(R.id.iv_test_renewal_x);

		comboImg = (ImageView)findViewById(R.id.iv_test_renewal_combo);
		comboAni = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.weekly_pop_combo_ani);
		comboImg.setAnimation(comboAni);

		timesUp = (LinearLayout)findViewById(R.id.ll_test_renewal_timesup);
		timesUpImg = (ImageView)findViewById(R.id.iv_test_renewal_timseup);
		timesUpAni = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.weekly_pop_timesup_ani);
		timesUpImg.setAnimation(timesUpAni);

		wordList = new ArrayList<Word>();

		mHelper = new WordDBHelper(this);

		resetWord = new Runnable(){
			@Override
			public void run() {	
				correctImg.setVisibility(View.INVISIBLE);
				incorrectImg.setVisibility(View.INVISIBLE);
				setWord();
			}
		};
		redTime = new Runnable(){
			@Override
			public void run() {		
				redBack.setVisibility(View.INVISIBLE);
			}
		};
		goNext = new Runnable(){
			@Override
			public void run() {		
				goNextActivity();
			}
		};

		getTestWords();

		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
		tTime = TrackUsageTime.getInstance(this);
	}

	private void getTestWords()
	{
		SQLiteDatabase db = mHelper.getReadableDatabase();
		String query="";
		if(groupName.equals("전체 단어")){
			query = "SELECT name, mean FROM mywords ORDER BY RANDOM()  LIMIT "+testSize;
			
		}else{
			query = "SELECT name, mean FROM mywords WHERE group_name='" + groupName + "' ORDER BY RANDOM() LIMIT "+testSize;	
		}
		Log.e("Start",query);
		try {
			Cursor cursor = db.rawQuery(query, null);
			qTotal = cursor.getCount();
			if (cursor.getCount()>0) {
				while(cursor.moveToNext()) {
					String word = cursor.getString(0);
					String mean = cursor.getString(1);

					Cursor otherCursor = db.rawQuery("SELECT DISTINCT mean FROM dic WHERE mean <> '" + cursor.getString(1) + "' ORDER BY RANDOM() LIMIT 3", null);
					otherCursor.moveToNext();
					String incor1 = otherCursor.getString(0);
					otherCursor.moveToNext();
					String incor2 = otherCursor.getString(0);
					otherCursor.moveToNext();
					String incor3 = otherCursor.getString(0);

					wordList.add(new Word(word, mean, incor1, incor2, incor3));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Log.i("STEVEN", "wordList size is : " + wordList.size());
	}


	@Override
	public void onResume(){
		super.onResume();

		if(timeLeft != 0){
			timer = new TotalTimer(TIME, 1000);
			progressTimer = new ProgressTimer(TIME, 10);
			timer.start();
			progressTimer.start();
		} else{
			timer = new TotalTimer(TIME, 1000);
			progressTimer = new ProgressTimer(TIME, 10);
			setWord();
			timer.start();
			progressTimer.start();
		}

	}
	@Override
	public void onPause(){
		super.onPause();
		timer.cancel();
		progressTimer.cancel();
	}
	private void setWord(){
		qCount++;
		if(qTotal > qCount){
			english.setText(wordList.get(qCount).getWord());

			int ran = new Random().nextInt(4);

			switch(ran){
			case 0:
				option1.setText(wordList.get(qCount).getMean());
				option2.setText(wordList.get(qCount).getIncorrect1());
				option3.setText(wordList.get(qCount).getIncorrect2());
				option4.setText(wordList.get(qCount).getIncorrect3());
				option1.setTag(true);
				option2.setTag(false);
				option3.setTag(false);
				option4.setTag(false);
				break;
			case 1:
				option1.setText(wordList.get(qCount).getIncorrect1());
				option2.setText(wordList.get(qCount).getMean());
				option3.setText(wordList.get(qCount).getIncorrect2());
				option4.setText(wordList.get(qCount).getIncorrect3());
				option1.setTag(false);
				option2.setTag(true);
				option3.setTag(false);
				option4.setTag(false);
				break;
			case 2:
				option1.setText(wordList.get(qCount).getIncorrect1());
				option2.setText(wordList.get(qCount).getIncorrect2());
				option3.setText(wordList.get(qCount).getMean());
				option4.setText(wordList.get(qCount).getIncorrect3());
				option1.setTag(false);
				option2.setTag(false);
				option3.setTag(true);
				option4.setTag(false);
				break;
			case 3:
				option1.setText(wordList.get(qCount).getIncorrect1());
				option2.setText(wordList.get(qCount).getIncorrect2());
				option3.setText(wordList.get(qCount).getIncorrect3());
				option4.setText(wordList.get(qCount).getMean());
				option1.setTag(false);
				option2.setTag(false);
				option3.setTag(false);
				option4.setTag(true);
				break;
			}
			option1.setClickable(true);
			option2.setClickable(true);
			option3.setClickable(true);
			option4.setClickable(true);
		} else{	//finish test
			if(cntSolvedAnswer >= wordList.size()){
				goNextActivity();
			}
		}
	}

	private class ButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			disableAndStop();
			cntSolvedAnswer++;
			db = mHelper.getWritableDatabase();
			if((Boolean)v.getTag()){ //correct
				correct();
			}else{
				incorrect();
			}
			timer.cancel();
			progressTimer.cancel();

			timer = new TotalTimer(TIME, 1000);
			progressTimer = new ProgressTimer(TIME, 10);

			timer.start();
			progressTimer.start();

		}
	}

	private void correct() {
		comboCount++;
		cntCorrect++;

		correctImg.setVisibility(View.VISIBLE);
		try{
			ContentValues row = new ContentValues();
			Word item = wordList.get(qCount);
			row.put("xo", "O");
			row.put("name", item.word);
			row.put("mean", item.mean);
			db.insert("mywordtest", null, row);
		} catch(Exception e){
			e.printStackTrace();
		}
		new Handler().postDelayed(resetWord, 500);
		
		if(comboCount == 10){ //10combo
			comboImg.setImageResource(R.drawable.weekly_1_img_10combo);
			comboAni.start();
		}
		else if(comboCount == 20){	//20combo
			comboImg.setImageResource(R.drawable.weekly_1_img_20combo);
			comboAni.start();
		}
		else if(comboCount == 30){  //30combo
			comboImg.setImageResource(R.drawable.weekly_1_img_30combo);
			comboAni.start();
		}
		else if(comboCount > 0){
			comboImg.setImageResource(R.drawable.weekly_1_img_combo);
			comboAni.start();
		}

		finalAnswerForRequest+="1";
	}

	private void incorrect(){
		disableAndStop();
		
		comboCount = -1;
		incorrectImg.setVisibility(View.VISIBLE);
		try{
			ContentValues row = new ContentValues();
			Word item = wordList.get(qCount);
			row.put("xo", "X");
			row.put("name", item.word);
			row.put("mean", item.mean);
			db.insert("mywordtest", null, row);
		} catch(Exception e){
			e.printStackTrace();
		}
		new Handler().postDelayed(resetWord, 500);

		finalAnswerForRequest+="0";
	}
	private void goNextActivity(){
		Intent intent = new Intent(getApplicationContext(), WordListTestResult.class);

		intent.putExtra("lastHigh", lastHigh);
		startActivity(intent);
		finish();
	}

	private void disableAndStop(){
		option1.setClickable(false);
		option2.setClickable(false);
		option3.setClickable(false);
		option4.setClickable(false);
	}

	public void onClickTuto(View v){
		//tutorial.setVisibility(View.GONE);
		setWord();
		timer.start();
		progressTimer.start();
	}

	private class TotalTimer extends CountDownTimer{

		public TotalTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {	//time up
			//time's up effect and end activity
			cntSolvedAnswer++;

			if(cntSolvedAnswer < 10){
				setWord();
				this.start();
			}else{
				timesUp.setVisibility(View.VISIBLE);
				timesUpAni.start();
				new Handler().postDelayed(goNext, 2500);
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
			timeLeft = millisUntilFinished;
			if(millisUntilFinished < 6000){	
				redBack.setVisibility(View.VISIBLE);
				new Handler().postDelayed(redTime, 500);

				if(millisUntilFinished < 2000){
					timeImg.setImageResource(R.drawable.weekly_1_text_time_1);
					timeAni.start();
				}else if(millisUntilFinished < 3000){
					timeImg.setImageResource(R.drawable.weekly_1_text_time_2);
					timeAni.start();
				}else if(millisUntilFinished < 4000){
					timeImg.setImageResource(R.drawable.weekly_1_text_time_3);
					timeAni.start();
				}else if(millisUntilFinished < 5000){
					timeImg.setImageResource(R.drawable.weekly_1_text_time_4);
					timeAni.start();
				}else if(millisUntilFinished < 6000){
					timeImg.setImageResource(R.drawable.weekly_1_text_time_5);
					timeAni.start();
				}
			}
		}

	}

	private class ProgressTimer extends CountDownTimer{

		public ProgressTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			this.start();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			circularProgress.setProgress((int) ((TIME - millisUntilFinished) / 10));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public void onBackPressed(){
		finish();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		mHelper.close();
	}


	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Word List Test Renewal");
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
}
