package com.todpop.sweetenglish;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.TypefaceActivity;
import com.todpop.api.request.GetLockScreenWord;
import com.todpop.sweetenglish.db.WordDBHelper;

public class LockScreen extends TypefaceActivity{
	ImageView background;
	ImageView seeThru;
	
	Bitmap bitmap;
	
	TextView time;
	TextView apm;
	TextView date;
	
	private static ViewPager cardViewPager;
	
	DoneHandler doneHandler;
	FinishHandler finishHandler;
	
	ImageView leftArrow;
	ImageView rightArrow;
	
	Animation leftFadeIn;
	Animation rightFadeIn;
	Animation leftFadeOut;
	Animation rightFadeOut;
	
	ImageView goApp;
	ImageView unlock;
	
	SeekBar lockSeekBar;
	
	BroadcastReceiver timeTickReceiver;
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
	private final SimpleDateFormat apmFormat = new SimpleDateFormat("a");
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("M월d일 cccc");
	
	SharedPreferences studyInfo;
	SharedPreferences setting;
	
	WordDBHelper dbHelper;
	SQLiteDatabase db;
	
	private boolean dontSetWord = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_screen);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		background = (ImageView)findViewById(R.id.lock_screen_background);
		seeThru = (ImageView)findViewById(R.id.lock_screen_seethru);
		
		time = (TextView)findViewById(R.id.lock_screen_text_time);
		apm = (TextView)findViewById(R.id.lock_screen_text_apm);
		date = (TextView)findViewById(R.id.lock_screen_text_date);
		
		cardViewPager = (ViewPager)findViewById(R.id.lock_screen_viewpager);
		
		doneHandler = new DoneHandler();
		finishHandler = new FinishHandler();
		
		leftArrow = (ImageView)findViewById(R.id.lock_screen_arrow_left);
		rightArrow = (ImageView)findViewById(R.id.lock_screen_arrow_right);
		
		goApp = (ImageView)findViewById(R.id.lock_screen_img_goapp);
		unlock = (ImageView)findViewById(R.id.lock_screen_img_unlock);
		
		lockSeekBar = (SeekBar)findViewById(R.id.lock_screen_seekbar);
		
		studyInfo = getSharedPreferences("studyInfo", 0);
		setting = getSharedPreferences("setting", 0);
		
		int trans = setting.getInt("lockerTransparent", 95);

		leftFadeIn= AnimationUtils.loadAnimation(this, R.anim.lock_screen_fade_in);
		rightFadeIn= AnimationUtils.loadAnimation(this, R.anim.lock_screen_fade_in);
		leftFadeOut = AnimationUtils.loadAnimation(this, R.anim.lock_screen_fade_out);
		rightFadeOut = AnimationUtils.loadAnimation(this, R.anim.lock_screen_fade_out);
				
		//set background
		setBackground();
		seeThru.getBackground().setAlpha(trans);
		
		//set time
		setTime();
		
		//set seekbar
		lockSeekBar.setMinimumHeight(lockSeekBar.getHeight());
		lockSeekBar.setOnSeekBarChangeListener(new LockScreenSeekBarListener(this, goApp, unlock, finishHandler));		
		
		//set word
		cardViewPager.setOnPageChangeListener(new LockScreenPageChangeListener());

		timeTickReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0){
					setTime();
				}
			}
		};
		
		registerReceiver(timeTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
	}
	@Override
	protected void onResume(){
		super.onResume();

	}
	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Lock Screen");
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	protected void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
	@Override
	protected void onPause(){
		super.onPause();
		
		if(!dontSetWord){
			leftArrow.startAnimation(leftFadeOut);
			rightArrow.startAnimation(rightFadeIn);
			
			new Thread(new Runnable(){
				public void run(){
					setWord();
				}
			}).start();
		}
		
		dontSetWord = true;
		
		/*
		 * some phone (like SGS4) calls onPause twice on screen off. 
		 * so, it has a delay on redraw second words list from response from server 
		 * if it calls onPause twice, make sure it doesn't call setWord() twice
		 */
		new Handler().postDelayed(new Runnable() {	//don't get new word if 

	        @Override
	        public void run() {
	            dontSetWord = false;                       
	        }
	    }, 2000);
	}
	@Override
	protected void onDestroy(){
		super.onDestroy();
		
		if(timeTickReceiver != null){
			unregisterReceiver(timeTickReceiver);
		}
		
		if(bitmap != null){
			bitmap.recycle();
			bitmap = null;
		}
	}
	
	private void setTime(){
		Date currentDate = new Date();
		time.setText(timeFormat.format(currentDate));
		apm.setText(apmFormat.format(currentDate));
		date.setText(dateFormat.format(currentDate));
	}
	
	private void setBackground(){
		int simpleSet = setting.getInt("lockerBgSimple", 9);
		
		switch(simpleSet){
		case 0:
			String path = setting.getString("lockerBgUserPic", "");
			
			int imgHeight = getBitmapOfHeight(path);
			int screenHeight;
			
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			
			if(android.os.Build.VERSION.SDK_INT >= 13){
				display.getSize(size);
				screenHeight = size.y;
			}
			else{
				screenHeight = display.getHeight();
			}

			if((imgHeight / screenHeight) >= 2){
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = (imgHeight / screenHeight);
				options.inPurgeable = true;
				options.inDither = true;
				bitmap = BitmapFactory.decodeFile(path, options);
			}
			else{
				bitmap = BitmapFactory.decodeFile(path);
			}
			
			background.setImageBitmap(bitmap);
			break;
		case 1:
			background.setImageResource(R.drawable.screenlock_1_img_simplebg_1);
			break;
		case 2:
			background.setImageResource(R.drawable.screenlock_1_img_simplebg_2);
			break;
		case 3:
			background.setImageResource(R.drawable.screenlock_1_img_simplebg_3);
			break;
		case 4:
			background.setImageResource(R.drawable.screenlock_1_img_simplebg_4);
			break;
		case 5:
			background.setImageResource(R.drawable.screenlock_1_img_simplebg_5);
			break;
		case 6:
			background.setImageResource(R.drawable.screenlock_1_img_simplebg_6);
			break;
		case 7:
			background.setImageResource(R.drawable.screenlock_1_img_simplebg_7);
			break;
		case 8:
			background.setImageResource(R.drawable.screenlock_1_img_simplebg_8);
			break;
		case 9:
			background.setImageResource(R.drawable.screenlock_1_img_simplebg_9);
			break;
		}
	}
	
	private void setWord(){
		
		dbHelper = new WordDBHelper(this);
		
		db = dbHelper.getReadableDatabase();
		int wordCategory = setting.getInt("lockerWordCategory", 0); //6 for myWordList
		
		//(wordCategory < 6) if selected word list is category
		switch(wordCategory){
		case 0:														//recent words
			ArrayList<LockScreenEngKorSet> wordList = new ArrayList<LockScreenEngKorSet>();
			int stageAccumulated = studyInfo.getInt("tmpStageAccumulated", 0);
			int stage = stageAccumulated % 10;
			
			if(stageAccumulated != 0){
				if(stage > 0){
					Cursor recentCursor = db.rawQuery("SELECT distinct name, mean FROM dic WHERE stage = " + stageAccumulated + " ORDER BY RANDOM()", null);
				
					while(recentCursor.moveToNext()){
						wordList.add(new LockScreenEngKorSet(recentCursor.getString(0), recentCursor.getString(1)));
					}
					
					String tmpWord;
					for(int i = 1; (i <= 3) && (wordList.size() <= 10); i++){
						tmpWord = studyInfo.getString("reviewWord" + i, "");
						Cursor reviewCursor = db.rawQuery("SELECT distinct mean FROM dic WHERE name = '" + tmpWord + "'", null);
						
						if(reviewCursor.moveToFirst()){
							wordList.add(new LockScreenEngKorSet(tmpWord, reviewCursor.getString(0)));
						}
					}
				}
				else{													//if last stage is 10
					Cursor recentCursor = db.rawQuery("SELECT DISTINCT name,  mean FROM dic WHERE stage > " + (((stageAccumulated - 1) / 10) * 10) + " AND stage <= " + (stageAccumulated - 1) + " ORDER BY RANDOM() LIMIT 10", null);
					
					while(recentCursor.moveToNext()){
						wordList.add(new LockScreenEngKorSet(recentCursor.getString(0), recentCursor.getString(1)));
					}
				}
				
				if(!wordList.isEmpty()){
					Message msg = doneHandler.obtainMessage();
					msg.obj = wordList;
					doneHandler.sendMessage(msg);
				}
				else{
					new GetLockScreenWord(0, doneHandler).execute();
				}
			}
			else{														//if never studied
				new GetLockScreenWord(0, doneHandler).execute();
			}
			break;
		case 1:
			new GetLockScreenWord(0, doneHandler).execute();
			
			break;
		case 2: 
			new GetLockScreenWord(1, doneHandler).execute();
			
			break;
		case 3: 
			new GetLockScreenWord(2, doneHandler).execute();
			
			break;
		case 4: 
			new GetLockScreenWord(3, doneHandler).execute();
			
			break;
		case 5: 
			new GetLockScreenWord(4, doneHandler).execute();
			
			break;
		case 6: 
			ArrayList<LockScreenEngKorSet> myWordList = new ArrayList<LockScreenEngKorSet>();
			
			String allWord = getResources().getString(R.string.home_more_locker_word_setting_list_category_all);
			
			String wordGroup = setting.getString("lockerWordMyList", allWord);
			Cursor wordListCursor = db.rawQuery("SELECT distinct name, mean FROM mywords WHERE group_name = '" + wordGroup + "' ORDER BY RANDOM() LIMIT 10", null);
		
			while(wordListCursor.moveToNext()){			
				myWordList.add(new LockScreenEngKorSet(wordListCursor.getString(0), wordListCursor.getString(1)));
			}
			
			if(myWordList.isEmpty()){
				new GetLockScreenWord(0, doneHandler).execute();
			}
			else{
				Message msg = doneHandler.obtainMessage();
				msg.obj = myWordList;
				doneHandler.sendMessage(msg);
			}
			break;
		}
		
		db.close();
		dbHelper.close();
	}

	private class DoneHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			
			cardViewPager.setAdapter(new LockScreenPagerAdapter(getApplicationContext(),  (ArrayList<LockScreenEngKorSet>)msg.obj));
		}
	};
	private class FinishHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			if(msg.what == 1){
				Intent intent = new Intent(getApplicationContext(), OpeningActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			dontSetWord = true;
			finish();
		}
	};
	
	private final class LockScreenPageChangeListener implements OnPageChangeListener{
		private int lastState = ViewPager.SCROLL_STATE_IDLE;
		@Override
		public void onPageScrollStateChanged(int state) {
			int position = cardViewPager.getCurrentItem();
			
			if(state == ViewPager.SCROLL_STATE_DRAGGING){
				if(position > 0){
					leftArrow.startAnimation(leftFadeOut);
				}
				if(position + 1 < cardViewPager.getAdapter().getCount()){
					rightArrow.startAnimation(rightFadeOut);
				}
			}
			else if(state == ViewPager.SCROLL_STATE_SETTLING){
				if(position > 0){
					leftArrow.startAnimation(leftFadeIn);
				}
				if(position + 1 < cardViewPager.getAdapter().getCount()){
					rightArrow.startAnimation(rightFadeIn);
				}
			}
			else if(state == ViewPager.SCROLL_STATE_IDLE){
				if(lastState == ViewPager.SCROLL_STATE_DRAGGING){
					if(position > 0){
						leftArrow.startAnimation(leftFadeIn);
					}
					if(position + 1 < cardViewPager.getAdapter().getCount()){
						rightArrow.startAnimation(rightFadeIn);
					}
				}
			}
			lastState = state;
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {		
		}

		@Override
		public void onPageSelected(int position) {
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public static int getBitmapOfHeight( String fileName ){
		 
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(fileName, options);
		 
		    return options.outHeight;
		} catch(Exception e) {
		    return 0;
		}
	}
}
