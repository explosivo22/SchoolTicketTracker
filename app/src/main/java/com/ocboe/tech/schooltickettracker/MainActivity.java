package com.ocboe.tech.schooltickettracker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ocboe.tech.schooltickettracker.updater.AppUpdate;
import com.ocboe.tech.schooltickettracker.updater.AppUpdateUtil;
import com.ocboe.tech.schooltickettracker.updater.DownloadUpdateService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by Brad on 8/31/2016.
 */
public class MainActivity extends AppCompatActivity {

    private JSONObject json_data;
    private String session_info[];
    private EditText username, password;
    private CheckBox remember_me;
    private TextView DisplayIP;
    private boolean LoginState = false;
    private MySpinnerDialog myInstance;
    private String TAG = "SCHOOL_TICKET";
    private String msg = null;
    public static Context mContext;
    private static WifiInfo wifiInfo;
    private static WifiManager wifiManager;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar tb;

    public static final String ACTION_SHOW_UPDATE_DIALOG = "show-update-dialog";
    public static boolean shouldShowUpdateDialog;

    public static Intent createUpdateDialogIntent(AppUpdate update) {
        Intent updateIntent = new Intent(MainActivity.ACTION_SHOW_UPDATE_DIALOG);
        updateIntent.putExtra("update", update);
        return updateIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolBar();
        getSupportActionBar().setTitle("Login");

        navigationView = (NavigationView) findViewById(R.id.navigation);

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


        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        remember_me = (CheckBox) findViewById(R.id.rememberMeCheck);

        DisplayIP = (TextView) findViewById(R.id.ipText);

        mContext = MainActivity.this;
        shouldShowUpdateDialog = true;

        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        if (BuildConfig.isInternetAvailable && sharedPrefs.getBoolean("autoUpdateEnabled", true))
            AppUpdateUtil.checkForUpdate(mContext);

        //Retrieve any saved values set by the remember me option
        GetSaved();

        remember_me.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    RememberMe();
                }else {
                    DeleteSaved();
                }

            }
        });

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                myInstance  = new MySpinnerDialog();
                Bundle args = new Bundle();
                args.putString("title", "logging in...");
                myInstance.setArguments(args);
                myInstance.show(fm, "update_dialog");
                new Login().execute(Properties.getURL(),username.getText().toString(),password.getText().toString());

            }
        });

        if(CheckWifi()){
            Properties.setIPAddress("10.99.0.26");
            DisplayIP.setText("IP: Internal");
        }else {
            Properties.setIPAddress("96.4.160.69");
            DisplayIP.setText("IP: External");
        }

        Properties.setPort(4001);

        Properties.setPage("logonandroid.php");

        //setHasOptionsMenu(true);
        LocalBroadcastManager.getInstance(this).registerReceiver(showUpdateDialog, new IntentFilter(ACTION_SHOW_UPDATE_DIALOG));
    }
    private void setToolBar() {
        tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.icon);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }*/

    @SuppressLint("ValidFragment")
    public class MySpinnerDialog extends DialogFragment {

        public MySpinnerDialog() {
            // use empty constructors. If something is needed use onCreate's
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {

            ProgressDialog _dialog = new ProgressDialog(getActivity());
            this.setStyle(STYLE_NO_TITLE, getTheme()); // You can use styles or inflate a view
            _dialog.setMessage(getArguments().getString("title")); // set your messages if not inflated from XML
            _dialog.setIndeterminate(true);

            _dialog.setCancelable(false);

            return _dialog;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //getActivity().getActionBar().setTitle("Login");
        //AppCompatActivity activity = (AppCompatActivity) getActivity();
        wifiInfo = wifiManager.getConnectionInfo();
        getSupportActionBar().setTitle("Login");
        Properties.setPage("logonandroid.php");
    }

    private class Login extends AsyncTask<String, Integer, Toast> {

        protected void onPreExecute(){
            super.onPreExecute();

            session_info = null;
        }

        protected Toast doInBackground(String... vars){

            InputStream is = null;
            String result = "";

            //http post
            try{

                //final String USER_AGENT = "Mozilla/5.0";
                URL url = new URL(vars[0]);
                //Log.d(TAG, url.toString());
                String urlParameters = "username=" + vars[1] + "&password=" + vars[2];
                byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
                int postDataLength = postData.length;
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                //conn.setRequestProperty("User-Agent", USER_AGENT);
                conn.setRequestProperty("Accept-Language", "UTF-8");

                conn.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
                outputStreamWriter.write(urlParameters);
                outputStreamWriter.flush();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println(response.toString());
                result = response.toString();
            }catch(Exception e){
                // Log.d(TAG, "Error in http connection "+e.toString());
            }
            //parse json data
            try{
                JSONArray jArray = new JSONArray(result);
                for(int i=0;i<jArray.length();i++){
                    json_data = jArray.getJSONObject(i);
                    //Log.i("log_tag","id: " + json_data.getInt("id") + ", response: " + json_data.getString("response") + ", session_username: " + json_data.getString("session_username") + ", session_name: " + json_data.getString("session_name") + ", session_techLevel: " + json_data.getString("session_techLevel") + ", session_maintLevel: " + json_data.getString("session_maintLevel") + ", session_reqLevel: " + json_data.getString("session_reqLevel") + ", session_school: " + json_data.getString("session_school") + ", session_room: " + json_data.getString("session_room"));
                    switch(json_data.getInt("id")){
                        case 0:
                            //successful login
                            StoreSession(json_data.getString("session_username"), json_data.getString("session_name"), json_data.getString("session_email"), json_data.getString("session_techLevel"), json_data.getString("session_maintLevel"), json_data.getString("session_reqLevel"), json_data.getString("session_school"), json_data.getString("session_room"));
                            LoginState = true;
                            break;
                        case 1:
                            LoginState = false;
                            msg = json_data.getString("response");
                            break;
                        default:
                            LoginState = false;
                            break;
                    }
                }

            }catch(JSONException e){
                Log.d(TAG, "Error parsing data "+e.toString());
                //msg = "Login unsuccessful";
                //Toast.makeText(getActivity().getApplicationContext(), "Login unsuccessful", Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                Log.d(TAG, "Caught Error "+e.toString());
            }

            return null;
        }

        protected void onPostExecute(Toast result){
            if(LoginState){
				/*TechSummaryFragment mFragment = new TechSummaryFragment();
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();

				ft.replace(R.id.summaryListContainer, mFragment);
				ft.addToBackStack("Login");
				ft.commit();*/
                //Log.d(TAG, "onPostExecute");
                myInstance.dismiss();
                Intent TechSummary = new Intent(mContext, TechSummaryActivity.class);
                startActivity(TechSummary);
            } else {
                myInstance.dismiss();
                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean CheckWifi(){
        wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();

        if(wifiInfo.getSSID()=="OCS") {
            Log.d(TAG, "CheckWifi: Connected to Internal Network");
            return true;
        }else{
            Log.d(TAG, "CheckWifi: Connected to External Network");
            return false;
        }
    }

    private void RememberMe() {
        this.getSharedPreferences("schooltickettracker", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("remember_me", remember_me.isChecked())
                .putString("username", username.getText().toString())
                .putString("password", password.getText().toString())
                .commit();

    }

    private void DeleteSaved(){
        SharedPreferences preferences = this.getSharedPreferences("schooltickettracker", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("password");
        editor.putBoolean("remember_me", remember_me.isChecked());
        editor.commit();
    }

    private void GetSaved() {
        SharedPreferences pref = this.getSharedPreferences("schooltickettracker", Context.MODE_PRIVATE);
        boolean saved = pref.getBoolean("remember_me", false);
        String user = pref.getString("username", null);
        String passw = pref.getString("password", null);

        if (!saved) {
            //Prompt for username and password
            username.setText("");
            password.setText("");
            remember_me.setChecked(false);
        }
        else {
            //Set The TextBoxes to the values saved
            username.setText(user);
            password.setText(passw);
            remember_me.setChecked(saved);
        }
    }

    private void StoreSession(String username, String name, String email, String techLevel, String maintLevel, String reqLevel, String school, String room){
        Properties.setUsername(username);
        Properties.setName(name);
        Properties.setEmail(email);
        Properties.setTechLevel(techLevel);
        Properties.setMaintLevel(maintLevel);
        Properties.setReqLevel(reqLevel);
        Properties.setSchool(school);
        Properties.setRoom(room);
    }

    private final BroadcastReceiver showUpdateDialog = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AppUpdate update = intent.getParcelableExtra("update");
            if (update.getStatus() == AppUpdate.UPDATE_AVAILABLE && shouldShowUpdateDialog && !isSchoolTicketTrackerBeingUpdated(context)) {
                //Log.d(TAG, "onReceive: update status:" + update.getStatus());
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
