package com.todpop.sweetenglish;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.todpop.api.TypefaceActivity;

public class LockScreen extends TypefaceActivity{
	ImageView background;
	ImageView seeThru;
	
	TextView time;
	TextView apm;
	TextView date;
	
	ViewPager cardViewPager;
	
	ImageView leftArrow;
	ImageView rightArrow;
	
	ImageView goApp;
	ImageView unlock;
	
	SeekBar lockSeekBar;
	
	SharedPreferences setting;
	
	public class EngKorSet{
		private String english;
		private String korean;
		
		public EngKorSet(String eng, String kor){
			english = eng;
			korean = kor;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_screen);
		
		background = (ImageView)findViewById(R.id.lock_screen_background);
		seeThru = (ImageView)findViewById(R.id.lock_screen_seethru);
		
		time = (TextView)findViewById(R.id.lock_screen_text_time);
		apm = (TextView)findViewById(R.id.lock_screen_text_apm);
		date = (TextView)findViewById(R.id.lock_screen_text_date);
		
		cardViewPager = (ViewPager)findViewById(R.id.lock_screen_viewpager);
		
		leftArrow = (ImageView)findViewById(R.id.lock_screen_arrow_left);
		rightArrow = (ImageView)findViewById(R.id.lock_screen_arrow_right);
		
		goApp = (ImageView)findViewById(R.id.lock_screen_img_goapp);
		unlock = (ImageView)findViewById(R.id.lock_screen_img_unlock);
		
		setting = getSharedPreferences("setting", 0);
		
		int trans = setting.getInt("lockerTransparent", 95);
		
		setBackground();
		seeThru.getBackground().setAlpha(trans);
	}
	
	private void setBackground(){
		int simpleSet = setting.getInt("lockerBgSimple", 4);
		
		switch(simpleSet){
		case 0:
			String path = setting.getString("lockerBgUserPic", "");
			
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			
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
		}
	}
}
