package com.ambsellerapp.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import arlut.csd.crypto.MD5Crypt;

import com.ambsellerapp.asynctasks.AsyncUtilies;
import com.ambsellerapp.listeners.CRMListener;
import com.AMBSEA.R;
import com.ambsellerapp.utilies.NetworkUtils;
import com.ambsellerapp.utilies.Utility;

public class SplashFragment extends Fragment
{
	private View contentView;
	private boolean checkInternet = false;
	public static EditText usernameEdittext;
	public static EditText passwordEdittext;
	private Button login;
	private SharedPreferences sharedpreferences;
	private ProgressBar progressBar;
	private String successStatus="";
	JSONArray jArr=null;
	private ArrayList<String> arrayPinCode = new ArrayList<String>();
	private String userid="",roleId="";
	
	public String getVersion() {
	    String v = "";
	    try {
	        v = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
	    } catch (Exception e) {
	        // Huh? Really?
	    }
	    return v;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		contentView = inflater.inflate(R.layout.splash_layout, container, false);
		sharedpreferences = getActivity().getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		usernameEdittext = (EditText) contentView.findViewById(R.id.username_text_field);
		passwordEdittext = (EditText) contentView.findViewById(R.id.password_text_field);

		login = (Button) contentView.findViewById(R.id.continue_button);
		progressBar=(ProgressBar)contentView.findViewById(R.id.progress);
		checkInternet = NetworkUtils.isNetworkAvailable(getActivity());
		final View includedLayout = contentView.findViewById(R.id.includeLayout);
		includedLayout.setVisibility(View.VISIBLE);
		TextView tView = (TextView) contentView.findViewById(R.id.versionCode);
		String version = getVersion()+"";
		tView.setText("Version "+version);
		if(!sharedpreferences.getString("username","").equalsIgnoreCase(""))
		{
			usernameEdittext.setText(sharedpreferences.getString("username",""));
			passwordEdittext.setText(sharedpreferences.getString("password",""));
		}
		
//		usernameEdittext.setText("north_SO");
//		passwordEdittext.setText("getit@123");
//		usernameEdittext.setText("SO_Gunjan");
//		passwordEdittext.setText("getit@123");
//		usernameEdittext.setText("Swift_Logistics");
//		passwordEdittext.setText("getit@123");
//		usernameEdittext.setText("SO_Vijayprasad");
//		passwordEdittext.setText("12345");
//		usernameEdittext.setText("TEST_SO");
//		passwordEdittext.setText("abc@123");
//		usernameEdittext.setText("t_so");
//		passwordEdittext.setText("test");
//		usernameEdittext.setText("sonorth");
//		passwordEdittext.setText("test");
//		usernameEdittext.setText("SO_Hiteshpopli");
//		passwordEdittext.setText("abc@123");
		login.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(checkEnteredDetails())///Checking weather the pincode and Phone field is not empty
				{
//					if(inputValidation())///Checking weather required length parameters is inputed or not
//					{
						if(checkInternet)
						{
							progressBar.setVisibility(View.VISIBLE);
							new AsyncUtilies.FirstAsyn(getChanllangerCompleteListener(),usernameEdittext.getText().toString()).execute();
						}
						else if(!sharedpreferences.getString("username","").equalsIgnoreCase(""))// //Checking weather the shared preference username is empty or not
						{
								progressBar.setVisibility(View.VISIBLE);
								Thread thread = new Thread(null, loadMoreListItems);
								thread.start();
						}
						else
						{
							getActivity().runOnUiThread(new Runnable() 
							{
								@Override
								public void run() 
								{
									Toast toast = Toast.makeText(getActivity(),"Please check Internet!!!",Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
									getActivity().finish();
								}
							});
						}
							
//					}
				}
				else
				{
					Toast toast = Toast.makeText(getActivity(),"Please fill all the fields!!!",Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}
		});
		return contentView;
	}

//	@Override
//	public void onResume()
//	{
//		System.out.println("----value of Sharedpreference data is --"+sharedpreferences.getString("data",""));
//		//		if (sharedpreferences.getString("data","").equalsIgnoreCase("true"))// //Checking weather the shared preference username is empty or not
//		//		{
//		//			Thread thread = new Thread(null, loadMoreListItems);
//		//			thread.start();
//		//		} 
//		//		else 
//		//		{
//		
//		//		}
//		super.onResume();
//	}

