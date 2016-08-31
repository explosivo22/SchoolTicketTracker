package com.ocboe.tech.schooltickettracker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class DisposeEquipmentFragment extends Fragment{
	
	private static EditText Tag;
	private static EditText DisposeInfo;
	private static EditText DateDisplay;
	private String UpdateResults;
	private MySpinnerDialog myInstance;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.dispose_equipment, container, false);
		
		Tag = (EditText)view.findViewById(R.id.disposeTag);
		DisposeInfo = (EditText)view.findViewById(R.id.disposeInfo);
		
		//set text to the most commonly disposed of method
		DisposeInfo.setText(R.string.e_waste);
		
		DateDisplay = (EditText)view.findViewById(R.id.dateDisplay);
		
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		//load initial date set to today
		updateDate(year, month, day);
		
		Button ChangeDate = (Button)view.findViewById(R.id.pickDate);
		ChangeDate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//create new date picker dialog and show it
				DialogFragment newFragment = new DatePickerFragment();
			    newFragment.show(getFragmentManager(), "datePicker");
				
			}
		});
		
		Button Submit = (Button)view.findViewById(R.id.disposeSubmit);
		Submit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				FragmentManager fm = getFragmentManager();
				myInstance = new MySpinnerDialog();
				Bundle args = new Bundle();
				args.putString("title", "Disposing...");
				myInstance.setArguments(args);
				myInstance.show(fm, "update_dialog");
				Properties.setPage("disposeAndroid.php");
				new DisposeEquipment().execute(Properties.getURL(),Tag.getText().toString(),DateDisplay.getText().toString(),DisposeInfo.getText().toString());

			}
		});

		//getActivity().getActionBar().setTitle("Dispose Equipment");
		((TechSummaryActivity) getActivity()).setActionBarTitle("Dispose Equipment");
		
		setHasOptionsMenu(true);
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		//getActivity().getActionBar().setTitle("Dispose Equipment");
		((TechSummaryActivity) getActivity()).setActionBarTitle("Dispose Equipment");
	}
	
	//update the text field with the new formatted date
	public static void updateDate(int Year, int Month, int Day) {
		GregorianCalendar c = new GregorianCalendar(Year, Month, Day);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
		
		DateDisplay.setText(sdf.format(c.getTime()));
	}
	
	//shows dialog with a date picker
	public static class DatePickerFragment extends DialogFragment
	implements DatePickerDialog.OnDateSetListener {
	
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
			
		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			//updateDate(String.valueOf(year) + String.valueOf((month + 1)) + String.valueOf(day));
			updateDate(year,month,day);
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
	
	protected class DisposeEquipment extends AsyncTask<String, Integer, Toast> {
		
		protected void onPreExecute(){
			super.onPreExecute();
		}
		
		protected Toast doInBackground(String... vars){
			
			InputStream is = null;
			String result = "";
			// the year data to send
			//ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			// the values to send
			//nameValuePairs.add(new BasicNameValuePair("tag", Tag.getText()
					//.toString()));
			//nameValuePairs.add(new BasicNameValuePair("dispose", DateDisplay
					//.getText().toString()));
			//nameValuePairs.add(new BasicNameValuePair("info", DisposeInfo
					//.getText().toString()));

			// http post
			try{

				//final String USER_AGENT = "Mozilla/5.0";
				URL url = new URL(vars[0]);
				//Log.d(TAG, url.toString());
				String urlParameters = "tag=" + vars[1] + "&dispose=" + vars[2] + "&info=" + vars[3];
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
					//Log.i("log_tag", "id: " + json_data.getInt("id")+ ", response: " + json_data.getString("response"));
					UpdateResults = json_data.getString("response");
				}

			} catch (JSONException e) {
				//Log.d(TAG, "Error parsing data " + e.toString());
				//Toast.makeText(getActivity().getApplicationContext(), "dispose unsuccessful",Toast.LENGTH_SHORT).show();
			}
			
			return null;
		}
		
		protected void onPostExecute(Toast result){
			
			myInstance.dismiss();
			Toast.makeText(getActivity().getApplicationContext(), UpdateResults, Toast.LENGTH_LONG).show();
			
		}
	}

}
