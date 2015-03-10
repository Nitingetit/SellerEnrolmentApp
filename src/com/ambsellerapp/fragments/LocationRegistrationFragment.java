package com.ambsellerapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ambsellerapp.activities.LauncherActivity;
import com.AMBSEA.R;
import com.ambsellerapp.utilies.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationRegistrationFragment extends Fragment implements OnClickListener
{
	private View contentView;
	private boolean checkInternet = false;
	private EditText locationEdittext;
	private Button saveButton;
	private Button previousButton;
	private Button markLocationButton;
	private SharedPreferences sharedpreferences;
	private ProgressBar progressBar;
	public static final int LOCATION_REGISTRATION_FRAGMENT = 2;
	private MapView mMapView;
	private GoogleMap googleMap;
	private com.ambsellerapp.utilies.GPSTracker gps;
	private LatLng cameraLatLng = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		contentView = inflater.inflate(R.layout.location_registration_form, container, false);

		SettingIds();///Call for Setting the ids of the Views inside the layout
		saveButton.setOnClickListener(this);
		previousButton.setOnClickListener(this);
		markLocationButton.setOnClickListener(this);
		MapsInitializer.initialize(getActivity());
		mMapView.onCreate(savedInstanceState);
		return contentView;
	}

	@Override
	public void onResume()
	{
		super.onResume();
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

	/** Method for settting the ids of the layouts
	 * @return void
	 */
	private void SettingIds()
	{
		sharedpreferences = getActivity().getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		locationEdittext=(EditText) contentView.findViewById(R.id.location_edittext);

//		saveButton=(Button)contentView.findViewById(R.id.btnSaveLocation);
//		previousButton=(Button)contentView.findViewById(R.id.btnPreviousLocation);
		markLocationButton=(Button)contentView.findViewById(R.id.markLocationButton);
		progressBar=(ProgressBar)contentView.findViewById(R.id.progress);
		mMapView=(MapView)contentView.findViewById(R.id.mapView);
		((LauncherActivity)getActivity()).currentFragment=LOCATION_REGISTRATION_FRAGMENT;///Setting the curent fragment for backButton Handling

	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId())
		{
//		case R.id.btnSaveLocation:
//			//			checkEnteredDetails();
//			break;
//
//		case R.id.btnPreviousLocation:
//			getActivity().onBackPressed();
//			break;
			
		case R.id.markLocationButton:
			gps = new com.ambsellerapp.utilies.GPSTracker(getActivity());

			// check if GPS enabled		
	        if(gps.canGetLocation())
	        {
	        	double latitude = gps.getLatitude();
	        	double longitude = gps.getLongitude();
	    		googleMap=mMapView.getMap();
	    		googleMap.getUiSettings().setAllGesturesEnabled(false);
	    		locationEdittext.setText(latitude+" N,"+longitude+" S");///Setting the current latitude and longitude in the Location Edittext
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
	        	Toast.makeText(getActivity(),"No internet connectivity",Toast.LENGTH_SHORT).show();
	        }
			break;
		default:
			break;
		}
	}
}
