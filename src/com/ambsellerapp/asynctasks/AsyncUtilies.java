package com.ambsellerapp.asynctasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.AMBSEA.R;
import com.ambsellerapp.listeners.CRMListener;
import com.ambsellerapp.modals.NewSeller;
import com.ambsellerapp.utilies.Utility;
import com.google.android.gms.internal.ct;

/**
 * @author ABHI on the date 9th Dec 2014
 *	Class for handling all of the Async operations
 */
public class AsyncUtilies
{
	public static String session="";
	/**
	 * @author ABHI
	 * Class for getting the token for communication
	 */

	public static class FirstAsyn extends AsyncTask<Void, Void, String>
	{
		CRMListener callCrmListener;
		String response,userName,fromWhere="";
		HttpGet httpGet=null;

		public FirstAsyn(CRMListener listener,String uName) 
		{
			callCrmListener =listener;
			userName=uName;
		}
		public FirstAsyn(CRMListener listener,String uName,String frm) 
		{
			callCrmListener =listener;
			userName=uName;
			fromWhere=frm;
		}
		@Override
		protected String doInBackground(Void... params) 
		{
			String url=Utility.getChallengeUrl;
			HttpClient httpClient = new DefaultHttpClient();
			//			HttpGet httpGet = new HttpGet(url +"?operation=getchallenge&username="+userName);
			if(fromWhere.equalsIgnoreCase(""))
			{	
				httpGet = new HttpGet(url +"?operation=getchallenge&username="+Utility.adminUser);
			}
			else
			{
				httpGet = new HttpGet(url +"?operation=getchallenge&username="+userName);
			}
//			System.out.println("--------Get Challenge URL is --"+url +"?operation=getchallenge&username="+Utility.adminUser);

			try 
			{
				HttpResponse httpResponse = httpClient.execute(httpGet);
				System.out.println("httpResponse");

				// getEntity() ; obtains the message entity of this response
				// getContent() ; creates a new InputStream object of the entity.
				// Now we need a readable source to read the byte stream that comes as the httpResponse
				InputStream inputStream = httpResponse.getEntity().getContent();
				// We have a byte stream. Next step is to convert it to a Character stream
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				// Then we have to wraps the existing reader (InputStreamReader) and buffer the input
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				// InputStreamReader contains a buffer of bytes read from the source stream and converts these into characters as needed.
				//The buffer size is 8K
				//Therefore we need a mechanism to append the separately coming chunks in to one String element
				// We have to use a class that can handle modifiable sequence of characters for use in creating String
				StringBuilder stringBuilder = new StringBuilder();
				String bufferedStrChunk = null;
				// There may be so many buffered chunks. We have to go through each and every chunk of characters
				//and assign a each chunk to bufferedStrChunk String variable
				//and append that value one by one to the stringBuilder
				while((bufferedStrChunk = bufferedReader.readLine()) != null)
				{
					stringBuilder.append(bufferedStrChunk);
				}
				// Now we have the whole response as a String value.
				//We return that value then the onPostExecute() can handle the content
				System.out.println("Returning value of doInBackground :" + stringBuilder.toString());
				response= stringBuilder.toString();
			}
			catch (ClientProtocolException cpe) 
			{
				System.out.println("Exception generates caz of httpResponse :" + cpe);
				cpe.printStackTrace();
			}
			catch (IOException ioe)
			{
				System.out.println("Second exception generates caz of httpResponse :" + ioe);
				ioe.printStackTrace();
			}
			return response;
		}
		@Override
		protected void onPostExecute(String result) 
		{
			callCrmListener.getCAllback(result);
		}
	}


