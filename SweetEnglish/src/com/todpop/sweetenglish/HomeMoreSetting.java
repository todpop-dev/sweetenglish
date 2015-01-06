package com.todpop.sweetenglish;

import java.io.File;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.FileManager;
import com.todpop.api.TrackUsageTime;
import com.todpop.api.TypefaceActivity;
import com.todpop.api.TypefaceFragmentActivity;
import com.todpop.sweetenglish.db.WordDBHelper;

public class HomeMoreSetting extends TypefaceFragmentActivity {
	
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

	private Uri mImageCaptureUri;
	
	private SharedPreferences setting;
	private SharedPreferences.Editor settingEdit;

	private SharedPreferences userInfo;
	private SharedPreferences.Editor userInfoEdit;
	
	private SharedPreferences studyInfo;
	private SharedPreferences.Editor studyInfoEdit;

	private SharedPreferences missionInfo;
	private Editor missionEditor;

	private String nickname;
	private boolean isPopupEnabled;
	private String alarmTime;

	private EditText etNickName;
	private TextView tvFrontTime;
	private TextView tvBackTime;

	private AlertDialog userPicDialog;
	
	private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			setAlarm(hourOfDay, minute);
			
			String strHour = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
			String strMin = minute < 10 ?  "0" + minute : "" + minute; 
			
			alarmTime = strHour + ":" + strMin;
			tvFrontTime.setText(strHour);
			tvBackTime.setText(strMin);
			
