package com.todpop.sweetenglish;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;
import com.todpop.api.TrackUsageTime;
import com.todpop.api.TypefaceActivity;

public class TutorialActivity extends TypefaceActivity{
	private final int HOME = 1;
	private final int WORDBOOK = 2;
	
	private LinearLayout homeTuto;
	private LinearLayout wordBookTuto;
	
	private LinearLayout homeIndicator;
	private LinearLayout wordBookIndicator;
	
	private ImageButton goHomeBtn;
	private ImageButton goWordBook;
	
	ImageView indi1;
	ImageView indi2;
	ImageView indi3;
	ImageView indi4;
	ImageView indi5;
	
	private int tutoType;
	private ViewPager viewPager;
	
	Animation fadeIn;
	Animation fadeOut;
	
	Editor userInfoEdit;
	
	private TrackUsageTime tTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_tutorial);
		
		Intent i = getIntent();
		tutoType = i.getIntExtra("tutoType", HOME);
		
		homeTuto = (LinearLayout)findViewById(R.id.tutorial_home_layout);
		wordBookTuto = (LinearLayout)findViewById(R.id.tutorial_wordbook_layout);
		
		homeIndicator = (LinearLayout)findViewById(R.id.tutorial_home_indicator_layout);
		wordBookIndicator = (LinearLayout)findViewById(R.id.tutorial_wordbook_indicator_layout);
				
		indi1 = new ImageView(this);
		indi2 = new ImageView(this);
		indi3 = new ImageView(this);
		indi4 = new ImageView(this);
		indi5 = new ImageView(this);
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(15, 0, 15, 0);
		
		indi1.setLayoutParams(layoutParams);
		indi2.setLayoutParams(layoutParams);	
		indi3.setLayoutParams(layoutParams);
		indi4.setLayoutParams(layoutParams);
		indi5.setLayoutParams(layoutParams);
		
		indi1.setImageResource(R.drawable.tutorial_img_indicator_white_on);
		indi2.setImageResource(R.drawable.tutorial_img_indicator_navy_off);
		indi3.setImageResource(R.drawable.tutorial_img_indicator_navy_off);
		indi4.setImageResource(R.drawable.tutorial_img_indicator_navy_off);
		indi5.setImageResource(R.drawable.tutorial_img_indicator_navy_off);
		
		SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
		userInfoEdit = userInfo.edit();

		fadeIn = AnimationUtils.loadAnimation(this, R.anim.lock_screen_fade_in);
		fadeOut = AnimationUtils.loadAnimation(this, R.anim.lock_screen_fade_out);
		fadeOut.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation arg0) {
				if(tutoType == HOME)
					goHomeBtn.setClickable(false);
				else if(tutoType == WORDBOOK)
					goWordBook.setClickable(false);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationStart(Animation arg0) {
			}
			
		});
		
		if(tutoType == HOME){
			goHomeBtn = (ImageButton)findViewById(R.id.tutorial_home_btn_gohome);
			goHomeBtn.setClickable(false);
			homeTuto.setVisibility(View.VISIBLE);
			homeIndicator.addView(indi1);
			homeIndicator.addView(indi2);
			homeIndicator.addView(indi3);
			homeIndicator.addView(indi4);
		}
		else if(tutoType == WORDBOOK){
			goWordBook = (ImageButton)findViewById(R.id.tutorial_wordbook_btn_gowordbook);
			goWordBook.setClickable(false);
			wordBookTuto.setVisibility(View.VISIBLE);
			wordBookIndicator.addView(indi1);
			wordBookIndicator.addView(indi2);
			wordBookIndicator.addView(indi3);
			wordBookIndicator.addView(indi4);
			wordBookIndicator.addView(indi5);
		}
		userInfoEdit.apply();
		
		viewPager = (ViewPager)findViewById(R.id.tutorial_id_pager);
		
		viewPager.setAdapter(new TutorialPagerAdapter(this));
		viewPager.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int position) {
				switch(position){
				case 0:
					indi1.setImageResource(R.drawable.tutorial_img_indicator_white_on);
					indi2.setImageResource(R.drawable.tutorial_img_indicator_navy_off);
					break;
				case 1:
					indi1.setImageResource(R.drawable.tutorial_img_indicator_navy_off);
					indi2.setImageResource(R.drawable.tutorial_img_indicator_white_on);
					indi3.setImageResource(R.drawable.tutorial_img_indicator_navy_off);
					break;
				case 2:
					indi2.setImageResource(R.drawable.tutorial_img_indicator_navy_off);
					indi3.setImageResource(R.drawable.tutorial_img_indicator_white_on);
					indi4.setImageResource(R.drawable.tutorial_img_indicator_navy_off);
					if(tutoType == HOME && goHomeBtn.isClickable()){
						goHomeBtn.startAnimation(fadeOut);
					}
					break;
				case 3:
					indi3.setImageResource(R.drawable.tutorial_img_indicator_navy_off);
					indi4.setImageResource(R.drawable.tutorial_img_indicator_white_on);
					if(tutoType == HOME){
						goHomeBtn.setVisibility(View.VISIBLE);
						goHomeBtn.startAnimation(fadeIn);
						goHomeBtn.setClickable(true);
					}
					else if(tutoType == WORDBOOK && goWordBook.isClickable()){
						indi5.setImageResource(R.drawable.tutorial_img_indicator_navy_off);
						goWordBook.startAnimation(fadeOut);
					}
					break;
				case 4:
					indi4.setImageResource(R.drawable.tutorial_img_indicator_navy_off);
					indi5.setImageResource(R.drawable.tutorial_img_indicator_white_on);
					if(tutoType == WORDBOOK){
						goWordBook.setVisibility(View.VISIBLE);
						goWordBook.startAnimation(fadeIn);
						goWordBook.setClickable(true);
					}
					break;
				}
			}
			
		});
		tTime = TrackUsageTime.getInstance(this);
	}
	
	private class TutorialPagerAdapter extends PagerAdapter{
		private LayoutInflater mInflater;
		
		public TutorialPagerAdapter(Context c){
			super();
			mInflater = LayoutInflater.from(c);
		}

		@Override
		public int getCount() {
			if(tutoType == HOME){
				return 4;
			}
			else{
				return 5;
			}
		}
		
		@Override
        public Object instantiateItem(View pager, int position) {
            View v = null;
            
            v = mInflater.inflate(R.layout.fragment_tutorial, null);
            ImageView img = (ImageView)v.findViewById(R.id.fragment_tutorial_img);
            
            if(tutoType == HOME){
            	img.setBackgroundResource(R.drawable.tutorial_img_1 + position);
            }
            else if(tutoType == WORDBOOK){
            	img.setBackgroundResource(R.drawable.wordbook_tutorial_img_1 + position);
            }
            else{
            	Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            	startActivity(i);
            	finish();
            }
             
            ((ViewPager)pager).addView(v, 0);
             
            return v; 
        }
 
        @Override
        public void destroyItem(View pager, int position, Object view) {    
            ((ViewPager)pager).removeView((View)view);
        }
        
		@Override
		public boolean isViewFromObject(View pager, Object obj) {
			return pager == obj;
		}
	}
	
	public void goHome(View v){
		userInfoEdit.putBoolean("homeTuto", true);
		userInfoEdit.apply();
		
    	Intent i = new Intent(getApplicationContext(), HomeActivity.class);
    	startActivity(i);
    	finish();
	}
	public void goWordBook(View v){
		userInfoEdit.putBoolean("wordbookTuto", true);
		userInfoEdit.apply();
		
    	Intent i = new Intent(getApplicationContext(), HomeWordListGroup.class);
    	startActivity(i);
    	finish();
	}
	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Tutorial Activity");
		tTime.start();
	}
	@Override
	protected void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
		tTime.stop();
	}
}
