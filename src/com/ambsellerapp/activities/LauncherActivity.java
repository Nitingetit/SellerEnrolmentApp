package com.ambsellerapp.activities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ambsellerapp.backgroundservices.SchedulerSetupReceiver;
import com.ambsellerapp.database.DatabaseHandler;
import com.ambsellerapp.fragments.CreateLeadsFragment;
import com.ambsellerapp.fragments.HomeFragment;
import com.ambsellerapp.fragments.LeadsFragment;
import com.ambsellerapp.fragments.NameRegistrationFragment;
import com.ambsellerapp.fragments.SearchDialog;
import com.ambsellerapp.fragments.SearchListingFragment;
import com.ambsellerapp.fragments.SplashFragment;
import com.ambsellerapp.listeners.CRMListener;
import com.ambsellerapp.modals.NewSeller;
import com.AMBSEA.R;
import com.ambsellerapp.utilies.CRMUtilities;
import com.ambsellerapp.utilies.NetworkUtils;
import com.ambsellerapp.utilies.Utility;
import com.google.analytics.tracking.android.EasyTracker;
import com.parse.ParseAnalytics;

public class LauncherActivity extends ActionBarActivity 
{
	public static final int NAME_REGISTRATION_FRAGMENT = 0;
	public static final int BANK_REGISTRATION_FRAGMENT = 1;
	public static final int LOCATION_REGISTRATION_FRAGMENT = 2;
	public static final int SELLER_LISTING_FRAGMENT = 3;
	public static final int HOME_FRAGMENT = 4;
	public static final int CREATE_LEADS = 5;
	public static final int LEADS = 6;

	public int currentFragment = NAME_REGISTRATION_FRAGMENT;
	private ActionBar actionBar;
	private SharedPreferences sharedpreferences;
	private Context ctx;
	private ArrayList<NewSeller> newSellerOfflineList;
	private NewSeller newSeller;
	private String successStatus;
	private boolean checkInternet;
	private long id=0;
	private ProgressBar progressBar;
	private String session="",userId="";
	public static int i=0;
	public static int fromSearch=0;
	public static int fromLead=0;

	boolean isHaveSession = false;
	CRMListener listenerForSettion=null;
	boolean isRefreshing=false;
	private DatabaseHandler db=null;
	private SchedulerSetupReceiver screenReceiver=null;


	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

		actionBar = getActionBar();
		ctx=LauncherActivity.this;
		//ParseAnalytics.trackAppOpened(getIntent());
		//		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00107d")));

