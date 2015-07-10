package dev.anzus.awlumina.util;

/**
 * Created by alej0 on 19/06/2015.
 */
public class NetworkInformation {

    private String netSSID = "";
    private String netBSSID = "";
    private String netSecurity = "";
    private String netSignal = "";
    private String netExtCH = "";
    private String netWPS = "";
    private String netDPID = "";
    private String netPartSecurity = "";
    private String netPartType = "";

    public NetworkInformation( String SSID, String BSSID, String security, String signal, String extCH, String WPS, String DPID ){
        this.netSSID = SSID;
        this.netBSSID = BSSID;
        this.netSecurity = security;
        this.netSignal = signal;
        this.netExtCH = extCH;
        this.netWPS = WPS;
        this.netDPID = DPID;
        if(this.netSecurity.length() > 0) {
            if (this.netSecurity.split("/").length > 1) {
                this.netPartSecurity = this.netSecurity.split("/")[0];
                this.netPartType = this.netSecurity.split("/")[1];
            }
        }
    }

    public NetworkInformation( String SSID, String security, String signal ){
        this.netSSID = SSID;
        this.netSecurity = security;
        this.netSignal = signal;
        if(this.netSecurity.length() > 0) {
            if (this.netSecurity.split("/").length > 1) {
                this.netPartSecurity = this.netSecurity.split("/")[0];
                this.netPartType = this.netSecurity.split("/")[1];
            }
        }
    }

    public String getNetPartSecurity() {
        return netPartSecurity;
    }

    public void setNetPartSecurity(String netPartSecurity) {
        this.netPartSecurity = netPartSecurity;
    }

    public String getNetDPID() {
        return netDPID;
    }

    public void setNetDPID(String netDPID) {
        this.netDPID = netDPID;
    }

    public String getNetWPS() {
        return netWPS;
    }

    public void setNetWPS(String netWPS) {
        this.netWPS = netWPS;
    }

    public String getNetExtCH() {
        return netExtCH;
    }

    public void setNetExtCH(String netExtCH) {
        this.netExtCH = netExtCH;
    }

    public String getNetSignal() {
        return netSignal;
    }

    public void setNetSignal(String netSignal) {
        this.netSignal = netSignal;
    }

    public String getNetSecurity() {
        return netSecurity;
    }

    public void setNetSecurity(String netSecurity) {
        this.netSecurity = netSecurity;
    }

    public String getNetBSSID() {
        return netBSSID;
    }

    public void setNetBSSID(String netBSSID) {
        this.netBSSID = netBSSID;
    }

    public String getNetPartType() {
        return netPartType;
    }

    public void setNetPartType(String netPartType) {
        this.netPartType = netPartType;
    }

    public String getNetSSID() {
        return netSSID;
    }

    public void setNetSSID(String netSSID) {
        this.netSSID = netSSID;
    }

    @Override
    public String toString(){
        return this.netSSID + "  -  " + this.netSignal;
    }

    public String toStringLink(){
        return this.netSSID + "," + this.netPartSecurity + "," + this.netPartType;
    }

}

