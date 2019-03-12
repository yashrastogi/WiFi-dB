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
    private List<Entry> entries;
    private ArrayAdapter<Entry> crsAdapter;
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
                } else {
                    SSID = "Connected: "+wifiInfo.getSSID();
                    BSSID = "BSSID: "+wifiInfo.getBSSID();
                }
                dBm = wifiInfo.getRssi();
                TextView tvBSSID = findViewById(R.id.textView7);
                TextView tvSSID = findViewById(R.id.textView2);
                TextView tvSignal = findViewById(R.id.textView6);
                tvBSSID.setText(BSSID);
                tvSSID.setText(SSID);
                tvSignal.setText("Signal Strength: "+dBm);
                handler.postDelayed(this, 1000);
            }
        }, delay);
        appDatabase = AppDatabase.getInstance(getApplication());
        final ListView listView = findViewById(R.id.itemsList);
        entries = AppDatabase.getInstance(getApplication()).daoAccess().fetchAllEntries();
        crsAdapter = new ArrayAdapter<>(getApplication(), R.layout.list_item, entries);
        listView.setAdapter(crsAdapter);
    }

    public void inputDB(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Entry entry = new Entry();
                double X;
                double Y;
                double Z;
                entry.setSno(appDatabase.daoAccess().lastNum()+1);
                entry.setBssid(BSSID);
                entry.setDbm(dBm);
                try {
                    if (entry.getDbm() == -127) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(), "Please connect to WiFi to continue", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    X = Double.parseDouble(""+( (EditText) findViewById(R.id.editTextX) ).getText());
                    Y = Double.parseDouble(""+( (EditText) findViewById(R.id.editTextY) ).getText());
                    Z = Double.parseDouble(""+( (EditText) findViewById(R.id.editTextZ) ).getText());
                    entry.setX(X);
                    entry.setY(Y);
                    entry.setZ(Z);
                    if (entry.getDbm() != -127) {
                        appDatabase.daoAccess().insertEntry(entry);
                        updateAdapter();
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(), "Inserted entry!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.d("ParseDoubleE", e.getMessage());
                    if(entry.getDbm() != -127) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(), "Enter numbers only!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

            }
        }).start();

    }
    public void updateAdapter() {
        entries.clear();
        for(int i=0; i<AppDatabase.getInstance(getApplication()).daoAccess().fetchAllEntries().size(); i++) {
            entries.add(AppDatabase.getInstance(getApplication()).daoAccess().fetchAllEntries().get(i));
        }
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                crsAdapter.notifyDataSetChanged();
            }
        });
    }
    public void deleteLast(View v) {
        Toast.makeText(v.getContext(), "Deleting last Entry", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                appDatabase.daoAccess().deleteLastEntry();
                updateAdapter();
            }
        }).start();
    }
}
