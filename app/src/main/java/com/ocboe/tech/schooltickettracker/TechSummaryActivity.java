package com.ocboe.tech.schooltickettracker;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ocboe.tech.schooltickettracker.updater.AppUpdate;
import com.ocboe.tech.schooltickettracker.updater.AppUpdateUtil;
import com.ocboe.tech.schooltickettracker.updater.DownloadUpdateService;

import org.w3c.dom.Text;

public class TechSummaryActivity extends ActionBarActivity {

	private String[] mDrawerItems;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
    private CharSequence mTitle;
	public static Context mContext;
	public static View mView;
	private NavigationView navigationView;
	private DrawerLayout drawerLayout;
	private Toolbar tb;
	private FragmentManager fm;
	private TextView header_user_name, header_email;


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mContext = this;
		mView = findViewById(android.R.id.content);
		/*Fragment mFragment = new TechSummaryFragment();
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		ft.add(R.id.summaryListContainer, mFragment);
		ft.commit();*/
		SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

		setContentView(R.layout.tech_summary);

		fm = getFragmentManager();

		setToolBar();
		setUpNavDrawer();

		navigationView = (NavigationView) findViewById(R.id.navigation);

		View header = navigationView.getHeaderView(0);
		header_user_name = (TextView)header.findViewById(R.id.header_username);
		header_email = (TextView)header.findViewById(R.id.header_email);

		SetHeader();


		//Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

			// This method will trigger on item Click of navigation menu
			@Override
			public boolean onNavigationItemSelected(MenuItem menuItem) {


				//Checking if the item is in checked state or not, if not make it in checked state
				if(menuItem.isChecked()) menuItem.setChecked(false);
				else menuItem.setChecked(true);

				//Closing drawer on item click
				drawerLayout.closeDrawers();

				//Check to see which item was being clicked and perform appropriate action
				switch (menuItem.getItemId()){


					//Replacing the main content with ContentFragment Which is our Inbox View;
					case R.id.techsummary:
						Toast.makeText(getApplicationContext(),"TechSummary Selected",Toast.LENGTH_SHORT).show();
						return true;

					// For rest of the options we just show a toast on click

					case R.id.dispose:
						Toast.makeText(getApplicationContext(),"Dispose Selected",Toast.LENGTH_SHORT).show();
						return true;

					case R.id.inventory:
						Toast.makeText(getApplicationContext(),"Inventory Selected",Toast.LENGTH_SHORT).show();
						return true;
					case R.id.settings:
						Toast.makeText(getApplicationContext(),"Settings Selected",Toast.LENGTH_SHORT).show();
						return true;
					default:
						return true;
				}
			}
		});

		// Initializing Drawer Layout and ActionBarToggle
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		Fragment fragment = new TechSummaryFragment();

		fm.beginTransaction()
				.replace(R.id.summaryListContainer, fragment, "techsummaryfragment")
				//.addToBackStack(null)
				.commit();

	}

	private void SetHeader() {

		header_user_name.setText(Properties.getName());
		header_email.setText(Properties.getEmail());
	}

	public void onBackPressed() {
		FragmentManager fragmentManager = getFragmentManager();
		if(fragmentManager.getBackStackEntryCount() != 0) {
			fragmentManager.popBackStack();
			TechSummaryFragment techFrag = (TechSummaryFragment)getFragmentManager().findFragmentByTag("techsummaryfragment");
			if (techFrag.isVisible()) { //If we are in fragment A when we press the back button, finish is called to exit
				finish();
			} else  {
				//displayView(0); //else, switch to fragment A
			}
		} else {
			super.onBackPressed();
		}
	}

	private void setToolBar() {
		tb = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(tb);
		ActionBar ab = getSupportActionBar();
		ab.setHomeAsUpIndicator(R.drawable.icon);
		//ab.setDisplayHomeAsUpEnabled(true);
	}

	private void setUpNavDrawer() {
		if (tb != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			tb.setNavigationIcon(R.drawable.ic_menu_black_24dp);
			tb.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					drawerLayout.openDrawer(GravityCompat.START);
				}
			});
		}
	}

	private void selectItem(int position) {
        // update the main content by replacing fragments

		switch(position){
		case 0:
			if(fm.findFragmentByTag("techsummaryfragment") == null){
		        Fragment fragment = new TechSummaryFragment();

		        fm.beginTransaction()
		        	.replace(R.id.summaryListContainer, fragment, "techsummaryfragment")
		        	//.addToBackStack(null)
		        	.commit();
	        }else {
	        	fm.popBackStack("techsummaryfragment",0);
	        	//Fragment fragment = new TechSummaryFragment();

		        //fragmentManager.beginTransaction().replace(R.id.summaryListContainer, fragment, "techsummaryfragment").commit();
	        }
			if(fm.findFragmentByTag("DisposeEquipment") != null){
				fm.popBackStack("DisposeEquipment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
	        break;
		case 1:
			if(fm.findFragmentByTag("techsummaryfragment") != null){
				fm.popBackStack("techsummaryfragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
			if(fm.findFragmentByTag("DisposeEquipment") != null){
				fm.popBackStack("DisposeEquipment", 0);
			} else {
			//fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			fm.popBackStack();
			Fragment disposeFragment = new DisposeEquipmentFragment();
			fm.beginTransaction()
				.replace(R.id.summaryListContainer, disposeFragment)
				.addToBackStack("DisposeEquipment")
				.commit();
			}

			break;
		case 2:
			if(fm.findFragmentByTag("techsummaryfragment") != null){
				fm.popBackStack("techsummaryfragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
			if(fm.findFragmentByTag("DisposeEquipment") != null){
				fm.popBackStack("DisposeEquipment", 0);
			} else {
			//fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			fm.popBackStack();
			Fragment inventoryFragment = new InventoryRoomFragment();
			fm.beginTransaction()
				.replace(R.id.summaryListContainer, inventoryFragment)
				.addToBackStack("InventorySearch")
				.commit();
			}

			break;
		case 3:
			Fragment Settings = new SettingsFragment();

	        FragmentManager SettingsFragmentManager = getFragmentManager();
	        //fragmentManager.popBackStack("TechSummaryFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
	        SettingsFragmentManager.beginTransaction()
		        .addToBackStack(null)
		        .replace(R.id.summaryListContainer, Settings)
		        .commit();

	        break;
			default:
	    	break;
		}
    }

	public void setActionBarTitle(String title) {
		getSupportActionBar().setTitle(title);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
