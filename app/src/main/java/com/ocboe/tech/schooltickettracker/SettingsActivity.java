package com.ocboe.tech.schooltickettracker;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsFragmentCompat;
import com.mikepenz.aboutlibraries.ui.LibsFragment;
import com.ocboe.tech.schooltickettracker.updater.AppUpdate;
import com.ocboe.tech.schooltickettracker.updater.AppUpdateUtil;

/**
 * Created by Brad on 9/3/2016.
 */
public class SettingsActivity extends AppCompatActivity {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar tb = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
        getSupportActionBar().setTitle(getResources().getString(R.string.settings_page_title));
        mContext = SettingsActivity.this;
        LocalBroadcastManager.getInstance(this).registerReceiver(showUpdateDialog, new IntentFilter(MainActivity.ACTION_SHOW_UPDATE_DIALOG));
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(showUpdateDialog);
        super.onDestroy();
    }

    private final BroadcastReceiver showUpdateDialog = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AppUpdate update = intent.getParcelableExtra("update");
            if (update.getStatus() == AppUpdate.UPDATE_AVAILABLE) {
                AlertDialog updateDialog = AppUpdateUtil.getAppUpdateDialog(mContext, update);
                updateDialog.show();
            } else if (update.getStatus() == AppUpdate.UP_TO_DATE)
                Snackbar.make(getWindow().getDecorView().getRootView(),
                        getResources().getString(R.string.up_to_date), Snackbar.LENGTH_SHORT).show();
            else
                Toast.makeText(mContext, getResources().getString(R.string.update_check_failed), Toast.LENGTH_SHORT)
                        .show();
        }
    };

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesName(SchoolTicketTrackerSettings.PREFS_SCHOOL_TICKET_TRACKER_SETTINGS);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            PreferenceScreen preferenceScreen = getPreferenceScreen();

            if (BuildConfig.isInternetAvailable) {
                Preference checkForUpdatePreference = getPreferenceManager().findPreference("checkForUpdate");
                checkForUpdatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (!MainActivity.isSchoolTicketTrackerBeingUpdated(getActivity())) {
                            Snackbar.make(getView(), getResources().getString(R.string.checking_for_update), Snackbar.LENGTH_SHORT).show();
                            MainActivity.shouldShowUpdateDialog = false;
                            AppUpdateUtil.checkForUpdate(getActivity());
                        } else
                            Snackbar.make(getView(), getResources().getString(R.string.ongoing_update), Snackbar.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }
        }
    }
}
