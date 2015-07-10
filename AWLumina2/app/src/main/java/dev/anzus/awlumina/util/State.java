package dev.anzus.awlumina.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by alej0 on 19/06/2015.
 */
public class State {

    private Context myContext;
    private SharedPreferences state = null;

    public State(Context context) {
        myContext = context;
        state = myContext.getSharedPreferences("app_state", Context.MODE_PRIVATE);
    }

    public void setOnOff(int group, boolean On) {
        state.edit().putBoolean(group+"-power", On).apply();
    }

    public boolean getOnOff(int group) {
        return state.getBoolean(group+"-power", false);
    }

    public void setBrighness(int group, int level) {
        state.edit().putInt(group+"-brightness", level).apply();
    }

    public int getBrightness(int group) {
        return state.getInt(group+"-brightness", 30);
    }

    public void setColor(int group, int Color) {
        state.edit().putInt(group+"-color", Color).apply();
    }

    public void removeColor(int group) {
        state.edit().remove(group+"-color").apply();
    }

    public int getColor(int group) {
        if(state.contains(group+"-color")) {
            return state.getInt(group+"-color", 0);
        }
        return 0;
    }
}

