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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class StudyTest369 extends TypefaceActivity{
	private final static boolean LEFT = true;
	private final static boolean RIGHT = false;
	
	private RelativeLayout backLayout;
	
	ImageView tuto;

	private TextView english;
	private TextView leftKor;
	private TextView rightKor;
	
	private int mActivePointerId = 0;
	
	private int screenHeight;
	private int screenXCenter;

	CanvasView canvasView;
	
	private boolean touchBlocked = false;
	
	private float locationY = 0;
	private float firstTouchX = 0;
	private float lastTouchX = 0;
	private float lastTouchY = 0;
	
	private float lastScale = 0;
	private boolean gotSmaller = false;
	
	private final float power = 4;
	
	private int[] max = new int[1];
	
	private boolean isNeverChecked = true;
	private boolean isFromSmall = false;

	int tmpStageAccumulated;
	
	int quizCount = 0;
	
	ArrayList<Integer> colorList;
	ArrayList<EngKorSet> quizList;
	
	String todayDate;
	
	WordDBHelper mHelper;
	SQLiteDatabase db;
	
	DownloadAndPlayPronounce pronounce;
	
	boolean random;
	
	TranslateAnimation leftToCenterAni;
	TranslateAnimation rightToCenterAni;
	TranslateAnimation resetAni;
	AlphaAnimation fadeOut;
	
	Editor userInfoEdit;
	
	private TrackUsageTime tTime;
	
	private class EngKorSet{
		public EngKorSet(String eng, String ans, String wro, boolean his){
			english = eng;
			answer = ans;
			wrong = wro;
			hasHistory = his;
		}
		String english;
		String answer;
		String wrong;
		boolean hasHistory;
	}
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_study_test_369);
		
		backLayout = (RelativeLayout)findViewById(R.id.study_test_369_back);
		canvasView = new CanvasView(this);
		
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		backLayout.addView(canvasView, params);
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		
		if(android.os.Build.VERSION.SDK_INT >= 13){
			display.getSize(size);
			screenHeight = size.y;
			screenXCenter = size.x / 2;
		}
		else{
			screenHeight = display.getHeight();
			screenXCenter = display.getWidth() / 2;
		}
		
		english = (TextView)findViewById(R.id.study_test_369_text_eng);
		leftKor = (TextView)findViewById(R.id.study_test_369_text_left);
		rightKor = (TextView)findViewById(R.id.study_test_369_text_right);
		
		locationY = screenHeight * 0.832f + 20;

		//power = (float) (Math.sqrt(Math.pow(screenHeight, 2) + Math.pow(screenXCenter * 2, 2)) * 0.006);
		
		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, max, 0);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		Calendar cal = Calendar.getInstance();
		
		todayDate = dateFormat.format(cal.getTime());
		
		mHelper = new WordDBHelper(this);
		db = mHelper.getWritableDatabase();
		
		pronounce = new DownloadAndPlayPronounce(this);

		SharedPreferences studyInfo = getSharedPreferences("studyInfo", 0);
		tmpStageAccumulated = studyInfo.getInt("tmpStageAccumulated", 1);
		
		SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
		if(!userInfo.getBoolean("test369tuto", false)){
			userInfoEdit = userInfo.edit();
			tuto = (ImageView)findViewById(R.id.study_test_369_tutorial);
			tuto.setBackgroundResource(R.drawable.test_1_img_3tutorial);
			tuto.setVisibility(View.VISIBLE);
		}
		
		initColor();
		initAni();
		initWords();
		setQuiz();
		
		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
		tTime = TrackUsageTime.getInstance(this);
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Study Test 369");
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
	protected void onDestroy(){
		super.onDestroy();
		db.close();
	}
	
	private void initColor(){
		colorList = new ArrayList<Integer>();
		
		colorList.add(Color.rgb(0, 181, 237));
		colorList.add(Color.rgb(253, 186, 40));
		colorList.add(Color.rgb(37, 42, 58));
		colorList.add(Color.rgb(167, 199, 106));
		colorList.add(Color.rgb(14, 64, 67));
		colorList.add(Color.rgb(255, 120, 100));
	}
	private void initAni(){
		leftToCenterAni = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.25f,
										  Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		leftToCenterAni.setDuration(500);
		leftToCenterAni.setFillAfter(true);
		
		rightToCenterAni = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -0.25f,
										  Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		rightToCenterAni.setDuration(500);
		rightToCenterAni.setFillAfter(true);
		
		resetAni = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
										  Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		resetAni.setDuration(0);
		resetAni.setFillAfter(true);
		
		fadeOut = new AlphaAnimation(1.0f, 0.0f);
		fadeOut.setDuration(500);
		fadeOut.setFillAfter(true);
		
		fadeOut.setAnimationListener(new StudyTest369ResetAnimationListener());
	}
	
	private void initWords(){
		quizList = new ArrayList<EngKorSet>();
		
		Cursor cursor = db.rawQuery("SELECT DISTINCT name,  mean, memorized_date FROM dic WHERE stage >= " + (tmpStageAccumulated - 2) + " AND stage <= " + tmpStageAccumulated + " ORDER BY RANDOM()", null);
		if (cursor.getCount()>0) {
			while(cursor.moveToNext()){
				// get wrong mean randomly except correct mean  
				Cursor otherCursor = db.rawQuery("SELECT DISTINCT mean FROM dic WHERE mean <> '" + cursor.getString(1) + "' ORDER BY RANDOM() LIMIT 1", null);
				otherCursor.moveToNext();
				
				Boolean hasHistory = false;
				if(cursor.getString(2) != null)
					hasHistory = true;
				
				quizList.add(new EngKorSet(cursor.getString(0), cursor.getString(1), otherCursor.getString(0), hasHistory));
			}
		}
	}
	
	private void setQuiz(){
		if(quizList.size() > quizCount){		//question left
			EngKorSet engKor = quizList.get(quizCount);
			english.setText(engKor.english);
			random = new Random().nextBoolean();
			if(random == LEFT){
				leftKor.setText(engKor.answer);
				rightKor.setText(engKor.wrong);
			}
			else{
				leftKor.setText(engKor.wrong);
				rightKor.setText(engKor.answer);			
			}
		}
		else{									//test done
			db.close();
			Intent intent = new Intent(this, StudyTestResult.class);
			startActivity(intent);
			finish();
		}
	}
	
	private void checkAnswer(){
		quizCount++;
		if(firstTouchX <= screenXCenter){		//left answer
			if(random == LEFT){					//correct
				correct();
			}
			else{								//incorrect
				incorrect();
				
				rightKor.startAnimation(rightToCenterAni);
				leftKor.startAnimation(fadeOut);
			}
		}
		else{									//right answer
			if(random == RIGHT){					//correct
				correct();
			}
			else{								//incorrect
				incorrect();
				
				leftKor.startAnimation(leftToCenterAni);
				rightKor.startAnimation(fadeOut);
			}
		}
	}
	
	private void correct(){
		ContentValues values = new ContentValues();
		
		values.put("xo", "O");
		if(!quizList.get(quizCount - 1).hasHistory){
			values.put("memorized_date", todayDate);
		}
		
		db.update("dic", values, "name = ? AND stage >= ? AND stage <= ?", new String[]{quizList.get(quizCount - 1).english, String.valueOf(tmpStageAccumulated - 2), String.valueOf(tmpStageAccumulated)});
		
		if(isFromSmall){
			correctBgChange();
			isFromSmall = false;
		}
		else{
			backLayout.setBackgroundColor(colorList.get((quizCount - 1) % 6));
		}
		setQuiz();
	}
	
	private void incorrect(){
		touchBlocked = true;
		
		ContentValues values = new ContentValues();
		values.put("xo", "X");
		
		db.update("dic", values, "name = ? AND stage >= ? AND stage <= ?", new String[]{quizList.get(quizCount - 1).english, String.valueOf(tmpStageAccumulated - 2), String.valueOf(tmpStageAccumulated)});
		
		incorrectBgChange();
		pronounce.readItForMe(quizList.get(quizCount - 1).english, "1", tmpStageAccumulated);
	}
	
	/*private void correctCircleAni(){
		final Handler drawHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				canvasView.invalidate();
			}
		};
		final Handler doneHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				backLayout.setBackgroundColor(colorList.get(quizCount % 6));
				quizCount++;
			}
		};
		new Thread(new Runnable(){
			public void run(){
				//double average = distanceSum / distanceCnt;
				try {
					while(max[0] > (lastScale * 2) && lastScale < screenHeight){
						lastScale += 2;
						drawHandler.sendEmptyMessage(0);
						
						Thread.sleep(5);
					}
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
				doneHandler.sendEmptyMessage(0);
			}
		}).start();
	}*/
	
	private void correctBgChange(){
		Drawable[] layers = {backLayout.getBackground(), new ColorDrawable(colorList.get((quizCount - 1) % 6))};
		TransitionDrawable transDrawable = new TransitionDrawable(layers);
		if(android.os.Build.VERSION.SDK_INT >= 16){
			backLayout.setBackground(transDrawable);
		}
		else{
			backLayout.setBackgroundDrawable(transDrawable);
		}
		transDrawable.startTransition(300);
	}
	
	private void incorrectBgChange(){
		Drawable[] layers = {backLayout.getBackground(), new ColorDrawable(Color.BLACK)};
		TransitionDrawable transDrawable = new TransitionDrawable(layers);
		if(android.os.Build.VERSION.SDK_INT >= 16){
			backLayout.setBackground(transDrawable);
		}
		else{
			backLayout.setBackgroundDrawable(transDrawable);
		}
		transDrawable.startTransition(300);
	}
	
	private class CanvasView extends View {
		Paint paint;
        public CanvasView(Context context) {
            super(context);
            paint = new Paint();
            
        }
        @Override
        public void onDraw(Canvas canvas) {
        	super.onDraw(canvas);
        	
            paint.setColor(colorList.get(quizCount % 6));
            
            if(firstTouchX <= screenXCenter){	//left answer
            	canvas.drawCircle(screenXCenter / 2, locationY, lastScale, paint);
            }
            else{								//right answer
            	canvas.drawCircle(screenXCenter * 1.5f, locationY, lastScale, paint);
            }
        }
    }

	@Override
	public boolean onTouchEvent(MotionEvent ev){
		
		final int action = MotionEventCompat.getActionMasked(ev);
		
		if(touchBlocked)
			return true;

		switch(action){
			case MotionEvent.ACTION_DOWN:{						
				gotSmaller = false;
				isNeverChecked = true;
				isFromSmall = false;	
				lastScale = 0;	
				
				final int pointerIndex = MotionEventCompat.getActionIndex(ev);
				final float x = MotionEventCompat.getX(ev, pointerIndex);
				final float y = MotionEventCompat.getY(ev, pointerIndex);
				
				firstTouchX = x;
				lastTouchX = x;
				lastTouchY = y;
				
				mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
				
				break;
			}
			case MotionEvent.ACTION_MOVE:{
				final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
				
				if(pointerIndex >= 0){
					
					final float x = MotionEventCompat.getX(ev, pointerIndex);
					final float y = MotionEventCompat.getY(ev, pointerIndex);
					
					double distance = Math.sqrt(Math.pow(lastTouchX - x, 2) + Math.pow(lastTouchY - y, 2));
					
					float toScale = (float)distance * power;
					
					if(firstTouchX <= screenXCenter){	//left answer
						if((x - lastTouchX) + (lastTouchY - y) >= 0){
							lastScale += toScale;
							gotSmaller = false;
						}
						else{
							lastScale -= toScale;
							gotSmaller = true;
						}
					}
					else{								//right answer
						if((lastTouchX - x) + (lastTouchY - y) >= 0){
							lastScale += toScale;
							gotSmaller = false;
						}
						else{
							lastScale -= toScale;
							gotSmaller = true;
						}
					}
					
					lastTouchX = x;
					lastTouchY = y;
		
					if(isNeverChecked){
						if(max[0] <= lastScale * 2){
							checkAnswer();		
							lastScale = 0;			
							isNeverChecked = false;
						}
						canvasView.invalidate();
					}
				}
				break;
			}
			case MotionEvent.ACTION_UP:{
				if(isNeverChecked){
					if(!gotSmaller){
						if((lastScale <= 500) || (lastScale == 0)){
							isFromSmall = true;
						}
						checkAnswer();
					}
	
					lastScale = 0;
					
					canvasView.invalidate();
				}
			}
		}
		return true;
	}

	private class StudyTest369ResetAnimationListener implements AnimationListener{
		@Override
		public void onAnimationEnd(Animation animation) {
			new Handler().postDelayed(resetIncorrect, 1000);
		}
	
		@Override
		public void onAnimationRepeat(Animation animation) {
		}
	
		@Override
		public void onAnimationStart(Animation animation) {
		}
	}
	
	Runnable resetIncorrect = new Runnable(){
		@Override
		public void run() {
			touchBlocked = false;
			backLayout.setBackgroundColor(colorList.get((quizCount - 1) % 6));

			rightKor.startAnimation(resetAni);
			leftKor.startAnimation(resetAni);
			
			setQuiz();
		}
		
	};
	
	public void closeTutorial(View v){
		userInfoEdit.putBoolean("test369tuto", true);
		userInfoEdit.apply();
		tuto.setVisibility(View.GONE);
		tuto.setBackground(null);
	}
}
