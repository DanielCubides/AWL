package dev.anzus.awlumina;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import dev.anzus.awlumina.util.AdapterController;
import dev.anzus.awlumina.util.Commands;
import dev.anzus.awlumina.util.ControllersSqLiteHelper;
import dev.anzus.awlumina.util.ItemController;


public class PanelActivity extends ActionBarActivity {

    public static final int  ON = 1, OFF = 2, BRIGHT_UP = 3, BRIGHT_DOWN = 4, WARMTH_UP = 5, WARMTH_DOWN = 6, ADD = 7, REMOVE = 8;
    public static final int GROUP1 = 1, GROUP2 = 2, GROUP3 = 3, GROUP4 = 4, ALL_LIGHTS = 0, NO_GROUP = 6;
    public static final String[] GROUPS = {"", "group1", "group2", "group3", "group4"};
    ViewFlipper viewFlipper;
    public float init_x;
    int activeWindow = 0, startPanel = 0;

    String name = "Aw Lumina";
    String deviceIP = "";
    String deviceMac = "";

    Commands sendCommand;
    public MyHandler myHandler = null;
    private static Context context;

    ControllersSqLiteHelper controllersDBH;
    SQLiteDatabase db;

    ImageView btnLuzAzulGG1, btnLuzAmarillaGG1, btnBrilloUpGG1, btnBrilloDownGG1,
              btnOnGGG1, btnOffGGG1, btnAnadirGG1, btnSuprimirGG1;

    ImageView btnLuzAzulGG2, btnLuzAmarillaGG2, btnBrilloUpGG2, btnBrilloDownGG2,
              btnOnGGG2, btnOffGGG2, btnAnadirGG2, btnSuprimirGG2;

    ImageView btnLuzAzulGG3, btnLuzAmarillaGG3, btnBrilloUpGG3, btnBrilloDownGG3,
              btnOnGGG3, btnOffGGG3, btnAnadirGG3, btnSuprimirGG3;

    ImageView btnLuzAzulGG4, btnLuzAmarillaGG4, btnBrilloUpGG4, btnBrilloDownGG4,
              btnOnGGG4, btnOffGGG4, btnAnadirGG4, btnSuprimirGG4;

    ImageView btnLuzAzul, btnLuzAmarilla, btnBrilloUp, btnBrilloDown, btnOn,
              btnOff, btnOnGG1, btnOffGG1, btnOnGG2, btnOffGG2, btnOnGG3, btnOffGG3,
              btnOnGG4, btnOffGG4, btnConfGG1, btnConfGG2, btnConfGG3, btnConfGG4;

    TextView textViewG1;
    TextView textViewG2;
    TextView textViewG3;
    TextView textViewG4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        viewFlipper.setOnTouchListener(new ListenerTouchViewFlipper());

