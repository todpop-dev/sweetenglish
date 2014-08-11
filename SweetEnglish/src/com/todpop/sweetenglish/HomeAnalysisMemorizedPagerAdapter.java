package com.todpop.sweetenglish;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomeAnalysisMemorizedPagerAdapter extends FragmentPagerAdapter{
	public HomeAnalysisMemorizedPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		//if(position == 0)
			return HomeAnalysisMemorizedAllFragment.init();
		/*else if(position == 1)
			return HomeAnalysisMemorizedCategoryFragment.init();
		else
			return HomeAnalysisMemorizedTimeFragment.init();*/
	}

	@Override
	public int getCount() {
		return 1;
	}

}
