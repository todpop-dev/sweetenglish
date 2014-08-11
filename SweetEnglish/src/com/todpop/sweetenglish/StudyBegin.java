package com.todpop.sweetenglish;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.LoadingDialog;
import com.todpop.api.TypefaceFragmentActivity;
import com.todpop.api.request.DownloadAndPlayPronounce;
import com.todpop.sweetenglish.db.PronounceDBHelper;
import com.todpop.sweetenglish.db.WordDBHelper;


public class StudyBegin extends TypefaceFragmentActivity{	
	ViewPager studyPage;
	
	// page point
	private ImageView point1;
	private ImageView point2;
	private ImageView point3;
	private ImageView point4;
	private ImageView point5;
	private ImageView point6;
	private ImageView point7;
	private ImageView point8;
	private ImageView point9;
	private ImageView point10;
	
	private SharedPreferences studyInfo;
	private Editor studyInfoEdit;

	private int tmpLevel;
	private int tmpStageAccumulated;
	private int tmpStage;
	private int tmpCategory;
	
	private ArrayList<StudyBeginWord> words;
	private ArrayList<String> reviewList;
	private int reviewCnt = 1;
	
	private WordDBHelper mHelper;
	private SQLiteDatabase mDB;
	private PronounceDBHelper pHelper;
	private SQLiteDatabase pDB;
	
	private LoadingDialog loadingDialog;
	
