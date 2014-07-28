package com.todpop.sweetenglish;

import java.util.ArrayList;

import com.todpop.api.TypefaceActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StudyBeginReviewListAdapter extends ArrayAdapter<StudyBeginWord>{
	private ViewHolder viewHolder = null;
	private LayoutInflater inflater = null;
	private int rowResourceId;
	
	public StudyBeginReviewListAdapter(Context c, int textViewResourceId, ArrayList<StudyBeginWord> wList){
		super(c, textViewResourceId, wList);

		inflater = LayoutInflater.from(c);
		rowResourceId = textViewResourceId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		
		if(v == null){
			viewHolder = new ViewHolder();
			v = inflater.inflate(rowResourceId, null);
			
			viewHolder.eng = (TextView) v.findViewById(R.id.tv_review_item_name);
			viewHolder.kor = (TextView) v.findViewById(R.id.tv_review_item_mean);
		
			TypefaceActivity.setFont(viewHolder.eng);
			TypefaceActivity.setFont(viewHolder.kor);
			
			v.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder)v.getTag();
		}
		
		viewHolder.eng.setText(getItem(position).getEngWord());
		viewHolder.kor.setText(getItem(position).getKorWord());
		
		return v;
	}
	
	private class ViewHolder{
		TextView eng;
		TextView kor;
	}
}