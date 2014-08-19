package com.todpop.sweetenglish;

import java.util.ArrayList;

import com.todpop.api.MemorizedRateAllCanvas;
import com.todpop.api.LineGraphCanvas;
import com.todpop.sweetenglish.db.AnalysisDBHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class HomeAnalysisMemorizedTimeFragment extends Fragment{
	LineGraphCanvas a;
	
	ArrayList<String> xAxis;
	ArrayList<Integer> values;
	static HomeAnalysisMemorizedTimeFragment init(){
		return new HomeAnalysisMemorizedTimeFragment();
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
		
		AnalysisDBHelper aHelper = new AnalysisDBHelper(getActivity());
		SQLiteDatabase db = aHelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT correct, total FROM time_memorize WHERE time >= 23 OR time <= 1", null);
		addFromQuery(cursor);
		cursor = db.rawQuery("SELECT correct, total FROM time_memorize WHERE time >= 2 AND time <= 4", null);
		addFromQuery(cursor);
		cursor = db.rawQuery("SELECT correct, total FROM time_memorize WHERE time >= 5 AND time <= 7", null);
		addFromQuery(cursor);
		cursor = db.rawQuery("SELECT correct, total FROM time_memorize WHERE time >= 8 AND time <= 10", null);
		addFromQuery(cursor);
		cursor = db.rawQuery("SELECT correct, total FROM time_memorize WHERE time >= 11 AND time <= 13", null);
		addFromQuery(cursor);
		cursor = db.rawQuery("SELECT correct, total FROM time_memorize WHERE time >= 14 AND time <= 16", null);
		addFromQuery(cursor);
		cursor = db.rawQuery("SELECT correct, total FROM time_memorize WHERE time >= 17 AND time <= 19", null);
		addFromQuery(cursor);
		cursor = db.rawQuery("SELECT correct, total FROM time_memorize WHERE time >= 20 AND time <= 22", null);
		addFromQuery(cursor);
		
		db.close();
		aHelper.close();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_memorized_rate_time, container, false);
		RelativeLayout back = (RelativeLayout)v.findViewById(R.id.memorized_back);

		a = new LineGraphCanvas(getActivity(), 100, 5, xAxis, values);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		back.addView(a, params);
		return v;
	}
	
	private void addFromQuery(Cursor cursor){
		if(cursor.moveToFirst()){
			if(cursor.getInt(0) != 0){
				int a = (int)((float)cursor.getInt(0) * 100 / cursor.getInt(1));
				values.add(a);
			}
			else{
				values.add(0);
			}
		}
		else{
			values.add(0);
		}
	}
}
