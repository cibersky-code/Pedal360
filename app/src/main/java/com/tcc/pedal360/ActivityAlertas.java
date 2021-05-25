package com.tcc.pedal360;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.internal.location.zzz;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ActivityAlertas extends AppCompatActivity {

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;

    private static final int REQUEST_CODE = 101;

    public double latitude;
    public double longitude;
    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;


    TextView latlong;
    EditText number, sms;
    Button send, salvar;
    SharedPreferences sharedPreferences;
    public static final String mypreference = "mypref";
    public static final String Telefone = "telefoneKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertas);

        number = findViewById(R.id.etTelefone);
        sms = findViewById(R.id.etMessage);

        send = findViewById(R.id.alertaBtn);
        salvar = findViewById(R.id.salvarBtn);


        SimpleMaskFormatter smf = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(number, smf);
        number.addTextChangedListener(mtw);


        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        if (sharedPreferences.contains(Telefone)) {
            number.setText(sharedPreferences.getString(Telefone, ""));
        }

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Number1 = number.getText().toString();

                if (Number1.equals("")) {
                    Toast.makeText(ActivityAlertas.this, "Contato não inserido", Toast.LENGTH_SHORT).show();
                    number.requestFocus();

                } else {
                    Save();
                    Toast.makeText(ActivityAlertas.this, "Atualização Concluída!", Toast.LENGTH_SHORT).show();


                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String sNumber = number.getText().toString().trim();
                if (ContextCompat.checkSelfPermission(ActivityAlertas.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    sendMessage();
                    getLocation();
                } else {
                    ActivityCompat.requestPermissions(ActivityAlertas.this, new String[]{Manifest.permission.SEND_SMS}, 100);
                }
            }

        });


    } // FIM DO ONCREATE

    private void sendMessage() {


        String sNumber = number.getText().toString().trim();
        String sMessage = sms.getText().toString().trim();


        if (!sNumber.equals("") && !sMessage.equals("")) {

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(sNumber, null, sMessage, null, null);
            Toast.makeText(getApplicationContext(), "Mensagem enviada com sucesso !", Toast.LENGTH_LONG).show();


        } else {
            Toast.makeText(getApplicationContext(), "Preencha os campos primeiro", Toast.LENGTH_LONG).show();
        }

    }

    protected void getLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        if (isLocationEnabled(ActivityAlertas.this)) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
            String sNumber = number.getText().toString().trim();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                Log.e("TAG", "GPS is on");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(sNumber, null, "https://www.google.com.br/maps/@" + String.valueOf(latitude) + "," + String.valueOf(latitude) + ",17.96z", null, null);

            }
            else{

                locationManager.requestLocationUpdates(bestProvider, 1000, 0, (LocationListener) this);
            }
        }
        else
        {
            //prompt user to enable location....
            //.................
        }
    }

    private boolean isLocationEnabled(ActivityAlertas activityAlertas) {
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            sendMessage();
            getLocation();


        } else {
            Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_LONG).show();
        }
    }

    public void Save (){
        String n = number.getText().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Telefone, n);
        editor.commit();

    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }





}