	/**
	 * @author ABHI
	 * Class for getting the login authentication
	 */
	public static class SecondAsyn extends AsyncTask<Void, Void, String>
	{
		CRMListener callCrmListener;
		String token,response,userName,fromWhere="";
		String accessKey="";
		public SecondAsyn(String parameter ,String uName ,CRMListener listener) 
		{
			token=parameter;
			userName=uName;
			callCrmListener =listener;
		}
		public SecondAsyn(String parameter ,String uName ,CRMListener listener,String frm) 
		{
			token=parameter;
			userName=uName;
			callCrmListener =listener;
			fromWhere=frm;
		}
		@Override
		protected String doInBackground(Void... params) 
		{
			String url=Utility.loginAPIUrl;
			
		
			try
			{
				HttpPost localHttpPost = new HttpPost(url);
				DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
				ArrayList<BasicNameValuePair> localArrayList = new ArrayList<BasicNameValuePair>(3);

				localArrayList.add(new BasicNameValuePair("operation", "login"));
				if(fromWhere.equalsIgnoreCase(""))
				{
					accessKey=Utility.APIAccessKey;
					accessKey=token+accessKey;
					accessKey=Utility.convertPassMd5(accessKey);
					localArrayList.add(new BasicNameValuePair("username", Utility.adminUser));
					localArrayList.add(new BasicNameValuePair("accessKey", accessKey));
				}
				else
				{
					accessKey=Utility.UserAPIAccessKey;
					accessKey=token+accessKey;
					accessKey=Utility.convertPassMd5(accessKey);
					localArrayList.add(new BasicNameValuePair("username", userName));
					localArrayList.add(new BasicNameValuePair("accessKey",accessKey ));
				}
				localHttpPost.setEntity(new UrlEncodedFormEntity(localArrayList));
				this.response = EntityUtils.toString(localDefaultHttpClient.execute(localHttpPost).getEntity());
				Log.d("JSON", response);
				return this.response;
			}
			catch (Exception localException)
			{
				localException.printStackTrace();
				return null;
			}
		}
		@Override
		protected void onPostExecute(String result) 
		{
			if(result!=null)
				callCrmListener.getCAllback(result);
		}
	}
	/**
	 * @author ABHI
	 * Class for successful Authentication and Getting the data
	 */
	public static class ThirdAsyn extends AsyncTask<Void, Void, String>
	{
		CRMListener callCrmListener;
		String id="",response="",userName="",passWord="",fromWhere="",query="",
				pinCode="",name="",mobileNo="",leadStatus="",apptDate="",apptLess="",apptMore="";
		NewSeller newSell=null;
		
		
		public ThirdAsyn(String uName,String pWord,String paramert,CRMListener listener) 
		{
			userName=uName;
			passWord=pWord;
			session=paramert;
			callCrmListener =listener; 
		}
		
		public ThirdAsyn(String uName,String pHone,String paramert,CRMListener listener,String frm) 
		{
			userName=uName;
			mobileNo=pHone;
			session=paramert;
			callCrmListener =listener; 
			fromWhere=frm;
		}
		public ThirdAsyn(String uPin,String nam,String paramert,CRMListener listener,String frm,String mob) 
		{
			pinCode=uPin;
			name=nam;
			mobileNo=mob;
			session=paramert;
			callCrmListener =listener; 
			fromWhere=frm;
		}
		public ThirdAsyn(String uPin,String date,String frm,String appLess,String appMore,String paramert,CRMListener listener) 
		{
			pinCode=uPin;
			apptDate=date;
			apptLess=appLess;
			session=paramert;
			apptMore=appMore;
			callCrmListener =listener; 
			fromWhere=frm;
			//leadStatus = leadSt;
		}
		public ThirdAsyn(String uNam, String mo,
				String ses, CRMListener listenere,
				String f, NewSeller newSeller) 
		{
			userName=uNam;
			mobileNo=mo;
			session=ses;
			callCrmListener =listenere; 
			fromWhere=f;
			newSell=newSeller;
		}
		public ThirdAsyn(String uPin,String nam,String paramert,CRMListener listener,String frm,String mob,String leadSt) 
		{
			pinCode=uPin;
			name=nam;
			mobileNo=mob;
			session=paramert;
			callCrmListener =listener; 
			fromWhere=frm;
			leadStatus = leadSt;
		}
		

