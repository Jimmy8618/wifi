package com.jimmy.wifi.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jimmy.wifi.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ListAdapter extends BaseAdapter {
    private Context mContext;
    private List<ScanResult> mWifiList = new ArrayList<>();

    public ListAdapter(Context context, List<ScanResult> wifiList) {
        mContext = context;
        mWifiList = wifiList;
    }

    @Override
    public int getCount() {
        return mWifiList.size();
    }

    @Override
    public Object getItem(int i) {
        return mWifiList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.wifi_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ScanResult scanResult = mWifiList.get(i);
        Log.i("jimmy", scanResult.SSID);

        if(Build.VERSION.SDK_INT >Build.VERSION_CODES.M) {
            holder.tvName.setText("\n地址" + scanResult.BSSID + "\n设备名字" + scanResult.SSID +
                    "\n加密方式" + scanResult.capabilities + "\n接入频率" + scanResult.frequency + "\n信号强度" +
                    scanResult.level + "\n频道宽度" + scanResult.channelWidth + "\n会场名" + scanResult.venueName + "\n时间戳" + scanResult.timestamp
                    + "\n中心频率0:" + scanResult.centerFreq0  + "\n中心频率1:" + scanResult.centerFreq1  + "\n运营商友情名称" + scanResult.operatorFriendlyName);
        }else{
            holder.tvName.setText("\n地址" + scanResult.BSSID + "\n设备名字" + scanResult.SSID +
                    "\n加密方式" + scanResult.capabilities + "\n接入频率" + scanResult.frequency + "\n信号强度" +
                    scanResult.level);

        }
        return convertView;
    }

    static class ViewHolder {
        TextView tvName;

        ViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.tv_name);
        }
    }
}
