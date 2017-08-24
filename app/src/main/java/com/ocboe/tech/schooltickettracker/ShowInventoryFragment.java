package com.ocboe.tech.schooltickettracker;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Brad on 8/23/2017.
 */

public class ShowInventoryFragment extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Properties.setPage("viewInventoryAndroid.php");
        //new TechSummaryFragment.getTicketSummary().execute(Properties.getURL());

        setHasOptionsMenu(true);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        Properties.setPage("viewtechSummaryandroid.php");

        ((InventoryActivity) getActivity()).setActionBarTitle("");
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
}
