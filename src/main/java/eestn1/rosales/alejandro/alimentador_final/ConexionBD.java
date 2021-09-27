package eestn1.rosales.alejandro.alimentador_final;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by User on 03/10/2016.
 */
public class ConexionBD {
    public static boolean Guardar_BD(Context contexto, ContentValues registro, String tabla) {
        boolean TodoOK=false;
        BD conexion = new BD(contexto, "DEMOBS", null, 1);
        //para escribir en la base de datos
        SQLiteDatabase BD = conexion.getWritableDatabase();

        if (BD != null) {
            long i = BD.insert(tabla, null, registro);
            //si los ingreso correctamente mensaje pantalla
            if (i > 0) {
                TodoOK=true;
            }
        }
        return TodoOK;
    }

    public static boolean Borrar_BD(Context contexto, String tabla) {
        boolean TodoOk=false;
        BD conexion = new BD(contexto, "DEMOBS", null, 1);
        //para borrar registros de la base de datos
        SQLiteDatabase BD = conexion.getReadableDatabase();
        if (BD != null) {
            //borra todos los registro
            long res = BD.delete(tabla, null, null);
            //si se borraron correctamente
            if (res>0){
                TodoOk= true;
            }
        }
        return TodoOk;
    }
    public static Cursor cargar_BD(Context contexto,String tabla) {
        BD conexion = new BD(contexto, "DEMOBS", null, 1);
        //leer la base de datos
        SQLiteDatabase BD = conexion.getReadableDatabase();
        Cursor c=null;
        if (BD != null) {
            //consulta SQL
            c = BD.rawQuery("SELECT * FROM "+tabla, null);

        }
        return c;
    }
}