package dev.anzus.awlumina.util;

/**
 * Created by alej0 on 18/06/2015.
 */
public class NetItem {

    private int range = 0;
    private String ssid = "";

    public NetItem(String ssid, int range){
        this.ssid = ssid;
        this.range = range;
    }

    public int getRange(){
        return this.range;
    }

    public void setRange(int range){
        this.range = range;
    }

    public String getSsid(){
        return this.ssid;
    }

    public void setSsid( String ssid ){
        this.ssid = ssid;
    }

    @Override
    public String toString() {
        return this.ssid + "  -  " + this.range;
    }

    public boolean equals( Object obj ){
        if( obj instanceof  NetItem ){
            NetItem item = (NetItem) obj;
            return item.getSsid().equals( this.getSsid() ) && item.getRange() == this.getRange();
        }else
            return false;

    }
}