        controllersDBH = new ControllersSqLiteHelper(this, "DBControllers", null, 1);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            name = getIntent().getStringExtra("Name") != null ? getIntent().getStringExtra("Name") : "Aw Lumina";
            deviceIP = getIntent().getStringExtra("IP") != null ? getIntent().getStringExtra("IP"): "";
            deviceMac = getIntent().getStringExtra("MAC") != null ? getIntent().getStringExtra("MAC"): "";
            if( name.length() > 0 ) {
                this.setTitle(name);
            }else
                this.setTitle(R.string.title_panel_general);
        }catch(Exception e){
            this.setTitle(R.string.title_panel_general);
        }

        context = this;
        myHandler = new MyHandler();
        sendCommand = new Commands( context, myHandler, deviceIP );

        startTexts();

        startPanelButtons();
        startGroup1Buttons();
        startGroup2Buttons();
        startGroup3Buttons();
        startGroup4Buttons();
        updateGroupsNames();
    }

    public void updateGroupsNames(){
        if(deviceMac.length() > 0) {
            db = controllersDBH.getWritableDatabase();
            if (db != null) {
                Cursor cursorGroup = db.rawQuery("SELECT group1, group2, group3, group4 FROM Controllers WHERE mac='" + deviceMac + "'", null);
                if (cursorGroup.moveToFirst()) {
                    do {
                        textViewG1.setText(cursorGroup.getString(0).trim());
                        textViewG2.setText(cursorGroup.getString(1).trim());
                        textViewG3.setText(cursorGroup.getString(2).trim());
                        textViewG4.setText(cursorGroup.getString(3).trim());
                    } while (cursorGroup.moveToNext());
                }
                db.close();
            }
        }
    }

    private void startTexts(){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Black.ttf");

        TextView textViewGroups = (TextView) findViewById(R.id.textViewGroups);
        textViewGroups.setTypeface( typeface );

        TextView textViewColorG = (TextView) findViewById(R.id.textViewColorG);
        textViewColorG.setTypeface( typeface );
        TextView textViewColorG1 = (TextView) findViewById(R.id.textViewColorG1);
        textViewColorG1.setTypeface( typeface );
        TextView textViewColorG2 = (TextView) findViewById(R.id.textViewColorG2);
        textViewColorG2.setTypeface( typeface );
        TextView textViewColorG3 = (TextView) findViewById(R.id.textViewColorG3);
        textViewColorG3.setTypeface( typeface );
        TextView textViewColorG4 = (TextView) findViewById(R.id.textViewColorG4);
        textViewColorG4.setTypeface( typeface );

        TextView textViewBrightG = (TextView) findViewById(R.id.textViewBrightG);
        textViewBrightG.setTypeface( typeface );
        TextView textViewBrightG1 = (TextView) findViewById(R.id.textViewBrightG1);
        textViewBrightG1.setTypeface( typeface );
        TextView textViewBrightG2 = (TextView) findViewById(R.id.textViewBrightG2);
        textViewBrightG2.setTypeface( typeface );
        TextView textViewBrightG3 = (TextView) findViewById(R.id.textViewBrightG3);
        textViewBrightG3.setTypeface( typeface );
        TextView textViewBrightG4 = (TextView) findViewById(R.id.textViewBrightG4);
        textViewBrightG4.setTypeface( typeface );

        textViewG1 = (TextView) findViewById(R.id.textViewG1);
        textViewG1.setTypeface( typeface );
        textViewG2 = (TextView) findViewById(R.id.textViewG2);
        textViewG2.setTypeface( typeface );
        textViewG3 = (TextView) findViewById(R.id.textViewG3);
        textViewG3.setTypeface( typeface );
        textViewG4 = (TextView) findViewById(R.id.textViewG4);
        textViewG4.setTypeface( typeface );

        TextView textViewAddG1 = (TextView) findViewById(R.id.textViewAddG1);
        textViewAddG1.setTypeface( typeface );
        TextView textViewAddG2 = (TextView) findViewById(R.id.textViewAddG2);
        textViewAddG2.setTypeface( typeface );
        TextView textViewAddG3 = (TextView) findViewById(R.id.textViewAddG3);
        textViewAddG3.setTypeface( typeface );
        TextView textViewAddG4 = (TextView) findViewById(R.id.textViewAddG4);
        textViewAddG4.setTypeface( typeface );

        TextView textViewRemoveG1 = (TextView) findViewById(R.id.textViewRemoveG1);
        textViewRemoveG1.setTypeface( typeface );
        TextView textViewRemoveG2 = (TextView) findViewById(R.id.textViewRemoveG2);
        textViewRemoveG2.setTypeface( typeface );
        TextView textViewRemoveG3 = (TextView) findViewById(R.id.textViewRemoveG3);
        textViewRemoveG3.setTypeface( typeface );
        TextView textViewRemoveG4 = (TextView) findViewById(R.id.textViewRemoveG4);
        textViewRemoveG4.setTypeface( typeface );
    }

    private void sendAction( int action, int arg ){
        final int group = arg;
        vibrate();
        switch( action ){
            case ON:
                startPanel = sendCommand.setOn(group, startPanel);
                break;
            case OFF:
                sendCommand.setOff(group);
                break;
            case BRIGHT_UP:
                startPanel = sendCommand.bright(group, startPanel, true);
                break;
            case BRIGHT_DOWN:
                startPanel = sendCommand.bright(group, startPanel, false);
                break;
            case WARMTH_UP:
                startPanel = sendCommand.warmth(group, startPanel, true);
                break;
            case WARMTH_DOWN:
                startPanel = sendCommand.warmth(group, startPanel, false);
                break;
            case ADD:
                sendCommand.addBulb(group);
                break;
            case REMOVE:
                sendCommand.removeBulb(group);
                break;
            default:
                break;
        }
    }

    private void startPanelButtons(){
        btnLuzAzul = (ImageView) findViewById(R.id.btnAzulG);
        btnLuzAmarilla = (ImageView) findViewById(R.id.btnAmarrilloG);
        btnBrilloUp = (ImageView) findViewById(R.id.btnBrilloUpG);
        btnBrilloDown = (ImageView) findViewById(R.id.btnBrilloDownG);
        btnOn = (ImageView) findViewById(R.id.btnOnG);
        btnOff = (ImageView) findViewById(R.id.btnOffG);
        btnOnGG1 = (ImageView) findViewById(R.id.btnOnGG1);
        btnOffGG1 = (ImageView) findViewById(R.id.btnOffGG1);
        btnOnGG2 = (ImageView) findViewById(R.id.btnOnGG2);
        btnOffGG2 = (ImageView) findViewById(R.id.btnOffGG2);
        btnOnGG3 = (ImageView) findViewById(R.id.btnOnGG3);
        btnOffGG3 = (ImageView) findViewById(R.id.btnOffGG3);
        btnOnGG4 = (ImageView) findViewById(R.id.btnOnGG4);
        btnOffGG4 = (ImageView) findViewById(R.id.btnOffGG4);
        btnConfGG1 = (ImageView) findViewById(R.id.btnConfGG1);
        btnConfGG2 = (ImageView) findViewById(R.id.btnConfGG2);
        btnConfGG3 = (ImageView) findViewById(R.id.btnConfGG3);
        btnConfGG4 = (ImageView) findViewById(R.id.btnConfGG4);

        btnLuzAzul.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction( WARMTH_DOWN, ALL_LIGHTS );
            }
        });
        btnLuzAmarilla.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(WARMTH_UP, ALL_LIGHTS);
            }
        });
        btnBrilloUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(BRIGHT_UP, ALL_LIGHTS);
            }
        });
        btnBrilloDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(BRIGHT_DOWN, ALL_LIGHTS);
            }
        });
        btnOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(ON, ALL_LIGHTS);
            }
        });
        btnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(OFF, ALL_LIGHTS);
            }
        });
        btnOnGG1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(ON, GROUP1);
            }
        });
        btnOffGG1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(OFF, GROUP1);
            }
        });
        btnOnGG2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(ON, GROUP2);
            }
        });
        btnOffGG2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(OFF, GROUP2);
            }
        });
        btnOnGG3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(ON, GROUP3);
            }
        });
        btnOffGG3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(OFF, GROUP3);
            }
        });
        btnOnGG4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(ON, GROUP4);
            }
        });
        btnOffGG4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(OFF, GROUP4);
            }
        });
        btnConfGG1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vibrate();
                activeWindow = 1;
                startPanel = activeWindow;
                updateTitleView();
                viewFlipper.showNext();
            }
        });
        btnConfGG2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vibrate();
                activeWindow = 2;
                startPanel = activeWindow;
                updateTitleView();
                viewFlipper.showNext();
                viewFlipper.showNext();
            }
        });
        btnConfGG3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vibrate();
                activeWindow = 3;
                startPanel = activeWindow;
                updateTitleView();
                for( int i = 0; i < 3; i++ )
                    viewFlipper.showNext();
            }
        });
        btnConfGG4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vibrate();
                activeWindow = 4;
                startPanel = activeWindow;
                updateTitleView();
                for( int i = 0; i < 4; i++ )
                    viewFlipper.showNext();
            }
        });
    }

    private void startGroup1Buttons(){
        btnLuzAzulGG1 = (ImageView) findViewById(R.id.btnAzulGG1);
        btnLuzAmarillaGG1 = (ImageView) findViewById(R.id.btnAmarrilloGG1);
        btnBrilloUpGG1 = (ImageView) findViewById(R.id.btnBrilloUpGG1);
        btnBrilloDownGG1 = (ImageView) findViewById(R.id.btnBrilloDownGG1);
        btnOnGGG1 = (ImageView) findViewById(R.id.btnOnGGG1);
        btnOffGGG1 = (ImageView) findViewById(R.id.btnOffGGG1);
        btnAnadirGG1 = (ImageView) findViewById(R.id.btnAnadirGG1);
        btnSuprimirGG1 = (ImageView) findViewById(R.id.btnSuprimirGG1);
        btnLuzAzulGG1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(WARMTH_DOWN, GROUP1);
            }
        });
        btnLuzAmarillaGG1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(WARMTH_UP, GROUP1);
            }
        });
        btnBrilloUpGG1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(BRIGHT_UP, GROUP1);
            }
        });
        btnBrilloDownGG1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(BRIGHT_DOWN, GROUP1);
            }
        });
        btnOnGGG1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(ON, GROUP1);
            }
        });
        btnOffGGG1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(OFF, GROUP1);
            }
        });
        btnAnadirGG1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(ADD, GROUP1);
            }
        });
        btnSuprimirGG1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(REMOVE, GROUP1);
            }
        });
    }

    private void startGroup2Buttons(){
        btnLuzAzulGG2 = (ImageView) findViewById(R.id.btnAzulGG2);
        btnLuzAmarillaGG2 = (ImageView) findViewById(R.id.btnAmarrilloGG2);
        btnBrilloUpGG2 = (ImageView) findViewById(R.id.btnBrilloUpGG2);
        btnBrilloDownGG2 = (ImageView) findViewById(R.id.btnBrilloDownGG2);
        btnOnGGG2 = (ImageView) findViewById(R.id.btnOnGGG2);
        btnOffGGG2 = (ImageView) findViewById(R.id.btnOffGGG2);
        btnAnadirGG2 = (ImageView) findViewById(R.id.btnAnadirGG2);
        btnSuprimirGG2 = (ImageView) findViewById(R.id.btnSuprimirGG2);
        btnLuzAzulGG2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(WARMTH_DOWN, GROUP2);
            }
        });
        btnLuzAmarillaGG2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(WARMTH_UP, GROUP2);
            }
        });
        btnBrilloUpGG2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(BRIGHT_UP, GROUP2);
            }
        });
        btnBrilloDownGG2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(BRIGHT_DOWN, GROUP2);
            }
        });
        btnOnGGG2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(ON, GROUP2);
            }
        });
        btnOffGGG2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(OFF, GROUP2);
            }
        });
        btnAnadirGG2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(ADD, GROUP2);
            }
        });
        btnSuprimirGG2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(REMOVE, GROUP2);
            }
        });
    }

    private void startGroup3Buttons(){
        btnLuzAzulGG3 = (ImageView) findViewById(R.id.btnAzulGG3);
        btnLuzAmarillaGG3 = (ImageView) findViewById(R.id.btnAmarrilloGG3);
        btnBrilloUpGG3 = (ImageView) findViewById(R.id.btnBrilloUpGG3);
        btnBrilloDownGG3 = (ImageView) findViewById(R.id.btnBrilloDownGG3);
        btnOnGGG3 = (ImageView) findViewById(R.id.btnOnGGG3);
        btnOffGGG3 = (ImageView) findViewById(R.id.btnOffGGG3);
        btnAnadirGG3 = (ImageView) findViewById(R.id.btnAnadirGG3);
        btnSuprimirGG3 = (ImageView) findViewById(R.id.btnSuprimirGG3);
        btnLuzAzulGG3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(WARMTH_DOWN, GROUP3);
            }
        });
        btnLuzAmarillaGG3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(WARMTH_UP, GROUP3);
            }
        });
        btnBrilloUpGG3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(BRIGHT_UP, GROUP3);
            }
        });
        btnBrilloDownGG3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(BRIGHT_DOWN, GROUP3);
            }
        });
        btnOnGGG3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(ON, GROUP3);
            }
        });
        btnOffGGG3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(OFF, GROUP3);
            }
        });
        btnAnadirGG3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction( ADD, GROUP3 );
            }
        });
        btnSuprimirGG3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction( REMOVE, GROUP3 );
            }
        });
    }

    private void startGroup4Buttons(){
        btnLuzAzulGG4 = (ImageView) findViewById(R.id.btnAzulGG4);
        btnLuzAmarillaGG4 = (ImageView) findViewById(R.id.btnAmarrilloGG4);
        btnBrilloUpGG4 = (ImageView) findViewById(R.id.btnBrilloUpGG4);
        btnBrilloDownGG4 = (ImageView) findViewById(R.id.btnBrilloDownGG4);
        btnOnGGG4 = (ImageView) findViewById(R.id.btnOnGGG4);
        btnOffGGG4 = (ImageView) findViewById(R.id.btnOffGGG4);
        btnAnadirGG4 = (ImageView) findViewById(R.id.btnAnadirGG4);
        btnSuprimirGG4 = (ImageView) findViewById(R.id.btnSuprimirGG4);
        btnLuzAzulGG4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(WARMTH_DOWN, GROUP4);
            }
        });
        btnLuzAmarillaGG4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(WARMTH_UP, GROUP4);
            }
        });
        btnBrilloUpGG4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(BRIGHT_UP, GROUP4);
            }
        });
        btnBrilloDownGG4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(BRIGHT_DOWN, GROUP4);
            }
        });
        btnOnGGG4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(ON, GROUP4);
            }
        });
        btnOffGGG4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(OFF, GROUP4);
            }
        });
        btnAnadirGG4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction(ADD, GROUP4);
            }
        });
        btnSuprimirGG4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAction( REMOVE, GROUP4 );
            }
        });
    }

    protected void vibrate(){
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(30);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_panel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Black.ttf");
        switch ( item.getItemId() ){
            case R.id.btnEdit:
                if( deviceMac.length() > 0 ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.editar);

                    final EditText input = new EditText(context);

                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setTypeface(typeface);
                    if (activeWindow != 0) {
                        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                        input.setHint(R.string.cambiarGrupo);
                    }
                    else {
                        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
                        input.setHint(R.string.cambiarGeneral);
                    }
                    builder.setView(input);

                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newName = input.getText().toString();
                            if (newName.length() > 0 && !newName.equals("AW Lumina"))
                                updateDataBaseName(newName);
                            else
                                dialog.cancel();
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateDataBaseName( String newName ){
        db = controllersDBH.getWritableDatabase();
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT name FROM Controllers", null);
            if (cursor.moveToFirst()) {
                boolean exists = false;
                do {
                    if ( newName.equals( cursor.getString(0) ) )
                            exists = true;
                } while (cursor.moveToNext());
                Cursor cursorGroup = db.rawQuery("SELECT group1, group2, group3, group4 FROM Controllers WHERE mac='" + deviceMac + "'", null);
                boolean existsGroup = false;
                if (cursorGroup.moveToFirst()) {
                    do {
                        for (int i = 0; i < 4; i++)
                            if (newName.equals(cursorGroup.getString(i)))
                                existsGroup = true;
                    } while (cursorGroup.moveToNext());
                }
                if (!exists && !existsGroup) {
                    if( activeWindow == 0 ) {
                        ContentValues values = new ContentValues();
                        values.put("name", newName);
                        db.update("Controllers", values, "mac='" + deviceMac + "'", null);
                        name = newName;
                    }else{
                        ContentValues values = new ContentValues();
                        values.put(GROUPS[activeWindow], newName);
                        db.update("Controllers", values, "mac='" + deviceMac + "'", null);
                        if(activeWindow == 1)
                            textViewG1.setText(newName.trim());
                        if(activeWindow == 2)
                            textViewG2.setText(newName.trim());
                        if(activeWindow == 3)
                            textViewG3.setText(newName.trim());
                        if(activeWindow == 4)
                            textViewG4.setText(newName.trim());
                    }
                }
            }
            db.close();
        }
        updateTitleView();
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public String getViewName( int window ){
        String title = "";
        if( window == 0 )
            title = "Aw Lumina";
        else
            title = "Grupo " + window;
        db = controllersDBH.getWritableDatabase();
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT name, group1, group2, group3, group4 FROM Controllers WHERE mac='" + deviceMac + "'", null);
            if (cursor.moveToFirst()) {
                do {
                    title = cursor.getString(window);
                } while (cursor.moveToNext());
            }
            db.close();
        }
        return title;
    }

    public void updateTitleView(){
        if( activeWindow != 0) {
            if( deviceMac.length() > 0)
                ((Activity) context).setTitle(getViewName(activeWindow));
            else
                ((Activity) context).setTitle("Grupo " + activeWindow);
        }
        else {
            if( name.length() > 0 ) {
                ((Activity) context).setTitle(name);
            }else
                ((Activity) context).setTitle(R.string.title_panel_general);
        }
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
                    if(distance > 80){
                        viewFlipper.showNext();
                        activeWindow++;
                    }
                    if(distance < -80) {
                        viewFlipper.showPrevious();
                        activeWindow--;
                    }
                    if( activeWindow < 0 )
                        activeWindow = 4;
                    if( activeWindow > 4 )
                        activeWindow = 0;
                    startPanel = activeWindow;
                    updateTitleView();
                default:
                    break;
            }
            return false;
        }
    }
    class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case Commands.DISCOVERED_DEVICE:
                    break;
                case Commands.LIST_WIFI_NETWORKS:
                    break;
            }
        }
    }
}
