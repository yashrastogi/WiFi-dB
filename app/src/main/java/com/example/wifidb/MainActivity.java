package com.example.wifidb;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
        ListView listView = findViewById(R.id.itemsList);
        List<Entry> entries = AppDatabase.getInstance(getApplication()).daoAccess().fetchAllEntries();
        ArrayAdapter<Entry> crsAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, entries);
        listView.setAdapter(crsAdapter);
    }

    public void inputDB(View v) {
        Toast.makeText(v.getContext(), "Inserting Entry", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Entry entry = new Entry();
                double X = Double.parseDouble(""+( (EditText) findViewById(R.id.editTextX) ).getText());
                double Y = Double.parseDouble(""+( (EditText) findViewById(R.id.editTextY) ).getText());
                double Z = Double.parseDouble(""+( (EditText) findViewById(R.id.editTextZ) ).getText());
                entry.setSno(appDatabase.daoAccess().lastNum()+1);
                entry.setBssid(BSSID);
                entry.setDbm(dBm);
                entry.setX(X);
                entry.setY(Y);
                entry.setZ(Z);
                appDatabase.daoAccess().insertEntry(entry);
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
