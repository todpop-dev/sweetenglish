package com.todpop.sweetenglish;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeAnalysisAttendContinueFragment extends Fragment{
	private final static int TOTAL_WEIGHT = 372;
	private final static int GRAPH_MAX = 225;
	
	private TextView highestText;
	private View highestGraph;
	private TextView currentText;
	private View currentGraph;
	
	private int highest;
	private int current;
	
	static HomeAnalysisAttendContinueFragment init(){
		return new HomeAnalysisAttendContinueFragment();
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		SharedPreferences studyInfo = getActivity().getSharedPreferences("studyInfo", 0);
		
		highest = studyInfo.getInt("highestAttend", 0);
		current = studyInfo.getInt("continuousStudy", 0);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_attend_continue, container, false);
		
		highestText = (TextView)v.findViewById(R.id.attendance_continue_highest_text);
		highestGraph = (View)v.findViewById(R.id.attendance_continue_highest_graph);
		currentText = (TextView)v.findViewById(R.id.attendance_continue_current_text);
		currentGraph = (View)v.findViewById(R.id.attendance_continue_current_graph);

		new AnimationAsync().execute();
		return v;
	}
	
	private class AnimationAsync extends AsyncTask<Void, Integer, Void>{
		LinearLayout.LayoutParams highestTopParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
		LinearLayout.LayoutParams highestBottomParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
		LinearLayout.LayoutParams currentTopParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
		LinearLayout.LayoutParams currentBottomParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
		@Override
		protected void onPreExecute(){
			highestTopParams.weight = TOTAL_WEIGHT;
			highestBottomParams.weight = 0;
			currentTopParams.weight = TOTAL_WEIGHT;
			currentBottomParams.weight = 0;
			
			highestText.setLayoutParams(highestTopParams);
			highestGraph.setLayoutParams(highestBottomParams);
			currentText.setLayoutParams(currentTopParams);
			currentGraph.setLayoutParams(currentBottomParams);
			
			highestText.setText("0");
			currentText.setText("0");
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				if(highest > 0){
					for(int i = 1; i <= GRAPH_MAX; i++){
						Thread.sleep(7);
						publishProgress(i);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress){
			super.onProgressUpdate(progress);
			highestTopParams.weight = TOTAL_WEIGHT - progress[0];
			highestBottomParams.weight = progress[0];
			
			highestText.setLayoutParams(highestTopParams);
			highestGraph.setLayoutParams(highestBottomParams);
			
			int tempHighest = (int)(highest * (float)progress[0] / GRAPH_MAX);
			highestText.setText(String.valueOf(tempHighest));
			
			if((tempHighest + 1) <= current){
				currentTopParams.weight = TOTAL_WEIGHT - progress[0];
				currentBottomParams.weight = progress[0];
				
				currentText.setLayoutParams(currentTopParams);
				currentGraph.setLayoutParams(currentBottomParams);
				
				currentText.setText(String.valueOf(tempHighest));
			}else if(tempHighest == current){
				currentText.setText(String.valueOf(current));
			}
		}
	}
}
