package com.ocboe.tech.schooltickettracker;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ocboe.tech.schooltickettracker.updater.AppUpdate;
import com.ocboe.tech.schooltickettracker.updater.AppUpdateUtil;
import com.ocboe.tech.schooltickettracker.updater.DownloadUpdateService;

public class LoginActivity extends AppCompatActivity {

	public static Context mContext;

	public static final String ACTION_SHOW_UPDATE_DIALOG = "show-update-dialog";
	public static boolean shouldShowUpdateDialog;

	public static Intent createUpdateDialogIntent(AppUpdate update) {
		Intent updateIntent = new Intent(LoginActivity.ACTION_SHOW_UPDATE_DIALOG);
		updateIntent.putExtra("update", update);
		return updateIntent;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);

		mContext = this;
		shouldShowUpdateDialog = true;

		SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

		if (BuildConfig.isInternetAvailable && sharedPrefs.getBoolean("autoUpdateEnabled", true))
			AppUpdateUtil.checkForUpdate(mContext);
 
		boolean isLight = sharedPrefs.getBoolean("light_theme", false);
		
		changeTheme(isLight);
	
		setContentView(R.layout.login_container);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);
		
		Fragment fragment = new LoginFragment();
		
        FragmentManager fragmentManager = getFragmentManager();
        //fragmentManager.popBackStack("TechSummaryFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction().replace(R.id.loginContainer, fragment).commit();

		LocalBroadcastManager.getInstance(this).registerReceiver(showUpdateDialog, new IntentFilter(ACTION_SHOW_UPDATE_DIALOG));
		
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
 
		boolean isLight = sharedPrefs.getBoolean("light_theme", false);
		
		changeTheme(isLight);
	}
	
	// somewhere in the activity
	private void changeTheme(boolean isLight) {
	    setTheme(isLight ? R.style.DarkTheme : R.style.LightTheme);
	}

	private final BroadcastReceiver showUpdateDialog = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			AppUpdate update = intent.getParcelableExtra("update");
			if (update.getStatus() == AppUpdate.UPDATE_AVAILABLE && shouldShowUpdateDialog && !isSchoolTicketTrackerBeingUpdated(context)) {
				AlertDialog updateDialog = AppUpdateUtil.getAppUpdateDialog(mContext, update);
				updateDialog.show();
			}
			if (!shouldShowUpdateDialog)
				shouldShowUpdateDialog = true;
		}
	};

	public static boolean isSchoolTicketTrackerBeingUpdated(Context context) {

		DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		DownloadManager.Query q = new DownloadManager.Query();
		q.setFilterByStatus(DownloadManager.STATUS_RUNNING);
		Cursor c = downloadManager.query(q);
		if (c.moveToFirst()) {
			String fileName = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
			if (fileName.equals(DownloadUpdateService.DOWNLOAD_UPDATE_TITLE))
				return true;
		}
		return false;
	}

	@Override
	public void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(showUpdateDialog);
		super.onDestroy();
	}

}
