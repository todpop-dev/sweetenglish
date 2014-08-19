package com.todpop.sweetenglish;

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

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.TrackUsageTime;
import com.todpop.api.TypefaceActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeMoreFAQ extends TypefaceActivity {

	ArrayList<String> titleArray = new ArrayList<String>() ;
	ArrayList<String> itemArray = new ArrayList<String>();

	TrackUsageTime tTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_notice);

		ImageView title = (ImageView)findViewById(R.id.notice_img_title);
		
		title.setImageResource(R.drawable.faq_text_faqtitle);
		
		new GetHelp().execute("http://todpop.co.kr/api/app_infos/get_helps.json?pro=1");

		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
		
		tTime = TrackUsageTime.getInstance(this);
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Home more FAQ");
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
	//--- request class ---
	private class GetHelp extends AsyncTask<String, Void, JSONObject> 
	{
		DefaultHttpClient httpClient ;
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL); 
				HttpParams httpParameters = new BasicHttpParams(); 
				httpClient = new DefaultHttpClient(httpParameters); 
				HttpResponse response = httpClient.execute(httpGet); 
				HttpEntity resEntity = response.getEntity();


				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 		        	
				}
				return result;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			if(json == null) {
			}

			try {
				if	(json.getBoolean("status")==true)
				{
					if(json.getJSONArray("data").length() > 0)
					{
						JSONArray jsonArray = json.getJSONArray("data");
						for(int i=0;i<jsonArray.length();i++)
						{
							titleArray.add(jsonArray.getJSONObject(i).getString("title"));
							itemArray.add(jsonArray.getJSONObject(i).getString("content"));
						}

						ExpandableListAdapter adapter = new BaseExpandableListAdapter()
						{
							@Override
							public Object getChild(int groupPosition, int childPosition)
							{
								//return arms[groupPosition][childPosition];
								return itemArray.get(groupPosition);
							}
							@Override
							public long getChildId(int groupPosition, int childPosition)
							{
								return childPosition;
							}
							@Override
							public int getChildrenCount(int groupPosition)
							{
								//return arms[groupPosition].length;
								return 1;//itemArray.size();
							}
							private TextView getTextView()
							{
								AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
										ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								TextView textView = new TextView(HomeMoreFAQ.this);
								textView.setLayoutParams(lp);
								textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
								textView.setPadding(70, 20, 0, 20);
								textView.setBackgroundResource(R.drawable.faq_img_answerbox);
								textView.setTextColor(Color.rgb(0, 0, 0));
								textView.setTextSize(14);
								setFont(textView);
								return textView;
							}
							@Override
							public View getChildView(int groupPosition, int childPosition,
									boolean isLastChild, View convertView, ViewGroup parent)
							{
								TextView textView = getTextView();			
								textView.setText(getChild(groupPosition, childPosition).toString());
								return textView;
							}
							@Override
							public Object getGroup(int groupPosition)
							{
								return titleArray.get(groupPosition);
							}
							@Override
							public int getGroupCount()
							{
								return titleArray.size();
							}
							@Override
							public long getGroupId(int groupPosition)
							{
								return groupPosition;
							}
							@Override
							public View getGroupView(int groupPosition, boolean isExpanded,
									View convertView, ViewGroup parent)
							{
								if(convertView == null){
									convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.notice_item, null);
								}
								
								TextView contentTitle = (TextView)convertView.findViewById(R.id.tv_notice_item_title);
								contentTitle.setText(getGroup(groupPosition).toString());
								setFont(contentTitle);
								convertView.findViewById(R.id.iv_notice_item_arrow).setSelected(isExpanded);
								
								return convertView;
							}
							@Override
							public boolean isChildSelectable(int groupPosition, int childPosition)
							{
								return true;
							}
							@Override
							public boolean hasStableIds()
							{
								return true;
							}
						};
						ExpandableListView expandListView = (ExpandableListView)findViewById(R.id.homemorenotice_id_listview_item);
						expandListView.setAdapter(adapter);

					}
				}

			} catch (Exception e) {

			}
		}
	}

	// on click
	public void onClickBack(View view)
	{
		finish();
	}
	
}



