package com.jimmy.wifi.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Administrator on 2019/9/9.
 */
public class WifiUtil {

    public WifiManager mWifiManager;

    private WifiInfo mWifiInfo;

    private List<ScanResult> mWifiList;

    private List<WifiConfiguration> mWificonfiguration;

    private WifiManager.WifiLock mWifiLock;


    public WifiUtil(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    // 打开WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 关闭WIFI
    public void closeWifi(Context context) {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }else if(mWifiManager.getWifiState() == 1){
            Toast.makeText(context,"亲，Wifi已经关闭，不用再关了", Toast.LENGTH_SHORT).show();
        }else if (mWifiManager.getWifiState() == 0) {
            Toast.makeText(context,"亲，Wifi正在关闭，不用再关了", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"请重新关闭", Toast.LENGTH_SHORT).show();
        }
    }

    // 检查当前WIFI状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // 锁定WifiLock
    public void acquireWifiLoc() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if(mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 创建一个WifiLock
    public void createWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("test");
    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfigurations () {
        return  mWificonfiguration;
    }

    // 指定配置好的网络进行连接
    public Boolean connectConfiguration(int index) {

//        if(index > mWificonfiguration.size()) {
//            return;
//        }
        mWifiManager.enableNetwork(index, true);
        mWifiManager.saveConfiguration();
        mWifiManager.reconnect();

        return true;
    }

    public void startScan() {
        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWificonfiguration = mWifiManager.getConfiguredNetworks();
//        if (mWifiList == null) {
//            if(mWifiManager.getWifiState()==3){
//                Toast.makeText(context,"当前区域没有无线网络", Toast.LENGTH_SHORT).show();
//            }else if(mWifiManager.getWifiState()==2){
//                Toast.makeText(context,"WiFi正在开启，请稍后重新点击扫描", Toast.LENGTH_SHORT).show();
//            }else{
//                Toast.makeText(context,"WiFi没有开启，无法扫描", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    // 得到wifi列表
    public List<ScanResult> getmWifiList() {
        return mWifiList;
    }

    // 查看扫描结果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder.append("Index_" + String.valueOf(i+1)+":");
            stringBuilder.append(mWifiList.get(i).toString());
            stringBuilder.append("/n");
        }
        return  stringBuilder;
    }

    // 得到MAC地址
    public String getMacAddress() {
        return  (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // 得到接入点的BSSID
    public String getSSID() {
        return  (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }

    // 得到IP地址
    public int getIpAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 得到连接的ID
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();

    }

    // 得到WifiInfo的所有信息包
    public  String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }


    // 添加一个网络并连接
    public boolean  addNetWork(WifiConfiguration wifiConfiguration) {
        int wcgID = mWifiManager.addNetwork(wifiConfiguration);
        Log.e("wcgID", wcgID+"true");
        mWifiManager.enableNetwork(wcgID, true);
        mWifiManager.saveConfiguration();
        mWifiManager.reconnect();
        return  true;

    }

    // 断开指定ID的网络
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    //移除指定ID的wifi
    public void removeWifi(int netId) {
        disconnectWifi(netId);
        mWifiManager.removeNetwork(netId);
    }

    public WifiConfiguration createWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.allowedAuthAlgorithms.clear();
        configuration.allowedGroupCiphers.clear();
        configuration.allowedKeyManagement.clear();
        configuration.allowedPairwiseCiphers.clear();
        configuration.allowedProtocols.clear();
        configuration.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.isExsits(SSID);
        if(tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        switch (Type) {
            case 1://WIFICIPHER_NOPASS
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//                configuration.status = WifiConfiguration.Status.ENABLED;
                break;
            case 2: //WIFICIPHER_WEP
                configuration.hiddenSSID = false;
                configuration.wepKeys[0] = "\"" + Password +"\"";
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                break;
            case 3://WIFICIPHER_WPA

                configuration.preSharedKey = "\"" + Password + "\"";
                configuration.hiddenSSID = false;
               // configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                configuration.status = WifiConfiguration.Status.ENABLED;
                break;
        }
        return  configuration;
    }

    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig:
             existingConfigs) {
            if (existingConfig.SSID.equals("\"" +SSID+"\"")) {
                return  existingConfig;
            }
            
        }
        return null;
    }




}
