package com.todpop.sweetenglish;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.EngKorOX;
import com.todpop.api.LoadingDialog;
import com.todpop.api.TrackUsageTime;
import com.todpop.api.TypefaceActivity;
import com.todpop.sweetenglish.db.AnalysisDBHelper;
import com.todpop.sweetenglish.db.DailyHistoryDBHelper;
import com.todpop.sweetenglish.db.WordDBHelper;

public class StudyTestResult extends TypefaceActivity {	
	ArrayList<String> reviewList;
	
	ArrayList<EngKorOX> arItem;
	int count = 0;

	int totalScore = 0;
	int avgScore = 0;
	
	// Database
	WordDBHelper mHelper;

	int tmpStageAccumulated;

	EngKorOX mi;

	TextView scoreView;

	LoadingDialog loadingDialog;

	SharedPreferences studyInfo;
	SharedPreferences.Editor studyInfoEdit;
	
	private TrackUsageTime tTime;

	final Calendar cal = Calendar.getInstance();
	private int today;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_test_result);
		
		Intent i = getIntent();
		reviewList = i.getStringArrayListExtra("reviewWords");

		studyInfo = getSharedPreferences("studyInfo",0);
		studyInfoEdit = studyInfo.edit();

		mHelper = new WordDBHelper(this);

		arItem = new ArrayList<EngKorOX>();

		scoreView = (TextView)findViewById(R.id.study_test_result_score);

		// Database to store words for My Word list
		SQLiteDatabase db = mHelper.getWritableDatabase();
		try {
			db.execSQL("CREATE TABLE mywords ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT NOT NULL UNIQUE, mean TEXT);");
		} catch (Exception e) {
			e.printStackTrace();
		}


		// Get Test result from database
		tmpStageAccumulated = studyInfo.getInt("tmpStageAccumulated", 1);
		SimpleDateFormat dateForamt = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		today = Integer.valueOf(dateForamt.format(cal.getTime()));
		getTestWords();

		// ----------- Request Result -------------
		//SharedPreferences pref = getSharedPreferences("rgInfo",0);
		// levelCount could be 1, 16, 61, 121 etc... 
		int category = studyInfo.getInt("lastCategory", 1);

		// ------- cys added -----------
		studyInfoEdit.putInt("currentCategory", category);
		studyInfoEdit.putInt("currentStageAccumulated", tmpStageAccumulated);		// save tmpStage -> currentStage
		int level = ((tmpStageAccumulated-1)/10) + 1;
		if(category==1)			{studyInfoEdit.putInt("levelLast1", level);Log.e("STR1",String.valueOf(level));}
		else if(category==2)	{studyInfoEdit.putInt("levelLast2", level);Log.e("STR2",String.valueOf(level));}
		else if(category==3)	{studyInfoEdit.putInt("levelLast3", level);Log.e("STR3",String.valueOf(level));}
		else					{studyInfoEdit.putInt("levelLast4", level);Log.e("STR4",String.valueOf(level));}
		studyInfoEdit.apply();
		// ----------------------------

		MyListAdapter MyAdapter = new MyListAdapter(this,R.layout.lvtest_result_list_item_view, arItem);

		ListView MyList;
		MyList=(ListView)findViewById(R.id.study_test_result_list);
		MyList.setAdapter(MyAdapter);
		
		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
		tTime = TrackUsageTime.getInstance(this);
	}

	@Override
	public void onResume(){
		super.onResume();
		
		saveToDailyHistoryDB();
	}

	private void getTestWords()
	{

		try {
			SQLiteDatabase db = mHelper.getReadableDatabase();
			
			int stage = tmpStageAccumulated % 10;

			int cnt = 0;

			if (stage == 0) {
				Cursor cursor = db.rawQuery("SELECT name,  mean, xo, memorized_date FROM dic WHERE stage > " + (((tmpStageAccumulated - 1) / 10) * 10) + " AND stage <= " + (tmpStageAccumulated - 1), null);
				
				addToList(cursor);
				
				cnt += cursor.getCount();
				
				avgScore = Math.round(((float)totalScore / cnt) * 100);
			} else if(stage == 3 || stage == 6 || stage == 9){
				Cursor cursor = db.rawQuery("SELECT name,  mean, xo, memorized_date FROM dic WHERE stage >= " + (tmpStageAccumulated - 2) + " AND stage <= " + tmpStageAccumulated, null);
				
				addToList(cursor);
				
				cnt += cursor.getCount();
				
				avgScore = Math.round(((float)totalScore / cnt) * 100);
			}
			else{
				Cursor cursor = db.rawQuery("SELECT name, mean, xo, memorized_date FROM dic WHERE stage=" + tmpStageAccumulated + ";", null);
				
				addToList(cursor);
				
				cnt += cursor.getCount();
				
				for(int i = 0; i < reviewList.size(); i++){
					Cursor reviewCursor = db.rawQuery("SELECT name, mean, xo, memorized_date FROM dic WHERE stage >= " + ((tmpStageAccumulated / 10) * 10) + 
											" AND stage <= " + (tmpStageAccumulated - 1) + " AND name = '" + reviewList.get(i) + "'", null);
					addToList(reviewCursor);
					
					cnt += reviewCursor.getCount();
				}
				
				avgScore = Math.round(((float)totalScore / cnt) * 100);
				
			}
			
			int selectedLevelNo = (tmpStageAccumulated - 1) / 10 + 1;
			int selectedStageNo = (tmpStageAccumulated - 1) % 10 + 1; 
			
			Log.i("STEVEN", "level is " + selectedLevelNo + "   stage is " + selectedStageNo);
			
			StringBuffer stageInfo = new StringBuffer(studyInfo.getString("level" + selectedLevelNo, "xxxxxxxxxx"));
			int nMedals = avgScore >= 80 ? 2: (avgScore >= 40 ? 1 : 0);
			
			char curStageInfo= stageInfo.charAt(selectedStageNo-1);
			int curStageMedals = (curStageInfo == '2' ? 2 : (curStageInfo == '1' ? 1 : 0));
			
			// set medals num
			if(curStageMedals < nMedals){
				stageInfo.deleteCharAt(selectedStageNo-1);
				stageInfo.insert(selectedStageNo-1, nMedals+"");
			}
			
			if(selectedStageNo < 10){
				char nextStageInfo= stageInfo.charAt(selectedStageNo);
	
				if(nMedals >= 1 && nextStageInfo == 'x'){ // open next stage
					stageInfo.deleteCharAt(selectedStageNo);
					stageInfo.insert(selectedStageNo, "Y");
				}
			}
			else{
				StringBuffer nextLvStageInfo = new StringBuffer(studyInfo.getString("level" + (selectedLevelNo + 1), "xxxxxxxxxx"));
				
				char nextStageInfo = nextLvStageInfo.charAt(0);
				
				if(nMedals >= 1 && nextStageInfo == 'x'){
					nextLvStageInfo.deleteCharAt(0);
					nextLvStageInfo.insert(0, "Y");
					
					SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
					Editor userInfoEdit = userInfo.edit();
					
					if(userInfo.getInt("userLevel", 1) < (selectedLevelNo + 1)){
						userInfoEdit.putInt("userLevel", (selectedLevelNo + 1));
						userInfoEdit.apply();
					}
				}
				
				studyInfoEdit.putString("level" + (selectedLevelNo + 1), nextLvStageInfo.toString());
				
			}
			
			studyInfoEdit.putString("level" + selectedLevelNo, stageInfo.toString());
			
			studyInfoEdit.apply();

			scoreView.setText(String.valueOf(avgScore));
		} catch (Exception e) {
			Log.e("AFDSDFDSFSDFDSF", "catch error");
			e.printStackTrace();
		}



	}
	
	private void addToList(Cursor cursor){
		int isNew;
		if (cursor.getCount() > 0) {
			while(cursor.moveToNext()) {
				if(cursor.getString(2).equals("O")){
					totalScore += 1;
				}
				Log.i("STEVEN", "db Date : " + cursor.getInt(3) + "  today : " + today);
				if(cursor.getInt(3) == today){
					isNew = 1;
				}
				else{
					isNew = 0;
				}
				mi = new EngKorOX(isNew, cursor.getString(0), cursor.getString(1), cursor.getString(2));
				arItem.add(mi);
			}
		}
	}

	private void saveToDailyHistoryDB(){
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
				
				DailyHistoryDBHelper dHelper = new DailyHistoryDBHelper(StudyTestResult.this);
				SQLiteDatabase db = dHelper.getWritableDatabase();
				
				ContentValues values = new ContentValues();
				EngKorOX myItem;
				
				for(int i = 0; i < arItem.size(); i++){
					myItem = arItem.get(i);
					
					values.clear();
					
					values.put("xo", myItem.getCheck());
					values.put("isNew", myItem.getIsNew());
					int effect = db.update("history", values, "name = '" + myItem.getEn() + "' AND day_of_week = " + dayOfWeek, null);
					
					if(effect <= 0){
						values.put("name", myItem.getEn());
						values.put("mean", myItem.getKr());
						values.put("day_of_week", dayOfWeek);
						values.put("isNew", myItem.getIsNew());
						db.insert("history", null, values);
					}
				}
				
				dHelper.close();
			}
		}).start();
		
		new Thread(new Runnable(){
			@Override
			public void run(){
				int time = cal.get(Calendar.HOUR_OF_DAY);
				
				Log.i("STEVEN", "time is " + time);
				AnalysisDBHelper aHelper = new AnalysisDBHelper(StudyTestResult.this);
				SQLiteDatabase db = aHelper.getWritableDatabase();
				
				EngKorOX myItem;
				
				for(int i = 0; i < arItem.size(); i++){
					myItem = arItem.get(i);
					
					if(myItem.getCheck().equals("O")){
						
						
						Log.i("STEVEN", myItem.getCheck());
						db.execSQL("INSERT OR REPLACE INTO time_memorize (time, correct, total) " +
										"VALUES (" + time + ", " +
												"COALESCE((SELECT correct FROM time_memorize WHERE time = " + time + ") + 1, 1)," +
												"COALESCE((SELECT total FROM time_memorize WHERE time = " + time +") + 1, 1)" + 
												")");
					}
					else{
						Log.i("STEVEN", "else "+myItem.getCheck());
						db.execSQL("INSERT OR REPLACE INTO time_memorize (time, correct, total) " +
								"VALUES (" + time + ", " +
										"COALESCE((SELECT correct FROM time_memorize WHERE time = " + time + "), 0)," +
										"COALESCE((SELECT total FROM time_memorize WHERE time = " + time +") + 1, 1)" + 
										")");
					}
				}
				db.close();
				aHelper.close();
			}
		}).start();
	}

	class MyListAdapter extends BaseAdapter
	{
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<EngKorOX> arSrc;
		int layout;

		public MyListAdapter(Context context,int alayout,ArrayList<EngKorOX> aarSrc)
		{
			maincon = context;
			Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = aarSrc;
			layout = alayout;
		}
		public int getCount()
		{
			return arSrc.size();
		}

		public String getItem(int position)
		{
			return arSrc.get(position).getEn();
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(int position,View convertView,ViewGroup parent)
		{
			count++;
			if(convertView == null){
				convertView = Inflater.inflate(layout, parent,false);
			}

			TextView textEn = (TextView)convertView.findViewById(R.id.lv_test_english);
			textEn.setText(arSrc.get(position).getEn());
			textEn.setSelected(true);

			TextView textKr = (TextView)convertView.findViewById(R.id.lv_test_kr);
			textKr.setText(arSrc.get(position).getKr());
			textKr.setSelected(true);

			setFont(textEn);
			setFont(textKr);

			ImageView checkView = (ImageView)convertView.findViewById(R.id.lv_test_check_correct);
			//Button itemBtn = (Button)convertView.findViewById(R.id.lv_test_btn);

			if(arSrc.get(position).getCheck().equals("O")) {
				checkView.setImageResource(R.drawable.study_5_img_o);
				//itemBtn.setBackgroundResource(R.drawable.lvtest_10_btn_pencil_on);
			} else {
				checkView.setImageResource(R.drawable.study_5_img_x);
				//itemBtn.setBackgroundResource(R.drawable.lvtest_10_btn_pencil_off);
			}

			CheckBox wordListCB = (CheckBox)convertView.findViewById(R.id.lv_test_btn);
			wordListCB.setTag(position);

			// Check if word is in word list
			try {
				SQLiteDatabase db = mHelper.getWritableDatabase();
				Cursor c = db.rawQuery("SELECT * FROM mywords WHERE name='" + arSrc.get(position).getEn() + "'" , null);
				if (c.getCount() > 0) {
					wordListCB.setChecked(true);
				} else {
					wordListCB.setChecked(false);
				}
			} catch (Exception e) {

			}


			wordListCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					try {
						if (isChecked) {
							// Insert word to DB
							SQLiteDatabase db = mHelper.getWritableDatabase();

							ContentValues cv = new ContentValues();
							cv.put("group_name", "전체 단어");
							cv.put("name", arSrc.get((Integer)(buttonView.getTag())).getEn());
							cv.put("mean", arSrc.get((Integer)(buttonView.getTag())).getKr());
							db.replace("mywords", null, cv);
						} else {
							// Delete word to DB
							SQLiteDatabase db = mHelper.getWritableDatabase();       
							try {
								db.delete("mywords", "name='" + arSrc.get((Integer)(buttonView.getTag())).getEn()+"'", null);
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			return convertView;
		}


	}

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
		{
			finish();
		}
		return false;
	}

	public void onClickContinue(View v)
	{
		finish();
	}	

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mHelper.close();
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Study Test Result");
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
		tTime.start();
	}
	@Override
	protected void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
		tTime.stop();
	}
}