		@Override
		protected String doInBackground(Void... params) 
		{
			String url=Utility.fetchDataUrl;
			if(fromWhere.equalsIgnoreCase("leadsSearch") || fromWhere.equalsIgnoreCase("allLeads"))
			    url=Utility.testLeadsSerch;
			HttpClient httpClient = new DefaultHttpClient();
			try
			{ 
				//				TODO ABHI
				String fields="user_password";
				//				String query="Select * from Users where user_name ="+ userName+";";
				if(fromWhere.equalsIgnoreCase(""))///For password checking
					query="Select * from Users where user_name = '"+userName+"' ;";
				else if(fromWhere.equalsIgnoreCase("mobileCheck"))
				{
					query="Select * from Accounts where phone = '"+mobileNo+"' ;";
				}
				else if(fromWhere.equalsIgnoreCase("updateSeller"))
				{
					query="Update Accounts where phone = '"+mobileNo+"' ;";
				}
				else if(fromWhere.equalsIgnoreCase("updateLeads")){
					query="Update Leads set leadstatus=converted  where id = '"+pinCode+"' ;";
				}
				else if(fromWhere.equalsIgnoreCase("allLeads")){
					
					/*String dt="";
					String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Calendar c = Calendar.getInstance();
					try
					{
						c.setTime(new Date());
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
					c.add(Calendar.DATE, 1);  // number of days to add
					dt = sdf.format(c.getTime());*/
					
					query = "Select * from Leads where mobile='"+pinCode+"';";
					//query="Select * from Leads where code in ( "+pinCode+" ) AND appt_date = '"+apptDate+"' and appt_time >= '"+apptLess+"' and appt_time<='"+apptMore+"'   AND leadstatus != 'invalid' and leadstatus !='Not Interested' and leadstatus !='Not Interested-Met' and leadstatus !='Not Interested-Unmet' and leadstatus !='converted';";
				}
				else if(fromWhere.equalsIgnoreCase("leadsSearch")){
			         if(mobileNo.equalsIgnoreCase("") && name.equalsIgnoreCase("") && leadStatus.equalsIgnoreCase("") )
			         {
			          if(pinCode.length()<6)
			          {
			           query="Select * from Leads where code =  "+pinCode+"  AND leadstatus != 'invalid' and leadstatus !='Not Interested' and leadstatus !='Not Interested-Met' and leadstatus !='Not Interested-Unmet' and leadstatus !='converted';";
			          }
			          else
			          {
			          query="Select * from Leads where code in ( "+pinCode+" ) AND leadstatus != 'invalid' and leadstatus !='Not Interested' and leadstatus !='Not Interested-Met' and leadstatus !='Not Interested-Unmet' and leadstatus !='converted';";
			           //query="Select * from Leads where code in ( "+pinCode+" ) AND appt_date = '2015-03-04' and appt_time >= '10:50:00' and appt_time<='18:00:00';";

			          }
			         }
			         else if(mobileNo.equalsIgnoreCase("") && leadStatus.equalsIgnoreCase(""))
			         {
			          if(pinCode.length()<6)
			          {
			           query="Select * from Leads where code = '"+pinCode+"' AND company = '"+name+"'  AND leadstatus != 'invalid' and leadstatus !='Not Interested' and leadstatus !='Not Interested-Met' and leadstatus !='Not Interested-Unmet' and leadstatus !='converted';";
			          }
			          else
			          {
			           query="Select * from Leads where code in ( "+pinCode+") AND company = '"+name+"'  AND leadstatus != 'invalid' and leadstatus !='Not Interested' and leadstatus !='Not Interested-Met' and leadstatus !='Not Interested-Unmet' and leadstatus !='converted';";
			          }
			         }
			         //      query="Select * from Accounts where ship_code = '"+pinCode+"' AND accountname LIKE '%"+name+"%' ;";
			         else if(name.equalsIgnoreCase("") && leadStatus.equalsIgnoreCase(""))
			         {
			          if(pinCode.length()<6)
			          {
			           query="Select * from Leads where code = '"+pinCode+"' AND mobile = '"+mobileNo+"'  AND leadstatus != 'invalid' and leadstatus !='Not Interested' and leadstatus !='Not Interested-Met' and leadstatus !='Not Interested-Unmet' and leadstatus !='converted';";
			          }
			          else
			          {
			           query="Select * from Leads where code in ( "+pinCode+") AND mobile = '"+mobileNo+"'  AND leadstatus != 'invalid' and leadstatus !='Not Interested' and leadstatus !='Not Interested-Met' and leadstatus !='Not Interested-Unmet' and leadstatus !='converted';";
			          }
			         }
			         else if(mobileNo.equalsIgnoreCase("") && name.equalsIgnoreCase(""))
			         {
			          if(pinCode.length()<6)
			          {
			           query="Select * from Leads where code = '"+pinCode+"' AND leadstatus = '"+leadStatus+"';";
			          }
			          else
			          {
			           query="Select * from Leads where code in ( "+pinCode+") AND leadstatus = '"+leadStatus+"';";
			          }
			         }
			         
			}
				else
				{//Search query creation section
					if(mobileNo.equalsIgnoreCase("") && name.equalsIgnoreCase("") )
					{
						if(pinCode.length()<6)
						{
							query="Select * from Accounts where ship_code =  "+pinCode+"  ;";
						}
						else
						{
							query="Select * from Accounts where ship_code in ( "+pinCode+" ) ;";
						}
					}
					else if(mobileNo.equalsIgnoreCase(""))
					{
						if(pinCode.length()<6)
						{
							query="Select * from Accounts where ship_code = '"+pinCode+"' AND accountname = '"+name+"' ;";
						}
						else
						{
							query="Select * from Accounts where ship_code in ( "+pinCode+") AND accountname = '"+name+"' ;";
						}
					}
					//						query="Select * from Accounts where ship_code = '"+pinCode+"' AND accountname LIKE '%"+name+"%' ;";
					else if(name.equalsIgnoreCase(""))
					{
						if(pinCode.length()<6)
						{
							query="Select * from Accounts where ship_code = '"+pinCode+"' AND phone = '"+mobileNo+"' ;";
						}
						else
						{
							query="Select * from Accounts where ship_code in ( "+pinCode+") AND phone = '"+mobileNo+"' ;";
						}
					}
					//						query="Select * from Accounts where ship_code = '"+pinCode+"' AND phone LIKE '%"+mobileNo+"%' ;";
					else
						//						query="Select * from Accounts where ship_code = '"+pinCode+"' AND phone LIKE '%"+mobileNo+"%' AND accountname LIKE '%"+name+"%' ;";
						query="Select * from Accounts where ship_code = '"+pinCode+"' AND phone = '"+mobileNo+"' AND accountname = '"+name+"' ;";
				}
				System.out.println("---ABHI query ===="+query);
				String request = url + "?operation=query&sessionName="+session+"&query="+java.net.URLEncoder.encode(query);
				//				String request = url + "?operation=query&sessionName="+session+"&query="+query;
				//				System.out.println("---ABHI get Request ===="+query);
				HttpGet httpGet = new HttpGet(request);
				System.out.println("---Value of httpGet url is--"+httpGet);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				System.out.println("httpResponse");
				InputStream inputStream = httpResponse.getEntity().getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				StringBuilder stringBuilder = new StringBuilder();
				String bufferedStrChunk = null;
				while((bufferedStrChunk = bufferedReader.readLine()) != null)
				{
					stringBuilder.append(bufferedStrChunk);
				}
				System.out.println("Returning value of doInBackground in ThirdAsyn:" + stringBuilder.toString());
				response= stringBuilder.toString();
			}
			catch (ClientProtocolException cpe) 
			{
				System.out.println("Exception generates caz of httpResponse :" + cpe);
				cpe.printStackTrace();
			}
			catch (IOException ioe)
			{
				System.out.println("Second exception generates caz of httpResponse :" + ioe);
				ioe.printStackTrace();
			}
			return response;
		}
		@Override
		protected void onPostExecute(String result) 
		{
			callCrmListener.getCAllback(result);
		}
	}

