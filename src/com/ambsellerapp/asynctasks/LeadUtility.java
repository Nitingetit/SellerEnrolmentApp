package com.ambsellerapp.asynctasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.ambsellerapp.listeners.CRMListener;
import com.ambsellerapp.modals.NewLead;
import com.ambsellerapp.utilies.Utility;

public class LeadUtility {
	/**
	 * @author Ravi-class for getting state and city on selecting pin code
	 */
	
	public static class GetStateCity extends AsyncTask<String, String, String>
	{
		String response="",request="",pincode="";
		CRMListener crm_state_city;
		
		public GetStateCity(CRMListener crmListener, String string){
			crm_state_city=crmListener;
			pincode=string;
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.d("MYSTATE", pincode);
			request=Utility.fetchStateandcity+"&pin="+pincode+"&format=json";
			Log.d("MYSTATE", request);
			try{
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(request);
				httpGet.setHeader("Authorization", "YXNrbWU6YXNrbWU=");
				HttpResponse httpResponse = httpClient.execute(httpGet);
				InputStream inputStream = httpResponse.getEntity().getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				StringBuilder stringBuilder = new StringBuilder();
				String bufferedStrChunk = null;
				while((bufferedStrChunk = bufferedReader.readLine()) != null)
				{
					stringBuilder.append(bufferedStrChunk);
				}
				response= stringBuilder.toString();
				Log.d("MYSTATE", response);
			}
			catch(Exception stateandcityException){
				while (true)
					stateandcityException.printStackTrace();
			}
			System.out.println("respone of city -"+response);
			return response;
		}
		@Override
		protected void onPostExecute(String result) {
			crm_state_city.getCAllback(result);
			Log.d("MYSTATE", result);
			
		}	
	}
	/**
	 * @author Ravi-class for getting state and city ends here
	 */
	
	/**
	 * @author Ravi-class for getting token of admin user
	 */
	
	
	public static class SendLeadDatatoCRMStep1 extends AsyncTask<String, String, String>{
		String response="",request="",username="",url="";
		CRMListener crm_lead_data;
		HttpGet httpGet=null;
		public SendLeadDatatoCRMStep1(CRMListener crmListener, String string){
			crm_lead_data=crmListener;
			username=string;
		}

