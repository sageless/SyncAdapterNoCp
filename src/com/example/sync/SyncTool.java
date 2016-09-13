package com.example.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.example.MyApplication;

public class SyncTool {

	public static final String BLING = StubProvider.AUTHORITY;
	//public static final String MENU = StubProviderMenu.AUTHORITY;

	// stick an account in here since it is only a dummy account
	private static Account mAccount;

	private static Account getAccount() {
		if (mAccount == null) {
			Context context = MyApplication.getContext();
			mAccount = CreateSyncAccount(context);
		}
		return mAccount;
	}

	/**
	 * Create a new dummy account for the sync adapter
	 * 
	 * @param context
	 *            The application context
	 */
	private static Account CreateSyncAccount(Context context) {
		// Create the account type and default account
		Account newAccount = new Account(Authenticator.ACCOUNT,
				Authenticator.ACCOUNT_TYPE);
		// Get an instance of the Android account manager
		AccountManager accountManager = (AccountManager) context
				.getSystemService(Context.ACCOUNT_SERVICE);
		/*
		 * Add the account and account type, no password or user data If
		 * successful, return the Account object, otherwise report an error.
		 */
		if (accountManager.addAccountExplicitly(newAccount, null, null)) {
			/*
			 * If you don't set android:syncable="true" in in your <provider>
			 * element in the manifest, then call context.setIsSyncable(account,
			 * AUTHORITY, 1) here.
			 */
		} else {
			/*
			 * The account exists or some other error occurred. Log this, report
			 * it, or handle it internally.
			 */
		}
		return newAccount;
	}

	public static void addPeriodicSync(String provider, long syncInterval) {
		addPeriodicSync(getAccount(), provider, syncInterval);
	}

	private static void addPeriodicSync(Account account, String provider,
			long syncInterval) {

    	ContentResolver.setIsSyncable(account, provider, 1);
		ContentResolver.setSyncAutomatically(account, provider, true);

		ContentResolver.addPeriodicSync(account, provider,
				Bundle.EMPTY, // settingsBundle
				syncInterval);
	}

	public static void removePeriodicSync(String provider) {
		removePeriodicSync(getAccount(), provider);
	}

	private static void removePeriodicSync(Account account, String provider) {
    	ContentResolver.setIsSyncable(account, provider, 0);
		ContentResolver.removePeriodicSync(account, provider,
				Bundle.EMPTY);
	}

	public static void forceSync(String provider) {
		forceSync(getAccount(), provider);
	}

	private static void forceSync(Account account, String provider) {

		// seem to need this sometimes?
    	ContentResolver.setIsSyncable(account, provider, 1);

		// Pass the settings flags by inserting them in a bundle
		Bundle settingsBundle = new Bundle();
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

		/*
		 * Request the sync for the default account, authority, and manual sync
		 * settings
		 */
		ContentResolver.requestSync(account, provider, settingsBundle);
	}
}
