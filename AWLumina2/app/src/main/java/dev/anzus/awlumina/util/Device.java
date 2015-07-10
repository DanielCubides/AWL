package dev.anzus.awlumina.util;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by alej0 on 19/06/2015.
 */
public class Device{

    ArrayList<String> devicesIPs = new ArrayList<>();
    ArrayList<String> devicesMacs = new ArrayList<>();

    public Device(){

    }

    public Device(ArrayList<String> devicesIPs, ArrayList<String> devicesMacs){
        this.devicesIPs = devicesIPs;
        this.devicesMacs = devicesMacs;
    }

    public ArrayList<String> getDevicesIPs(){
        return this.devicesIPs;
    }

    public ArrayList<String> getDevicesMacs(){
        return this.devicesMacs;
    }

    public void addItem( String deviceIp, String deviceMac){
        this.devicesIPs.add( deviceIp );
        this.devicesMacs.add( deviceMac );
    }

    public boolean containsAddr( String deviceMac ){
        for( String addrMac: this.devicesMacs ) {
            if( addrMac.equals(deviceMac) )
                return true;
        }
        return false;
    }

    public int size(){
        if( devicesMacs.size() == devicesIPs.size() )
            return devicesMacs.size();
        else
            clear();
        return 0;
    }

    public void clear(){
        this.devicesIPs.clear();
        this.devicesMacs.clear();
    }
}

