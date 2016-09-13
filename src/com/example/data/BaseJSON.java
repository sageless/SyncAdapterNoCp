package com.example.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BaseJSON {

	protected JSONObject subObject;

	public BaseJSON(JSONObject subObject) {
		this.subObject = subObject;
	}

	protected String getString(String key) {
		try {
			return subObject.getString(key);
		} catch (JSONException e) {
			throw new IllegalArgumentException("Unknown Key: " + key, e);
		}
	}
	
	protected String getArrayAsString(String key) {
		try {
			JSONArray array = subObject.getJSONArray(key);
			return array == null ? "" : array.toString();
		} catch (JSONException e) {
			throw new IllegalArgumentException("Unknown Key: " + key, e);
		}
	}

	protected int getInt(String key) {
		try {
			return subObject.getInt(key);
		} catch (JSONException e) {
			throw new IllegalArgumentException("Unknown Key: " + key, e);
		}
	}
	
	protected double getDouble(String key) {
		try {
			return subObject.getDouble(key);
		} catch (JSONException e) {
			throw new IllegalArgumentException("Unknown Key: " + key, e);
		}
	}

}