package com.ambsellerapp.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ambsellerapp.modals.NewSeller;
import com.AMBSEA.R;

public class SellerListingAdapter extends BaseAdapter 
{
	private ArrayList<NewSeller> list;
	ViewHolder holder;
	private LayoutInflater mInflator;
	private Context mCtx;
	 private int mLastPosition;
	public SellerListingAdapter(Context ctx,  ArrayList<NewSeller>  nameCollection) 
	{
		list = nameCollection;
		Log.d("Seller", "@Abhi::Context:: " + ctx);
		mCtx=ctx;
		mInflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() 
	{
		return list.size();
	}

	@Override
	public Object getItem(int arg0) 
	{
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) 
	{
		return list.size();
	}

	@Override
	public View getView(int position, View arg1, ViewGroup parent) 
	{
		View view = arg1;
		if(view==null)
		{
			view=mInflator.inflate(com.AMBSEA.R.layout.product_list_item,null);
			holder = new ViewHolder();
			holder.pinCode=(TextView)view.findViewById(R.id.sellerPINCodetextview);
			holder.name=(TextView)view.findViewById(R.id.sellerNametextview);
			holder.address=(TextView)view.findViewById(R.id.sellerAddresstextview);
			holder.phone=(TextView)view.findViewById(R.id.sellerNumbertextview);
			holder.topupAmount=(TextView)view.findViewById(R.id.topAmounttextview);
			view.setTag(holder);
		}
		else 
		{
			holder = (ViewHolder) view.getTag();
		}
//		s= (HashMap)list.get(arg0);
		final NewSeller sellerInfo= list.get(position);
		holder.pinCode.setText("PIN:"+sellerInfo.getShipping_pin_code());
		holder.name.setText(sellerInfo.getBussiness_name());
		holder.address.setText(sellerInfo.getBilling_address());
//		holder.phone.setText(sellerInfo.getMobile_no());
		SpannableString spanString = new SpannableString(sellerInfo.getMobile_no());
		spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
		holder.phone.setText(spanString);
		int sellTop=sellerInfo.getTopup_amount();
		if(String.valueOf(sellTop).length()<=2)
		{	
			sellTop=sellTop*1000;
			holder.topupAmount.setText("Top-up \n"+String.valueOf(sellTop));
		}
		else
		{
			holder.topupAmount.setText("Top-up \n"+String.valueOf(sellTop));
		}
		holder.phone.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" +sellerInfo.getMobile_no()));
				mCtx.startActivity(intent); 
			}
		});
		
//		  float initialTranslation = (mLastPosition <= position ? 500f : -500f);
		  
			/*Animation animationY = new TranslateAnimation(0, 0, parent.getHeight()/4, 0);
			animationY.setDuration(1000);
			view.startAnimation(animationY);  
			animationY = null; */
		  
		  
//          view.setTranslationY(initialTranslation);
//          view.animate().setInterpolator(new DecelerateInterpolator(1.0f))
//                  .translationY(0f)
//                  .setDuration(300l)
//                  .setListener(null);
//
//          // Keep track of the last position we loaded 
//          mLastPosition = position;
		
		return view;
	}

	public static class ViewHolder 
	{
		public TextView name,address,phone,topupAmount,pinCode;
	}
}
