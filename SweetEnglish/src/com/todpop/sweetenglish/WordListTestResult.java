package com.todpop.sweetenglish;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.TrackUsageTime;
import com.todpop.api.TypefaceActivity;
import com.todpop.api.TypefaceFragmentActivity;
import com.todpop.sweetenglish.db.WordDBHelper;

import android.os.Bundle;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class WordListTestResult extends TypefaceFragmentActivity {
	public final static boolean FROM_INFI = false;
	public final static boolean FROM_WORD = true;

	private boolean fromWhere; 
	
	ViewHolder viewHolder = null;

	ArrayList<MyItem> arItem;

	// Database
	WordDBHelper mHelper;

	int wordListSize = 0;
	int wordCorrectCnt = 0;
	MyItem mi;

	ArrayList<String> enArray = new ArrayList<String>();
	ArrayList<String> krArray = new ArrayList<String>();
	
	DialogFragment mission;

	SharedPreferences studyInfo;
	SharedPreferences.Editor studyInfoEdit;
	
	SharedPreferences missionInfo;
	SharedPreferences.Editor missionEditor;
	
	private TrackUsageTime tTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wordlist_test_result);

		Intent i = getIntent();
		fromWhere = i.getBooleanExtra("fromWhere", FROM_INFI);
		
		mHelper = new WordDBHelper(this);

		studyInfo = getSharedPreferences("studyInfo", 0);
		studyInfoEdit = studyInfo.edit();

		missionInfo = getSharedPreferences("missionInfo", 0);
		missionEditor = missionInfo.edit();

		arItem = new ArrayList<MyItem>();

		SQLiteDatabase db = mHelper.getWritableDatabase();
		try {
			db.execSQL("CREATE TABLE mywords ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "name TEXT NOT NULL UNIQUE, mean TEXT);");
		} catch (Exception e) {
			e.printStackTrace();
		}

		getTestWords();

		TextView totalWords = (TextView) findViewById(R.id.wordlist_test_result_total);
		TextView levelShow = (TextView) findViewById(R.id.wordlist_test_result_correct);
		totalWords.setText(String.valueOf(wordListSize));
		levelShow.setText(String.valueOf(wordCorrectCnt));

		MyListAdapter MyAdapter = new MyListAdapter(this,
				R.layout.lvtest_result_list_item_view, arItem);

		ListView MyList;
		MyList = (ListView) findViewById(R.id.wordlist_test_result_list);
		MyList.setAdapter(MyAdapter);

		if(fromWhere == FROM_WORD){		//from wordbook test
			boolean history = missionInfo.getBoolean("wordbookTest", false);
			
			if(history == false){
				missionEditor.putBoolean("wordbookTest", true);
				missionEditor.apply();
				
				mission = MissionGetDialogFragment.newInstance(MissionGetDialogFragment.WORDBOOK_TEST);
				mission.show(getSupportFragmentManager(), "wordbookTest");
			}
		}
		else{							//from infinite challenge
			if(wordCorrectCnt >= 80){
				boolean history = missionInfo.getBoolean("infinity80", false);
				
				if(history == false){
					missionEditor.putBoolean("infinity80", true);
					missionEditor.apply();
					
					mission = MissionGetDialogFragment.newInstance(MissionGetDialogFragment.INFINITY_80);
					mission.show(getSupportFragmentManager(), "infinity80");
				}
			}
			
			if(wordCorrectCnt >= 100){
				boolean history = missionInfo.getBoolean("infinity100", false);
				
				if(history == false){
					missionEditor.putBoolean("infinity100", true);
					missionEditor.apply();
					
					mission = MissionGetDialogFragment.newInstance(MissionGetDialogFragment.INFINITY_100);
					mission.show(getSupportFragmentManager(), "infinity100");
				}
			}
		}
		
		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
		tTime = TrackUsageTime.getInstance(this);
	}

	private void getTestWords() {

		try {
			SQLiteDatabase db = mHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT name, mean, xo FROM mywordtest;",
					null);

			wordListSize = cursor.getCount();
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					mi = new MyItem(cursor.getString(0), cursor.getString(1),
							cursor.getString(2));
					arItem.add(mi);
					if(cursor.getString(2).equals("O")){
						wordCorrectCnt++;
					}
				}
				db.delete("mywordtest", null, null);
			}
		} catch (Exception e) {
			Log.e("AFDSDFDSFSDFDSF", "catch error");
			e.printStackTrace();
		}

	}

	class MyItem {
		MyItem(String aEn, String aKr, String Check) {
			en = aEn;
			kr = aKr;
			check = Check;
		}

		String en;
		String kr;
		String check;
	}

	class MyListAdapter extends BaseAdapter {
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<MyItem> arSrc;
		int layout;

		public MyListAdapter(Context context, int alayout,
				ArrayList<MyItem> aarSrc) {
			maincon = context;
			Inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = aarSrc;
			layout = alayout;
			for (int i = 0; i < wordListSize; i++) {
				Log.e("RESULT 129", arSrc.get(i).en + arSrc.get(i).kr);

			}
		}

		public int getCount() {

			return arSrc.size();
		}

		public String getItem(int position) {
			return arSrc.get(position).en;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (convertView == null) {
				convertView = Inflater.inflate(layout, parent, false);

			}
			viewHolder = new ViewHolder();
			v = Inflater.inflate(layout, parent, false);
			viewHolder.textEn = (TextView) v.findViewById(R.id.lv_test_english);
			viewHolder.textKr = (TextView) v.findViewById(R.id.lv_test_kr);
			viewHolder.checkView = (ImageView) v
					.findViewById(R.id.lv_test_check_correct);
			viewHolder.selectBtn = (CheckBox) v.findViewById(R.id.lv_test_btn);

			setFont(viewHolder.textEn);
			setFont(viewHolder.textKr);
			
			
			// v.setTag(viewHolder);

			// else {
			// viewHolder = (ViewHolder)v.getTag();
			// }

			viewHolder.textEn.setText(arSrc.get(position).en);
			viewHolder.textKr.setText(arSrc.get(position).kr);

			viewHolder.textEn.setTag(position);
			// viewHolder.textKr.setTag(position);
			viewHolder.selectBtn.setTag(position);

			Log.d("LvTestResult", "line 169" + "positoin = " + position + " "
					+ arSrc.get(position).en + arSrc.get(position).kr);
			// if(enSave != null)
			// {
			// if(enSave.get(position).equals(arSrc.get(position).en))
			// {
			// viewHolder.selectBtn.setEnabled(false);
			// }else{
			// viewHolder.selectBtn.setEnabled(true);
			// }
			// }

			if (arSrc.get(position).check.equals("O")) {
				viewHolder.checkView
						.setImageResource(R.drawable.study_5_img_o);
			} else {
				viewHolder.checkView
						.setImageResource(R.drawable.study_5_img_x);
			}

			// Check if word is in word list
			SQLiteDatabase db = mHelper.getWritableDatabase();
			Cursor c = db.rawQuery(
					"SELECT * FROM mywords WHERE name='"
							+ arSrc.get(position).en + "'", null);
			if (c.getCount() > 0) {
				viewHolder.selectBtn.setChecked(true);
			} else {
				viewHolder.selectBtn.setChecked(false);
			}

			viewHolder.selectBtn
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {

							if (isChecked) {
								// Insert word to DB
								SQLiteDatabase db = mHelper
										.getWritableDatabase();

								ContentValues cv = new ContentValues();
								cv.put("name", arSrc.get((Integer) (buttonView
										.getTag())).en);
								cv.put("mean", arSrc.get((Integer) (buttonView
										.getTag())).kr);
								db.replace("mywords", null, cv);
							} else {
								// Delete word to DB
								SQLiteDatabase db = mHelper
										.getWritableDatabase();
								try {
									db.delete(
											"mywords",
											"name='"
													+ arSrc.get((Integer) (buttonView
															.getTag())).en
													+ "'", null);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					});

			return v;
		}
	}

	class ViewHolder {
		public TextView textEn = null;
		public TextView textKr = null;
		public ImageView checkView = null;
		public CheckBox selectBtn = null;

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return false;
		}
		return false;
	}

	public void continueActivity(View v) {
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.lvtest_result, menu);
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHelper.close();
	}


	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Word List Test Result");
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
