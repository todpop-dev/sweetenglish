package com.todpop.sweetenglish;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class StudyBeginReviewFragment extends Fragment{
	static StudyBeginReviewFragment init(int val, ArrayList<StudyBeginWord> wordList){
		StudyBeginReviewFragment reviewFragment = new StudyBeginReviewFragment();
		
		Bundle args = new Bundle();
		args.putParcelableArrayList("wordList", wordList);
		reviewFragment.setArguments(args);
		return reviewFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Bundle bundle = getArguments();
		
		ArrayList<StudyBeginWord> wordClass = bundle.getParcelableArrayList("wordList");
		
		View layoutView = inflater.inflate(R.layout.fragment_study_begin_finish, container, false);
		
		ListView reviewList = (ListView) layoutView.findViewById(R.id.lv_study_begin_finish_words);

		reviewList.setAdapter(new StudyBeginReviewListAdapter(getActivity(), R.layout.review_item, wordClass));
		
		return layoutView;
	}
}