	/**
	 * @author ABHI
	 * Class for checking the RD id in the database
	 */
	public static class RDChecking extends AsyncTask<Void, Void, String>
	{
		CRMListener callCrmListener;
		String sellerId="",query="",response="";

		//		public ThirdAsyn(String paramert,String id1,CRMListener listener) 
		//		{
		//			session=paramert;
		//			id=id1;
		//			callCrmListener =listener; 
		//		}

		public RDChecking(String uId,CRMListener crmList) 
		{
			sellerId=uId;
			callCrmListener=crmList;
		}
		@Override
		protected String doInBackground(Void... params) 
		{
			String url=Utility.fetchDataUrl;
			HttpClient httpClient = new DefaultHttpClient();
			try
			{ 
				//				TODO ABHI
				String fields="user_password";
				//				String query="Select * from Users where user_name ="+ userName+";";
				//				query="Select smownerid from Accounts where id = '"+sellerId+"' ;";
				query="Select * from Accounts ;";
				System.out.println("---RD query ===="+query);
				//				String request = url + "?operation=query&sessionName="+session+"&query="+java.net.URLEncoder.encode(query);
				String request = url + "?operation=query&sessionName="+session+"&query="+query;
				//				System.out.println("---ABHI get Request ===="+query);
				HttpGet httpGet = new HttpGet(request);
				System.out.println("---Value of httpGet url is--"+httpGet);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				System.out.println("httpResponse");
				InputStream inputStream = httpResponse.getEntity().getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				StringBuilder stringBuilder = new StringBuilder();
				String bufferedStrChunk = null;
				while((bufferedStrChunk = bufferedReader.readLine()) != null)
				{
					stringBuilder.append(bufferedStrChunk);
				}
				System.out.println("Returning value of doInBackground in RDChecking:" + stringBuilder.toString());
				response= stringBuilder.toString();
			}
			catch (ClientProtocolException cpe) 
			{
				System.out.println("Exception generates caz of httpResponse :" + cpe);
				cpe.printStackTrace();
			}
			catch (IOException ioe)
			{
				System.out.println("Second exception generates caz of httpResponse :" + ioe);
				ioe.printStackTrace();
			}
			return response;
		}
		@Override
		protected void onPostExecute(String result) 
		{
			callCrmListener.getCAllback(result);
		}
	}

	/**
	 * @author ABHI
	 * Class for checking the RD top up balance with the selected one before sending
	 */
	public static class RDTopUpChecking extends AsyncTask<Void, Void, String>
	{
		CRMListener callCrmListener;
		String rDId="",query="",response="";

