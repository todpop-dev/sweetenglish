package com.todpop.sweetenglish;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class LockScreenPagerAdapter extends PagerAdapter{

	private LayoutInflater mInflater;
	
	public LockScreenPagerAdapter(Context c){
		super();
		mInflater = LayoutInflater.from(c);
	}
	
	@Override
	public int getCount() {
		return 4;
	}
	
	@Override
	public Object instantiateItem(View pager, int position){
		View v = null;
		v = mInflater.inflate(R.layout.fragment_study_category, null);

		ImageView page = (ImageView)v.findViewById(R.id.category_pager_img);

		if(position == 0){
			page.setBackgroundResource(R.drawable.study_3_img_basiccard);
		} else if(position == 1){
			page.setBackgroundResource(R.drawable.study_3_img_middlecard);
		} else if(position == 2){
			page.setBackgroundResource(R.drawable.study_3_img_highcard);
		} else if(position == 3){
			page.setBackgroundResource(R.drawable.study_3_img_toeiccard);
		}

		((ViewPager)pager).addView(v, 0);

		return v;
	}

	@Override
	public void destroyItem(View pager, int position, Object view){
		((ViewPager)pager).removeView((View)view);
	}
	@Override
	public boolean isViewFromObject(View pager, Object obj) {
		return pager == obj;
	}

}
