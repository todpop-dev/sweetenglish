package com.todpop.sweetenglish;

import java.util.ArrayList;

import com.todpop.api.LineGraphCanvas;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class HomeAnalysisAttendTimeFragment extends Fragment{
	LineGraphCanvas a;
	
	ArrayList<String> xAxis;
	ArrayList<Integer> values;
	
	private int maxValue;
	
	static HomeAnalysisAttendTimeFragment init(){
		return new HomeAnalysisAttendTimeFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		xAxis = new ArrayList<String>();
		values = new ArrayList<Integer>();
		
		xAxis.add("00");
		xAxis.add("03");
		xAxis.add("06");
		xAxis.add("09");
		xAxis.add("12");
		xAxis.add("15");
		xAxis.add("18");
		xAxis.add("21");
		
		SharedPreferences studyInfo = getActivity().getSharedPreferences("studyInfo", 0);
		
		int sum = studyInfo.getInt("attendHour23", 0) + studyInfo.getInt("attendHour0", 0) + studyInfo.getInt("attendHour1", 0);
		values.add(sum);
		maxValue = sum;
		for(int i = 2; i < 23; i += 3){
			sum = studyInfo.getInt("attendHour" + i, 0) + studyInfo.getInt("attendHour" + (i + 1), 0) + studyInfo.getInt("attendHour" + (i + 2), 0);
			values.add(sum);
			
			if(maxValue < sum)
				maxValue = sum;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_attend_time, container, false);
		RelativeLayout back = (RelativeLayout)v.findViewById(R.id.memorized_back);

		int maxY = maxValue + (10 - (maxValue % 5));
		a = new LineGraphCanvas(getActivity(), false, true, maxY, 5, xAxis, values, 
				Color.rgb(147, 149, 152), Color.rgb(209, 211, 212), Color.BLACK, Color.rgb(37, 42, 58), Color.rgb(246, 249, 237), Color.rgb(2, 181, 237), Color.rgb(253, 186, 40),
				Paint.Style.STROKE);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		back.addView(a, params);
		return v;
	}
}
