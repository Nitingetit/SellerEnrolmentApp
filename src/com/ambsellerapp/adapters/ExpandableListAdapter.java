package com.ambsellerapp.adapters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.AMBSEA.R;
import com.ambsellerapp.asynctasks.LeadUtility;
import com.ambsellerapp.fragments.CreateLeadsFragment;
import com.ambsellerapp.fragments.LeadsFragment;
import com.ambsellerapp.fragments.NameRegistrationFragment;
import com.ambsellerapp.listeners.CRMListener;
import com.ambsellerapp.listeners.RefreshListener;
import com.ambsellerapp.modals.LeadsData;
import com.ambsellerapp.modals.NewLead;
import com.ambsellerapp.modals.NewSeller;
import com.ambsellerapp.ui.MyEditText;
import com.ambsellerapp.utilies.NetworkUtils;
import com.ambsellerapp.utilies.Utility;

public class ExpandableListAdapter extends BaseExpandableListAdapter implements OnItemSelectedListener
{
	private ArrayList<LeadsData> list;
	private LayoutInflater mInflator;
	private Context mCtx;
	private TimePicker leads_timep;
	private DatePicker leads_datep;
	//private Button date_time_set;
	private String hour, minute, month, day, year, second = "";
	//private Spinner changeStatus;
	//private Dialog dateandtime_picker;
	//private MyEditText Reason_editText;
	//private LinearLayout layoutReschedule;
	//private Button rescheduleButton,saveButton,enterDetails;;
	//private TextView Rescheduled_date,Rescheduled_time,leadsBussinessName,address,leadPhone,topupAmount,leadsBillPinCode,leadsShipPinCode,leadsRemark,leadsStatus,leadDate,leadTime;	
	//private LinearLayout MainDropDownLayoutsetStatus=null,Layoutreason=null;
	private int mLastPosition;
	//private String[] statusSpinnerItems = {"Convert","Not Interested","Invalid","Re-scheduele"};
	private RefreshListener ref;
	private LeadsData leadsInfo;
	NewLead newLead = new NewLead();
	private boolean checkInternet = false;
	private String lead_status,lead_adminSession;
	private SharedPreferences sharedpreferences;
	private String id="";
	private ActionBarActivity mActivity;
	private Dialog lead_progress_bar_dialog;

	public ExpandableListAdapter(Context ctx,
			ArrayList<LeadsData> leadsArraylist, ActionBarActivity activity,RefreshListener refreshListener) {
		// TODO Auto-generated constructor stub
		
		list = leadsArraylist;		
		Log.d("Leads", "@Abhi::Context:: " + ctx);
		mCtx=ctx;
		sharedpreferences = mCtx.getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		mInflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ref=refreshListener;
		mActivity = activity;
	}

	@Override
	public int getGroupCount()
	{
		// TODO Auto-generated method stub
		 return this.list.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) 
	{
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) 
	{
		// TODO Auto-generated method stub
		  return this.list.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) 
	{
		// TODO Auto-generated method stub
		Log.d("MSG", String.valueOf(childPosition));
		LeadsData item = (LeadsData)list.get(childPosition);
		return item;
	}

