package com.todpop.sweetenglish;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class HomeActivity extends Activity{
	DrawerLayout drawerLayout;
	Button drawerToggle;
	ListView drawerMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_home);
		
		drawerLayout = (DrawerLayout)findViewById(R.id.home_drawer_layout);
		drawerToggle = (Button)findViewById(R.id.home_drawer_toggle);
		drawerMenu = (ListView)findViewById(R.id.home_drawer_menu);
	}
	
	public void onClickDrawer(View v){
		
	}
}
