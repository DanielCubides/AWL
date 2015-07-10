package dev.anzus.awlumina.util;

/**
 * Created by alej0 on 19/06/2015.
 */
import android.os.CountDownTimer;
import android.util.Log;
import java.io.IOException;
import android.content.Context;
import android.os.Handler;

import dev.anzus.awlumina.PanelActivity;

public class Commands {

    public static final int DISCOVERED_DEVICE = 111;
    public static final int LIST_WIFI_NETWORKS = 802;
    public static final int COMMAND_SUCCESS = 222;

    private UDPConnection udpc;
    public int LastOn = -1;
    public boolean sleeping = false;
    private Context myContext;
    private boolean measuring = false;
    private boolean candling = false;
    public final int[] tolerance = new int[1];
    public State appState = null;

    public Commands(Context context, Handler handler, String deviceIP) {
        udpc = new UDPConnection(context, handler, deviceIP);
        myContext = context;
        tolerance[0] = 25000;
        appState = new State(context);
    }

    public void killUDPC() {
        udpc.destroyUDPC();
    }

    public void discover() {
        try {
            udpc.sendAdminMessage("AT+Q\r".getBytes());
            Thread.sleep(150);
            udpc.sendAdminMessage("Link_Wi-Fi".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getWifiNetworks() {
        try {
            udpc.sendAdminMessage("+ok".getBytes(), true);
            Thread.sleep(150);
            udpc.sendAdminMessage("AT+WSCAN\r\n".getBytes(), true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getWifiNetworks( String addrIP ) {
        try {
            udpc.sendAdminMessage("+ok".getBytes(), true);
            Thread.sleep(100);
            udpc.sendAdminMessage("AT+WSCAN\r\n".getBytes(), true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getWan() {
        try {
            udpc.sendAdminMessage("+ok".getBytes(), true);
            Thread.sleep(100);
            udpc.sendAdminMessage("AT+WANN\r\n".getBytes(), true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setWan() {
        try {
            udpc.sendAdminMessage("+ok".getBytes(), true);
            Thread.sleep(100);
            udpc.sendAdminMessage("AT+WANN=DHCP,192.168.0.13,255.255.255.0,192.168.0.1\r\n".getBytes(), true);
            Thread.sleep(100);
            udpc.sendAdminMessage("AT+Z\r\n".getBytes(), true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setWifiNetwork(String SSID, String Security, String Type, String Password) {
        try {
            udpc.sendAdminMessage("+ok".getBytes(), true);
            Thread.sleep(100);
            udpc.sendAdminMessage(("AT+WSSSID="+SSID+"\r").getBytes(), true);
            Thread.sleep(100);
            udpc.sendAdminMessage(("AT+WSKEY="+Security+","+Type+","+Password+"\r\n").getBytes(), true);
            Thread.sleep(100);
            udpc.sendAdminMessage("AT+WMODE=STA\r\n".getBytes(), true);
            Thread.sleep(100);
            udpc.sendAdminMessage("AT+Z\r\n".getBytes(), true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setWifiNetwork(String SSID) {
        try {
            udpc.sendAdminMessage("+ok".getBytes(), true);
            Thread.sleep(100);
            udpc.sendAdminMessage(("AT+WSSSID=" + SSID + "\r").getBytes(), true);
            Thread.sleep(100);
            udpc.sendAdminMessage("AT+WMODE=STA\r\n".getBytes(), true);
            Thread.sleep(100);
            udpc.sendAdminMessage("AT+Z\r\n".getBytes(), true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void LightsOn(int group) {
        byte[] messageBA = new byte[3];
        switch(group) {
            case 0:
                messageBA[0] = 53;
                break;
            case 1:
                messageBA[0] = 56;
                break;
            case 2:
                messageBA[0] = 61;
                break;
            case 3:
                messageBA[0] = 55;
                break;
            case 4:
                messageBA[0] = 50;
                break;
        }
        messageBA[1] = 0;
        messageBA[2] = 85;
        LastOn = group;
        try {
            udpc.sendMessage(messageBA);
        } catch (IOException e) {
            e.printStackTrace();
        }
        appState.setOnOff(group, true);
    }

    public void LightsOff(int group) {
        byte[] messageBA = new byte[3];
        switch(group) {
            case 0:
                messageBA[0] = 57;
                break;
            case 1:
                messageBA[0] = 59;
                break;
            case 2:
                messageBA[0] = 51;
                break;
            case 3:
                messageBA[0] = 58;
                break;
            case 4:
                messageBA[0] = 54;
                break;
        }
        messageBA[1] = 0;
        messageBA[2] = 85;
        try {
            udpc.sendMessage(messageBA);
        } catch (IOException e) {
            e.printStackTrace();
        }
        appState.setOnOff(group, false);
    }

    public void setBrightnessUpOne() {
        byte[] messageBA = new byte[3];
        messageBA[0] = 60;
        messageBA[1] = 0;
        messageBA[2] = 85;
        try {
            udpc.sendMessage(messageBA);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBrightnessDownOne() {
        byte[] messageBA = new byte[3];
        messageBA[0] = 52;
        messageBA[1] = 0;
        messageBA[2] = 85;
        try {
            udpc.sendMessage(messageBA);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setWarmthUpOne() {
        byte[] messageBA = new byte[3];
        messageBA[0] = 62;
        messageBA[1] = 0;
        messageBA[2] = 85;
        try {
            udpc.sendMessage(messageBA);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setWarmthDownOne() {
        byte[] messageBA = new byte[3];
        messageBA[0] = 63;
        messageBA[1] = 0;
        messageBA[2] = 85;
        try {
            udpc.sendMessage(messageBA);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int setOn(int group, int arg){
        int startPanel = arg;
        final int groupFinal = group;
        if( startPanel == group ) {
            new CountDownTimer(120, 10) {
                public void onFinish() {
                    LightsOn(groupFinal);
                    new CountDownTimer(150, 10) {
                        public void onFinish() {
                            LightsOn(groupFinal);
                        }
                        public void onTick(long millisUntilFinished) {}
                    }.start();
                }
                public void onTick(long millisUntilFinished) {}
            }.start();
            startPanel = PanelActivity.NO_GROUP;
        }
        else{
            new CountDownTimer(150, 10) {
                public void onFinish() {
                    LightsOn(groupFinal);
                }
                public void onTick(long millisUntilFinished) {}
            }.start();
        }
        return startPanel;
    }

    public void setOff(int group){
        final int arg = group;
        new CountDownTimer(150, 10) {
            public void onFinish() { LightsOff(arg); }
            public void onTick(long millisUntilFinished) {}
        }.start();
    }

    public int bright(int group, int arg, boolean up){
        int startPanel = arg;
        final int groupFinal = group;
        final boolean isUp = up;
        if( startPanel == group ) {
            new CountDownTimer(120, 10) {
                public void onFinish() {
                    LightsOn(groupFinal);
                    new CountDownTimer(150, 10) {
                        public void onFinish() {
                            if( isUp )
                                setBrightnessUpOne();
                            else
                                setBrightnessDownOne();
                        }
                        public void onTick(long millisUntilFinished) {}
                    }.start();
                }
                public void onTick(long millisUntilFinished) {}
            }.start();
            startPanel = PanelActivity.NO_GROUP;
        }
        else{
            new CountDownTimer(150, 10) {
                public void onFinish() {
                    if( isUp )
                        setBrightnessUpOne();
                    else
                        setBrightnessDownOne();
                }
                public void onTick(long millisUntilFinished) {}
            }.start();
        }
        return startPanel;
    }

    public int warmth(int group, int arg, boolean up){
        int startPanel = arg;
        final int groupFinal = group;
        final boolean isUp = up;
        if( startPanel == group ) {
            new CountDownTimer(120, 10) {
                public void onFinish() {
                    LightsOn(groupFinal);
                    new CountDownTimer(150, 10) {
                        public void onFinish() {
                            if( isUp )
                                setWarmthUpOne();
                            else
                                setWarmthDownOne();
                        }
                        public void onTick(long millisUntilFinished) {}
                    }.start();
                }
                public void onTick(long millisUntilFinished) {}
            }.start();
            startPanel = PanelActivity.NO_GROUP;
        } else{
            new CountDownTimer(150, 10) {
                public void onFinish() {
                    if( isUp )
                        setWarmthUpOne();
                    else
                        setWarmthDownOne();
                }
                public void onTick(long millisUntilFinished) {}
            }.start();
        }
        return startPanel;
    }

    public void addBulb(int group){
        final int arg = group;
        new CountDownTimer(150, 10) {
            public void onFinish() {
                LightsOn(arg);
                new CountDownTimer(150, 10) {
                    public void onFinish() {
                        LightsOn(arg);
                    }
                    public void onTick(long millisUntilFinished) {}
                }.start();
            }
            public void onTick(long millisUntilFinished) {}
        }.start();
    }

    public void removeBulb(int group){
        final int arg = group;
        new CountDownTimer(120, 10) {
            public void onFinish() {
                LightsOn( arg );
                new CountDownTimer(150, 10) {
                    public void onFinish() {
                        LightsOn( arg );
                        new CountDownTimer(150, 10) {
                            public void onFinish() {
                                LightsOn( arg );
                                new CountDownTimer(150, 10) {
                                    public void onFinish() {
                                        LightsOn( arg );
                                        new CountDownTimer(150, 10) {
                                            public void onFinish() {
                                                LightsOn( arg );
                                            }
                                            public void onTick(long millisUntilFinished) {}
                                        }.start();
                                    }
                                    public void onTick(long millisUntilFinished) {}
                                }.start();
                            }
                            public void onTick(long millisUntilFinished) {}
                        }.start();
                    }
                    public void onTick(long millisUntilFinished) {}
                }.start();
            }
            public void onTick(long millisUntilFinished) {}
        }.start();
    }

}

