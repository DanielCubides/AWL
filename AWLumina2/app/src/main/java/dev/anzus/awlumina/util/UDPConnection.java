package dev.anzus.awlumina.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * Created by alej0 on 19/06/2015.
 */
public class UDPConnection {

    public static String CONTROLLERIP = "";
    public static int CONTROLLERPORT = 0;
    public static final int CONTROLLERADMINPORT = 48899;
    private Utils utils;
    private UDP_Server server = null;
    private SharedPreferences prefs;
    private static Context myContext;
    private static Handler myHandler;
    private String networkBroadCast;
    private String deviceIP = "";

    private boolean onlineMode = false;

    public UDPConnection(Context context, Handler handler, String deviceIP) {
        myContext = context;
        myHandler = handler;
        utils = new Utils(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        this.deviceIP = deviceIP;

        networkBroadCast = "255.255.255.255";
        try {
            networkBroadCast = utils.getWifiIP(utils.BROADCAST_ADDRESS);
        } catch (ConnectionException e) {
            e.printStackTrace();
            return;
        }
    }

    public void setOnlineMode(boolean online) {
        onlineMode = online;
    }

    public void sendMessage(byte[] Bytes) throws IOException {
        if( utils.validIP(deviceIP)){
            CONTROLLERIP = prefs.getString("pref_light_controller_ip", deviceIP);
            CONTROLLERPORT = Integer.parseInt(prefs.getString("pref_light_controller_port", "8899"));
            DatagramSocket s = new DatagramSocket();
            InetAddress controller = InetAddress.getByName(CONTROLLERIP);
            DatagramPacket p = new DatagramPacket(Bytes, 3, controller, CONTROLLERPORT);
            s.send(p);
        }else{
            if(!onlineMode) {
                CONTROLLERIP = prefs.getString("pref_light_controller_ip", networkBroadCast);
                CONTROLLERPORT = Integer.parseInt(prefs.getString("pref_light_controller_port", "8899"));
                DatagramSocket s = new DatagramSocket();
                InetAddress controller = InetAddress.getByName(CONTROLLERIP);
                DatagramPacket p = new DatagramPacket(Bytes, 3, controller, CONTROLLERPORT);
                s.send(p);
            } else {
                //send message in online mode;
            }
        }
    }

    public void sendAdminMessage(byte[] Bytes) throws IOException {
        sendAdminMessage(Bytes, false);
    }

    public void sendAdminMessage(byte[] Bytes, Boolean Device) throws IOException {
        if(server == null) {
            server = new UDP_Server();
            server.runUdpServer();
        } else if(!server.Server_aktiv) {
            server.runUdpServer();
        }
        networkBroadCast = null;
        if(Device) {
            CONTROLLERIP = prefs.getString("pref_light_controller_ip", "255.255.255.255");
            networkBroadCast = CONTROLLERIP;
        } else {
            try {
                networkBroadCast = utils.getWifiIP(utils.BROADCAST_ADDRESS);
            } catch (ConnectionException e) {
                e.printStackTrace();
                return;
            }
        }
        DatagramSocket s = new DatagramSocket();
        InetAddress controller = InetAddress.getByName(networkBroadCast);
        DatagramPacket p = new DatagramPacket(Bytes, Bytes.length, controller, CONTROLLERADMINPORT);
        s.setBroadcast(true);
        s.send(p);
    }

    public void destroyUDPC() {
        if(server != null) {
            server.stop_UDP_Server();
        }
    }

    class UDP_Server {
        private AsyncTask<Void, Void, Void> async;
        public boolean Server_aktiv = true;

        @SuppressLint("NewApi")
        public void runUdpServer() {
            Server_aktiv = true;
            async = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    byte[] lMsg = new byte[1024];
                    DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
                    DatagramSocket ds = null;
                    try {
                        ds = new DatagramSocket(UDPConnection.CONTROLLERADMINPORT);
                        ds.setSoTimeout(1000);
                        while (Server_aktiv) {
                            try {
                                ds.receive(dp);
                                String data = new String(dp.getData());
                                Log.d("UDPmensaje0", " " + data);
                                if(data.startsWith("+ok")) {
                                    if(data.startsWith("+ok=")) {
                                        Message m = new Message();
                                        m.what = Commands.LIST_WIFI_NETWORKS;
                                        String deviceMac = null;
                                        String deviceAddr = dp.getAddress().getHostAddress();
                                        deviceMac = utils.getMacFromArpCache(deviceAddr);
                                        if( deviceMac != null ){
                                            if( utils.getIpFromArpCache( deviceMac ).equals( deviceAddr ) ) {
                                                Log.d("new device ---- ", deviceAddr + " --- " + deviceMac + " --- " + utils.getSSID());
                                                m.obj = data + "\n\r" + deviceAddr + "\n\r" + deviceMac + "\n\r" + utils.getSSID();
                                            }
                                            else
                                                m.obj = data + "\n\r" + "\n\r" + "\n\r";
                                        }else
                                            m.obj = data + "\n\r" + "\n\r" + "\n\r";
                                        myHandler.sendMessage(m);
                                        Server_aktiv = false;
                                    } else {
                                        Message m = new Message();
                                        m.what = Commands.COMMAND_SUCCESS;
                                        m.obj = data;
                                        myHandler.sendMessage(m);
                                        Server_aktiv = false;
                                    }
                                } else {
                                    String[] parts = data.split(",");
                                    if (parts.length > 1) {
                                        if (utils.validIP(parts[0]) && utils.validMac(parts[1])) {
                                            Message m = new Message();
                                            m.what = Commands.DISCOVERED_DEVICE;
                                            m.obj = parts;
                                            myHandler.sendMessage(m);
                                            Server_aktiv = false;
                                        }
                                    }
                                }
                            } catch(SocketTimeoutException e) {
                                //no problem
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (ds != null) {
                            ds.close();
                        }
                    }
                    return null;
                }
            };
            if (Build.VERSION.SDK_INT >= 11)
                async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else async.execute();
        }
        public void stop_UDP_Server() {
            Server_aktiv = false;
        }
    }

}