package eestn1.rosales.alejandro.alimentador_final;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

/**
 * Created by User on 20/09/2016.
 */
public class ConexionArduino {

        public static String downloadUrl(String param,Context context) throws IOException {
            String dato = null;
            //codifica la variable que se enviara
            try {
                 dato= "a"+"="+ URLEncoder.encode(param,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //carga la IP+puerto y el codigo de seguridad de la BD
            String IP=null;
            String codseg=null;
            Cursor config= ConexionBD.cargar_BD(context,"Config");
            if (config.moveToFirst()) {
                //asigna a las variables los datos del registro
                IP = config.getString(1)+":"+config.getString(2);
                codseg = config.getString(3);
            }
            String myurl="http://"+IP+"?"+dato;
            //ejecuta la conexion
            //variale q contiene la respues del servidor
            InputStream is = null;
         try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //caracteristicas de la conexion
                //tiempo de lectura
                conn.setReadTimeout(5000);
                //tiempo de conexion
                conn.setConnectTimeout(10000 );
                //metodo de conexion
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // empieza la conexion
                conn.connect();
                //toma la respuesta del serviidor
                is = conn.getInputStream();
                //transforma la respuesta del servidor a string
                String contentAsString = readIt(is);
                return contentAsString;

            } finally {
                if (is != null) {
                    //cuando el servidor deja de responder
                    is.close();
                }
            }
        }

        private static String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
            int n = 0;
            char[] buffer = new char[1024 * 4];
            InputStreamReader reader = new InputStreamReader(stream, "UTF8");
            StringWriter writer = new StringWriter();
            while (-1 != (n = reader.read(buffer)))
                writer.write(buffer, 0, n);
            return writer.toString();
        }
}