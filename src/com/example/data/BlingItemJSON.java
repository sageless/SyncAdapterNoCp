package com.example.data;

import org.json.JSONObject;

import com.example.sync.SyncAdapter.ISyncable;

import android.content.ContentValues;


public class BlingItemJSON extends BaseJSON implements BlingItemGet, ISyncable {

	public static final String ID = "id";
	public static final String MY_INT = "MyInt";
	public static final String MY_NAME = "MyName";
	
	public BlingItemJSON(JSONObject subObject) {
		super(subObject);
	}
	
	@Override
	public int getId() {
		return getInt(ID);
	}

	@Override
	public int getMyInt() {
		return getInt(MY_INT);
	}

	@Override
	public String getMyName() {
		return getString(MY_NAME);
	}
	
	public ContentValues getContentValues() {

		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_BLING_ITEM_ID, getId());
		values.put(MySQLiteHelper.COLUMN_BLING_ITEM_MY_INT, getMyInt());
		values.put(MySQLiteHelper.COLUMN_BLING_ITEM_MY_NAME, getMyName());
		values.put(MySQLiteHelper.COLUMN_BLING_ITEM_HASH, hash8());
		return values;
	}
	
	public static class BlingItemUtils {
		public static int hashCode(BlingItemGet di) {
			StringBuilder b = new StringBuilder();
			b.append(di.getId())
			.append(di.getMyInt())
			.append(di.getMyName());
			return HashUtil.hash8(b.toString());
		}
	}
	
	// ISyncable

	@Override
	public int getPrimaryKey() {
		return getId();
	}

	@Override
	public int hash8() {
		return BlingItemUtils.hashCode(this);
	}

}