		sharedpreferences = getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		checkInternet = NetworkUtils.isNetworkAvailable(ctx);
		try { 
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		//		broadcastIntent();

		// Below is the code for Google Analytics Tracking

		db=new DatabaseHandler(ctx);
		setContentView(R.layout.activity_main);
		progressBar=(ProgressBar)findViewById(R.id.progressBarLauncherActivity);
		if(!sharedpreferences.getString("username","").equalsIgnoreCase(""))
		{
			HomeFragment newFragment = new HomeFragment();
			//			Bundle args = new Bundle();
			//			newFragment.setArguments(args);

			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.container, newFragment);
			transaction.commit();
		}
		else
		{
			if(savedInstanceState==null)
			{
				actionBar.hide();
				SplashFragment newFragment = new SplashFragment();
				Bundle args = new Bundle();
				newFragment.setArguments(args);
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction(); 

				// Replace whatever is in the fragment_container view with this fragment,and add the transaction to the back stack so the user can navigate back 
				transaction.replace(R.id.container, newFragment);
				//transaction.addToBackStack(null); 

				// Commit the transaction 
				transaction.commit(); 
			}
		}

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		screenReceiver=new SchedulerSetupReceiver();
		registerReceiver(screenReceiver, filter);
		
	}
	@Override
	protected void onStart() 
	{
		super.onStart();
		EasyTracker.getInstance(ctx).activityStart(this); // Add this method.
	}
	@Override
	protected void onStop() 
	{
		super.onStop();
		EasyTracker.getInstance(ctx).activityStop(this); // Add this method.
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(screenReceiver);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu)
	{
		if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
			if(menu.getClass().getSimpleName().equals("MenuBuilder")){
				try{
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				}
				catch(NoSuchMethodException e){
					Log.e("TAG", "onMenuOpened", e);
				}
				catch(Exception e){
					throw new RuntimeException(e);
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);
		View v = menu.findItem(R.id.search).getActionView();

		//		return super.onCreateOptionsMenu(menu);///Commented on 3rd Feb 15 by ABHI due to overflow menu opening after clicking the option button of the device
		return false;
	}
	@Override

	public void onBackPressed() 
	{
		if(currentFragment==NAME_REGISTRATION_FRAGMENT)
		{
			if(fromSearch==1)
			{
				currentFragment=SELLER_LISTING_FRAGMENT;
				SearchListingFragment newFragment = new SearchListingFragment();
				Bundle args = new Bundle();
				args.putString("SearchList", "default");
				newFragment.setArguments(args);

				FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction(); 

				// Replace whatever is in the fragment_container view with this fragment,and add the transaction to the back stack so the user can navigate back 
				transaction.replace(R.id.container, newFragment);

				// Commit the transaction 
				transaction.commit();
				Log.d("FRAG",String.valueOf(currentFragment));
			}
			else if(fromLead==1){
				currentFragment=LEADS;
				fromLead=0;
				LeadsFragment leadsFragment=new LeadsFragment();
				FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction(); 
				transaction.replace(R.id.container, leadsFragment);
				transaction.commit();
				Log.d("FRAG",String.valueOf(currentFragment));
			}
			else
			{
				HomeFragment newFragment = new HomeFragment();
				FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction(); 

				// Replace whatever is in the fragment_container view with this fragment,and add the transaction to the back stack so the user can navigate back 
				transaction.replace(R.id.container, newFragment);

				// Commit the transaction 
				transaction.commit();
				Log.d("FRAG",String.valueOf(currentFragment));
			}
		}
		else if(currentFragment==CREATE_LEADS)
		{
			if(fromLead==2){
				currentFragment=LEADS;
				fromLead=0;
				LeadsFragment leadsFragment=new LeadsFragment();
				FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction(); 
				transaction.replace(R.id.container, leadsFragment);
				transaction.commit();
				Log.d("FRAG",String.valueOf(currentFragment));
			}
			else{
				HomeFragment newFragment = new HomeFragment();
				FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction(); 
				transaction.replace(R.id.container, newFragment);
				transaction.commit();
				Log.d("FRAG",String.valueOf(currentFragment));
			}
		}
		else if(currentFragment==LEADS || currentFragment==SELLER_LISTING_FRAGMENT)
		{
			HomeFragment newFragment = new HomeFragment();
			FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction(); 
			transaction.replace(R.id.container, newFragment);
			transaction.commit();
			Log.d("FRAG",String.valueOf(currentFragment));
		}
		
		else if(currentFragment==HOME_FRAGMENT)
		{
			Log.d("FRAG",String.valueOf(currentFragment));
			finish();
		}
		
		else
		{
			super.onBackPressed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		case android.R.id.home:

			HomeFragment homeFragment = new HomeFragment();
			FragmentTransaction homeTransaction = this.getSupportFragmentManager().beginTransaction(); 
			homeTransaction.replace(R.id.container, homeFragment);
			homeTransaction.commit();
			actionBar.setDisplayShowCustomEnabled(false);
			actionBar.setDisplayShowTitleEnabled(true);
			return true;

		case R.id.sellerList:

			SearchListingFragment searchFrag=new SearchListingFragment();
			Bundle args=new Bundle();
			//						System.out.println("------Value of sellItem in SellerFragment is--"+item.getTitle());
			//			args.putParcelableArrayList("sellerArraylist", searchSellerList);
			args.putString("SearchList", "default");
			searchFrag.setArguments(args);

			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction(); 
			//			transaction.addToBackStack("SELLER_LISTING_FRAGMENT");
			// Replace whatever is in the fragment_container view with this fragment,and add the transaction to the back stack so the user can navigate back 
			transaction.replace(R.id.container, searchFrag);
			transaction.commit();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setDisplayShowCustomEnabled(false);
			return true;

		/*case R.id.search:

			SearchListingFragment searchFragForSearch=new SearchListingFragment();
			Bundle arg=new Bundle();
			arg.putString("SearchList", "SearchList");
			searchFragForSearch.setArguments(arg);
			FragmentTransaction trans1 = getSupportFragmentManager().beginTransaction(); 
			trans1.replace(R.id.container, searchFragForSearch);
			trans1.commit();

			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setDisplayShowCustomEnabled(false);
			return true;*/
		case R.id.registration:

			NameRegistrationFragment newFragment = new NameRegistrationFragment();
			//			Bundle args = new Bundle();
			//			newFragment.setArguments(args);

			FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
			tran.replace(R.id.container, newFragment);
			tran.commit();
			actionBar.setDisplayShowCustomEnabled(false);
			actionBar.setDisplayShowTitleEnabled(true);
			return true;
		case R.id.refresh:
			isRefreshing =true;
			// help action

			newSellerOfflineList=(ArrayList<NewSeller>) db.getNotSyncedSellers();

			//						recursiveCallback(newSellerOfflineList);
			//			int i = 0;
			//			while (i < newSellerOfflineList.size())
			//			{
			//				newSeller=newSellerOfflineList.get(i);
			if(checkInternet)
			{
				if(newSellerOfflineList.size()!=0)
				{
					progressBar.setVisibility(View.VISIBLE);
					CRMUtilities crmUtilities =new CRMUtilities(this);
					crmUtilities.setRequestForRefresh();
					crmUtilities.setEndListner(new CRMListener() {

						@Override
						public void getCAllback(String result) {
							progressBar.setVisibility(View.GONE);
						}
					});
					crmUtilities.recursiveCallback(newSellerOfflineList);
				}
			}
			else
			{
				Toast customToast = new Toast(getBaseContext());
				customToast = Toast.makeText(getBaseContext(), "No internet Connectivity!!!", Toast.LENGTH_SHORT);
				customToast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
				customToast.show();
			}

			return true;
		case R.id.log_out:
			// check for updates action
			actionBar.hide();
			SplashFragment splashFragment = new SplashFragment();
			Bundle arg2 = new Bundle();
			splashFragment.setArguments(arg2);
			FragmentTransaction trans = getSupportFragmentManager().beginTransaction(); 

			// Replace whatever is in the fragment_container view with this fragment,and add the transaction to the back stack so the user can navigate back 
			trans.replace(R.id.container, splashFragment);

			SharedPreferences sharedPref= getSharedPreferences(Utility.MyPREFERENCES,0);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.clear();      //its clear all data.
			editor.commit();  //Don't forgot to commit  SharedPreferences.
			//transaction.addToBackStack(null); 
			//Toast.makeText(getBaseContext(), "Logout!!!", Toast.LENGTH_SHORT).show();
			actionBar.setDisplayShowCustomEnabled(false);
			// Commit the transaction 
			trans.commit(); 
			return true;
		case R.id.create_leads:
			CreateLeadsFragment createLeadsFragment = new CreateLeadsFragment();
			FragmentTransaction createLeadsTrans = getSupportFragmentManager().beginTransaction();
			createLeadsTrans.replace(R.id.container, createLeadsFragment);
			createLeadsTrans.addToBackStack("CreateLeads"); 
			createLeadsTrans.commit();
			actionBar.setDisplayShowCustomEnabled(false);	
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(true);
			return true;
		case R.id.leads:
			LeadsFragment leadsFragment = new LeadsFragment();
			FragmentTransaction leadsTrans = getSupportFragmentManager().beginTransaction();
			leadsTrans.replace(R.id.container, leadsFragment);
			leadsTrans.addToBackStack("Leads"); 
			leadsTrans.commit();
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	//	CRMUtilities crmUtilities= new CRMUtilities();
	//	
	//	crmUtilities.setEndListner(new CRMListener() {
	//		
	//		@Override
	//		public void getCAllback(String result) {
	//			
	//		}
	//	});
	//	public void recursiveCallback(final ArrayList<NewSeller> sellerList)
	//	{
	//		if(sellerList.isEmpty()==false){
	//			if(!isHaveSession)
	//			{
	//				new AsyncUtilies.FirstAsyn(getChallenge(),sharedpreferences.getString("username", "")).execute();
	//				listenerForSettion = new CRMListener() {
	//					
	//					@Override
	//					public void getCAllback(String result) {
	//						isHaveSession= true;
	//						sellerList.remove(0);
	//						recursiveCallback(sellerList);
	//					}
	//				};
	//			}else{
	//				new AsyncUtilies.postingSellerDataAsyn(sharedpreferences.getString("username", ""),
	//						session,sellerList.get(0),getFinalResultListener(),userId).execute();
	//			}
	//		}else{
	//			isRefreshing=false;
	//		}
	//	}

	
	/**
	 * Method of changing the Fragment from the Adapter
	 */
	public void switchContent(Fragment fragment) 
	{ 
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    } 
}
