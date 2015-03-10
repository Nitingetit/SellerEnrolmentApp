package com.ambsellerapp.utilies;

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

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ambsellerapp.asynctasks.AsyncUtilies;
import com.ambsellerapp.listeners.CRMListener;
import com.ambsellerapp.listeners.LeadsListener;
import com.ambsellerapp.listeners.SearchDataListener;
import com.ambsellerapp.modals.LeadsData;
import com.ambsellerapp.modals.NewSeller;

public class SellerUtilities 
{
	private  SearchDataListener listener;
	private LeadsListener leadsListener;
	private Context ctx;
	SharedPreferences sharedpreferences;
	private boolean checkInternet;
	private String successStatus;
	private String session="",userId="",pinCode="",fromWhere="search";
	private ArrayList<NewSeller> searchSellerList=new ArrayList<NewSeller>();
	private ArrayList<LeadsData> LeadsList=new ArrayList<LeadsData>();
	private boolean isAdminSessionFound=false;
	/**
	 * @ Setting the End Listener of the Call
	 */
	public void setEndListner(SearchDataListener searchDataListener)
	{
		this.listener =searchDataListener;
	}
	public void setLeadsEndListner(LeadsListener leadsDataListener)
	{
		this.leadsListener =leadsDataListener;
	}
	public SellerUtilities(Context ct,String pin, String frm)
	{
		ctx=ct;
		sharedpreferences =ctx.getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		checkInternet=NetworkUtils.isNetworkAvailable(ctx);
		pinCode=pin;
		fromWhere=frm;
	}

