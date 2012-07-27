package com.shrini.yaasa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class TimelineActivity extends BaseActivity {
	ListView listStatuses;
	Cursor cursor;
	SimpleCursorAdapter adapter;
	TimelineReceiver timelineReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.timeline);
		listStatuses = (ListView) findViewById(R.id.statuses);
		
		// put data and pop up list
		this.setupList();

		if (yaasa.prefs.getString("username", null) == null) {
			startActivity(new Intent(this, PrefsActivity.class));
			Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG);
			return;
		}

	}

	private void setupList() {
		// Data
		cursor = yaasa.statusData.query();
		startManagingCursor(cursor);

		// Set List adapter
		String[] from = { StatusData.C_USER, StatusData.C_TEXT,
				StatusData.C_CREATED_AT };
		int[] to = { R.id.textUser, R.id.textText, R.id.textCreatedAt };
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);
		adapter.setViewBinder(VIEW_BINDER);
		listStatuses.setAdapter(adapter);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Register the TimelineReceiver
		if (timelineReceiver == null) {
			timelineReceiver = new TimelineReceiver();
		}
		registerReceiver(timelineReceiver, new IntentFilter(
				UpdateService.ACTION_NEW_STATUS));
	}

	@Override
	protected void onPause() {
		// Unregister the TimelineReceiver
		unregisterReceiver(timelineReceiver);
		super.onPause();
	}

	static final ViewBinder VIEW_BINDER = new ViewBinder() {

		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (cursor.getColumnIndex(StatusData.C_CREATED_AT) != columnIndex) {
				// Not processing any other columns
				return false;
			} else {

				long timeStamp = cursor.getLong(columnIndex);
				CharSequence relativeTime = DateUtils
						.getRelativeTimeSpanString(timeStamp);
				((TextView) view).setText(relativeTime);
				return true;
			}

		}

	};

	// TimelineReceiver, wakes up when there is a new status

	class TimelineReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			setupList();
		}

	}
}
