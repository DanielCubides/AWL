package dev.anzus.awlumina;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import dev.anzus.awlumina.util.NetAdapter;
import dev.anzus.awlumina.util.Commands;
import dev.anzus.awlumina.util.NetItem;
import dev.anzus.awlumina.util.Utils;


public class LinkActivity extends ActionBarActivity {

    private static final int POS_SSID = 1;
    private static final int POS_SECURITY = 3;
    private static final int POS_SIGNAL = 4;

    ListView list;
    Commands sendCommand;
    public MyHandler myHandler = null;
    private static Context context;

    ProgressBar pBNetworks, pBConnection;
    View rle1, rle2;
    int attempsGetNetworks = 0;
    String deviceIP = null;
    ArrayList<String> networksName = new ArrayList<>();
    ArrayList<NetItem> listItems;
    ArrayList<NetItem> listRemoveItems;
    String netPassword;
    String mac = "-";
    String ip = "-";
    int attemps = 0;

    Utils util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);



        final Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Black.ttf");

        this.setTitle(R.string.title_activity_enlace);

        listItems = new ArrayList<NetItem>();
        listRemoveItems = new ArrayList<NetItem>();
        rle1 = (View)findViewById(R.id.rle1);
        rle2 = (View)findViewById(R.id.rle2);
        pBNetworks = (ProgressBar)findViewById(R.id.pBNetworks);
        pBConnection = (ProgressBar)findViewById(R.id.pBConnection);
        pBNetworks.setVisibility( View.GONE );
        pBConnection.setVisibility( View.GONE );

        mac = getIntent().getStringExtra("MAC");
        ip = getIntent().getStringExtra("IP");

        list = (ListView)findViewById(R.id.listViewRedes);
        list.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                Adapter adapter = list.getAdapter();
                if (listItems.size() > 0) {
                    final String title = (String) adapter.getItem(position).toString();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(title.split("-")[0]);

                    final EditText input = new EditText(context);

                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    input.setTypeface( typeface );
                    input.setHint(R.string.clave);
                    builder.setView(input);

                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            netPassword = input.getText().toString();
                            int position = networksName.indexOf(title);
                            linkWifi(position);
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();

                }
            }

        }));

        final EditText editNetworks = (EditText) findViewById(R.id.cajaRedes);
        editNetworks.setTypeface( typeface );
        editNetworks.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                NetAdapter adapter = (NetAdapter) list.getAdapter();
                restoreList();
                if (listItems.size() > 0 && editNetworks.getText().length() > 0) {
                    for (int i = 0; i < listItems.size(); i++) {
                        if (!((NetItem) adapter.getItem(i)).getSsid().toLowerCase().contains(editNetworks.getText().toString().toLowerCase())) {
                            getItemsToRemove(i);
                        }
                    }
                    removeFromList();
                    NetAdapter newAdapter = new NetAdapter((Activity) context, listItems);
                    list.setAdapter(newAdapter);
                }
            }
        });
        list.requestFocus();
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
    }

    public void onBackPressed(){
        /*Intent intent = new Intent(this, ControllersActivity.class);
        startActivity(intent);*/
        finish();
    }

    @Override
    protected void onDestroy() {
        //Intent intent = new Intent(this, ControllersActivity.class);
        //startActivity(intent);
        super.onDestroy();
    }

    public void verifyConnection( boolean state ){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        if( state ) {
            toast.makeText(getApplicationContext(), R.string.conectado, Toast.LENGTH_LONG).show();
            new CountDownTimer(2000, 10) {
                public void onFinish() {
                    finish();
                    /*Intent intent = new Intent(context, ControllersActivity.class);
                    startActivity(intent);*/
                }
                public void onTick(long millisUntilFinished) {
                }
            }.start();
        }
        else
            toast.makeText(getApplicationContext(), R.string.noConectado, Toast.LENGTH_LONG).show();
    }
    
    private void getItemsToRemove( int pos ) {
        if (pos < listItems.size())
            listRemoveItems.add(listItems.get(pos));
    }

    private void removeFromList(){
        if( listRemoveItems.size() > 0 ) {
            for (NetItem item : listRemoveItems) {
                int deleteIndex = listItems.indexOf( item );
                if( deleteIndex >= 0 )
                    listItems.remove( deleteIndex );
            }
        }
    }

    private void restoreList(){
        if( listRemoveItems.size() > 0 ) {
            for (int i = 0; i < listRemoveItems.size(); i++) {
                listItems.add( listRemoveItems.remove(i) );
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_enlace, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.btnActualizar:
                attemps++;
                if( attemps > 1 ) {
                    startActivity(getIntent());
                }else
                    startThread();
                return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startThread(){
        startActions();
        new asyncTaskUpdateNetworks().execute();
    }

    private void searchWifi(){
        attemptDiscovery();
        new CountDownTimer(100, 10) {
            public void onFinish() {
                sendCommand.getWifiNetworks();
            }
            public void onTick(long millisUntilFinished) {}
        }.start();
    }

    private void linkWifi(int position ){
        if( position >= 0 && position < listItems.size() ) {
            String ssid = listItems.get(position).getSsid();
            if (netPassword != null) {
                if (netPassword.length() > 0)
                    sendCommand.setWifiNetwork(ssid, "WPA2PSK", "AES", netPassword);
                else
                    sendCommand.setWifiNetwork(ssid);
            }
        }
        new asyncTaskUpdateConnection().execute();
    }

    private void attemptDiscovery() {
        sendCommand.discover();
    }

    private void listWifiNetworks(final String[] networks) {
        listItems.clear();
        if( networks.length > 3 ) {
            String[] showNetworks = new String[networks.length - 3];
            for (int i = 2; i < networks.length - 1; i++) {
                String[] networkInfo = networks[i].split(",");
                showNetworks[i - 2] = networkInfo[POS_SSID] + "," + networkInfo[POS_SECURITY] + "," + networkInfo[POS_SIGNAL];
                NetItem newItem = new NetItem( showNetworks[i - 2].split(",")[0], Integer.parseInt(showNetworks[i - 2].split(",")[2]) );
                if( listItems.indexOf( newItem ) < 0){
                    listItems.add( newItem );
                }
            }
            for( NetItem network: listItems){
                networksName.add( network.toString() );
            }
            if( listItems.size() > 0 ) {
                NetAdapter adapter = new NetAdapter(this, listItems);
                list.setAdapter( adapter );
            }
            else
                list.setAdapter(null);
        }else{
            if( attempsGetNetworks < 5 ) {
                sendCommand.getWifiNetworks();
                attempsGetNetworks++;
            }
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
                    String[] networksIP = networkString.split("\\n\\r");
                    String[] networks = new String[networksIP.length - 3];
                    for(int i = 0; i < networksIP.length - 3; i++)
                        networks[i] = networksIP[i];
                    if( networksIP[networksIP.length - 3].length() > 0 ) {
                        deviceIP = networksIP[networksIP.length - 3];
                    }
                    else
                        deviceIP = null;
                    listWifiNetworks(networks);
            }
        }
    }

    public boolean isConnected(){
        return !util.getIpFromArpCache(mac).equals(ip);
    }

    public class asyncTaskUpdateNetworks extends AsyncTask<Void, Integer, Void> {
        int progress;
        @Override
        protected void onPostExecute(Void result) {
            pBNetworks.setVisibility( View.GONE );
            list.setVisibility( View.VISIBLE );
        }
        @Override
        protected void onPreExecute() {
            pBNetworks.setVisibility( View.VISIBLE );
            list.setVisibility( View.GONE );
            progress = 0;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            pBNetworks.setProgress(values[0]);
            if( values[0] == 1 )
                searchWifi();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            while (progress < 300) {
                publishProgress(progress);
                SystemClock.sleep(20);
                progress++;
            }
            return null;
        }
    }

    public class asyncTaskUpdateConnection extends AsyncTask<Void, Integer, Void> {
        int progress;
        boolean connected = false;
        @Override
        protected void onPostExecute(Void result) {
            pBConnection.setVisibility( View.GONE );
            list.setVisibility( View.VISIBLE );
            rle1.setVisibility( View.VISIBLE );
            rle2.setVisibility( View.VISIBLE );
            verifyConnection( connected );
        }
        @Override
        protected void onPreExecute() {
            pBConnection.setVisibility( View.VISIBLE );
            list.setVisibility( View.GONE );
            rle1.setVisibility( View.GONE );
            rle2.setVisibility( View.GONE );
            progress = 0;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            pBConnection.setProgress(values[0]);
            if(  !isConnected() )
                progress += 1;
            else{
                progress += 300;
                connected = true;
            }
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            while (progress < 300) {
                publishProgress(progress);
                SystemClock.sleep(100);
            }
            return null;
        }
    }
}

