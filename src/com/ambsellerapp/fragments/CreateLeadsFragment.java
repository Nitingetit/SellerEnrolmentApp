package com.ambsellerapp.fragments;

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

import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.AMBSEA.R;
import com.ambsellerapp.activities.LauncherActivity;
import com.ambsellerapp.adapters.NothingSelectedSpinnerAdapter;
import com.ambsellerapp.asynctasks.AsyncUtilies;
import com.ambsellerapp.asynctasks.LeadUtility;
import com.ambsellerapp.listeners.CRMListener;
import com.ambsellerapp.modals.NewLead;
import com.ambsellerapp.modals.NewSeller;
import com.ambsellerapp.ui.MyEditText;
import com.ambsellerapp.utilies.NetworkUtils;
import com.ambsellerapp.utilies.Utility;

public class CreateLeadsFragment extends Fragment {
	private View contentView;
	private Dialog dateandtime_picker;
	private Dialog lead_progress_bar_dialog;
	private Button date_time_set;
	private TimePicker leads_timep;
	private DatePicker leads_datep;
	private String hour, minute, month, day, year, second = "";
	private static final int CREATE_LEADS = 5;
	private boolean checkInternet = false;
	private SharedPreferences sharedpreferences;
	private ArrayList<String> arrayPinCode = new ArrayList<String>();
	private MyEditText lead_business_name;
	private MyEditText lead_mobile_nunber;
	private Spinner lead_pin_code;
	private MyEditText lead_edittext_pin_code;
	private MyEditText lead_edittext_date_and_time;
	private MyEditText lead_city;
	private MyEditText lead_state;
	private MyEditText lead_address;
	private MyEditText lead_remarks;
	private Button lead_select_date_time;
	private TextView lead_time;
	private TextView lead_date;
	private Button lead_reset;
	private Button lead_submit;
	private String pincode;
	JSONObject location1_obj = null;
	JSONObject location2_obj = null;
	private String lead_json_State = "";
	private String lead_json_City = "";
	private static int select_pointer = 0;
	private String lead_status = "";
	private String lead_adminSession = "";
	private String id = "";
	NewLead newLead = new NewLead();
	private NewLead myLead;
	private String fromWhere="";
	private String sessionUserName;
	private String assignedUserId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.findItem(R.id.registration).setVisible(true).setEnabled(true);
		menu.findItem(R.id.sellerList).setVisible(true).setEnabled(true);
		menu.findItem(R.id.create_leads).setVisible(false).setEnabled(false);
		menu.findItem(R.id.refresh).setVisible(false).setEnabled(false);
		menu.findItem(R.id.search).setVisible(false).setEnabled(false);
		menu.findItem(R.id.leads).setVisible(true).setEnabled(true);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.create_leads, container, false);
		getActivity().getActionBar().show();
		
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		((LauncherActivity) getActivity()).currentFragment = CREATE_LEADS;
		getActivity().getActionBar().setDisplayShowCustomEnabled(false);
		getActivity().getActionBar().setDisplayShowTitleEnabled(true);
		getActivity().getActionBar().setTitle("Create Lead");

		// checkInternet = NetworkUtils.isNetworkAvailable(getActivity());
		sharedpreferences = getActivity().getSharedPreferences(
				Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		settingIds();

		Bundle extras = getArguments(); 
		if (extras != null) 
		{
			myLead=(NewLead) extras.getSerializable("leadsInfo");
			fromWhere=extras.getString("from");

		}
		if(fromWhere.equalsIgnoreCase("moreinfo_leads")){
			com.ambsellerapp.activities.LauncherActivity.fromLead=2;
			lead_business_name.setText(myLead.getBusiness_name());
			lead_business_name.setEnabled(false);
			lead_mobile_nunber.setText(myLead.getMobile_number());
			lead_mobile_nunber.setEnabled(false);
			arrayPinCode.add(myLead.getPin_code());
			getActivity().getActionBar().setTitle("Lead Details");
			ArrayAdapter<String> adapter_amount = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, arrayPinCode);
			adapter_amount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			lead_pin_code.setAdapter(adapter_amount);
			lead_pin_code.setEnabled(false);
			lead_state.setText(myLead.getState());
			lead_state.setEnabled(false);
			lead_city.setText(myLead.getCity());
			lead_city.setEnabled(false);
			lead_address.setText(myLead.getAddress());
			lead_remarks.setText(myLead.getRemarks());
			//lead_remarks.setEnabled(false);
			lead_time.setText(myLead.getTime().replace(":00", ""));
			lead_time.setEnabled(false);
			lead_date.setText(myLead.getDate());
			lead_date.setEnabled(false);
			
			newLead = myLead;
			//newLead.set
			lead_submit.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					/*newLead.setBusiness_name(lead_business_name.getText().toString());
					newLead.setMobile_number(lead_mobile_nunber.getText().toString());
					newLead.setPin_code(lead_pin_code.getSelectedItem().toString());
					newLead.setState(lead_state.getText().toString());
					newLead.setCity(lead_city.getText().toString());*/
					newLead.setAddress(lead_address.getText().toString());
					newLead.setRemarks(lead_remarks.getText().toString());
					/*newLead.setRemarks(lead_remarks.getText().toString());
					newLead.setDate(lead_date.getText().toString());
					newLead.setTime(lead_time.getText().toString());*/
					
					
					if(checkInternet){
						lead_progress_bar_dialog = new Dialog(getActivity());
						lead_progress_bar_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						lead_progress_bar_dialog.setContentView(R.layout.lead_progressbar);
						lead_progress_bar_dialog.setCancelable(false);
						lead_progress_bar_dialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
						lead_progress_bar_dialog.show();
						
						new LeadUtility.SendLeadDatatoCRMStep1(getLeadResponseStep1(), "").execute();
							}
					else{
						Toast toast=Toast.makeText(getActivity(), "Please Check Internet Connectivity !!!",Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
					
				}
			});
		}
		else{
			Set<String> set = sharedpreferences.getStringSet("pincodeList", null);
			arrayPinCode = new ArrayList<String>(set);
			Collections.sort(arrayPinCode);
			if (arrayPinCode.isEmpty()) {
				arrayPinCode.add("PIN Code not found");
			}

			ArrayAdapter<String> adapter_amount = new ArrayAdapter<String>(
					getActivity(), android.R.layout.simple_spinner_item,
					arrayPinCode);
			adapter_amount
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			lead_pin_code.setAdapter(new NothingSelectedSpinnerAdapter(
					adapter_amount, R.layout.contact_spinner_row_nothing_selected,
					getActivity()));

			lead_pin_code.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					System.out.println("---inside onItemSelected");

					if (position != 0) {
						select_pointer = position;
						lead_edittext_pin_code.setError(null);
						//lead_pin_code.getBackground().setAlpha(100);
						pincode = lead_pin_code.getSelectedItem().toString();

						if (checkInternet) {
							new LeadUtility.GetStateCity(getStateCity(), "122018")
									.execute();
						}
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});

			lead_select_date_time.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dateandtime_picker = new Dialog(getActivity());
					dateandtime_picker
							.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dateandtime_picker
							.setContentView(R.layout.date_and_time_picker);
					dateandtime_picker.setCancelable(true);
					// dateandtime_picker.setTitle(
					// Html.fromHtml("<font color='#3B3131' size='8' >Select Date and Time</font>"));
					leads_datep = (DatePicker) dateandtime_picker
							.findViewById(R.id.datePicker_leads);
					leads_timep = (TimePicker) dateandtime_picker
							.findViewById(R.id.timePicker_leads);
					date_time_set = (Button) dateandtime_picker
							.findViewById(R.id.btnSet_leads);
					date_time_set.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							month = String.valueOf(leads_datep.getMonth()+1);
							day = String.valueOf(leads_datep.getDayOfMonth());
							year = String.valueOf(leads_datep.getYear());
							hour = String.valueOf(leads_timep.getCurrentHour());
							minute = String.valueOf(leads_timep.getCurrentMinute());
							second = "00";
							if (month.length() == 1)
								month = "0" + month;
							if (day.length() == 1)
								day = "0" + day;
							if (hour.length() == 1)
								hour = "0" + hour;
							if (minute.length() == 1)
								minute = "0" + minute;
							if(!datetime_check("Time : " + hour + ":" + minute,"Date : " + day + "-" + month + "-"
									+ year))
							{
								if(!amPMCheckTime("Time : " + hour + ":" + minute))
								{
									lead_time.setText("Time : " + hour + ":" + minute);
									lead_date.setText("Date : " + day + "-" + month + "-"
											+ year );
									dateandtime_picker.dismiss();
								}
								else
								{
									Toast.makeText(getActivity(), " Select time in between 9 AM to 9 PM !!!",Toast.LENGTH_LONG).show();
								}
							}
							else
							{
								Toast.makeText(getActivity(), " Could not select current/past day and time !!!",Toast.LENGTH_LONG).show();
							}
