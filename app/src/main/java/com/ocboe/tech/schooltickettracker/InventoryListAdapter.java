package com.ocboe.tech.schooltickettracker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Brad on 8/23/2017.
 */

public class InventoryListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Inventory> mInventoryList;

    //Constructor
    public InventoryListAdapter(Context mContext, List<Inventory> mInventoryList) {
        this.mContext = mContext;
        this.mInventoryList = mInventoryList;
    }

    @Override
    public int getCount() { return mInventoryList.size(); }

    @Override
    public Object getItem(int position) { return mInventoryList.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext,R.layout.inventory_list_adapter, null);
        TextView inventory_Tag = (TextView)v.findViewById(R.id.inventory_tag);
        TextView inventory_Type = (TextView)v.findViewById(R.id.inventory_type);
        TextView inventory_Brand = (TextView)v.findViewById(R.id.inventory_brand);
        TextView inventory_Model = (TextView)v.findViewById(R.id.inventory_model);
        TextView inventory_Serial = (TextView)v.findViewById(R.id.inventory_serial);

        inventory_Tag.setText("Tag#: " + String.valueOf(mInventoryList.get(position).getTag()));
        inventory_Type.setText("Type: " + String.valueOf(mInventoryList.get(position).getType()));
        inventory_Brand.setText("Brand: " + String.valueOf(mInventoryList.get(position).getBrand()));
        inventory_Model.setText("Model: " + String.valueOf(mInventoryList.get(position).getModel()));
        inventory_Serial.setText("Serial: " + String.valueOf(mInventoryList.get(position).getSerial()));

        return v;
    }
}
