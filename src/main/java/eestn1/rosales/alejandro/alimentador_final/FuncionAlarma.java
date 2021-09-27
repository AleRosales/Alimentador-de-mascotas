package eestn1.rosales.alejandro.alimentador_final;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by User on 08/10/2016.
 */
public class FuncionAlarma extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //carga datos de las preferencias
        SharedPreferences preferencias =  context.getSharedPreferences("DatosAlarma", MODE_PRIVATE);
        String tipo = preferencias.getString("TipoMascota","").toLowerCase();
        String cant = preferencias.getString("CantMascota","");
        //cuando se ejecuta la alarma
        //notifica al usuario que se esta por alimentar a la mascota
        notificacion("Alimentador de Mascotas","Alimentando mascota",context,false);
        //crea variable con la hora y fecha actual del sistema
        Calendar calendario = Calendar.getInstance();
        calendario.add(Calendar.MONTH,1);
        //toma los balores que va ingresar del calendar
        final String hora = String.valueOf(calendario.get(Calendar.HOUR_OF_DAY) + "s" + calendario.get(Calendar.MINUTE));
        final String fecha = String.valueOf(calendario.get(Calendar.DAY_OF_MONTH) + "s" + calendario.get(Calendar.MONTH));
        //envia señal al arduino
        //mientras envia muestra dialog de carga
        new ConexionAsyncTask(context).execute(tipo+hora+"s"+fecha+"s"+cant);
    }
    private class ConexionAsyncTask extends AsyncTask<String, Void, String> {
        private Context mContext;

        public ConexionAsyncTask(Context context) {
            mContext = context;
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
            int asd=aVoid.length();
            Log.e("jh", "on: "+aVoid+" "+asd );
            //si se envia correctamente
            if(aVoid=="error"){
                //notifica al usuario del error
                notificacion("Alimentador de Mascotas","Error de conexion al alimentar la mascota",mContext,true);
            }else{
                notificacion("Alimentador de Mascotas","Su mascota a sido alimentda",mContext,false);
            }
        }
    }
    private void notificacion(String titulo,String contenido, Context context,Boolean onclick ){
        //suena el sonido de notificacion predeterminado del telefono
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        //Servicio notificaciones
        NotificationManager Snotificacion = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //crea la notificacion
        NotificationCompat.Builder notificacion = new NotificationCompat.Builder(context);
        //caracteristicas de la notifiicacion
        notificacion.setContentTitle(titulo);
        notificacion.setContentText(contenido);
        //solo si que esta funcion este en la notificacion
        if (onclick=true) {
            //layout que abre si lo clickeamos
            Intent intentl = new Intent(context, MenuActivity.class);
            PendingIntent pintent = PendingIntent.getActivities(context, 0, new Intent[]{intentl}, 0);
            notificacion.setContentIntent(pintent);

        }
        notificacion.setSound(sonido);
        notificacion.setSmallIcon(R.mipmap.ic_launcher);
        //Ejecuta la notificacion
        Snotificacion.notify(1, notificacion.build());
    }
}