//							lead_edittext_date_and_time.setError(null);
						}
					});
					dateandtime_picker.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
					dateandtime_picker.show();

				}
			});

			// error icon focus listeners

			lead_business_name
					.setOnFocusChangeListener(new OnFocusChangeListener() {

						public void onFocusChange(View v, boolean hasFocus) {
							lead_business_name.setError(null);

						}
					});

			lead_mobile_nunber
					.setOnFocusChangeListener(new OnFocusChangeListener() {

						public void onFocusChange(View v, boolean hasFocus) {
							lead_mobile_nunber.setError(null);

						}
					});

			lead_state.setOnFocusChangeListener(new OnFocusChangeListener() {

				public void onFocusChange(View v, boolean hasFocus) {
					lead_state.setError(null);

				}
			});

			lead_city.setOnFocusChangeListener(new OnFocusChangeListener() {

				public void onFocusChange(View v, boolean hasFocus) {
					lead_city.setError(null);

				}
			});

			lead_address.setOnFocusChangeListener(new OnFocusChangeListener() {

				public void onFocusChange(View v, boolean hasFocus) {
					lead_address.setError(null);

				}
			});

			lead_remarks.setOnFocusChangeListener(new OnFocusChangeListener() {

				public void onFocusChange(View v, boolean hasFocus) {
					lead_remarks.setError(null);

				}
			});

			lead_reset.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					CreateLeadsFragment newFragment = new CreateLeadsFragment();
					FragmentTransaction transaction = getActivity()
							.getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.container, newFragment);
					transaction.commit();
				}
			});

			lead_submit.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					if (checkEnteredDetails()) {
						if (checkInternet) {

							newLead.setBusiness_name(lead_business_name.getText()
									.toString());
							newLead.setMobile_number(lead_mobile_nunber.getText()
									.toString());
							newLead.setPin_code(lead_pin_code.getSelectedItem()
									.toString());
							newLead.setState(lead_state.getText().toString());
							newLead.setCity(lead_city.getText().toString());
							newLead.setAddress(lead_address.getText().toString());
							newLead.setRemarks(lead_remarks.getText().toString());
							newLead.setDate(lead_date.getText().toString());
							newLead.setTime(lead_time.getText().toString());
							lead_progress_bar_dialog = new Dialog(getActivity());
							lead_progress_bar_dialog
									.requestWindowFeature(Window.FEATURE_NO_TITLE);
							lead_progress_bar_dialog
									.setContentView(R.layout.lead_progressbar);
							lead_progress_bar_dialog.setCancelable(false);
							lead_progress_bar_dialog.getWindow().setLayout(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT);
							
							String firstCharacter=lead_mobile_nunber.getText().toString();
							firstCharacter=firstCharacter.substring(0,1);
							if(firstCharacter.equalsIgnoreCase("9") || firstCharacter.equalsIgnoreCase("8") || firstCharacter.equalsIgnoreCase("7"))
							{
								lead_progress_bar_dialog.show();
								new LeadUtility.SendLeadDatatoCRMStep1(getLeadResponseStep1(), "").execute();
							}
							else
							{
								Toast toast=Toast.makeText(getActivity(), "Phone must start with 9,8,7!!!",Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}
							
						} else {
							Toast.makeText(getActivity(),
									"Please Check Your Internet Connection !!!",
									Toast.LENGTH_LONG).show();
						}
					}
				}
			});
		}
		

		return contentView;
	}

	private boolean checkEnteredDetails() {
		String toastMessage="";
		if (lead_business_name.getText().toString().trim().equals("")) {
			lead_business_name.setFocusableInTouchMode(true);
			lead_business_name.requestFocus();
//			lead_business_name.setError("");
			toastMessage="Bussiness Name";
//			return false;
		} 
		if (lead_mobile_nunber.getText().toString().trim().equals("")
				|| lead_mobile_nunber.getText().toString().length() < 10) {
			lead_mobile_nunber.setFocusableInTouchMode(true);
			lead_mobile_nunber.requestFocus();
//			lead_mobile_nunber.setError("");
			if(!toastMessage.equalsIgnoreCase(""))
				toastMessage=toastMessage+", Mobile";
			else
				toastMessage=toastMessage+" Mobile";
//			return false;
		} 
		if (select_pointer == 0) {
			lead_pin_code.getBackground().setAlpha(70);
			lead_edittext_pin_code.getBackground().setAlpha(30);
			lead_pin_code.setFocusableInTouchMode(true);
			lead_pin_code.requestFocus();
//			lead_edittext_pin_code.setError("");
			if(!toastMessage.equalsIgnoreCase(""))
				toastMessage=toastMessage+", Pincode";
			else
				toastMessage=toastMessage+" Pincode";
//			return false;
		} 
		if (lead_state.getText().toString().trim().equals("")) {
			lead_state.setFocusableInTouchMode(true);
			lead_state.requestFocus();
//			lead_state.setError("");
			if(!toastMessage.equalsIgnoreCase(""))
				toastMessage=toastMessage+", State";
			else
				toastMessage=toastMessage+" State";
//			return false;
		}
		if (lead_city.getText().toString().trim().equals("")) {
			lead_city.setFocusableInTouchMode(true);
			lead_city.requestFocus();
//			lead_city.setError("");
			if(!toastMessage.equalsIgnoreCase(""))
				toastMessage=toastMessage+", City";
			else
				toastMessage=toastMessage+" City";
			
//			return false;
		} /*else if (lead_address.getText().toString().trim().equals("")) {
			lead_address.setFocusableInTouchMode(true);
			lead_address.requestFocus();
			lead_address.setError("");
			return false;
		}*/ 
		if (lead_remarks.getText().toString().trim().equals("")) {
			lead_remarks.setFocusableInTouchMode(true);
			lead_remarks.requestFocus();
//			lead_remarks.setError("");
			if(!toastMessage.equalsIgnoreCase(""))
				toastMessage=toastMessage+", Remarks";
			else
				toastMessage=toastMessage+" Remarks";
//			return false;
		} 
		if (lead_time.getText().toString().trim().equals("")
				|| lead_date.getText().toString().trim().equals("") ) {
			lead_select_date_time.setFocusableInTouchMode(true);
			lead_select_date_time.requestFocus();			
//			lead_edittext_date_and_time.setError("");
			if(!toastMessage.equalsIgnoreCase(""))
				toastMessage=toastMessage+", Date & Time";
			else
				toastMessage=toastMessage+" Date & Time";
//			return false;
		}

		if(!toastMessage.equalsIgnoreCase(""))
		{
			Toast.makeText(getActivity(), toastMessage+" should not be empty !!!",Toast.LENGTH_LONG).show();
			return false;
		}
		else
		{
			return true;
		}
	}
	private boolean amPMCheckTime(String timSelected)
	{
		Date todaydate=new Date();
		//todaydate=subtractDay(todaydate);
		try{
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			String selectedtime=timSelected;
			selectedtime=selectedtime.replace("Time : ", "")+":00";
			Date d = df.parse(selectedtime); 
			Date d1 = df.parse("09:00"); 
			Date d2 = df.parse("21:00"); 
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.add(Calendar.MINUTE, 60);
			String moreTime = df.format(cal.getTime());
			if(d.before(d1) || d.after(d2)){
				return true;
			}
		}catch(Exception ex){
			return true;
		}
		return false;
	}

	private boolean datetime_check(String timSelected,String datSelected){
		Date todaydate=new Date();
		Date d1=null;
		Date t1=null;
		String selecteddate=datSelected;
		selecteddate=selecteddate.replace("Date : ", "");
		String selectedtime=timSelected;
		selectedtime=selectedtime.replace("Time : ", "")+":00";
//		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");		
//		SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss");
		SimpleDateFormat sdf3 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");	///Changes made by ABHI on 5th March
		try {
			d1=sdf3.parse(selecteddate+" "+selectedtime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("TIME",todaydate.toString());
		Log.d("TIME", d1.toString());
		
		if(d1.before(todaydate)){
			return true;
		}
		return false;		
	}
	
	
	private void settingIds() {
		lead_business_name = (MyEditText) contentView
				.findViewById(R.id.edittext_leads_businessname);
		lead_mobile_nunber = (MyEditText) contentView
				.findViewById(R.id.edittext_leads_mobileno);
		lead_pin_code = (Spinner) contentView
				.findViewById(R.id.spinner_leads_pincode);
		lead_city = (MyEditText) contentView
				.findViewById(R.id.edittext_leads_city);
		lead_state = (MyEditText) contentView
				.findViewById(R.id.edittext_leads_state);
		lead_edittext_pin_code = (MyEditText) contentView
				.findViewById(R.id.edittext_leads_pincode);
		lead_edittext_date_and_time = (MyEditText) contentView
				.findViewById(R.id.edittext_leads_select_date_and_time);
		lead_address = (MyEditText) contentView
				.findViewById(R.id.edittext_leads_address);
		lead_remarks = (MyEditText) contentView
				.findViewById(R.id.edittext_leads_remarks);
		lead_select_date_time = (Button) contentView
				.findViewById(R.id.button_leads_date_and_time);
		lead_time = (TextView) contentView
				.findViewById(R.id.textview_leads_time);
		lead_date = (TextView) contentView
				.findViewById(R.id.textview_leads_date);
		lead_reset = (Button) contentView.findViewById(R.id.button_leads_reset);
		lead_submit = (Button) contentView
				.findViewById(R.id.button_leads_submit);
		checkInternet = NetworkUtils.isNetworkAvailable(getActivity());
	}

	public CRMListener getStateCity() {
		CRMListener city_state_crmListener = new CRMListener() {

			@Override
			public void getCAllback(String result) {
				// TODO Auto-generated method stub
				try {
					JSONObject jObj = new JSONObject(result);
					// State_city_status=jObj.get("locationinfo").toString();
					location1_obj = (JSONObject) jObj.get("locationinfo");
					location2_obj = (JSONObject) location1_obj
							.get("locationinfo");
					lead_json_City = location2_obj.getString("city");
					lead_json_State = location2_obj.getString("state");
					// Toast.makeText(getActivity(),
					// "City is "+lead_json_City+" and State is "+lead_json_State,Toast.LENGTH_LONG).show();
					lead_city.setText(lead_json_City);
					lead_state.setText(lead_json_State);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Log.d("MYSTATE", result);
			}

		};
		return city_state_crmListener;
	}

	private CRMListener getLeadResponseStep1() {

		CRMListener lead_response = new CRMListener() {
			String token = "";

			@Override
			public void getCAllback(String result) {
				try {
					JSONObject jObj = new JSONObject(result);
					lead_status = jObj.get("success").toString();
					if (lead_status.equalsIgnoreCase("true")) {
						JSONObject jsonTokenObj = new JSONObject(jObj.get(
								"result").toString());
						token = (String) jsonTokenObj.get("token");
						new LeadUtility.SendLeadDatatoCRMStep2(token,
								getLeadResponseStep2(), "").execute();
					}
					else{
						Toast.makeText(getActivity(), "Something Went Wrong !!!",Toast.LENGTH_LONG).show();
						lead_progress_bar_dialog.dismiss();
					}

				} catch (Exception leadexception1) {
					leadexception1.printStackTrace();
				}
			}
		};
		return lead_response;
	}

	private CRMListener getLeadResponseStep2() {

		CRMListener lead_response = new CRMListener() {
			@Override
			public void getCAllback(String result) {
				try {

					JSONObject jObj = new JSONObject(result);
					String successStatus = jObj.get("success").toString();
					if (successStatus.equalsIgnoreCase("true")) {
						JSONObject jsonTokenObj = new JSONObject(jObj.get(
								"result").toString());
						lead_adminSession = (String) jsonTokenObj
								.get("sessionName");
						new LeadUtility.SendLeadDatatoCRMStep3(
								sharedpreferences.getString("username", ""),
								lead_adminSession, getLeadResponseStep3())
								.execute();

					}
					else{
						Toast.makeText(getActivity(), "Something Went Wrong !!!",Toast.LENGTH_LONG).show();
						lead_progress_bar_dialog.dismiss();
					}
				} catch (Exception leadexception1) {
					leadexception1.printStackTrace();
				}

			}
		};
		return lead_response;
	}

	private CRMListener getLeadResponseStep3() {

		CRMListener lead_response = new CRMListener() {

			@Override
			public void getCAllback(String result) {
				try {
					JSONObject jObj = new JSONObject(result);
					lead_status = jObj.get("success").toString();
					if (lead_status.equalsIgnoreCase("true")) {
						JSONArray resultArray = jObj.getJSONArray("result");
						JSONObject resultObj = resultArray.getJSONObject(0);
						Utility.UserAPIAccessKey = resultObj
								.getString("accesskey");
						new LeadUtility.SendLeadDatatoCRMStep1(
								getLeadResponseStep4(),
								sharedpreferences.getString("username", ""))
								.execute();
					}
					else{
						Toast.makeText(getActivity(), "Something Went Wrong !!!",Toast.LENGTH_LONG).show();
						Log.d("MYLEAD", result);
						lead_progress_bar_dialog.dismiss();
					}
					
					// Toast.makeText(getActivity(),
					// result,Toast.LENGTH_LONG).show();
				} catch (Exception leadexception1) {
					leadexception1.printStackTrace();
				}

			}
		};
		return lead_response;
	}

	private CRMListener getLeadResponseStep4() {

		CRMListener lead_response = new CRMListener() {
			String token = "";

			@Override
			public void getCAllback(String result) {
				try {
					JSONObject jObj = new JSONObject(result);
					lead_status = jObj.get("success").toString();
					if (lead_status.equalsIgnoreCase("true")) {
						JSONObject jsonTokenObj = new JSONObject(jObj.get(
								"result").toString());
						token = (String) jsonTokenObj.get("token");
						new LeadUtility.SendLeadDatatoCRMStep2(token,
								getLeadResponseStep5(),
								sharedpreferences.getString("username", ""))
								.execute();
					}
					else{
						Toast.makeText(getActivity(), "Something Went Wrong !!!",Toast.LENGTH_LONG).show();
						Log.d("MYLEAD", result);
						lead_progress_bar_dialog.dismiss();
					}
					
				} catch (Exception leadexception1) {
					leadexception1.printStackTrace();
				}

			}
		};
		return lead_response;
	}

	private CRMListener getLeadResponseStep5() {

		CRMListener lead_response = new CRMListener() {
			String sessionName = "", userid = "";

			@Override
			public void getCAllback(String result) {
				try {
					JSONObject jObj = new JSONObject(result);
					lead_status = jObj.get("success").toString();
					if (lead_status.equalsIgnoreCase("true")) {
						JSONObject jsonTokenObj = new JSONObject(jObj.get(
								"result").toString());
						sessionName = (String) jsonTokenObj.get("sessionName");
						sessionUserName = sessionName;
						
						userid = (String) jsonTokenObj.get("userId");
						assignedUserId = userid;
						Log.d("MYLEAD", "SessionName"+sessionName);
						Log.d("MYLEAD", "UserId"+userid);
						if(fromWhere.equalsIgnoreCase("moreinfo_leads"))
							new LeadUtility.SendLeadDatatoCRMStep4(sessionName,userid, newLead, getLeadResponseStep6(),"update").execute();
						else{
							//new LeadUtility.SendLeadDatatoCRMStep4(sessionName,userid, newLead, getLeadResponseStep6(),"create").execute();
							String myTime = newLead.getTime();
							 SimpleDateFormat df = new SimpleDateFormat("HH:mm");
							 Date d = df.parse(myTime); 
							 Calendar cal = Calendar.getInstance();
							 cal.setTime(d);
							 cal.add(Calendar.MINUTE, 60);
							 String moreTime = df.format(cal.getTime());
							 Calendar cal1 = Calendar.getInstance();
							 cal1.setTime(d);
							 cal1.add(Calendar.MINUTE, -60);
							 String lessTime = df.format(cal1.getTime());
							//(String uPin,String date,String frm,String appLess,String appMore,String paramert,CRMListener listener) 
							new AsyncUtilies.ThirdAsyn(newLead.getMobile_number(),
									newLead.getDate(),"allLeads",lessTime,moreTime,sessionUserName,getValidationStep()).execute();
						}
							//getValidationStep()
							//new LeadUtility.SendLeadDatatoCRMStep4(sessionName,userid, newLead, getLeadResponseStep6(),"create").execute();
					}
					else{
						Toast.makeText(getActivity(), "Something Went Wrong !!!",Toast.LENGTH_LONG).show();
						Log.d("MYLEAD", result);
						lead_progress_bar_dialog.dismiss();
					}
					
					// Toast.makeText(getActivity(),
					// result,Toast.LENGTH_LONG).show();
				} catch (Exception leadexception1) {
					leadexception1.printStackTrace();
				}

			}
		};
		return lead_response;
	}
	
	private CRMListener getValidationStep() {

		CRMListener lead_response = new CRMListener() {
			String sessionName = "", userid = "";

			@Override
			public void getCAllback(String result) {
				try {
					JSONObject jObj = new JSONObject(result);
					lead_status = jObj.get("success").toString();
					if (lead_status.equalsIgnoreCase("true")) {
						
						JSONArray resultArray=jObj.getJSONArray("result");
						if(resultArray.length()>0){
							Toast.makeText(getActivity(), "Mobile number already exist",Toast.LENGTH_LONG).show();
							Log.d("MYLEAD", result);
							lead_progress_bar_dialog.dismiss();
							//new LeadUtility.SendLeadDatatoCRMStep4(sessionName,userid, newLead, getLeadResponseStep6(),"create").execute();
						}
						else{
							new LeadUtility.SendLeadDatatoCRMStep4(sessionUserName,assignedUserId, newLead, getLeadResponseStep6(),"create").execute();
						}
					}
					
					else{
						Toast.makeText(getActivity(), "Something Went Wrong !!!",Toast.LENGTH_LONG).show();
						Log.d("MYLEAD", result);
						lead_progress_bar_dialog.dismiss();
					}
					
					// Toast.makeText(getActivity(),
					// result,Toast.LENGTH_LONG).show();
				} catch (Exception leadexception1) {
					leadexception1.printStackTrace();
				}

			}
		};
		return lead_response;
	}


	private CRMListener getLeadResponseStep6() {

		CRMListener lead_response = new CRMListener() {

			@Override
			public void getCAllback(String result) {
				try {
					JSONObject jObj = new JSONObject(result);
					lead_status = jObj.get("success").toString();
					if (lead_status.equalsIgnoreCase("true")) {
						JSONObject jsonTokenObj = new JSONObject(jObj.get(
								"result").toString());
						id = (String) jsonTokenObj.get("id");
						if (id.equals("")){
							Toast.makeText(getActivity(),
									"Sorry, Something went wrong !!!",
									Toast.LENGTH_LONG).show();
							lead_progress_bar_dialog.dismiss();
						}
						else {
							LeadsFragment newFragment = new LeadsFragment();
							FragmentTransaction transaction = getActivity()
									.getSupportFragmentManager()
									.beginTransaction();
							transaction.replace(R.id.container, newFragment);
							transaction.commit();
							lead_progress_bar_dialog.dismiss();
							if(fromWhere.equalsIgnoreCase("moreinfo_leads")){
								Toast toast=Toast.makeText(getActivity(), "Lead Updated Successfully !!!",Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}		
							else{
								Toast toast=Toast.makeText(getActivity(), "Lead Created Successfully !!!",Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}							
						}
					}
					else{
						Toast.makeText(getActivity(), "Sorry, Something Went Wrong !!!",Toast.LENGTH_LONG).show();
						Log.d("MYLEAD", result);
						lead_progress_bar_dialog.dismiss();
					}
					
					// Toast.makeText(getActivity(),
					// result,Toast.LENGTH_LONG).show();
				} catch (Exception leadexception1) {
					leadexception1.printStackTrace();
				}

			}
		};
		return lead_response;
	}
}