		//		public ThirdAsyn(String paramert,String id1,CRMListener listener) 
		//		{
		//			session=paramert;
		//			id=id1;
		//			callCrmListener =listener; 
		//		}

		public RDTopUpChecking(String uId,CRMListener crmList,String ses) 
		{
			rDId=uId;
			callCrmListener=crmList;
			session=ses;
		}
		@Override
		protected String doInBackground(Void... params) 
		{
			String url=Utility.fetchDataUrl;
			HttpClient httpClient = new DefaultHttpClient();
			try
			{ 
				//				TODO ABHI
				String fields="user_password";
				//				String query="Select * from Users where user_name ="+ userName+";";
				//				query="Select smownerid from Accounts where id = '"+rDId+"' ;";
				query="Select rd_security_amt , id from RDRegistration  where assigned_user_id = '"+rDId+"' ;";

				System.out.println("---RDRegistration query ===="+query);
				String request = url + "?operation=query&sessionName="+session+"&query="+java.net.URLEncoder.encode(query);
				//				String request = url + "?operation=query&sessionName="+session+"&query="+query;
				//				System.out.println("---ABHI get Request ===="+query);
				HttpGet httpGet = new HttpGet(request);
				System.out.println("---Value of httpGet url is--"+httpGet);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				System.out.println("httpResponse");
				InputStream inputStream = httpResponse.getEntity().getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				StringBuilder stringBuilder = new StringBuilder();
				String bufferedStrChunk = null;
				while((bufferedStrChunk = bufferedReader.readLine()) != null)
				{
					stringBuilder.append(bufferedStrChunk);
				}
				System.out.println("Returning value of doInBackground in RDChecking:" + stringBuilder.toString());
				response= stringBuilder.toString();
			}
			catch (ClientProtocolException cpe) 
			{
				System.out.println("Exception generates caz of httpResponse :" + cpe);
				cpe.printStackTrace();
				return null;
			}
			catch (IOException ioe)
			{
				System.out.println("Second exception generates caz of httpResponse :" + ioe);
				ioe.printStackTrace();
				return null;
			}
			return response;
		}
		@Override
		protected void onPostExecute(String result) 
		{
			callCrmListener.getCAllback(result);
		}
	}


	/**
	 * @author ABHI
	 * Class for Sending the SMS Verification code to the SMS Gateway
	 */
	public static class SendSMSAsyn extends AsyncTask<Void, Void, String>
	{
		//		CRMListener callCrmListener;
		String phoneNumber,txtMsg,response;
		public SendSMSAsyn(String number ,String text ) 
		{
			phoneNumber=number;
			txtMsg=text;
		}

		@Override
		protected String doInBackground(Void... params) 
		{
			String url=Utility.smsPostURl;
			try
			{
				HttpPost localHttpPost = new HttpPost(url);
				DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
				ArrayList<BasicNameValuePair> localArrayList = new ArrayList<BasicNameValuePair>(3);

				localArrayList.add(new BasicNameValuePair("userId", "getitalt"));
				localArrayList.add(new BasicNameValuePair("pass", "getitalt"));
				localArrayList.add(new BasicNameValuePair("appid", "getitalt"));
				localArrayList.add(new BasicNameValuePair("subappid", "getitalt"));
				localArrayList.add(new BasicNameValuePair("contenttype", "1"));
				localArrayList.add(new BasicNameValuePair("to", "91"+phoneNumber));
				localArrayList.add(new BasicNameValuePair("from", "ASKMEB"));
				localArrayList.add(new BasicNameValuePair("text", txtMsg));
				localArrayList.add(new BasicNameValuePair("selfid", "true"));
				localArrayList.add(new BasicNameValuePair("alert", "1"));
				localArrayList.add(new BasicNameValuePair("dlrreq", "true"));


				localHttpPost.setEntity(new UrlEncodedFormEntity(localArrayList));
				this.response = EntityUtils.toString(localDefaultHttpClient.execute(localHttpPost).getEntity());
				System.out.println("---Value of return value from the server is--"+response);
				Log.d("JSON", response);
				return this.response;
			}
			catch (Exception localException)
			{
				while (true)
					localException.printStackTrace();
			}
		}
		@Override
		protected void onPostExecute(String result) 
		{
			//			callCrmListener.getCAllback(result);
		}
	}

	/**
	 * @author ABHI
	 * Class for Posting the Seller Data on the CRM
	 */
	public static class postingSellerDataAsyn extends AsyncTask<Void, Void, String>
	{
		CRMListener callCrmListener;
		String userName,sessionId,response,id;
		NewSeller newSeller;
		String  industry="Mobile phones";
		String brand="Samsung";
		String operation="create";
		public postingSellerDataAsyn(String uName ,String sessId, NewSeller nSeller,CRMListener getFinal ,String assignId) 
		{
			userName=uName;
			sessionId=sessId;
			newSeller=nSeller;
			callCrmListener=getFinal;
			id=assignId;
		}
		
