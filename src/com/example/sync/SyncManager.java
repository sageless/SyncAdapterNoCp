package com.example.sync;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.util.SparseIntArray;

import com.example.MyApplication;
import com.example.data.DBData;
import com.example.data.MySQLiteHelper;
import com.example.data.SQLiteTableOperation;
import com.example.sync.SyncAdapter.ISyncable;
import com.example.sync.SyncAdapter.JsonFactory;

public class SyncManager {
	
	private static final String TAG = SyncManager.class.getSimpleName();
	
	// this one uses the dbdata wrapper instead of plain cursor/db
	static void synchronize(JSONArray jArray, JsonFactory jsonFactory,
			DBData<? extends Cursor> dbd)
			throws JSONException {

		synchronize(jArray, jsonFactory, dbd.getTable(),
				dbd.getPrimaryKeyColumn(), dbd.getHashColumn(), dbd.getUri());
	}

	
	static void synchronize(JSONArray jArray, JsonFactory jsonFactory,
			String tableName, String primaryKeyColumn, String hashColumn,
			Uri URI) throws JSONException {

		SQLiteDatabase database = MySQLiteHelper.getInstance(
				MyApplication.getContext()).getWritableDatabase();
		
		ArrayList<SQLiteTableOperation> operations = new ArrayList<SQLiteTableOperation>();

		// make mapping of id, hash here from database
		SparseIntArray idMap = getIdMap(database, tableName, primaryKeyColumn, hashColumn);
    	
    	for (int a = 0; a < jArray.length(); a++) {
			JSONObject subObject = new JSONObject(jArray.get(a).toString());
			ISyncable jsonData = jsonFactory.make(subObject);
			
			processAddUpdates(jsonData, idMap, tableName, primaryKeyColumn, operations);
    	}
 
		processDeletes(idMap, tableName, primaryKeyColumn, operations);
    	
		// update the db if any changes
		if (operations.size() > 0) {
			//CategoryData categoryData = new CategoryData();
			Context context = MyApplication.getContext();
			SQLiteTableOperation.executeAll(context, database, operations, URI); 
		}
    }
	
	// generate a map of ["primarykey" => "hashcode"] for tablename
    
	private static SparseIntArray getIdMap(SQLiteDatabase database, String tableName,
			String primaryKeyColumn, String hashColumn) {
		SparseIntArray idMap = new SparseIntArray();
		String[] columns = { primaryKeyColumn, hashColumn };
		Cursor cursor = database.query(tableName, columns, null, null, null,
				null, null);
		while (cursor.moveToNext()) {
			idMap.put(cursor.getInt(0), cursor.getInt(1));
		}
		return idMap;
	}
	
	private static void processAddUpdates(ISyncable jsonData, SparseIntArray idMap,
			String tableName, String primaryKeyColumn,
			ArrayList<SQLiteTableOperation> operations) {
		
		// compute hash and check with id (date)
		// if not found, it is an insert
		// if hash match, nothing
		// if hash not mach, update
		// delete it from the list after found... then any remaining must be deleted
					
		int id = jsonData.getPrimaryKey();
		int dbHash = idMap.get(id);
		if (dbHash != 0) {
			
			//Log.d("sync", "db=" + dbHash + ", json=" + jsonData.hash8());
			// contains key
			if (dbHash != jsonData.hash8()) {
				Log.d(TAG, "update for: " + id);
				ContentValues values = jsonData.getContentValues();
				String where = primaryKeyColumn + "=?";
				String[] whereArgs = new String[] { String.valueOf(id) };
				operations.add(new SQLiteTableOperation.UpdateOperation(tableName, values, where, whereArgs));
			} else {
				// data did not change
				//Log.d(TAG, "no change for: " + id);
			}
						
			// delete it from the list after found... then any remaining must be deleted
			idMap.delete(id);
		} else {
			Log.d(TAG, "insert for: " + id);
			ContentValues values = jsonData.getContentValues();
			operations.add(new SQLiteTableOperation.InsertOperation(tableName, values));
		}
	}
	
	private static void processDeletes(SparseIntArray idMap, String tableName,
			String primaryKeyColumn, ArrayList<SQLiteTableOperation> operations) {
		// whatever is left in db that was not found from server must be deleted
		final int length = idMap.size();
		for (int i = 0; i < length; i++) {
			int id = idMap.keyAt(i);
			Log.d(TAG, "delete for: [" + tableName + ", " + primaryKeyColumn
					+ "] = " + id);
			String where = primaryKeyColumn + "=?";
			String[] whereArgs = new String[] { String.valueOf(id) };
			operations.add(new SQLiteTableOperation.DeleteOperation(tableName,
					where, whereArgs));
		}
	}
}
