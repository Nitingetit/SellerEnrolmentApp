package com.ambsellerapp.backgroundservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SchedulerEventReceiver extends BroadcastReceiver
{
	private static final String APP_TAG = "com.hascode.android";

	@Override
	public void onReceive(final Context ctx, final Intent intent) 
	{
//		Log.d(APP_TAG, "SchedulerEventReceiver.onReceive() called");
//		Toast.makeText(ctx, "Inside the SchedulerEventReceiver", Toast.LENGTH_LONG).show();
		Intent eventService = new Intent(ctx, SchedulerEventService.class);
		ctx.startService(eventService);
	}

}