		public postingSellerDataAsyn(String nam, String session,
				NewSeller newSeller2, CRMListener finalResultListener,
				String id2, String oper) 
		{
			userName=nam;
			sessionId=session;
			newSeller=newSeller2;
			callCrmListener=finalResultListener;
			id=id2;
			operation=oper;
		}

		@Override
		protected String doInBackground(Void... params) 
		{
			String url=Utility.loginAPIUrl;
			try
			{
				HttpPost localHttpPost = new HttpPost(url);
				DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
				ArrayList<BasicNameValuePair> localArrayList = new ArrayList<BasicNameValuePair>(3);

				localArrayList.add(new BasicNameValuePair("sessionName", sessionId));
				localArrayList.add(new BasicNameValuePair("elementType", "Accounts"));
				//				localArrayList.add(new BasicNameValuePair("assigned_user_id", id));
				localArrayList.add(new BasicNameValuePair("operation", operation));

				JSONObject data = new JSONObject();

				data.put("accountname", newSeller.getBussiness_name());
				data.put("phone", newSeller.getMobile_no());
				data.put("email1", newSeller.getEmail_id());
				data.put("bill_code", newSeller.getBilling_pin_code());
				data.put("ship_code", newSeller.getShipping_pin_code());
				data.put("bill_street", newSeller.getBilling_address());
				data.put("ship_street", newSeller.getShipping_address());
				data.put("bill_city", newSeller.getBilling_city());
				data.put("ship_city", newSeller.getShipping_city());
				data.put("bill_state", newSeller.getBilling_state());
				data.put("ship_state", newSeller.getShipping_state());
				data.put("approved", "N.A");
				data.put("email_verification","No");
				if(newSeller.isLead()){
				     data.put("convertedleadid", newSeller.getLeadId());
				     data.put("isconvertedfromlead", "yes");
				}
//				
//				data.put("industry", industry);
//				data.put("product_brand", brand);
				
				
				data.put("assigned_user_id", id);
				data.put("bill_country", "India");
				data.put("ship_country", "India");
				data.put("beneficary_name", newSeller.getBeneficaryName());
				data.put("ac_no", newSeller.getAccount_no());
				data.put("bank_name", newSeller.getBank_name());
				data.put("branch_address", newSeller.getBranch());
				data.put("ifsc_code", newSeller.getIfsc_code());
				data.put("seller_type", "National");////////need for for discussion
				data.put("enrollment_source", "SEA");////////need for for discussion
				data.put("mobile_verified", "Yes");
				data.put("latitude", newSeller.getLatitude());
				data.put("longitude", newSeller.getLongitude());
				data.put("topupamount", newSeller.getTopup_amount());
				data.put("description", "New user");
				data.put("cst_tin_no", newSeller.getCst_tin_number());
				data.put("id", newSeller.getCrm_seller_id());
				
				
				localArrayList.add(new BasicNameValuePair ("element",data.toString()));

				System.out.println("---ABHI Value of request parameters is--"+localArrayList.toString());
				localHttpPost.setEntity(new UrlEncodedFormEntity(localArrayList));
				this.response = EntityUtils.toString(localDefaultHttpClient.execute(localHttpPost).getEntity());
				Log.d("JSON", response);
				return this.response;
			}
			catch (Exception localException)
			{
				localException.printStackTrace();
				return null;
			}
		}
		@Override
		protected void onPostExecute(String result) 
		{
			Log.d("MSG",result);
			callCrmListener.getCAllback(result);
		}
	}


	/**
	 * @author ABHI
	 * Class for Posting the top up data of the seller on to the server
	 */
	public static class postingTopDataOfSeller extends AsyncTask<Void, Void, String>
	{
		CRMListener callCrmListener;
		String rdId,sellerId,id,response,session,assignId,serviceTax,operation;
		int topUpQuantity=1;
		NewSeller newSeller;

		public postingTopDataOfSeller(String rDID, String sellId,int i, String ses, String assigned_id, 
				String serviceTaxAmount, CRMListener topUpSellerPostingResponse, String oper)
		{
			rdId=rDID;
			sellerId=sellId;
			topUpQuantity=i;
			session=ses;
			callCrmListener=topUpSellerPostingResponse;
			assignId=assigned_id;
			serviceTax=serviceTaxAmount;
			operation=oper;
		}

