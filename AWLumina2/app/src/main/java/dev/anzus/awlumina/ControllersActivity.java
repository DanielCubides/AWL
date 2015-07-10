package dev.anzus.awlumina;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.annotation.AnimRes;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import dev.anzus.awlumina.util.AdapterController;
import dev.anzus.awlumina.util.Commands;
import dev.anzus.awlumina.util.ControllersSqLiteHelper;
import dev.anzus.awlumina.util.Device;
import dev.anzus.awlumina.util.ItemController;
import dev.anzus.awlumina.util.Utils;


public class ControllersActivity extends ActionBarActivity {

    private final int UNDISCOVERED = 0, DISCOVERED = 1, CONNECTED = 2;
    private long backPressedTime = 0;

    ListView lista;
    Commands sendCommand;
    public MyHandler myHandler = null;
    private static Context context;

    ControllersSqLiteHelper controllersDBH;
    SQLiteDatabase db;

    int attempsGetNetworks = 0;
    String deviceIP = null;
    String deviceMac = null;
    String deviceSSID = null;
    Device devices = new Device();
    Utils util;
    ProgressBar progressBar;
    int attemps = 0;

    ArrayList<ItemController> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controllers);

        controllersDBH = new ControllersSqLiteHelper(this, "DBControllers", null, 1);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        lista = (ListView) findViewById(R.id.listaControladores);
        listItems = new ArrayList<ItemController>();
        AdapterController adapter = new AdapterController(this, listItems);
        lista.setAdapter(adapter);
        final Context context = this;
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if( !isConnected(listItems.get(position).getPreviousMac()) ) {
                    if( listItems.get(position).getActiveState() == DISCOVERED ) {
                        Intent intent = new Intent(context, LinkActivity.class);
                        intent.putExtra("MAC", listItems.get(position).getPreviousMac());
                        intent.putExtra("IP", util.getIpFromArpCache(listItems.get(position).getPreviousMac()));
                        startActivity(intent);
                        finish();
                    }
                }else{
                    if( listItems.get(position).getActiveState() == CONNECTED ) {
                        Intent intent = new Intent(context, PanelActivity.class);
                        intent.putExtra("MAC", listItems.get(position).getMac());
                        intent.putExtra("IP", util.getIpFromArpCache(listItems.get(position).getMac()));
                        intent.putExtra("Name", listItems.get(position).getName());
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        startThread();
    }

    private void startActions(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        attemps = 0;

        context = this;
        myHandler = new MyHandler();
        sendCommand = new Commands(context, myHandler, "");
        util = new Utils( context );
        clearDevices();
        addFromDataBase();
    }

    public void addFromDataBase(){
        db = controllersDBH.getWritableDatabase();
        if( db != null ){
            Cursor cursor = db.rawQuery("SELECT mac, previous_mac, name FROM Controllers", null);
            if (cursor.moveToFirst()) {
                do {
                    String mac = cursor.getString(0);
                    String previousMac = cursor.getString(1);
                    String name = cursor.getString(2);
                    devices.addItem("10.10.100.254", mac);
                    ItemController item = new ItemController(devices.size(), mac, previousMac, name, UNDISCOVERED);
                    listItems.add(item);
                } while(cursor.moveToNext());
                if( devices.size() > 0 ) {
                    AdapterController adapter = new AdapterController(this, listItems);
                    lista.setAdapter(adapter);
                }
                else
                    lista.setAdapter(null);
            }
            db.close();
        }
    }

    public boolean isConnected( String mac ){
        return !util.getIpFromArpCache(mac).equals("10.10.100.254");
    }

    public void onBackPressed(){
        finish();
        /*Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_controladores, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch ( item.getItemId() ){
            case R.id.btnActualizar:
                attemps++;

                if( attemps > 1 ) {
                    startActivity(getIntent());
                }else
                    startThread();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startThread(){
        startActions();
        new asyncTaskUpdateProgress().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        //startActivity(getIntent());
    }

    private void searchWifi(){
        attempsGetNetworks = 0;
        attemptDiscovery();
        new CountDownTimer(100, 10) {
            public void onFinish() {
                sendCommand.getWifiNetworks();
            }
            public void onTick(long millisUntilFinished) {}
        }.start();
    }

    private void attemptDiscovery() {
        sendCommand.discover();
    }

    private void clearDevices(){
        deviceIP = null;
        deviceMac = null;
        deviceSSID = null;
        devices.clear();
        listItems.clear();
        lista.setAdapter(null);
    }

    private void listWifiNetworks(final String[] networks) {
        if( networks.length < 3 ) {
            if (attempsGetNetworks < 15) {
                attemptDiscovery();
                sendCommand.getWifiNetworks();
                attempsGetNetworks++;
            }
        }
    }

    private void newDevice( String addrIP, String addrMac, String SSID ){
        String previousMac = addrMac;
        if( !isConnected(previousMac) )
            addrMac = addrMac.substring(0, addrMac.length() - 2) + SSID.toLowerCase().substring(SSID.length() - 2);
        Log.d( "prueva device", addrMac + " --- " + addrIP );
        if( !devices.containsAddr( addrMac ) ) {
            if( util.validIP(addrIP) && util.validMac(addrMac.replace( ":", "" ).toUpperCase()) ){
                devices.addItem(addrIP, addrMac);
                ItemController item = new ItemController(devices.size(), addrMac, previousMac, "Aw Lumina " + devices.size(), isConnected(previousMac)? CONNECTED: DISCOVERED);
                listItems.add(item);
                db = controllersDBH.getWritableDatabase();
                if( db != null ){
                    ContentValues newInput = new ContentValues();
                    newInput.put("mac", addrMac);
                    newInput.put("previous_mac", previousMac);
                    newInput.put("name", item.getName());
                    newInput.put("group1", "Grupo 1");
                    newInput.put("group2", "Grupo 2");
                    newInput.put("group3", "Grupo 3");
                    newInput.put("group4", "Grupo 4");

                    db.insert("Controllers", null, newInput);
                    db.close();
                }
            }
        }else{
            for(ItemController item: listItems){
                if( item.getMac().equals(addrMac) ){
                    item.setActive(isConnected(previousMac) ? CONNECTED: DISCOVERED);
                }
            }
        }
        if( devices.size() > 0 ) {
            AdapterController adapter = new AdapterController(this, listItems);
            lista.setAdapter(adapter);
        }
        else
            lista.setAdapter(null);
    }

    public class asyncTaskUpdateProgress extends AsyncTask<Void, Integer, Void> {
        int progress;
        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility( View.GONE );
        }
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progress = 0;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            if( values[0] == 1 )
                searchWifi();
            if( values[0] > 0 && values[0] % 60 == 0 )
                searchWifi();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            while (progress < 900) {
                if( attempsGetNetworks >= 15 )
                    progress = 100;
                publishProgress(progress);
                SystemClock.sleep(10);
                progress++;
            }
            return null;
        }
    }

    class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case Commands.DISCOVERED_DEVICE:
                    String[] deviceInfo = (String[])msg.obj;
                    String mac = deviceInfo[1];
                    String ip = deviceInfo[0];
                    break;
                case Commands.LIST_WIFI_NETWORKS:
                    String networkString = (String)msg.obj;
                    String[] networksIPMac = networkString.split("\\n\\r");
                    if( networksIPMac.length >= 3 ) {
                        String[] networks = new String[networksIPMac.length - 3];
                        for (int i = 0; i < networksIPMac.length - 3; i++)
                            networks[i] = networksIPMac[i];
                        if (networksIPMac[networksIPMac.length - 3].length() > 0) {
                            deviceIP = networksIPMac[networksIPMac.length - 3];
                            deviceMac = networksIPMac[networksIPMac.length - 2];
                            deviceSSID = networksIPMac[networksIPMac.length - 1];
                            newDevice(deviceIP, deviceMac, deviceSSID);
                        } else {
                            deviceIP = null;
                            deviceMac = null;
                            deviceSSID = null;
                        }
                        listWifiNetworks(networks);
                    }else{
                        listWifiNetworks( new String[1] );
                    }
            }
        }
    }
}
