package com.ambsellerapp.utilies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils
{
	public static boolean isNetworkAvailable(ConnectivityManager connectivityManager) 
	{
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}
	
	public static boolean isNetworkAvailable(Context context)
	{
		return isNetworkAvailable((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE));
	}
}
