package dev.anzus.awlumina.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dev.anzus.awlumina.R;

/**
 * Created by alej0 on 18/06/2015.
 */
public class NetAdapter extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<NetItem> items;
    protected ArrayList<NetItem> removedItems;

    public NetAdapter(Activity activity, ArrayList<NetItem> items){
        this.activity = activity;
        this.items = items;
        removedItems = new ArrayList<NetItem>();
    }

    @Override
    public int getCount(){
        return items.size() - removedItems.size();
    }

    @Override
    public Object getItem(int pos){
        return items.get(pos);
    }

    public void getItemsToRemove(int pos){
        if( pos < items.size() )
            removedItems.add( items.get( pos ) );
    }

    public void removeFromAdapter(){
        if( items.size() > 0 ) {
            for ( NetItem item : removedItems ) {
                int deleteIndex = items.indexOf( item );
                if( deleteIndex > -1 )
                    items.remove( deleteIndex );
            }
        }
    }

    public void restore(){
        if( removedItems.size() > 0 ){
            for( int i = 0; i < removedItems.size(); i++){
                items.add( removedItems.remove(i) );
            }
        }
    }

    @Override
    public long getItemId(int pos){
        return items.indexOf(items.get(pos));
    }

    @Override
    public View getView( int pos, View convertView, ViewGroup parent ){
        View v = convertView;
        if( convertView == null ){
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.red, null);
        }

        NetItem itemR = items.get( pos );
        Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Black.ttf");

        TextView cajaRed = (TextView) v.findViewById(R.id.cajaRed);
        cajaRed.setTypeface( typeface );
        cajaRed.setText( itemR.getSsid() );

        ImageView quality = (ImageView) v.findViewById(R.id.imageViewRed);
        if( itemR.getRange() == 0 ){
            quality.setImageResource(R.drawable.wifinone);
        }
        if( itemR.getRange() > 0 && itemR.getRange() <= 33 ){
            quality.setImageResource(R.drawable.wifilow);
        }
        if( itemR.getRange() > 33 && itemR.getRange() <= 66 ){
            quality.setImageResource(R.drawable.wifimedium);
        }
        if( itemR.getRange() > 66 ){
            quality.setImageResource(R.drawable.wififull);
        }
        return v;
    }
}
