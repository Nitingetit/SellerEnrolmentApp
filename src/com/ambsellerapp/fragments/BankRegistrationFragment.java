package com.ambsellerapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.ambsellerapp.activities.LauncherActivity;
import com.AMBSEA.R;
import com.ambsellerapp.utilies.Utility;

public class BankRegistrationFragment extends Fragment implements OnClickListener
{
	private View contentView;
	private boolean checkInternet = false;
	private EditText bankNameEdittext;
	private EditText ifscCodeEdittext;
	private EditText accountNumberEdittext;
	private EditText branchEdittext;
	private Button nextButton;
	private Button previousButton;
	private SharedPreferences sharedpreferences;
	private ProgressBar progressBar;
	//private Spinner topUpDropDown;
	
	public static final int BANK_REGISTRATION_FRAGMENT = 1;
	
	private String[] amount = { "1000", "2000","3000","4000","5000","6000","7000","8000","9000","10000" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		contentView = inflater.inflate(R.layout.bank_registration_form, container, false);

		SettingIds();///Call for Setting the ids of the Views inside the layout
		nextButton.setOnClickListener(this);
		previousButton.setOnClickListener(this);
		return contentView;
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
		bankNameEdittext=(EditText) contentView.findViewById(R.id.bank_name_edittext);
		ifscCodeEdittext=(EditText) contentView.findViewById(R.id.ifsc_edittext);
		accountNumberEdittext=(EditText) contentView.findViewById(R.id.account_no_edittext);
		branchEdittext=(EditText) contentView.findViewById(R.id.branch_edittext);
		//topUpDropDown=(Spinner) contentView.findViewById(R.id.topUpDropDown);

//		nextButton=(Button)contentView.findViewById(R.id.btnNextBankRegistration);
//		previousButton=(Button)contentView.findViewById(R.id.btnPreviousBankRegistration);
		progressBar=(ProgressBar)contentView.findViewById(R.id.progress);
		
		((LauncherActivity)getActivity()).currentFragment=BANK_REGISTRATION_FRAGMENT;///Setting the curent fragment for backButton Handling
		
		 ArrayAdapter<String> adapter_amount = new ArrayAdapter<String>(getActivity(),
				 android.R.layout.simple_spinner_item, amount);
		 adapter_amount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 //topUpDropDown.setAdapter(adapter_amount);
	}
	

	/** Method for checking the pin code and phone empty Parameter
	 * @return true if they are not empty
	 */
	private boolean checkEnteredDetails()
	{
		if (bankNameEdittext.getText().toString().equals(""))
			return false;
		if (ifscCodeEdittext.getText().toString().equals(""))
			return false;
		if (accountNumberEdittext.getText().toString().equals(""))
			return false;
		if (branchEdittext.getText().toString().equals(""))
			return false;
		return true;
	}
	/** Method for checking the pin code and phone required Parameter lenght is inputed or not
	 * @return true 
	 */
	private boolean inputValidation() 
	{
		//		if (pincodeEdittext.getText().toString().length() < 6) 
		//		{
		//			Toast.makeText(getActivity(), "Pin Code should be of 6 digits.",Toast.LENGTH_SHORT).show();
		//			return false;
		//		}
		//		if (phoneEdittext.getText().toString().length() < 10) 
		//		{
		//			Toast.makeText(getActivity(),"Mobile number should be of 10 digits.", Toast.LENGTH_SHORT).show();
		//			return false;
		//		}
		return true;
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId())
		{
//		case R.id.btnNextBankRegistration:
////			checkEnteredDetails();
//			LocationRegistrationFragment newFragment = new LocationRegistrationFragment();
//			Bundle args = new Bundle();
//			newFragment.setArguments(args);
//
//			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//			transaction.replace(R.id.container, newFragment);
//			transaction.addToBackStack(null); 
//			transaction.commit();
//			break;

//		case R.id.btnPreviousBankRegistration:
//			getActivity().onBackPressed();
//			break;
		default:
			break;
		}
	}
}
