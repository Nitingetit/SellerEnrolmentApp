package com.ambsellerapp.listeners;

import java.util.ArrayList;

import com.ambsellerapp.modals.LeadsData;
import com.ambsellerapp.modals.NewSeller;

public interface LeadsListener
{
	public void onLeadsComplete(ArrayList<LeadsData> leadsSellerList);
}
