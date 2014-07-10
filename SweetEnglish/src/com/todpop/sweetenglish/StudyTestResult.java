package com.todpop.sweetenglish;

import java.util.ArrayList;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import com.todpop.api.LoadingDialog;
import com.todpop.api.TypefaceActivity;
import com.todpop.sweetenglish.db.WordDBHelper;

public class StudyTestResult extends TypefaceActivity {

	ArrayList<MyItem> arItem;
	int count = 0;

	// Database
	WordDBHelper mHelper;

	int tmpStageAccumulated;

	MyItem mi;

	TextView scoreView;

	LoadingDialog loadingDialog;

	SharedPreferences studyInfo;
	SharedPreferences.Editor studyInfoEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_test_result);

		studyInfo = getSharedPreferences("studyInfo",0);
		studyInfoEdit = studyInfo.edit();

		mHelper = new WordDBHelper(this);

		arItem = new ArrayList<MyItem>();

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
		SharedPreferences studyInfo = getSharedPreferences("studyInfo", 0);
		tmpStageAccumulated = studyInfo.getInt("tmpStageAccumulated", 1);
		getTestWords();

		// ----------- Request Result -------------
		//SharedPreferences pref = getSharedPreferences("rgInfo",0);
		// levelCount could be 1, 16, 61, 121 etc... 
		int category = studyInfo.getInt("tmpCategory", 1);

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
	}


	private void getTestWords()
	{

		try {
			SQLiteDatabase db = mHelper.getReadableDatabase();
			int totalScore = 0;
			int avgScore = 0;

			if (tmpStageAccumulated%10 == 0) {
				Cursor cursor = db.rawQuery("SELECT name, mean, xo FROM flip;", null);

				Log.e("cursor.getCount()", "cursor.getCount() : "+cursor.getCount());
				if (cursor.getCount() > 0) {
					while(cursor.moveToNext()) {
						if(cursor.getString(2).equals("O")){
							totalScore += 1;
						}
						Log.d("D E F ------", cursor.getString(0) + "  " + cursor.getString(1) + "   " + cursor.getString(2));
						mi = new MyItem(cursor.getString(0), cursor.getString(1), cursor.getString(2));
						arItem.add(mi);
					}
					avgScore = Math.round(((float)totalScore / 36) * 36);
					db.delete("flip", null, null);
				}
			} else {
				Cursor cursor = db.rawQuery("SELECT name, mean, xo FROM dic WHERE stage=" + tmpStageAccumulated + ";", null);

				Log.e("cursor.getCount()", "cursor.getCount() : "+cursor.getCount());
				if (cursor.getCount() > 0) {
					while(cursor.moveToNext()) {
						if(cursor.getString(2).equals("O")){
							totalScore += 1;
						}
						Log.e("A B C ------", cursor.getString(0) + "  " + cursor.getString(1) + "   " + cursor.getString(2));
						mi = new MyItem(cursor.getString(0), cursor.getString(1), cursor.getString(2));
						arItem.add(mi);
					}
					avgScore = Math.round(((float)totalScore / 10) * 100);
				}
			}
			SharedPreferences stdInfo = getSharedPreferences("studyInfo", 0);
			Editor stdInfoEdit = stdInfo.edit();
			
			int selectedLevelNo = (tmpStageAccumulated - 1) / 10 + 1;
			int selectedStageNo = (tmpStageAccumulated - 1) % 10 + 1; 
			
			Log.i("STEVEN", "level is " + selectedLevelNo + "   stage is " + selectedStageNo);
			
			StringBuffer stageInfo = new StringBuffer(stdInfo.getString("level" + selectedLevelNo, "xxxxxxxxxx"));
			int nMedals = avgScore >= 80 ? 2: (avgScore >= 40 ? 1 : 0);
			
			char curStageInfo= stageInfo.charAt(selectedStageNo-1);
			int curStageMedals = (curStageInfo == '2' ? 2 : (curStageInfo == '1' ? 1 : 0));
			
			char nextStageInfo= stageInfo.charAt(selectedStageNo);
			
			// set medals num
			if(curStageMedals < nMedals){
				stageInfo.deleteCharAt(selectedStageNo-1);
				stageInfo.insert(selectedStageNo-1, nMedals+"");
			}

			if(nMedals >= 1 && nextStageInfo == 'x'){ // open next stage
				stageInfo.deleteCharAt(selectedStageNo);
				stageInfo.insert(selectedStageNo, "Y");
			}

			stdInfoEdit.putString("level" + selectedLevelNo, stageInfo.toString());
			stdInfoEdit.apply();

			scoreView.setText(String.valueOf(avgScore));
		} catch (Exception e) {
			Log.e("AFDSDFDSFSDFDSF", "catch error");
			e.printStackTrace();
		}



	}

	class MyItem{
		MyItem(String aEn,String aKr,String Check)
		{
			en = aEn;
			kr = aKr;
			check =Check;
		}
		String en;
		String kr;
		String check;
	}

	class MyListAdapter extends BaseAdapter
	{
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<MyItem> arSrc;
		int layout;

		public MyListAdapter(Context context,int alayout,ArrayList<MyItem> aarSrc)
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
			return arSrc.get(position).en;
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
			textEn.setText(arSrc.get(position).en);

			TextView textKr = (TextView)convertView.findViewById(R.id.lv_test_kr);
			textKr.setText(arSrc.get(position).kr);

			setFont(textEn);
			setFont(textKr);

			ImageView checkView = (ImageView)convertView.findViewById(R.id.lv_test_check_correct);
			//Button itemBtn = (Button)convertView.findViewById(R.id.lv_test_btn);

			if(arSrc.get(position).check.equals("O")) {
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
				Cursor c = db.rawQuery("SELECT * FROM mywords WHERE name='" + arSrc.get(position).en + "'" , null);
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
							cv.put("name", arSrc.get((Integer)(buttonView.getTag())).en);
							cv.put("mean", arSrc.get((Integer)(buttonView.getTag())).kr);
							db.replace("mywords", null, cv);
						} else {
							// Delete word to DB
							SQLiteDatabase db = mHelper.getWritableDatabase();       
							try {
								db.delete("mywords", "name='" + arSrc.get((Integer)(buttonView.getTag())).en+"'", null);
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
	protected void onStart()
	{
		super.onStart();
		//FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		//EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();		
		//FlurryAgent.onEndSession(this);
		//EasyTracker.getInstance(this).activityStop(this);
	}
}
