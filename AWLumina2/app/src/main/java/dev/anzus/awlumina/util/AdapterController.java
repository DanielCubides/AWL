package dev.anzus.awlumina.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import android.widget.ImageView;
import android.widget.TextView;

import dev.anzus.awlumina.R;

/**
 * Created by alej0 on 18/06/2015.
 */
public class AdapterController extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<ItemController> items;

    public AdapterController(Activity activity, ArrayList<ItemController> items){
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount(){
        return items.size();
    }

    @Override
    public Object getItem(int pos){
        return items.get(pos);
    }

    @Override
    public long getItemId(int pos){
        return items.get(pos).getNumero();
    }

    @Override
    public View getView( int pos, View convertView, ViewGroup parent ){
        View v = convertView;
        if( convertView == null ){
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.controller, null);
        }

        ItemController itemC = items.get( pos );

        Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Black.ttf");

        TextView cajaAW = (TextView) v.findViewById(R.id.cajaAWLumina);
        cajaAW.setTypeface( typeface );
        cajaAW.setText( itemC.getName() );

        TextView cajaTexto = (TextView) v.findViewById(R.id.cajaControlador);
        cajaTexto.setTypeface( typeface );
        cajaTexto.setText( itemC.getMac() );

        ImageView signal = (ImageView) v.findViewById(R.id.imageViewSignal);
        ImageView router = (ImageView) v.findViewById(R.id.imageViewRouter);
        if( itemC.getActiveState() == itemC.CONNECTED )
            signal.setImageResource(R.drawable.controller_on);
        else
            signal.setImageResource(R.drawable.controller_off);
        if( itemC.getActiveState() == itemC.UNDISCOVERED )
            router.setImageResource(R.drawable.router_off);
        else
            router.setImageResource(R.drawable.router_on);
        return v;
    }
}
