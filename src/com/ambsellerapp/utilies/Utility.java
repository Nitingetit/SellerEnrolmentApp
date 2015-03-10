package com.ambsellerapp.utilies;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.ambsellerapp.asynctasks.AsyncUtilies;

public class Utility
{
/*//  Live Server URL
 	public static String getChallengeUrl="http://ncrm.askmebazaar.com/webservice.php";
	public static String loginAPIUrl="http://ncrm.askmebazaar.com/webservice.php";
	public static String fetchDataUrl="http://ncrm.askmebazaar.com/webservice.php";
	public static String smsPostURl="http://push3.maccesssmspush.com/servlet/com.aclwireless.pushconnectivity.listeners.TextListener?";
	public static String getPincodeUrl="http://apps.askmebazaar.com/salesterritorybuilder/index.php?r=crm-service/get-pincode-list";
	
	public static String APIAccessKey="3YoP4UIwErjAfCxv";
	public static String adminUser="app_user";
*/	
// Staging server URL
  	public static String getChallengeUrl="http://staging.crm.askmebazaar.com/webservice.php";
	public static String loginAPIUrl="http://staging.crm.askmebazaar.com/webservice.php";
	public static String fetchDataUrl="http://staging.crm.askmebazaar.com/webservice.php";
	public static String smsPostURl="http://push3.maccesssmspush.com/servlet/com.aclwireless.pushconnectivity.listeners.TextListener?";
	public static String getPincodeUrl="http://apps.qa.askmebazaar.com/salesterritorybuilder/index.php?r=crm-service/get-pincode-list";

	public static String APIAccessKey="XncqyFeW6MjcaqMG";
	public static String adminUser="admin";
	public static String getLeadsUrl="http://staging.crm.askmebazaar.com/askmecrmwrapper/";
	public static String mobileNumberFinal="";
	public static String UserAPIAccessKey="";
	
	
	//added by author Ravi Kumar
		public static String fetchStateandcity="http://developers.askme.com/service/service.ashx?method=getlocationbypin";
		public static String fetchleadtokenurl="http://staging.crm.askmebazaar.com/webservice.php";
		public static String testLeadsSerch = "http://staging.crm.askmebazaar.com/webservice.php";
	//Ravi Kumar added portion ends here
	
	public static final String MyPREFERENCES = "AmbSellerAppSharedPreferences";
	
	public static final String [] roleIdArray={"H16","H17","H18","H23","H24","H25","H31","H32","H4","H42","H43","H46","H47","H54","H55"};
//	public static String username="admin";
//	public static String password="admin";
	/** Method for converting the String into MD5
	 * @param String password
	 * @return
	 */
	public static String convertPassMd5(String pass)
	{
		String password = null;
		MessageDigest mdEnc;
		try 
		{
			mdEnc = MessageDigest.getInstance("MD5");
			mdEnc.update(pass.getBytes(), 0, pass.length());
			pass = new BigInteger(1, mdEnc.digest()).toString(16);
			while (pass.length() < 32) 
			{
				pass = "0" + pass;
			}
			password = pass;
		} 
		catch (NoSuchAlgorithmException e1) 
		{
			e1.printStackTrace();
		}
		return password;
	}
	
	/** Method for converting the sending the sms verification code to the SMS gateway
	 * @return
	 */
	public static void sendSMS(String uniqueKey,String phone)
	{
		String responseStatus = phone;
		String request="Your verification key for askmeBazaar seller registration is "+uniqueKey;
		new AsyncUtilies.SendSMSAsyn(responseStatus, request).execute();
//		postSMS.execute(null);
//		return responseStatus;
	}
}
