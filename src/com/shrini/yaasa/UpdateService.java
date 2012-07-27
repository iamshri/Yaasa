package com.shrini.yaasa;

import java.util.List;

import winterwell.jtwitter.Status;
import winterwell.jtwitter.Twitter;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class UpdateService extends Service {

	private static final String TAG = "Yaasa/"
			+ UpdateService.class.getSimpleName();
	static final String ACTION_NEW_STATUS = "yaasa.NEWSTATUS";
	private static final int NOTIFICATION_ID = 1;
	private Updater updater;
	YaasaApplication yaasa;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		updater = new Updater();
		yaasa = ((YaasaApplication) getApplication());
		Log.d(TAG, "OnCreate");
	}

	@Override
	public synchronized void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (!yaasa.isServiceRunning() && yaasa.isOnline()) {
			updater.start();
			Log.d(TAG, "Service Started");
		} else {
			 Toast.makeText(this, R.string.toastServiceNotStarted,
			 Toast.LENGTH_LONG);
			// createNotification();
			 this.stopSelf();
		}

		Log.d(TAG, "OnStart");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (yaasa.isServiceRunning()) {
			updater.interrupt();
			updater = null;
		}
		Log.d(TAG, "OnDestroy");
	}

	private void createNotification() {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.ic_launcher_yaasa;
		CharSequence tickerText = getResources().getString(R.string.notificationTickerTest);
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		Context context = getApplicationContext();
		CharSequence contentTitle = getResources().getString(R.string.notificationTitle); 
		CharSequence contentText =  getResources().getString(R.string.notificationContent);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		
		mNotificationManager.notify(NOTIFICATION_ID, notification);

	}

	class Updater extends Thread {
		private static final long DELAY = 30000;

		public Updater() {
			super("Updater");
		}

		@Override
		public void run() {
			boolean hasNewStatuses = false;
			yaasa.setServiceRunning(true);
			while (yaasa.isServiceRunning()) {
				try {
					Log.d(TAG, "Updater Running");

					Twitter twitter = yaasa.getTwitter();
					List<Status> statuses = twitter.getHomeTimeline();
					for (Status status : statuses) {
						if (yaasa.statusData.insert(status) >= 0) {
							hasNewStatuses = true;
						}
						Log.d(TAG, String.format("%s: %s", status.user.name,
								status.text));
					}

					// Send Broadcast if new Statuses are added to time line
					if (hasNewStatuses) {
						sendBroadcast(new Intent(ACTION_NEW_STATUS));
					}

					// Block until delay
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
					yaasa.setServiceRunning(false);
				}// Interrupted
			}// while
		}

	}

}
