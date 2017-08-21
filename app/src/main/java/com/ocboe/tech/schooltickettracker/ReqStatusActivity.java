package com.ocboe.tech.schooltickettracker;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.aboutlibraries.LibsBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brad on 9/6/2016.
 */
public class ReqStatusActivity extends AppCompatActivity {

    private ArrayList<String> results = new ArrayList<>();
    private List<ReqStatus> mReqStatusList = new ArrayList<>();
    private ReqStatusListAdapter adapter;
    private static final String TAG = ReqStatusActivity.class.getSimpleName();
    protected Context mContext;
    private Toolbar tb;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TextView header_user_name, header_email;
    private ListView reqList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reqstatus);
        Properties.setPage("reqStatusAndroid.php");
        new getReqStatus().execute(Properties.getURL());

        mContext = ReqStatusActivity.this;

        reqList = (ListView) findViewById(R.id.reqList);

        reqList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String reqNumber = view.getTag().toString();;
                Properties.setPage("viewPDF.php?id=");
                Intent startDownloadIntent = new Intent(ReqStatusActivity.this,DownloadReqPDFService.class);
                startDownloadIntent.putExtra("downloadURL", Properties.getURL() + reqNumber);
                startDownloadIntent.putExtra("downloadFileName", "req" + reqNumber + ".pdf");
                startService(startDownloadIntent);
            }
        });

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
                        ReqStatusActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(ReqStatusActivity);
                        finish();
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
                                .start(mContext);
                        return true;
                    default:
                        return true;
                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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

    protected class getReqStatus extends AsyncTask<String, Integer, Toast> {
        protected void onPreExecute() {
            super.onPreExecute();
            results.clear();
        }

        protected Toast doInBackground(String... vars){
            OkHttpClient httpClient = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("reqLevel", Properties.getReqLevel())
                    .add("username", Properties.getUsername())
                    .build();

            Request request = new Request.Builder()
                    .url(vars[0])
                    .post(formBody)
                    .build();

            try{
                Response response = httpClient.newCall(request).execute();

                final int code = response.code();

                if (code == 200) {
                    try{
                        //get the response body string which is a JSONObject
                        JSONObject responseInfo = new JSONObject(response.body().string());

                        //get the array assets and store it
                        JSONArray responseAssets = responseInfo.getJSONArray("assets");

                        //go through the array and do something with the assets individually
                        for(int i=0;i<responseAssets.length();i++){
                            JSONObject assetObject = responseAssets.getJSONObject(i);
                            //String List = "Req#: " + assetObject.getInt("id") + "\r\n" + "PO: " + assetObject.getString("po") + "\r\n" +  "Date: " + assetObject.getString("date") + "\r\n" + "Vendor: " + assetObject.getString("vendor") + "\r\n" + "Status: " + assetObject.getString("status");
                            //results.add(List);
                            mReqStatusList.add(new ReqStatus(assetObject.getInt("id"),assetObject.getString("po"),assetObject.getString("date"),assetObject.getString("vendor"),assetObject.getString("status")));
                        }
                    } catch(JSONException je) {
                        je.printStackTrace();
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Toast result){
            //adapter = new ArrayAdapter<String>(ReqStatusActivity.this, android.R.layout.simple_list_item_1, results);
            adapter = new ReqStatusListAdapter(getApplicationContext(), mReqStatusList);
            reqList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
