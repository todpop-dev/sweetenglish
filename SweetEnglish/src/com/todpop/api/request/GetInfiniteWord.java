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
import org.json.JSONException;
import org.json.JSONObject;

import com.todpop.sweetenglish.StudyTestInfiniteEngKorSet;

import android.os.AsyncTask;
import android.os.Handler;

public class GetInfiniteWord extends AsyncTask<Void, Void, JSONObject>{
	DefaultHttpClient httpClient ;
	ArrayList<StudyTestInfiniteEngKorSet> engKorList;
	Handler handler;
	
	public GetInfiniteWord(ArrayList<StudyTestInfiniteEngKorSet> engKorList, Handler handler) {
		this.engKorList = engKorList;
		this.handler = handler;
	}
	@Override
	protected JSONObject doInBackground(Void... urls) 
	{
		JSONObject result = null;
		try {
			String getURL = "http://www.todpop.co.kr/api/studies/weekly_challenge.json?pro=1";
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
		try {
			if(json != null){
				if(json.getBoolean("status")){
					JSONArray jsonArray = json.getJSONArray("test");
					JSONObject jsonObj;
						
					for(int i = 0; i < jsonArray.length(); i++){
						jsonObj = jsonArray.getJSONObject(i);
						
						engKorList.add(new StudyTestInfiniteEngKorSet(jsonObj.getString("word"), jsonObj.getString("mean"), jsonObj.getString("incorrect1")));
					}
						
					handler.sendEmptyMessage(0);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
