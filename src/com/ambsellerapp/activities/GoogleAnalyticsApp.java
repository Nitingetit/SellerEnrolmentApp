package com.ambsellerapp.activities;

import java.util.HashMap;

import com.AMBSEA.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.PushService;

import android.app.Application;

public class GoogleAnalyticsApp extends Application
{
	// change the following line 
		private static final String PROPERTY_ID = "UA-59358708-1";

		public static int GENERAL_TRACKER = 0;

		public enum TrackerName
		{
			APP_TRACKER, GLOBAL_TRACKER, ECOMMERCE_TRACKER,
		}

		public HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

		public GoogleAnalyticsApp() {
			super();
		}

		public synchronized Tracker getTracker(TrackerName appTracker) {
			if (!mTrackers.containsKey(appTracker)) {
				GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
				Tracker t = (appTracker == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID) : (appTracker == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker) : analytics.newTracker(R.xml.ecommerce_tracker);
				mTrackers.put(appTracker, t);
			}
			return mTrackers.get(appTracker);
		}
		
		@Override
		public void onCreate() {
			
			super.onCreate();
			// Enable Local Datastore.
			Parse.enableLocalDatastore(this);
			 //live account
			Parse.initialize(this, "20CdomFZWqksjEqy9aOS8qQOtqNsv9DTBa8wmN7N", "dnV6tDCki9pAPVlULC3c0Wd7Db9XppaVN4ZMUK8D");
			//nitin account//Parse.initialize(this, "NAEUEYUx9aeuxVihtvRXRmG0XuaaccklThw3MeUw", "T9SLKCA2TOKmSiCLLQoRx8cDmQS3rakn96L1yrfL"); 
			//PushService.setDefaultPushCallback(this, LauncherActivity.class); 
			//Log.i( LOG_TAG, "onCreate()");
			 
			 
		}

		

		@Override
		public void onTerminate() {
			//Log.i( LOG_TAG, "onTerminate()");
			
			super.onTerminate();
		}

}
