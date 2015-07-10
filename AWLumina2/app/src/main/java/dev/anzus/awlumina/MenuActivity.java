package dev.anzus.awlumina;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MenuActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Black.ttf");

        TextView menuControladores = (TextView) findViewById(R.id.menuControladores);
        menuControladores.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ControllersActivity.class);
                startActivity(intent);
            }
        });

        menuControladores.setTypeface( typeface );

        TextView menuPanel = (TextView) findViewById(R.id.menuPanel);
        menuPanel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent( MenuActivity.this, PanelActivity.class);
                startActivity(intent);
            }
        });

        menuPanel.setTypeface( typeface );

        TextView menuInstrucciones = (TextView) findViewById(R.id.menuInstrucciones);
        menuInstrucciones.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent( MenuActivity.this, InstructionsActivity.class);
                startActivity(intent);
            }
        });

        menuInstrucciones.setTypeface( typeface );

        TextView menuAcerca = (TextView) findViewById(R.id.menuAcerca);
        menuAcerca.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent( MenuActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        menuAcerca.setTypeface( typeface );

    }

    public void onBackPressed(){
        finish();
    }

    @Override
    protected void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
