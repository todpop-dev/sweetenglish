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

import android.os.AsyncTask;
import android.util.Log;

public class GetKakao extends AsyncTask<Void, Void, JSONObject>{
	DefaultHttpClient httpClient ;
	KakaoObject kakaoObj;
	
	public GetKakao(KakaoObject kakaoObj) {
		this.kakaoObj = kakaoObj;
	}
	@Override
	protected JSONObject doInBackground(Void... urls) 
	{
		JSONObject result = null;
		try {
			String getURL = "http://todpop.co.kr/api/app_infos/get_cacao_msg.json?pro=1";
			HttpGet httpGet = new HttpGet(getURL); 
			HttpParams httpParameters = new BasicHttpParams(); 
			httpClient = new DefaultHttpClient(httpParameters); 
			HttpResponse response = httpClient.execute(httpGet); 
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {    
				result = new JSONObject(EntityUtils.toString(resEntity)); 	   
				Log.i("STEVEN GetKakao Result--", result.toString());
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
			JSONObject jsonObj = json.getJSONObject("data");
			kakaoObj.setMent(jsonObj.getString("ment"));
			kakaoObj.setBtnText(jsonObj.getString("btn_text"));
			String url = jsonObj.getString("img_url");
			if(!url.equals("")){
				kakaoObj.setImgSrc(url);
				kakaoObj.setImgHeight(jsonObj.getInt("img_height"));
				kakaoObj.setImgWidth(jsonObj.getInt("img_width"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
