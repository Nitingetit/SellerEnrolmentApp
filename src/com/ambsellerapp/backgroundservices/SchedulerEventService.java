package com.ambsellerapp.backgroundservices;

import java.util.ArrayList;
import java.util.Date;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.ambsellerapp.database.DatabaseHandler;
import com.ambsellerapp.listeners.CRMListener;
import com.ambsellerapp.modals.NewSeller;
import com.ambsellerapp.utilies.CRMUtilities;
import com.ambsellerapp.utilies.NetworkUtils;
import com.ambsellerapp.utilies.Utility;

public class SchedulerEventService extends Service 
{
	private static final String APP_TAG = "com.ambsellerapp";
	private ArrayList<NewSeller> newSellerOfflineList;
	private boolean checkInternet;
	boolean isHaveSession = false;
	CRMListener listenerForSettion=null;
	boolean isRefreshing=false;
	private SharedPreferences sharedpreferences;
	private String session="",userId="";
	public static int i=0;
	private NewSeller newSeller;
	private String successStatus;
	private long id=0;
	private Context ctx;
	private String sellerFinalId;
	private String rDId;
	@Override
	public IBinder onBind(final Intent intent) 
	{
		return null;
	}
	CRMUtilities crmUtilities=null;
	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startId) 
	{
//		Toast.makeText(this, "Inside the Service Starting Class", Toast.LENGTH_SHORT).show();
		Log.d(APP_TAG, "Service Started intialised : " + new Date().toString());
		ctx=this;
		DatabaseHandler db=new DatabaseHandler(ctx);
		checkInternet = NetworkUtils.isNetworkAvailable(ctx);
		sharedpreferences = getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		newSellerOfflineList=(ArrayList<NewSeller>) db.getNotSyncedSellers();
		
		
		if(checkInternet)
		{
			if(newSellerOfflineList.size()!=0)
			{
				CRMUtilities crmUtilities =new CRMUtilities(this);
				crmUtilities.setRequestForRefresh();
				crmUtilities.setEndListner(new CRMListener() {

					@Override
					public void getCAllback(String result) {
					}
				});
				crmUtilities.recursiveCallback(newSellerOfflineList);
			}
		}
		return Service.START_NOT_STICKY;
	}
}
