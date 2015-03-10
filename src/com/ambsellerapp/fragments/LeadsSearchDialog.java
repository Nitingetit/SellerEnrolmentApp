package com.ambsellerapp.fragments;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.AMBSEA.R;
import com.ambsellerapp.adapters.NothingSelectedSpinnerAdapter;
import com.ambsellerapp.asynctasks.AsyncUtilies;
import com.ambsellerapp.listeners.CRMListener;
import com.ambsellerapp.listeners.LeadsListener;
import com.ambsellerapp.modals.LeadsData;
import com.ambsellerapp.utilies.NetworkUtils;
import com.ambsellerapp.utilies.Utility;

public class LeadsSearchDialog extends DialogFragment implements
OnItemClickListener {


	private Button btnSearch;
	private EditText editTxtMobile=null,editTxtName=null;
	private Spinner editTxtPinNo,changeStatus;
	private CheckBox checkBxPinNo,checkBxMobile,checkBxName,checkStatus;
	private View view;
	private boolean isNameChecked=false,isMobileCheck=false,enteredDetails=false,isStatusCheked=false;
	private ProgressBar progBar;
	private boolean checkInternet = false;
	private SharedPreferences sharedpreferences;
	private String successStatus;
	private long id=0;
	private ProgressBar progressBar;
	private String session="",userId="";
	private String pinCode="",mobileNo="",name="",fromWhere="leadsSearch";
	private ArrayList<LeadsData> searchSellerList=new ArrayList<LeadsData>();
	private RadioGroup radioLayout;
	private LinearLayout pinLayout,mobileLayout,nameLayout;
	private String btnMode="online";
	private Calendar cal;
	 private int day;
	 private int month;
	 private int year;
	 private TextView et;
	private ArrayList<String> arrayPinCode = new ArrayList<String>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{

		view = inflater.inflate(com.AMBSEA.R.layout.lead_search_layout, null, false);
	//	getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
		getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
		getDialog().setCancelable(true);
		getDialog().setTitle( Html.fromHtml("<font color='#ffffff' size='10' >Search By</font>"));
		getDialog().show();
		getDialog().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_action_search);

		settingIds();
		return view;

	}
	
	
	/*@Override
	public void onClick(View v) {
		getActivity().showDialog(1);
	}
	 
	 @Deprecated
	 protected Dialog onCreateDialog(int id) {
	  return new DatePickerDialog(getActivity(), datePickerListener, year, month, day);
	 }

	 private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
	  public void onDateSet(DatePicker view, int selectedYear,
	    int selectedMonth, int selectedDay) {
	   et.setText(selectedDay + " / " + (selectedMonth + 1) + " / "
	     + selectedYear);
	  }
	 };*/
	private void settingIds()
	{
		btnSearch=(Button)view.findViewById(R.id.searchButton);
		editTxtPinNo= (Spinner) view.findViewById(R.id.editTextPinNo);
		editTxtMobile= (EditText) view.findViewById(R.id.editTxtMobile);
		editTxtName= (EditText) view.findViewById(R.id.editTxtName);
		checkBxPinNo= (CheckBox) view.findViewById(R.id.pinCheckBox);
		checkBxMobile= (CheckBox) view.findViewById(R.id.mobileCheckBox);
		checkBxName= (CheckBox) view.findViewById(R.id.nameCheckBox);
		checkStatus = (CheckBox) view.findViewById(R.id.statusCheckBox);
		progBar=(ProgressBar)view.findViewById(R.id.progressBarSearchDialog);
		checkInternet = NetworkUtils.isNetworkAvailable(getActivity());
		sharedpreferences = getActivity().getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		pinLayout=(LinearLayout)view.findViewById(R.id.pinNoLayout);
		mobileLayout=(LinearLayout)view.findViewById(R.id.mobileNoLayout);
		nameLayout=(LinearLayout)view.findViewById(R.id.nameLayout);
		view.findViewById(R.id.radio_layout).setVisibility(View.GONE);
		String[] statusSpinnerItems = {"Pending","Re-scheduled"};
		changeStatus=(Spinner)view.findViewById(R.id.spinnerstatus);
		/*cal = Calendar.getInstance();
		  day = cal.get(Calendar.DAY_OF_MONTH);
		  month = cal.get(Calendar.MONTH);
		  year = cal.get(Calendar.YEAR);
		  et = (TextView) view.findViewById(R.id.dateandtimeTextView);
		  
		  et.setOnClickListener(this);*/
		  
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item,
				statusSpinnerItems);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		changeStatus.setAdapter(new NothingSelectedSpinnerAdapter(
				dataAdapter, R.layout.contact_spinner_row_nothing_selected,
				getActivity()));
		//radioLayout.setSelected(true);

		try 
		{
			try 
			{
				Set<String> set = sharedpreferences.getStringSet("pincodeList", null);
				arrayPinCode=new ArrayList<String>(set);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				arrayPinCode.add("PIN Code not found");
			}

			if(!arrayPinCode.isEmpty())
			{
				Collections.sort(arrayPinCode);
				ArrayAdapter<String> adapter_amount = new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_spinner_item, arrayPinCode);
				adapter_amount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				editTxtPinNo.setAdapter( new NothingSelectedSpinnerAdapter
						( 	
								adapter_amount,
								R.layout.contact_spinner_row_nothing_selected,getActivity()
								));
				editTxtPinNo.setOnItemSelectedListener(new MyOnItemSelectedListener());
			}
			else
			{
				arrayPinCode.add("PIN Code not found");
				ArrayAdapter<String> adapter_amount = new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_spinner_item, arrayPinCode);
				adapter_amount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				editTxtPinNo.setAdapter( new NothingSelectedSpinnerAdapter
						( 
								adapter_amount,
								R.layout.contact_spinner_row_nothing_selected,getActivity()
								));
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		/*radioLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup arg0, int id) 
			{
				switch (id) 
				{
				case R.id.online_radio_button:
					pinLayout.setVisibility(View.VISIBLE);
					mobileLayout.setVisibility(View.VISIBLE);
					nameLayout.setVisibility(View.VISIBLE);
					break;
				case R.id.offline_radio_button:
					pinLayout.setVisibility(View.GONE);
					mobileLayout.setVisibility(View.GONE);
					nameLayout.setVisibility(View.GONE);
					break;
				default:
					break;
				}
			}
		});*/
		
		

		btnSearch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				enteredDetails=checkEnteredDetails();
				//if(radioLayout.getCheckedRadioButtonId()==R.id.online_radio_button)
				//{
					if(enteredDetails)
					{
						checkInternet = NetworkUtils.isNetworkAvailable(getActivity());
						if(checkInternet)
						{
							if(!arrayPinCode.isEmpty())
							{
								try 
								{
									
									if(editTxtPinNo.getSelectedItemPosition()!=0)////If some pincode is choosen from spinner
									{	
										pinCode=editTxtPinNo.getSelectedItem().toString();

									}
//									else if(editTxtPinNo.getSelectedItem().toString().equalsIgnoreCase("PIN Code not found"))
//									{	
//										Toast toast=Toast.makeText(getActivity(), "PIN Code not found!!!",Toast.LENGTH_SHORT);
//										toast.setGravity(Gravity.CENTER, 0, 0);
//										toast.show();
//									}
									else
									{///If nothing is choosen from the spinner
										String frnames[]=arrayPinCode.toArray(new String[arrayPinCode.size()]);
										for(int i = 0; i < frnames.length ; i++)
										{
											if(i==frnames.length-1)
											{
												pinCode+="'"+frnames[i]+"'";
											}	
											else
											{
												pinCode+="'"+frnames[i]+"',";
											}
										} 
									}

									mobileNo=editTxtMobile.getText().toString();
									name=editTxtName.getText().toString();

									progBar.setVisibility(View.VISIBLE);
									new AsyncUtilies.FirstAsyn(getChallenge(),sharedpreferences.getString("username", "")).execute();
									btnMode="online";
								}

								catch (Exception e) 
								{
									e.printStackTrace();
								}
							}
							else
							{
								Toast toast=Toast.makeText(getActivity(), "PIN Code not found!!!",Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}
						}
						else
						{
							getDialog().dismiss();
							Toast toast=Toast.makeText(getActivity(), "Please check Internet connectivity!!!",Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
					}
					else
					{
						Toast toast=Toast.makeText(getActivity(), "Please fill mandatory fields!!!",Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
				/*}
				else
				{///If offline radio button is selected than below code works TODO
					DatabaseHandler db=new DatabaseHandler(getActivity());
					btnMode="offline";
					searchSellerList=(ArrayList<LeadsData>) db.getNotSyncedSellers();
					((LeadsListener) getTargetFragment()).onLeadsComplete(searchSellerList);
					getDialog().dismiss();
				}*/

			}
		});

		checkBxMobile.setOnCheckedChangeListener( new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) 
			{
				if(isMobileCheck==false)
					isMobileCheck=true;
				else
					isMobileCheck=false;
			}
		});
		
		checkStatus.setOnCheckedChangeListener( new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) 
			{
				if(isStatusCheked==false)
					isStatusCheked=true;
				else
					isStatusCheked=false;
			}
		});
		
		//

		checkBxName.setOnCheckedChangeListener( new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) 
			{
				if(isNameChecked==false)
					isNameChecked=true;
				else
					isNameChecked=false;
			}
		});
	}
	private boolean checkEnteredDetails()
	{
		//		if (editTxtPinNo.getSelectedItem().toString().equals(""))
		//			return false;
		if(isMobileCheck)
		{
			if (editTxtMobile.getText().toString().equals(""))
				return false;
		}
		if(isNameChecked)
		{
			if (editTxtName.getText().toString().equals(""))
				return false;
		}
		if(isStatusCheked)
		{
			if (changeStatus.getSelectedItem().toString().equals(""))
				return false;
		}
		return true;
	}		
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
	{
		dismiss();
	}


	public CRMListener getChallenge()
	{
		CRMListener crmListenerGetChallenge=new CRMListener() 
		{
			@Override
			public void getCAllback(String result) 
			{
				String token="";
				//				ctx=
				//				sharedpreferences=ctx.getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
				try 
				{
					JSONObject jObj=new JSONObject(result);
					successStatus=jObj.get("success").toString();
					if(successStatus.equalsIgnoreCase("true"))
					{
						JSONObject jsonTokenObj=new JSONObject(jObj.get("result").toString());
						token=(String)jsonTokenObj.get("token");
						System.out.println("----Second Async Calll---");
						new AsyncUtilies.SecondAsyn(token,sharedpreferences.getString("username", ""),getLoginCompleteListener()).execute();

					}



				}
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
		};
		return crmListenerGetChallenge;
	}
	private CRMListener getLoginCompleteListener()
	{
		CRMListener crmListenerLoginCompleteListener=new CRMListener()
		{
			@Override
			public void getCAllback(String result) 
			{
				System.out.println("----value in getLoginCompleteListener getCallBacok Method  --- "+result);

				try 
				{
					JSONObject jObj=new JSONObject(result);
					String successStatus=jObj.get("success").toString();
					if(successStatus.equalsIgnoreCase("true"))
					{
						JSONObject jsonTokenObj=new JSONObject(jObj.get("result").toString());
						session=(String)jsonTokenObj.get("sessionName");
						userId=(String)jsonTokenObj.get("userId");
						System.out.println("----Third Async Calll---");
						String status ="";
						if(changeStatus.getSelectedItem()!=null)
								status = changeStatus.getSelectedItem().toString();
						new AsyncUtilies.ThirdAsyn(pinCode,
								name,session,getDataCompleteListener(),fromWhere,mobileNo,status).execute();
					}
					else
					{
						progBar.setVisibility(View.GONE);
						Toast toast=Toast.makeText(getActivity(), "Internal Server issue please try again later !!!",Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}

				}
				catch (JSONException e) 
				{
					e.printStackTrace();
				}


				//				new AsyncUtilies.postingSellerDataAsyn(sharedpreferences.getString("username", ""),
				//						session,newSeller,getFinalResultListener(),userId).execute();
				//				(String uPin,String nam,String paramert,CRMListener listener,String frm,String mob) 

			}
		};
		return crmListenerLoginCompleteListener;
	}

	private CRMListener getDataCompleteListener() 
	{
		CRMListener crmListener = new CRMListener() 
		{
			@Override
			public void getCAllback(String obj) 
			{
				//TODO				 final result
				String successStatus="",inputtedUserPassword="",userPass="",temp="",userid="";
				//				progressBar.setVisibility(View.GONE);
				JSONObject resultObj=null;
				LeadsData newLead=null;
				String topup="";
				int newTopup=0;
				try 
				{

					JSONObject jObj=new JSONObject(obj);
					successStatus=jObj.get("success").toString();
					if(successStatus.equalsIgnoreCase("true"))
					{	
						JSONArray resultArray=jObj.getJSONArray("result");
						if(!resultArray.isNull(0))
						{	
							DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
							DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
							String dat="";
							Date date=null;
							for(int i=0;i<resultArray.length();i++)
							{
								resultObj=resultArray.getJSONObject(i);
								//newSeller=new LeadsData();


								newLead=new LeadsData();
								newLead.setLeadid(resultObj.getString("id"));
								newLead.setEmail(resultObj.getString("email"));
								newLead.setLead_no(resultObj.getString("lead_no"));
								newLead.setBusinessname(resultObj.getString("company"));
								newLead.setLeadstatus(resultObj.getString("leadstatus"));
								//newLead.setReason_lead_status(resultObj.getString("reason_lead_status"));
								newLead.setReason_lead_status(resultObj.getString("description"));
								newLead.setLeadsource(resultObj.getString("leadsource"));
								try {
									date = inputFormat.parse(resultObj.getString("appt_date"));
									dat	= outputFormat.format(date);
								} catch (ParseException e) {
									e.printStackTrace();
								} 
								newLead.setAppt_date(dat);
								newLead.setAppt_time(resultObj.getString("appt_time"));
								newLead.setCity(resultObj.getString("city"));
								newLead.setCode(resultObj.getString("code"));
								newLead.setState(resultObj.getString("state"));
								newLead.setMobile(resultObj.getString("mobile"));
								newLead.setAddress(resultObj.getString("lane"));
								newLead.setSmownerid(resultObj.getString("assigned_user_id"));
								newLead.setSmcreatorid(resultObj.getString("created_user_id"));
								newLead.setCreatedby(resultObj.getString("created_user_id"));
								newLead.setCreatedtime(resultObj.getString("createdtime"));
								searchSellerList.add(i,newLead);

							}
						}
					}
					//					SearchListingFragment searchFrag=new SearchListingFragment();
					//					Bundle args=new Bundle();
					//					//						System.out.println("------Value of sellItem in SellerFragment is--"+item.getTitle());
					//					args.putParcelableArrayList("sellerArraylist", searchSellerList);
					//					searchFrag.setArguments(args);
					//
					//					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction(); 
					//
					//					// Replace whatever is in the fragment_container view with this fragment,and add the transaction to the back stack so the user can navigate back 
					//					transaction.replace(R.id.container, searchFrag);
					//					progBar.setVisibility(View.GONE);
					//
					//					// Commit the transaction 
					//					transaction.commit();
					((LeadsListener) getTargetFragment()).onLeadsComplete(searchSellerList);
					//					searchResult.onSearchComplete(searchSellerList);
					getDialog().dismiss();
				}

				catch (JSONException e) 
				{
					e.printStackTrace();
					getDialog().dismiss();
				}
			}
		};
		return crmListener;
	}

	private CRMListener getFinalResultListener()
	{
		CRMListener crmListenerFinalResultListener=new CRMListener()
		{
			@Override
			public void getCAllback(String result) 
			{
				progressBar.setVisibility(View.GONE);
				try
				{
					JSONObject jObj=new JSONObject(result);
					String successStatus=jObj.getString("success");

					System.out.println("---getFinalResultListener=====result==="+successStatus);
					//				    Bundle args = new Bundle();
					//				    args.putString("status", successStatus);
					//				    f.setArguments(args);
					if(successStatus.equalsIgnoreCase("true"))
					{
						//						DatabaseHandler db=new DatabaseHandler(ctx);
						//						db.updateSellerStatus((int)id);
					}
				}
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
		};
		return crmListenerFinalResultListener;
	}
	public class MyOnItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

			//check which spinner triggered the listener
			switch (parent.getId()) {
			case R.id.editTextPinNo:
				if(editTxtPinNo.getSelectedItemPosition()!=0)
					checkBxPinNo.setChecked(true);
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing.
		}
	}
}