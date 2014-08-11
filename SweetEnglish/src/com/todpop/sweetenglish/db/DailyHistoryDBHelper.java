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
public class DailyHistoryDBHelper extends SQLiteOpenHelper {
	public DailyHistoryDBHelper(Context context) {
		super(context, "DailyHistory.db", null, 1);
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE history ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "name TEXT NOT NULL, mean TEXT NOT NULL, xo TEXT NOT NULL, day_of_week INT NOT NULL);");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS history");
		onCreate(db);
	}
}