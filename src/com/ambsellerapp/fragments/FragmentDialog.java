package com.ambsellerapp.fragments;

import java.util.Calendar;

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

public class FragmentDialog extends DialogFragment implements
OnItemClickListener {


	private Button confirmButton,BtnConfirmOfStartEndDay;
	private static TextView EditTxtRegistrationStatus,TextViewTime,TextViewDate,TextViewToastTitle;
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		String status = getTag(); 
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		view = inflater.inflate(com.AMBSEA.R.layout.dialog_fragment, null, false);
		confirmButton=(Button)view.findViewById(R.id.confirm_button);
		EditTxtRegistrationStatus = (TextView) view.findViewById(R.id.editTxtRegistrationStatus);
		if(status.equalsIgnoreCase("true"))
			status="Registration Successful";
		else if(status.equalsIgnoreCase("offline"))
			status="Data stored in the local database, it will sync later.";
		else if(status.equalsIgnoreCase("update"))
			status="Seller Updated Successfully.";
		else
			status="Registration "+"Failed";
		EditTxtRegistrationStatus.setText(status);

		confirmButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				HomeFragment newFragment = new HomeFragment();
				//				Bundle args = new Bundle();
				//				newFragment.setArguments(args);

				FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.container, newFragment);
				transaction.commit();
				getDialog().dismiss();
			}
		});

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		dismiss();
	}
}