package com.ocboe.tech.schooltickettracker;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Brad on 9/3/2016.
 */
public class DisposeActivity extends AppCompatActivity {
    private static EditText Tag;
    private static EditText DisposeInfo;
    private static EditText DateDisplay;
    private String UpdateResults;
    private MySpinnerDialog myInstance;
    protected Context mContext;
    private Toolbar tb;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private FragmentManager fm;
    private TextView header_user_name, header_email;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispose);

        mContext = DisposeActivity.this;
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
                                    //.addToBackStack(null)
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
                        if(fm.findFragmentByTag("InventorySearch") == null){
                            Fragment inventoryFragment = new InventoryRoomFragment();
                            fm.popBackStack();
                            fm.beginTransaction()
                                    .replace(R.id.summaryListContainer, inventoryFragment, "InventorySearch")
                                    //.addToBackStack("InventorySearch")
                                    .commit();
                        }
                        return true;
                    case R.id.settings:
                        Intent SettingsActivity = new Intent(mContext, SettingsActivity.class);
                        startActivity(SettingsActivity);
                        return true;
                    default:
                        return true;
                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Tag = (EditText)findViewById(R.id.disposeTag);
        DisposeInfo = (EditText)findViewById(R.id.disposeInfo);

        //set text to the most commonly disposed of method
        DisposeInfo.setText(R.string.e_waste);

        DateDisplay = (EditText)findViewById(R.id.dateDisplay);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //load initial date set to today
        updateDate(year, month, day);

        Button ChangeDate = (Button)findViewById(R.id.pickDate);
        ChangeDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //create new date picker dialog and show it
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");

            }
        });

        Button Submit = (Button)findViewById(R.id.disposeSubmit);
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

        getSupportActionBar().setTitle("Dispose Equipment");

        //setHasOptionsMenu(true);
    }

    private void setToolBar() {
        tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.icon);
        ab.setDisplayHomeAsUpEnabled(true);
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

    private void SetHeader() {

        header_user_name.setText(Properties.getName());
        header_email.setText(Properties.getEmail());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_settings:
                Intent SettingsActivity = new Intent(mContext, SettingsActivity.class);
                startActivity(SettingsActivity);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setTitle("Dispose Equipment");
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
            updateDate(year,month,day);
        }
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
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Toast result){

            myInstance.dismiss();
            Toast.makeText(mContext, UpdateResults, Toast.LENGTH_LONG).show();

        }
    }
}