			settingEdit.putString("alarmTime", alarmTime);
			settingEdit.apply();
		}
	};
	private CheckBox swAlarmOnOff;
	private CheckBox swPopupOnOff;
	private boolean isAlarmEnabled;
	private LinearLayout llSettingAlarm;
	private ImageView ivMyPicture;
	private Bitmap bmpMyPicture;
	private FileManager fm;
	
	private NotificationManager notificationManager;
	private AlarmManager alarmManager;
	
	private Animation fadeOut;
	private Animation fadeIn;
	
	private PopupWindow popupWindow;
	
	private TrackUsageTime tTime;
	
	private DialogFragment popupDialog;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_setting);


		setting = getSharedPreferences("setting", 0);
		settingEdit = setting.edit();

		userInfo = getSharedPreferences("userInfo", 0);
		userInfoEdit = userInfo.edit();
		
		studyInfo = getSharedPreferences("studyInfo", 0);
		studyInfoEdit = studyInfo.edit();

		missionInfo = getSharedPreferences("missionInfo", 0);
		missionEditor = missionInfo.edit();

		nickname = userInfo.getString("userNick", "No_Nickname");
		isPopupEnabled = setting.getBoolean("isPopupEnabled", false);
		isAlarmEnabled = setting.getBoolean("isAlarmEnabled", false);
		alarmTime = setting.getString("alarmTime","00:00");
		
		llSettingAlarm = (LinearLayout)findViewById(R.id.ll_setting_alarm); 
		
		ivMyPicture = (ImageView)findViewById(R.id.iv_setting_mypicture);

		etNickName = (EditText)findViewById(R.id.et_setting_name);
		tvFrontTime = (TextView)findViewById(R.id.tv_setting_alarm_time_front);
		tvBackTime = (TextView)findViewById(R.id.tv_setting_alarm_time_back);

		swAlarmOnOff = (CheckBox)findViewById(R.id.sw_setting_alarm_onoff);
		swPopupOnOff= (CheckBox)findViewById(R.id.sw_setting_popup_onoff);
		
		fm = new FileManager(this);
		if(fm.isUserImgSet()){
			bmpMyPicture = fm.getUserImgFile();
			ivMyPicture.setImageBitmap(bmpMyPicture);
		}

		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		fadeOut = AnimationUtils.loadAnimation(this, R.anim.setting_fade_out);
		fadeIn = AnimationUtils.loadAnimation(this, R.anim.setting_fade_in);
		
		initSettingViews();
		
		swAlarmOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isAlarmEnabled = isChecked;
				if(!isChecked){
					llSettingAlarm.startAnimation(fadeOut);
					llSettingAlarm.setClickable(false);
					swPopupOnOff.startAnimation(fadeOut);
					swPopupOnOff.setClickable(false);
				}else{
					checkAlarmMission();
					
					llSettingAlarm.startAnimation(fadeIn);
					llSettingAlarm.setClickable(true);
					swPopupOnOff.startAnimation(fadeIn);
					swPopupOnOff.setClickable(true);
				}
				setAlarm(Integer.valueOf(tvFrontTime.getText().toString()), Integer.valueOf(tvBackTime.getText().toString()));
				
				settingEdit.putBoolean("isAlarmEnabled", isAlarmEnabled);
				settingEdit.apply();
			}
		});

		swPopupOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isPopupEnabled = isChecked;
				
				setAlarm(Integer.valueOf(tvFrontTime.getText().toString()), Integer.valueOf(tvBackTime.getText().toString()));

				settingEdit.putBoolean("isPopupEnabled", isPopupEnabled);
				settingEdit.apply();
			}
		});

		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
		
		tTime = TrackUsageTime.getInstance(this);
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Home More Setting");
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
	protected void onPause(){
		super.onPause();

		userInfoEdit.putString("userNick", etNickName.getText().toString());
		userInfoEdit.apply();
	}
	private void initSettingViews() {
		etNickName.setText(nickname);
		String[] timeToken = alarmTime.split(":");
		tvFrontTime.setText(timeToken[0]);
		tvBackTime.setText(timeToken[1]);
		swPopupOnOff.setChecked(isPopupEnabled);
		swAlarmOnOff.setChecked(isAlarmEnabled);
		if(!isAlarmEnabled){
			llSettingAlarm.startAnimation(fadeOut);
			llSettingAlarm.setClickable(false);
			swPopupOnOff.startAnimation(fadeOut);
			swPopupOnOff.setClickable(false);
		}
	}

	private void setAlarm(int targetHour, int targetMinute){	
		Intent intentReceiver = new Intent(getApplicationContext(), AlamReceiver.class);
		PendingIntent pendingIntentReceiver = PendingIntent.getBroadcast(getApplicationContext(), 0, intentReceiver, 0);
		
		Intent intentActivity = new Intent("com.todpop.sweetenglish.alarmactivity");
		PendingIntent pendingIntentActivity = PendingIntent.getActivity(getApplicationContext(), 0, intentActivity, Intent.FLAG_ACTIVITY_NEW_TASK);
		
		//cancel all
		notificationManager.cancelAll();
		alarmManager.cancel(pendingIntentReceiver);
		alarmManager.cancel(pendingIntentActivity);

		if(isAlarmEnabled){			// Setup Alarm
			Calendar calendar =  Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, targetHour);
			calendar.set(Calendar.MINUTE, targetMinute);
			long when = calendar.getTimeInMillis();         // notification time
			
			if(isPopupEnabled){
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, when, AlarmManager.INTERVAL_DAY, pendingIntentActivity);
			}
			else{
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, when, AlarmManager.INTERVAL_DAY, pendingIntentReceiver);		
			}
		}
	}
	
	private void checkAlarmMission(){
		boolean hasHistory = missionInfo.getBoolean("alarm", false);
		if(hasHistory == false){
			missionEditor.putBoolean("alarm", true);
			missionEditor.apply();
			
			popupDialog = MissionGetDialogFragment.newInstance(MissionGetDialogFragment.ALARM);
			popupDialog.show(getSupportFragmentManager(), "alarm");
		}
	}
	
	public void setMyPicture(View v){
		userPicDialog = createDialog();
		userPicDialog.show();
	}
	
	private AlertDialog createDialog(){
		final View innerView = getLayoutInflater().inflate(R.layout.alert_user_image, null);
		
		Button camera = (Button)innerView.findViewById(R.id.alert_user_img_camera);
		Button gallery = (Button)innerView.findViewById(R.id.alert_user_img_gallery);
		
		camera.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				doTakePhotoAction();
				setDismiss(userPicDialog);
			}
		});
		gallery.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				doTakeAlbumAction();
				setDismiss(userPicDialog);
			}
		});

		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		
		ab.setTitle("Choose");
		ab.setView(innerView);
		
		return ab.create();
	}

    private void setDismiss(AlertDialog dialog){
    	if(dialog != null && dialog.isShowing())
    		dialog.dismiss();
    }
    private void doTakePhotoAction(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageCaptureUri = createSaveCropFile();
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }
    
    private void doTakeAlbumAction(){
    	Intent intent = new Intent(Intent.ACTION_PICK);
    	intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
    	startActivityForResult(intent, PICK_FROM_ALBUM);
    }
    
    private Uri createSaveCropFile(){
    	Log.i("STEVEN", "createSaveCropFile fm.getUserImgPath() : " + fm.getUserImgPath());
        Uri uri;
        String url = fm.getUserImgPath() + "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        uri = Uri.fromFile(new File(url));
        return uri;
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("STEVEN", "onActivityResult ///////  result code is " + resultCode);
		Log.i("STEVEN", "onActivityResult ///////  request code is " + requestCode);
		if(resultCode != RESULT_OK){
			return;
		}
		
		switch(requestCode){
		case PICK_FROM_CAMERA:
			Intent intentCamera = new Intent("com.android.camera.action.CROP");
			intentCamera.setDataAndType(mImageCaptureUri, "image/*");
			
			intentCamera.putExtra("outputX", 250);
			intentCamera.putExtra("outputY", 250);
			intentCamera.putExtra("aspectX", 1);
			intentCamera.putExtra("aspectY", 1);
			intentCamera.putExtra("output", mImageCaptureUri);
			
			startActivityForResult(intentCamera, CROP_FROM_CAMERA);
			break;
		case PICK_FROM_ALBUM:
			mImageCaptureUri = data.getData();

			Intent intentAlbum = new Intent("com.android.camera.action.CROP");
			intentAlbum.setDataAndType(mImageCaptureUri, "image/*");
			
			mImageCaptureUri = createSaveCropFile();

			intentAlbum.putExtra("outputX", 250);
			intentAlbum.putExtra("outputY", 250);
			intentAlbum.putExtra("aspectX", 1);
			intentAlbum.putExtra("aspectY", 1);
			intentAlbum.putExtra("output", mImageCaptureUri);
			
			startActivityForResult(intentAlbum, CROP_FROM_CAMERA);
			break;
		case CROP_FROM_CAMERA:
			String full_path = mImageCaptureUri.getPath();
			
			SharedPreferences.Editor userInfoEditor = userInfo.edit();
			userInfoEditor.putString("userImgPath", full_path);
			userInfoEditor.apply();
			
			bmpMyPicture = BitmapFactory.decodeFile(full_path);
			ivMyPicture.setImageBitmap(bmpMyPicture);

			break;
		}
	}

	public void setAlarmTime(View v){
		TimePickerDialog timePicker;
		
		if(android.os.Build.VERSION.SDK_INT >= 11){
			timePicker = new TimePickerDialog(HomeMoreSetting.this, TimePickerDialog.THEME_HOLO_LIGHT, timeSetListener, 0, 0, false);
		}
		else{
			timePicker = new TimePickerDialog(HomeMoreSetting.this, timeSetListener, 0, 0, false);
		}
		
		timePicker.setTitle("시간 설정");
		timePicker.show();
	}

	public void initStudyInfo(View v){
		// init all
		
		View popupView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_reset, null);
		ImageView popupCancel = (ImageView)popupView.findViewById(R.id.iv_more_setting_reset_popup_cancel);
		ImageView popupReset = (ImageView)popupView.findViewById(R.id.iv_more_setting_reset_popup_reset);
		popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);

		popupCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
			}
		});
		popupReset.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
				resetStudyHistory();
			}
		});

		popupWindow.showAtLocation(etNickName, Gravity.CENTER, 0, 0);
	}
	
	private void resetStudyHistory(){

		WordDBHelper mHelper = new WordDBHelper(this);
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.delete("dic", null, null);
		db.delete("mywords", null, null);
		db.delete("mywordtest", null, null);
		db.delete("word_groups", null, null);
		db.close();
		
		getSharedPreferences("studyInfo", 0).edit().clear().apply();
	}

	public void saveSetting(View v){
		userInfoEdit.putString("nickname", etNickName.getText().toString());
		settingEdit.putBoolean("isPopupEnabled", isPopupEnabled);
		settingEdit.putBoolean("isAlarmEnabled", isAlarmEnabled);
		settingEdit.putString("alarmTime", alarmTime);
		userInfoEdit.apply();
		settingEdit.apply();
		
		finish();
	}
	public void onClickBack(View v){
		finish();
	}
}
