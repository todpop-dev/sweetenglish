package com.todpop.sweetenglish;

import com.todpop.api.TypefaceActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class StudyTest369 extends TypefaceActivity{

	private int mActivePointerId = 0;

	private TextView english;
	private TextView leftKor;
	private TextView rightKor;
	
	private ImageView leftOval;
	private ImageView rightOval;
	
	private int screenHeight;
	private int screenXCenter;

	CanvasView canvasView;
	
	private float firstTouchX = 0;
	private float lastTouchX = 0;
	private float lastTouchY = 0;
	
	private float lastScale = 0;
	
	private float power = 1;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_study_test_369);
		
		RelativeLayout backLayout = (RelativeLayout)findViewById(R.id.study_test_369_back);
		
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
		
		Log.i("STEVEN", "Height is " + screenHeight);
		leftOval = (ImageView)findViewById(R.id.study_test_369_left_oval);
		rightOval = (ImageView)findViewById(R.id.study_test_369_right_oval);
		
		Log.i("STEVEN", "screen width is " + screenXCenter + "      screen 1/2 is " + screenXCenter / 2);
	}
	private class CanvasView extends View {

        public CanvasView(Context context) {
            super(context);
            
        }
        @Override
        public void draw(Canvas canvas) {
            
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            if(firstTouchX <= screenXCenter){	//left answer
            	canvas.drawCircle(100, screenHeight - 100, lastScale, paint);
            }
            else{								//right answer
            	canvas.drawCircle(screenXCenter * 2 - 100, screenHeight - 100, lastScale, paint);
            }
        }
    }
	@Override
	public boolean onTouchEvent(MotionEvent ev){
		
		final int action = MotionEventCompat.getActionMasked(ev);
		
		switch(action){
		case MotionEvent.ACTION_DOWN:{
			final int pointerIndex = MotionEventCompat.getActionIndex(ev);
			final float x = MotionEventCompat.getX(ev, pointerIndex);
			final float y = MotionEventCompat.getY(ev, pointerIndex);
			
			firstTouchX = x;
			lastTouchX = x;
			lastTouchY = y;
			
			power = (float) (screenXCenter / Math.abs(x - screenXCenter) * 1.5);
			
			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
			
			break;
		}
		case MotionEvent.ACTION_MOVE:{
			final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
			final float x = MotionEventCompat.getX(ev, pointerIndex);
			final float y = MotionEventCompat.getY(ev, pointerIndex);
			
			double distance = Math.sqrt(Math.pow(lastTouchX - x, 2) + Math.pow(lastTouchY - y, 2));
			
			float toScale = (float)distance * power;
			
			if(firstTouchX <= screenXCenter){	//left answer
				if((x - lastTouchX) + (lastTouchY - y) > 0){
					lastScale += toScale;
				}
				else{
					lastScale -= toScale;
				}
			}
			else{								//right answer
				if((lastTouchX - x) + (lastTouchY - y) > 0){
					lastScale += toScale;
				}
				else{
					lastScale -= toScale;
				}
			}
			
			lastTouchX = x;
			lastTouchY = y;
			
			canvasView.invalidate();
			break;
		}
		case MotionEvent.ACTION_UP:{

			firstTouchX = 0;
			lastTouchX = 0;
			lastTouchY = 0;
			
			lastScale = 0;
			
			power = 1;
			
			canvasView.invalidate();
		}
		}
		return true;
	}
}
