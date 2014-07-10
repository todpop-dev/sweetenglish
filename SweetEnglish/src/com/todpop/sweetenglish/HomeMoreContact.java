package com.todpop.sweetenglish;

import com.todpop.api.TypefaceActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class HomeMoreContact extends TypefaceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_contact);

	}

	public void onClickBack(View view)
	{
		finish();
	}
}