		public postingTopDataOfSeller(String rDId2, String sellerFinalId,
				int topup_amount, String session2, String assin,
				String serviceTaxAmount,
				CRMListener topUpSellerPostingResponse, String o,
				NewSeller sellerInfo) {
			// TODO Auto-generated constructor stub
			
			rdId=rDId2;
			sellerId=sellerFinalId;
			topUpQuantity=topup_amount;
			session=session2;
			callCrmListener=topUpSellerPostingResponse;
			assignId=assin;
			serviceTax=serviceTaxAmount;
			operation=o;
			newSeller=sellerInfo;
		}

		@Override
		protected String doInBackground(Void... params) 
		{
			String url=Utility.loginAPIUrl;
			try
			{
				HttpPost localHttpPost = new HttpPost(url);
				DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
				ArrayList<BasicNameValuePair> localArrayList = new ArrayList<BasicNameValuePair>(3);

				localArrayList.add(new BasicNameValuePair("sessionName", session));
				localArrayList.add(new BasicNameValuePair("elementType", "SellerTopup"));
				//				localArrayList.add(new BasicNameValuePair("assigned_user_id", id));
				localArrayList.add(new BasicNameValuePair("operation", operation));

				JSONObject data = new JSONObject();
				float sellerActuaTopUP=topUpQuantity*Float.parseFloat("889.99");
				double roundOff = (double) Math.round(sellerActuaTopUP * 100) / 100;
				float amtWithServiceTax=topUpQuantity*Float.parseFloat("1000");
				double roundAmtWithServiceTax = (double) Math.round(amtWithServiceTax * 100) / 100;
				data.put("seller_type","National");
				data.put("rdid", rdId);
				data.put("seller_actual_topup_amt", roundOff);
				data.put("amt_with_servicetax", roundAmtWithServiceTax);
				data.put("assigned_user_id", assignId);
				data.put("seller_topup_qty",topUpQuantity);
				data.put("seller_amt_service_tax",serviceTax);
				data.put("topupamount", "889.99");
			
				if(operation.equalsIgnoreCase("create"))
					data.put("accountid", sellerId);
				else
				{
//					data.put("id", sellerId);
//					data.put("accountname", newSeller.getBussiness_name());
//					data.put("phone", newSeller.getMobile_no());
//					data.put("email1", newSeller.getEmail_id());
//					data.put("bill_code", newSeller.getBilling_pin_code());
//					data.put("ship_code", newSeller.getShipping_pin_code());
//					data.put("bill_street", newSeller.getBilling_address());
//					data.put("ship_street", newSeller.getShipping_address());
//					data.put("bill_city", newSeller.getBilling_city());
//					data.put("ship_city", newSeller.getShipping_city());
//					data.put("bill_state", newSeller.getBilling_state());
//					data.put("ship_state", newSeller.getShipping_state());
//					
//					
//					data.put("industry", "Mobile phones");
//					data.put("product_brand", "Samsung");
//					
//					
////					data.put("assigned_user_id", id);
//					data.put("bill_country", "India");
//					data.put("ship_country", "India");
//					data.put("beneficary_name", newSeller.getBeneficaryName());
//					data.put("ac_no", newSeller.getAccount_no());
//					data.put("bank_name", newSeller.getBank_name());
//					data.put("branch_address", newSeller.getBranch());
//					data.put("ifsc_code", newSeller.getIfsc_code());
//					data.put("seller_type", "National");////////need for for discussion
//					data.put("enrollement_source", "App");////////need for for discussion
//					data.put("mobile_verified", "Yes");
//					data.put("latitude", newSeller.getLatitude());
//					data.put("longitude", newSeller.getLongitude());
////					data.put("topupamount", newSeller.getTopup_amount());
//					data.put("description", "New user");
//					data.put("cst_tin_no", newSeller.getCst_tin_number());
//					data.put("id", newSeller.getCrm_seller_id());
				}
				//				  data.put("seller_topup_qty",topUpQuantity);


				localArrayList.add(new BasicNameValuePair ("element",data.toString()));

				System.out.println("---ABHI Value of request parameters is--"+localArrayList.toString());
				localHttpPost.setEntity(new UrlEncodedFormEntity(localArrayList));
				this.response = EntityUtils.toString(localDefaultHttpClient.execute(localHttpPost).getEntity());
				Log.d("JSON", response);
				return this.response;
			}
			catch (Exception localException)
			{
				localException.printStackTrace();
				return null;
			}
		}
		@Override
		protected void onPostExecute(String result) 
		{
			callCrmListener.getCAllback(result);
		}
	}
	
