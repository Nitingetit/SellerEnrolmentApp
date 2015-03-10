package com.ambsellerapp.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import arlut.csd.crypto.MD5Crypt;

import com.AMBSEA.R;
import com.ambsellerapp.activities.LauncherActivity;
import com.ambsellerapp.adapters.NothingSelectedSpinnerAdapter;
import com.ambsellerapp.asynctasks.AsyncUtilies;
import com.ambsellerapp.asynctasks.LeadUtility;
import com.ambsellerapp.database.DatabaseHandler;
import com.ambsellerapp.listeners.CRMListener;
import com.ambsellerapp.modals.NewLead;
import com.ambsellerapp.modals.NewSeller;
import com.ambsellerapp.utilies.NetworkUtils;
import com.ambsellerapp.utilies.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NameRegistrationFragment extends Fragment implements OnClickListener
{
	private View contentView;
	private boolean checkInternet = false;
	private EditText bussinessNameEdittext;
	private EditText mobileNumberEdittext;
	private EditText emailIdEdittext;

	private EditText shippingAddressEdittext;
	private Spinner shippingPincodeEdittext;
	private EditText shippingCityEdittext;
	private EditText shippingStateEdittext;

	private EditText billingAddressEdittext;
	private Spinner billingPincodeEdittext;
	private EditText billingCityEdittext;
	private EditText billingStateEdittext;
	private EditText verficationKeyEdittext;

	private EditText EdittextbankName;
	private EditText Edittextbranch;
	private EditText EdittextifscCode;
	private EditText EdittextaccountNumber;
	private EditText EdittextBeneficaryName;


	private Button verifyButton;
	private Button nextButton,btnPrevious;
	private SharedPreferences sharedpreferences;
	private ProgressBar progressBar,progressBarVerifiedButton;
	private int counter =0;
	private View layoutName,layoutBank,layoutLocation;

	public static final int BUSSINESS_NAME_REGISTRATION_FRAGMENT = 0;
	public String UniqueString="";
	private String[] amount = { "1", "2","3","4","5","6","7","8","9","10" };
	//private EditText topUpDropDown;

	private MapView mMapView;
	private GoogleMap googleMap;
	private com.ambsellerapp.utilies.GPSTracker gps;
	private LatLng cameraLatLng = null;
	private Button BtnMarkLocation;
	private EditText edittextLocation;
	private Button btnClearAll;
	private NewSeller newSeller;
	private long id=0;
	private String successStatus="";
	private double latitude=0;
	private double longitude=0;
	private String adminSession="",fromWhere="abc",mobileExistanceCheck="abc",userSession="";
	private NewSeller sellerInfo=null;
	private String sellerFinalId;
	private String rDId;
	//private LinearLayout spinnerLayout;
	private TextView txtViewSellerBalance;
	private LinearLayout LinearLayoutCurentBalance;
	public String verficationCode="";
	//private TextView TxtViewActualAmount;
	//private TextView TxtViewServiceTax;
	//private TextView TxtViewTotalAmount;
	private boolean isNumberNotExists=false;
	private TextView TxtViewQuestionMark,TxtViewVerificationKey;
	private DatabaseHandler db=null;
	private CheckBox makeBillingSameAsShipping;
	private boolean isBillingAddressIsSameAsShipping=false;
	private EditText editTextCSTTINNumber;
	private boolean isEditButtonClicked=false,isReVerifyClicked=false,isMobileNumberVerified=false;
	private LinearLayout LinearLayoutVerificationKey;
	private ArrayList<String> arrayPinCode = new ArrayList<String>();
	JSONArray jArr=null;
	private boolean isAdminSessionFound=false;
	private String serviceTax="";
	private NewLead myLead;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setHasOptionsMenu (true);
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		if(fromWhere.equalsIgnoreCase("abc"))
		{
			menu.findItem(R.id.registration).setVisible(false).setEnabled(true);
		}
		else
		{
			menu.findItem(R.id.registration).setVisible(true).setEnabled(true);
		}
		menu.findItem(R.id.create_leads).setVisible(true).setEnabled(true);
		menu.findItem(R.id.leads).setVisible(true).setEnabled(true);
		menu.findItem(R.id.refresh).setVisible(false).setEnabled(false);
		menu.findItem(R.id.sellerList).setVisible(true).setEnabled(true);
		menu.findItem(R.id.search).setVisible(false).setEnabled(false);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		contentView = inflater.inflate(R.layout.registration, container, false);
		getActivity().getActionBar().show();
		getActivity().getActionBar().setDisplayShowCustomEnabled(false);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().getActionBar().setDisplayShowTitleEnabled(true);
		checkInternet = NetworkUtils.isNetworkAvailable(getActivity());
		sharedpreferences = getActivity().getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		Bundle extras = getArguments(); 
		settingIds();///Call for Setting the ids of the Views inside the layout
		if (extras != null) 
		{
			sellerInfo=(NewSeller) extras.getSerializable("sellerArraylist");
			myLead=(NewLead) extras.getSerializable("leadsInfo");
			fromWhere=extras.getString("from");

		}
		com.ambsellerapp.activities.LauncherActivity.fromLead=0;
		
		if(fromWhere.equalsIgnoreCase("leads")){
			com.ambsellerapp.activities.LauncherActivity.fromLead=1;
			getActivity().invalidateOptionsMenu();
			getActivity().getActionBar().setTitle(R.string.seller_detail_1);
			arrayPinCode.add(sellerInfo.getShipping_pin_code());
			ArrayAdapter<String> adapter_amount = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_spinner_item, arrayPinCode);
			adapter_amount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			shippingPincodeEdittext.setAdapter(adapter_amount);
			billingPincodeEdittext.setAdapter(adapter_amount);
		}
		
		else if(fromWhere.equalsIgnoreCase("abc"))
		{
			getActivity().getActionBar().setTitle(R.string.resgistration_1);
			getActivity().supportInvalidateOptionsMenu();
			if(checkInternet)
			{
				new AsyncUtilies.GetPincode(getPinCode(),sharedpreferences.getString("username","")).execute();
			}
			else
			{//Getting the list of pincode from the shared preference in case of offline mode
				Set<String> set = sharedpreferences.getStringSet("pincodeList", null);
				arrayPinCode=new ArrayList<String>(set);

				Collections.sort(arrayPinCode);
				if(arrayPinCode.isEmpty())
				{
					arrayPinCode.add("PIN Code not found");
				}
				ArrayAdapter<String> adapter_amount = new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_spinner_item, arrayPinCode);
				adapter_amount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				shippingPincodeEdittext.setAdapter( new NothingSelectedSpinnerAdapter
						( 
								adapter_amount,
								R.layout.contact_spinner_row_nothing_selected,getActivity()
								));
				billingPincodeEdittext.setAdapter( new NothingSelectedSpinnerAdapter
						( 
								adapter_amount,
								R.layout.contact_spinner_row_nothing_selected,getActivity()
								));

			}
		}
		else
		{
			getActivity().invalidateOptionsMenu();
			getActivity().getActionBar().setTitle(R.string.seller_detail_1);
			arrayPinCode.add(sellerInfo.getShipping_pin_code());
			ArrayAdapter<String> adapter_amount = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_spinner_item, arrayPinCode);
			adapter_amount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			shippingPincodeEdittext.setAdapter(adapter_amount);
			billingPincodeEdittext.setAdapter(adapter_amount);
		}
		System.out.println("-----Value of FromWhere is---"+fromWhere);


		SettingListeners();////Call for setting the listeners
		verifyButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		btnPrevious.setOnClickListener(this);
		btnClearAll.setOnClickListener(this);

		BtnMarkLocation.setOnClickListener(this);
		MapsInitializer.initialize(getActivity());
		mMapView.onCreate(savedInstanceState);
		layoutName.setVisibility(View.VISIBLE);
		layoutBank.setVisibility(View.GONE);
		layoutLocation.setVisibility(View.GONE);
		if(fromWhere.equalsIgnoreCase("leads")){
			allEdittextEditable();
			LinearLayoutVerificationKey.setVisibility(View.VISIBLE);
		}
		return contentView;
	}

	/** Method for settting the ids of the layouts
	 * @return void
	 */
	private void settingIds()
	{

		bussinessNameEdittext=(EditText) contentView.findViewById(R.id.bussiness_name_edittext);
		mobileNumberEdittext=(EditText) contentView.findViewById(R.id.mobile_no_edittext);
		emailIdEdittext=(EditText) contentView.findViewById(R.id.email_id_edittext);
		billingAddressEdittext=(EditText) contentView.findViewById(R.id.billing_address_edittext);
		shippingAddressEdittext=(EditText) contentView.findViewById(R.id.shipping_address_edittext);
		shippingPincodeEdittext=(Spinner) contentView.findViewById(R.id.pincode_edittext);
		billingPincodeEdittext=(Spinner)contentView.findViewById(R.id.billing_pincode_edittext);
		shippingCityEdittext=(EditText) contentView.findViewById(R.id.city_edittext);
		billingCityEdittext=(EditText)contentView.findViewById(R.id.billing_city_edittext);
		shippingStateEdittext=(EditText) contentView.findViewById(R.id.state_edittext);
		billingStateEdittext=(EditText)contentView.findViewById(R.id.billing_state_edittext);
		verficationKeyEdittext=(EditText) contentView.findViewById(R.id.verfication_key_edittext);
		verifyButton=(Button)contentView.findViewById(R.id.verify_button);
		verifyButton.setEnabled(false);
		nextButton=(Button)contentView.findViewById(R.id.btnNext);
		progressBar=(ProgressBar)contentView.findViewById(R.id.progressBar111);
		btnPrevious =(Button)contentView.findViewById(R.id.btnPrevious);
		layoutName = contentView.findViewById(R.id.nameLayout);
		layoutBank= contentView.findViewById(R.id.bankLayout);
		layoutLocation= contentView.findViewById(R.id.locationLayout);
		((LauncherActivity)getActivity()).currentFragment=BUSSINESS_NAME_REGISTRATION_FRAGMENT;///Setting the curent fragment for backButton Handling

		EdittextbankName=(EditText) contentView.findViewById(R.id.bank_name_edittext);
		EdittextifscCode=(EditText) contentView.findViewById(R.id.ifsc_edittext);
		EdittextaccountNumber=(EditText) contentView.findViewById(R.id.account_no_edittext);
		Edittextbranch=(EditText) contentView.findViewById(R.id.branch_edittext);

		//topUpDropDown=(EditText) contentView.findViewById(R.id.topUpDropDown);
		BtnMarkLocation=(Button)contentView.findViewById(R.id.markLocationButton);
		mMapView=(MapView)contentView.findViewById(R.id.mapView);
		edittextLocation=(EditText) contentView.findViewById(R.id.location_edittext);
		//		ArrayAdapter<String> adapter_amount = new ArrayAdapter<String>(getActivity(),
		//				android.R.layout.simple_spinner_item, amount);
		//		adapter_amount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//		topUpDropDown.setAdapter(adapter_amount);

		btnClearAll=(Button)contentView.findViewById(R.id.btnClearAll);
		checkInternet = NetworkUtils.isNetworkAvailable(getActivity());
		EdittextBeneficaryName=(EditText)contentView.findViewById(R.id.beneficary_name_edittext);
		//spinnerLayout=(LinearLayout)contentView.findViewById(R.id.spinnerLayout);
		txtViewSellerBalance=(TextView)contentView.findViewById(R.id.TxtViewSellerBalance);
		LinearLayoutCurentBalance=(LinearLayout)contentView.findViewById(R.id.LinearLayoutCurentBalance);
		//LinearLayoutServiceTax=(LinearLayout)contentView.findViewById(R.id.LinearLayoutServiceTax);
		LinearLayoutVerificationKey=(LinearLayout)contentView.findViewById(R.id.LinearLayoutVerificationKey);
		progressBarVerifiedButton=(ProgressBar)contentView.findViewById(R.id.progressBarVerifiedButton);
		//TxtViewActualAmount=(TextView)contentView.findViewById(R.id.TxtViewActualAmount);
		//TxtViewServiceTax=(TextView)contentView.findViewById(R.id.TxtViewServiceTax);
		//TxtViewTotalAmount=(TextView)contentView.findViewById(R.id.TxtViewTotalAmount);
		TxtViewQuestionMark=(TextView)contentView.findViewById(R.id.TxtViewQuestionMark);
		TxtViewVerificationKey=(TextView)contentView.findViewById(R.id.TxtViewVerificationKey);
		makeBillingSameAsShipping=(CheckBox)contentView.findViewById(R.id.BillingAddressSameAsShippingAddress);
		editTextCSTTINNumber=(EditText)contentView.findViewById(R.id.EditTextCSTTINNumber);


	}
	private void SettingListeners()
	{
		/**
		 * Copying the shipping address into billing address
		 */
		makeBillingSameAsShipping.setOnCheckedChangeListener( new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) 
			{
				if(isBillingAddressIsSameAsShipping==false)
				{
					billingAddressEdittext.setText(shippingAddressEdittext.getText().toString());
					billingPincodeEdittext.setSelection(shippingPincodeEdittext.getSelectedItemPosition());
					billingCityEdittext.setText(shippingCityEdittext.getText().toString());
					billingStateEdittext.setText(shippingStateEdittext.getText().toString());
					isBillingAddressIsSameAsShipping=true;
				}
				else
				{
					billingAddressEdittext.setText("");
					//					billingPincodeEdittext.setText("");
					billingCityEdittext.setText("");
					billingStateEdittext.setText("");
					isBillingAddressIsSameAsShipping=false;
				}

			}
		});

		mobileNumberEdittext.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) 
			{
				if(verifyButton.getText().equals("verified"))
				{
					verifyButton.setText("Reverified");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) 
			{
			}

			@Override
			public void afterTextChanged(Editable s) 
			{
				//				if(s.toString().charAt(0)==0)
				//				{
				//					Toast toast=Toast.makeText(getActivity(), "0 is not allowed on the starting",Toast.LENGTH_SHORT);
				//					toast.setGravity(Gravity.CENTER, 0, 0);
				//					toast.show();
				//				}


				if(fromWhere.equalsIgnoreCase("search"))
				{
					//					if(sellerInfo.getMobile_verified().equalsIgnoreCase("Yes"))
					//					{	
					if(!sellerInfo.getMobile_no().equalsIgnoreCase(mobileNumberEdittext.getText().toString()))
					{
						if(s.length()==10)
						{
							verifyButton.setText("Re-verify");
							verifyButton.setBackgroundResource(R.drawable.drawable_btn_verify_clicked);
							verifyButton.setEnabled(true);
							LinearLayoutVerificationKey.setVisibility(View.VISIBLE);
							layoutName.invalidate();
							isReVerifyClicked=true;
							System.out.println("----isReVerifyClicked----"+isReVerifyClicked);
							UniqueString="1";
						}
						else if(s.length()<10)
						{
							verifyButton.setEnabled(false);
							verifyButton.setBackgroundResource(R.drawable.drawable_btn_verify);
						}
					}
					//					}
				}
				else
				{
					if(s.length()==10)
					{
						if(!Utility.mobileNumberFinal.equalsIgnoreCase(s.toString()))
						{
							verifyButton.setText("Verify");
						}
						verifyButton.setEnabled(true);
						verifyButton.setBackgroundResource(R.drawable.drawable_btn_verify_clicked);

					}
					else if(s.length()<10)
					{
						verifyButton.setEnabled(false);
						verifyButton.setBackgroundResource(R.drawable.drawable_btn_verify);
					}

					System.out.println("-----value of isMobileNumberVerified-11111111-"+isMobileNumberVerified);
					if(isMobileNumberVerified)
					{
						UniqueString="";
						if(s.length()==10)
						{
							verifyButton.setEnabled(true);
							verifyButton.setBackgroundResource(R.drawable.drawable_btn_verify_clicked);

						}
						System.out.println("-----value of UniqueString-"+UniqueString);
					}
				}

			}
		});

		/*		shippingPincodeEdittext.addTextChangedListener(new TextWatcher() 
		{
			public void onTextChanged(CharSequence s, int start, int before,int count) 
			{
			}

			public void beforeTextChanged(CharSequence s, int start, int count,int after) 
			{
			}

			public void afterTextChanged(Editable s) 
			{
				//				if(s.length()<6)
				//				{
				//					Toast toast=Toast.makeText(getActivity(), "PIN code must contain 6 digits !!!",Toast.LENGTH_SHORT);
				//					toast.setGravity(Gravity.CENTER, 0, 0);
				//					toast.show();
				//				}
			}
		});*/
		verficationKeyEdittext.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) 
			{
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) 
			{
			}

			@Override
			public void afterTextChanged(Editable s) 
			{
				verficationCode=verficationKeyEdittext.getText().toString();
			}
		});

		/*topUpDropDown.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) 
			{
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) 
			{
			}

			@Override
			public void afterTextChanged(Editable s) 
			{
				int item = 0;
				if(!s.toString().equalsIgnoreCase(""))
				{
					item =Integer.parseInt(topUpDropDown.getText().toString());
					DecimalFormat df = new DecimalFormat("#.##");

					String actAmount=String.format("%.2f",(item*1000/1.1236));
					TxtViewTotalAmount.setText(actAmount);
					TxtViewActualAmount.setText(String.valueOf(item*1000));
					double serTax=(float)item*1000-item*1000/1.1236;
					serviceTax=String.format("%.2f", serTax);
					TxtViewServiceTax.setText(serviceTax);
				}
				else
				{
					topUpDropDown.setTag("0");
				}
			}
		});

*/

		if(fromWhere != null && fromWhere.equalsIgnoreCase("search"))
		{
			allEdittextUneditable();
		}
		else
		{
			LauncherActivity.fromSearch=0;
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		isEditButtonClicked=false;
		mMapView.onResume();

	}
	@Override
	public void onPause() 
	{
		super.onPause();
		mMapView.onPause();
	}
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		mMapView.onDestroy();
	}
	private void allEdittextUneditable()
	{
		bussinessNameEdittext.setClickable(false);
		bussinessNameEdittext.setFocusable(false);
		bussinessNameEdittext.setFocusableInTouchMode(false);
		bussinessNameEdittext.setText(sellerInfo.getBussiness_name());

		mobileNumberEdittext.setClickable(false);
		mobileNumberEdittext.setFocusable(false);
		mobileNumberEdittext.setFocusableInTouchMode(false);
		mobileNumberEdittext.setText(sellerInfo.getMobile_no());

		emailIdEdittext.setClickable(false);
		emailIdEdittext.setFocusable(false);
		emailIdEdittext.setFocusableInTouchMode(false);
		emailIdEdittext.setText(sellerInfo.getEmail_id());




		shippingAddressEdittext.setClickable(false);
		shippingAddressEdittext.setFocusable(false);
		shippingAddressEdittext.setFocusableInTouchMode(false);
		shippingAddressEdittext.setText(sellerInfo.getShipping_address());


		shippingPincodeEdittext.setClickable(false);
		shippingPincodeEdittext.setFocusable(false);
		shippingPincodeEdittext.setFocusableInTouchMode(false);

		shippingPincodeEdittext.setSelection(arrayPinCode.indexOf(sellerInfo.getShipping_pin_code()));

		//		shippingPincodeEdittext.setText(sellerInfo.getShipping_pin_code());

		shippingCityEdittext.setClickable(false);
		shippingCityEdittext.setFocusable(false);
		shippingCityEdittext.setFocusableInTouchMode(false);
		shippingCityEdittext.setText(sellerInfo.getShipping_city());


		shippingStateEdittext.setClickable(false);
		shippingStateEdittext.setFocusable(false);
		shippingStateEdittext.setFocusableInTouchMode(false);
		shippingStateEdittext.setText(sellerInfo.getShipping_state());


		billingAddressEdittext.setClickable(false);
		billingAddressEdittext.setFocusable(false);
		billingAddressEdittext.setFocusableInTouchMode(false);
		billingAddressEdittext.setText(sellerInfo.getBilling_address());

		billingPincodeEdittext.setClickable(false);
		billingPincodeEdittext.setFocusable(false);
		billingPincodeEdittext.setFocusableInTouchMode(false);
		billingPincodeEdittext.setSelection(arrayPinCode.indexOf(sellerInfo.getBilling_pin_code()));
		//		billingPincodeEdittext.setText(sellerInfo.getBilling_pin_code());

		billingCityEdittext.setClickable(false);
		billingCityEdittext.setFocusable(false);
		billingCityEdittext.setFocusableInTouchMode(false);
		billingCityEdittext.setText(sellerInfo.getBilling_city());


		billingStateEdittext.setClickable(false);
		billingStateEdittext.setFocusable(false);
		billingStateEdittext.setFocusableInTouchMode(false);
		billingStateEdittext.setText(sellerInfo.getBilling_state());


		EdittextbankName.setClickable(false);
		EdittextbankName.setFocusable(false);
		EdittextbankName.setFocusableInTouchMode(false);
		EdittextbankName.setText(sellerInfo.getBank_name());


		EdittextifscCode.setClickable(false);
		EdittextifscCode.setFocusable(false);
		EdittextifscCode.setFocusableInTouchMode(false);
		EdittextifscCode.setText(sellerInfo.getIfsc_code());

		EdittextaccountNumber.setClickable(false);
		EdittextaccountNumber.setFocusable(false);
		EdittextaccountNumber.setFocusableInTouchMode(false);
		EdittextaccountNumber.setText(sellerInfo.getAccount_no());

		Edittextbranch.setClickable(false);
		Edittextbranch.setFocusable(false);
		Edittextbranch.setFocusableInTouchMode(false);
		Edittextbranch.setText(sellerInfo.getBranch());

		EdittextBeneficaryName.setClickable(false);
		EdittextBeneficaryName.setFocusable(false);
		EdittextBeneficaryName.setFocusableInTouchMode(false);
		EdittextBeneficaryName.setText(sellerInfo.getBeneficaryName());

		edittextLocation.setText(sellerInfo.getLatitude()+","+sellerInfo.getLongitude());
		LinearLayoutVerificationKey.setVisibility(View.GONE);
		//spinnerLayout.setVisibility(View.GONE);

		editTextCSTTINNumber.setClickable(false);
		editTextCSTTINNumber.setFocusable(false);
		editTextCSTTINNumber.setFocusableInTouchMode(false);
		editTextCSTTINNumber.setText(sellerInfo.getCst_tin_number());

		if(sellerInfo.getMobile_verified().equalsIgnoreCase("Yes"))
		{
			verifyButton.setText("Verified");
			verifyButton.setEnabled(false);
		}
		else
			verifyButton.setText("Verify");
		verifyButton.setTextColor(Color.parseColor("#ffffff"));
		//		TxtViewVerificationKey.setVisibility(View.GONE);

		btnClearAll.setText("Edit");
		BtnMarkLocation.setEnabled(false);
		//		verifyButton.setBackgroundColor(Color.parseColor("#2a9b2a"));

		LinearLayoutCurentBalance.setVisibility(View.VISIBLE);


	}

	/**
	 * Method for making all fields editable except PINcode
	 */
	private void allEdittextEditable()
	{
		bussinessNameEdittext.setClickable(true);
		bussinessNameEdittext.setFocusable(true);
		bussinessNameEdittext.setFocusableInTouchMode(true);
		bussinessNameEdittext.setText(sellerInfo.getBussiness_name());

		mobileNumberEdittext.setClickable(true);
		mobileNumberEdittext.setFocusable(true);
		mobileNumberEdittext.setFocusableInTouchMode(true);
		mobileNumberEdittext.setText(sellerInfo.getMobile_no());

		emailIdEdittext.setClickable(true);
		emailIdEdittext.setFocusable(true);
		emailIdEdittext.setFocusableInTouchMode(true);
		emailIdEdittext.setText(sellerInfo.getEmail_id());

		//		shippingPincodeEdittext.setClickable(true);
		//		shippingPincodeEdittext.setFocusable(true);
		//		shippingPincodeEdittext.setFocusableInTouchMode(true);

		shippingAddressEdittext.setClickable(true);
		shippingAddressEdittext.setFocusable(true);
		shippingAddressEdittext.setFocusableInTouchMode(true);
		shippingAddressEdittext.setText(sellerInfo.getShipping_address());


		shippingCityEdittext.setClickable(true);
		shippingCityEdittext.setFocusable(true);
		shippingCityEdittext.setFocusableInTouchMode(true);
		shippingCityEdittext.setText(sellerInfo.getShipping_city());


		shippingStateEdittext.setClickable(true);
		shippingStateEdittext.setFocusable(true);
		shippingStateEdittext.setFocusableInTouchMode(true);
		shippingStateEdittext.setText(sellerInfo.getShipping_state());


		//		billingPincodeEdittext.setClickable(true);
		//		billingPincodeEdittext.setFocusable(true);
		//		billingPincodeEdittext.setFocusableInTouchMode(true);

		billingAddressEdittext.setClickable(true);
		billingAddressEdittext.setFocusable(true);
		billingAddressEdittext.setFocusableInTouchMode(true);
		billingAddressEdittext.setText(sellerInfo.getBilling_address());


		billingCityEdittext.setClickable(true);
		billingCityEdittext.setFocusable(true);
		billingCityEdittext.setFocusableInTouchMode(true);
		billingCityEdittext.setText(sellerInfo.getBilling_city());


		billingStateEdittext.setClickable(true);
		billingStateEdittext.setFocusable(true);
		billingStateEdittext.setFocusableInTouchMode(true);
		billingStateEdittext.setText(sellerInfo.getBilling_state());


		EdittextbankName.setClickable(true);
		EdittextbankName.setFocusable(true);
		EdittextbankName.setFocusableInTouchMode(true);
		EdittextbankName.setText(sellerInfo.getBank_name());


		EdittextifscCode.setClickable(true);
		EdittextifscCode.setFocusable(true);
		EdittextifscCode.setFocusableInTouchMode(true);
		EdittextifscCode.setText(sellerInfo.getIfsc_code());

		EdittextaccountNumber.setClickable(true);
		EdittextaccountNumber.setFocusable(true);
		EdittextaccountNumber.setFocusableInTouchMode(true);
		EdittextaccountNumber.setText(sellerInfo.getAccount_no());

		Edittextbranch.setClickable(true);
		Edittextbranch.setFocusable(true);
		Edittextbranch.setFocusableInTouchMode(true);
		Edittextbranch.setText(sellerInfo.getBranch());

		EdittextBeneficaryName.setClickable(true);
		EdittextBeneficaryName.setFocusable(true);
		EdittextBeneficaryName.setFocusableInTouchMode(true);
		EdittextBeneficaryName.setText(sellerInfo.getBeneficaryName());

		edittextLocation.setText(sellerInfo.getLocation());
		LinearLayoutVerificationKey.setVisibility(View.GONE);
		//spinnerLayout.setVisibility(View.GONE);

		editTextCSTTINNumber.setClickable(true);
		editTextCSTTINNumber.setFocusable(true);
		editTextCSTTINNumber.setFocusableInTouchMode(true);
		editTextCSTTINNumber.setText(sellerInfo.getCst_tin_number());
		BtnMarkLocation.setEnabled(true);


		//		verifyButton.setBackgroundColor(Color.parseColor("#2a9b2a"));

		//		LinearLayoutCurentBalance.setVisibility(View.GONE);


	}
	/** Method for checking the pin code and phone empty Parameter
	 * @return true if they are not empty
	 */
	private boolean checkEnteredDetails()
	{
		if (bussinessNameEdittext.getText().toString().trim().equals(""))
			return false;
		if (mobileNumberEdittext.getText().toString().trim().equals(""))
			return false;
		if (emailIdEdittext.getText().toString().trim().equals(""))
			return false;
		if (shippingAddressEdittext.getText().toString().trim().equals(""))
			return false;
		if (billingAddressEdittext.getText().toString().trim().equals(""))
			return false;
		if(fromWhere.equalsIgnoreCase("abc"))
		{	
			if (shippingPincodeEdittext.getSelectedItemPosition()==0)
				return false;
			if (billingPincodeEdittext.getSelectedItemPosition()==0)
				return false;
		}
		if (shippingCityEdittext.getText().toString().trim().equals(""))
			return false;
		if (billingCityEdittext.getText().toString().trim().equals(""))
			return false;
		if (shippingStateEdittext.getText().toString().trim().equals(""))
			return false;
		if (billingStateEdittext.getText().toString().trim().equals(""))
			return false;
		return true;
	}

	/** Method for checking the pin code and phone empty Parameter
	 * @return true if they are not empty
	 */
	private boolean checkBankDetailsFields()
	{
		if (EdittextbankName.getText().toString().equals(""))
			return false;
		if (EdittextaccountNumber.getText().toString().equals(""))
			return false;
		if (EdittextifscCode.getText().toString().equals(""))
			return false;
		if (EdittextBeneficaryName.getText().toString().equals(""))
			return false;
		if (Edittextbranch.getText().toString().equals(""))
			return false;
		if (editTextCSTTINNumber.getText().toString().equals(""))
			return false;
		return true;
	}
	/** Method for checking the pin code and phone required Parameter lenght is inputed or not
	 * @return true 
	 */
	private boolean inputValidation() 
	{
		//		if (pincodeEdittext.getText().toString().length() < 6) 
		//		{
		//			Toast.makeText(getActivity(), "Pin Code should be of 6 digits.",Toast.LENGTH_SHORT).show();
		//			return false;
		//		}
		//		if (phoneEdittext.getText().toString().length() < 10) 
		//		{
		//			Toast.makeText(getActivity(),"Mobile number should be of 10 digits.", Toast.LENGTH_SHORT).show();
		//			return false;
		//		}
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

						editor.commit();
						Collections.sort(arrayPinCode);

						if(arrayPinCode.isEmpty())
						{
							arrayPinCode.add(0,"No PIN Code found");
						}
						ArrayAdapter<String> adapter_amount = new ArrayAdapter<String>(getActivity(),
								android.R.layout.simple_spinner_item, arrayPinCode);
						adapter_amount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						shippingPincodeEdittext.setAdapter( new NothingSelectedSpinnerAdapter
								( 
										adapter_amount,
										R.layout.contact_spinner_row_nothing_selected,getActivity()
										));
						billingPincodeEdittext.setAdapter( new NothingSelectedSpinnerAdapter
								( 
										adapter_amount,
										R.layout.contact_spinner_row_nothing_selected,getActivity()
										));
						if(fromWhere.equalsIgnoreCase("search"))
						{
							System.out.println("-----Value of arrayPinCode.indexOf(sellerInfo.getShipping_pin_code().toString()--"+arrayPinCode.indexOf(sellerInfo.getShipping_pin_code().toString()));
							shippingPincodeEdittext.setSelection(arrayPinCode.indexOf(sellerInfo.getShipping_pin_code().toString()));
							billingPincodeEdittext.setSelection(arrayPinCode.indexOf(sellerInfo.getBilling_pin_code().toString()));
						}

					}
					else
					{///In case of no pin code found

						arrayPinCode.add("PIN Code not found");
						ArrayAdapter<String> adapter_amount = new ArrayAdapter<String>(getActivity(),
								android.R.layout.simple_spinner_item, arrayPinCode);
						adapter_amount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						shippingPincodeEdittext.setAdapter( new NothingSelectedSpinnerAdapter
								( 
										adapter_amount,
										R.layout.contact_spinner_row_nothing_selected,getActivity()
										));
						billingPincodeEdittext.setAdapter( new NothingSelectedSpinnerAdapter
								( 
										adapter_amount,
										R.layout.contact_spinner_row_nothing_selected,getActivity()
										));
						shippingPincodeEdittext.setSelection(arrayPinCode.indexOf(arrayPinCode.toString()));
						billingPincodeEdittext.setSelection(arrayPinCode.indexOf(arrayPinCode.toString()));
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
	public CRMListener getChallenge()
	{
		CRMListener crmListener=new CRMListener() 
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
					else
					{
						progressBar.setVisibility(View.GONE);
						Toast toast=Toast.makeText(getActivity(), "Unable to find User !!!",Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
					if(checkInternet)
					{	
						if(isAdminSessionFound)
							new AsyncUtilies.SecondAsyn(token,sharedpreferences.getString("username", ""),getLoginCompleteListener(),"forUserSession").execute();
						else
							new AsyncUtilies.SecondAsyn(token,sharedpreferences.getString("username", ""),getLoginCompleteListener()).execute();
					}
					else
					{
						progressBar.setVisibility(View.GONE);
						Toast toast=Toast.makeText(getActivity(), "No Internet Connectivity !!!",Toast.LENGTH_SHORT);
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

	private CRMListener getLoginCompleteListener()
	{
		CRMListener crmListener=new CRMListener()
		{
			@Override
			public void getCAllback(String result) 
			{
				System.out.println("----value after getting the adminSession is --- "+result);

				try 
				{
					JSONObject jObj=new JSONObject(result);
					String successStatus=jObj.get("success").toString();
					if(successStatus.equalsIgnoreCase("true"))
					{
						if(!isAdminSessionFound)
						{
							JSONObject jsonTokenObj=new JSONObject(jObj.get("result").toString());
							adminSession=(String)jsonTokenObj.get("sessionName");

							//						id=(String)jsonTokenObj.get("id");


							if(checkInternet)
							{
								if(mobileExistanceCheck.equalsIgnoreCase("abc"))
								{
									new AsyncUtilies.ThirdAsyn(sharedpreferences.getString("username", ""),
											sharedpreferences.getString("password", ""),adminSession,getPassWordCheckListener()).execute();
								}
								//							TODO
								//							else if(isEditButtonClicked)
								//							{
								//								new AsyncUtilies.ThirdAsyn(sharedpreferences.getString("username", ""),mobileExistanceCheck,
								//										session,getPassWordCheckListener(),"updateSeller",newSeller).execute();
								//							}
								else
								{
									new AsyncUtilies.ThirdAsyn(sharedpreferences.getString("username", ""),mobileExistanceCheck,
											adminSession,getPassWordCheckListener(),"mobileCheck").execute();
								}
							}
							else
							{
								progressBar.setVisibility(View.GONE);
								Toast toast=Toast.makeText(getActivity(), "No Internet Connectivity !!!",Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();

							}
						}
						else
						{
							JSONObject jsonTokenObj=new JSONObject(jObj.get("result").toString());
							userSession=(String)jsonTokenObj.get("sessionName");
							String id=sharedpreferences.getString("id", "");
							if(checkInternet)
							{
								if(isEditButtonClicked)
								{
									new AsyncUtilies.postingSellerDataAsyn(sharedpreferences.getString("username", ""),
											adminSession,newSeller,getFinalResultListener(),id,"update").execute();
								}
								else
								{	
									new AsyncUtilies.postingSellerDataAsyn(sharedpreferences.getString("username", ""),
											userSession,newSeller,getFinalResultListener(),id).execute();
								}
							}
						}
					}
					else
					{
						progressBarVerifiedButton.setVisibility(View.GONE);
						progressBar.setVisibility(View.GONE);
						Toast toast=Toast.makeText(getActivity(), "Internal Server issue please try again later !!!",Toast.LENGTH_SHORT);
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

	private CRMListener getDuplicateNumberCheckListener() 
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
						if(resultArray.length()==0)
						{
							if(checkInternet)
							{
								//								new AsyncUtilies.postingSellerDataAsyn(sharedpreferences.getString("username", ""),
								//										adminSession,newSeller,getFinalResultListener(),id).execute();
								isAdminSessionFound=true;
								new AsyncUtilies.FirstAsyn(getChallenge(),sharedpreferences.getString("username", ""),"forUserToken").execute();
							}
						}
						else
						{///If found the number duplicate than setting the status in the local database
							DatabaseHandler db=new DatabaseHandler(getActivity());
							db.updateSellerIsNumberDuplicateStatus(newSeller.getId());//TODO midnight 1:42 PM 29th Dec
							//							listenerForSettion.getCAllback(null);
							progressBar.setVisibility(View.GONE);
							Toast toast=Toast.makeText(getActivity(), "Sorry this number is already registered with us.Try with some other number",Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
					}
					else
					{
						//						listener.getCAllback(null);
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
		final CRMListener crmListener = new CRMListener() 
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
					if(mobileExistanceCheck.equalsIgnoreCase("abc"))
					{
						if(successStatus.equalsIgnoreCase("true"))
						{	
							JSONArray resultArray=jObj.getJSONArray("result");
							JSONObject resultObj=resultArray.getJSONObject(0);
							userPass=resultObj.getString("user_password");
							Utility.UserAPIAccessKey=resultObj.getString("accesskey");
							userid=resultObj.getString("id");
							inputtedUserPassword=sharedpreferences.getString("password", "");
							temp="$1$"+sharedpreferences.getString("username", "").substring(0, 2)+"0000000";


							System.out.println("-----ABHI value of temp is==="+temp);
							temp=MD5Crypt.crypt(inputtedUserPassword, temp);
							System.out.println("----Converted value of password is ---"+temp);
							System.out.println("----Compare value of password is ---"+userPass.compareTo(temp));
							if(userPass.compareTo(temp)==0)
							{
								if(checkInternet)
								{
									if(isEditButtonClicked)
									{
										//										new AsyncUtilies.postingSellerDataAsyn(sharedpreferences.getString("username", ""),
										//												adminSession,newSeller,getFinalResultListener(),id,"update").execute();
										isAdminSessionFound=true;
										new AsyncUtilies.FirstAsyn(getChallenge(),sharedpreferences.getString("username", ""),"forUserToken").execute();
									}
									//		ABHI on 6th Feb 5:5 PM TODO							else if(fromWhere.equalsIgnoreCase("abc"))
									//									{
									//										new AsyncUtilies.postingSellerDataAsyn(sharedpreferences.getString("username", ""),
									//												session,newSeller,getFinalResultListener(),id).execute();
									//									}
									else
									{
										new AsyncUtilies.ThirdAsyn(newSeller.getShipping_pin_code(),newSeller.getBussiness_name(),adminSession,getDuplicateNumberCheckListener(),"mobileCheck",newSeller.getMobile_no()).execute();
									}
								}
								else
								{
									progressBar.setVisibility(View.GONE);
									Toast toast=Toast.makeText(getActivity(), "No Internet Connectivity !!!",Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								}
							}
							else
							{
								Editor editor=sharedpreferences.edit();
								editor.clear();
								editor.commit();

								SplashFragment newFragment = new SplashFragment();
								Bundle args = new Bundle();
								newFragment.setArguments(args);
								getActivity().getActionBar().hide();

								FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
								transaction.replace(R.id.container, newFragment);
								transaction.commit();
							}
						}
					}
					else
					{
						if(successStatus.equalsIgnoreCase("true"))
						{	
							JSONArray resultArray=jObj.getJSONArray("result");
							progressBarVerifiedButton.setVisibility(View.GONE);

							if(resultArray.length()==0)
							{
								isNumberNotExists=true;
								String key = UniqueString.substring(2);
								key = "XX"+key;
								Toast toast=Toast.makeText(getActivity(), "Verification Key "+key+" sent to seller !!!",5000);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
								verifyButton.setTextColor(Color.parseColor("#ffffff"));
								verifyButton.setText("Re-Verify");
								Log.d("MSG", UniqueString);
								Utility.sendSMS(UniqueString,mobileNumberEdittext.getText().toString());
								mobileExistanceCheck="abc";

							}
							else
							{
								Toast toast=Toast.makeText(getActivity(), "Sorry this number is already registered with us.Try with some other number",Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
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
	private CRMListener getFinalResultListener()
	{
		CRMListener crmListener=new CRMListener()
		{
			@Override
			public void getCAllback(String result) 
			{
				try
				{
					if(!result.isEmpty())
					{
						JSONObject jObj=new JSONObject(result);
						String successStatus=jObj.getString("success");
						if(successStatus.equalsIgnoreCase("true"))
						{

							JSONObject resultObj=jObj.getJSONObject("result");
							rDId=resultObj.getString("assigned_user_id");
							sellerFinalId=resultObj.getString("id");
							//						db.updateSellerSyncStatus((int) id);//done 8th jan 2014
							db.updateSellerStatus((int)id,rDId,sellerFinalId);
							if(checkInternet)////Commented on to the date 23 due to Top up not working at the CRM end
							{
								if(sellerInfo != null && sellerInfo.isLead()){
							         //new AsyncUtilies.ThirdAsyn(sellerInfo.getLeadId(),
							           //"converted",userSession,getDataCompleteListener(),"updateLeads","").execute();
							         new LeadUtility.SendLeadDatatoCRMStep4(userSession,
							           sellerInfo.getLeadId(), getDataCompleteListener(),"update","conversion",myLead,sharedpreferences.getString("username", " "))
							           .execute();
							        }
								else{
									if(successStatus.equalsIgnoreCase("true"))
									{
										db.updateSellerPaymentStatus((int)id);
										progressBar.setVisibility(View.GONE);
										FragmentManager manager = getFragmentManager();
										FragmentDialog dialog=new FragmentDialog(); 
										if(isEditButtonClicked)
											dialog.show(manager, "update");
										else
											dialog.show(manager, successStatus);
										//						new AsyncUtilies.postingTopDataOfSeller(rDTopUpAmount,rDId,getPassWordCheckListener()).execute();
									}
									else
									{
										progressBar.setVisibility(View.GONE);
										Toast toast=Toast.makeText(getActivity(), "Due to internal error unable to Top Up !!!",Toast.LENGTH_SHORT);
										toast.setGravity(Gravity.CENTER, 0, 0);
										toast.show();
									}
								}
								
								

								
								/*
								if(isEditButtonClicked)
								{
									if(topUpDropDown.getText().toString().equalsIgnoreCase(" "))
									{
										progressBar.setVisibility(View.GONE);
										Toast toast=Toast.makeText(getActivity(), "Seller Updated Successfully !!!",Toast.LENGTH_SHORT);
										toast.setGravity(Gravity.CENTER, 0, 0);
										toast.show();

										NameRegistrationFragment newFragment = new NameRegistrationFragment();

										FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
										transaction.replace(R.id.container, newFragment);
										transaction.commit();
									}
									else
									{
										new AsyncUtilies.RDTopUpChecking(rDId,getRDCheckingListener(),adminSession).execute();
									}
								}
								else
								{
									Toast t=Toast.makeText(getActivity(), "Seller Created Successfully !!!",Toast.LENGTH_SHORT);
									t.setGravity(Gravity.CENTER, 0, 0);
									t.show();
									new AsyncUtilies.RDTopUpChecking(rDId,getRDCheckingListener(),adminSession).execute();
								}

							*/}
							else
							{
								progressBar.setVisibility(View.GONE);
								Toast toast=Toast.makeText(getActivity(), "No Internet Connectivity !!!",Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}
						}
						else
						{
						/*	if(sellerInfo != null && sellerInfo.isLead()){
								//new AsyncUtilies.ThirdAsyn(sellerInfo.getLeadId(),
										//"converted",userSession,getDataCompleteListener(),"updateLeads","").execute();
								new LeadUtility.SendLeadDatatoCRMStep4(userSession,
										sellerInfo.getLeadId(), getDataCompleteListener(),"update","conversion",myLead,sharedpreferences.getString("username", " "))
										.execute();
							}*/
							
							progressBarVerifiedButton.setVisibility(View.GONE);
							progressBar.setVisibility(View.GONE);
							Toast toast=Toast.makeText(getActivity(), "Internal Server issue please try again later !!!",Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
					}
					else
					{
						/*if(sellerInfo != null && sellerInfo.isLead()){
				              //new AsyncUtilies.ThirdAsyn(sellerInfo.getLeadId(),
				                //"converted",userSession,getDataCompleteListener(),"updateLeads","").execute();
				              new LeadUtility.SendLeadDatatoCRMStep4(userSession,
				                sellerInfo.getLeadId(), getDataCompleteListener(),"update","conversion",myLead,sharedpreferences.getString("username", " "))
				                .execute();
				             }
				      else{
				       if(successStatus.equalsIgnoreCase("true"))
				       {
				        db.updateSellerPaymentStatus((int)id);
				        progressBar.setVisibility(View.GONE);
				        FragmentManager manager = getFragmentManager();
				        FragmentDialog dialog=new FragmentDialog(); 
				        if(isEditButtonClicked)
				         dialog.show(manager, "update");
				        else
				         dialog.show(manager, successStatus);
				        //      new AsyncUtilies.postingTopDataOfSeller(rDTopUpAmount,rDId,getPassWordCheckListener()).execute();
				       }
				       else
				       {
				        progressBar.setVisibility(View.GONE);
				        Toast toast=Toast.makeText(getActivity(), "Due to internal error unable to Top Up !!!",Toast.LENGTH_SHORT);
				        toast.setGravity(Gravity.CENTER, 0, 0);
				        toast.show();
				       }
				      }*/
						progressBar.setVisibility(View.GONE);
						Toast toast=Toast.makeText(getActivity(), "Internal Server issue please try again later !!!",Toast.LENGTH_SHORT);
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
						if(successStatus.equalsIgnoreCase("true"))
						{
							db.updateSellerPaymentStatus((int)id);
							progressBar.setVisibility(View.GONE);
							FragmentManager manager = getFragmentManager();
							FragmentDialog dialog=new FragmentDialog(); 
							if(isEditButtonClicked)
								dialog.show(manager, "update");
							else
								dialog.show(manager, successStatus);
							//						new AsyncUtilies.postingTopDataOfSeller(rDTopUpAmount,rDId,getPassWordCheckListener()).execute();
						}
						else
						{
							progressBar.setVisibility(View.GONE);
							Toast toast=Toast.makeText(getActivity(), "Due to internal error unable to Top Up !!!",Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
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
						if(resultArray.length()!=0)
						{
							JSONObject resultObj=resultArray.getJSONObject(0);
							String rDTopUpAmount=resultObj.getString("rd_security_amt");
							if(rDTopUpAmount.isEmpty())
							{
								rDTopUpAmount="0";
							}
							float rdAmount=Float.valueOf(rDTopUpAmount);
							String rdAssingId=resultObj.getString("id");
							if(rdAmount>=Float.valueOf(newSeller.getTopup_amount())*1000)
							{
								if(checkInternet)////Checking the internet connectivity
								{
									System.out.println("---Value of UserSession is ---"+userSession);
									if(isEditButtonClicked)
										new AsyncUtilies.postingTopDataOfSeller(rdAssingId,sellerFinalId,newSeller.getTopup_amount(),adminSession,rDId,serviceTax,getTopUpSellerPostingResponse(),"create",sellerInfo).execute();
									else
										new AsyncUtilies.postingTopDataOfSeller(rdAssingId,sellerFinalId,newSeller.getTopup_amount(),adminSession,rDId,serviceTax,getTopUpSellerPostingResponse(),"create").execute();
								}
								else
								{
									progressBar.setVisibility(View.GONE);
									Toast toast=Toast.makeText(getActivity(), "No Internet Connectivity !!!",Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								}
							}
							else
							{
								progressBar.setVisibility(View.GONE);
								Toast toast=Toast.makeText(getActivity(), "RD does not had sufficient Balance !!!",Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}
						}
						else
						{
							progressBar.setVisibility(View.GONE);
							Toast toast=Toast.makeText(getActivity(), "RD does not had sufficient Balance !!!",Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}

					}
				}
				catch (JSONException e) 
				{
					progressBar.setVisibility(View.GONE);
					Toast toast=Toast.makeText(getActivity(), "PIN code doesn't Exist !!!",Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					//					toast.show();
					e.printStackTrace();
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
						db.updateSellerPaymentStatus((int)id);
						progressBar.setVisibility(View.GONE);
						FragmentManager manager = getFragmentManager();
						FragmentDialog dialog=new FragmentDialog(); 
						if(isEditButtonClicked)
							dialog.show(manager, "update");
						else
							dialog.show(manager, successStatus);
						//						new AsyncUtilies.postingTopDataOfSeller(rDTopUpAmount,rDId,getPassWordCheckListener()).execute();
					}
					else
					{
						progressBar.setVisibility(View.GONE);
						Toast toast=Toast.makeText(getActivity(), "Due to internal error unable to Top Up !!!",Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}

					//					dialog.show(manager, successStatus);
				}
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
		};
		return crmListener;
	}
	@Override
	public void onClick(View v) 
	{
		switch (v.getId())
		{
		case R.id.btnNext:
			System.out.println("---Inside Next button Click ABHI and Counter =="+counter);
			//			btnClearAll.setBackgroundResource(R.drawable.clear_all_button);
			if(counter==0)
			{
				if(fromWhere.equalsIgnoreCase("abc") || fromWhere.equalsIgnoreCase("leads"))
				{	

					if(checkEnteredDetails())/////Checking the user inputed all the fields
					{////Starting of checkEnteredDetails
						if (!verficationKeyEdittext.getText().toString().trim().equals(""))
						{
							if(UniqueString.equalsIgnoreCase(verficationCode))/////Checking the Unique Key with the user inputed Verification key
							{///Starting of UniqueString.equalsIgnorecase(verificationCode)
								if(!UniqueString.equals(""))////Checking the unique is empty or not 
								{///Starting of UniqueString.equals("")
									//								if(isNumberNotExists)///Checking weather the number exists or not in the CRM
									//								{///Starting of isNumberNotExists	
									if(android.util.Patterns.EMAIL_ADDRESS.matcher(emailIdEdittext.getText().toString()).matches())///Checking the email address either valid or not
									{///Starting of email address valid or not		
										if(mobileNumberEdittext.getText().length()==10)//Checking weather mobile number is valid or not
										{///Starting of mobile number length TODO
											Utility.mobileNumberFinal=mobileNumberEdittext.getText().toString();

											if(isMobileNumberVerified)
											{
												if(UniqueString.equalsIgnoreCase(verficationCode))
												{
													if(!shippingPincodeEdittext.getSelectedItem().toString().equalsIgnoreCase("PIN Code not found"))
													{	
														if(!billingPincodeEdittext.getSelectedItem().toString().equalsIgnoreCase("PIN Code not found"))
														{	
															btnPrevious.setClickable(true);
															btnPrevious.setBackgroundResource(R.drawable.prev_button2);
															layoutName.setVisibility(View.GONE);
															layoutBank.setVisibility(View.VISIBLE);
															layoutLocation.setVisibility(View.GONE);

															getActivity().getActionBar().setTitle(R.string.resgistration_2);	
															verifyButton.setText("Verified");
															verifyButton.setTextColor(Color.parseColor("#ffffff"));
															counter++;
														}
														else
														{
															Toast toast=Toast.makeText(getActivity(), "PIN Code Error !!!",Toast.LENGTH_SHORT);
															toast.setGravity(Gravity.CENTER, 0, 0);
															toast.show();
														}
													}
													else
													{
														Toast toast=Toast.makeText(getActivity(), "PIN Code Error !!!",Toast.LENGTH_SHORT);
														toast.setGravity(Gravity.CENTER, 0, 0);
														toast.show();
													}
												}
												else
												{
													Toast toast=Toast.makeText(getActivity(), "Verfication Code is invalid !!!",Toast.LENGTH_SHORT);
													toast.setGravity(Gravity.CENTER, 0, 0);
													toast.show();
												}
											}
											else
											{

												if(!shippingPincodeEdittext.getSelectedItem().toString().equalsIgnoreCase("PIN Code not found"))
												{	
													if(!billingPincodeEdittext.getSelectedItem().toString().equalsIgnoreCase("PIN Code not found"))
													{
														btnPrevious.setClickable(true);
														btnPrevious.setBackgroundResource(R.drawable.prev_button2);
														layoutName.setVisibility(View.GONE);
														layoutBank.setVisibility(View.VISIBLE);
														layoutLocation.setVisibility(View.GONE);

														getActivity().getActionBar().setTitle(R.string.resgistration_2);	
														verifyButton.setText("Verified");
														verifyButton.setTextColor(Color.parseColor("#ffffff"));
														counter++;
													}
													else
													{
														Toast toast=Toast.makeText(getActivity(), "PIN Code Error !!!",Toast.LENGTH_SHORT);
														toast.setGravity(Gravity.CENTER, 0, 0);
														toast.show();
													}
												}
												else
												{
													Toast toast=Toast.makeText(getActivity(), "PIN Code Error !!!",Toast.LENGTH_SHORT);
													toast.setGravity(Gravity.CENTER, 0, 0);
													toast.show();
												}

											}
										}///Ending of mobile number length 
										else
										{
											Toast toast=Toast.makeText(getActivity(), "Mobile number is invalid!!!",Toast.LENGTH_SHORT);
											toast.setGravity(Gravity.CENTER, 0, 0);
											toast.show();
										}
									}///Ending of email address valid or not
									else
									{
										Toast toast=Toast.makeText(getActivity(), "Email Addresss is invalid !!!",Toast.LENGTH_SHORT);
										toast.setGravity(Gravity.CENTER, 0, 0);
										toast.show();
									}
									//								}///Ending of isNumberNotExists	
									//								else
									//								{
									//									Toast toast=Toast.makeText(getActivity(), "Mobile Number Already Exists,try with different number !!!",Toast.LENGTH_SHORT);
									//									toast.setGravity(Gravity.CENTER, 0, 0);
									//									toast.show();
									//								}
								}///Ending of UniqueString.equals("")
								else
								{
									Toast toast=Toast.makeText(getActivity(), "Please Verify your number !!!",Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								}
							}///Ending of UniqueString.equalsIgnorecase(verificationCode)
							else
							{
								Toast toast=Toast.makeText(getActivity(), "Verfication Code is invalid !!!",Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}
						}
						else
						{
							Toast toast=Toast.makeText(getActivity(), "Please fill all the fields !!!",Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
					}////Ending of checkEnteredDetails
					else
					{
						Toast toast=Toast.makeText(getActivity(), "Please fill all the fields !!!",Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
				}
				else if(fromWhere.equalsIgnoreCase("search"))
				{
					if(!isEditButtonClicked)
					{	
						getActivity().getActionBar().setTitle(R.string.seller_detail_2);
						btnPrevious.setClickable(true);
						btnPrevious.setBackgroundResource(R.drawable.prev_button2);
						layoutName.setVisibility(View.GONE);
						layoutBank.setVisibility(View.VISIBLE);
						layoutLocation.setVisibility(View.GONE);
						//						LinearLayoutServiceTax.setVisibility(View.GONE);
						txtViewSellerBalance.setText(String.valueOf(sellerInfo.getTopup_amount()));
						//spinnerLayout.setVisibility(View.GONE);
						counter++;
					}
					else
					{
						if(checkEnteredDetails())
						{

							getActivity().getActionBar().setTitle(R.string.seller_detail_2);
							btnPrevious.setClickable(true);
							btnPrevious.setBackgroundResource(R.drawable.prev_button2);


							if(isReVerifyClicked)
							{
								if(UniqueString.equalsIgnoreCase(verficationCode))
								{	
									verifyButton.setText("Verified");
									layoutName.setVisibility(View.GONE);
									layoutBank.setVisibility(View.VISIBLE);
									layoutLocation.setVisibility(View.GONE);
									//spinnerLayout.setVisibility(View.GONE);
									txtViewSellerBalance.setText(String.valueOf(sellerInfo.getTopup_amount()));
									counter++;
								}
								else
								{
									Toast toast=Toast.makeText(getActivity(), "Verification Key is invalid !!!",Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								}
							}
							else
							{
								layoutName.setVisibility(View.GONE);
								layoutBank.setVisibility(View.VISIBLE);
								layoutLocation.setVisibility(View.GONE);
								//spinnerLayout.setVisibility(View.GONE);
								txtViewSellerBalance.setText(String.valueOf(sellerInfo.getTopup_amount()));
								counter++;
							}

						}
						else
						{
							Toast toast=Toast.makeText(getActivity(), "Please fill all the fields !!!",Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
					}
				}

			}
			else if(counter==1)
			{
				if(fromWhere.equalsIgnoreCase("abc") || fromWhere.equalsIgnoreCase("leads"))
				{
					if(checkBankDetailsFields())
					{	
						/*if(!topUpDropDown.getText().toString().equalsIgnoreCase(""))
						{	
							if(topUpDropDown.getText().toString().equalsIgnoreCase("0"))
							{	*/
								getActivity().getActionBar().setTitle(R.string.resgistration_3);
								layoutName.setVisibility(View.GONE);
								layoutBank.setVisibility(View.GONE);
								layoutLocation.setVisibility(View.VISIBLE);
								nextButton.setBackgroundResource(R.drawable.green_check);
								counter++;
							/*}
							else
							{
								Toast toast=Toast.makeText(getActivity(), "Top Up amount could not be 0.",Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}*/
					/*	}
						else
						{
							Toast toast=Toast.makeText(getActivity(), "Top Up amount could not be Empty.",Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}*/
					}
					else
					{
						Toast toast=Toast.makeText(getActivity(), "Please fill all the field's.",Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
				}
				else
				{
					getActivity().getActionBar().setTitle(R.string.seller_detail_3);

					if(isEditButtonClicked)
					{
						if(checkBankDetailsFields())
						{
							nextButton.setBackgroundResource(R.drawable.green_check);
							layoutName.setVisibility(View.GONE);
							layoutBank.setVisibility(View.GONE);
							layoutLocation.setVisibility(View.VISIBLE);
							counter++;
						}
						else
						{
							Toast toast=Toast.makeText(getActivity(), "Please fill all the fields !!!",Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
					}
					else
					{
						layoutName.setVisibility(View.GONE);
						layoutBank.setVisibility(View.GONE);
						layoutLocation.setVisibility(View.VISIBLE);
						//						edittextLocation.setText(newSeller.getLocation());
						nextButton.setBackgroundResource(R.drawable.next_button);
						counter++;
					}

				}

			}
			else if(counter==2)
			{
				layoutName.setVisibility(View.GONE);
				layoutBank.setVisibility(View.GONE);
				layoutLocation.setVisibility(View.VISIBLE);
				nextButton.setEnabled(false);
				if(fromWhere.equalsIgnoreCase("abc") || fromWhere.equalsIgnoreCase("leads"))
				{

					//save logic

					//save logic
					getActivity().getActionBar().setTitle(R.string.resgistration_3);
					progressBar.setVisibility(View.VISIBLE);
					db=new DatabaseHandler(getActivity());

					newSeller=new NewSeller();
					if(fromWhere.equalsIgnoreCase("leads")){
						newSeller.setLead(true);
						newSeller.setLeadId(sellerInfo.getLeadId());
					}
					newSeller.setBussiness_name(bussinessNameEdittext.getText().toString());
					newSeller.setMobile_no(mobileNumberEdittext.getText().toString());

					newSeller.setEmail_id(emailIdEdittext.getText().toString());
					newSeller.setShipping_address(shippingAddressEdittext.getText().toString());
					newSeller.setBilling_address(billingAddressEdittext.getText().toString());

					newSeller.setShipping_pin_code(shippingPincodeEdittext.getSelectedItem().toString());
					newSeller.setBilling_pin_code(billingPincodeEdittext.getSelectedItem().toString());
					newSeller.setShipping_city(shippingCityEdittext.getText().toString());
					newSeller.setBilling_city(billingCityEdittext.getText().toString());
					newSeller.setShipping_state(shippingStateEdittext.getText().toString());
					newSeller.setBilling_state(billingStateEdittext.getText().toString());

					newSeller.setVerfication_status(1);
					newSeller.setBank_name(EdittextbankName.getText().toString());
					System.out.println("---Value of Account  no is===="+EdittextaccountNumber.getText().toString());
					newSeller.setAccount_no(EdittextaccountNumber.getText().toString());
				/*	if(topUpDropDown.getText().toString().matches(""))
					{
						topUpDropDown.setText("0");
					}*/
					//System.out.println("----Android get the value of selected item in spinner is--"+topUpDropDown.getText().toString());
					newSeller.setTopup_amount(0);
					System.out.println("--Value of selected topup amout is --"+0);
					newSeller.setIfsc_code(EdittextifscCode.getText().toString());
					newSeller.setBeneficaryName(EdittextBeneficaryName.getText().toString());
					newSeller.setBranch(Edittextbranch.getText().toString());
					newSeller.setCst_tin_number(editTextCSTTINNumber.getText().toString());
					//					newSeller.setTopup_amount(topUpDropDown.toString());
					newSeller.setLocation(edittextLocation.getText().toString());
					newSeller.setLatitude(String.valueOf(latitude+" N"));
					newSeller.setLongitude(String.valueOf(longitude+" E"));
					newSeller.setMobile_verified("Yes");
					newSeller.setCrm_seller_id("0");

					//				db.getWritableDatabase();
					id=db.addNewSeller(newSeller);///Inserting the data into the local Database
					System.out.println("---Value of Returned Id is---"+id);
					//				db.getAllSellers();
					checkInternet = NetworkUtils.isNetworkAvailable(getActivity());
					if(checkInternet)
					{
						if(isAdminSessionFound)
							new AsyncUtilies.FirstAsyn(getChallenge(),sharedpreferences.getString("username", ""),"forUserToken").execute();
						else
							new AsyncUtilies.FirstAsyn(getChallenge(),sharedpreferences.getString("username", "")).execute();
					}
					else
					{
						progressBar.setVisibility(View.GONE);
						FragmentManager manager = getFragmentManager();
						FragmentDialog dialog=new FragmentDialog(); 
						successStatus="offline";
						dialog.show(manager, successStatus);
					}
				}
				else
				{
					if(isEditButtonClicked)
					{
						progressBar.setVisibility(View.VISIBLE);
						db=new DatabaseHandler(getActivity());

						newSeller=new NewSeller();
						if(fromWhere.equalsIgnoreCase("leads")){
							newSeller.setLead(true);
							newSeller.setLeadId(sellerInfo.getLeadId());
						}
						newSeller.setBussiness_name(bussinessNameEdittext.getText().toString());
						newSeller.setMobile_no(mobileNumberEdittext.getText().toString());

						newSeller.setEmail_id(emailIdEdittext.getText().toString());
						newSeller.setShipping_address(shippingAddressEdittext.getText().toString());
						newSeller.setBilling_address(billingAddressEdittext.getText().toString());

						newSeller.setShipping_pin_code(shippingPincodeEdittext.getSelectedItem().toString());
						newSeller.setBilling_pin_code(billingPincodeEdittext.getSelectedItem().toString());
						newSeller.setShipping_city(shippingCityEdittext.getText().toString());
						newSeller.setBilling_city(billingCityEdittext.getText().toString());
						newSeller.setShipping_state(shippingStateEdittext.getText().toString());
						newSeller.setBilling_state(billingStateEdittext.getText().toString());

						newSeller.setVerfication_status(1);
						newSeller.setBank_name(EdittextbankName.getText().toString());
						System.out.println("---Value of Account  no is===="+EdittextaccountNumber.getText().toString());
						newSeller.setAccount_no(EdittextaccountNumber.getText().toString());
						/*if(topUpDropDown.getText().toString().matches(""))
						{
							topUpDropDown.setText("0");
						}*/
						//System.out.println("----Android get the value of selected item in spinner is--"+topUpDropDown.getText().toString());
						newSeller.setTopup_amount(0);
						//System.out.println("--Value of selected topup amout is --"+Integer.parseInt(topUpDropDown.getText().toString()));
						newSeller.setIfsc_code(EdittextifscCode.getText().toString());
						newSeller.setBeneficaryName(EdittextBeneficaryName.getText().toString());
						newSeller.setBranch(Edittextbranch.getText().toString());
						newSeller.setCst_tin_number(editTextCSTTINNumber.getText().toString());
						//					newSeller.setTopup_amount(topUpDropDown.toString());
						newSeller.setLocation(edittextLocation.getText().toString());
						newSeller.setLatitude(String.valueOf(latitude+" N"));
						newSeller.setLongitude(String.valueOf(longitude+" E"));
						newSeller.setMobile_verified("Yes");
						newSeller.setCrm_seller_id(sellerInfo.getCrm_seller_id());

						//				db.getWritableDatabase();
						//						id=db.addNewSeller(newSeller);///Inserting the data into the local Database
						System.out.println("---Value of Returned Id is---"+id);
						//				db.getAllSellers();
						checkInternet = NetworkUtils.isNetworkAvailable(getActivity());
						if(checkInternet)
						{
							if(isAdminSessionFound)
								new AsyncUtilies.FirstAsyn(getChallenge(),sharedpreferences.getString("username", ""),"forUserToken").execute();
							else
								new AsyncUtilies.FirstAsyn(getChallenge(),sharedpreferences.getString("username", "")).execute();
						}
						else
						{


							NameRegistrationFragment newFragment = new NameRegistrationFragment();
							//							Bundle args = new Bundle();
							//							newFragment.setArguments(args);

							FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
							transaction.replace(R.id.container, newFragment);
							transaction.commit();
							FragmentManager manager = getFragmentManager();
							FragmentDialog dialog=new FragmentDialog(); 
							successStatus="offline";
							dialog.show(manager, successStatus);
						}
					}
					else
					{
						nextButton.setBackgroundResource(R.drawable.next_button2);
					}
				}
			} 

			break;
		case R.id.btnPrevious:
			System.out.println("---Inside previous button Click ABHI and Counter =="+counter);
			//			btnClearAll.setBackgroundResource(R.drawable.clear_all_button);
			if(counter==0){
				//
				btnPrevious.setClickable(false);

			}else if(counter==1){
				isMobileNumberVerified=true;
				if(fromWhere.equalsIgnoreCase("abc"))
				{	
					getActivity().getActionBar().setTitle(R.string.resgistration_1);
					btnPrevious.setBackgroundResource(R.drawable.prev_button);
					layoutName.setVisibility(View.VISIBLE);
					layoutBank.setVisibility(View.GONE);
					layoutLocation.setVisibility(View.GONE);
					counter--;
				}
				else
				{
					getActivity().getActionBar().setTitle(R.string.seller_detail_1);
					btnPrevious.setBackgroundResource(R.drawable.prev_button);
					nextButton.setBackgroundResource(R.drawable.next_button2);
					layoutName.setVisibility(View.VISIBLE);
					layoutBank.setVisibility(View.GONE);
					layoutLocation.setVisibility(View.GONE);
					counter--;
				}
			}else if(counter==2){
				nextButton.setEnabled(true);
				if(fromWhere.equalsIgnoreCase("abc"))
				{		
					nextButton.setBackgroundResource(R.drawable.next_button2);
					getActivity().getActionBar().setTitle(R.string.resgistration_2);
					isAdminSessionFound=false;
					layoutName.setVisibility(View.GONE);
					layoutBank.setVisibility(View.VISIBLE);
					layoutLocation.setVisibility(View.GONE);
					counter--;
				}
				else
				{
					getActivity().getActionBar().setTitle(R.string.seller_detail_2);
					nextButton.setBackgroundResource(R.drawable.next_button2);
					layoutName.setVisibility(View.GONE);
					layoutBank.setVisibility(View.VISIBLE);
					layoutLocation.setVisibility(View.GONE);
					counter--;
				}
			} 

			break;
		case R.id.verify_button:
			if(fromWhere.equalsIgnoreCase("abc"))
			{
				int randomPIN = (int)(Math.random()*9000)+1000;
				UniqueString=String.valueOf(randomPIN);
				System.out.println("---Value of Unique key is----"+UniqueString);
				String mob=mobileNumberEdittext.getText().toString();
				if(checkInternet)
				{
					if(verifyButton.getText().length()==8)
					{
						verifyButton.setClickable(false);
					}
					else
					{
						//						TODO
						verifyButton.setClickable(true);
						
						String firstCharacter=mobileNumberEdittext.getText().toString();
						firstCharacter=firstCharacter.substring(0,1);
						if(firstCharacter.equalsIgnoreCase("9") || firstCharacter.equalsIgnoreCase("8") || firstCharacter.equalsIgnoreCase("7")
								)
						{
							progressBarVerifiedButton.setVisibility(View.VISIBLE);
							mobileExistanceCheck=mobileNumberEdittext.getText().toString();
							new AsyncUtilies.FirstAsyn(getChallenge(),sharedpreferences.getString("username", "")).execute();
						}
						else
						{
							Toast toast=Toast.makeText(getActivity(), "Phone must start with 9,8,7!!!",Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}

					}
				}
				else
				{
					//				Toast.makeText(getActivity(), "No Internet Connectivity !!!",Toast.LENGTH_SHORT).show();
					isNumberNotExists=true;
					SmsManager smsManager = SmsManager.getDefault();
					PendingIntent sentPI;
					String SENT = "SMS_SENT";

					sentPI = PendingIntent.getBroadcast(getActivity(), 0,new Intent(SENT), 0);
					
					String firstCharacter=mobileNumberEdittext.getText().toString();
					firstCharacter=firstCharacter.substring(0,1);
					if(firstCharacter.equalsIgnoreCase("9") || firstCharacter.equalsIgnoreCase("8") || firstCharacter.equalsIgnoreCase("7")
							)
					{
						smsManager.sendTextMessage(mob, null, "Your verification key for askmeBazaar seller registration is" 
								+UniqueString, sentPI, null);
						verifyButton.setTextColor(Color.parseColor("#ffffff"));
						verifyButton.setText("Re-Verify");
					}
					else
					{
						Toast toast=Toast.makeText(getActivity(), "Phone must start with 9,8,7!!!",Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
					
				}
			}
			else
			{
				int randomPIN = (int)(Math.random()*9000)+1000;
				UniqueString=String.valueOf(randomPIN);
				System.out.println("---Value of Unique key is----"+UniqueString);
				String mob=mobileNumberEdittext.getText().toString();
				//				if(sellerInfo.getMobile_verified().equalsIgnoreCase("No"))
				//				{	
				if(checkInternet)
				{
					if(verifyButton.getText().length()==8)
					{
						verifyButton.setClickable(false);
					}
					else
					{
						//						TODO
						verifyButton.setClickable(true);
						String firstCharacter=mobileNumberEdittext.getText().toString();
						firstCharacter=firstCharacter.substring(0,1);
						if(firstCharacter.equalsIgnoreCase("9") || firstCharacter.equalsIgnoreCase("8") || firstCharacter.equalsIgnoreCase("7")
								)
						{
							progressBarVerifiedButton.setVisibility(View.VISIBLE);
							mobileExistanceCheck=mobileNumberEdittext.getText().toString();
							new AsyncUtilies.FirstAsyn(getChallenge(),sharedpreferences.getString("username", "")).execute();
						}
						else
						{
							Toast toast=Toast.makeText(getActivity(), "Phone must start with 9,8,7!!!",Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}						

					}
				}
				else
				{
					//				Toast.makeText(getActivity(), "No Internet Connectivity !!!",Toast.LENGTH_SHORT).show();
					isNumberNotExists=true;
					SmsManager smsManager = SmsManager.getDefault();
					PendingIntent sentPI;
					String SENT = "SMS_SENT";

					sentPI = PendingIntent.getBroadcast(getActivity(), 0,new Intent(SENT), 0);
					String firstCharacter=mobileNumberEdittext.getText().toString();
					firstCharacter=firstCharacter.substring(0,1);
					if(firstCharacter.equalsIgnoreCase("9") || firstCharacter.equalsIgnoreCase("8") || firstCharacter.equalsIgnoreCase("7")
							)
					{
						smsManager.sendTextMessage(mob, null, "Your verification key for askmeBazaar seller registration is" 
								+UniqueString, sentPI, null);
						verifyButton.setTextColor(Color.parseColor("#ffffff"));
						verifyButton.setText("Re-Verify");
					}
					else
					{
						Toast toast=Toast.makeText(getActivity(), "Phone must start with 9,8,7!!!",Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
				}
				//				}
				//				else
				//				{
				//					//					verifyButton.setText("Re-Verify");
				//				}

			}
			break;
		case R.id.btnClearAll:
			if(fromWhere.equalsIgnoreCase("abc"))
			{

				//				btnClearAll.setBackgroundColor(Color.parseColor("#0d469b"));
				if(layoutName.getVisibility()==View.VISIBLE)
				{
					isNumberNotExists=false;
					bussinessNameEdittext.setText("");
					mobileNumberEdittext.setText("");
					emailIdEdittext.setText("");
					billingAddressEdittext.setText("");
					shippingAddressEdittext.setText("");
					//					shippingPincodeEdittext.setText("");
					shippingCityEdittext.setText("");
					shippingStateEdittext.setText("");
					billingCityEdittext.setText("");
					billingStateEdittext.setText("");
					verficationKeyEdittext.setText("");
					makeBillingSameAsShipping.setChecked(false);
					verifyButton.setText("Verify");
					verifyButton.setBackgroundResource(R.drawable.drawable_btn_verify);

				}else if(layoutBank.getVisibility()==View.VISIBLE)
				{
					EdittextbankName.setText("");
					Edittextbranch.setText("");
					EdittextaccountNumber.setText("");
					EdittextifscCode.setText("");
					EdittextBeneficaryName.setText("");
					//topUpDropDown.setText("");
					editTextCSTTINNumber.setText("");

				}else if(layoutLocation.getVisibility()==View.VISIBLE)
				{
					edittextLocation.setText("");
					nextButton.setEnabled(true);
					
				}
			}
			else
			{
				isEditButtonClicked=true;
				btnClearAll.setText("Reset");
				if(layoutLocation.getVisibility()==View.VISIBLE)
				{
					nextButton.setEnabled(true);
					nextButton.setBackgroundResource(R.drawable.green_check);
					
				}
				allEdittextEditable();
			}
			break;

		case R.id.markLocationButton:
			gps = new com.ambsellerapp.utilies.GPSTracker(getActivity());
			//			if(fromWhere.equalsIgnoreCase("abc"))
			//			{	
			// check if GPS enabled		
			if(gps.canGetLocation())
			{
				TxtViewQuestionMark.setVisibility(View.GONE);
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
				googleMap=mMapView.getMap();
				googleMap.getUiSettings().setAllGesturesEnabled(false);
				edittextLocation.setText(latitude+" N,"+longitude+" E");///Setting the current latitude and longitude in the Location Edittext
				googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				cameraLatLng = new LatLng(latitude, longitude);//Setting the camera latitude and longitude
				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraLatLng, 15));
				googleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);  // Zoom in, animating the camera.
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.draggable(false);
				markerOptions.position(new LatLng(latitude, longitude));
				markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
				googleMap.addMarker(markerOptions);
			}
			else
			{
				Toast toast=Toast.makeText(getActivity(), "No internet connectivity!!!",Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			//			}
			break;
		default:
			break;
		}
	}
	/**
	 * Checking weather the mobile number is valid or not
	 * @param target
	 * @return
	 */
	public static final boolean isValidPhoneNumber(CharSequence target) 
	{ 
		if (target == null || TextUtils.isEmpty(target)) 
		{ 
			return false; 
		}
		else 
		{ 
			return android.util.Patterns.PHONE.matcher(target).matches(); 
		} 
	}
}
