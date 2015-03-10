package com.ambsellerapp.listeners;

import java.util.ArrayList;

import com.ambsellerapp.modals.NewSeller;

public interface SearchListener
{
	public void onSearchComplete(ArrayList<NewSeller> searchSellerList);
}