	/**
	 * Method for getting the token 
	 * @return
	 */
	public CRMListener getChallenge()
	{
		CRMListener crmListenerGetChallenge=new CRMListener() 
		{
			@Override
			public void getCAllback(String result) 
			{
				String token="";
				try 
				{
					JSONObject jObj=new JSONObject(result);
					successStatus=jObj.get("success").toString();
					if(successStatus.equalsIgnoreCase("true"))
					{
						Log.d("MSG",result);
						JSONObject jsonTokenObj=new JSONObject(jObj.get("result").toString());
						token=(String)jsonTokenObj.get("token");
						System.out.println("----Second Async Calll---");
						if(fromWhere.equalsIgnoreCase("search"))
							new AsyncUtilies.SecondAsyn(token,sharedpreferences.getString("username", ""),getLoginCompleteListener()).execute();
						else
						{
							if(isAdminSessionFound)
								new AsyncUtilies.SecondAsyn(token,sharedpreferences.getString("username", ""),getLoginCompleteListener(),"leads").execute();
							else
								new AsyncUtilies.SecondAsyn(token,sharedpreferences.getString("username", ""),getLoginCompleteListener()).execute();
						}
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

	/**
	 * Method for the getting the session of the admin
	 * @return
	 */
	private CRMListener getLoginCompleteListener()
	{
		CRMListener crmListenerLoginCompleteListener=new CRMListener()
		{
			public void getCAllback(String result) 
			{
				try 
				{
					JSONObject jObj=new JSONObject(result);
					String successStatus=jObj.get("success").toString();
					if(successStatus.equalsIgnoreCase("true"))
					{
						Log.d("MSG",result);
						JSONObject jsonTokenObj=new JSONObject(jObj.get("result").toString());
						session=(String)jsonTokenObj.get("sessionName");
						userId=(String)jsonTokenObj.get("userId");
						System.out.println("----Third Async Calll---");
						if(fromWhere.equalsIgnoreCase("search"))
						{
							new AsyncUtilies.ThirdAsyn(pinCode,"",session,getDataCompleteListener(),fromWhere,"").execute();
						}
						else
						{
							if(isAdminSessionFound)
							{///If adminSessionFound than we also had user Session now need to get user API key
								
							   // Utility.UserAPIAccessKey=jsonTokenObj.getString("accesskey");
								String dt="";
								String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
								Calendar c = Calendar.getInstance();
								Calendar c1 = Calendar.getInstance();
						        try
						        {
						         c.setTime(new Date());
						         c1.setTime(new Date());
						        }
						        catch (Exception e) 
						        {
						         e.printStackTrace();
						        }
						        c.add(Calendar.DATE, 1); 
						        c1.add(Calendar.DATE, -1);// number of days to add
						        dt = sdf.format(c.getTime()); 
						        date = sdf.format(c1.getTime());
								/*try 
								{
									Set<String> set = sharedpreferences.getStringSet("pincodeList", null);
									ArrayList<String> arrayPinCode=new ArrayList<String>(set);
									Collections.sort(arrayPinCode);
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
								catch (Exception e) 
								{
									e.printStackTrace();
								}*/
								
								
								new AsyncUtilies.GetLeads(getLeadsDataListener(),sharedpreferences.getString("username", ""),date,dt).execute();
							/*	new AsyncUtilies.ThirdAsyn(pinCode,
										"",session,getDataCompleteListener(),"allLeads","").execute();*/
								
							}
							else
							{
								new AsyncUtilies.ThirdAsyn(sharedpreferences.getString("username", ""),"",session,getDataCompleteListener(),"").execute();
							}
						}
					}
					else
					{

					}
				}
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
		};
		return crmListenerLoginCompleteListener;
	}
	/**
	 * Method for getting the data and making the arraylist as per that
	 * @return
	 */
	private CRMListener getDataCompleteListener() 
	{
		CRMListener crmListener = new CRMListener() 
		{
			public void getCAllback(String obj) 
			{
				String successStatus="",inputtedUserPassword="",userPass="",temp="",userid="";
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
							Log.d("MSG",resultArray.toString());
							if(fromWhere.equalsIgnoreCase("search"))
							{///For search calcultions	
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
								((SearchDataListener) listener).getCallback(searchSellerList);
							}
							else
							{
								isAdminSessionFound=true;
								resultObj=resultArray.getJSONObject(0);
								Utility.UserAPIAccessKey=resultObj.getString("accesskey");
								new AsyncUtilies.FirstAsyn(getChallenge(),sharedpreferences.getString("username", ""),fromWhere).execute();
							}
						}

					}

				}

				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
		};
		return crmListener;
	}
	
	/**
	 * Method for getting the data and making the arraylist as per that
	 * @return
	 */
	private CRMListener getLeadsDataListener() 
	{
		CRMListener crmListener = new CRMListener() 
		{
			public void getCAllback(String obj) 
			{
				String successStatus="",inputtedUserPassword="",userPass="",temp="",userid="";
				JSONObject resultObj=null;
				LeadsData newLead=null;
				try 
				{
					JSONObject jObj=new JSONObject(obj);
					successStatus=jObj.get("success").toString();
					Log.d("MSG",successStatus);
					if(successStatus.equalsIgnoreCase("true"))
					{	
						JSONObject Obj=jObj.getJSONObject("result");
						
						if(Obj.getJSONArray("result")!=null){
						
						JSONArray resultArray=Obj.getJSONArray("result");
						Log.d("MSG",Obj.toString());
						if(!resultArray.isNull(0))
						{	
							Log.d("MSG",resultArray.toString());
							if(fromWhere.equalsIgnoreCase("leads"))
							{///For Leads Data Calculation
								DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
								DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
								String dat="";
								Date date=null;
								for(int i=0;i<resultArray.length();i++)
								{
									resultObj=resultArray.getJSONObject(i);
									newLead=new LeadsData();
									newLead.setLead_no(resultObj.getString("lead_no"));
									newLead.setLeadid(resultObj.getString("leadid"));
									newLead.setEmail(resultObj.getString("email"));
									newLead.setBusinessname(resultObj.getString("businessname"));
									newLead.setLeadstatus(resultObj.getString("leadstatus"));
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
									newLead.setSmownerid(resultObj.getString("smownerid"));
									newLead.setSmcreatorid(resultObj.getString("smcreatorid"));
									newLead.setCreatedby(resultObj.getString("createdby"));
									newLead.setCreatedtime(resultObj.getString("createdtime"));
									
									
									LeadsList.add(i, newLead);
								}
								leadsListener.onLeadsComplete(LeadsList);
							}
							else
							{
								isAdminSessionFound=true;
								new AsyncUtilies.FirstAsyn(getChallenge(),sharedpreferences.getString("username", ""),fromWhere).execute();
							}
						}
						}
						else{
							Log.d("MSG","nothing found");
							leadsListener.onLeadsComplete(LeadsList);
						}

					}
					else{
						Log.d("MSG","nothing found");
					//	leadsListener.onLeadsComplete(LeadsList);
					}

				}

				catch (JSONException e) 
				{
					e.printStackTrace();
					Log.d("MSG","nothing found");
					leadsListener.onLeadsComplete(LeadsList);
				}
			}
		};
		return crmListener;
	}

}