	private DownloadAndPlayPronounce player;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_begin);

		studyPage = (ViewPager)findViewById(R.id.study_begin_id_pager);
		
		//page point
		point1 = (ImageView)findViewById(R.id.studybigin_id_point1);
		point2 = (ImageView)findViewById(R.id.studybigin_id_point2);
		point3 = (ImageView)findViewById(R.id.studybigin_id_point3);
		point4 = (ImageView)findViewById(R.id.studybigin_id_point4);
		point5 = (ImageView)findViewById(R.id.studybigin_id_point5);
		point6 = (ImageView)findViewById(R.id.studybigin_id_point6);
		point7 = (ImageView)findViewById(R.id.studybigin_id_point7);
		point8 = (ImageView)findViewById(R.id.studybigin_id_point8);
		point9 = (ImageView)findViewById(R.id.studybigin_id_point9);
		point10 = (ImageView)findViewById(R.id.studybigin_id_point10);

		studyInfo = getSharedPreferences("studyInfo",0);
		studyInfoEdit = studyInfo.edit();
		tmpStageAccumulated = studyInfo.getInt("tmpStageAccumulated", 1);
		tmpCategory = studyInfo.getInt("tmpCategory", 1);
		
		tmpLevel = ((tmpStageAccumulated - 1) / 10) + 1;
		tmpStage = tmpStageAccumulated % 10;
		
		words = new ArrayList<StudyBeginWord>();
		reviewList = new ArrayList<String>();
		
		mHelper = new WordDBHelper(this);
		pHelper = new PronounceDBHelper(this);
		
		player = new DownloadAndPlayPronounce(this);

		loadingDialog = new LoadingDialog(this);
		loadingDialog.show();
		
		studyPage.setOnPageChangeListener(new PageListener());
		
		String getWordsUrl = "http://todpop.co.kr/api/studies/get_level_words.json?stage=" + tmpStage + "&level=" + tmpLevel;
		
		new GetWord().execute(getWordsUrl);

		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Study Begin");
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	@Override
	protected void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
	private class GetWord extends AsyncTask<String, Void, JSONObject>{
		DefaultHttpClient httpClient ;
		
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject result = null;
			try{
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL); 
				HttpParams httpParameters = new BasicHttpParams(); 
				int timeoutConnection = 5000; 
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
				int timeoutSocket = 5000; 
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 

				httpClient = new DefaultHttpClient(httpParameters); 
				HttpResponse response = httpClient.execute(httpGet); 
				HttpEntity resEntity = response.getEntity();

				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity));
				}
				return result;
			}
			catch(Exception e){
				
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if(json.getBoolean("status") == true) {
					mDB = mHelper.getWritableDatabase();
					
					JSONArray jsonWords = json.getJSONArray("data");
					JSONObject jsonObj;
					
					for(int i = 0; i < jsonWords.length(); i++){
						jsonObj = jsonWords.getJSONObject(i);
						if(!rowExistCheck(jsonObj.getString("name"))){
							ContentValues row = new ContentValues();
							
							row.put("name", jsonObj.getString("name"));
							row.put("mean", jsonObj.getString("mean"));
							row.put("example_en", jsonObj.getString("example_en"));
							row.put("example_ko", jsonObj.getString("example_ko"));
							row.put("phonetics", jsonObj.getString("phonetics"));
							row.put("picture", jsonObj.getString("picture"));
							row.put("image_url", jsonObj.getString("image_url"));
							row.put("stage", tmpStageAccumulated);
							row.put("xo", "X");
							
							mDB.insert("dic", null, row);
						}
						
						words.add(new StudyBeginWord(jsonObj.getString("name"), jsonObj.getString("mean"), jsonObj.getString("example_en"),
								jsonObj.getString("example_ko"), jsonObj.getString("phonetics"), jsonObj.getString("image_url"), jsonObj.getString("voice")));
					}
					
					if(jsonWords.length() < 10){	//2,3,4,5,6,7,8,9 stage	
						/*
						 * Add review 3 words from this level's wrong word
						 */
						Cursor reviewCursor = mDB.rawQuery("SELECT DISTINCT name, mean, example_en, example_ko, phonetics, image_url FROM dic WHERE" + 
											" xo = \'X\' AND stage > " + ((tmpStageAccumulated / 10) * 10) + " AND stage <= " + (tmpStageAccumulated - 1) + 
											" ORDER BY RANDOM() LIMIT 3", null);
						addToWords(reviewCursor);
					}
					if(words.size() < 10){
						String overlap = "";
						for(int i = (10 - jsonWords.length()); i > 0; i--){
							overlap += "'" + jsonWords.getJSONObject(i).getString("name") + "'";
							
							if(i != 1)
								overlap += ",";
						}
						
						Cursor reviewCursor = mDB.rawQuery("SELECT DISTINCT name, mean, example_en, example_ko, phonetics, image_url FROM dic WHERE " +
								"xo=\'O\' AND stage>=" + ((tmpStageAccumulated / 10) * 10) + " AND stage <=" + (tmpStageAccumulated - 1) + 
								" AND name NOT IN ("+overlap+") ORDER BY RANDOM() LIMIT " + (10 - words.size()) , null);
						addToWords(reviewCursor);
					}
					
					studyInfoEdit.apply();		//save review words for lock screen

					studyPage.setAdapter(new StudyBeginPagerAdapter(getSupportFragmentManager(), words));

					loadingDialog.dissmiss();
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private boolean rowExistCheck(String eng){
		Cursor cursor = mDB.rawQuery("SELECT 1 FROM dic WHERE name = '" + eng + "' AND stage = " + tmpStageAccumulated, null);
		if(cursor.getCount() > 0)
			return true;
		else
			return false;
	}
	
	private void addToWords(Cursor cursor){
		while(cursor.moveToNext()){
			
			/*
			 * save review words for lock screen
			 */
			studyInfoEdit.putString("reviewWord" + reviewCnt, cursor.getString(0));
			reviewCnt++;
			
			/*
			 * save review words for study and test 
			 */
			pDB = pHelper.getReadableDatabase();
			Cursor soundCursor = pDB.rawQuery("SELECT version FROM pronounce WHERE word='" + cursor.getString(0) + "'", null);

			if(soundCursor.getCount() > 0){
				soundCursor.moveToFirst();
				words.add(new StudyBeginWord(cursor.getString(0), cursor.getString(1), cursor.getString(2)
						, cursor.getString(3), cursor.getString(4), cursor.getString(5), soundCursor.getString(0)));
				reviewList.add(cursor.getString(0));
			}
			else{
				words.add(new StudyBeginWord(cursor.getString(0), cursor.getString(1), cursor.getString(2)
						, cursor.getString(3), cursor.getString(4), cursor.getString(5), "1"));
				reviewList.add(cursor.getString(0));
			}
			
			pDB.close();
		}
	}
	public void onClickBack(View v){
		finish();
	}
	public void showTestActivity(View v){
		if(tmpStage == 1 || tmpStage == 2 || tmpStage == 4 || tmpStage == 5 || tmpStage == 7 || tmpStage == 8) {
			Intent intent = new Intent(getApplicationContext(), StudyTestRenewal.class);
			intent.putExtra("reviewWords", reviewList);
			startActivity(intent);
			finish();
		} else if(tmpStage==3 || tmpStage==6 || tmpStage==9) {
			Intent intent = new Intent(getApplicationContext(), StudyTest369.class);
			startActivity(intent);
			finish();
		}
	}
	public void readItForMe(View v){
		StudyBeginWord temp = words.get(studyPage.getCurrentItem());
		player.readItForMe(temp.getEngWord(), String.valueOf(temp.getVoiceVer()), tmpCategory);
	}
	
	private class PageListener implements OnPageChangeListener{
		@Override
		public void onPageSelected(int position) {
			switch(position){
			case 0:
				point1.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point2.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 1:
				point1.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point2.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point3.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 2:
				point2.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point3.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point4.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 3:
				point3.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point4.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point5.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 4: 
				point4.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point5.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point6.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 5: 
				point5.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point6.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point7.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 6: 
				point6.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point7.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point8.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 7: 
				point7.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point8.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point9.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 8: 
				point8.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point9.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point10.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 9: 
				point9.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point10.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				break;
			}
		}
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {			
		}
	}
}