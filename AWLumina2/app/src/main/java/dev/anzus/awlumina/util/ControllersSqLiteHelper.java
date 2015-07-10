package dev.anzus.awlumina.util;

/**
 * Created by alej0 on 07/07/2015.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ControllersSqLiteHelper extends SQLiteOpenHelper{

    String sqlCreate = "CREATE TABLE Controllers (mac TEXT, previous_mac TEXT, name TEXT, group1 TEXT, group2 TEXT, group3 TEXT, group4 TEXT )";

    public ControllersSqLiteHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        db.execSQL("DROP TABLE IF EXISTS Controllers");
        db.execSQL(sqlCreate);
    }
}
