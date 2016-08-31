package com.ocboe.tech.schooltickettracker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ocboe.tech.schooltickettracker.UpdateTicketFragment.MySpinnerDialog;
import com.ocboe.tech.schooltickettracker.updater.AppUpdate;
import com.ocboe.tech.schooltickettracker.updater.AppUpdateUtil;
import com.ocboe.tech.schooltickettracker.updater.DownloadUpdateService;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends Fragment{
	
	private JSONObject json_data;
	private String session_info[];
	private EditText username, password;
	private CheckBox remember_me;
	private TextView DisplayIP;
	private boolean LoginState = false;
	private MySpinnerDialog myInstance;
	private String TAG = "SCHOOL_TICKET";
	private String msg = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		
		View view = inflater.inflate(R.layout.login, container, false);

		AppCompatActivity activity = (AppCompatActivity) getActivity();
		activity.getSupportActionBar().setTitle("Login");
		
		username = (EditText) view.findViewById(R.id.username);
		password = (EditText) view.findViewById(R.id.password);

		remember_me = (CheckBox) view.findViewById(R.id.rememberMeCheck);

		DisplayIP = (TextView) view.findViewById(R.id.ipText);

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
		
		Button loginButton = (Button) view.findViewById(R.id.loginButton);
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
		
		setHasOptionsMenu(true);

		Handler toastHandler = new Handler();
		Runnable toastRunnable = new Runnable() {public void run() {Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG).show();}};

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		MenuItem Settings = menu.add(0,1,0,R.string.settings_menu_title);
	    Settings.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	    SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());

		boolean isLight = sharedPrefs.getBoolean("light_theme", false);

		if(isLight){
			Settings.setIcon(R.drawable.ic_settings_white_48dp);
		}else{
			Settings.setIcon(R.drawable.ic_settings_black_48dp);
		}
	    super.onCreateOptionsMenu(menu,inflater);
	}
	
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
	public boolean onOptionsItemSelected(MenuItem item){
		Log.d("SCHOOL_TICKET", item.toString());
		switch(item.getItemId()){
		case 1:
			//change fragment to SettingsFragment
			Log.d("SCHOOL_TICKET", item.toString());
			getFragmentManager().beginTransaction()
				.replace(R.id.loginContainer, new SettingsFragment())
				.addToBackStack(null)
				.commit();
			return true;
			//break;
		default:
			Log.d("SCHOOL_TICKET", item.toString());
			return super.onOptionsItemSelected(item);
			//break;
		}

		//return false;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		//getActivity().getActionBar().setTitle("Login");
		AppCompatActivity activity = (AppCompatActivity) getActivity();
		activity.getSupportActionBar().setTitle("Login");
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
		        		StoreSession(json_data.getString("session_username"), json_data.getString("session_name"), json_data.getString("session_techLevel"), json_data.getString("session_maintLevel"), json_data.getString("session_reqLevel"), json_data.getString("session_school"), json_data.getString("session_room"));
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
				Intent TechSummary = new Intent(getActivity().getApplicationContext(), TechSummaryActivity.class);
				startActivity(TechSummary);
			} else {
				myInstance.dismiss();
				Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		}
	}

	private boolean CheckWifi(){
		WifiManager wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		if(wifiInfo.getSSID()=="OCS") {
			return true;
		}else{
			return false;
		}
	}
	
	private void RememberMe() {
		getActivity().getApplicationContext().getSharedPreferences("schooltickettracker", Context.MODE_PRIVATE)
        .edit()
        .putBoolean("remember_me", remember_me.isChecked())
        .putString("username", username.getText().toString())
        .putString("password", password.getText().toString())
        .commit();

	}
	
	private void DeleteSaved(){
		SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences("schooltickettracker", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove("password");
		editor.putBoolean("remember_me", remember_me.isChecked());
		editor.commit();
	}
	
	private void GetSaved() {
		SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("schooltickettracker", Context.MODE_PRIVATE);
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
	
	private void StoreSession(String username, String name, String techLevel, String maintLevel, String reqLevel, String school, String room){
		Properties.setUsername(username);
		Properties.setName(name);
		Properties.setTechLevel(techLevel);
		Properties.setMaintLevel(maintLevel);
		Properties.setReqLevel(reqLevel);
		Properties.setSchool(school);
		Properties.setRoom(room);
	}

}
