package com.mikesaurio.pastillero.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mikesaurio.pastillero.R;

/**
 * clase que controla el add de eventos
 * 
 * @author mikesaurio
 * 
 */
public class AcercaDeFragment extends Fragment  {

	Activity activity;
	ImageView iv_twitter,iv_git;

	
	
	/**
	 * Constructor
	 * @param activity
	 */
	public AcercaDeFragment(Activity activity){
		this.activity=activity;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.activity_acerca_de, container,false);
		iv_twitter=(ImageView)v.findViewById(R.id.acerca_de_iv_twitter);
		iv_twitter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try
				{
				    // Check if the Twitter app is installed on the phone.
					activity. getPackageManager().getPackageInfo("com.twitter.android", 0);

				    Intent intent = new Intent(Intent.ACTION_VIEW);
				    intent.setClassName("com.twitter.android", "com.twitter.android.ProfileActivity");
				    // Don't forget to put the "L" at the end of the id.
				    intent.putExtra("user_id", 156322749L);
				    startActivity(intent);
				}
				catch (NameNotFoundException e)
				{
				    // If Twitter app is not installed, start browser.
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/yosoymikesaurio")));
				}
				
			}
		});
		
		iv_git=(ImageView)v.findViewById(R.id.acerca_de_iv_git);
		iv_git.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mikesaurio")));
			}
		});
		
		
	
		
		return v;
	}
	
	

	
	
	
	

}
