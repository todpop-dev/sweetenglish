package com.todpop.sweetenglish;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class HomeDailyFragmentPagerAdapter extends FragmentPagerAdapter{
	public HomeDailyFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		if(position == 0)
			return HomeDailyGraphFragment.init();
		else
			return HomeDailyCircleFragment.init(position);
	}

	@Override
	public int getCount() {
		return 3;
	}
	
	@Override
	public int getItemPosition(Object item){
		return POSITION_NONE;
	}
}