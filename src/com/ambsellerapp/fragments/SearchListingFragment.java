package com.ambsellerapp.fragments;


import java.util.ArrayList;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ambsellerapp.activities.LauncherActivity;
import com.ambsellerapp.adapters.SellerListingAdapter;
import com.ambsellerapp.asynctasks.AsyncUtilies;
import com.ambsellerapp.listeners.CRMListener;
import com.ambsellerapp.listeners.SearchDataListener;
import com.ambsellerapp.listeners.SearchListener;
import com.ambsellerapp.modals.NewSeller;
import com.AMBSEA.R;
import com.ambsellerapp.utilies.NetworkUtils;
import com.ambsellerapp.utilies.SellerUtilities;
import com.ambsellerapp.utilies.Utility;
import com.google.android.gms.internal.mc;

public class SearchListingFragment<AlphaInAnimationAdapter> extends Fragment implements SearchListener
{
	private View contentView;
	private ListView productList;
	private Context ctx;
	private ProgressBar progressBar;
	private EditText searchEdittext;
	private ImageView goButton;
	private TextView noRecordFoundTextview;
	private SharedPreferences sharedpreferences;
	private ArrayList<NewSeller> sellerArraylist=null;
	private static final int SELLER_LISTING_FRAGMENT=3;
	private String pinCode="",fromWhere="";
	private ArrayList<String> arrayPinCode = new ArrayList<String>();
	private Bundle bundle=null;
	private boolean checkInternet;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setHasOptionsMenu (true);
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		menu.findItem(R.id.registration).setVisible(true).setEnabled(true);
		menu.findItem(R.id.sellerList).setVisible(false).setEnabled(false);
		menu.findItem(R.id.create_leads).setVisible(false).setEnabled(false);
		menu.findItem(R.id.leads).setVisible(true).setEnabled(true);
		menu.findItem(R.id.search).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.findItem(R.id.refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		super.onCreateOptionsMenu(menu, inflater);
	}
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) 
	{
		bundle=getArguments();
		if(!bundle.isEmpty())
		{
			fromWhere=bundle.getString("SearchList");
		}
		contentView=inflater.inflate(com.AMBSEA.R.layout.search_listing_fragment_layout,container, false);
		productList=(ListView)contentView.findViewById(R.id.product_listview);
		progressBar=(ProgressBar)contentView.findViewById(R.id.progressBarSellerListFragment);
		noRecordFoundTextview=(TextView)contentView.findViewById(R.id.no_result_found_textview);

		ctx=this.getActivity();
		checkInternet = NetworkUtils.isNetworkAvailable(ctx);
		
		sharedpreferences = getActivity().getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);

		return contentView;
	}

	@Override
	public void onResume() 
	{
		getActivity().getActionBar().setTitle("Seller List");
		((LauncherActivity)getActivity()).currentFragment=SELLER_LISTING_FRAGMENT;
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		if(checkInternet){
		setListners();
	
		if(!fromWhere.equalsIgnoreCase("SearchList"))
		{	
			Set<String> set = sharedpreferences.getStringSet("pincodeList", null);
			arrayPinCode=new ArrayList<String>(set);

			if(!arrayPinCode.isEmpty())
			{
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
				progressBar.setVisibility(View.VISIBLE);
				SellerUtilities sellUtl=new SellerUtilities(ctx,pinCode,"search");
				sellUtl.setEndListner(new SearchDataListener() 
				{
					public void getCallback(ArrayList<NewSeller> searchSellerList) 
					{
						onSearchComplete(searchSellerList);
					}
				});
				new AsyncUtilies.FirstAsyn(sellUtl.getChallenge(),sharedpreferences.getString("username", "")).execute();
			}
			else
			{
				noRecordFoundTextview.setVisibility(View.VISIBLE);
			}
		}
		else
		{
				FragmentManager manager = getActivity().getSupportFragmentManager();
				SearchDialog dialog=new SearchDialog(); 
				dialog.setTargetFragment(this, 0);
				dialog.show(manager, "successStatus");
				noRecordFoundTextview.setVisibility(View.GONE);
		}
		}
		else{
		
			Toast.makeText(getActivity(), "Please, Check Internet Connectivity!!!", Toast.LENGTH_SHORT).show();
		}
		
		super.onResume();
	}
	private void setListners()
	{
		productList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> viewgroup, View view, int position,long id) 
			{
				Intent i=new Intent();
				NameRegistrationFragment newFragment = new NameRegistrationFragment();
				Bundle args=new Bundle();
				NewSeller item=sellerArraylist.get(position);
				args.putSerializable("sellerArraylist", item);
				args.putString("from", "search");
				newFragment.setArguments(args);
				LauncherActivity.fromSearch=1;
				FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction(); 

				// Replace whatever is in the fragment_container view with this fragment,and add the transaction to the back stack so the user can navigate back 
				transaction.replace(R.id.container, newFragment);
				transaction.addToBackStack("SellerListingFragment"); 

				// Commit the transaction 
				transaction.commit();

			}
		});

	}

	@Override
	public void onSearchComplete(ArrayList<NewSeller> searchSellerList) 
	{
		sellerArraylist = searchSellerList;
		progressBar.setVisibility(View.GONE);
		if(!sellerArraylist.isEmpty())
		{	
			noRecordFoundTextview.setVisibility(View.GONE);
			productList.setVisibility(View.VISIBLE);
			SellerListingAdapter adapter=new SellerListingAdapter(ctx, sellerArraylist);
			//				adapter.notifyDataSetChanged();
			productList.setAdapter(adapter);
		}
		else
		{
			noRecordFoundTextview.setVisibility(View.VISIBLE);
		}
	}

	@Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	  // Take appropriate action for each action item click
	  switch (item.getItemId()) {
	    case R.id.search:

	     SearchListingFragment searchFragForSearch=new SearchListingFragment();
	     Bundle arg=new Bundle();
	     arg.putString("SearchList", "SearchList");
	     searchFragForSearch.setArguments(arg);
	     FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction(); 
	     trans1.replace(R.id.container, searchFragForSearch);
	     trans1.commit();
	     //noRecordFoundTextview.setVisibility(View.VISIBLE);   
	     return true;
	   default:
	   return super.onOptionsItemSelected(item);
	  }
	 }

}
