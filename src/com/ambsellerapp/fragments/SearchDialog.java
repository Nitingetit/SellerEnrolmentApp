package com.ambsellerapp.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.ambsellerapp.adapters.NothingSelectedSpinnerAdapter;
import com.ambsellerapp.asynctasks.AsyncUtilies;
import com.ambsellerapp.database.DatabaseHandler;
import com.ambsellerapp.listeners.CRMListener;
import com.ambsellerapp.listeners.SearchListener;
import com.ambsellerapp.modals.NewSeller;
import com.AMBSEA.R;
import com.ambsellerapp.utilies.NetworkUtils;
import com.ambsellerapp.utilies.Utility;

public class SearchDialog extends DialogFragment implements
OnItemClickListener {


	private Button btnSearch;
	private EditText editTxtMobile=null,editTxtName=null;
	private Spinner editTxtPinNo;
	private CheckBox checkBxPinNo,checkBxMobile,checkBxName;
	private View view;
	private boolean isNameChecked=false,isMobileCheck=false,enteredDetails=false;
	private ProgressBar progBar;
	private boolean checkInternet = false;
	private SharedPreferences sharedpreferences;
	private String successStatus;
	private long id=0;
	private ProgressBar progressBar;
	private String session="",userId="";
	private String pinCode="",mobileNo="",name="",fromWhere="search";
	private ArrayList<NewSeller> searchSellerList=new ArrayList<NewSeller>();
	private RadioGroup radioLayout;
	private LinearLayout pinLayout,mobileLayout,nameLayout;
	private String btnMode="online";
	private ArrayList<String> arrayPinCode = new ArrayList<String>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{

		view = inflater.inflate(com.AMBSEA.R.layout.search_layout, null, false);
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
	private void settingIds()
	{
		btnSearch=(Button)view.findViewById(R.id.searchButton);
		editTxtPinNo= (Spinner) view.findViewById(R.id.editTextPinNo);
		editTxtMobile= (EditText) view.findViewById(R.id.editTxtMobile);
		editTxtName= (EditText) view.findViewById(R.id.editTxtName);
		checkBxPinNo= (CheckBox) view.findViewById(R.id.pinCheckBox);
		checkBxMobile= (CheckBox) view.findViewById(R.id.mobileCheckBox);
		checkBxName= (CheckBox) view.findViewById(R.id.nameCheckBox);
		progBar=(ProgressBar)view.findViewById(R.id.progressBarSearchDialog);
		checkInternet = NetworkUtils.isNetworkAvailable(getActivity());
		sharedpreferences = getActivity().getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		pinLayout=(LinearLayout)view.findViewById(R.id.pinNoLayout);
		mobileLayout=(LinearLayout)view.findViewById(R.id.mobileNoLayout);
		nameLayout=(LinearLayout)view.findViewById(R.id.nameLayout);
		radioLayout=(RadioGroup)view.findViewById(R.id.radio_layout);
		radioLayout.setSelected(true);

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

		radioLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
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
		});

		btnSearch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				enteredDetails=checkEnteredDetails();
				if(radioLayout.getCheckedRadioButtonId()==R.id.online_radio_button)
				{
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
				}
				else
				{///If offline radio button is selected than below code works TODO
					DatabaseHandler db=new DatabaseHandler(getActivity());
					btnMode="offline";
					searchSellerList=(ArrayList<NewSeller>) db.getNotSyncedSellers();
					((SearchListener) getTargetFragment()).onSearchComplete(searchSellerList);
					getDialog().dismiss();
				}

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
					if(checkInternet){
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
					else{
						getDialog().dismiss();
						Toast toast=Toast.makeText(getActivity(), "Please check Internet connectivity!!!",Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
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
						new AsyncUtilies.ThirdAsyn(pinCode,
								name,session,getDataCompleteListener(),fromWhere,mobileNo).execute();
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
				NewSeller newSeller=null;
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
							for(int i=0;i<resultArray.length();i++)
							{
								resultObj=resultArray.getJSONObject(i);
								newSeller=new NewSeller();


								newSeller.setBussiness_name(resultObj.getString("accountname"));
								newSeller.setShipping_pin_code(resultObj.getString("ship_code"));
								newSeller.setBilling_pin_code(resultObj.getString("bill_code"));
								newSeller.setBilling_address(resultObj.getString("bill_street"));
								newSeller.setShipping_address(resultObj.getString("ship_street"));//TODO ABHI 6th
								newSeller.setMobile_no(resultObj.getString("phone"));
								newSeller.setShipping_state(resultObj.getString("ship_state"));
								newSeller.setBilling_state(resultObj.getString("bill_state"));
								newSeller.setShipping_city(resultObj.getString("ship_city"));
								newSeller.setBilling_city(resultObj.getString("bill_city"));
								newSeller.setEmail_id(resultObj.getString("email1"));
								//								newSeller.setId(resultObj.getInt("assigned_user_id"));
								newSeller.setBeneficaryName(resultObj.getString("beneficary_name"));
								newSeller.setAccount_no(resultObj.getString("ac_no"));
								newSeller.setBank_name(resultObj.getString("bank_name"));
								newSeller.setBranch(resultObj.getString("branch_address"));
								newSeller.setIfsc_code(resultObj.getString("ifsc_code"));
								newSeller.setLocation(resultObj.getString("location"));
								newSeller.setLatitude(resultObj.getString("latitude"));
								newSeller.setLongitude(resultObj.getString("longitude"));
								newSeller.setMobile_verified(resultObj.getString("mobile_verified"));
								newSeller.setCst_tin_number(resultObj.getString("cst_tin_no"));
								try
								{
									topup=resultObj.getString("topup_amount");
									if(topup.equalsIgnoreCase(""))
									{
										topup="0";
										newTopup=Integer.parseInt(topup);
									}
									else
									{
										float f=Float.parseFloat(topup);
										newTopup=Math.round(f);
									}
								} 
								catch (NumberFormatException e) 
								{
									e.printStackTrace();
								}

								newSeller.setTopup_amount(newTopup);
								newSeller.setCrm_seller_id(resultObj.getString("id"));///Adding these parameter for updating the already existing Seller	

								searchSellerList.add(i,newSeller);

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
					((SearchListener) getTargetFragment()).onSearchComplete(searchSellerList);
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