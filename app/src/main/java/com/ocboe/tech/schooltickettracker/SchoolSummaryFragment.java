package com.ocboe.tech.schooltickettracker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SchoolSummaryFragment extends ListFragment {
	
	private String school;
	
	private static final String TAG = "School Tech";
	
	private ArrayList<String> results = new ArrayList<String> ();
	private ArrayAdapter<String> adapter;
	
	private Fragment mFragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Bundle bundle = this.getArguments();
		school = bundle.getString("school");
		((TechSummaryActivity) getActivity()).setActionBarTitle(school);
		
		Properties.setPage("viewtechSchoolandroid.php");
		new getSchoolSummary().execute(Properties.getURL(),school);
		
		setHasOptionsMenu(true);
		
		return super.onCreateView(inflater, container, savedInstanceState);
		
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	
    	super.onListItemClick(l, v, position, id);
    	
    	mFragment = new UpdateTicketFragment();
		Bundle mBundle = new Bundle();
		
		String ticketID = (String)l.getItemAtPosition(position);
		
		int start = ticketID.lastIndexOf("Ticket #:",0);
    	//Toast.makeText(getApplicationContext(), start, Toast.LENGTH_SHORT);
    	int last = ticketID.indexOf("user",0);
    	String endid = ticketID.substring(start+10, last-1);
		
		mBundle.putString("id", endid);
		//Log.d("School Tech", school);
		mFragment.setArguments(mBundle);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		ft.replace(R.id.summaryListContainer, mFragment);
		ft.addToBackStack(null);
		ft.commit();
 
    }
	
	@Override
	public void onResume() {
		
		super.onResume();
		
		//Properties.setPage("viewtechSchoolandroid.php");
		//new getSchoolSummary().execute(Properties.getURL());
		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		getActivity().getMenuInflater().inflate(R.menu.login_menu, menu);
		super.onCreateOptionsMenu(menu,inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case 1:
			//change fragment to SettingsFragment
			getFragmentManager().beginTransaction()
				.replace(R.id.summaryListContainer, new SettingsFragment())
				.addToBackStack("Settings")
				.commit();
			break;
		default:
			break;
		}
		
		return false;
	}
	
	private class getSchoolSummary extends AsyncTask<String, Integer, Toast> {
		
		protected void onPreExecute() {
			
			super.onPreExecute();
			
			results.clear();
			
		}
		
		protected Toast doInBackground(String... vars) {
			
			InputStream is = null;
		     String result = "";
		     //the year data to send
		     //ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		     //the values to send
		     //nameValuePairs.add(new BasicNameValuePair("school", school));


		     //http post
			try{

				//final String USER_AGENT = "Mozilla/5.0";
				URL url = new URL(vars[0]);
				//Log.d(TAG, url.toString());
				String urlParameters = "school=" + vars[1];
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
		         Log.d(TAG, "Error converting result "+e.toString());
		     }
		     //parse json data
		     try{
		         JSONArray jArray = new JSONArray(result);
		         for(int i=0;i<jArray.length();i++){
		         	JSONObject json_data = jArray.getJSONObject(i);
		         	 Log.d(TAG,"id: " + json_data.getInt("id") + ", daysold: " + json_data.getString("daysold") + ", user: " + json_data.getString("user") + ", room" + json_data.getString("room") + ", type" + json_data.getString("type") + ", info: " + json_data.getString("info"));
		                 
		                 //myVec.add(i,json_data.getString("school")+" - "+json_data.getInt("orders"));

		         	 //String infotitle = String.format(getString(R.string.info));
		         	 if(json_data.getInt("id") != 0){
		         	String test = "Ticket #: " + json_data.getString("id") + "\nuser: " + json_data.getString("user") + "\nroom: " + json_data.getString("room") + "\ntype: " + json_data.getString("type") + "\nDays Old: " + json_data.getString("daysold") + "\n\n" + "info:" + "\n" + json_data.getString("info");
		         	results.add(test);
		         	 }
		         	 else{
		         		 String test = "There are no tickets here";
		         		 results.add(test);
		         	 }
		         	 //myVec.add(i,"Ticket #: " + json_data.getString("id") + "\nuser: " + json_data.getString("user") + "\nroom: " + json_data.getString("room") + "\ntype: " + json_data.getString("type") + "\n\n" + styledText.toString() + "\n" + json_data.getString("info"));
		                 //Log.d(TAG,songs[i]);
		                 
		                // Toast.makeText(getApplicationContext(), "The Message Was " + json_data.getString("song_name"), Toast.LENGTH_SHORT).show();
		         }

		     }catch(JSONException e){
		         Log.d(TAG, "Error parsing data "+e.toString());
		         Toast.makeText(getActivity().getApplicationContext(), "Updating Tech Summary Failed", Toast.LENGTH_SHORT).show();
		     }
			
			return null;
		}
		
		protected void onPostExecute(Toast result) {
			
			adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, results);
			
			setListAdapter(adapter);
			
			adapter.notifyDataSetChanged();
			
		}
	}

}
