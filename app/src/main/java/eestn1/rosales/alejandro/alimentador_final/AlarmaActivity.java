package eestn1.rosales.alejandro.alimentador_final;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmaActivity extends AppCompatActivity {
    TextView TVhora;
    TextView TVmin;
    Calendar Chorario;
    Spinner spinnerMascotas, spinnerHoras;
    SharedPreferences preferencias = null;
    Button SubHora, BajarHora, SubMin, BajarMin,BTaceptar;
    Switch sw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarma);
        preferencias = getSharedPreferences("DatosAlarma", MODE_PRIVATE);
        spinnerMascotas = (Spinner) findViewById(R.id.Spinner);
        spinnerHoras=(Spinner) findViewById(R.id.SpinnerHora);
        SubHora = (Button) findViewById(R.id.SubHora);
        BajarHora = (Button) findViewById(R.id.BajarHora);
        SubMin = (Button) findViewById(R.id.SubMin);
        BajarMin = (Button) findViewById(R.id.BajarMin);
        BTaceptar=(Button)findViewById(R.id.BTaceptarAl);
        Button BTcancelar=(Button)findViewById(R.id.BTcancelar_alarma);
        TVhora =(TextView) findViewById(R.id.TVhora);
        TVmin =(TextView) findViewById(R.id.TVmin);
        sw=(Switch)findViewById(R.id.swich);
        //cambia el estado de los botones segun el estado del swwich
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EstadoAlarma(isChecked);
            }
        });
        //crea variable con la hora y fecha actual del sistema
        Chorario=Calendar.getInstance();
        Chorario.setTimeInMillis(System.currentTimeMillis());
        //cambia sus valores a hora predeterminada
        Chorario.set(Calendar.HOUR_OF_DAY,11);
        Chorario.set(Calendar.MINUTE,59);
        Chorario.set(Calendar.SECOND,0);
        settime(0,1);
        //funcion de los botones que cambian la hora
        SubHora.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            settime(1,0);
        }});
        BajarHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settime(-1,0);
            }});
        SubMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settime(0,5);
            }});
        BajarMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settime(0,-5);

            }});
        //funcion de los botones de aceptar y cancelar
        BTaceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrearAlarma();
                finish();
            }
        });
        BTcancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelarAlarma();
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //chequea si la alarma esta activada
        boolean estado=preferencias.getBoolean("EstadoAlarma",false);
        //si esta la alarma activada
        if (estado==true) {
            sw.setChecked(true);
            EstadoAlarma(true);
        }else {
            sw.setChecked(false);
            EstadoAlarma(false);
        }
    }

    private void EstadoAlarma(boolean i){
        if (i==true){
            SubHora.setEnabled(true);
            BajarHora.setEnabled(true);
            SubMin.setEnabled(true);
            BajarMin.setEnabled(true);
            BTaceptar.setEnabled(true);
            SubHora.setBackgroundColor(getResources().getColor(R.color.colorBoton));
            BajarHora.setBackgroundColor(getResources().getColor(R.color.colorBoton));
            SubMin.setBackgroundColor(getResources().getColor(R.color.colorBoton));
            BajarMin.setBackgroundColor(getResources().getColor(R.color.colorBoton));
            BTaceptar.setBackgroundColor(getResources().getColor(R.color.colorBoton));
        }else {
            SubHora.setEnabled(false);
            BajarHora.setEnabled(false);
            SubMin.setEnabled(false);
            BajarMin.setEnabled(false);
            BTaceptar.setEnabled(false);
            SubHora.setBackgroundColor(Color.GRAY);
            BajarHora.setBackgroundColor(Color.GRAY);
            SubMin.setBackgroundColor(Color.GRAY);
            BajarMin.setBackgroundColor(Color.GRAY);
            BTaceptar.setBackgroundColor(Color.GRAY);
            preferencias.edit().putBoolean("EstadoAlarma",false).commit();
            CancelarAlarma();
        }
    }
    private void settime(int hora, int minutos){
        Chorario.add(Calendar.HOUR_OF_DAY,hora);
        Chorario.add(Calendar.MINUTE,minutos);
        String Hora= String.valueOf(Chorario.get(Calendar.HOUR_OF_DAY));
        String Minutos= String.valueOf(Chorario.get(Calendar.MINUTE));
        if (Hora.length()<2){
            Hora="0"+Hora;
        }
        if (Minutos.length()<2){
            Minutos="0"+Minutos;
        }
        TVhora.setText(Hora);
        TVmin.setText(Minutos);
    }
    private void CrearAlarma(){
        //tipo de mascota que alimentara
        String TipoMascota= spinnerMascotas.getSelectedItem().toString();
        String MascotaCant= String.valueOf(spinnerMascotas.getSelectedItemPosition()+1);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("TipoMascota",TipoMascota);
        editor.putString("CantMascota",MascotaCant);
        editor.commit();
        //intervalo de hotas
        int Intervalo;
        //saca el intervalo del string del spinner
        String sintervalo =spinnerHoras.getSelectedItem().toString().substring(5);
        if (sintervalo.length()==4){
            Intervalo= Integer.parseInt(sintervalo.substring(0,2));
        }else{
            Intervalo= Integer.parseInt(sintervalo.substring(0,1));
        }
        //elimina la ultima alarma activada, para evitar errores
        CancelarAlarma();
        //clase que contiene lo que va a hacer la alarma
        Intent Ifuncion=new Intent(AlarmaActivity.this,FuncionAlarma.class);
        //variable servicio de alarma
        AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //lo declara como pendiente
        PendingIntent FuncionPendiente=PendingIntent.getBroadcast(AlarmaActivity.this,0,Ifuncion,0);
        //Wakeup es para que no importe el dia del calendar
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,Chorario.getTimeInMillis(),1000*60*60*Intervalo,FuncionPendiente);
        preferencias.edit().putBoolean("EstadoAlarma",true).commit();
        Toast.makeText(this,"Alarma establecida",Toast.LENGTH_LONG).show();
    }
    public void CancelarAlarma() {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmaActivity.this, FuncionAlarma.class);
        alarmIntent = PendingIntent.getBroadcast(AlarmaActivity.this, 0, intent, 0);
        //si hay una alarma activada
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
            preferencias.edit().putBoolean("EstadoAlarma",false).commit();
        }
    }
}
