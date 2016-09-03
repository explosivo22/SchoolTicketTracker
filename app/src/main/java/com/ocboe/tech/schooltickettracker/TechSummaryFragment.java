package com.ocboe.tech.schooltickettracker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TechSummaryFragment  extends ListFragment {
	
	private ArrayList<String> results = new ArrayList<> ();
	private ArrayAdapter<String> adapter;
	
	private int totalOrders = 0;
	private int BOorders, CTCorders, HCorders, LRorders, OCorders, RMorders, SFEorders, SFHorders, OCSorders;
	private String[] techresults;
	
	private Fragment mFragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		
		//Properties.setIPAddress("96.4.160.69");
		//Properties.setPort(4001);
		
		Properties.setPage("viewtechSummaryandroid.php");
		new getTicketSummary().execute(Properties.getURL());
		
		setHasOptionsMenu(true);
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		Properties.setPage("viewtechSummaryandroid.php");
		
		((TechSummaryActivity) getActivity()).setActionBarTitle("Total Orders: " + totalOrders);
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
		case R.id.menu_settings:
			//change fragment to SettingsFragment
			getFragmentManager().beginTransaction()
				.replace(R.id.summaryListContainer, new SettingsFragment())
				.addToBackStack(null)
				.commit();
			break;
		default:
			break;
		}
		
		return false;
	}
	
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	
    	super.onListItemClick(l, v, position, id);
    	
    	mFragment = new SchoolSummaryFragment();
		Bundle mBundle = new Bundle();
		String school = (String)l.getItemAtPosition(position);
		school = school.substring(0,school.indexOf("-")-1);
		mBundle.putString("school", school);
		mFragment.setArguments(mBundle);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		ft.replace(R.id.summaryListContainer, mFragment);
		ft.addToBackStack("schoolsummaryfragment");
		ft.commit();
 
    }
	
	private class getTicketSummary extends AsyncTask<String, Integer, Toast> {
		
		protected void onPreExecute() {
			
			super.onPreExecute();
			results.clear();
			
		}
		
		protected Toast doInBackground(String... vars) {

	        String result = "";
	        totalOrders = 0;
	        techresults = null;
	        //the year data to send
	        //ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	        //the values to send
	        //nameValuePairs.add(new BasicNameValuePair("", ""));


	        //http post
			try{

				//final String USER_AGENT = "Mozilla/5.0";
				URL url = new URL(vars[0]);
				//Log.d(TAG, url.toString());
				//String urlParameters = "username=" + vars[1] + "&password=" + vars[2];
				//byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
				//int postDataLength = postData.length;
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestMethod("POST");
				//conn.setRequestProperty("User-Agent", USER_AGENT);
				conn.setRequestProperty("Accept-Language", "UTF-8");

				//conn.setDoOutput(true);
				//OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
				//outputStreamWriter.write(urlParameters);
				//outputStreamWriter.flush();

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
				e.printStackTrace();
	        }
	        //parse json data
	        try{
	            JSONArray jArray = new JSONArray(result);
	            for(int i=0;i<jArray.length();i++){
	            	JSONObject json_data = jArray.getJSONObject(i);
	            	 //Log.d(TAG,"school: " + json_data.getString("school") + ", orders: " + json_data.getInt("orders"));

	            	if(json_data.getInt("orders") == 0){
	            		String test = "There are no tickets at this time";
	            		results.add(test);
	            	}else{

	            	 if(json_data.getString("school").equals("Black Oak Elementary")){
	            		 BOorders = json_data.getInt("orders");
	            	 } else if(json_data.getString("school").equals("Career Technology Center")){
	            		 CTCorders = json_data.getInt("orders");
	            	 } else if(json_data.getString("school").equals("Hillcrest Elementary")){
	            		 HCorders = json_data.getInt("orders");
	            	 } else if(json_data.getString("school").equals("Lake Road Elementary")){
	            		 LRorders = json_data.getInt("orders");
	            	 } else if(json_data.getString("school").equals("Obion County Central High")){
	            		 OCorders = json_data.getInt("orders");
	            	 } else if(json_data.getString("school").equals("Ridgemont Elementary")){
	            		 RMorders = json_data.getInt("orders");
	            	 } else if(json_data.getString("school").equals("South Fulton Elementary")){
	            		 SFEorders = json_data.getInt("orders");
	            	 } else if(json_data.getString("school").equals("South Fulton Middle/High")){
	            		 SFHorders = json_data.getInt("orders");
	            	 } else if(json_data.getString("school").equals("Obion County Schools")){
	            		 OCSorders = json_data.getInt("orders");
	            	 }

	            	 String test = json_data.getString("school")+" - " + json_data.getInt("orders");
	            	 results.add(test);
	            	 totalOrders += json_data.getInt("orders");
	            	 //publishProgress(totalOrders);
	                    //Log.d(TAG,songs[i]);

	                   // Toast.makeText(getApplicationContext(), "The Message Was " + json_data.getString("song_name"), Toast.LENGTH_SHORT).show();
	            	}
	            }

	        }catch(JSONException e){
	            Toast.makeText(getActivity().getApplicationContext(), "Updating Tech Summary Failed", Toast.LENGTH_SHORT).show();
	        }
	        /*for(int k = 0; k < totalOrders; k++){
	        	//Log.d(TAG, "k = " + Integer.toString(k));
	        	publishProgress((int) ((k / (float) totalOrders) * 100));
	        }*/
			
			return null;
		}
		
		protected void onProgressUpdate(Integer... progress) {
			
			//setProgress(progress[0]);
			
		}
		
		protected void onPostExecute(Toast result) {

			adapter = new ArrayAdapter<> (getActivity(), android.R.layout.simple_list_item_1, results);

			setListAdapter(adapter);

			((TechSummaryActivity) getActivity()).setActionBarTitle("Total Orders: " + totalOrders);

			adapter.notifyDataSetChanged();
			
			//actionBar.setTitle("Total Orders: " + totalOrders);
			
		}
		
		
	}

}
