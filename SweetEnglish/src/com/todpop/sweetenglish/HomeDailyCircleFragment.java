package com.todpop.sweetenglish;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.todpop.sweetenglish.R;
import com.todpop.sweetenglish.R.id;
import com.todpop.sweetenglish.R.layout;
import com.todpop.sweetenglish.db.DailyHistoryDBHelper;
import com.todpop.sweetenglish.db.WordDBHelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeDailyCircleFragment extends Fragment implements OnClickListener{
	private WordDBHelper wHelper;
	
	SharedPreferences userInfo;
	
	private DialogFragment historyDialog;
	
	static HomeDailyCircleFragment init(int page){
		HomeDailyCircleFragment graphFragment = new HomeDailyCircleFragment();
		
		Bundle args = new Bundle();
		args.putInt("page", page);
		
		graphFragment.setArguments(args);
		return graphFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		wHelper = new WordDBHelper(getActivity());
		
		userInfo = getActivity().getSharedPreferences("userInfo", 0);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View layoutView;
		
		Bundle bundle = getArguments();
		
		SQLiteDatabase db = wHelper.getReadableDatabase();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		Calendar cal = Calendar.getInstance();
		
		int today = Integer.valueOf(dateFormat.format(cal.getTime()));
		
		int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
		if(day_of_week == Calendar.SUNDAY){
			cal.add(Calendar.DATE, -6);
		}
		else{
			cal.add(Calendar.DATE, -(day_of_week - 2));
		}
		
		int tempDate = Integer.valueOf(dateFormat.format(cal.getTime()));
		
		if(bundle.getInt("page") == 1){		//Monday to Wednesday
			layoutView = inflater.inflate(R.layout.fragment_home_circle_left, container, false);
			
			TextView goalText = (TextView)layoutView.findViewById(R.id.fragment_circle_left_text_goal);
			goalText.setOnClickListener(this);
			goalText.setText(String.valueOf(userInfo.getInt("userGoal", 30)));

			for(int i = 0; i < 3; i++){
				TextView textView = (TextView)layoutView.findViewById(R.id.fragment_circle_left_text_mon + i);
				textView.setOnClickListener(this);
				
				if(tempDate <= today){
					Cursor cursor = db.rawQuery("SELECT COUNT(DISTINCT name)  FROM dic WHERE memorized_date = " + tempDate, null);
					if(cursor.moveToFirst()){
						textView.setText(cursor.getString(0));
					}
				}
				tempDate++;
			}
			
			
			/*for(int day = Calendar.MONDAY; day <= Calendar.WEDNESDAY; day++){
				TextView textView = (TextView)layoutView.findViewById(R.id.fragment_circle_left_text_mon + (day - 2));
				textView.setOnClickListener(this);
				
				Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM history WHERE day_of_week = " + day,  null);
				
				if(cursor.moveToFirst()){
					textView.setText(cursor.getString(0));
				}
			}*/
		}
		else{								//Thursday to Sunday
			layoutView = inflater.inflate(R.layout.fragment_home_circle_right, container, false);
			
			tempDate += 3;
			
			for(int i = 0; i < 4; i++){
				TextView textView = (TextView)layoutView.findViewById(R.id.fragment_circle_left_text_thu + i);
				textView.setOnClickListener(this);
				
				if(tempDate <= today){
					Cursor cursor = db.rawQuery("SELECT COUNT(DISTINCT name)  FROM dic WHERE memorized_date = " + tempDate, null);
					if(cursor.moveToFirst()){
						textView.setText(cursor.getString(0));
					}
				}
				tempDate++;
			}
			
			/*TextView sundayText = (TextView)layoutView.findViewById(R.id.fragment_circle_left_text_sun);
			sundayText.setOnClickListener(this);
			
			Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM history WHERE day_of_week = " + Calendar.SUNDAY,  null);
			
			if(cursor.moveToFirst()){
				sundayText.setText(cursor.getString(0));
			}
			
			for(int day = Calendar.THURSDAY; day <= Calendar.SATURDAY; day++){
				TextView textView = (TextView)layoutView.findViewById(R.id.fragment_circle_left_text_mon + (day - 2));
				textView.setOnClickListener(this);
				
				Cursor cursor2 = db.rawQuery("SELECT COUNT(*) FROM history WHERE day_of_week = " + day,  null);
				
				if(cursor2.moveToFirst()){
					textView.setText(cursor2.getString(0));		
				}
			}*/
		}
		
		wHelper.close();
		
		return layoutView;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.fragment_circle_left_text_goal:
			Intent intent = new Intent(getActivity().getApplicationContext(), HomeMoreGoal.class);
			startActivity(intent);
			break;
		case R.id.fragment_circle_left_text_mon:
			historyDialog = HomeDailyHistoryDialogFragment.newInstance(Calendar.MONDAY);
			historyDialog.show(getChildFragmentManager(), "history");
			break;
		case R.id.fragment_circle_left_text_tue:
			historyDialog = HomeDailyHistoryDialogFragment.newInstance(Calendar.TUESDAY);
			historyDialog.show(getChildFragmentManager(), "history");
			break;
		case R.id.fragment_circle_left_text_wed:
			historyDialog = HomeDailyHistoryDialogFragment.newInstance(Calendar.WEDNESDAY);
			historyDialog.show(getChildFragmentManager(), "history");
			break;
		case R.id.fragment_circle_left_text_thu:
			historyDialog = HomeDailyHistoryDialogFragment.newInstance(Calendar.THURSDAY);
			historyDialog.show(getChildFragmentManager(), "history");
			break;
		case R.id.fragment_circle_left_text_fri:
			historyDialog = HomeDailyHistoryDialogFragment.newInstance(Calendar.FRIDAY);
			historyDialog.show(getChildFragmentManager(), "history");
			break;
		case R.id.fragment_circle_left_text_sat:
			historyDialog = HomeDailyHistoryDialogFragment.newInstance(Calendar.SATURDAY);
			historyDialog.show(getChildFragmentManager(), "history");
			break;
		case R.id.fragment_circle_left_text_sun:
			historyDialog = HomeDailyHistoryDialogFragment.newInstance(Calendar.SUNDAY);
			historyDialog.show(getChildFragmentManager(), "history");
			break;
		}
	}
}