		@Override
		protected String doInBackground(String... params) {
			url=Utility.fetchleadtokenurl;
			HttpClient httpClient = new DefaultHttpClient();
			if(username.equals(""))
				httpGet = new HttpGet(url +"?operation=getchallenge&username="+Utility.adminUser);	
			else
				httpGet = new HttpGet(url +"?operation=getchallenge&username="+username);	
			
			HttpResponse httpResponse;
			try {
				httpResponse = httpClient.execute(httpGet);
				InputStream inputStream = httpResponse.getEntity().getContent();			
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);			
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);			
				StringBuilder stringBuilder = new StringBuilder();
				String bufferedStrChunk = null;			
				while((bufferedStrChunk = bufferedReader.readLine()) != null)
				{
					stringBuilder.append(bufferedStrChunk);
				}				
				response= stringBuilder.toString();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			crm_lead_data.getCAllback(result);
		}		
	}
	
	/**
	 * @author Ravi-class for getting token of adminuser ends here
	 */
	
	/**
	 * @author Ravi-class for getting token of admin user
	 */
	
	
	public static class SendLeadDatatoCRMStep2 extends AsyncTask<String, String, String>{
		String response="",request="",username="",url="",user_token="",apiAccessKey="";
		CRMListener crm_lead_data;
		HttpGet httpGet=null;
		public SendLeadDatatoCRMStep2(String token,CRMListener crmListener, String string){
			crm_lead_data=crmListener;
			username=string;
			user_token=token;
		}

		@Override
		protected String doInBackground(String... params) {
			url=Utility.fetchleadtokenurl;
			HttpClient httpClient = new DefaultHttpClient();
			httpGet = new HttpGet(url +"?operation=getchallenge&username="+Utility.adminUser);		
			
			HttpResponse httpResponse;
			try {
				HttpPost localHttpPost = new HttpPost(url);
				DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
				ArrayList<BasicNameValuePair> localArrayList = new ArrayList<BasicNameValuePair>(3);
				localArrayList.add(new BasicNameValuePair("operation", "login"));
				if(username.equals(""))
					apiAccessKey=Utility.APIAccessKey;
				else
					apiAccessKey=Utility.UserAPIAccessKey;
				apiAccessKey=user_token+apiAccessKey;
				apiAccessKey=Utility.convertPassMd5(apiAccessKey);
				
				if(username.equals(""))
					localArrayList.add(new BasicNameValuePair("username", Utility.adminUser));
				else
					localArrayList.add(new BasicNameValuePair("username", username));
				localArrayList.add(new BasicNameValuePair("accessKey", apiAccessKey));
				localHttpPost.setEntity(new UrlEncodedFormEntity(localArrayList));
				response = EntityUtils.toString(localDefaultHttpClient.execute(localHttpPost).getEntity());
				Log.d("JSON", response);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}					
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			if(result!=null)
				crm_lead_data.getCAllback(result);
		}		
	}
	
	/**
	 * @author Ravi-class for getting token of adminuser ends here
	 */
	
	/**
	 * @author Ravi-class for getting token of admin user
	 */
	
	
	public static class SendLeadDatatoCRMStep3 extends AsyncTask<String, String, String>{
		String response="",request="",user_name="",url="",query="",adminSession="";
		CRMListener crm_lead_data;
		HttpGet httpGet=null;
		public SendLeadDatatoCRMStep3(String username, String session,CRMListener crmListener){
			crm_lead_data=crmListener;
			adminSession=session;
			user_name=username;
		}

		@Override
		protected String doInBackground(String... params) {
			url=Utility.fetchleadtokenurl;
			HttpClient httpClient = new DefaultHttpClient();
			httpGet = new HttpGet(url +"?operation=getchallenge&username="+Utility.adminUser);
			query="Select * from Users where user_name = '"+user_name+"' ;";
			Log.d("MYLEAD", query);
		    request = url + "?operation=query&sessionName="+adminSession+"&query="+java.net.URLEncoder.encode(query);
			Log.d("MYLEAD", request);
		    try{
		    HttpGet httpGet = new HttpGet(request);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuilder stringBuilder = new StringBuilder();
			String bufferedStrChunk = null;
			while((bufferedStrChunk = bufferedReader.readLine()) != null)
			{
				stringBuilder.append(bufferedStrChunk);
			}
			response= stringBuilder.toString();
			}
			catch(Exception e){e.printStackTrace();}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			if(result!=null)
				crm_lead_data.getCAllback(result);
		}		
	}
	
	/**
	 * @author Ravi-class for getting token of adminuser ends here
	 */
	
	public static class SendLeadDatatoCRMStep4 extends AsyncTask<String, String, String>{
		CRMListener crm_lead_data;
		String session_Name,response,User_Id,id,fromWhere="";
		String operation="";
		NewLead new_Lead;
		public SendLeadDatatoCRMStep4(String sessionName,String Userid,NewLead newLead,CRMListener crmListener,String lead_operation){
			session_Name=sessionName;
			User_Id=Userid;
			new_Lead=newLead;
			crm_lead_data=crmListener;
			operation=lead_operation;
		}
		
		public SendLeadDatatoCRMStep4(String sessionName,String id1,CRMListener crmListener,String lead_operation,String fromWhere1,NewLead lead,String userId){
			session_Name=sessionName;
			id=id1;
			User_Id = userId;
			fromWhere = fromWhere1;
			crm_lead_data=crmListener;
			operation=lead_operation;
			new_Lead = lead;
		}
		@Override
		protected String doInBackground(String... params) {
			String url=Utility.fetchleadtokenurl;
			try{
				HttpPost localHttpPost = new HttpPost(url);
				DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
				ArrayList<BasicNameValuePair> localArrayList = new ArrayList<BasicNameValuePair>(3);

				localArrayList.add(new BasicNameValuePair("operation", operation));
				localArrayList.add(new BasicNameValuePair("sessionName", session_Name));
				localArrayList.add(new BasicNameValuePair("elementType", "Leads"));
				
					JSONObject data = new JSONObject();
					if(fromWhere.equalsIgnoreCase("conversion")){
						String leadid = "";
					       if(!new_Lead.getId().contains("10x")){
					        leadid = "10x"+new_Lead.getId();
					       }
					       else{
					        leadid = new_Lead.getId();
					       }
					       data.put("id", leadid);
						data.put("leadstatus","Converted");
						
						String assignUserid = "";
					       if(!new_Lead.getAssigndUserId().contains("19x")){
					    	   assignUserid = "19x"+new_Lead.getAssigndUserId();
					       }
					       else{
					    	   assignUserid = new_Lead.getAssigndUserId();
					       }
						
						data.put("assigned_user_id", assignUserid);
						data.put("company", new_Lead.getBusiness_name());
						data.put("mobile", new_Lead.getMobile_number());
						data.put("code", new_Lead.getPin_code());
						data.put("appt_date", new_Lead.getDate());
						data.put("appt_time", new_Lead.getTime());
						data.put("leadsource", new_Lead.getLeadSource());
						data.put("description", new_Lead.getRemarks());
						data.put("email", "");
						data.put("beneficiary_name", "");
						data.put("bank_name", "");
						data.put("branch_address", "");
						data.put("ac_no", "");
						data.put("ifsc_code", "");
						data.put("bill_pincode", new_Lead.getPin_code());
						data.put("bill_state", "");
						data.put("bill_city", "");
						data.put("bill_lane", "");
						data.put("city", new_Lead.getCity());
						data.put("state", new_Lead.getState());
						data.put("lane", new_Lead.getAddress());
						data.put("reason_lead_status", new_Lead.getRemarks());
					}
					else{
						String assignUserid = "";
					       if(!User_Id.contains("19x")){
					    	   assignUserid = "19x"+User_Id;
					       }
					       else{
					    	   assignUserid = User_Id;
					       }
						
						data.put("assigned_user_id", assignUserid);
						data.put("assigned_user_id", User_Id);
						data.put("company", new_Lead.getBusiness_name());
						data.put("mobile", new_Lead.getMobile_number());
						data.put("code", new_Lead.getPin_code());
						data.put("appt_date", new_Lead.getDate());
						data.put("appt_time", new_Lead.getTime());
						if(operation.equals("create"))
							data.put("leadstatus", "Pending");
						else
							data.put("leadstatus", new_Lead.getStatus());
						data.put("leadsource", "SEA");
						data.put("description", new_Lead.getRemarks());
						data.put("email", "");
						data.put("beneficiary_name", "");
						data.put("bank_name", "");
						data.put("branch_address", "");
						data.put("ac_no", "");
						data.put("ifsc_code", "");
						data.put("bill_pincode", new_Lead.getPin_code());
						data.put("bill_state", "");
						data.put("bill_city", "");
						data.put("bill_lane", "");
						data.put("city", new_Lead.getCity());
						data.put("state", new_Lead.getState());
						data.put("lane", new_Lead.getAddress());
						if(operation.equals("update")){
							String leadid = "";
						       if(!new_Lead.getId().contains("10x")){
						        leadid = "10x"+new_Lead.getId();
						       }
						       else{
						        leadid = new_Lead.getId();
						       }
						       data.put("id", leadid);
							data.put("reason_lead_status", new_Lead.getRemarks());
						}
					}
				localArrayList.add(new BasicNameValuePair ("element",data.toString()));
				Log.d("MYLEAD", localArrayList.toString());
				localHttpPost.setEntity(new UrlEncodedFormEntity(localArrayList));
				
				response=EntityUtils.toString(localDefaultHttpClient.execute(localHttpPost).getEntity());
				Log.d("response",response);
			}
			catch(Exception e){
				e.printStackTrace();
				}
			return response;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(result!=null)
				crm_lead_data.getCAllback(result);
		}	
	}
	
}
