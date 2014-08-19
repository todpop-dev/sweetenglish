package com.todpop.sweetenglish;

import com.todpop.api.MemorizedRateAllCanvas;
import com.todpop.sweetenglish.R;
import com.todpop.sweetenglish.R.id;
import com.todpop.sweetenglish.R.layout;
import com.todpop.sweetenglish.db.AnalysisDBHelper;
import com.todpop.sweetenglish.db.WordDBHelper;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class HomeAnalysisMemorizedAllFragment extends Fragment{
	private static final int ANI_TIME = 2500;		//2.5sec
	
	private int total;
	private int memorized;
	private int percent;
	
	private MemorizedRateAllCanvas a;
	private TextView percentTextView;
	
	static HomeAnalysisMemorizedAllFragment init(int total, int memorized){
		HomeAnalysisMemorizedAllFragment allFragment = new HomeAnalysisMemorizedAllFragment();
		
		Bundle args = new Bundle();
		args.putInt("total", total);
		args.putInt("memorized", memorized);
		allFragment.setArguments(args);
		return allFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		total = args.getInt("total");
		memorized = args.getInt("memorized");
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
				long time = ANI_TIME / memorized;
				for(int i = 0; i < memorized; i ++){
					try{
						Thread.sleep(time);
						countHandler.sendEmptyMessage(i + 1);
					} catch (InterruptedException e) {		
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		new Thread(new Runnable(){
			public void run(){
				long time = ANI_TIME / percent;
				for(int i = 0; i < percent; i++){
					try {
						Thread.sleep(time);
						percentHandler.sendEmptyMessage(i + 1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	

	Handler countHandler = new Handler(){
		public void handleMessage(Message msg){
			a.setTotalAndCorrect(total, msg.what);
		}
	};
	Handler percentHandler = new Handler(){
		public void handleMessage(Message msg){
			percentTextView.setText(String.valueOf(msg.what));
		}
	};
}
