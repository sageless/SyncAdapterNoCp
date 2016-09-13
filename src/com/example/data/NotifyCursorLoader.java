package com.example.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public abstract class NotifyCursorLoader extends SimpleCursorLoader {

	final ForceLoadContentObserver observer;

	public NotifyCursorLoader(final Context context) {
		super(context);
		this.observer = new ForceLoadContentObserver();
	}

	@Override
	public Cursor loadInBackground() {
		final Cursor c = fillCursor();
		if (c != null) {
			// Ensure the cursor window is filled
			c.getCount();
			// this is to force a reload when the content change
			c.registerContentObserver(this.observer);
			// this make sure this loader will be notified when
			// a notifyChange is called
			c.setNotificationUri(getContext().getContentResolver(),
					getNotificationUri());
		}
		return c;
	}

	protected abstract Cursor fillCursor();

	protected abstract Uri getNotificationUri();
}
