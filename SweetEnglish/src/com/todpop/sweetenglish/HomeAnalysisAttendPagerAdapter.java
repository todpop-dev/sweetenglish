package com.todpop.sweetenglish;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomeAnalysisAttendPagerAdapter extends FragmentPagerAdapter{
	public HomeAnalysisAttendPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		if(position == 0)
			return HomeAnalysisAttendContinueFragment.init();
		else if(position == 1)
			return HomeAnalysisAttendTimeFragment.init();
		else
			return HomeAnalysisAttendDailyFragment.init();
	}

	@Override
	public int getCount() {
		return 3;
	}

}
