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
public class AnalysisDBHelper extends SQLiteOpenHelper {
	public AnalysisDBHelper(Context context) {
		super(context, "Analysis.db", null, 1);
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE daily_achieve (date INTEGER PRIMARY KEY NOT NULL UNIQUE, achieve INTEGER);");
		db.execSQL("CREATE TABLE weekly_achieve (yymmweek INTEGER PRIMARY KEY NOT NULL UNIQUE, achieve INTEGER);");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS daily_achieve");
		db.execSQL("DROP TABLE IF EXISTS weekly_achieve");
		onCreate(db);
	}
}