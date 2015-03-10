package com.ambsellerapp.fragments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.AMBSEA.R;
import com.ambsellerapp.activities.LauncherActivity;
import com.ambsellerapp.utilies.NetworkUtils;
import com.ambsellerapp.utilies.Utility;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class HomeFragment extends Fragment implements OnClickListener
{
	private View contentView;
	private Button BtnStartDay,BtnEndDay,BtnRegisterSeller,BtnSellerList,BtnLeads,BtnCreateLeeds;
	private Button previousButton;
	private SharedPreferences sharedpreferences;
	private ProgressBar progressBar;
	public static final int HOME_FRAGMENT = 4;

	public static final int BANK_REGISTRATION_FRAGMENT = 1;
	private boolean checkInternet;
	private Dialog lead_progress_bar_dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setHasOptionsMenu (true);
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		menu.findItem(R.id.create_leads).setVisible(false).setEnabled(false);
		menu.findItem(R.id.leads).setVisible(false).setEnabled(false);
		menu.findItem(R.id.registration).setVisible(false).setEnabled(false);
		menu.findItem(R.id.sellerList).setVisible(false).setEnabled(false);
		menu.findItem(R.id.search).setVisible(false).setEnabled(false);
		menu.findItem(R.id.refresh).setVisible(false).setEnabled(false);

		super.onCreateOptionsMenu(menu, inflater);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		contentView = inflater.inflate(R.layout.home_fragment, container, false);
		SettingIds();///Call for Setting the ids of the Views inside the layout
		BtnStartDay.setOnClickListener(this);
		BtnEndDay.setOnClickListener(this);
		BtnRegisterSeller.setOnClickListener(this);
		BtnSellerList.setOnClickListener(this);
		BtnLeads.setOnClickListener(this);
		BtnCreateLeeds.setOnClickListener(this);
		checkInternet = NetworkUtils.isNetworkAvailable(getActivity());
		if(checkInternet){
			lead_progress_bar_dialog = new Dialog(getActivity());
			lead_progress_bar_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			lead_progress_bar_dialog.setContentView(R.layout.lead_progressbar);
			lead_progress_bar_dialog.setCancelable(false);
			lead_progress_bar_dialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			lead_progress_bar_dialog.show();
			getNewVersionInfo();
		}
		//addAnalytics();
		return contentView;
	}
	
	private void getNewVersionInfo(){
		String versionName="";
		int versionNumber=0;
		 try {
			 ParseQuery<ParseObject> query = ParseQuery
						.getQuery("AppVersion");
				query.orderByDescending("version_number");
				query.setLimit(1);
				query.findInBackground(new FindCallback<ParseObject>() {

					@Override
					public void done(List<ParseObject> objects, ParseException e) {
						if (e == null) {
							checkNewVersion(objects);
						} else {
							// nothing to do here
						}

					}
				});
			 
		    } catch (Exception e) {
		        // Huh? Really?
		    }
	}
	
	private void checkNewVersion(List<ParseObject> pObjects){
		boolean isNewVersionFound=false;
		String driveLink="";
		int verNoParse=0;
		String versionNameParse ="";
		if(!pObjects.isEmpty()){
			ParseObject pObj = pObjects.get(0);
			driveLink = pObj.getString("drive_link");
			verNoParse = pObj.getInt("version_number");
			versionNameParse = pObj.getString("version_name");
			try{
				 String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
				 int versionNumber = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
				 if(verNoParse > versionNumber){
					 isNewVersionFound = true;
				 }
				
			}catch(Exception ex){
				Log.d("HomeFragment", ex.getMessage());
			}
			
			
		}
		
		if(isNewVersionFound){
			lead_progress_bar_dialog.dismiss();
			FragmentManager manager = getFragmentManager();
			VersionDialog verDialog=new VersionDialog(); 
			verDialog.setCancelable(false);
			verDialog.show(manager, driveLink);
		}
		else{
			lead_progress_bar_dialog.dismiss();
		}
		
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	/** Method for settting the ids of the layouts
	 * @return void
	 */
	private void SettingIds()
	{
		sharedpreferences = getActivity().getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);

		BtnStartDay=(Button)contentView.findViewById(R.id.btnStartDay);
		BtnEndDay=(Button)contentView.findViewById(R.id.btnEndDay);
		BtnRegisterSeller=(Button)contentView.findViewById(R.id.btnRegisterSeller);
		BtnSellerList=(Button)contentView.findViewById(R.id.btnSellerList);
		BtnLeads=(Button)contentView.findViewById(R.id.btnLeeds);
		BtnCreateLeeds=(Button)contentView.findViewById(R.id.btnCreateLeeds);
		progressBar=(ProgressBar)contentView.findViewById(R.id.progress);
		getActivity().getActionBar().show();
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		getActivity().getActionBar().setDisplayShowCustomEnabled(false);	
		getActivity().getActionBar().setDisplayShowTitleEnabled(true);
		getActivity().getActionBar().setTitle("Home");
		((LauncherActivity)getActivity()).currentFragment=HOME_FRAGMENT;///Setting the curent fragment for backButton Handling
		
	}
	@Override
	public void onClick(View v) 
	{
		String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
		switch (v.getId())
		{
		case R.id.btnStartDay:
//			LayoutInflater inflater = getActivity().getLayoutInflater();
//
//			View layout = inflater.inflate(R.layout.custom_toast,
//					(ViewGroup)contentView.findViewById(R.id.TextViewToast));
//			TextView text = (TextView) layout.findViewById(R.id.TextViewToast);
//			// Set the Text to show in TextView
//			text.setText("Your day starts at \n"+date);
//
//			Toast toast = new Toast(getActivity());
//			toast.setDuration(Toast.LENGTH_SHORT);
//			toast.setView(layout);
//			toast.setGravity(Gravity.CENTER, 0, 0);
//			toast.show();
			
			if(ButtonEnable.startDayEnable){
				ButtonEnable.startDayEnable=false;
				ButtonEnable.endDayEnable=true;
				FragmentManager manager = getFragmentManager();
				StartEndDialog dialog=new StartEndDialog(); 
				String successStatus="start";
				dialog.setCancelable(false);
				dialog.show(manager, successStatus);
			}
			break;

		case R.id.btnEndDay:
//			LayoutInflater inflater1 = getActivity().getLayoutInflater();
//
//			View layout1 = inflater1.inflate(R.layout.custom_toast,
//					(ViewGroup)contentView.findViewById(R.id.TextViewToast));
//			TextView text1 = (TextView) layout1.findViewById(R.id.TextViewToast);
//			text1.setText("Your day Ends at \n"+date);
//
//			Toast toast1 = new Toast(getActivity());
//			toast1.setDuration(Toast.LENGTH_SHORT);
//			toast1.setView(layout1);
//			toast1.setGravity(Gravity.CENTER, 0, 0);
//			toast1.show();
//			break;
			if(ButtonEnable.endDayEnable){
				ButtonEnable.startDayEnable=true;
				ButtonEnable.endDayEnable=false;
				FragmentManager managerEnd = getFragmentManager();
				StartEndDialog dialogEnd=new StartEndDialog(); 
				String successStatusEnd="end";
				dialogEnd.setCancelable(false);
				dialogEnd.show(managerEnd, successStatusEnd);
			}
			
			break;

		case R.id.btnRegisterSeller:
			NameRegistrationFragment newFragment = new NameRegistrationFragment();
			Bundle args = new Bundle();
			//			newFragment.setArguments(args);

			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.container, newFragment);
			transaction.addToBackStack("HomeFragment"); 
			transaction.commit();
			break;

		case R.id.btnSellerList:
			SearchListingFragment searchFragment = new SearchListingFragment();
			Bundle args1 = new Bundle();
			args1.putString("SearchList", "default");
			searchFragment.setArguments(args1);

			FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
			trans.replace(R.id.container, searchFragment);
			trans.addToBackStack("HomeFragment"); 
			trans.commit();
			break;


		case R.id.btnLeeds:
			LeadsFragment leadsFragment = new LeadsFragment();
			FragmentTransaction leadsTrans = getActivity().getSupportFragmentManager().beginTransaction();
			leadsTrans.replace(R.id.container, leadsFragment);
			leadsTrans.addToBackStack("LeadsFragment");
			leadsTrans.commit();
			break;

		case R.id.btnCreateLeeds:
			CreateLeadsFragment createLeadsFragment = new CreateLeadsFragment();
			FragmentTransaction createLeadsTrans = getActivity().getSupportFragmentManager().beginTransaction();
			createLeadsTrans.replace(R.id.container, createLeadsFragment);
			createLeadsTrans.addToBackStack("CreateLeadsFragment"); 
			createLeadsTrans.commit();
			break;
			
		default:
			break;
		}
	}
	private void addAnalytics(){
		 Map<String, String> dimensions = new HashMap<String, String>();
			dimensions.put("SCREEN_NAME", "Home Dashboard");
			dimensions.put("UserId", sharedpreferences.getString("username", " "));
			ParseAnalytics.trackEvent("DailyLogin", dimensions);
	}
}
