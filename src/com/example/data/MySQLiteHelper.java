package com.example.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {
	
	private static MySQLiteHelper sInstance;
	private static final String DATABASE_NAME = "example.db";
	private static final int DATABASE_VERSION = 1;

	// bling item
	public static final String TABLE_BLING_ITEM = "BlingItem";
	public static final String COLUMN_BLING_ITEM_ID = "_id";
	public static final String COLUMN_BLING_ITEM_MY_INT = "myint";
	public static final String COLUMN_BLING_ITEM_MY_NAME = "myname";
	public static final String COLUMN_BLING_ITEM_HASH = "hash";

	private static final String DATABASE_CREATE_TABLE_BLING_ITEM = "create table "
			+ TABLE_BLING_ITEM + "(" + COLUMN_BLING_ITEM_ID
			+ " integer primary key autoincrement,"
			+ COLUMN_BLING_ITEM_MY_INT + " integer, "
			+ COLUMN_BLING_ITEM_MY_NAME + " text, "
			+ COLUMN_BLING_ITEM_HASH + " integer);";
	
	public static synchronized MySQLiteHelper getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new MySQLiteHelper(context.getApplicationContext());
		}
		return sInstance;
	}

	private MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_TABLE_BLING_ITEM);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLING_ITEM);
		onCreate(db);
	}
}
