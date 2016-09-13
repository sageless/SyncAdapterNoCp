package com.example.data;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.util.Log;

public abstract class DBData<T extends SyncCursor> {

	private static final String TAG = DBData.class.getName();
	
	protected SQLiteDatabase database;
	
	protected Context context;
	
	private String[] columns = null;
	
	private String primaryKeyColumn;
	
	private String hashColumn;
	
	private String table;
	
	private Uri URI;
	
	
	public DBData(Context context, SQLiteDatabase database, String table,
			String[] columns, String primaryKeyColumn,
			String hashColumn, Uri uri ) {
		this.context = context;
		this.database = database;
		this.table = table;
		this.columns = columns;
		this.primaryKeyColumn = primaryKeyColumn;
		this.hashColumn = hashColumn;
		this.URI = uri;
	}
	
	public String[] getColumns() {
		return columns;
	}
	
	public void setColumns(String[] columns) {
		this.columns = columns;
	}
	
	public String getTable() {
		return table;
	}
	
	public Uri getUri() {
		return URI;
	}
	
	public String getPrimaryKeyColumn() {
		return primaryKeyColumn;
	}
	
	public String getHashColumn() {
		return hashColumn;
	}
	
	public abstract T newInstance(Cursor cursor);
	
	public boolean isEmpty() {
		boolean empty = true;
		Cursor cursor = null;
		try {
			cursor = database.rawQuery("SELECT COUNT(*) FROM "
					+ table, null);
			cursor.moveToFirst();
			empty = (cursor.getInt(0) == 0);
			cursor.close();
			cursor = null;
		} finally {
			closeSilent(cursor);
		}
		return empty;
	}
	
	
	public T selectAll() {
		return select(null, null, null);
	}
	
	public T select(String where, String[] whereArgs, String orderBy) {
		Cursor cursor = database.query(table, columns, where, whereArgs, null,
				null, orderBy);
		return newInstance(cursor);
	}
	
	public long insert(ContentValues dataToInsert) {
		long val = database.insert(table, null, dataToInsert);
		if (val > -1) {
			context.getContentResolver().notifyChange(URI, null);
		}
		return val;
	}
	
	public int update(ContentValues dataToInsert, String where,
			String[] whereArgs) {
		int val = database.update(table, dataToInsert, where, whereArgs);
		if (val > 0) {
			context.getContentResolver().notifyChange(URI, null);
		}
		return val;
	}
	
	public int delete(String where, String[] whereArgs) {
		int val = database.delete(table, where, whereArgs);
		context.getContentResolver().notifyChange(URI, null);
		return val;
	}
	
	public void deleteAll() {
		delete(null, null);
	}
	
	public interface Callback<T> {
		//if callback returns false, processing stops
		boolean onRow( int row, T cursor);
	}
	
	
	public boolean forEach(Callback<T> callback) {
		T cursor = null;
		boolean result = true;
		try {
			cursor = select(null, null, null);
			if (cursor != null) {
				for (int i = 0; cursor.moveToNext() && result; i++) {
					result = callback.onRow(i, cursor);
				}
				cursor.close();
			}
		} finally {
			closeSilent(cursor);
		}
		return result;
	}
	
	
	public void execute(Uri URI, List<SQLiteTableOperation> operations) {
		database.beginTransaction();
		try {
			for (SQLiteTableOperation op : operations) {
				op.execute(database);
			}
			database.setTransactionSuccessful();
			context.getContentResolver().notifyChange(URI, null);
		} finally {
			database.endTransaction();
		}
	}
	
	// static
	
	public static final void closeSilent(Cursor c) {
		try {
			if (c != null) {
				c.close();
			}
		} catch (SQLiteException e) {
			// ignored
			Log.e(TAG, "Error closing cursor", e);
		}
	}
	
	
	public static class ColumnIndexCache {
		private ArrayMap<String, Integer> mMap = new ArrayMap<String,Integer>();

		public int getColumnIndex(Cursor cursor, String columnName) {
			Integer idx = mMap.get(columnName);
			if (idx == null) {
				idx = cursor.getColumnIndexOrThrow(columnName);
				mMap.put(columnName, idx);
			}
			return idx;
		}

		public void clear() {
			mMap.clear();
		}
	}

}
