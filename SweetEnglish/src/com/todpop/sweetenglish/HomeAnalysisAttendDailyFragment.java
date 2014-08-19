package com.todpop.sweetenglish;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.todpop.sweetenglish.db.UsageTimeDBHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeAnalysisAttendDailyFragment extends Fragment{
	private TextView[] graphTop;
	private TextView[] graphBottom;
	private TextView[] date;
	private TextView[] times;
	
	
	static HomeAnalysisAttendDailyFragment init(){
		return new HomeAnalysisAttendDailyFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		graphTop = new TextView[7];
		graphBottom = new TextView[7];
		date = new TextView[7];
		times = new TextView[7];
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_attend_daily, container, false);
		for(int i = 0; i < 7; i++){
			graphTop[i] = (TextView)v.findViewById(R.id.attend_daily_graph_top_1 + (i * 4));
			graphBottom[i] = (TextView)v.findViewById(R.id.attend_daily_graph_bottom_1 + (i * 4));
			date[i] = (TextView)v.findViewById(R.id.attend_daily_date_1 + (i * 4));
			times[i] = (TextView)v.findViewById(R.id.attend_daily_times_1 + (i * 4));
		}
		
		new SetAttend().execute();
		return v;
	}
	
	private class SetAttend extends AsyncTask<Void, Void, Void>{
		int useMax = 0;
		int[] useTime;
		int[] attendTime;
		Calendar cal;
	
		@Override
		protected Void doInBackground(Void... arg0) {
			useTime = new int[7];
			attendTime = new int[7];
			
			SimpleDateFormat dateForSearch = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
			cal = Calendar.getInstance();

			String endDate = dateForSearch.format(cal.getTime());
			cal.add(Calendar.DATE, -6);
			String startDate = dateForSearch.format(cal.getTime());

			UsageTimeDBHelper uHelper = new UsageTimeDBHelper(getActivity());
			SQLiteDatabase db = uHelper.getReadableDatabase();
			
			Cursor cursor = db.rawQuery("SELECT date, usage_time, attend FROM daily_usage WHERE date >= " + startDate + " AND date <= " + endDate, null);
			int i = 0;
			int currentDate = Integer.valueOf(startDate);
			while(cursor.moveToNext()){
				while(currentDate != cursor.getInt(0)){
					useTime[i] = 0;
					attendTime[i] = 0;
					i++;
					currentDate++;
				}
				
				useTime[i] = cursor.getInt(1) / 60;
				attendTime[i] = cursor.getInt(2);
				
				if(cursor.getInt(1) > useMax){
					useMax = cursor.getInt(1) / 60;
				}
				i++;
				currentDate++;
			}
			for(; i < 7; i++){
				useTime[i] = 0;
				attendTime[i] = 0;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			super.onPostExecute(result);
			SimpleDateFormat dateForDisplay = new SimpleDateFormat("M.dd", Locale.getDefault());
			
			
			for(int i = 0; i < 7; i++){
				LinearLayout.LayoutParams topParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
				LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
				int graphHeight = (int)((float)useTime[i] / (float)useMax * 167) ;
				Log.i("STEVEN", "i = " + i + "   graphHeight = " + graphHeight);
				bottomParams.weight = graphHeight;
				topParams.weight = 42 + (167 - graphHeight);
				
				graphTop[i].setLayoutParams(topParams);
				graphBottom[i].setLayoutParams(bottomParams);
				
				if(graphHeight < 25){
					graphTop[i].setText(String.valueOf(useTime[i]));
				}
				else{
					graphBottom[i].setText(String.valueOf(useTime[i]));
				}
				
				date[i].setText(dateForDisplay.format(cal.getTime()));
				cal.add(Calendar.DATE, 1);
				
				times[i].setText(String.valueOf(attendTime[i]));
			}
		}
	}
}
