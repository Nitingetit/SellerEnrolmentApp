package com.ambsellerapp.backgroundservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReciever extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		//                    sms.sendTextMessage(phoneNumberReciver, null, message, null, null);
		// Show the toast  like in above screen shot
		Toast.makeText(context, "Alarm Triggered and SMS Sent", Toast.LENGTH_LONG).show();
	}

}
