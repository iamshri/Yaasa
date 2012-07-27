package com.shrini.yaasa;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StatusActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "Yaasa/"
			+ StatusActivity.class.getSimpleName();
	private static final int DIALOG_ID = 1;
	EditText editStatus;
	Button buttonUpdate;
	ProgressDialog postingDialog;
	
	String statusMessage = null;
	UpdateStatusTask postTask = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status);
		// Setting Trace: ex: Useful in monitoring of n/w traffic
		// Debug.startMethodTracing("yaasa.trace");
		editStatus = (EditText) findViewById(R.id.editStatus);
		buttonUpdate = (Button) findViewById(R.id.buttonUpdate);

		buttonUpdate.setOnClickListener(this);

	}

	@Override
	protected void onStop() {
		// Debug.stopMethodTracing();
		super.onStop();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		postTask.dettach();
		return postTask;
	}

	private void taskCompleted(Boolean result) {
		if (result) {
			dismissDialog(DIALOG_ID);
			Toast.makeText(this, R.string.toastStatusUpdated, Toast.LENGTH_LONG)
					.show();

		} else {
			dismissDialog(DIALOG_ID);
			Toast.makeText(this, R.string.toastStatusUpdateFailed,
					Toast.LENGTH_LONG).show();

		}

	}

	private void updateProgress(int progress) {

	}

	// Button Stuff
	// @Override
	public void onClick(View v) {
		statusMessage = editStatus.getText().toString();
		Log.d(TAG, "Status: " + statusMessage);
		postTask = (UpdateStatusTask) getLastNonConfigurationInstance();
		showDialog(DIALOG_ID);
		if (postTask == null) {
			postTask = new UpdateStatusTask(this);
			postTask.execute(statusMessage);
		} else {
			postTask.attach(this);
			updateProgress(postTask.getProgress());
			if (postTask.getProgress() >= 100) {
				// taskCompleted();
			}
		}

	}

	// progress dialog code
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch (id) {
		case DIALOG_ID: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(StatusActivity.this
					.getString(R.string.msgPleasewhilePosting));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			return dialog;
		}
		}
		return null;
	}


	// Posting task Stuff
	static class UpdateStatusTask extends AsyncTask<String, String, Boolean> {
		StatusActivity activity = null;
		int progress = 0;

		public UpdateStatusTask(StatusActivity activity) {
			super();
			this.activity = activity;
		}

		@Override
		protected Boolean doInBackground(String... status) {
			Boolean result = false;
			try {
				YaasaApplication app = ((YaasaApplication) activity
						.getApplication());
				Twitter twitter;
				twitter = app.getTwitter();
				twitter.setStatus(status[0]);
				progress = 100;
				result = true;
			} catch (TwitterException e) {
				result = false;
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (activity == null) {

			} else {
				activity.taskCompleted(result);
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		void attach(StatusActivity activity) {
			this.activity = activity;
		}

		void dettach() {
			this.activity = null;
		}

		public int getProgress() {
			return progress;
		}

	}
}