package com.todpop.sweetenglish;

import com.todpop.api.FileManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeDrawerAdapter extends BaseAdapter{
	private Context context;
	private ViewHolder viewHolder;
	
	private LayoutInflater inflater = null;
	private String userId;
	private int userLv;
	
	public HomeDrawerAdapter(Context c, String userId, int userLv){
		this.context = c;
		this.inflater = LayoutInflater.from(c);
		this.userId = userId;
		this.userLv = userLv;
	}
	
	@Override
	public int getCount() {
		return 8;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if(v == null){
			viewHolder = new ViewHolder();
			if(position == 0){
				v = inflater.inflate(R.layout.list_home_drawer_user, null);
				viewHolder.img = (ImageView)v.findViewById(R.id.home_drawer_user_img);
				viewHolder.userId = (TextView)v.findViewById(R.id.home_drawer_user_id);
				viewHolder.userLv = (TextView)v.findViewById(R.id.home_drawer_user_level);
			}
			else{
				v = inflater.inflate(R.layout.list_home_drawer_menu, null);
				viewHolder.img = (ImageView)v.findViewById(R.id.home_drawer_menu_img);
			}
			v.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder)v.getTag();
		}

		switch(position){
		case 0:
			FileManager fm = new FileManager(context);
			
			if(fm.isUserImgSet()){
				Bitmap photo = fm.getUserImgFile();
				viewHolder.img.setImageBitmap(photo);
			}
			
			viewHolder.userId.setText(userId);
			viewHolder.userLv.setText("Level. " + userLv);
			break;
		case 1:
			viewHolder.img.setImageResource(R.drawable.sidebar_img_goal);
			break;
		case 2:
			viewHolder.img.setImageResource(R.drawable.sidebar_img_invitefriends);
			break;
		case 3:
			viewHolder.img.setImageResource(R.drawable.sidebar_img_screenlock);
			break;
		case 4:
			viewHolder.img.setImageResource(R.drawable.sidebar_img_notice);
			break;
		case 5:
			viewHolder.img.setImageResource(R.drawable.sidebar_img_setting);
			break;
		case 6: 
			viewHolder.img.setImageResource(R.drawable.sidebar_img_mission);
			break;
		case 7: 
			viewHolder.img.setImageResource(R.drawable.sidebar_img_contact);
			break;
		}
		return v;
	}
	class ViewHolder{
		public ImageView img = null;
		public TextView userId = null;
		public TextView userLv = null;
	}

}
