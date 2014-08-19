package com.todpop.sweetenglish;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomeAnalysisMemorizedPagerAdapter extends FragmentPagerAdapter{
	private int total;
	private int memorized;
	
	public HomeAnalysisMemorizedPagerAdapter(FragmentManager fm, int total, int memorized) {
		super(fm);
		
		this.total = total;
		this.memorized = memorized;
	}

	@Override
	public Fragment getItem(int position) {
		if(position == 0)
			return HomeAnalysisMemorizedAllFragment.init(total, memorized);
		else if(position == 1)
			return HomeAnalysisMemorizedCategoryFragment.init();
		else
			return HomeAnalysisMemorizedTimeFragment.init();
	}

	@Override
	public int getCount() {
		return 3;
	}

}
