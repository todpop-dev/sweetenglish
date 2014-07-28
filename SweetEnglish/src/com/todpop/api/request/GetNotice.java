package com.todpop.api.request;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.todpop.api.KakaoObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class GetNotice extends AsyncTask<Void, Void, JSONObject>{
	DefaultHttpClient httpClient ;
	Context context;
	Handler handler;
	
	public GetNotice(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}
	
	@Override
	protected JSONObject doInBackground(Void... urls) 
	{
		JSONObject result = null;
		try {
			String getURL = "http://www.todpop.co.kr/api/etc/main_notice.json?pro=1";
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
			int currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
			
			JSONObject jsonObj = json.getJSONObject("data");
			int latest_version = jsonObj.getInt("android_version");
			int major_version = jsonObj.getInt("major_version");
			
			if(currentVersion < major_version){			//major update 
				handler.sendEmptyMessage(0);
			}
			else if(currentVersion < latest_version){	//minor update
				handler.sendEmptyMessage(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
