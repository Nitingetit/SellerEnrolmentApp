package com.ambsellerapp.utilies;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ProgressBar;
import arlut.csd.crypto.MD5Crypt;

import com.ambsellerapp.asynctasks.AsyncUtilies;
import com.ambsellerapp.database.DatabaseHandler;
import com.ambsellerapp.listeners.CRMListener;
import com.ambsellerapp.modals.NewSeller;
import com.AMBSEA.R;

public class CRMUtilities
{
	private NewSeller newSeller;
	private String successStatus;
	private boolean checkInternet;
	//	private long id=0;
	private ProgressBar progressBar;
	private String adminSession="",userId="",userSession="";
	private Context ctx;
	private  CRMListener listener;
	boolean isHaveSession = false;
	CRMListener listenerForSettion=null;
	boolean isRefreshing=false;
	SharedPreferences sharedpreferences;
	private String pinCode="",mobileNo="",name="",fromWhere="search";
	private String sellerFinalId;
	private String rDId;
	boolean isREquestForRefresh=false;
	int rowID = 0;
	private String id="";
	private String mobileExistanceCheck="abc";
	private ArrayList<NewSeller> sellerList=null;
	private boolean isAdminSessionFound=false;
	public void setEndListner(CRMListener listener){
		this.listener =listener;
	}

	public void asyncStart()
	{
		sharedpreferences =ctx.getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		new AsyncUtilies.FirstAsyn(getChallenge(),sharedpreferences.getString("username", "")).execute();
	}
	public CRMUtilities(Context ct, String pin, String mob, String nam)
	{
		ctx=ct;
		pinCode=pin;
		mobileNo=mob;
		name=nam;
	}
	public void setRequestForRefresh(){
		isREquestForRefresh =true;
	}

	public CRMUtilities(Context ct)
	{
		ctx=ct;
		sharedpreferences =ctx.getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		checkInternet=NetworkUtils.isNetworkAvailable(ctx);
		id=sharedpreferences.getString("id", "");
	}

