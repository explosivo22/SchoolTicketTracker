package com.ocboe.tech.schooltickettracker;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Brad on 9/8/2016.
 */
public class ReqStatusListAdapter extends BaseAdapter {
    private Context mContext;
    private List<ReqStatus> mReqStatusList;

    //Constructor


    public ReqStatusListAdapter(Context mContext, List<ReqStatus> mReqStatusList) {
        this.mContext = mContext;
        this.mReqStatusList = mReqStatusList;
    }

    @Override
    public int getCount() {
        return mReqStatusList.size();
    }

    @Override
    public Object getItem(int position) {
        return mReqStatusList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_req_status, null);
        TextView req_Number = (TextView)v.findViewById(R.id.req_number);
        TextView req_PO = (TextView)v.findViewById(R.id.req_po);
        TextView req_Date = (TextView)v.findViewById(R.id.req_date);
        TextView req_Vendor = (TextView)v.findViewById(R.id.req_vendor);
        TextView req_Status = (TextView)v.findViewById(R.id.req_status);

        //set text for TextView
        req_Number.setText("REQ#: " + String.valueOf(mReqStatusList.get(position).getReqNumber()));
        String PO;
        if (mReqStatusList.get(position).getReqPO().isEmpty()) {
            PO = new String();
        } else {
            PO = mReqStatusList.get(position).getReqPO();
        }
        req_PO.setText("PO#: " + PO);
        req_Date.setText("Date: " + mReqStatusList.get(position).getReqDate());
        req_Vendor.setText("Vendor: " + mReqStatusList.get(position).getReqVendor());
        switch (mReqStatusList.get(position).getReqStatus()){
            case "Issued":
                req_Status.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "Denied":
                req_Status.setTextColor(Color.parseColor("#F44336"));
                break;
            default:
                break;
        }
        req_Status.setText(mReqStatusList.get(position).getReqStatus());

        //save reqNumber to tag
        v.setTag(mReqStatusList.get(position).getReqNumber());

        return v;
    }
}
