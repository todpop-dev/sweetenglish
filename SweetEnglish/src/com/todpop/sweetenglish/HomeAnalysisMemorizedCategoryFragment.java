package com.todpop.sweetenglish;

import java.util.ArrayList;

import com.todpop.sweetenglish.R;
import com.todpop.sweetenglish.R.id;
import com.todpop.sweetenglish.R.layout;
import com.todpop.sweetenglish.db.WordDBHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class HomeAnalysisMemorizedCategoryFragment extends Fragment{
	private ArrayList<Integer> categoryMemo;
	private ArrayList<ProgressBar> progressBarList;
	private int maxProgress;
	
	static HomeAnalysisMemorizedCategoryFragment init(){
		HomeAnalysisMemorizedCategoryFragment categoryFragment = new HomeAnalysisMemorizedCategoryFragment();
		return categoryFragment;
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		categoryMemo = new ArrayList<Integer>();
		progressBarList = new ArrayList<ProgressBar>();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_memorized_rate_category, container, false);
		
		progressBarList.add((ProgressBar)v.findViewById(R.id.memoized_rate_category_basic));
		progressBarList.add((ProgressBar)v.findViewById(R.id.memoized_rate_category_middle));
		progressBarList.add((ProgressBar)v.findViewById(R.id.memoized_rate_category_high));
		progressBarList.add((ProgressBar)v.findViewById(R.id.memoized_rate_category_toeic));
		
		return v;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		new ProgressAsyncTask().execute();
	}
		
	private class ProgressAsyncTask extends AsyncTask<Void, Integer, Void>{
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			for(int i = 0; i < 4; i++){
				progressBarList.get(i).setProgress(0);
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			WordDBHelper wHelper = new WordDBHelper(getActivity());
			SQLiteDatabase wDB = wHelper.getReadableDatabase();
			
			Cursor basicCorrectCursor = wDB.rawQuery("SELECT count(*) FROM dic WHERE stage <= 150 AND xo = 'O'", null);
			Cursor middleCorrectCursor = wDB.rawQuery("SELECT count(*) FROM dic WHERE stage > 150 AND stage <= 600 AND xo = 'O'", null);
			Cursor highCorrectCursor = wDB.rawQuery("SELECT count(*) FROM dic WHERE stage > 600 AND stage <= 1200 AND xo = 'O'", null);
			Cursor toeicCorrectCursor = wDB.rawQuery("SELECT count(*) FROM dic WHERE stage > 1200 AND xo = 'O'", null);

			int tempProgress;
			
			//basic
			if(basicCorrectCursor.moveToFirst()){
				int basicCorrect = basicCorrectCursor.getInt(0);
				if(basicCorrect == 0){
					categoryMemo.add(0);
					maxProgress = 0;
				}
				else{
					Cursor basicTotalCursor = wDB.rawQuery("SELECT count(*) FROM dic WHERE stage <= 150", null);
					basicTotalCursor.moveToFirst();
					int basicTotal = basicTotalCursor.getInt(0);
					tempProgress = (basicTotal == 0) ? 0 : (int)Math.round(basicCorrect * 100.0 / basicTotal);
					maxProgress = tempProgress;
					categoryMemo.add(tempProgress);
				}
			}
			else{
				categoryMemo.add(0);
			}

			//middle
			if(middleCorrectCursor.moveToFirst()){
				int middleCorrect = middleCorrectCursor.getInt(0);
				if(middleCorrect == 0){
					categoryMemo.add(0);
				}
				else{
					Cursor middleTotalCursor = wDB.rawQuery("SELECT count(*) FROM dic WHERE stage > 150 AND stage <= 600", null);
					middleTotalCursor.moveToFirst();
					int middleTotal = middleTotalCursor.getInt(0);
					tempProgress = (middleTotal == 0) ? 0 : (int)Math.round(middleCorrect * 100.0 / middleTotal);
					if(maxProgress < tempProgress)
						maxProgress = tempProgress;
					categoryMemo.add(tempProgress);
				}
			}
			else{
				categoryMemo.add(0);
			}

			//high
			if(highCorrectCursor.moveToFirst()){
				int highCorrect = highCorrectCursor.getInt(0);
				if(highCorrect == 0){
					categoryMemo.add(0);
				}
				else{
					Cursor highTotalCursor = wDB.rawQuery("SELECT count(*) FROM dic WHERE stage > 600 AND stage <= 1200", null);
					highTotalCursor.moveToFirst();
					int highTotal = highTotalCursor.getInt(0);
					tempProgress = (highTotal == 0) ? 0 : (int)Math.round(highCorrect * 100.0 / highTotal);
					if(maxProgress < tempProgress)
						maxProgress = tempProgress;
					categoryMemo.add(tempProgress);
				}
			}
			else{
				categoryMemo.add(0);
			}

			//toeic
			if(toeicCorrectCursor.moveToFirst()){
				int toeicCorrect = toeicCorrectCursor.getInt(0);
				if(toeicCorrect == 0){
					categoryMemo.add(0);
				}
				else{
					Cursor toeicTotalCursor = wDB.rawQuery("SELECT count(*) FROM dic WHERE stage > 1200", null);
					toeicTotalCursor.moveToFirst();
					int toeicTotal = toeicTotalCursor.getInt(0);
					tempProgress = (toeicTotal == 0) ? 0 : (int)Math.round(toeicCorrect * 100.0 / toeicTotal);
					if(maxProgress < tempProgress)
						maxProgress = tempProgress;
					categoryMemo.add(tempProgress);
				}				
			}
			else{
				categoryMemo.add(0);
			}
			
			wDB.close();
			wHelper.close();
			
			/*
			 * do UI things
			 */
			try {
				int[] current = new int[4];
				
				for(int i = 1; i <= maxProgress; i++){
					for(int k = 0; k < 4; k++){
						if(i <= categoryMemo.get(k)){
							current[k] = i;
						}
					}
					publishProgress(current[0], current[1], current[2], current[3]);
					
					Thread.sleep(15);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress){
			super.onProgressUpdate(progress);
			for(int i = 0; i < 4; i++){
				progressBarList.get(i).setProgress(progress[i]);
			}
		}
	}
}