	public void setData(String pin, String mob, String nam){
		pinCode=pin;
		mobileNo=mob;
		name=nam;
	}
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
						JSONObject jsonTokenObj=new JSONObject(jObj.get("result").toString());
						token=(String)jsonTokenObj.get("token");
					}
					System.out.println("----Second Async Calll---");
					if(checkInternet)
					{
						if(isAdminSessionFound)
							new AsyncUtilies.SecondAsyn(token,sharedpreferences.getString("username", ""),getLoginCompleteListener(),"forUserSession").execute();
						else
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

						if(checkInternet)
						{
							if(!isAdminSessionFound)
							{
								JSONObject jsonTokenObj=new JSONObject(jObj.get("result").toString());
								adminSession=(String)jsonTokenObj.get("sessionName");
								userId=(String)jsonTokenObj.get("userId");

								new AsyncUtilies.ThirdAsyn(sharedpreferences.getString("username", ""),
										sharedpreferences.getString("password", ""),adminSession,getPassWordCheckListener()).execute();							
							}
							else
							{

								JSONObject jsonTokenObj=new JSONObject(jObj.get("result").toString());
								userSession=(String)jsonTokenObj.get("sessionName");
								userId=(String)jsonTokenObj.get("userId");
								new AsyncUtilies.postingSellerDataAsyn(sharedpreferences.getString("username", ""),
										userSession,newSeller,getFinalResultListener(),userId).execute();
							}
						}
						else
						{
							listenerForSettion.getCAllback(null);
						}
					}
					else
					{
						listenerForSettion.getCAllback(null);
					}


				}
				catch (JSONException e) 
				{
					e.printStackTrace();
					listenerForSettion.getCAllback(null);
				}

				System.out.println("----Third Async Calll---");
				//				if(newSellerOfflineList.size()!=0)
				//					newSeller=newSellerOfflineList.get(newSellerOfflineList.size()-1);


			}
		};
		return crmListenerLoginCompleteListener;
	}

	private CRMListener getDuplicateNumberCheckListener() 
	{
		CRMListener crmListener = new CRMListener() 
		{
			@Override
			public void getCAllback(String obj) 
			{
				String successStatus="";
				try 
				{
					JSONObject jObj=new JSONObject(obj);
					successStatus=jObj.get("success").toString();
					if(successStatus.equalsIgnoreCase("true"))
					{	
						JSONArray resultArray=jObj.getJSONArray("result");
						if(resultArray.length()==0)
						{
							if(checkInternet)
							{
								isHaveSession=true;
								isAdminSessionFound=true;
								new AsyncUtilies.FirstAsyn(getChallenge(),sharedpreferences.getString("username", ""),"forUserToken").execute();
								//								new AsyncUtilies.postingSellerDataAsyn(sharedpreferences.getString("username", ""),
								//										session,newSeller,getFinalResultListener(),id).execute();
							}
						}
						else
						{///If found the number duplicate than setting the status in the local database
							DatabaseHandler db=new DatabaseHandler(ctx);
							db.updateSellerIsNumberDuplicateStatus(rowID);//TODO midnight 1:42 PM 29th Dec
							listenerForSettion.getCAllback(null);
						}
					}
					else
					{
						listener.getCAllback(null);
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

	private CRMListener getPassWordCheckListener() 
	{
		CRMListener crmListener = new CRMListener() 
		{
			@Override
			public void getCAllback(String obj) 
			{
				String successStatus="",inputtedUserPassword="",userPass="",temp="",userid="";
				String id=sharedpreferences.getString("id", "");
				try 
				{
					JSONObject jObj=new JSONObject(obj);
					successStatus=jObj.get("success").toString();
					if(successStatus.equalsIgnoreCase("true"))
					{	
						JSONArray resultArray=jObj.getJSONArray("result");
						JSONObject resultObj=resultArray.getJSONObject(0);
						userPass=resultObj.getString("user_password");
						userid=resultObj.getString("id");
						inputtedUserPassword=sharedpreferences.getString("password", "");
						temp="$1$"+sharedpreferences.getString("username", "").substring(0, 2)+"0000000";
						Utility.UserAPIAccessKey=resultObj.getString("accesskey");

						System.out.println("-----ABHI value of temp is==="+temp);
						temp=MD5Crypt.crypt(inputtedUserPassword, temp);
						System.out.println("----Converted value of password is ---"+temp);
						System.out.println("----Compare value of password is ---"+userPass.compareTo(temp));
						if(userPass.compareTo(temp)==0)
						{
							if(checkInternet)
							{
								//								new AsyncUtilies.ThirdAsyn(sharedpreferences.getString("username", ""),mobileExistanceCheck,
								//										session,getDuplicateNumberCheckListener(),newSeller.getMobile_no()).execute();

								new AsyncUtilies.ThirdAsyn(newSeller.getShipping_pin_code(),newSeller.getBussiness_name(),adminSession,getDuplicateNumberCheckListener(),"mobileCheck",newSeller.getMobile_no()).execute();
								//								(String uPin,String nam,String paramert,CRMListener listener,String frm,String mob) 
							}
							else
							{
							}
						}
						else
						{
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

	private CRMListener getFinalResultListener()
	{
		CRMListener crmListenerFinalResultListener=new CRMListener()
		{
			@Override
			public void getCAllback(String result) 
			{
				try
				{
					JSONObject jObj=new JSONObject(result);
					String successStatus=jObj.getString("success");

					System.out.println("---getFinalResultListener=====result==="+successStatus);
					if(successStatus.equalsIgnoreCase("true"))
					{
						DatabaseHandler db=new DatabaseHandler(ctx);


						JSONObject resultObj=jObj.getJSONObject("result");
						rDId=resultObj.getString("assigned_user_id");
						sellerFinalId=resultObj.getString("id");
						db.updateSellerStatus(newSeller.getId(),rDId,sellerFinalId);
						if(checkInternet)////Commented on to the date 23 due to Top up not working at the CRM end
							new AsyncUtilies.RDTopUpChecking(rDId,getRDCheckingListener(),adminSession).execute();
					}

				}
				catch (JSONException e) 
				{
					e.printStackTrace();
					listenerForSettion.getCAllback(null);
				}
				//				listenerForSettion.getCAllback(null);
			}
		};
		return crmListenerFinalResultListener;
	}

	private CRMListener getRDCheckingListener()
	{
		CRMListener crmListener=new CRMListener()
		{
			@Override
			public void getCAllback(String result) 
			{
				try
				{
					JSONObject jObj=new JSONObject(result);
					String successStatus=jObj.getString("success");

					if(successStatus.equalsIgnoreCase("true"))
					{
						JSONArray resultArray=jObj.getJSONArray("result");
						JSONObject resultObj=resultArray.getJSONObject(0);
						String rDTopUpAmount=resultObj.getString("rd_security_amt");
						float rdAmount=Float.valueOf(rDTopUpAmount);
						String rDId=resultObj.getString("id");
						float userSelectedAmount=Float.valueOf(newSeller.getTopup_amount())*1000;

						if(rdAmount>=userSelectedAmount)
						{
							if(checkInternet)////Checking the internet connectivity
							{
								String serviceTaxAmount=String.valueOf((Float.valueOf(newSeller.getTopup_amount())*1000)*R.string.service_tax);
								new AsyncUtilies.postingTopDataOfSeller(rDId,sellerFinalId,newSeller.getTopup_amount(),adminSession,sharedpreferences.getString("id", ""),serviceTaxAmount,getTopUpSellerPostingResponse(),"create").execute();
							}
						}
					}
				}
				catch (NumberFormatException e) 
				{
					e.printStackTrace();
					listenerForSettion.getCAllback(null);
				}
				catch (JSONException e) 
				{
					e.printStackTrace();
					listenerForSettion.getCAllback(null);
				}
			}
		};
		return crmListener;
	}

	private CRMListener getTopUpSellerPostingResponse()
	{
		CRMListener crmListener=new CRMListener()
		{
			@Override
			public void getCAllback(String result) 
			{
				try
				{
					JSONObject jObj=new JSONObject(result);
					String successStatus=jObj.getString("success");

					if(successStatus.equalsIgnoreCase("true"))
					{
						DatabaseHandler db=new DatabaseHandler(ctx);
						db.updateSellerPaymentStatus(rowID);
					}
					listenerForSettion.getCAllback(null);
				}
				catch (JSONException e) 
				{
					e.printStackTrace();
					listenerForSettion.getCAllback(null);
				}
			}
		};
		return crmListener;
	}

	public void recursiveCallback(final ArrayList<NewSeller> sellList)
	{
		sellerList=sellList;
		if(sellerList.isEmpty()==false){
			if(!isHaveSession)
			{
				newSeller=sellerList.get(0);
				new AsyncUtilies.FirstAsyn(getChallenge(),sharedpreferences.getString("username", "")).execute();
				listenerForSettion = new CRMListener() {

					@Override
					public void getCAllback(String result) {
						isHaveSession= true;
						sellerList.remove(0);
						recursiveCallback(sellerList);
					}
				};
			}else{
				newSeller = sellerList.get(0);
				rowID  = newSeller.getId();
				if(newSeller.getSync_status()==0){
					//					new AsyncUtilies.postingSellerDataAsyn(sharedpreferences.getString("username", ""),
					//							session,newSeller,getFinalResultListener(),id).execute();
					//					new AsyncUtilies.ThirdAsyn(sharedpreferences.getString("username", ""),mobileExistanceCheck,
					//							session,getDuplicateNumberCheckListener(),"mobileCheck").execute();
					new AsyncUtilies.ThirdAsyn(newSeller.getShipping_pin_code(),newSeller.getBussiness_name(),adminSession,getDuplicateNumberCheckListener(),"mobileCheck",newSeller.getMobile_no()).execute();
				} else {
					sellerFinalId = newSeller.getSeller_Id();
					rDId = newSeller.getRdId();
					if(checkInternet)
						new AsyncUtilies.RDTopUpChecking(rDId,getRDCheckingListener(),adminSession).execute();
				}
			}
		}else{
			listener.getCAllback(null);
		}
	}
}
