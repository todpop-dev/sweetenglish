package com.todpop.sweetenglish;

import com.todpop.sweetenglish.R;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class HomeAnalysisDialogFragment extends DialogFragment{
	private static final int ANALYSIS_ATTEND = 0;
	private static final int ANALYSIS_MEMORIZE = 1;
	private static final int ANALYSIS_ACHIEVE = 2;
	
	private TextSwitcher textSwitcher;
	
	private ImageView indi1;
	private ImageView indi2;
	private ImageView indi3;
	
	static HomeAnalysisDialogFragment newInstance(int type, Integer arg1, Integer arg2){
		HomeAnalysisDialogFragment fragment = new HomeAnalysisDialogFragment();
		Bundle args = new Bundle();
		args.putInt("type", type);
		if(type == ANALYSIS_MEMORIZE){
			args.putInt("arg1", arg1);
			args.putInt("arg2", arg2);
		}
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.dialog_analysis, container, false);
		
		ImageButton btn = (ImageButton)v.findViewById(R.id.dialog_anal_close);
		btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				getDialog().dismiss();
			}
		});
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		
		textSwitcher = (TextSwitcher)v.findViewById(R.id.dialog_anal_title);
		textSwitcher.setFactory(new ViewFactory(){
			@Override
			public View makeView() {
				TextView txtView = new TextView(getActivity());
				txtView.setGravity(Gravity.CENTER);
				txtView.setTextSize(15);
				txtView.setTextColor(Color.WHITE);
				return txtView;
			}
			
		});
		
		Animation in = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
		Animation out = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
		
		textSwitcher.setInAnimation(in);
		textSwitcher.setOutAnimation(out);
		
		ViewPager viewPager = (ViewPager)v.findViewById(R.id.dialog_analysis_pager);
		
		LinearLayout indiLayout = (LinearLayout)v.findViewById(R.id.dialog_anal_indicator);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(15, 0, 15, 0);
		
		Bundle args = getArguments();
		
		int type = args.getInt("type");
		
		switch(type){
		case ANALYSIS_ATTEND:
			indi1 = new ImageView(getActivity());
			indi2 = new ImageView(getActivity());
			indi3 = new ImageView(getActivity());
			indi1.setLayoutParams(layoutParams);
			indi2.setLayoutParams(layoutParams);
			indi3.setLayoutParams(layoutParams);
			indi1.setImageResource(R.drawable.common_popup_img_indicator_blue);
			indi2.setImageResource(R.drawable.common_popup_img_indicator_gray);
			indi3.setImageResource(R.drawable.common_popup_img_indicator_gray);
			indiLayout.addView(indi1);
			indiLayout.addView(indi2);
			indiLayout.addView(indi3);
			
			viewPager.setAdapter(new HomeAnalysisAttendPagerAdapter(getChildFragmentManager()));
			viewPager.setOnPageChangeListener(new AttendPageChangeListener());
			textSwitcher.setText(getResources().getString(R.string.analysis_attendance_continue));
			break;
		case ANALYSIS_MEMORIZE:
			indi1 = new ImageView(getActivity());
			indi2 = new ImageView(getActivity());
			indi3 = new ImageView(getActivity());
			indi1.setLayoutParams(layoutParams);
			indi2.setLayoutParams(layoutParams);
			indi3.setLayoutParams(layoutParams);
			indi1.setImageResource(R.drawable.common_popup_img_indicator_blue);
			indi2.setImageResource(R.drawable.common_popup_img_indicator_gray);
			indi3.setImageResource(R.drawable.common_popup_img_indicator_gray);
			indiLayout.addView(indi1);
			indiLayout.addView(indi2);
			indiLayout.addView(indi3);
			
			int total = args.getInt("arg1");
			int memorized = args.getInt("arg2");
			viewPager.setAdapter(new HomeAnalysisMemorizedPagerAdapter(getChildFragmentManager(), total, memorized));
			viewPager.setOnPageChangeListener(new MemorizePageChangeListener());
			textSwitcher.setText(getResources().getString(R.string.analysis_memorized_rate_all));
			break;
		case ANALYSIS_ACHIEVE:
			indi1 = new ImageView(getActivity());
			indi2 = new ImageView(getActivity());
			indi1.setLayoutParams(layoutParams);
			indi2.setLayoutParams(layoutParams);
			indi1.setImageResource(R.drawable.common_popup_img_indicator_blue);
			indi2.setImageResource(R.drawable.common_popup_img_indicator_gray);
			indiLayout.addView(indi1);
			indiLayout.addView(indi2);
			
			viewPager.setAdapter(new HomeAnalysisAchievePagerAdapter(getChildFragmentManager()));
			viewPager.setOnPageChangeListener(new AchievePageChangeListener());
			textSwitcher.setText(getResources().getString(R.string.analysis_weekly_achieve));
			break;
		}
		return v;
	}
	
	private class AttendPageChangeListener implements OnPageChangeListener{
		@Override
		public void onPageScrollStateChanged(int arg0) {}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}
		@Override
		public void onPageSelected(int position) {
			if(position == 0){
				textSwitcher.setText(getResources().getString(R.string.analysis_attendance_continue));
				indi1.setImageResource(R.drawable.common_popup_img_indicator_blue);
				indi2.setImageResource(R.drawable.common_popup_img_indicator_gray);
			}
			else if(position == 1){
				textSwitcher.setText(getResources().getString(R.string.analysis_attendance_time));
				indi1.setImageResource(R.drawable.common_popup_img_indicator_gray);
				indi2.setImageResource(R.drawable.common_popup_img_indicator_blue);
				indi3.setImageResource(R.drawable.common_popup_img_indicator_gray);
			}
			else{
				textSwitcher.setText(getResources().getString(R.string.analysis_attendance_daily));
				indi2.setImageResource(R.drawable.common_popup_img_indicator_gray);
				indi3.setImageResource(R.drawable.common_popup_img_indicator_blue);
			}
		}
	}	
	private class MemorizePageChangeListener implements OnPageChangeListener{
		@Override
		public void onPageScrollStateChanged(int arg0) {}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}
		@Override
		public void onPageSelected(int position) {
			if(position == 0){
				textSwitcher.setText(getResources().getString(R.string.analysis_memorized_rate_all));
				indi1.setImageResource(R.drawable.common_popup_img_indicator_blue);
				indi2.setImageResource(R.drawable.common_popup_img_indicator_gray);
			}
			else if(position == 1){
				textSwitcher.setText(getResources().getString(R.string.analysis_memorized_rate_category));
				indi1.setImageResource(R.drawable.common_popup_img_indicator_gray);
				indi2.setImageResource(R.drawable.common_popup_img_indicator_blue);
				indi3.setImageResource(R.drawable.common_popup_img_indicator_gray);
			}
			else{
				textSwitcher.setText(getResources().getString(R.string.analysis_memorized_rate_time));
				indi2.setImageResource(R.drawable.common_popup_img_indicator_gray);
				indi3.setImageResource(R.drawable.common_popup_img_indicator_blue);
			}
		}
	}
	private class AchievePageChangeListener implements OnPageChangeListener{
		@Override
		public void onPageScrollStateChanged(int arg0) {}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}
		@Override
		public void onPageSelected(int position) {
			if(position == 0){
				textSwitcher.setText(getResources().getString(R.string.analysis_weekly_achieve));
				indi1.setImageResource(R.drawable.common_popup_img_indicator_blue);
				indi2.setImageResource(R.drawable.common_popup_img_indicator_gray);
			}
			else{
				textSwitcher.setText(getResources().getString(R.string.analysis_monthly_achieve));
				indi1.setImageResource(R.drawable.common_popup_img_indicator_gray);
				indi2.setImageResource(R.drawable.common_popup_img_indicator_blue);
			}
		}
	}
}
