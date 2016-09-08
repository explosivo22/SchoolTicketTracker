package com.ocboe.tech.schooltickettracker;

import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.aboutlibraries.LibsBuilder;

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
import java.util.ArrayList;

/**
 * Created by Brad on 9/3/2016.
 */
public class InventoryActivity extends AppCompatActivity {
    private static Spinner SchoolSpinner;
    private static Spinner RoomSpinner;
    private static Spinner UserSpinner;
    private boolean inhibit = true;
    private boolean inhibitUser = true;
    private boolean inhibitRoom = true;
    private ArrayList<String> roomArrayList = new ArrayList<String> ();
    private ArrayList<String> userArrayList = new ArrayList<String> ();
    protected static ArrayAdapter<String> roomAdapter, userAdapter;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar tb;
    private FragmentManager fm;
    private Context mContext;
    private TextView header_user_name, header_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        mContext = InventoryActivity.this;
        fm = getFragmentManager();

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
                        Intent TechSummaryActivity = new Intent(mContext, TechSummaryActivity.class);
                        TechSummaryActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(TechSummaryActivity);
                        finish();
                        return true;

                    // For rest of the options we just show a toast on click

                    case R.id.dispose:
                        Intent DisposeActivity = new Intent(mContext, DisposeActivity.class);
                        DisposeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(DisposeActivity);
                        finish();
                        return true;

                    case R.id.inventory:
                        Intent InventoryActivity = new Intent(mContext, InventoryActivity.class);
                        InventoryActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(InventoryActivity);
                        finish();
                        return true;
                    case R.id.settings:
                        Intent SettingsActivity = new Intent(mContext, SettingsActivity.class);
                        startActivity(SettingsActivity);
                        return true;
                    case R.id.reqStatus:
                        Intent ReqStatusActivity = new Intent(mContext, ReqStatusActivity.class);
                        startActivity(ReqStatusActivity);
                        return true;
                    case R.id.openSource:
                        new LibsBuilder()
                                .withFields(R.string.class.getFields())
                                .withAutoDetect(true)
                                .withVersionShown(false)
                                .withLicenseShown(false)
                                .withActivityTheme(R.style.AppTheme)
                                .withActivityTitle(getString(R.string.open_source_setting_title))
                                .withAboutIconShown(true)
                                .withAboutVersionShown(true)
                                .withAboutDescription(getString(R.string.app_license_description))
                                .start(InventoryActivity.this);
                        return true;
                    default:
                        return true;
                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        SchoolSpinner = (Spinner)findViewById(R.id.schoolSpinner);
        RoomSpinner = (Spinner)findViewById(R.id.roomSpinner);
        UserSpinner = (Spinner)findViewById(R.id.userSpinner);

        RoomSpinner.setVisibility(View.GONE);
        UserSpinner.setVisibility(View.GONE);

        SchoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        RoomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        UserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        getSupportActionBar().setTitle("Inventory Search");
    }

    private void SetHeader() {

        header_user_name.setText(Properties.getName());
        header_email.setText(Properties.getEmail());
    }

    private void setToolBar() {
        tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.icon);
        //ab.setDisplayHomeAsUpEnabled(true);
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

            RoomSpinner.setVisibility(View.VISIBLE);
            UserSpinner.setVisibility(View.VISIBLE);
            inhibitUser = true;
            inhibitRoom = true;

            roomAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, roomArrayList);
            userAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, userArrayList);

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
            userAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, userArrayList);

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

            roomAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, roomArrayList);
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
