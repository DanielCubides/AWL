package dev.anzus.awlumina;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ViewFlipper;


public class InstructionsActivity extends ActionBarActivity {

    ViewFlipper viewFlipper;
    public float init_x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        viewFlipper.setOnTouchListener(new ListenerTouchViewFlipper());
        this.setTitle(R.string.menu_instrucciones);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_instrucciones, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private class ListenerTouchViewFlipper implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    init_x = event.getX();
                    return true;
                case MotionEvent.ACTION_UP:
                    float distance = init_x - event.getX();
                    if(distance > 80)
                        viewFlipper.showNext();
                    if(distance < -80)
                        viewFlipper.showPrevious();
                default:
                    break;
            }
            return false;
        }
    }

}
