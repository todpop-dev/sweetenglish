package com.todpop.sweetenglish;

import java.util.ArrayList;

import com.todpop.api.EngKorOX;
import com.todpop.sweetenglish.db.DailyHistoryDBHelper;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

public class HomeDailyHistoryDialogFragment extends DialogFragment{
	private ArrayList<EngKorOX> engKorOXList;
	private DailyHistoryDBHelper dHelper;
	private ListView listView;
	
	static HomeDailyHistoryDialogFragment newInstance(int day){
		HomeDailyHistoryDialogFragment dailyHistoryDialog = new HomeDailyHistoryDialogFragment();
		Bundle args = new Bundle();
		args.putInt("dayOfWeek", day);
		dailyHistoryDialog.setArguments(args);
		return dailyHistoryDialog;
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		engKorOXList = new ArrayList<EngKorOX>();
		
		dHelper = new DailyHistoryDBHelper(getActivity());
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Bundle bundle = getArguments();
		
		final int day = bundle.getInt("dayOfWeek");
		
		View v = inflater.inflate(R.layout.dialog_daily_history, container, false);
		
		ImageButton btn = (ImageButton)v.findViewById(R.id.dialog_anal_close);
		btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				getDialog().dismiss();
			}
			
		});
		
		listView = (ListView)v.findViewById(R.id.dialog_history_list);
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				SQLiteDatabase db = dHelper.getReadableDatabase();
				Cursor cursor = db.rawQuery("SELECT name, mean, xo FROM history WHERE day_of_week = " + day, null);
				
				while(cursor.moveToNext()){
					engKorOXList.add(new EngKorOX(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
				}
				
				if(engKorOXList.size() == 0)
					engKorOXList.add(new EngKorOX("No History", "기록이 없습니다", "X"));
				
				handler.sendEmptyMessage(0);
			}
		}).start();
		
		return v;
	}
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			listView.setAdapter(new HomeDailyHistoryArrayAdapter(getActivity(), R.layout.list_home_history_word, engKorOXList));
		}
	};
}
