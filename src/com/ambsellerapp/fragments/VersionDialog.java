package com.ambsellerapp.fragments;

import java.util.Calendar;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.AMBSEA.R;
import com.ambsellerapp.backgroundservices.LocationTimeSyncService;
import com.ambsellerapp.utilies.Utility;

public class VersionDialog extends DialogFragment implements
OnItemClickListener {


	private Button confirmButton,BtnConfirmOfStartEndDay,BtnCancel;
	private static TextView EditTxtRegistrationStatus,TextViewTime,TextViewDate,TextViewToastTitle;
	private View view;
	public String status;
	private SharedPreferences sharedpreferences;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		status = getTag(); 
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		sharedpreferences = getActivity().getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		view = inflater.inflate(com.AMBSEA.R.layout.version_dialog, null, false);
		TextViewToastTitle=(TextView)view.findViewById(R.id.TextViewToastTitle);
		TextViewTime=(TextView)view.findViewById(R.id.TxtViewTime);
//		TextViewDate=(TextView)view.findViewById(R.id.TxtViewDate);
		BtnConfirmOfStartEndDay=(Button)view.findViewById(R.id.BtnConfirmOfStartEndDay);
		BtnCancel=(Button)view.findViewById(R.id.BtnCcancel);
		TextViewToastTitle.setText("New version of SEA app is available, Please download and upgrade click on below link");
		TextViewTime.setClickable(true);
		TextViewTime.setMovementMethod(LinkMovementMethod.getInstance());
		String text = "<a href='"+status+"'> App link </a>";
		TextViewTime.setText(Html.fromHtml(text));
//		TextViewTime.setText("     Time :  "+mytime);
//		TextViewDate.setText("     Date :  "+date);

		BtnConfirmOfStartEndDay.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				ActionBar actionBar = getActivity().getActionBar();
				actionBar.hide();
				SplashFragment splashFragment = new SplashFragment();
				Bundle arg2 = new Bundle();
				splashFragment.setArguments(arg2);
				FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction(); 

				// Replace whatever is in the fragment_container view with this fragment,and add the transaction to the back stack so the user can navigate back 
				trans.replace(R.id.container, splashFragment);

				SharedPreferences sharedPref= getActivity().getSharedPreferences(Utility.MyPREFERENCES,0);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.clear();      //its clear all data.
				editor.commit();  //Don't forgot to commit  SharedPreferences.
				//transaction.addToBackStack(null); 
				//Toast.makeText(getBaseContext(), "Logout!!!", Toast.LENGTH_SHORT).show();
				actionBar.setDisplayShowCustomEnabled(false);
				// Commit the transaction 
				trans.commit(); 
				getDialog().dismiss();

			/*	if(status.equals("start")){

					getActivity().startService(new Intent(getActivity(),Tracklocation.class));					
				}
				else
				{
					getActivity().stopService(new Intent(getActivity(),Tracklocation.class));
				}
*/			}
		});
		
		/*BtnCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(status.equals("start")){
					ButtonEnable.startDayEnable=true;
					ButtonEnable.endDayEnable=false;
				}
				else{
					ButtonEnable.startDayEnable=false;
					ButtonEnable.endDayEnable=true;
				}
				
				getDialog().dismiss();

				if(status.equals("start")){

					getActivity().startService(new Intent(getActivity(),Tracklocation.class));					
				}
				else
				{
					getActivity().stopService(new Intent(getActivity(),Tracklocation.class));
				}
			}
		});
*/

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		dismiss();
	}
}