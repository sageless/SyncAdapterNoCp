package com.example.sync;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.data.BlingItemJSON;
import com.example.data.MySQLiteHelper;
import com.example.sync.SyncAdapter.FakeNetwork;
import com.example.sync.SyncAdapter.ISyncable;
import com.example.sync.SyncAdapter.JsonFactory;
import com.example.sync.SyncAdapter.NetworkException;

/**
 * Handle the transfer of data between a server and an app, using the Android
 * sync adapter framework.
 */
public class SyncAdapterMenu extends AbstractThreadedSyncAdapter {

	private final static String TAG = "SyncAdapterMenu";

	// Global variables
	// Define a variable to contain a content resolver instance
	ContentResolver mContentResolver;

	/**
	 * Set up the sync adapter
	 */
	public SyncAdapterMenu(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		/*
		 * If your app uses a content resolver, get an instance of it from the
		 * incoming Context
		 */
		mContentResolver = context.getContentResolver();
	}

	/**
	 * Set up the sync adapter. This form of the constructor maintains
	 * compatibility with Android 3.0 and later platform versions
	 */
	public SyncAdapterMenu(Context context, boolean autoInitialize,
			boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
		/*
		 * If your app uses a content resolver, get an instance of it from the
		 * incoming Context
		 */
		mContentResolver = context.getContentResolver();
	}

	/*
	 * Specify the code you want to run in the sync adapter. The entire sync
	 * adapter runs in a background thread, so you don't have to set up your own
	 * background processing.
	 */
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {

		Log.d(TAG, "onPerformSync for account[" + account.name + "]");
		/*
		 * Put the data transfer code here.
		 */

		// check if we are a manual sync or not
		boolean manualSync = extras
				.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL);

		Log.d(TAG, "MANUAL SYNC = " + manualSync);
		if (manualSync == false) {
			// don't do the periodic sync if we just did a manual sync
			// n second ago...
			long timeNow = System.currentTimeMillis();
			if (timeNow - lastSyncTime < 2000) {
				Log.d(TAG, "Not Syncing");
				return;
			}
		}

		try {
			// can pass syncResult in here to set insert/update/delete counts
			// but since we are not showing info on the accounts setting screen
			// it is not needed
			syncFluffItem();

			lastSyncTime = System.currentTimeMillis();

		} catch (JSONException e) {
			syncResult.stats.numParseExceptions++;
			Log.e(TAG, "Parse error", e);
		} catch (NetworkException e) {
			syncResult.stats.numIoExceptions++;
			Log.e(TAG, "Network error", e);
		} catch (SQLiteException e) {
			// if there are database transactions in another process, can check
			// for SQLiteDatabaseLockedException here and sleep() and retry
			syncResult.databaseError = true;
			Log.e(TAG, "Database error", e);
		}
	}

	static long lastSyncTime;

	private void syncFluffItem() throws JSONException, NetworkException {

		Log.i(TAG, "syncFluffItem()");

		/**
		 * would call network here and get new data
		 */
		FakeNetwork fn = new FakeNetwork();
		String result = fn.send();
		if (result != null) {
			JSONObject jObject = new JSONObject(result);
			JSONArray jArray = jObject.getJSONArray("BlingItemResponse");
			diffFluffItem(jArray);
		}
	}

	private void diffFluffItem(JSONArray jArray) throws JSONException {

		JsonFactory factory = new JsonFactory() {
			@Override
			public ISyncable make(JSONObject job) {
				return new BlingItemJSON(job);
			}
		};

		String table = MySQLiteHelper.TABLE_BLING_ITEM;
		Uri URI = Uri.parse("sqlite://com.example/" + table);
		SyncManager.synchronize(jArray, factory, table, MySQLiteHelper.COLUMN_BLING_ITEM_ID, 
				MySQLiteHelper.COLUMN_BLING_ITEM_HASH, URI);
	}
}
