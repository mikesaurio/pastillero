package com.mikesaurio.pastillero.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikesaurio.pastillero.R;

/**
 * clase que controla el add de eventos
 * 
 * @author mikesaurio
 * 
 */
public class CitasFragment extends Fragment  {

	Activity activity;

	
	
	/**
	 * Constructor
	 * @param activity
	 */
	public CitasFragment(Activity activity){
		this.activity=activity;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.activity_citas, container,false);
		
		
		
	
		
		return v;
	}
	
	

	
	
	
	

}