	@Override
	public long getGroupId(int groupPosition) 
	{
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) 
	{
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public boolean hasStableIds() 
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) 
	{
		// TODO Auto-generated method stub
		View view = convertView;
		if(view==null)
		{
			view=mInflator.inflate(com.AMBSEA.R.layout.leads_item,null);
			//holder=new ViewHolder();

			
//			holder = new ViewHolder(holder.leadsBussinessName, holder.leadsBillPinCode,holder.leadsRemark, holder.address, holder.leadsStatus
//					, holder.leadDate, holder.leadTime, null, null, null, null, null, null);
			//			holder.leadDate=(TextView)view.findViewById(R.id.lead_date_textview);
			//			holder.leadDate=(TextView)view.findViewById(R.id.lead_date_textview);

		}
		
		TextView leadsBussinessName=(TextView)view.findViewById(R.id.leadNametextview);
		TextView leadsBillPinCode=(TextView)view.findViewById(R.id.leadpincodetextview);
		TextView leadsRemark=(TextView)view.findViewById(R.id.leadRemarkstextview);
		TextView leadsStatus=(TextView)view.findViewById(R.id.lead_status_textview);
		TextView leadDate=(TextView)view.findViewById(R.id.lead_date_textview);
		TextView leadTime=(TextView)view.findViewById(R.id.lead_time_textview);
		TextView leadPhone=(TextView)view.findViewById(R.id.leadNumbertextview);
		TextView moreinfo=(TextView) view.findViewById(R.id.leadmoreinfotextview);
	
		final LeadsData leadsInfo= list.get(groupPosition);
		leadsBussinessName.setText(leadsInfo.getBusinessname());
		leadsBillPinCode.setText("PIN Code :"+leadsInfo.getCode());
		leadsRemark.setText("Remarks :"+leadsInfo.getReason_lead_status());
		leadsStatus.setText(leadsInfo.getLeadstatus());
		leadDate.setText(leadsInfo.getAppt_date());
		try{
			   /*String dt = "";
			   Date date = new Date();
			     if(leadsInfo.getAppt_date()!=null && !StringUtils.isEmpty(leadsInfo.getAppt_date())){
			      dt = leadsInfo.getAppt_date();
			     }
			     else{
			      dt = date.toString();
			     }*/
			   String input = leadsInfo.getAppt_date()+" "+leadsInfo.getAppt_time();
			   DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			   DateFormat outputFormat = new SimpleDateFormat("KK:mm a");
			   //System.out.println(outputFormat.format(inputFormat.parse(input)));
			   String time = outputFormat.format(inputFormat.parse(input));
			   if(time.indexOf("00")==0)
			    time = time.replace("00","12");
			   leadTime.setText(time);
			  }catch(Exception ex){
			   System.out.println(ex.getMessage());
			   leadTime.setText(leadsInfo.getAppt_time());
			  }
		if(leadsInfo.getMobile()!=null)
			leadPhone.setText(leadsInfo.getMobile());
		
		leadPhone.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:" +leadsInfo.getMobile()));
				mCtx.startActivity(intent); 
			}
		});
		
		moreinfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CreateLeadsFragment newFragment = new CreateLeadsFragment();
				NewLead myLead = convertToLNewLead(leadsInfo);
				Bundle args = new Bundle();
				args.putString("from", "moreinfo_leads");
				args.putSerializable("leadsInfo", myLead);
				newFragment.setArguments(args);
				FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.container, newFragment);
				transaction.addToBackStack("LeadsFragment");
				transaction.commit();
			}
		});
		
		return view;
	}

	@Override
	public View getChildView(final int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		
		checkInternet = NetworkUtils.isNetworkAvailable(mCtx);
	    if (convertView == null) {
	        LayoutInflater infalInflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        convertView = infalInflater.inflate(R.layout.main_dashboard, null);
	    }
	    final	Spinner changeStatus=(Spinner)convertView.findViewById(R.id.leadstatusSpinner);
		final	Button saveButton=(Button)convertView.findViewById(R.id.leadSaveButton);
		final	Button rescheduleButton=(Button) convertView.findViewById(R.id.rescheduleButton);
		final	LinearLayout Layoutreason=(LinearLayout)convertView.findViewById(R.id.LayoutReason);
		final	LinearLayout layoutReschedule=(LinearLayout) convertView.findViewById(R.id.LayoutReschedule);
		final	TextView Rescheduled_date=(TextView) convertView.findViewById(R.id.Reschedule_date);
		final	TextView Rescheduled_time=(TextView) convertView.findViewById(R.id.Reschedule_time);
		final	MyEditText Reason_editText=(MyEditText) convertView.findViewById(R.id.reason_editText);
		String[] statusSpinnerItems = {"Convert","Not Interested-Met","Not Interested-Unmet","Invalid","Re-schedule"};
		
		//final Dialog dateandtime_picker;
			
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
					mCtx, android.R.layout.simple_spinner_item,
					statusSpinnerItems);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			changeStatus.setAdapter(new NothingSelectedSpinnerAdapter(
					dataAdapter, R.layout.contact_spinner_row_nothing_selected,
					mCtx));
			
			Reason_editText.setOnFocusChangeListener(new OnFocusChangeListener() {

				public void onFocusChange(View v, boolean hasFocus) {
					Reason_editText.setError(null);

				}
			});
			
			
			changeStatus.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					if(position>0){
						if(changeStatus.getSelectedItemPosition()==1){
							rescheduleButton.setVisibility(view.GONE);
							layoutReschedule.setVisibility(view.GONE);
							Layoutreason.setVisibility(view.GONE);
							saveButton.setVisibility(view.VISIBLE);
							saveButton.setText("Enter Details");
							saveButton.setOnClickListener(new OnClickListener() 
							{
								@Override
								public void onClick(View v) 
								{
									NewSeller newSeller = convertLeadToSeller(list.get(groupPosition));
									NewLead myLead = convertToLNewLead(list.get(groupPosition));
					                NameRegistrationFragment newFragment = new NameRegistrationFragment();
					                Bundle args = new Bundle();
					                args.putString("from", "leads");
					                args.putSerializable("sellerArraylist", newSeller);
					                args.putSerializable("leadsInfo", myLead);
					                newFragment.setArguments(args);
							         
							         FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
							         transaction.replace(R.id.container, newFragment);
							         transaction.addToBackStack("LeadsFragment"); 
							         transaction.commit();
							         
								}
							});
							}
					    if(changeStatus.getSelectedItemPosition()==2){
					    	
							rescheduleButton.setVisibility(view.GONE);
							layoutReschedule.setVisibility(view.GONE);
							Layoutreason.setVisibility(view.VISIBLE);
							saveButton.setVisibility(view.VISIBLE);
							saveButton.setText("Save");
							saveButton.setOnClickListener(new OnClickListener() 
							{
								@Override
								public void onClick(View v) 
								{
									LeadsData leadsInfo2 = list.get(groupPosition);
									if (Reason_editText.getText().toString().trim().equals("")) {
										Reason_editText.setFocusableInTouchMode(true);
										Reason_editText.requestFocus();
										Reason_editText.setError("");
									}
									else{
										
										newLead.setBusiness_name(leadsInfo2.getBusinessname());
										newLead.setMobile_number(leadsInfo2.getMobile());
										newLead.setPin_code(leadsInfo2.getCode());
										newLead.setState(leadsInfo2.getState());
										newLead.setId(leadsInfo2.getLeadid());
										newLead.setCity(leadsInfo2.getCity());
										newLead.setAddress(leadsInfo2.getBranch_address());
										newLead.setRemarks(Reason_editText.getText().toString());
										newLead.setStatus(changeStatus.getSelectedItem().toString());
										newLead.setTime(leadsInfo2.getAppt_time());
										newLead.setDate(leadsInfo2.getAppt_date());
										if(checkInternet){
											lead_progress_bar_dialog = new Dialog(mCtx);
											lead_progress_bar_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
											lead_progress_bar_dialog.setContentView(R.layout.lead_progressbar);
											lead_progress_bar_dialog.setCancelable(false);
											lead_progress_bar_dialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
											lead_progress_bar_dialog.show();
											new LeadUtility.SendLeadDatatoCRMStep1(getLeadResponseStep1(), "").execute();
												}
										else{
											Toast toast=Toast.makeText(mActivity, "Please Check Your Internet Conection !!!",Toast.LENGTH_SHORT);
											toast.setGravity(Gravity.CENTER, 0, 0);
											toast.show();
										}
											}
								}
							});
							}
					    if(changeStatus.getSelectedItemPosition()==3){
					    	
							rescheduleButton.setVisibility(view.GONE);
							layoutReschedule.setVisibility(view.GONE);
							Layoutreason.setVisibility(view.VISIBLE);
							saveButton.setVisibility(view.VISIBLE);
							saveButton.setText("Save");
							saveButton.setOnClickListener(new OnClickListener() 
							{
								@Override
								public void onClick(View v) 
								{
									LeadsData leadsInfo2 = list.get(groupPosition);
									if (Reason_editText.getText().toString().trim().equals("")) {
										Reason_editText.setFocusableInTouchMode(true);
										Reason_editText.requestFocus();
										Reason_editText.setError("");
									}
									else{
										
										newLead.setBusiness_name(leadsInfo2.getBusinessname());
										newLead.setMobile_number(leadsInfo2.getMobile());
										newLead.setPin_code(leadsInfo2.getCode());
										newLead.setState(leadsInfo2.getState());
										newLead.setId(leadsInfo2.getLeadid());
										newLead.setCity(leadsInfo2.getCity());
										newLead.setAddress(leadsInfo2.getBranch_address());
										newLead.setRemarks(Reason_editText.getText().toString());
										newLead.setStatus(changeStatus.getSelectedItem().toString());
										newLead.setTime(leadsInfo2.getAppt_time());
										newLead.setDate(leadsInfo2.getAppt_date());
										if(checkInternet){
											lead_progress_bar_dialog = new Dialog(mCtx);
											lead_progress_bar_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
											lead_progress_bar_dialog.setContentView(R.layout.lead_progressbar);
											lead_progress_bar_dialog.setCancelable(false);
											lead_progress_bar_dialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
											lead_progress_bar_dialog.show();
											new LeadUtility.SendLeadDatatoCRMStep1(getLeadResponseStep1(), "").execute();
												}
										else{
											Toast toast=Toast.makeText(mActivity, "Please Check Your Internet Conection !!!",Toast.LENGTH_SHORT);
											toast.setGravity(Gravity.CENTER, 0, 0);
											toast.show();
										}
											}
								}
							});
							}
					  
					     if(changeStatus.getSelectedItemPosition()==4){
					    	 
							rescheduleButton.setVisibility(view.GONE);
							layoutReschedule.setVisibility(view.GONE);
							Layoutreason.setVisibility(view.VISIBLE);
							saveButton.setVisibility(view.VISIBLE);
							saveButton.setText("Save");
							saveButton.setOnClickListener(new OnClickListener() 
							{
								@Override
								public void onClick(View v) 
								{
									LeadsData leadsInfo3 = list.get(groupPosition);
									if (Reason_editText.getText().toString().trim().equals("")) {
										Reason_editText.setFocusableInTouchMode(true);
										Reason_editText.requestFocus();
										Reason_editText.setError("");
									}
									else{
										newLead.setBusiness_name(leadsInfo3.getBusinessname());
										newLead.setMobile_number(leadsInfo3.getMobile());
										newLead.setPin_code(leadsInfo3.getCode());
										newLead.setState(leadsInfo3.getState());
										newLead.setCity(leadsInfo3.getCity());
										newLead.setId(leadsInfo3.getLeadid());
										newLead.setAddress(leadsInfo3.getBranch_address());
										newLead.setRemarks(Reason_editText.getText().toString());
										newLead.setStatus(changeStatus.getSelectedItem().toString());
										newLead.setTime(leadsInfo3.getAppt_time());
										newLead.setDate(leadsInfo3.getAppt_date());
										if(checkInternet){
											lead_progress_bar_dialog = new Dialog(mCtx);
											lead_progress_bar_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
											lead_progress_bar_dialog.setContentView(R.layout.lead_progressbar);
											lead_progress_bar_dialog.setCancelable(false);
											lead_progress_bar_dialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
											lead_progress_bar_dialog.show();
											new LeadUtility.SendLeadDatatoCRMStep1(getLeadResponseStep1(), "").execute();
												}
										else{
											Toast toast=Toast.makeText(mActivity, "Please Check Your Internet Connection !!!",Toast.LENGTH_SHORT);
											toast.setGravity(Gravity.CENTER, 0, 0);
											toast.show();
										}
											}
								}
							});
							}
						 if(changeStatus.getSelectedItemPosition()==5){
							 
							rescheduleButton.setVisibility(view.VISIBLE);
							Layoutreason.setVisibility(view.GONE);
							Rescheduled_date.setText("");
							Rescheduled_time.setText("");
							rescheduleButton.setOnClickListener(new OnClickListener() {
							
							Dialog	dateandtime_picker;
								@Override
								public void onClick(View v) {
									
									dateandtime_picker = new Dialog(mCtx);
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
									Button date_time_set = (Button) dateandtime_picker
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
											Rescheduled_time.setText("Time : " + hour + ":" + minute+ ":"+ second);
											Rescheduled_date.setText("Date : " + year + "-" + month + "-"
													+ day);
											dateandtime_picker.dismiss();
											layoutReschedule.setVisibility(view.VISIBLE);
											saveButton.setVisibility(view.VISIBLE);
											saveButton.setText("Save");
										}
									});
									dateandtime_picker.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
									dateandtime_picker.show();
								}
							});
							saveButton.setOnClickListener(new OnClickListener() 
							{
								@Override
								public void onClick(View v) 
								{
									LeadsData leadsInfo4 = list.get(groupPosition);
									if(Rescheduled_date.getText().equals("") || Rescheduled_time.getText().equals("") || datetime_check(Rescheduled_date.getText().toString(),Rescheduled_time.getText().toString()))
											{
												Toast toast=Toast.makeText(mActivity, "Please Select Correct Date and Time to Reschedule !!!",Toast.LENGTH_SHORT);
												toast.setGravity(Gravity.CENTER, 0, 0);
												toast.show();
											}
									else{
										
											newLead.setBusiness_name(leadsInfo4.getBusinessname());
											newLead.setMobile_number(leadsInfo4.getMobile());
											newLead.setPin_code(leadsInfo4.getCode());
											newLead.setState(leadsInfo4.getState());
											newLead.setId(leadsInfo4.getLeadid());
											newLead.setCity(leadsInfo4.getCity());
											newLead.setAddress(leadsInfo4.getBranch_address());
											newLead.setRemarks(leadsInfo4.getReason_lead_status());
											newLead.setStatus("Re-scheduled");
											newLead.setTime(Rescheduled_time.getText().toString());
											newLead.setDate(Rescheduled_date.getText().toString());
											
										//	Toast.makeText(mCtx, leadsInfo4.getReason_lead_status(),Toast.LENGTH_LONG).show();
											if(checkInternet){
												lead_progress_bar_dialog = new Dialog(mCtx);
												lead_progress_bar_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
												lead_progress_bar_dialog.setContentView(R.layout.lead_progressbar);
												lead_progress_bar_dialog.setCancelable(false);
												lead_progress_bar_dialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
												lead_progress_bar_dialog.show();
												new LeadUtility.SendLeadDatatoCRMStep1(getLeadResponseStep1(), "").execute();
													}
											else{
												Toast toast=Toast.makeText(mActivity, "Please Check Your Internet Connection !!!",Toast.LENGTH_SHORT);
												toast.setGravity(Gravity.CENTER, 0, 0);
												toast.show();
												}
												
										}
								}
							});
							}
					}
				}
				
				

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
			});
			//convertView.setTag(holder);
	    
	   
	
	    return convertView;
	}
	
	protected void childButtonClick(int index){
		
	}

	
	private boolean datetime_check(String lead_date, String lead_time){
		Date todaydate=new Date();
		//todaydate=subtractDay(todaydate);
		Date d1=null;
		Date t1=null;
		String selecteddate=lead_date.toString();
		selecteddate=selecteddate.replace("Date : ", "");
		String selectedtime=lead_time.toString();
		selectedtime=selectedtime.replace("Time : ", "");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");		
		SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
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
	
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

	public CRMListener getLeadResponseStep1() {

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
						new LeadUtility.SendLeadDatatoCRMStep2(token,getLeadResponseStep2(), "").execute();
						//Toast.makeText(mCtx, token,Toast.LENGTH_LONG).show();
						
					}
					else{
						Toast toast=Toast.makeText(mActivity, "Something Went Wrong !!!",Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();					
						lead_progress_bar_dialog.dismiss();
					}

				} catch (Exception leadexception1) {
					leadexception1.printStackTrace();
				}
			}
		};
		return lead_response;
	}
	
	public CRMListener getLeadResponseStep2() {

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
					//	Toast.makeText(mCtx, lead_adminSession,Toast.LENGTH_LONG).show();
						new LeadUtility.SendLeadDatatoCRMStep3(sharedpreferences.getString("username", ""),lead_adminSession, getLeadResponseStep3()).execute();

					}
					else{
						Toast toast=Toast.makeText(mActivity, "Something Went Wrong !!!",Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						lead_progress_bar_dialog.dismiss();
					}
				} catch (Exception leadexception1) {
					leadexception1.printStackTrace();
				}

			}
		};
		return lead_response;
	}

	public CRMListener getLeadResponseStep3() {

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
					//	Toast.makeText(mCtx, Utility.UserAPIAccessKey,Toast.LENGTH_LONG).show();
						new LeadUtility.SendLeadDatatoCRMStep1(getLeadResponseStep4(),sharedpreferences.getString("username", "")).execute();
					}
					else{
						Toast toast=Toast.makeText(mActivity, "Something Went Wrong !!!",Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					lead_progress_bar_dialog.dismiss();
					Log.d("MYLEAD", result);
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
	
	public CRMListener getLeadResponseStep4() {

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
					//	Toast.makeText(mCtx," User-token "+token,Toast.LENGTH_LONG).show();
						new LeadUtility.SendLeadDatatoCRMStep2(token,getLeadResponseStep5(),sharedpreferences.getString("username", "")).execute();
					}
					else{
						Toast toast=Toast.makeText(mActivity, "Something Went Wrong !!!",Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						lead_progress_bar_dialog.dismiss();
					}
				} catch (Exception leadexception1) {
					leadexception1.printStackTrace();
				}

			}
		};
		return lead_response;
	}
	
	public CRMListener getLeadResponseStep5() {

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
						userid = (String) jsonTokenObj.get("userId");
					//	Toast.makeText(mCtx,"Session-Name "+sessionName+" User-id "+userid,Toast.LENGTH_LONG).show();
						new LeadUtility.SendLeadDatatoCRMStep4(sessionName,userid, newLead, getLeadResponseStep6(),"update").execute();
					}
					else{
						Toast toast=Toast.makeText(mActivity, "Something Went Wrong !!!",Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					lead_progress_bar_dialog.dismiss();
					Log.d("MYLEAD", result);
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
	
	public CRMListener getLeadResponseStep6() {
		
		CRMListener lead_response = new CRMListener() {

			@Override
			public void getCAllback(String result) {
				try {
					JSONObject jObj = new JSONObject(result);
					lead_status = jObj.get("success").toString();
					if (lead_status.equalsIgnoreCase("true")) {
						JSONObject jsonTokenObj = new JSONObject(jObj.get("result").toString());
						id=(String) jsonTokenObj.get("id");
						if(id.equals("")){
							Toast toast=Toast.makeText(mActivity, "Something Went Wrong !!!",Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
							lead_progress_bar_dialog.dismiss();
						}
						else{
							LeadsFragment newFragment = new LeadsFragment();
							FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
							transaction.replace(R.id.container, newFragment);
							transaction.commit();
							lead_progress_bar_dialog.dismiss();
							Toast toast=Toast.makeText(mActivity, "Updated Successfully !!!",Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
						Log.d("MYLEAD", result);
						
					}
					else{
						Log.d("MYLEAD", result);
						Toast toast=Toast.makeText(mActivity, "Something Went Wrong !!!",Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						lead_progress_bar_dialog.dismiss();
					}
				//	Log.d("MYLEAD", result);
					// Toast.makeText(getActivity(),
					// result,Toast.LENGTH_LONG).show();
				} catch (Exception leadexception1) {
					leadexception1.printStackTrace();
				}

			}
		};
		return lead_response;
	}
	
	private NewSeller convertLeadToSeller(LeadsData leadsData){
		  NewSeller newSeller = new NewSeller();
		  newSeller.setBussiness_name(leadsData.getBusinessname());
		  newSeller.setLead(true);
		  newSeller.setLeadId(leadsData.getLead_no());
		  newSeller.setShipping_pin_code(leadsData.getCode());
		  newSeller.setShipping_city(leadsData.getCity());
		  newSeller.setShipping_state(leadsData.getState());
		  newSeller.setMobile_no(leadsData.getMobile());
		  newSeller.setEmail_id(leadsData.getEmail());
		  
		  return newSeller;  
		 }
	
	private NewLead convertToLNewLead(LeadsData leadsInfo1){
		  NewLead newLead1 = new NewLead();
		  newLead1.setBusiness_name(leadsInfo1.getBusinessname());
		  newLead1.setMobile_number(leadsInfo1.getMobile());
		  newLead1.setPin_code(leadsInfo1.getCode());
		  newLead1.setState(leadsInfo1.getState());
		  newLead1.setId(leadsInfo1.getLeadid());
		  newLead1.setCity(leadsInfo1.getCity());
		  newLead1.setAddress(leadsInfo1.getAddress());
		  newLead1.setRemarks(leadsInfo1.getReason_lead_status());
		  newLead1.setStatus(leadsInfo1.getLeadstatus());
		  newLead1.setTime(leadsInfo1.getAppt_time());
		  newLead1.setDate(leadsInfo1.getAppt_date());
		  newLead1.setAssigndUserId(leadsInfo1.getSmownerid());
		  newLead1.setLeadSource(leadsInfo1.getLeadsource());
		  newLead1.setLeadNumber(leadsInfo1.getLead_no());
		  return newLead1;
		 }
}