	/**
	 * @author ABHI-class for getting the list of Sellers from the server and even parsing the response and storing it into the database
	 */
	public static class GetPincode extends AsyncTask<String, String, String>
	{
		String response = "",url="",username="";
		CRMListener crm;
		ListView sellerList;

		public GetPincode(CRMListener crmListener, String string)
		{
			crm=crmListener;
			username = string;
			//			progressBar.setVisibility(View.VISIBLE);
//			sharedpreferences = MainActivity.current_Activity.getSharedPreferences(Utilities.MyPREFERENCES, Context.MODE_PRIVATE);
		}
		
		@Override
		protected void onPreExecute() 
		{
			//			progDialog.setMessage("Loading, Please wait...");
			//			progDialog.setIndeterminate(false);
			//			progDialog.show();
//			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params)
		{
			url=Utility.getPincodeUrl;
			try
			{
				HttpPost localHttpPost = new HttpPost(url);
				DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
				ArrayList<BasicNameValuePair> localArrayList = new ArrayList<BasicNameValuePair>(3);
				localArrayList.add(new BasicNameValuePair("api_user", "AskMeService"));
				localArrayList.add(new BasicNameValuePair("api_password", "Service@2014"));
				localArrayList.add(new BasicNameValuePair("user_name", username));
				//				localArrayList.add(new BasicNameValuePair("contact", "9999988888"));
				localHttpPost.setEntity(new UrlEncodedFormEntity(localArrayList));
				this.response = EntityUtils.toString(localDefaultHttpClient.execute(localHttpPost).getEntity());

				return this.response;
			}
			catch (Exception localException)
			{
				while (true)
					localException.printStackTrace();
			}
		}

		@Override
		protected void onPostExecute(String paramString)
		{
			super.onPostExecute(paramString);
			System.out.println("Value of seller response is----ABHI-----"+paramString);
			crm.getCAllback(paramString);
		}
	}
	
	/**
	 * @author ABHI
	 * Class for Getting the user Access Key by making query request
	 */
	public static class GetLeads extends AsyncTask<Void, Void, String>
	{
		CRMListener callCrmListener;
		String response,userName,startDay="",endDay="";
		HttpGet httpGet=null;
		public GetLeads(CRMListener listener,String uName,String startday,String endday) 
		{
			callCrmListener =listener;
			userName=uName;
			startDay=startday;
			endDay=endday;
		}
		@Override
		protected String doInBackground(Void... params) 
		{
			String url=Utility.getLeadsUrl;
			try 
			{
				HttpClient httpClient = new DefaultHttpClient();
				JSONObject data=new JSONObject();
				data.put("date_before", startDay);
				data.put("date_after", endDay);
				
//				List<NameValuePair> parms = new LinkedList<NameValuePair>();
//				parms.add(new BasicNameValuePair("date_before",startDay));
//				parms.add(new BasicNameValuePair("date_after", endDay));
//				String paramString = URLEncodedUtils.format(parms, "utf-8");

				String encodedUrl="{\"date_before\":\""+endDay +"\",\""+"date_after\":\""+startDay +"\"}" ;
				
				System.out.println("===Value of Url is --"+url
						+"index.php?username="+userName+"&accesskey="+
						Utility.UserAPIAccessKey+"&operation=getLeads&parms= "+java.net.URLEncoder.encode(encodedUrl));
				
				httpGet = new HttpGet(url
						+"index.php?username="+userName+"&accesskey="+
						Utility.UserAPIAccessKey+"&operation=getLeads&parms="+java.net.URLEncoder.encode(encodedUrl));
//					HttpPost localHttpPost = new HttpPost(url+"index.php?");
//					DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
//					ArrayList<BasicNameValuePair> localArrayList = new ArrayList<BasicNameValuePair>(3);

//					localArrayList.add(new BasicNameValuePair("username", userName));
//					localArrayList.add(new BasicNameValuePair("accesskey",Utility.UserAPIAccessKey));
//					localArrayList.add(new BasicNameValuePair("operation", "getLeads"));
						HttpResponse httpResponse = httpClient.execute(httpGet);
						System.out.println("httpResponse");
						InputStream inputStream = httpResponse.getEntity().getContent();
						InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
						BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
						StringBuilder stringBuilder = new StringBuilder();
						String bufferedStrChunk = null;
						while((bufferedStrChunk = bufferedReader.readLine()) != null)
						{
							stringBuilder.append(bufferedStrChunk);
						}
						System.out.println("Returning value of doInBackground :" + stringBuilder.toString());
						response= stringBuilder.toString();
						
				}
				catch (Exception localException)
				{
					localException.printStackTrace();
					return null;
				}
			return response;
		}
		@Override
		protected void onPostExecute(String result) 
		{
			callCrmListener.getCAllback(result);
		}
	}
}
