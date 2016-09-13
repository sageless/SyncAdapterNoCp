package com.example.data;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public abstract class SQLiteTableOperation {
	
	protected String table;
	
	public SQLiteTableOperation(String table) {
		this.table = table;
	}
	
	public abstract void execute(SQLiteDatabase db);
	
	public static void executeAll(Context context, SQLiteDatabase database,
			List<SQLiteTableOperation> operations, Uri URI) {
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
	
	public static class InsertOperation extends SQLiteTableOperation {
		private ContentValues values;
		
		public InsertOperation(String table, ContentValues values) {
			super(table);
			this.values = values;
		}
		
		public void execute(SQLiteDatabase db) {
			db.insert(table, null, values);
		}
	}
	
	public static class UpdateOperation extends SQLiteTableOperation {
		private ContentValues values;
		private String where;
		private String[] whereArgs;
		
		public UpdateOperation(String table, ContentValues values,
				String where, String[] whereArgs) {
			super(table);
			this.values = values;
			this.where = where;
			this.whereArgs = whereArgs;
		}
		
		public void execute(SQLiteDatabase db) {
			db.update(table, values, where, whereArgs);
		}
	}
	
	public static class DeleteOperation extends SQLiteTableOperation {
		private String where;
		private String[] whereArgs;

		public DeleteOperation(String table, String where,
				String[] whereArgs) {
			super(table);
			this.where = where;
			this.whereArgs = whereArgs;
		}
		
		public void execute(SQLiteDatabase db) {
			db.delete(table, where, whereArgs);
		}
	}
}
