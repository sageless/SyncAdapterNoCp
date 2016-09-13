package com.example.sync;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;

import com.example.data.BlingItemData;
import com.example.data.BlingItemJSON;

/**
 * Handle the transfer of data between a server and an app, using the Android
 * sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

	private final static String TAG = "SyncAdapter";

	// Global variables
	// Define a variable to contain a content resolver instance
	ContentResolver mContentResolver;

	/**
	 * Set up the sync adapter
	 */
	public SyncAdapter(Context context, boolean autoInitialize) {
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
	public SyncAdapter(Context context, boolean autoInitialize,
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
			syncBlingItem();

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

	public interface ISyncable {
		public int getPrimaryKey();

		public int hash8();

		public ContentValues getContentValues();
	};

	static interface JsonFactory {
		ISyncable make(JSONObject job);
	};

	private void syncBlingItem() throws JSONException, NetworkException {

		Log.i(TAG, "syncBlingItem()");

		/**
		 * would call network here and get new data
		 */
		FakeNetwork fn = new FakeNetwork();
		String result = fn.send();
		if (result != null) {
			JSONObject jObject = new JSONObject(result);
			JSONArray jArray = jObject.getJSONArray("BlingItemResponse");
			diffBlingItem(jArray);
		}
	}

	private void diffBlingItem(JSONArray jArray) throws JSONException {

		JsonFactory factory = new JsonFactory() {
			@Override
			public ISyncable make(JSONObject job) {
				return new BlingItemJSON(job);
			}
		};

		BlingItemData bd = new BlingItemData();
		SyncManager.synchronize(jArray, factory, bd);
	}

	/***
	 * Fake out network call and as an example return changing data for one
	 * field.
	 * (can be modified to have other fields change also)
	 */
	static class FakeNetwork {
		private Random r = new Random();

		public String send() throws JSONException, NetworkException {

			JSONArray ja = new JSONArray();
			ja.put(makeEntry(1, random(), "First"));
			ja.put(makeEntry(2, random(), "Second"));
			ja.put(makeEntry(3, random(), "Third"));
			ja.put(makeEntry(4, random(), "Forth"));

			JSONObject mainObj = new JSONObject();
			mainObj.put("BlingItemResponse", ja);

			return mainObj.toString();
		}

		private int random() {
			int max = 5;
			int min = 1;
			return r.nextInt(max - min + 1) + min;
		}

		private JSONObject makeEntry(int id, int myInt, String myName)
				throws JSONException {
			JSONObject jo = new JSONObject();
			jo.put(BlingItemJSON.ID, id);
			jo.put(BlingItemJSON.MY_INT, myInt);
			jo.put(BlingItemJSON.MY_NAME, myName);
			return jo;
		}
	}
	
	// a fake exception that our fake network call can throw
	@SuppressWarnings("serial")
	static class NetworkException extends Exception {
		public NetworkException(Throwable t) {
			super(t);
		}
	}
}
