package com.todpop.sweetenglish;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.todpop.api.MemorizedRateAllCanvas;
import com.todpop.api.MemorizedRateAllCanvasTest;
import com.todpop.sweetenglish.db.AnalysisDBHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class HomeAnalysisMemorizedAllFragment extends Fragment{
	private int total;
	private int memorized;
	private int percent;
	
	private AnalysisDBHelper aHelper;
	private SQLiteDatabase mDB;
	private MemorizedRateAllCanvas a;
	private TextView percentTextView;
	
	static HomeAnalysisMemorizedAllFragment init(){
		return new HomeAnalysisMemorizedAllFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		total = 16000;
		memorized = 16000;
		percent = memorized * 100 / total;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_memorized_rate_all, container, false);
		RelativeLayout back = (RelativeLayout)v.findViewById(R.id.memorized_back);
		a = new MemorizedRateAllCanvas(getActivity(), total, memorized);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		percentTextView = (TextView)v.findViewById(R.id.memorized_percent);
		back.addView(a, params);
		
		return v;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		new Thread(new Runnable(){
			public void run(){
				for(int i = 0; i < memorized; i ++){
					
				}
			}
		}).start();
		new Thread(new Runnable(){
			public void run(){
				for(int i = 0; i < percent; i++){
					try {
						Thread.sleep(15);
						yearlyHandler.sendEmptyMessage(i + 1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	
	Handler yearlyHandler = new Handler(){
		public void handleMessage(Message msg){
			a.setTotalAndCorrect(total, msg.what);
			if(msg.what * 100 / total <= percent)
				percentTextView.setText(String.valueOf(msg.what * 100 / total));
		}
	};
}
