package eestn1.rosales.alejandro.alimentador_final;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class ConfigActivity extends AppCompatActivity {
    EditText ETip, ETpuerto, ETcodseg;
    TextView conint, conalim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ETip = (EditText) findViewById(R.id.ETip);
        ETpuerto = (EditText) findViewById(R.id.ETpuerto);
        ETcodseg = (EditText) findViewById(R.id.ETcodseg);
        conint = (TextView) findViewById(R.id.ConexionInt);
        conalim = (TextView) findViewById(R.id.ConexionALim);
        Button BTguardar = (Button) findViewById(R.id.BTguardar);
        Button BTcancelar = (Button) findViewById(R.id.BTcancelar_conf);
        Button BTconexion = (Button) findViewById(R.id.BTprobar);
        //carga la configuracion guardada
        cargar_config_BD();
        //chequea conexion a internet, si hay abre la ventada de "cargando" y conecta al alimentador
        checkconexion();

        BTconexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkconexion();
            }
        });

        BTguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toma los datos de las edittext
                String ip = ETip.getText().toString();
                String puerto = ETpuerto.getText().toString();
                String codseg = ETcodseg.getText().toString();
                //borra los registros
                ConexionBD.Borrar_BD(ConfigActivity.this, "Config");
                //contenedor de valores que vamos a ingresar
                ContentValues nuevoregistro = new ContentValues();
                nuevoregistro.put("IP", ip);
                nuevoregistro.put("Puerto", puerto);
                nuevoregistro.put("CodSeg", codseg);
                //ingresa registro
                ConexionBD.Guardar_BD(ConfigActivity.this, nuevoregistro, "Config");
                Toast.makeText(ConfigActivity.this, "Configuraciones guardadas", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
        BTcancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void cargar_config_BD() {
        Cursor c = ConexionBD.cargar_BD(ConfigActivity.this, "Config");
        String IP = null;
        String Puerto = null;
        String codseg = null;
        //va al primer registro
        if (c.moveToFirst()) {
            //asigna a las variables los datos de la BD
            IP = c.getString(1);
            Puerto = c.getString(2);
            codseg = c.getString(3);
        }
        //le asigna a las EditText los datos
        ETip.setText(IP);
        ETpuerto.setText(Puerto);
        ETcodseg.setText(codseg);
        //cierra el cursor
        c.close();
    }

    private void checkconexion() {
        if (hayConexion() == true) {
            conint.setBackgroundColor(Color.GREEN);
            //chequea conexion al alimentador
            new ConexionAsyncTask(this,"Chequeando conexion").execute("hola");
        }
    }

    private boolean hayConexion() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private class ConexionAsyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        private Context mContext;
        private String msj;

        public ConexionAsyncTask(Context context, String mensaje) {
            mContext = context;
            msj = mensaje;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //ejecuta el dialog
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setCancelable(false);
            progressDialog.show();
            progressDialog.setContentView(R.layout.dialog_carga);
            //cambia el texto del PD
            TextView textView = (TextView) progressDialog.findViewById(R.id.cargando);
            textView.setText(msj);
        }

        @Override
        protected String doInBackground(String... urls) {
            //conecta con la URL y la variable
            try {
                return ConexionArduino.downloadUrl(urls[0],mContext);
            } catch (IOException e) {
                //si hay un error de conexion
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            //finaliza la conexion lo cierra
            progressDialog.dismiss();
            //si se envia correctamente
            if (aVoid.equals("error")) {
            Toast.makeText(mContext,"Alimentador desconectado",Toast.LENGTH_SHORT).show();
            }else {
                conalim.setBackgroundColor(Color.GREEN);
            }
        }
    }
}