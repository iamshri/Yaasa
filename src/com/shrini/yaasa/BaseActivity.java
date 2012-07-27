package com.shrini.yaasa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseActivity extends Activity {
	YaasaApplication yaasa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		yaasa = (YaasaApplication) getApplication();
	}

	// Menu Stuff
	// Called every time menu item is selected
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class));
			break;
		case R.id.itemToggleService:
			if (yaasa.isServiceRunning()) {
				stopService(new Intent(this, UpdateService.class));
			} else {
				if (yaasa.isOnline()) {
					startService(new Intent(this, UpdateService.class));
				} else {
					Toast.makeText(this, R.string.toastNoNetwork, Toast.LENGTH_LONG)
							.show();
				}
			}
			break;
		case R.id.itemPurge:
			yaasa.statusData.delete();
			Toast.makeText(this, R.string.toastPurge, Toast.LENGTH_LONG).show();
			break;
		case R.id.itemTimeline:
			if (yaasa.isOnline()) {
				startActivity(new Intent(this, TimelineActivity.class)
						.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
								| Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			} else {
				Toast.makeText(this, R.string.toastNoNetwork, Toast.LENGTH_LONG)
						.show();
			}
			break;
		case R.id.itemstatus:
			startActivity(new Intent(this, StatusActivity.class)
					.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
							| Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));

			break;
		}
		return true;
	}

	// Called every time menu is created
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	// Called every time menu is opened
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		MenuItem toggleItem = menu.findItem(R.id.itemToggleService);
		if (yaasa.isServiceRunning()) {
			toggleItem.setTitle(R.string.menuTitleServiceStop);
			toggleItem.setIcon(android.R.drawable.ic_media_pause);
		} else {
			toggleItem.setTitle(R.string.menutitleStartService);
			toggleItem.setIcon(android.R.drawable.ic_media_play);
		}
		return true;
	}

}
