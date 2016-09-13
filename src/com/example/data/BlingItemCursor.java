package com.example.data;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.data.DBData.ColumnIndexCache;

public class BlingItemCursor extends CursorWrapper implements BlingItemGet, SyncCursor {

	private static ColumnIndexCache sCache = new ColumnIndexCache();

	private ColumnIndexCache cache;

	public BlingItemCursor(Cursor cursor, DBData<BlingItemCursor> ddata) {
		this(cursor, ddata.getColumns());
	}

	public BlingItemCursor(Cursor cursor) {
		this(cursor, cursor.getColumnNames());
	}

	private BlingItemCursor(Cursor cursor, String[] columns) {
		super(cursor);
		this.cache = columns == null ? sCache : new ColumnIndexCache();
	}
	
	
	@Override
	public int getId() {
		return getInt(cache.getColumnIndex(this,
				MySQLiteHelper.COLUMN_BLING_ITEM_ID));
	}
	
	
	@Override
	public int getMyInt() {
		return getInt(cache.getColumnIndex(this,
				MySQLiteHelper.COLUMN_BLING_ITEM_MY_INT));
	}
	
	@Override
	public String getMyName() {
		return getString(cache.getColumnIndex(this,
				MySQLiteHelper.COLUMN_BLING_ITEM_MY_NAME));
	}

	// ISyncCursor
	
	@Override
	public int getPrimaryKey() {
		return getId();
	}

	@Override
	public int getHashCode() {
		return getInt(cache.getColumnIndex(this,
				MySQLiteHelper.COLUMN_BLING_ITEM_HASH));
	}
	
}
