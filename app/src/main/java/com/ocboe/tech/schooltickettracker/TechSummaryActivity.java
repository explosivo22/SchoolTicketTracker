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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.ocboe.tech.schooltickettracker.updater.AppUpdate;
import com.ocboe.tech.schooltickettracker.updater.AppUpdateUtil;
import com.ocboe.tech.schooltickettracker.updater.DownloadUpdateService;

public class TechSummaryActivity extends ActionBarActivity {

	private String[] mDrawerItems;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
    private CharSequence mTitle;
	public static Context mContext;
	public static View mView;


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

		boolean isLight = sharedPrefs.getBoolean("light_theme", false);

		if(isLight){
			setTheme(R.style.DarkTheme);
		}else{
			setTheme(R.style.LightTheme);
		}

		setContentView(R.layout.tech_summary);

		mTitle = mDrawerTitle = getTitle();
		mDrawerItems = getResources().getStringArray(R.array.drawer_list);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer opens
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDrawerItems));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		//getSupportActionBar().setDisplayUseLogoEnabled(true);

		// enable ActionBar app icon to behave as action to toggle nav drawer
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);

     // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }

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

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	/**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectItem(position);
	    	// update selected item and title, then close the drawer
	        //mDrawerList.setItemChecked(position, true);
	        //setTitle(mPlanetTitles[position]);
	        //mDrawerLayout.closeDrawer(mDrawerList);
	    }
	}

	private void selectItem(int position) {
        // update the main content by replacing fragments
		FragmentManager fm = getFragmentManager();

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

	        // update selected item and title, then close the drawer
	        mDrawerList.setItemChecked(position, true);
	        //setTitle(mPlanetTitles[position]);
	        mDrawerLayout.closeDrawer(mDrawerList);
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
			// update selected item and title, then close the drawer
	        mDrawerList.setItemChecked(position, true);
	        //setTitle(mPlanetTitles[position]);
	        mDrawerLayout.closeDrawer(mDrawerList);

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
			// update selected item and title, then close the drawer
	        mDrawerList.setItemChecked(position, true);
	        //setTitle(mPlanetTitles[position]);
	        mDrawerLayout.closeDrawer(mDrawerList);

			break;
		case 3:
			Fragment Settings = new SettingsFragment();

	        FragmentManager SettingsFragmentManager = getFragmentManager();
	        //fragmentManager.popBackStack("TechSummaryFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
	        SettingsFragmentManager.beginTransaction()
		        .addToBackStack(null)
		        .replace(R.id.summaryListContainer, Settings)
		        .commit();

	        // update selected item and title, then close the drawer
	        mDrawerList.setItemChecked(position, true);
	        //setTitle(mPlanetTitles[position]);
	        mDrawerLayout.closeDrawer(mDrawerList);
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
