package com.todpop.sweetenglish;

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
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class HomeAnalysisDialogFragment extends DialogFragment{
	static HomeAnalysisDialogFragment newInstance(int type){
		HomeAnalysisDialogFragment fragment = new HomeAnalysisDialogFragment();
		Bundle args = new Bundle();
		args.putInt("type", type);
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
		
		final TextSwitcher textSwitcher = (TextSwitcher)v.findViewById(R.id.dialog_anal_title);
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

		
		Bundle args = getArguments();
		
		int type = args.getInt("type");
		viewPager.setAdapter(new HomeAnalysisMemorizedPagerAdapter(getChildFragmentManager()));
		/*
		textSwitcher.setText(getResources().getString(R.string.analysis_month_achieve));
		
		viewPager.setAdapter(new HomeAnalysisFragmentPagerAdapter(getChildFragmentManager()));
		viewPager.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}

			@Override
			public void onPageSelected(int position) {
				if(position == 0)
					textSwitcher.setText(getResources().getString(R.string.analysis_month_achieve));
				else
					textSwitcher.setText(getResources().getString(R.string.analysis_year_achieve));
			}
			
		});*/
		return v;
	}
}
