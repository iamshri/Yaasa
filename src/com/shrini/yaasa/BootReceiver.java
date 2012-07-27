package com.shrini.yaasa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	Context context;
	@Override
	public void onReceive(Context context, Intent arg1) {
		this.context = context;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if(prefs.getBoolean("startAtBoot", false)){
			context.startService(new Intent(context, UpdateService.class));
			Log.d("BootReceiver", "OnstartAtBoot");
		}
		Log.d("BootReceiver", "onReceive");
	}

	


}
