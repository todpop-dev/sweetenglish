package com.todpop.sweetenglish;

import java.util.ArrayList;

import com.todpop.api.TypefaceActivity;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class LockScreenPagerAdapter extends PagerAdapter{
	private LayoutInflater mInflater;
	
	private ArrayList<LockScreenEngKorSet> mWordList;
	
	public LockScreenPagerAdapter(Context c, ArrayList<LockScreenEngKorSet> wordList){
		super();
		mInflater = LayoutInflater.from(c);
		mWordList = wordList;
	}
	
	@Override
	public int getCount() {
		return mWordList.size();
	}
	
	@Override
	public Object instantiateItem(View pager, int position){
		View v = pager;
		
		v = mInflater.inflate(R.layout.lock_screen_view_pager, null);
			
		TextView eng = (TextView)v.findViewById(R.id.lock_screen_pager_eng);
		TextView kor = (TextView)v.findViewById(R.id.lock_screen_pager_kor);
		
		TypefaceActivity.setFont(eng);
		TypefaceActivity.setFont(kor);
		
		eng.setText(mWordList.get(position).english);
		kor.setText(mWordList.get(position).korean);
		
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
