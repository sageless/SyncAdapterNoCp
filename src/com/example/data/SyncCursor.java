package com.example.data;

import android.database.Cursor;

public interface SyncCursor extends Cursor {
	
	public int getPrimaryKey();
	
	public int getHashCode();

}
