package com.example.wifidb;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    String BSSID;
    String SSID;
    int dBm;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
        }

        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final Handler handler = new Handler();
        int delay = 100; //milliseconds
        handler.postDelayed(new Runnable(){
            public void run(){
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if(wifiInfo.getRssi() == -127) {
                    SSID = "Not connected to WiFi";
                    BSSID = "Connection required!";
                    dBm = 0;
                } else {
                    SSID = wifiInfo.getSSID();
                    BSSID = wifiInfo.getBSSID();
                    dBm = wifiInfo.getRssi();
                }
                TextView tvBSSID = findViewById(R.id.textView7);
                TextView tvSSID = findViewById(R.id.textView2);
                TextView tvSignal = findViewById(R.id.textView6);
                tvBSSID.setText("BSSID: "+BSSID);
                tvSSID.setText("Connected: "+SSID);
                tvSignal.setText("Signal Strength: "+dBm);
                handler.postDelayed(this, 1000);
            }
        }, delay);
        appDatabase = AppDatabase.getInstance(getApplication());
        final ListView listView = findViewById(R.id.itemsList);
        handler.postDelayed(new Runnable(){
            public void run(){
                List<Entry> entries = AppDatabase.getInstance(getApplication()).daoAccess().fetchAllEntries();
                ArrayAdapter<Entry> crsAdapter = new ArrayAdapter<Entry>(getApplication(),R.layout.list_item, entries);
                listView.setAdapter(crsAdapter);
                handler.postDelayed(this, 1000);
            }
        }, delay);


    }

    public void inputDB(View v) {
        Toast.makeText(v.getContext(), "Inserting Entry", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Entry entry = new Entry();
                double X;
                double Y;
                double Z;
                try {
                    X = Double.parseDouble(""+( (EditText) findViewById(R.id.editTextX) ).getText());
                    Y = Double.parseDouble(""+( (EditText) findViewById(R.id.editTextY) ).getText());
                    Z = Double.parseDouble(""+( (EditText) findViewById(R.id.editTextZ) ).getText());
                    entry.setSno(appDatabase.daoAccess().lastNum()+1);
                    entry.setBssid(BSSID);
                    entry.setDbm(dBm);
                    entry.setX(X);
                    entry.setY(Y);
                    entry.setZ(Z);
                    appDatabase.daoAccess().insertEntry(entry);
                } catch (Exception e) {
                    Log.d("ParseDoubleE", e.getMessage());
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplication(), "Enter numbers only!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }
    public void deleteLast(View v) {
        Toast.makeText(v.getContext(), "Deleting last Entry", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                appDatabase.daoAccess().deleteLastEntry();
            }
        }).start();
    }
}
