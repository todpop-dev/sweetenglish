package com.todpop.sweetenglish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class LockScreenSeekBarListener implements OnSeekBarChangeListener{
	private Context context;
	
	private Vibrator vibrator;
	
	boolean isFromCenter = true;

	private Drawable noThumb;
	private Drawable normalThumb;
	private Drawable pressedThumb;
	
	private ImageView goApp;
	private ImageView unlock;
	
	private Drawable goAppNormal;
	private Drawable goAppPressed;
	
	private Drawable unlockNormal;
	private Drawable unlockPressed;	
	
	private Handler finishHandler;
	
	public LockScreenSeekBarListener(Context c, ImageView goApp, ImageView unlock, Handler finishHandler){
		context = c;
		vibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
		
		noThumb = c.getResources().getDrawable(R.drawable.lock_screen_seekbar_background);
		normalThumb = c.getResources().getDrawable(R.drawable.screenlock_3_btn_circle_normal);
		pressedThumb = c.getResources().getDrawable(R.drawable.screenlock_3_btn_circle_pressed);
		
		this.goApp = goApp;
		this.unlock = unlock;
		this.finishHandler = finishHandler;
		
		goAppNormal = c.getResources().getDrawable(R.drawable.screenlock_3_btn_goapp_normal);
		goAppPressed = c.getResources().getDrawable(R.drawable.screenlock_3_btn_goapp_pressed);
		
		unlockNormal = c.getResources().getDrawable(R.drawable.screenlock_3_btn_unlock_normal);
		unlockPressed = c.getResources().getDrawable(R.drawable.screenlock_3_btn_unlock_pressed);
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if(fromUser){
			if(progress >= 85){
				if(isFromCenter){
					vibrator.vibrate(50);
					isFromCenter = false;
				}
				seekBar.setThumb(noThumb);
				seekBar.setProgress(100);
			}
			else if(progress <= 15){
				if(isFromCenter){
					vibrator.vibrate(50);
					isFromCenter = false;
				}
				seekBar.setThumb(noThumb);
				seekBar.setProgress(0);
			}
			else{
				if(!isFromCenter){
					seekBar.setThumb(pressedThumb);
					isFromCenter = true;
				}
			}
		}
		else{
			return;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		seekBar.setThumb(pressedThumb);
		goApp.setImageDrawable(goAppPressed);
		unlock.setImageDrawable(unlockPressed);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		int progress = seekBar.getProgress();
		
		if(progress == 100){
			finishHandler.sendEmptyMessage(0);
		}
		else if (progress == 0){
			finishHandler.sendEmptyMessage(1);
		}
		else{
			seekBar.setProgress(50);
			seekBar.setThumb(normalThumb);
			goApp.setImageDrawable(goAppNormal);
			unlock.setImageDrawable(unlockNormal);
		}
	}

}
