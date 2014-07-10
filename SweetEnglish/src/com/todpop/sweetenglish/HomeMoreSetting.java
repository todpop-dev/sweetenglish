package com.todpop.sweetenglish;

import java.io.File;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.todpop.api.FileManager;
import com.todpop.api.TypefaceActivity;

public class HomeMoreSetting extends TypefaceActivity {

	//private static final int REQUEST_CODE_IMAGE = 0;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    

	private Uri mImageCaptureUri;
	
	SharedPreferences setting;
	SharedPreferences.Editor settingEdit;

	SharedPreferences userInfo;
	SharedPreferences.Editor userInfoEdit;

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
			String strHour = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
			String strMin = minute < 10 ?  "0" + minute : "" + minute; 
			alarmTime = strHour + ":" + strMin;
			tvFrontTime.setText(strHour);
			tvBackTime.setText(strMin);
		}
	};
	private CheckBox swAlarmOnOff;
	private CheckBox swPopupOnOff;
	private boolean isAlarmEnabled;
	private LinearLayout llSettingAlarm;
	private ImageView ivMyPicture;
	private Bitmap bmpMyPicture;
	private FileManager fm;
	
	Animation fadeOut;
	Animation fadeIn;
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_setting);


		setting = getSharedPreferences("setting", 0);
		settingEdit = setting.edit();

		userInfo = getSharedPreferences("userInfo", 0);
		userInfoEdit = userInfo.edit();

		nickname = userInfo.getString("userNick", "No_Nickname");
		isPopupEnabled = setting.getBoolean("isPopupEnabled", true);
		isAlarmEnabled = setting.getBoolean("isAlarmEnabled", true);
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

		fadeOut = AnimationUtils.loadAnimation(this, R.anim.setting_fade_out);
		fadeIn = AnimationUtils.loadAnimation(this, R.anim.setting_fade_in);
		swAlarmOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isAlarmEnabled = isChecked;
				if(!isChecked){
					llSettingAlarm.startAnimation(fadeOut);
				}else{
					llSettingAlarm.startAnimation(fadeIn);
				}
			}
		});

		swPopupOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isPopupEnabled = isChecked;
			}
		});

		initSettingViews();
	}

	@Override
	protected void onPause(){
		super.onPause();

		userInfoEdit.putString("userNick", etNickName.getText().toString());
		settingEdit.putBoolean("isPopupEnabled", isPopupEnabled);
		settingEdit.putBoolean("isAlarmEnabled", isAlarmEnabled);
		settingEdit.putString("alarmTime", alarmTime);
		userInfoEdit.apply();
		settingEdit.apply();
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
    private void doTakePhotoAction()
    {
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
        Uri uri;
        String url = fm.getUserImgPath() + "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        uri = Uri.fromFile(new File(url));
        return uri;
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != RESULT_OK){
			return;
		}
		
		switch(requestCode){
		case PICK_FROM_CAMERA:
			Intent intentCamera = new Intent("com.android.camera.action.CROP");
			intentCamera.setDataAndType(mImageCaptureUri, "image/*");
			
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

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;
			options.inPurgeable = true;
			options.inDither = true;
			
			bmpMyPicture = BitmapFactory.decodeFile(full_path, options);
			
			ivMyPicture.setImageBitmap(bmpMyPicture);

			break;
		}
	}

	public void setAlarmTime(View v){
		TimePickerDialog timePicker = new TimePickerDialog(HomeMoreSetting.this, timeSetListener, 0, 0, false);
		timePicker.setTitle("시간 설정");
		timePicker.show();
	}

	public void initStudyInfo(View v){
		// init all
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
