package dev.anzus.awlumina;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Black.ttf");

        TextView version = (TextView) findViewById(R.id.textView);
        TextView link = (TextView) findViewById(R.id.textView2);
        version.setTypeface( typeface );
        link.setTypeface( typeface );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_acerca, menu);
        return true;
    }

    public void onBackPressed(){
        /*Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);*/
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
