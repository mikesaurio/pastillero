package com.mikesaurio.pastillero;

import java.io.IOException;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mikesaurio.pastillero.bd.DBHelper;
import com.mikesaurio.pastillero.custom.CustomList;
import com.mikesaurio.pastillero.dialogos.DatosDialogActivity;
import com.mikesaurio.pastillero.fragments.DashboardFragment;
;


/**
 * clase principal que controla el mainFrame y la lista lateral(menu)
 * @author mikesaurio
 *
 */
public class PastilleroActivity extends ActionBarActivity {

	private DrawerLayout drawerLayout;
	private ListView navList;
	private  CustomList adapter;

	private ActionBarDrawerToggle drawerToggle;
	private DashboardFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.main_pastillero);

		
		try {
			DBHelper	BD = new DBHelper(PastilleroActivity.this);
		    BD.loadDataBase(BD);
			BD.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.marco_negro));
		LayoutInflater mInflater = LayoutInflater.from(this);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		
		
		this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		this.navList = (ListView) findViewById(R.id.left_drawer);

		// Load an array of options names
		final String[] names = getResources().getStringArray(R.array.nav_options);

		// Set previous array as adapter of the list
	//	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, names);
		adapter = new CustomList(PastilleroActivity.this,names);
		navList.setAdapter(adapter);
		navList.setOnItemClickListener(new DrawerItemClickListener());
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.drawable.ic_launcher, R.string.app_name,R.string.app_name) {
			public void onDrawerClosed(View view) {
				supportInvalidateOptionsMenu();
			}
			public void onDrawerOpened(View drawerView) {
				supportInvalidateOptionsMenu();
			}
		};

		drawerLayout.setDrawerListener(drawerToggle);
		
		selectItem(0);

	}

	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			
			return true;
		}
		 switch (item.getItemId()){
		 case R.id.action_mas:
			 fragment.setMensaje();
	      return true;
		 }
		
		return super.onOptionsItemSelected(item);
	}

	


	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}
	
	
	private void selectItem(int position) {
		if(position==0){
			 fragment = new DashboardFragment(PastilleroActivity.this);
			Bundle args = new Bundle();
			fragment.setArguments(args);
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			navList.setItemChecked(position, true);
			drawerLayout.closeDrawer(navList);

		}
		
		
		
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = drawerLayout.isDrawerOpen(navList);
		menu.findItem(R.id.action_mas).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	
	
}
