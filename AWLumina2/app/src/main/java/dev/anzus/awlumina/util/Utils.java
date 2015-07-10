package dev.anzus.awlumina.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.util.Patterns;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by alej0 on 19/06/2015.
 */
public class Utils {

    public final static int IP_ADDRESS = 0;
    public final static int BROADCAST_ADDRESS = 1;

    private Context myContext;

    public Utils(Context context) {
        myContext = context;
    }

    public boolean isWifiConnection() {
        ConnectivityManager connManager = (ConnectivityManager) myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }

    public String getSSID(){
        String ssid = "";
        if(isWifiConnection()) {
            WifiManager wifi = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            ssid = info.getSSID();
            return ssid.replace("\"", "");
        }
        return ssid;
    }

    public String getDeviceIP(){
        if(isWifiConnection()) {
            WifiManager wifi = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
            DhcpInfo dhcp = wifi.getDhcpInfo();
            return intToIp(dhcp.ipAddress);
        }
        return "";
    }

    public String getMacFromArpCache(String ip) {
        if (ip == null)
            return null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4 && ip.equals(splitted[0])) {
                    // Basic sanity check
                    String mac = splitted[3];
                    if ( mac.matches("..:..:..:..:..:..") )
                        return mac;
                    else
                        return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getIpFromArpCache(String mac) {
        if (mac == null)
            return "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4 && mac.equals(splitted[3])) {
                    // Basic sanity check
                    String ip = splitted[0];
                    if( validIP(ip) )
                        return ip;
                    else
                        return "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getWifiIP(int type) throws ConnectionException {
        if(isWifiConnection()) {
            WifiManager wifi = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
            DhcpInfo dhcp = wifi.getDhcpInfo();
            switch(type) {
                case BROADCAST_ADDRESS :
                    System.setProperty("java.net.preferIPv4Stack", "true");
                    try {
                        for(Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements();) {
                            NetworkInterface ni = niEnum.nextElement();
                            if (!ni.isLoopback()) {
                                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                                    if(interfaceAddress.getBroadcast() != null) {
                                        return interfaceAddress.getBroadcast().toString().substring(1);
                                    }
                                }
                            }
                        }
                    }catch (SocketException e) {
                        throw new ConnectionException("Cant get Address", ConnectionException.CANT_GET_ADDRESS);
                    }
                    return null;
                case IP_ADDRESS :
                    byte[] bytes = BigInteger.valueOf(dhcp.ipAddress).toByteArray();
                    try {
                        InetAddress address = InetAddress.getByAddress(bytes);
                        return address.getHostAddress();
                    } catch (UnknownHostException e) {
                        throw new ConnectionException("Cant get Address", ConnectionException.CANT_GET_ADDRESS);
                    }

            }
        } else {
            throw new ConnectionException("Wifi not connected", ConnectionException.WIFI_NOT_CONNECTED);
        }
        return null;
    }

    public String intToIp(int addr) {
        return  ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
    }

    public String getWifiName() {
        WifiManager wifi = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
        return wifi.getConnectionInfo().getSSID().substring(1, wifi.getConnectionInfo().getSSID().length() - 1);
    }

    public String getWifiMac() {
        WifiManager wifi = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
        return wifi.getConnectionInfo().getBSSID();
    }

    public boolean validIP(String ip) {
        if (ip == null || ip.isEmpty()) return false;
        ip = ip.trim();
        if ((ip.length() < 6) & (ip.length() > 15)) return false;

        try {
            Pattern pattern = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
            Matcher matcher = pattern.matcher(ip);
            return matcher.matches();
        } catch (PatternSyntaxException ex) {
            return false;
        }
    }

    public static boolean validHost(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    public boolean validMac(String MAC) {
        if (MAC == null || MAC.isEmpty()) return false;
        MAC = MAC.trim();
        if(MAC.equals("000000000000")) return false;
        if (MAC.length() != 12) return false;

        try {
            Pattern pattern = Pattern.compile("^([0-9A-F]{2}){6}$");
            Matcher matcher = pattern.matcher(MAC);
            return matcher.matches();
        } catch (PatternSyntaxException ex) {
            return false;
        }
    }

}

