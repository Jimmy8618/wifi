package com.jimmy.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import android.cube.AllDevicePolicyManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText ssidEditText;
    private EditText passwordEditText;
    private Button saveBtn,removeBtn;
    private WifiUtil wifiUtil;
    private AllDevicePolicyManager mAllDevicePolicyManager;
    WifiManager mWifiManager;

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

    private void initView() {
        ssidEditText = (EditText) findViewById(R.id.edit_ssid);
        passwordEditText = (EditText) findViewById(R.id.eidt_password);
        saveBtn = (Button) findViewById(R.id.btn_save);
        removeBtn = (Button) findViewById(R.id.btn_remove);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                wifiUtil.mWifiManager.enableNetwork(20, true);
//                wifiUtil.mWifiManager.saveConfiguration();
//                wifiUtil.mWifiManager.reconnect();
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

            }
        });


        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
    }
}
