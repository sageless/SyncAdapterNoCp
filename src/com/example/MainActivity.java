package com.example;

import com.example.sync.SyncTool;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class MainActivity extends FragmentActivity implements
		BlingFragment.OnBlingSelectedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bling);
		showBlingItems();
	}
	
	
	/**
	 * for this example we only sync when this screen is visible
	 */
		
	@Override
	protected void onResume() {
		super.onResume();
		SyncTool.forceSync(SyncTool.BLING);
		SyncTool.addPeriodicSync(SyncTool.BLING, 10L);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		SyncTool.removePeriodicSync(SyncTool.BLING);
	}

	private void showBlingItems() {
		BlingFragment blingFragment = (BlingFragment) getSupportFragmentManager()
				.findFragmentById(R.id.layoutBling);
		if (blingFragment == null) {
			blingFragment = new BlingFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.layoutBling, blingFragment).commit();
		}
	}

	//not used 
	@SuppressWarnings("unused")
	private void removeBlingItemFragment() {
		BlingFragment blingFragment = (BlingFragment) getSupportFragmentManager()
				.findFragmentById(R.id.layoutBling);
		if (blingFragment != null) {
			getSupportFragmentManager().beginTransaction()
					.remove(blingFragment).commit();
		}
	}

	@Override
	public void onBlingSelected(int blingId) {
		Log.d("Bling", "Got a: " + blingId);
	}

}
