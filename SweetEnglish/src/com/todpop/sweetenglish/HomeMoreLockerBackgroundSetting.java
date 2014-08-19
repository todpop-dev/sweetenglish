package com.todpop.sweetenglish;

import java.io.File;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.TrackUsageTime;
import com.todpop.api.TypefaceActivity;

public class HomeMoreLockerBackgroundSetting extends TypefaceActivity {
	ImageView latestPic;
	TextView totalPic;

    private static final int PICK_FROM_ALBUM = 1;
    
	Editor settingEditor;

	TrackUsageTime tTime;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_locker_bg_setting);

		latestPic = (ImageView)findViewById(R.id.locker_bg_setting_latest);
		totalPic = (TextView)findViewById(R.id.locker_bg_setting_count);

	    SharedPreferences setting = getSharedPreferences("setting", 0);
	    settingEditor = setting.edit();
	    
		getLastTakenPic();

		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
		
		tTime = TrackUsageTime.getInstance(this);
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Home More Locker Bg Setting");
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != RESULT_OK){
			return;
		}
		
		switch(requestCode){
		case PICK_FROM_ALBUM:
			Uri uri = data.getData();
			String path;
			
			Cursor cursor = getContentResolver().query(uri, null, null, null, null);
			if(cursor  == null){
				settingEditor.putInt("lockerBgSimple", 9);
			}
			else{
				cursor.moveToFirst();
				int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
				path = cursor.getString(idx);
				settingEditor.putInt("lockerBgSimple", 0);
				settingEditor.putString("lockerBgUserPic", path);
			}

			cursor.close();
			
			settingEditor.apply();
			
			finish();
			
			break;
		}
	}
	
	public void setSimpleBackground(View v){
		switch(v.getId()){
		case R.id.locker_setting_bg_simple_1:
			settingEditor.putInt("lockerBgSimple", 1);
			break;
		case R.id.locker_setting_bg_simple_2:
			settingEditor.putInt("lockerBgSimple", 2);
			break;
		case R.id.locker_setting_bg_simple_3:
			settingEditor.putInt("lockerBgSimple", 3);
			break;
		case R.id.locker_setting_bg_simple_4:
			settingEditor.putInt("lockerBgSimple", 4);
			break;
		case R.id.locker_setting_bg_simple_5:
			settingEditor.putInt("lockerBgSimple", 5);
			break;
		case R.id.locker_setting_bg_simple_6:
			settingEditor.putInt("lockerBgSimple", 6);
			break;
		case R.id.locker_setting_bg_simple_7:
			settingEditor.putInt("lockerBgSimple", 7);
			break;
		case R.id.locker_setting_bg_simple_8:
			settingEditor.putInt("lockerBgSimple", 8);
			break;
		case R.id.locker_setting_bg_simple_9:
			settingEditor.putInt("lockerBgSimple", 9);
			break;
		}
		
		settingEditor.apply();
		
		finish();
	}
	
	public void onClickCameraRoll(View v){
    	Intent intent = new Intent(Intent.ACTION_PICK);
    	intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
    	startActivityForResult(intent, PICK_FROM_ALBUM);
	}
	
	private void getLastTakenPic(){
		String[] projection = new String[]{
				MediaStore.Images.ImageColumns._ID,
				MediaStore.Images.ImageColumns.DATA
		};
		
		final Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
									, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
		
		if(cursor.moveToFirst()){
			totalPic.setText(String.valueOf(cursor.getCount()));
			
			String imageLocation = cursor.getString(1);
			File imageFile = new File(imageLocation);
			
			if(imageFile.exists()){
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 8;
				options.inPurgeable = true;
				options.inDither = true;
				
				Bitmap bm = BitmapFactory.decodeFile(imageLocation, options);
				latestPic.setImageBitmap(bm);
			}
		}
		cursor.close();
	}
	public void onClickBack(View v){
		finish();
	}
}