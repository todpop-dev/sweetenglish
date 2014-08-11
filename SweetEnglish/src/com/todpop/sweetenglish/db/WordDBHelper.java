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
public class WordDBHelper extends SQLiteOpenHelper {
	public WordDBHelper(Context context) {
		super(context, "EngWord.db", null, 1);
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE dic ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "name TEXT, mean TEXT, example_en TEXT, example_ko TEXT, phonetics TEXT, "
				+ "picture INTEGER, image_url TEXT, stage INTEGER, xo TEXT, memorized_date TEXT);");
		db.execSQL("CREATE TABLE mywords ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "group_name TEXT NOT NULL, name TEXT NOT NULL UNIQUE, mean TEXT);");
		db.execSQL("CREATE TABLE mywordtest ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "name TEXT, mean TEXT, xo TEXT);");
		db.execSQL("CREATE TABLE word_groups ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "name TEXT NOT NULL UNIQUE);");	
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS dic");
		db.execSQL("DROP TABLE IF EXISTS mywords");
		db.execSQL("DROP TABLE IF EXISTS mywordtest");
		db.execSQL("DROP TABLE IF EXISTS word_groups");
		onCreate(db);
	}
}