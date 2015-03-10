package com.ambsellerapp.fragments;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
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

public class StartEndDialog extends DialogFragment implements
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
		view = inflater.inflate(com.AMBSEA.R.layout.start_end_dialog, null, false);
		TextViewToastTitle=(TextView)view.findViewById(R.id.TextViewToastTitle);
//		TextViewTime=(TextView)view.findViewById(R.id.TxtViewTime);
//		TextViewDate=(TextView)view.findViewById(R.id.TxtViewDate);
		BtnConfirmOfStartEndDay=(Button)view.findViewById(R.id.BtnConfirmOfStartEndDay);
		BtnCancel=(Button)view.findViewById(R.id.BtnCcancel);
		if(status.equals("start"))
		{
			TextViewToastTitle.setText("Your start date/time will be recorded");
		}
		else
		{
			TextViewToastTitle.setText("Your end date/time will be recorded");
		}
		String date = (DateFormat.format("dd-MM-yyyy", new java.util.Date()).toString());
		String mytime = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
//		TextViewTime.setText("     Time :  "+mytime);
//		TextViewDate.setText("     Date :  "+date);

		BtnConfirmOfStartEndDay.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				
				boolean startTime = false;
				boolean endTime = false;
				
				Editor editor=sharedpreferences.edit();

				if(status.equals("start")){
					startTime = true;
					endTime = false;
					editor.putString("dayStarted", "yes");
				}
				else{
					editor.remove("dayStarted");
					startTime = false;
					endTime = true;
					
				}
				editor.commit();
				
				Intent locationTimeSyncService = new Intent(getActivity(), LocationTimeSyncService.class);
				locationTimeSyncService.putExtra("startTime", startTime);
				locationTimeSyncService.putExtra("endTime", endTime);
				getActivity().startService(locationTimeSyncService);

				
				
				HomeFragment newFragment = new HomeFragment();
				FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.container, newFragment);
				transaction.commit();
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
		
		BtnCancel.setOnClickListener(new OnClickListener()
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

			/*	if(status.equals("start")){

					getActivity().startService(new Intent(getActivity(),Tracklocation.class));					
				}
				else
				{
					getActivity().stopService(new Intent(getActivity(),Tracklocation.class));
				}
*/			}
		});


		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		dismiss();
	}
}