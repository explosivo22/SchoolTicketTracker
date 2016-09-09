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
import android.media.audiofx.BassBoost;
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

import com.mikepenz.aboutlibraries.LibsBuilder;
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
						if(fm.findFragmentByTag("techsummaryfragment") == null) {
							Fragment fragment = new TechSummaryFragment();
							fm.beginTransaction()
									.replace(R.id.summaryListContainer, fragment, "techsummaryfragment")
									.addToBackStack(null)
									.commit();
						}
						return true;

					// For rest of the options we just show a toast on click

					case R.id.dispose:
						Intent DisposeActivity = new Intent(mContext, DisposeActivity.class);
						DisposeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(DisposeActivity);
						finish();
						return true;

					case R.id.inventory:
						Intent InventoryActivity = new Intent(mContext, InventoryActivity.class);
						InventoryActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(InventoryActivity);
						finish();
						return true;
					case R.id.settings:
						Intent SettingsActivity = new Intent(mContext, SettingsActivity.class);
						startActivity(SettingsActivity);
						return true;
					case R.id.reqStatus:
						Intent ReqStatusActivity = new Intent(mContext, ReqStatusActivity.class);
						startActivity(ReqStatusActivity);
						return true;
					case R.id.openSource:
						new LibsBuilder()
								.withFields(R.string.class.getFields())
								.withAutoDetect(true)
								.withVersionShown(false)
								.withLicenseShown(false)
								.withActivityTheme(R.style.AppTheme)
								.withActivityTitle(getString(R.string.open_source_setting_title))
								.withAboutIconShown(true)
								.withAboutVersionShown(true)
								.withAboutDescription(getString(R.string.app_license_description))
								.start(TechSummaryActivity.this);
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
				.addToBackStack(null)
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
			if (techFrag != null && techFrag.isVisible()) { //If we are in fragment A when we press the back button, finish is called to exit
				finish();
			} else {
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

	public void setActionBarTitle(String title) {
		getSupportActionBar().setTitle(title);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
