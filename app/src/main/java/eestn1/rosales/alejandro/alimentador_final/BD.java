package eestn1.rosales.alejandro.alimentador_final;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BD extends SQLiteOpenHelper {
    String tabla="CREATE TABLE Historial(HoraFecha Text,Cantidad Text)";
    String tabla2="CREATE TABLE Config(IP Text,Puerto Text,CodSeg Text)";

    public BD(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase BD) {
        BD.execSQL(tabla);
        BD.execSQL(tabla2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase BD, int i, int i1) {

    }
}
