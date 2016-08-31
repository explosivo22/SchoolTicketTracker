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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class InventoryRoomFragment extends Fragment{
	
	private static Spinner SchoolSpinner;
	private static Spinner RoomSpinner;
	private static Spinner UserSpinner;
	private boolean inhibit = true;
	private boolean inhibitUser = true;
	private boolean inhibitRoom = true;
	private ArrayList<String> roomArrayList = new ArrayList<String> ();
	private ArrayList<String> userArrayList = new ArrayList<String> ();
	protected static ArrayAdapter<String> roomAdapter, userAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.inventory_search, container, false);
		
		SchoolSpinner = (Spinner)view.findViewById(R.id.schoolSpinner);
		RoomSpinner = (Spinner)view.findViewById(R.id.roomSpinner);
		UserSpinner = (Spinner)view.findViewById(R.id.userSpinner);
		
		RoomSpinner.setVisibility(4);
		UserSpinner.setVisibility(4);
		
		SchoolSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				if(inhibit) {
					inhibit = false;
				}else{
					Properties.setPage("roomSearchFunctions.php");
					new roomUserSearch().execute(Properties.getURL(),SchoolSpinner.getSelectedItem().toString());
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parentView){
				//nothing
			}
		});
		
		RoomSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				if(inhibitUser) {
					inhibitUser = false;
				}else{
					Properties.setPage("roomSearchFunctions.php");
					new userSearch().execute(Properties.getURL(),SchoolSpinner.getSelectedItem().toString(),RoomSpinner.getSelectedItem().toString());
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parentView){
				//nothing
			}
		});
		
		UserSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				if (inhibitRoom) {
					inhibitRoom = false;
				} else {
					Properties.setPage("roomSearchFunctions.php");
					new roomSearch().execute(Properties.getURL(),SchoolSpinner.getSelectedItem().toString(),UserSpinner.getSelectedItem().toString());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				//nothing
			}
		});
		
		//getActivity().getActionBar().setTitle("Inventory Search");
		((TechSummaryActivity) getActivity()).setActionBarTitle("Inventory Search");
		
		setHasOptionsMenu(true);
		
		return view;
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
	
	protected class roomUserSearch extends AsyncTask<String, Integer, Toast> {
		
		protected void onPreExecute(){
			super.onPreExecute();
			
			userArrayList.clear();
			roomArrayList.clear();
		}
		
		protected Toast doInBackground(String... vars){
			
			InputStream is = null;
			String result = "";
			// the year data to send
			//ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			// the values to send
			//nameValuePairs.add(new BasicNameValuePair("school", SchoolSpinner.getSelectedItem().toString()));

			// http post
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
			} catch (Exception e) {
				//Log.d(TAG, "Error converting result " + e.toString());
			}
			// parse json data
			try {
				JSONArray jArray = new JSONArray(result);
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);
					//Log.i("log_tag", "room: " + json_data.getInt("userRoom_room")+ ", user: " + json_data.getString("userRoom_user"));
					userArrayList.add(json_data.getString("userRoom_username"));
					roomArrayList.add(json_data.getString("userRoom_room"));
				}

			} catch (JSONException e) {
				//Log.d(TAG, "Error parsing data " + e.toString());
				//Toast.makeText(getActivity().getApplicationContext(), "dispose unsuccessful",Toast.LENGTH_SHORT).show();
			}
			
			return null;
		}
		
		protected void onPostExecute(Toast result){
			
			//myInstance.dismiss();
			//Toast.makeText(getActivity().getApplicationContext(), UpdateResults, Toast.LENGTH_LONG).show();
			
			RoomSpinner.setVisibility(0);
			UserSpinner.setVisibility(0);
			inhibitUser = true;
			inhibitRoom = true;
			
			roomAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, roomArrayList);
			userAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, userArrayList);
			
			roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			RoomSpinner.setAdapter(roomAdapter);
			UserSpinner.setAdapter(userAdapter);
			
			roomAdapter.notifyDataSetChanged();
			userAdapter.notifyDataSetChanged();
			
		}
	}
	
	protected class userSearch extends AsyncTask<String, Integer, Toast> {
		
		protected void onPreExecute(){
			
			super.onPreExecute();
			
			userArrayList.clear();
		}
		
		protected Toast doInBackground(String... vars){
			
			InputStream is = null;
			String result = "";
			// the year data to send
			//ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			// the values to send
			//nameValuePairs.add(new BasicNameValuePair("school", SchoolSpinner.getSelectedItem().toString()));
			//nameValuePairs.add(new BasicNameValuePair("room", RoomSpinner.getSelectedItem().toString()));

			// http post
			try{

				//final String USER_AGENT = "Mozilla/5.0";
				URL url = new URL(vars[0]);
				//Log.d(TAG, url.toString());
				String urlParameters = "school=" + vars[1] + "&room=" + vars[2];
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
			} catch (Exception e) {
				//Log.d(TAG, "Error converting result " + e.toString());
			}
			// parse json data
			try {
				JSONArray jArray = new JSONArray(result);
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);
					//Log.i("log_tag", "room: " + json_data.getInt("userRoom_room")+ ", user: " + json_data.getString("userRoom_user"));
					userArrayList.add(json_data.getString("userRoom_username"));
				}

			} catch (JSONException e) {
				//Log.d(TAG, "Error parsing data " + e.toString());
				//Toast.makeText(getActivity().getApplicationContext(), "dispose unsuccessful",Toast.LENGTH_SHORT).show();
			}
			
			return null;
		}
		
		protected void onPostExecute(Toast result){
			
			//myInstance.dismiss();
			//Toast.makeText(getActivity().getApplicationContext(), UpdateResults, Toast.LENGTH_LONG).show();
			
			//RoomSpinner.setVisibility(0);
			//UserSpinner.setVisibility(0);
			inhibitUser = true;
			inhibitRoom = true;
			
			//roomAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, roomArrayList);
			userAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, userArrayList);
			
			//roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			//RoomSpinner.setAdapter(roomAdapter);
			UserSpinner.setAdapter(userAdapter);
			
			//roomAdapter.notifyDataSetChanged();
			userAdapter.notifyDataSetChanged();
			
		}
	}
	
	protected class roomSearch extends AsyncTask<String, Integer, Toast> {
		
		protected void onPreExecute(){
			
			super.onPreExecute();
			
			roomArrayList.clear();
		}
		
		protected Toast doInBackground(String... vars){
			
			InputStream is = null;
			String result = "";
			// the year data to send
			//ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			// the values to send
			//nameValuePairs.add(new BasicNameValuePair("school", SchoolSpinner.getSelectedItem().toString()));
			//nameValuePairs.add(new BasicNameValuePair("user", UserSpinner.getSelectedItem().toString()));

			// http post
			try{

				//final String USER_AGENT = "Mozilla/5.0";
				URL url = new URL(vars[0]);
				//Log.d(TAG, url.toString());
				String urlParameters = "school=" + vars[1] + "&user=" + vars[2];
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
			} catch (Exception e) {
				//Log.d(TAG, "Error converting result " + e.toString());
			}
			// parse json data
			try {
				JSONArray jArray = new JSONArray(result);
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);
					//Log.i("log_tag", "room: " + json_data.getInt("userRoom_room")+ ", user: " + json_data.getString("userRoom_user"));
					roomArrayList.add(json_data.getString("userRoom_room"));
				}

			} catch (JSONException e) {
				//Log.d(TAG, "Error parsing data " + e.toString());
				//Toast.makeText(getActivity().getApplicationContext(), "dispose unsuccessful",Toast.LENGTH_SHORT).show();
			}
			
			return null;
		}
		
		protected void onPostExecute(Toast result){
			
			//myInstance.dismiss();
			//Toast.makeText(getActivity().getApplicationContext(), UpdateResults, Toast.LENGTH_LONG).show();
			
			//RoomSpinner.setVisibility(0);
			//UserSpinner.setVisibility(0);
			inhibitRoom = true;
			inhibitUser = true;
			
			roomAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, roomArrayList);
			//userAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, userArrayList);
			
			roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			//userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			RoomSpinner.setAdapter(roomAdapter);
			//UserSpinner.setAdapter(userAdapter);
			
			roomAdapter.notifyDataSetChanged();
			//userAdapter.notifyDataSetChanged();
			
		}
	}
	
	
}