	private Runnable loadMoreListItems = new Runnable() 
	{
		@Override
		public void run()
		{
			try 
			{
				Thread.sleep(1000);
//				System.out.println("-----Value of internet---" + checkInternet);
				NameRegistrationFragment newFragment = new NameRegistrationFragment();
//				Bundle args = new Bundle();
//				newFragment.setArguments(args);

				FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.container, newFragment);
				transaction.commit();
				progressBar.setVisibility(View.VISIBLE);
			} 
			catch (InterruptedException e) 
			{
			}
		}

	};

	private CRMListener getChanllangerCompleteListener() 
	{
		CRMListener crmListener = new CRMListener() 
		{
			@Override
			public void getCAllback(String obj) 
			{
				System.out.println("----Value of Object returned value is --- "+obj);
				String token="";
				try 
				{
					JSONObject jObj=new JSONObject(obj);
					String successStatus=jObj.get("success").toString();
					if(successStatus.equalsIgnoreCase("true"))
					{
						JSONObject jsonTokenObj=new JSONObject(jObj.get("result").toString());
						token=(String)jsonTokenObj.get("token");
						new AsyncUtilies.SecondAsyn(token,usernameEdittext.getText().toString(),getLoginCompleteListener()).execute();
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

	private CRMListener getLoginCompleteListener() 
	{
		CRMListener crmListener = new CRMListener() 
		{
			@Override
			public void getCAllback(String obj)
			{
				System.out.println("----value after getting the session is --- "+obj);
				String session="",id="";
				try 
				{
					JSONObject jObj=new JSONObject(obj);
					String successStatus=jObj.get("success").toString();
					if(successStatus.equalsIgnoreCase("true"))
					{
						JSONObject jsonTokenObj=new JSONObject(jObj.get("result").toString());
						session=(String)jsonTokenObj.get("sessionName");
						id=(String)jsonTokenObj.get("userId");
						new AsyncUtilies.ThirdAsyn(usernameEdittext.getText().toString(),
								passwordEdittext.getText().toString(),session,getDataCompleteListener()).execute();
					}
					else
					{
						getActivity().runOnUiThread(new Runnable() 
						{
							@Override
							public void run() 
							{
								Toast toast = Toast.makeText(getActivity()," Username is invalid !",Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
								progressBar.setVisibility(View.GONE);
							}
						});
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

	private CRMListener getDataCompleteListener() 
	{
		CRMListener crmListener = new CRMListener() 
		{
			@Override
			public void getCAllback(String obj) 
			{
				//TODO				 final result
				String successStatus="",inputtedUserPassword="",userPass="",temp="";
				progressBar.setVisibility(View.GONE);
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
						roleId=resultObj.getString("roleid");
						inputtedUserPassword=passwordEdittext.getText().toString();
						temp="$1$"+usernameEdittext.getText().toString().substring(0, 2)+"0000000";


						System.out.println("-----ABHI value of temp is==="+temp);
						temp=MD5Crypt.crypt(inputtedUserPassword, temp);
						System.out.println("----Converted value of password is ---"+temp);
						System.out.println("----Compare value of password is ---"+userPass.compareTo(temp));
						System.out.println("----Result of Comparing role Id--"+Arrays.asList(Utility.roleIdArray).contains(roleId));
						Utility.UserAPIAccessKey=resultObj.getString("accesskey");
						if(userPass.compareTo(temp)==0)
						{
							if(Arrays.asList(Utility.roleIdArray).contains(roleId))
							{	
								if(checkInternet)
								{
									new AsyncUtilies.GetPincode(getPinCode(),usernameEdittext.getText().toString()).execute();
								}
							}
							else
							{
								getActivity().runOnUiThread(new Runnable() 
								{
									@Override
									public void run() 
									{
										Toast toast = Toast.makeText(getActivity(),"Your Role doesn't allow to use these application!",Toast.LENGTH_SHORT);
										toast.setGravity(Gravity.CENTER, 0, 0);
										toast.show();
									}
								});
							}

						}
						else
						{
							getActivity().runOnUiThread(new Runnable() 
							{
								@Override
								public void run() 
								{
									Toast toast = Toast.makeText(getActivity(),"Username / password is incorrect !",Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								}
							});
						}
					}
				}

				catch (JSONException e) 
				{
					e.printStackTrace();
					getActivity().runOnUiThread(new Runnable() 
					{
						@Override
						public void run() 
						{
							Toast toast = Toast.makeText(getActivity(),"Username / password is incorrect !",Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
					});
				}
				
	

				//				}
				//				else 
				//				{
				//					getActivity().runOnUiThread(new Runnable() 
				//					{
				//						@Override
				//						public void run() 
				//						{
				//							Toast toast = Toast.makeText(getActivity(),"Please check Internet!!!",Toast.LENGTH_SHORT);
				//							toast.setGravity(Gravity.CENTER, 0, 0);
				//							toast.show();
				//							getActivity().finish();
				//						}
				//					});
				//				}
			}
		};
		return crmListener;
	}


	/** Method for checking the pin code and phone empty Parameter
	 * @return true if they are not empty
	 */
	private boolean checkEnteredDetails()
	{
		if (usernameEdittext.getText().toString().equals(""))
			return false;
		if (passwordEdittext.getText().toString().equals(""))
			return false;
		return true;
	}
	/** Method for checking the pin code and phone required Parameter lenght is inputed or not
	 * @return true 
	 */
	private boolean inputValidation() 
	{
		if (usernameEdittext.getText().toString().length() < 5) 
		{
			Toast.makeText(getActivity(), "Username must contain 5 digits.",Toast.LENGTH_SHORT).show();
			return false;
		}
		if (passwordEdittext.getText().toString().length() < 5) 
		{
			Toast.makeText(getActivity(),"Password must contain 5 digits.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	public CRMListener getPinCode()
	{
		CRMListener crmListener=new CRMListener() 
		{
			@Override
			public void getCAllback(String result) 
			{
				String token="";
				try 
				{
					System.out.println("---Value of return response is--"+result);
					JSONObject jObj=new JSONObject(result);
					successStatus=jObj.get("status").toString();

					if(successStatus.equalsIgnoreCase("Success"))
					{
						//						JSONObject jsonTokenObj=new JSONObject(jObj.get("data").toString());
						jArr=jObj.getJSONArray("data");
						for(int i = 0, count = jArr.length(); i< count; i++)
						{ 
							try 
							{ 
								arrayPinCode.add(jArr.getString(i));
							} 
							catch (JSONException e) 
							{
								e.printStackTrace();
							} 
						} 
						Editor editor=sharedpreferences.edit();

						/**
						 * Putting the PIN Code arraylist into the sharedpreference so that it can be used in the offline mode
						 */
						//						for(int i=0;i<arrayPinCode.size();i++)
						//						{
						//							editor.putString("pincodeList"+i,arrayPinCode.get(i));
						//						}
						Set<String> set = new HashSet<String>();
						set.addAll(arrayPinCode);
						editor.putStringSet("pincodeList", set);
						editor.putString("username", usernameEdittext.getText().toString());
						editor.putString("password", passwordEdittext.getText().toString());
						editor.putString("id", userid);
						editor.commit();
						
						Collections.sort(arrayPinCode);

						if(arrayPinCode.isEmpty())
						{
							arrayPinCode.add(0,"No PIN Code found");
						}
						
				
					

						HomeFragment newFragment = new HomeFragment();
						FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
						transaction.replace(R.id.container, newFragment);
						transaction.commit();

					}
					else
					{///In case of no pin code found
						Toast toast = Toast.makeText(getActivity(),"No PIN Code found!!!",Toast.LENGTH_SHORT);
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
		return crmListener;
	}
}
