package eestn1.rosales.alejandro.alimentador_final;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Objects;


public class MenuActivity extends AppCompatActivity {
    private Intent intent;
    SharedPreferences preferencias = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //identificar el primer inicio
        preferencias = getSharedPreferences("eestn1.rosales.alejandro.alimentador_final", MODE_PRIVATE);
        Button BTalimentar = (Button) findViewById(R.id.BTalimentar);
        Button BTalarma = (Button) findViewById(R.id.BTalarma);
        Button BThistorial = (Button) findViewById(R.id.BThistorial);
        Button BTconf = (Button) findViewById(R.id.BTconf);
        Button BTsalir=(Button) findViewById(R.id.BTsalir);
        //conf botones
        BTalimentar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            ventana_alimentar();
            }
        });

        BThistorial.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MenuActivity.this, HistorialActivity.class);
                startActivity(intent);
            }
        });
        BTalarma.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MenuActivity.this, AlarmaActivity.class);
                startActivity(intent);
            }
        });
        BTconf.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MenuActivity.this, ConfigActivity.class);
                startActivity(intent);
            }
        });
        BTsalir.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // El código que quieras que se ejecute la primera vez que usan tu App
        if (preferencias.getBoolean("seejecuto", true)) {
            //ingresa valores predeterminados en la BD
            ContentValues nuevoregistro = new ContentValues();
            nuevoregistro.put("IP", "192.168.1.177");
            nuevoregistro.put("Puerto", "8080");
            nuevoregistro.put("CodSeg", "1234");
            //ingresa registro
            ConexionBD.Guardar_BD(this, nuevoregistro, "Config");
            preferencias.edit().putBoolean("seejecuto", false).commit();
        }
    }

    private void ventana_alimentar() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View v = inflater.inflate(R.layout.alert_menu, null);
        builder.setView(v);
        //que se cierre cuando precione fuera del dialog
        builder.setCancelable(true);

        final RadioButton RB1=(RadioButton) v.findViewById(R.id.RB1);
        final RadioButton RB2=(RadioButton) v.findViewById(R.id.RB2);
        final RadioButton RB3=(RadioButton) v.findViewById(R.id.RB3);
        Button BTaceptar_alimentar = (Button) v.findViewById(R.id.BTaceptar_alimentar);
        Button BTcancelar_alimentar = (Button) v.findViewById(R.id.BTcancelar_alimentar);


        BTaceptar_alimentar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int item_selec=0;
                String var=null;
                if (RB1.isChecked()) {
                    item_selec=1;
                    var="chico";
                } else if (RB2.isChecked()) {
                    item_selec=2;
                    var="mediano";
                }else if (RB3.isChecked()) {
                    item_selec=3;
                    var="grande";
                }
                if (item_selec>0) {
                    //crea variable con la hora y fecha actual del sistema
                    Calendar calendario = Calendar.getInstance();
                    calendario.add(Calendar.MONTH,1);
                    //toma los balores que va ingresar del calendar
                    final String hora = String.valueOf(calendario.get(Calendar.HOUR_OF_DAY) + "s" + calendario.get(Calendar.MINUTE));
                    final String fecha = String.valueOf(calendario.get(Calendar.DAY_OF_MONTH) + "s" + calendario.get(Calendar.MONTH));
                    //envia señal al arduino
                    //mientras envia muestra dialog de carga
                    new ConexionAsyncTask(MenuActivity.this,"Alimentando Mascota").execute(var+hora+"s"+fecha+"s"+item_selec);
                }else {
                    Toast.makeText(MenuActivity.this,"Seleccione tipo de mascota",Toast.LENGTH_SHORT).show();
                }
            }
        });
        BTcancelar_alimentar.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                        intent = new Intent(MenuActivity.this, MenuActivity.class);
                        startActivity(intent);
                    }
                });
        builder.show();
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
            super.onPostExecute(aVoid);;
            //si se envia correctamente
            switch (aVoid) {
                case "error":
                    Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(mContext, "mascota alimentada", Toast.LENGTH_SHORT).show();
                    break;
            }
            //finaliza la conexion lo cierra
            progressDialog.dismiss();
        }
    }
}