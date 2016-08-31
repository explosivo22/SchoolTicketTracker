package com.ocboe.tech.schooltickettracker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateTicketFragment extends Fragment{
	
	private Vector<String> info = new Vector<String>();
	private TextView ticketInfo;
	private String id;
	private ProgressBar progressBar;
	private MySpinnerDialog myInstance;
	private String TicketStatus;
	private EditText UpdatedInfo;
	private String UpdateResults;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.update_ticket, container, false);
		
		progressBar = (ProgressBar) view.findViewById(R.id.loadingInfo);
		progressBar.setVisibility(View.VISIBLE);
		
		Bundle bundle = this.getArguments();
		id = bundle.getString("id");
		((TechSummaryActivity) getActivity()).setActionBarTitle("Updating Order #" + id);
		
		ticketInfo = (TextView) view.findViewById(R.id.ticketInfo);
		UpdatedInfo = (EditText)view.findViewById(R.id.updateInfo);
		
		Button updateButton = (Button) view.findViewById(R.id.updateButton);
		updateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				FragmentManager fm = getFragmentManager();
				myInstance  = new MySpinnerDialog();
				Bundle args = new Bundle();
				args.putString("title", "Updating...");
				myInstance.setArguments(args);
				myInstance.show(fm, "update_dialog");
				Properties.setPage("updateTechOrderandroid.php");
				new UpdateOrder().execute(Properties.getURL(),Properties.getUsername(),id,UpdatedInfo.getText().toString(),TicketStatus);
				
			}
		});
		
		Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				getActivity().getFragmentManager().popBackStack();
				
			}
		});
		
		Spinner status = (Spinner)view.findViewById(R.id.ticketStatus);
		status.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                
            	TicketStatus = parentView.getItemAtPosition(position).toString();
            	
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
		
		//On first load, get the information for the ticket the user just requested
		Properties.setPage("updateTechOrderandroid.php?order=");
		new GetOrder().execute(Properties.getURL() + id);
		
		setHasOptionsMenu(true);
		
		return view;
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
	
	private class GetOrder extends AsyncTask<String, Integer, Toast>{
		
		protected void onPreExecute() {
			
			super.onPreExecute();
			info.clear();
			
		}
		
		protected Toast doInBackground(String... vars){
			
			InputStream is = null;
			String result = "";
			//the year data to send
			//ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			//the values to send
			//nameValuePairs.add(new BasicNameValuePair("username", username.getText().toString()));
			
			
			//http post
			try{

				//final String USER_AGENT = "Mozilla/5.0";
				URL url = new URL(vars[0]);
				//Log.d(TAG, url.toString());
				//String urlParameters = "username=" + vars[1] + "&password=" + vars[2];
				//byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
				//int postDataLength = postData.length;
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				//conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestMethod("GET");
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
		        Log.d("School Tech", "Error converting result "+e.toString());
			}
			//parse json data
			try{
		        JSONArray jArray = new JSONArray(result);
		        for(int i=0;i<jArray.length();i++){
		        	JSONObject json_data = jArray.getJSONObject(i);
		                Log.i("log_tag","date: " + json_data.getString("date") + ", user: " + json_data.getString("user") + ", info: " + json_data.getString("info"));
		                //TechSummary.putExtra("logonmessage", json_data.getString("response"));
		                
		                info.add(i,"Date: " + json_data.getString("date") + "\nUser: " + json_data.getString("user") + "\n\nInfo: \n" + json_data.getString("info") + "\n\n");
		                
		                //Toast.makeText(getApplicationContext(), "The Message Was " + json_data.getString("message"), Toast.LENGTH_SHORT).show();
		        }

			}catch(JSONException e){
		        Log.d("School Tech", "Error parsing data "+e.toString());
		        //Toast.makeText(getActivity().getApplicationContext(), "Login unsuccessful", Toast.LENGTH_SHORT).show();
		        
			}
			
			return null;
			
		}
		
		protected void onPostExecute(Toast result){
			
			progressBar.setVisibility(View.GONE);
			
			String[] orderResults = new String[info.size()];
 			info.toArray(orderResults);
 			//int i=0;
 			//textView1.setText(orderResults[i]);
 			//i++;
 			for(int i=0;i<orderResults.length;i++){
 				ticketInfo.append(orderResults[i]);
 			}
		}
	}
	
	private class UpdateOrder extends AsyncTask<String, Integer, Toast> {
		
		protected void onPreExecute(){
			super.onPreExecute();
		}
		
		protected Toast doInBackground(String... vars){
			
			InputStream is = null;
			String result = "";
			//the year data to send
			//ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			//the values to send
			//nameValuePairs.add(new BasicNameValuePair("username", Properties.getUsername()));
			//nameValuePairs.add(new BasicNameValuePair("id", id));
			//nameValuePairs.add(new BasicNameValuePair("info" , UpdatedInfo.getText().toString()));
			//nameValuePairs.add(new BasicNameValuePair("status" , TicketStatus));


			//http post
			try{

				//final String USER_AGENT = "Mozilla/5.0";
				URL url = new URL(vars[0]);
				//Log.d(TAG, url.toString());
				String urlParameters = "username=" + vars[1] + "&id=" + vars[2] + "&info=" + vars[3] + "&status=" + vars[4];
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
		        //Log.d(TAG, "Error converting result "+e.toString());
			}
			//parse json data
			try{
		        JSONArray jArray = new JSONArray(result);
		        for(int i=0;i<jArray.length();i++){
		        	JSONObject json_data = jArray.getJSONObject(i);
		                Log.i("log_tag","results: " + json_data.getString("results"));
		                //TechSummary.putExtra("logonmessage", json_data.getString("response"));

		                UpdateResults = json_data.getString("results");

		                //Toast.makeText(getApplicationContext(), "The Message Was " + json_data.getString("message"), Toast.LENGTH_SHORT).show();
		        }

			}catch(JSONException e){
		        //Log.d(TAG, "Error parsing data "+e.toString());
		        Toast.makeText(getActivity().getApplicationContext(), "Update unsuccessful", Toast.LENGTH_SHORT).show();

			}
			
			return null;
		}
		
		protected void onPostExecute(Toast result){
			
			myInstance.dismiss();
			Toast.makeText(getActivity().getApplicationContext(), UpdateResults, Toast.LENGTH_LONG).show();
			
			//remove the UpdateTicketFragment because the update was successful
			getActivity().getFragmentManager().popBackStack();
			
		}
	}

}
