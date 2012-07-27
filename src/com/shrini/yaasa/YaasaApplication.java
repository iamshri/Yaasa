package com.shrini.yaasa;

import winterwell.jtwitter.Twitter;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

public class YaasaApplication extends Application implements
		OnSharedPreferenceChangeListener {
	SharedPreferences prefs;
	Twitter twitter;
	StatusData statusData;
	private boolean isServiceRunning = false;

	
	public boolean isServiceRunning() {
		return isServiceRunning;
	}

	public void setServiceRunning(boolean isRunning) {
		this.isServiceRunning = isRunning;
	}
	
	public boolean isOnline() {
		 ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		 return (cm.getActiveNetworkInfo()!= null);

	}
	@Override
	public void onCreate() {
		super.onCreate();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);

		statusData = new StatusData(this);
	}

	@Override
	public void onTerminate() {
		statusData.close();
		super.onTerminate();
	}

	// Lazy initialization
	public synchronized Twitter getTwitter() {
		if (twitter == null) {
			String username = prefs.getString("username", "");
			String password = prefs.getString("password", "");
			String server = prefs.getString("serverURL", "");
			twitter = new Twitter(username, password);
			twitter.setAPIRootUrl(server);
		}
		return twitter;
	}

	// Preference change listener
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		// Invalidate
		twitter = null;
	}

}
