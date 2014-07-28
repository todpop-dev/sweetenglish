package com.todpop.sweetenglish;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class StudyBeginPagerAdapter extends FragmentStatePagerAdapter{

	ArrayList<StudyBeginWord> wordList;
	
	public StudyBeginPagerAdapter(FragmentManager fm, ArrayList<StudyBeginWord> alw) {
		super(fm);
		wordList = alw;
	}

	@Override
	public Fragment getItem(int position) {
		if(position != 10){
			return StudyBeginStudyFragment.init(position, wordList);
		}
		else{
			return StudyBeginReviewFragment.init(position, wordList);
		}
	}

	@Override
	public int getCount() {
		return 11;
	}
}