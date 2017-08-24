package com.ocboe.tech.schooltickettracker;

import android.app.ListFragment;
import android.content.Intent;
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
 * Created by Brad on 8/23/2017.
 */

public class ShowInventoryFragment extends ListFragment {

    private ArrayList<String> results = new ArrayList<>();
    private List<Inventory> mInventoryList = new ArrayList<>();
    private InventoryListAdapter adapter;
    private ListView inventoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_inventory_fragment, container, false);
        Properties.setPage("viewInventoryAndroid.php");

        inventoryList = (ListView)view.findViewById(android.R.id.list);

        Bundle bundle = this.getArguments();
        String school = bundle.getString("school");
        String user = bundle.getString("user");
        String room = bundle.getString("room");
        new getInventory().execute(Properties.getURL(),school,user,room);
        ((InventoryActivity) getActivity()).setActionBarTitle(school+": "+room);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Properties.setPage("viewtechSummaryandroid.php");

        //((InventoryActivity) getActivity()).setActionBarTitle("");
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
                Intent SettingsActivity = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                startActivity(SettingsActivity);;
                break;
            default:
                break;
        }

        return false;
    }

    private class getInventory extends AsyncTask<String, Integer, Toast> {
        protected void onPreExecute() {
            super.onPreExecute();
            results.clear();
        }

        protected Toast doInBackground(String... vars){
            OkHttpClient httpClient = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("school", vars[1])
                    .add("user", vars[2])
                    .add("room", vars[3])
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
                            //String inventory = assetObject.getString("tag") + "\r\n"  + assetObject.getString("type") + "\r\n"  + assetObject.getString("brand") + "\r\n"  + assetObject.getString("model") + "\r\n"  + assetObject.getString("serial");
                            mInventoryList.add(new Inventory(assetObject.getString("tag"),assetObject.getString("type"),assetObject.getString("brand"),assetObject.getString("model"),assetObject.getString("serial")));
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
            adapter = new InventoryListAdapter(getActivity(), mInventoryList);
            inventoryList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
