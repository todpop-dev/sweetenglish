package com.todpop.sweetenglish;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.TrackUsageTime;
import com.todpop.api.TypefaceActivity;
import com.todpop.sweetenglish.db.WordDBHelper;

public class HomeMoreLockerWordSetting extends TypefaceActivity {
	ListView categoryList;
	ListView myWordList;
	
	LockerWordSettingListAdapter categoryAdapter;
	LockerWordSettingListAdapter myWordAdapter;
	
	ArrayList<String> category;
	ArrayList<String> myWordGroup;
	
	int selected;
	
	SharedPreferences setting;
	Editor settingEditor;

	TrackUsageTime tTime;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_locker_word_setting);
		
		categoryList = (ListView)findViewById(R.id.locker_setting_listview_category);
		myWordList = (ListView)findViewById(R.id.locker_setting_listview_wordlist);
		

		setting = getSharedPreferences("setting", 0);
		settingEditor = setting.edit();
		
		setList();
		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
		
		tTime = TrackUsageTime.getInstance(this);
	}
	
	private void setList(){
		category = new ArrayList<String>();
		myWordGroup = new ArrayList<String>();
		
		int selectedCategory = setting.getInt("lockerWordCategory", 0); //6 for myWordList
		
		String allWord = getResources().getString(R.string.home_more_locker_word_setting_list_category_all);
		category.add(getResources().getString(R.string.home_more_locker_word_setting_list_category_recent));
		category.add(allWord);
		category.add(getResources().getString(R.string.home_more_locker_word_setting_list_category_basic));
		category.add(getResources().getString(R.string.home_more_locker_word_setting_list_category_middle));
		category.add(getResources().getString(R.string.home_more_locker_word_setting_list_category_high));
		category.add(getResources().getString(R.string.home_more_locker_word_setting_list_category_toeic));
		
		WordDBHelper dbHelper = new WordDBHelper(getApplicationContext());
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		myWordGroup.add(allWord);

		if(selectedCategory < 6){		//if selected word list is category
			selected = selectedCategory;
			
			Cursor groupCursor = db.rawQuery("SELECT name FROM word_groups", null);
			while(groupCursor.moveToNext()){
				myWordGroup.add(groupCursor.getString(0));
			}
		}
		else{							//if selected word list is myList
			String selectedMyList = setting.getString("lockerWordMyList", allWord);
			
			if(!selectedMyList.equals(allWord)){	//if not all word
				Cursor groupCursor = db.rawQuery("SELECT name FROM word_groups", null);
				while(groupCursor.moveToNext()){
					selectedCategory++;
					
					if(selectedMyList.equals(groupCursor.getString(0))){
						selected = selectedCategory;
					}
					myWordGroup.add(groupCursor.getString(0));
				}
			}
			else{									//if all word
				selected = selectedCategory;
				
				Cursor groupCursor = db.rawQuery("SELECT name FROM word_groups", null);
				while(groupCursor.moveToNext()){
					myWordGroup.add(groupCursor.getString(0));
				}
			}
		}
		
		db.close();
		dbHelper.close();
		
		categoryAdapter = new LockerWordSettingListAdapter(this, category, true);
		myWordAdapter = new LockerWordSettingListAdapter(this, myWordGroup, false);
		
		categoryList.setAdapter(categoryAdapter);
		myWordList.setAdapter(myWordAdapter);	
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Home More Locker Word Setting");
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
	class LockerWordSettingListAdapter extends BaseAdapter{

		class ViewHolder{
			CheckBox chk;
			TextView title;
		}

		private Context mContext;
		private ArrayList<String> mArrGroups;
		private Boolean mIsCategory;

		public LockerWordSettingListAdapter(Context context, ArrayList<String> arrGroups, Boolean isCategory) {
			mContext = context;
			mArrGroups = arrGroups;
			mIsCategory = isCategory;
		}

		@Override
		public int getCount() {
			return mArrGroups.size();
		}

		@Override
		public Object getItem(int position) {
			return mArrGroups.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mArrGroups.hashCode();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.home_more_locker_word_item_view, null);
				holder.title = (TextView) convertView.findViewById(R.id.locker_setting_word_list_text);
				holder.chk = (CheckBox) convertView.findViewById(R.id.locker_setting_word_list_check);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			final String item = (String) getItem(position);
			holder.title.setText(item);

			holder.chk.setClickable(false);
			
			if(mIsCategory){			//if category
				if(position == selected){	
					holder.chk.setChecked(true);
				}
				else{
					holder.chk.setChecked(false);
				}
				convertView.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						selected = position;
						settingEditor.putInt("lockerWordCategory", position);
						settingEditor.apply();
						categoryAdapter.notifyDataSetChanged();
						myWordAdapter.notifyDataSetChanged();
					}
					
				});
			}
			else if(!mIsCategory){		//if my word list
				if(position == selected - 6){
					holder.chk.setChecked(true);
				}
				else{
					holder.chk.setChecked(false);
				}
				convertView.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						selected = position + 6;
						settingEditor.putInt("lockerWordCategory", 6);
						settingEditor.putString("lockerWordMyList", item);
						settingEditor.apply();
						categoryAdapter.notifyDataSetChanged();
						myWordAdapter.notifyDataSetChanged();
					}
				});
			}
			
			return convertView;
		}

	}

	public void onClickBack(View v){
		finish();
	}
}