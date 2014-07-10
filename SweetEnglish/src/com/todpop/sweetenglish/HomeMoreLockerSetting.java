package com.todpop.sweetenglish;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.todpop.api.TypefaceActivity;
import com.todpop.api.VerticalSeekBar;

public class HomeMoreLockerSetting extends TypefaceActivity {
	ImageView preview;
	ImageView seeThru;
	
	VerticalSeekBar seekBar;
	
	SharedPreferences setting;
	Editor settingEditor;
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_locker_setting);
		
		preview = (ImageView)findViewById(R.id.locker_setting_preview);
		seeThru = (ImageView)findViewById(R.id.locker_setting_preview_seethru);
		
		seekBar = (VerticalSeekBar)findViewById(R.id.locker_setting_seekbar_transparent);
		
		seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());

		setting = getSharedPreferences("setting", 0);
		settingEditor = setting.edit();
		
		int trans = setting.getInt("lockerTransparent", 95);
		
		seekBar.setProgress(trans);
		seeThru.getBackground().setAlpha(trans);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		int simpleSet = setting.getInt("lockerBgSimple", 4);
		
		switch(simpleSet){
		case 0:
			String path = setting.getString("lockerBgUserPic", "");
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;
			options.inPurgeable = true;
			options.inDither = true;
			
			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			
			preview.setImageBitmap(bitmap);
			break;
		case 1:
			preview.setImageResource(R.drawable.screenlock_1_img_simplebg_1);
			break;
		case 2:
			preview.setImageResource(R.drawable.screenlock_1_img_simplebg_2);
			break;
		case 3:
			preview.setImageResource(R.drawable.screenlock_1_img_simplebg_3);
			break;
		case 4:
			preview.setImageResource(R.drawable.screenlock_1_img_simplebg_4);
			break;
		case 5:
			preview.setImageResource(R.drawable.screenlock_1_img_simplebg_5);
			break;
		case 6:
			preview.setImageResource(R.drawable.screenlock_1_img_simplebg_6);
			break;
		case 7:
			preview.setImageResource(R.drawable.screenlock_1_img_simplebg_7);
			break;
		case 8:
			preview.setImageResource(R.drawable.screenlock_1_img_simplebg_8);
			break;
		}
	}
	
	public void onClickSetBackground(View v){
		Intent intent = new Intent(getApplicationContext(), HomeMoreLockerBackgroundSetting.class);
		startActivity(intent);
	}
	public void onClickSetWord(View v){
		Intent intent = new Intent(getApplicationContext(), HomeMoreLockerWordSetting.class);
		startActivity(intent);
	}
	public void onClickBack(View v){
		finish();
	}
	
	private final class SeekBarChangeListener implements OnSeekBarChangeListener{
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			int current = seekBar.getProgress();
			
			settingEditor.putInt("lockerTransparent", current);
			settingEditor.apply();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (fromUser) {
				seeThru.getBackground().setAlpha(progress);
			} else {
				return;
			}
		}
	}
}