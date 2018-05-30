package pt.guilhermeramos.gestorcombustivel.models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, "bdviagens", null, 3);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE viagens (_id integer primary key, descricao varchar(100), kmInicial integer, kmFinal integer, pago integer, data text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public DB(Context context) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void write(String query) {
        db.execSQL(query);
    }

    public Cursor read(String query) {
        return db.rawQuery(query, null);
    }


}