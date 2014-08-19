package com.todpop.sweetenglish.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Copyright 2014 TODPOP Corp. All rights reserved.
 * 
 * @author steven@todpop.co.kr
 * @version 1.0
 * 
 */
// ------- Database Operation ------------------
public class UsageTimeDBHelper extends SQLiteOpenHelper {
	public UsageTimeDBHelper(Context context) {
		super(context, "Usage.db", null, 1);
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE daily_usage (date INTEGER PRIMARY KEY NOT NULL UNIQUE, usage_time UNSIGNED INTEGER, attend INTEGER);");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS daily_usage");
		onCreate(db);
	}
}