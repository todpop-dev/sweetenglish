package com.todpop.api.request;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.todpop.sweetenglish.LockScreenEngKorSet;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetLockScreenWord extends AsyncTask<Void, Void, JSONObject>{
	DefaultHttpClient httpClient ;
	Handler handler;
	int category;
	
	public GetLockScreenWord(int category, Handler handler) {
		this.handler = handler;
		this.category = category;
	}
	@Override
	protected JSONObject doInBackground(Void... urls) 
	{
		JSONObject result = null;
		try {
			String getURL = "http://www.todpop.co.kr/api/screen_lock/word.json?pro=1&category" + category;
			HttpGet httpGet = new HttpGet(getURL); 
			HttpParams httpParameters = new BasicHttpParams(); 
			httpClient = new DefaultHttpClient(httpParameters); 
			HttpResponse response = httpClient.execute(httpGet); 
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {    
				result = new JSONObject(EntityUtils.toString(resEntity)); 
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected void onPostExecute(JSONObject json) {
		if(json == null) {
		}
		try {
			JSONArray jsonArray = json.getJSONObject("data").getJSONArray("word");
			JSONArray eng_kor;
			

			ArrayList<LockScreenEngKorSet> engKorList = new ArrayList<LockScreenEngKorSet>();
			
			Log.i("STEVEN", "json result" + json.toString());
			
			for(int i = 0; i < jsonArray.length(); i++){
				eng_kor = jsonArray.getJSONArray(i);
				
				engKorList.add(new LockScreenEngKorSet(eng_kor.getString(0), eng_kor.getString(1)));
			}
			
			Message msg = handler.obtainMessage();
			msg.obj = engKorList;
			handler.sendMessage(msg);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
