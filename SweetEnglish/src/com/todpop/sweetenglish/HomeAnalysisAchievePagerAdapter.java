package com.todpop.sweetenglish;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomeAnalysisAchievePagerAdapter extends FragmentPagerAdapter{
	public HomeAnalysisAchievePagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		if(position == 0)
			return HomeAnalysisAchieveWeeklyFragment.init();
		else
			return HomeAnalysisAchieveMonthlyFragment.init();
	}

	@Override
	public int getCount() {
		return 2;
	}

}
