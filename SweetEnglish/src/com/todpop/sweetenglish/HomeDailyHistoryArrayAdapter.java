package com.todpop.sweetenglish;

import java.util.ArrayList;

import com.todpop.api.EngKorOX;
import com.todpop.sweetenglish.R;
import com.todpop.sweetenglish.R.drawable;
import com.todpop.sweetenglish.R.id;
import com.todpop.sweetenglish.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeDailyHistoryArrayAdapter extends ArrayAdapter<EngKorOX>{
	private ViewHolder viewHolder = null;
	private LayoutInflater inflater;
	
	public HomeDailyHistoryArrayAdapter(Context c, int textViewResourceId, ArrayList<EngKorOX> arrays){
		super(c, textViewResourceId, arrays);
		
		inflater = LayoutInflater.from(c);
	}
	
	@Override 
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		
		if(v == null){
			viewHolder = new ViewHolder();
			v = inflater.inflate(R.layout.list_home_history_word, null);
			
			viewHolder.newIv = (ImageView)v.findViewById(R.id.list_home_history_new);
			viewHolder.eng = (TextView)v.findViewById(R.id.list_home_history_eng);
			viewHolder.kor = (TextView)v.findViewById(R.id.list_home_history_kor);
			viewHolder.ox = (ImageView)v.findViewById(R.id.list_home_history_ox);
			
			v.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder)v.getTag();
		}
		
		if(getItem(position).getIsNew() == 1)
			viewHolder.newIv.setVisibility(View.VISIBLE);
		else
			viewHolder.newIv.setVisibility(View.INVISIBLE);
		
		viewHolder.eng.setText(getItem(position).getEn());
		viewHolder.kor.setText(getItem(position).getKr());
		if(getItem(position).getCheck().equals("O"))
			viewHolder.ox.setImageResource(R.drawable.graph_4_img_o);
		else
			viewHolder.ox.setImageResource(R.drawable.graph_4_img_x);
		
		viewHolder.eng.setSelected(true);
		viewHolder.kor.setSelected(true);
		
		return v;
	}
	
	private class ViewHolder{
		ImageView newIv;
		TextView eng;
		TextView kor;
		ImageView ox;
	}
}
