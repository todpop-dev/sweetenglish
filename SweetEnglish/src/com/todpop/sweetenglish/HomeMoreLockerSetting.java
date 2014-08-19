package com.todpop.sweetenglish;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.TrackUsageTime;
import com.todpop.api.TypefaceActivity;
import com.todpop.api.VerticalSeekBar;

public class HomeMoreLockerSetting extends TypefaceActivity {
	CheckBox lockerOnOff;
	
	LinearLayout previewLayout;
	ImageButton bgSettingBtn;
	Button wordSettingBtn;
	
	ImageView preview;
	ImageView seeThru;
	
	VerticalSeekBar seekBar;

	TextView barTime;
	TextView time;
	TextView apm;
	TextView date;
	
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
	private final SimpleDateFormat apmFormat = new SimpleDateFormat("a");
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("M월d일 cccc");
	
	SharedPreferences setting;
	Editor settingEditor;
	
	Animation fadeOut;
	Animation fadeIn;

	TrackUsageTime tTime;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_locker_setting);
		
		lockerOnOff = (CheckBox)findViewById(R.id.locker_setting_switch);
		
		previewLayout = (LinearLayout)findViewById(R.id.locker_setting_layout_bg);
		bgSettingBtn = (ImageButton)findViewById(R.id.locker_setting_bg_setting);
		wordSettingBtn = (Button)findViewById(R.id.locker_setting_word_setting);
		
		preview = (ImageView)findViewById(R.id.locker_setting_preview);
		seeThru = (ImageView)findViewById(R.id.locker_setting_preview_seethru);
		
		seekBar = (VerticalSeekBar)findViewById(R.id.locker_setting_seekbar_transparent);
		
		barTime = (TextView)findViewById(R.id.locker_setting_preview_bar_time);
		time = (TextView)findViewById(R.id.locker_setting_preview_time);
		apm = (TextView)findViewById(R.id.locker_setting_preview_apm);
		date = (TextView)findViewById(R.id.locker_setting_preview_date);

		fadeOut = AnimationUtils.loadAnimation(this, R.anim.setting_fade_out);
		fadeIn = AnimationUtils.loadAnimation(this, R.anim.setting_fade_in);
		
		lockerOnOff.setOnCheckedChangeListener(new CheckBoxChangeListener());
		seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());

		setting = getSharedPreferences("setting", 0);
		settingEditor = setting.edit();
		
		boolean lockerEnabled = setting.getBoolean("lockerEnabled", true);
		lockerOnOff.setChecked(lockerEnabled);
		if(!lockerEnabled){
			disable();
		}
		
		int trans = setting.getInt("lockerTransparent", 95);
		
		seekBar.setProgress(trans);
		seeThru.getBackground().setAlpha(trans);

		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
		
		tTime = TrackUsageTime.getInstance(this);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		setTime();
		
		int simpleSet = setting.getInt("lockerBgSimple", 9);
		
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
		case 9:
			preview.setImageResource(R.drawable.screenlock_1_img_simplebg_9);
			break;
		}
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Home More Locker Setting");
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
	private void setTime(){
		Date currentDate = new Date();
		barTime.setText(apmFormat.format(currentDate) + " " + timeFormat.format(currentDate));
		time.setText(timeFormat.format(currentDate));
		apm.setText(apmFormat.format(currentDate));
		date.setText(dateFormat.format(currentDate));
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
	
	private final class CheckBoxChangeListener implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(isChecked){
				settingEditor.putBoolean("lockerEnabled", true);
				Intent i = new Intent(HomeMoreLockerSetting.this, LockScreenService.class);
				startService(i);
				
				previewLayout.startAnimation(fadeIn);
				bgSettingBtn.setClickable(true);
				seekBar.setEnabled(true);
				wordSettingBtn.startAnimation(fadeIn);
				wordSettingBtn.setClickable(true);
			}
			else{
				settingEditor.putBoolean("lockerEnabled", false);	
				Intent i = new Intent(HomeMoreLockerSetting.this, LockScreenService.class);
				stopService(i);		
				
				disable();
			}
			settingEditor.apply();
		}
		
	}
	private void disable(){
		previewLayout.startAnimation(fadeOut);
		bgSettingBtn.setClickable(false);
		seekBar.setEnabled(false);
		wordSettingBtn.startAnimation(fadeOut);
		wordSettingBtn.setClickable(false);
	}
}