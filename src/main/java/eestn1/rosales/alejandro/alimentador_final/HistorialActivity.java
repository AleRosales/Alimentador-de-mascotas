package eestn1.rosales.alejandro.alimentador_final;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

public class HistorialActivity extends AppCompatActivity {
    ListView lista;
    TextView UltimAct;
    SharedPreferences preferencias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferencias = getSharedPreferences("DatosHistorial", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        UltimAct=(TextView)findViewById(R.id.TVultimAct);
        lista = (ListView) findViewById(R.id.lista);
        Button BTborrarH = (Button) findViewById(R.id.BTborrarH);
        Button BTatras = (Button) findViewById(R.id.BTatras);
        Button BTactualizar = (Button) findViewById(R.id.BTactulizar);

        //pide el historial al arduino
        new ConexionAsyncTask(this, "Actualizando Historial").execute("ActualizarHist");
        UltimAct.setText("Ultima vez: "+preferencias.getString("UltimAct","--:-- --/--"));
        //carga el historial q se guardo en la BD
        cargar_BD(lista);
        BTactualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConexionAsyncTask(HistorialActivity.this, "Actualizando Historial").execute("ActualizarHist");
            }
        });
        BTborrarH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConexionAsyncTask(HistorialActivity.this, "Borrando Historial").execute("BorrarHist");
            }
        });
        BTatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cierra esta activity
                finish();
            }
        });
    }

    private class ConexionAsyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        private Context mContext;
        private String msj;
        private String funcion;

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
        protected String doInBackground(String... var) {
            funcion = var[0];
            //conecta con la URL y la variable
            try {
                return ConexionArduino.downloadUrl(var[0], mContext);
            } catch (IOException e) {
                //si hay un error de conexion
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            String respuestaArduino;
            respuestaArduino = aVoid.replaceAll("\n", "");
            //finaliza la conexion lo cierra
            progressDialog.dismiss();
            //chequea dependiendo de la funcion
            if (funcion == "ActualizarHist") {
                ActualizaHistorial(respuestaArduino);
                cargar_BD(lista);
            } else if (funcion == "BorrarHist") {
                BorrarHistorial_BD(respuestaArduino);
            }
        }
    }

    //toma la respuesta, si no es un error guarda el historial en la base de datos
    private void ActualizaHistorial(String asd) {
        String stringBD = asd;
        if (stringBD.equals("error")) {
            Toast.makeText(this, "Error de conexion al actualizar el Historial", Toast.LENGTH_SHORT).show();
        } else {
            //borra BD
            ConexionBD.Borrar_BD(this, "Historial");
            //Si hay registros
            if (stringBD.length() > 1) {
                //separa todos los registros
                String[] contenedorBD = stringBD.split("-");
                //segun cantidad de registros el tamaño de los contenedores
                String[] contenedorIMG = new String[contenedorBD.length];
                String[] contenedorFH = new String[contenedorBD.length];
                //recorre todos los registros
                for (int a = 0; a < contenedorBD.length; ) {
                    //divide el registro en partes
                    String[] divisor = contenedorBD[a].split("s");
                    String hora=divisor[0];
                    String min=divisor[1];
                    String fecha=divisor[2];
                    String mes=divisor[3];
                    String arreglo=ArreglaFecha(hora,min,fecha,mes,"    ");
                    contenedorFH[a] = arreglo;
                    contenedorIMG[a] = divisor[4].substring(0,1);
                    a++;
                }
                //guarda los registros
                for (int a = 0; a < contenedorBD.length; ) {
                    ContentValues nuevoregistro = new ContentValues();
                    nuevoregistro.put("HoraFecha", contenedorFH[a]);
                    nuevoregistro.put("Cantidad", contenedorIMG[a]);
                    boolean guardado =ConexionBD.Guardar_BD(this, nuevoregistro, "Historial");
                    a++;
                }
            }
            Toast.makeText(this, "Historial Actualizado", Toast.LENGTH_SHORT).show();
            Calendar calendar=Calendar.getInstance();
            calendar.add(Calendar.MONTH,1);
            String j=ArreglaFecha (String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)),
                    String.valueOf(calendar.get(Calendar.MINUTE)),
                    String.valueOf(calendar.get(Calendar.DATE)),
                    String.valueOf( calendar.get(Calendar.MONTH))," ");
            UltimAct.setText("Ultima vez:"+j);
            preferencias.edit().putString("UltimAct",j).commit();
        }
    }
    private  String ArreglaFecha(String hora,String min,String fecha,String mes,String espacio){
        if (hora.length()<2){
            hora="0"+hora;
        }
        if (min.length()<2){
            min="0"+min;
        }
        if (fecha.length()<2){
            fecha="0"+fecha;
        }
        if (mes.length()<2){
            mes="0"+mes;
        }
        String arreglo = hora + ":" + min + espacio + fecha + "/" + mes;
        return arreglo;
    }
    //chequea si se elimino correctamente
    private void BorrarHistorial_BD(String resp) {
        if (resp.equals("error")) {
            Toast.makeText(this, "Error de conexion al borrar el historial", Toast.LENGTH_SHORT).show();
        } else {
            //borra la BD historial
            boolean borrarBD = ConexionBD.Borrar_BD(this, "Historial");
            //si la borro correctamente
            if (borrarBD == true) {
                cargar_BD(lista);
            }
        }
    }

    //carga los registros y los muestra en la list
    private void cargar_BD(ListView lista) {
        Cursor c = ConexionBD.cargar_BD(this, "Historial");
        //si hay registros
        if (c != null) {
            //obtener cantidad de registros
            int cantidad = c.getCount();
            int[] contenedorIMG = new int[cantidad];
            String[] contenedorFH = new String[cantidad];
            //Va al primer registro
            if (c.moveToFirst()) {
                int i = 0;
                //mientras alla otro registro
                do {
                    //de cada registro-----
                    //guarda hora y fecha en un string
                    String linea = "   " + c.getString(1);
                    contenedorFH[i] = linea;
                    //guarda el SRC imagen
                    String imagen = c.getString(2);
                    int img = 0;
                    switch (imagen) {
                        case "1":
                            img = R.drawable.a1;
                            break;
                        case "2":
                            img = R.drawable.a2;
                            break;
                        case "3":
                            img = R.drawable.a3;
                            break;
                    }
                    contenedorIMG[i] = img;
                    i++;
                    //va al siguiente registro
                } while (c.moveToNext());
            }
            //cierra el cursor
            c.close();
            //se copio toda la BD a los contenedores
            //pasa los contenedores al ListView con un adaptador
            Adapter adapter;
            adapter = new ListViewAdapter(this, contenedorFH, contenedorIMG);
            lista.setAdapter((ListAdapter) adapter);
        }
    }
}

