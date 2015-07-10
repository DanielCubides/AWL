package dev.anzus.awlumina.util;

/**
 * Created by alej0 on 18/06/2015.
 */
public class ItemController {

    public final int UNDISCOVERED = 0, DISCOVERED = 1, CONNECTED = 2;

    private int numero = 0;
    private String mac = "";
    private String previousMac = "";
    private String name = "";
    private int active = UNDISCOVERED;


    public ItemController(int numero, String mac, String previousMac, String name, int active){
        this.numero = numero;
        this.mac = mac;
        this.previousMac = previousMac;
        this.name = name;
        this.active = active;
    }

    public int getActiveState(){
        return this.active;
    }

    public void setActive( int active ){
        this.active = active;
    }

    public int getNumero(){
        return this.numero;
    }

    public String getMac(){
        return this.mac;
    }

    public String getPreviousMac(){
        return this.previousMac;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

}
