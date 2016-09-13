package com.example.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.MyApplication;


public class BlingItemData extends DBData<BlingItemCursor>  {
	
	public final static String table = MySQLiteHelper.TABLE_BLING_ITEM;
	
	public static final Uri URI = Uri.parse("sqlite://com.example/" + table);
	
	public BlingItemData() {
		this(MyApplication.getContext());
	}
	
	public BlingItemData(Context context) {
		this(context, null);
	}
	
	public BlingItemData(Context context, String[] columns) {
		super(context, getDatabase(), table, columns,
				MySQLiteHelper.COLUMN_BLING_ITEM_ID,
				MySQLiteHelper.COLUMN_BLING_ITEM_HASH, URI);
	}

	@Override
	public BlingItemCursor newInstance(Cursor cursor) {
		return new BlingItemCursor(cursor, BlingItemData.this);
	}
	
	public BlingItemCursor selectByMyInt(int myInt) {
		String where = MySQLiteHelper.COLUMN_BLING_ITEM_MY_INT + "= ?";
		String[] whereArgs = { String.valueOf(myInt) };
		BlingItemCursor cursor = select(where, whereArgs, null);
		cursor.moveToFirst();
		return cursor;
	}
	
	private static SQLiteDatabase getDatabase() {
		return MySQLiteHelper.getInstance(MyApplication.getContext())
				.getWritableDatabase();
	}
}
