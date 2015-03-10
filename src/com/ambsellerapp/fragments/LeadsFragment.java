package com.ambsellerapp.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.AMBSEA.R;
import com.ambsellerapp.activities.LauncherActivity;
import com.ambsellerapp.adapters.ExpandableListAdapter;
import com.ambsellerapp.adapters.SellerListingAdapter;
import com.ambsellerapp.asynctasks.AsyncUtilies;
import com.ambsellerapp.listeners.CRMListener;
import com.ambsellerapp.listeners.LeadsListener;
import com.ambsellerapp.listeners.RefreshListener;
import com.ambsellerapp.modals.LeadsData;
import com.ambsellerapp.modals.NewSeller;
import com.ambsellerapp.utilies.CRMUtilities;
import com.ambsellerapp.utilies.NetworkUtils;
import com.ambsellerapp.utilies.SellerUtilities;
import com.ambsellerapp.utilies.Utility;

public class LeadsFragment extends Fragment implements RefreshListener,LeadsListener
{
	private View contentView;
	private TextView noRecordFoundTextview;
	private SharedPreferences sharedpreferences;
	private ExpandableListView leadsList;
	private Bundle bundle=null;
	private String fromWhere="";
	public static final int LEADS = 6;
	private ProgressBar progressBar;
	private Context ctx;
	private ArrayList<LeadsData> leadsArraylist=null;
	private ExpandableListAdapter adapter=null;
	private int lead_counter=0;
	private boolean checkInternet;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setHasOptionsMenu (true);
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		menu.findItem(R.id.registration).setVisible(true).setEnabled(true);
		menu.findItem(R.id.sellerList).setVisible(true).setEnabled(true);
		menu.findItem(R.id.leads).setVisible(false).setEnabled(false);
		menu.findItem(R.id.search).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.findItem(R.id.create_leads).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.findItem(R.id.refresh).setVisible(false).setEnabled(false);
		super.onCreateOptionsMenu(menu, inflater);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		try{
		contentView = inflater.inflate(R.layout.leads_listing_fragment_layout, container, false);
		getActivity().getActionBar().show();
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		setHasOptionsMenu(true);
		this.getActivity().getActionBar().setDisplayShowCustomEnabled(true);
		this.getActivity().getActionBar().setDisplayShowTitleEnabled(false);
		((LauncherActivity) getActivity()).currentFragment = LEADS;
		com.ambsellerapp.activities.LauncherActivity.fromLead=0;
		
		leadsList=(ExpandableListView)contentView.findViewById(R.id.leads_listview);
		progressBar=(ProgressBar)contentView.findViewById(R.id.leadsProgressBarSellerListFragment);
		noRecordFoundTextview=(TextView)contentView.findViewById(R.id.leads_no_result_found_textview);

		ctx=this.getActivity();
		checkInternet = NetworkUtils.isNetworkAvailable(ctx);
		sharedpreferences = getActivity().getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		
		if(checkInternet){
			
		getActivity().getActionBar().setDisplayShowCustomEnabled(true);
		progressBar.setVisibility(View.VISIBLE);
		Log.d("MSG","1");
		SellerUtilities sellUtl=new SellerUtilities(ctx,"","leads");
		Log.d("MSG","2");
		sellUtl.setLeadsEndListner(new LeadsListener() 
		{
			public void onLeadsComplete(ArrayList<LeadsData> leadsSellerList) 
			{
				
				Log.d("MSG","3");
				leadsArraylist = leadsSellerList;
				progressBar.setVisibility(View.GONE);
				lead_counter=leadsArraylist.size();
				LayoutInflater inflator = getActivity().getLayoutInflater();
				View v = inflator.inflate(R.layout.badge_layout, null);
				if(lead_counter<10)
					((TextView)v.findViewById(R.id.counter)).setText(Html.fromHtml("<font color='#ffffff'>0"+lead_counter+"</font>"));
				else
					((TextView)v.findViewById(R.id.counter)).setText(Html.fromHtml("<font color='#ffffff'>"+lead_counter+"</font>"));				
				getActivity().getActionBar().setCustomView(v);
				
				if(!leadsArraylist.isEmpty())
				{	Log.d("MSG","4");
					noRecordFoundTextview.setVisibility(View.GONE);
					leadsList.setVisibility(View.VISIBLE);
					adapter=new ExpandableListAdapter(ctx, leadsArraylist,(ActionBarActivity)getActivity(),new RefreshListener() {

						private void childButtonClick()
						{
						
						}
						@Override
						public void refresh() {
							// TODO Auto-generated method stub
							NameRegistrationFragment newFragment = new NameRegistrationFragment();
							Bundle args = new Bundle();
							//			newFragment.setArguments(args);

							FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
							transaction.replace(R.id.container, newFragment);
							transaction.addToBackStack("HomeFragment"); 
							transaction.commit();
						}
					});

					leadsList.setAdapter(adapter);
				}
				else
				{
					noRecordFoundTextview.setVisibility(View.VISIBLE);
				}
			}
		});
		new AsyncUtilies.FirstAsyn(sellUtl.getChallenge(),sharedpreferences.getString("username", "")).execute();
		}
		else{
			getActivity().getActionBar().setDisplayShowCustomEnabled(false);
			Toast.makeText(getActivity(), "Please, Check Internet Connectivity!!!", Toast.LENGTH_SHORT).show();
		}
		}catch(Exception ex){
			Log.d("LeadsFragment", ex.getMessage());
		}
		return contentView;
	}
	public void onResume() 
	{
		super.onResume();
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
				case R.id.search:
					if(checkInternet){
					FragmentManager manager = getActivity().getSupportFragmentManager();
					LeadsSearchDialog dialog=new LeadsSearchDialog(); 
					dialog.setTargetFragment(this, 0);
					dialog.show(manager, "successStatus");
					//noRecordFoundTextview.setVisibility(View.VISIBLE);
					}
					else{
						Toast.makeText(getActivity(), "Please, Check Internet Connectivity!!!", Toast.LENGTH_SHORT).show();
					}
					return true;				
			default:
			return super.onOptionsItemSelected(item);
		}
	}

	
	private void setListners()
	{
		//		leadsList.setOnGroupClickListener(new OnGroupClickListener() {
		//			
		//			@Override
		//			public boolean onGroupClick(ExpandableListView parent, View v,
		//					int groupPosition, long id) {
		//				// TODO Auto-generated method stub
		//				return false;
		//			}
		//		});
		//		leadsList.setOnGroupExpandListener(new OnGroupExpandListener() {
		//
		//			@Override
		//			public void onGroupExpand(int groupPosition) {
		//				leadsList.getChildAt(groupPosition);
		//			}
		//		});
		//		leadsList.setOnGroupCollapseListener(new OnGroupCollapseListener() {
		//
		//			@Override
		//			public void onGroupCollapse(int groupPosition) {
		//
		//			}
		//		});
		//		leadsList.setOnChildClickListener(new OnChildClickListener() 
		//		{
		//			public boolean onChildClick(ExpandableListView parent, View v,
		//					int groupPosition, int childPosition, long id) 
		//			{
		//				
		//				return false;
		//			}
		//		});

	}
	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		NameRegistrationFragment newFragment = new NameRegistrationFragment();
		Bundle args = new Bundle();
		//			newFragment.setArguments(args);

		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, newFragment);
		transaction.addToBackStack("NameRegistration"); 
		transaction.commit();	
	}
	
	@Override
	public void onLeadsComplete(ArrayList<LeadsData> leadsSellerList) 
	{
		leadsArraylist = new ArrayList<LeadsData>();
		leadsArraylist = leadsSellerList;
		progressBar.setVisibility(View.GONE);
		if(!leadsArraylist.isEmpty())
		{	
			getActivity().getActionBar().setDisplayShowCustomEnabled(true);
			lead_counter=leadsSellerList.size();
			Log.d("MSG",String.valueOf(lead_counter));
			LayoutInflater inflator = getLayoutInflater(null);
			View v = inflator.inflate(R.layout.badge_layout, null);
			if(lead_counter<10)
				((TextView)v.findViewById(R.id.counter)).setText(Html.fromHtml("<font color='#ffffff'>0"+lead_counter+"</font>"));
			else
				((TextView)v.findViewById(R.id.counter)).setText(Html.fromHtml("<font color='#ffffff'>"+lead_counter+"</font>"));
			getActivity().getActionBar().setCustomView(v);
			noRecordFoundTextview.setVisibility(View.GONE);
			leadsList.setVisibility(View.VISIBLE);
			adapter=new ExpandableListAdapter(ctx, leadsArraylist,(ActionBarActivity)getActivity(),new RefreshListener() {

				private void childButtonClick()
				{
				
				}
				@Override
				public void refresh() {
					// TODO Auto-generated method stub
					NameRegistrationFragment newFragment = new NameRegistrationFragment();
					Bundle args = new Bundle();
					//			newFragment.setArguments(args);

					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.container, newFragment);
					transaction.addToBackStack("HomeFragment"); 
					transaction.commit();
				}
			});

			leadsList.setAdapter(adapter);
		}
		else
		{
			getActivity().getActionBar().setDisplayShowCustomEnabled(false);
			leadsList.setVisibility(View.GONE);
			noRecordFoundTextview.setVisibility(View.VISIBLE);
		}
	}


	


}
