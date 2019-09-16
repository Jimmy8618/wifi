package com.jimmy.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import android.cube.AllDevicePolicyManager;
import android.widget.ListView;
import android.widget.TextView;

import com.jimmy.wifi.adapter.ListAdapter;
import com.jimmy.wifi.utils.PermissionUtil;
import com.jimmy.wifi.utils.WifiUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "jimmy";

    private EditText ssidEditText;
    private EditText passwordEditText;
    private Button saveBtn,removeBtn,checkBtn,openBtn,closeBtn,scanBtn;
    private TextView tvScan;
    private ListView mListView;
    private WifiUtil wifiUtil;
    private AllDevicePolicyManager mAllDevicePolicyManager;
    WifiManager mWifiManager;
    private List<ScanResult> mWifiList = new ArrayList<>(); //wifi列表
    private ListAdapter adapter;
    private WifiBroadcastReceiver wifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAllDevicePolicyManager = (AllDevicePolicyManager) getSystemService("cube");
       mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                initView();
        wifiUtil = new WifiUtil(this);
        wifiUtil.startScan();
        for (WifiConfiguration configuration :
                wifiUtil.getConfigurations()) {

            Log.e("configuration", "ssid:"+configuration.SSID +"--id:"+ configuration.networkId +
                    "--priority" + configuration.priority + "--allowedAuthAlgorithms:"+configuration.allowedAuthAlgorithms +
            "--allowedGroupCiphers:"+configuration.allowedGroupCiphers +"--allowedKeyManagement:" +configuration.allowedKeyManagement +
                    "--allowedAuthAlgorithms:"+configuration.allowedAuthAlgorithms
            + "--allowedPairwiseCiphers:"+configuration.allowedPairwiseCiphers
            +"--hiddenSSID:"+configuration.hiddenSSID
            +"--wepTxKeyIndex:"+configuration.wepTxKeyIndex
            +"--wepKeys:"+configuration.wepKeys[0]
            +"--preSharedKey"+ configuration.preSharedKey
            +"--status:"+configuration.status);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //注册广播
        wifiReceiver = new WifiBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//监听wifi是开关变化的状态
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//监听wifiwifi连接状态广播
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);//监听wifi列表变化（开启一个热点或者关闭一个热点）
        registerReceiver(wifiReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //取消监听
        unregisterReceiver(wifiReceiver);
    }


    private void initView() {
        ssidEditText = (EditText) findViewById(R.id.edit_ssid);
        passwordEditText = (EditText) findViewById(R.id.eidt_password);
        tvScan = findViewById(R.id.tv_scan);
        saveBtn = (Button) findViewById(R.id.btn_save);
        removeBtn = (Button) findViewById(R.id.btn_remove);
        checkBtn=(Button) findViewById(R.id.check_wifi);
        openBtn=(Button) findViewById(R.id.open_wifi);
        closeBtn=(Button) findViewById(R.id.close_wifi);
        scanBtn=(Button) findViewById(R.id.scan_wifi);
        mListView=(ListView) findViewById(R.id.wifi_list);

        saveBtn.setOnClickListener(this);
        removeBtn.setOnClickListener(this);
        checkBtn.setOnClickListener(this);
        openBtn.setOnClickListener(this);
        scanBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        tvScan.setOnClickListener(this);
//        mlistView.setOnClickListener(this);

    }

    /**
     * 扫描附近wifi
     */
    private void scanWifiInfo() {
        mWifiManager.setWifiEnabled(true);
        mWifiManager.startScan();

        mWifiList.clear();
        mWifiList = mWifiManager.getScanResults();

        if(mWifiList != null && mWifiList.size() > 0) {
            adapter = new ListAdapter(this, mWifiList);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //连接wifi
                    ScanResult scanResult = mWifiList.get(position);
                    connectWifi(scanResult.SSID, "ILOVEYOU", "WPA");
                }
            });
        }
    }

    /**
     * 连接wifi
     * @param targetSsid wifi的SSID
     * @param targetPsd 密码
     * @param enc 加密类型
     */
    public void connectWifi(String targetSsid, String targetPsd, String enc) {
        // 1、注意热点和密码均包含引号，此处需要需要转义引号
        String ssid = "\"" + targetSsid + "\"";
        String psd = "\"" + targetPsd + "\"";

        //2、配置wifi信息
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
        switch (enc) {
            case "WEP":
                // 加密类型为WEP
                conf.wepKeys[0] = psd;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case "WPA":
                // 加密类型为WPA
                conf.preSharedKey = psd;
                break;
            case "OPEN":
                //开放网络
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        //3、链接wifi
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals(ssid)) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }

    //监听wifi状态广播接收器
    public class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {

                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (state) {
                    /**
                     * WIFI_STATE_DISABLED    WLAN已经关闭
                     * WIFI_STATE_DISABLING   WLAN正在关闭
                     * WIFI_STATE_ENABLED     WLAN已经打开
                     * WIFI_STATE_ENABLING    WLAN正在打开
                     * WIFI_STATE_UNKNOWN     未知
                     */
                    case WifiManager.WIFI_STATE_DISABLED: {
                        Log.i(TAG, "已经关闭");
                        break;
                    }
                    case WifiManager.WIFI_STATE_DISABLING: {
                        Log.i(TAG, "正在关闭");
                        break;
                    }
                    case WifiManager.WIFI_STATE_ENABLED: {
                        Log.i(TAG, "已经打开");
//                        sortScaResult();
                        break;
                    }
                    case WifiManager.WIFI_STATE_ENABLING: {
                        Log.i(TAG, "正在打开");
                        break;
                    }
                    case WifiManager.WIFI_STATE_UNKNOWN: {
                        Log.i(TAG, "未知状态");
                        break;
                    }
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                Log.i(TAG, "--NetworkInfo--" + info.toString());
                if (NetworkInfo.State.DISCONNECTED == info.getState()) {//wifi没连接上
                    Log.i(TAG, "wifi没连接上");
                } else if (NetworkInfo.State.CONNECTED == info.getState()) {//wifi连接上了
                    Log.i(TAG, "wifi连接上了");
                } else if (NetworkInfo.State.CONNECTING == info.getState()) {//正在连接
                    Log.i(TAG, "wifi正在连接");
                }
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                Log.i(TAG, "网络列表变化了");
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                try {
                    String ssid = ssidEditText.getText().toString().trim();
                    String password = passwordEditText.getEditableText().toString().trim();
                    Log.d("jimmy","WifiConfiguration networkId");
                    WifiConfiguration config = wifiUtil.createWifiInfo(ssid, password, password.length() == 0 ? 1 : 3);
                    int wcgID = mWifiManager.addNetwork(config);
                    mAllDevicePolicyManager.connectWifi(wcgID);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.btn_remove:
                try {
//                    WifiManager mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    List<WifiConfiguration> configList = mWifiManager.getConfiguredNetworks();
                    for (WifiConfiguration config : configList) {
                        Log.d("jimmy", "WifiConfiguration config=" + config);
                        mAllDevicePolicyManager.removeWifiConfig(config.SSID);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.check_wifi:
                break;
            case R.id.open_wifi:
                wifiUtil.openWifi();
                break;
            case R.id.close_wifi:
                wifiUtil.closeWifi(this);
                break;
            case R.id.scan_wifi:
                break;
            case R.id.tv_scan:
                //要定位权限才能搜索wifi
                PermissionUtil.requestEach(this, new PermissionUtil.OnPermissionListener() {
                    @Override
                    public void onSucceed() {
                        //授权成功后打开wifi
                        scanWifiInfo();
                    }

                    @Override
                    public void onFailed(boolean showAgain) {

                    }
                },PermissionUtil.LOCATION);
                break;

        }

    }
}
