package com.ambsellerapp.backgroundservices;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.text.format.DateUtils;
import android.util.Log;

import com.AMBSEA.BuildConfig;
import com.ambsellerapp.fragments.ButtonEnable;
import com.ambsellerapp.utilies.LocationUtils;
import com.ambsellerapp.utilies.NetworkUtils;
import com.ambsellerapp.utilies.Utility;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;


/**
 * @author sachin.gupta
 */
public class LocationTimeSyncService extends IntentService {

	private static final String	CLASS_NAME	= "LocationTimeSyncService";
	
	private static final long SYNC_INTERVAL = 300 * DateUtils.SECOND_IN_MILLIS;
	private boolean checkInternet;
	private static Timer chatSyncTimer = new Timer();
	private SharedPreferences sharedpreferences;
	private boolean startTime = false;
	private boolean endTime = false;
	private String lat;
	private String lang;
	/**
	 * No-arg constructor
	 */
	public LocationTimeSyncService() {
		super(CLASS_NAME);
		
		
	}
	
	
	@Override
	protected void onHandleIntent(Intent intent) {
		try { 
			sharedpreferences = this.getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
			checkInternet = NetworkUtils.isNetworkAvailable(this);
			/*if( ! checkInternet ) {
				if( BuildConfig.DEBUG ) {
					Log.i( CLASS_NAME, "Device is out of network coverage area." );
					Log.i( CLASS_NAME, "Returned - Netword disabled");
				}
				//Toast.makeText(this, "Returned - Netword disabled", Toast.LENGTH_SHORT).show();
				
				return;
			}*/
			
			//if(sharedpreferences.getString("dayStarted",null)!=null){
			
				startTime = intent.getBooleanExtra("startTime", false);
				endTime = intent.getBooleanExtra("endTime", false);
				//Location mLocation = LocationUtils.getBestLastKnownLocation(this);
				Location mLocation = LocationUtils.getBestLocation(this);
				if(mLocation!=null){
					lat = mLocation.getLatitude()+"";
					lang = mLocation.getLongitude()+"";
				}
				synchLocationTime();
				if(!endTime){
					scheduleNextSync();
				}
				

			//}
			//{
				
			//}
			
						
			//phoneContactDataInstance = PhoneContactDataInstance.getInstance(this);
					
			
		} catch (Exception e) {
			if( BuildConfig.DEBUG ) {
				Log.i( CLASS_NAME, "onHandleIntent", e );
			}
			if(!endTime){
				scheduleNextSync();

			}
		}
	}
	
	
	/**
	 * schedule next sync after SYNC_REQUIRED_AFTER_MILLIS
	 */
	private void scheduleNextSync() {
		//mSyncRequestInProgress = true;
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				restartDataSyncService();
			}
		}; 
		//Timer timer = new Timer();
		chatSyncTimer.schedule(timerTask, SYNC_INTERVAL);
		if( BuildConfig.DEBUG ) {
			Log.i( CLASS_NAME,"Scheduled for after - " + SYNC_INTERVAL + " Sec.");
		}
	}
	
	/**
	 * starts sync service
	 */
	private void restartDataSyncService() {
		
		try{
			if(sharedpreferences.getString("dayStarted",null)!=null){
				if(isMidNight()){
					Editor editor=sharedpreferences.edit();
					editor.remove("dayStarted");
					editor.commit();
					Intent locationTimeSyncService = new Intent(this, LocationTimeSyncService.class);
					locationTimeSyncService.putExtra("endTime", true);
					startService(locationTimeSyncService);
					ButtonEnable.endDayEnable=false;
					ButtonEnable.startDayEnable=true;
				}
				else{
					Intent locationTimeSyncService = new Intent(this, LocationTimeSyncService.class);
					startService(locationTimeSyncService);
				}

			}
			else{
				stopSelf();
			}
			
		}catch(Exception ex){
			if( BuildConfig.DEBUG ) {
				Log.i("DataSyncService", ex.getMessage());
			}
			//scheduleNextSync();
		}
	}
	
	private boolean isMidNight(){
		
		Calendar now = Calendar.getInstance();

	    int hour = now.get(Calendar.HOUR);
	    int minute = now.get(Calendar.MINUTE);

	    Date date = parseDate(hour + ":" + minute);
	    Date dateCompareOne = parseDate("23:59");
	    Date dateCompareTwo = parseDate("01:59");

	    if ( dateCompareOne.before( date ) && dateCompareTwo.after(date)) {
	        return true;
	    }
	    else{
	    	return false;
	    }

	
	
	}
	
	private Date parseDate(String date) {
		SimpleDateFormat inputParser = new SimpleDateFormat( "HH:mm", Locale.US);
	    try {
	        return inputParser.parse(date);
	    } catch (java.text.ParseException e) {
	        return new Date(0);
	    }
	}
	
	private void synchLocationTime(){
		ParseObject pObject = new ParseObject("LocationTimeSyncTable");
		pObject.put("userId", sharedpreferences.getString("id", " "));
		pObject.put("userName", sharedpreferences.getString("username", " "));
		pObject.put("latitude", lat);
		pObject.put("longitude", lang);
		pObject.put("reatAt", new Date());
		pObject.put("startTime", startTime);
		pObject.put("endTime", endTime);
		if(checkInternet){
		pObject.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException ex) {
				
				if(ex==null){
					// TODO Auto-generated method stub
					Log.d(CLASS_NAME, "location saved");
				}
				else{
					Log.d(CLASS_NAME, ex.getMessage());
				}
					
				
			}
		});
		}
		else{
			pObject.saveEventually();
		}
		
	}
	
	
	
}