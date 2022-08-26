package com.example.transporte_alimentos_amst_g1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.transporte_alimentos_amst_g1.clases.ClaseUsando;
import com.example.transporte_alimentos_amst_g1.clases.usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GuardarInfo extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    EditText eLongitud, eLatitud;
    String longi;
    String latit;
    Spinner spinner;
    String usuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardar_info);
        usuario user = ClaseUsando.usuarioUsando;
        usuarioActual = user.getUsuario();
        eLongitud=findViewById(R.id.ingresoLong);
        eLatitud=findViewById(R.id.ingresoLat);

        //Se inicializa la base de datos
        inicializarFirebase();

        //Se arma el Spinner
        iniciarSpinner();
    }

    private void iniciarSpinner(){
        spinner = findViewById(R.id.spinnerTipo);
        databaseReference.child("UsersRegis").child("Usuarios").child(usuarioActual).child("Conductores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> categorias = new ArrayList<>();
                for (DataSnapshot objSnapchot : snapshot.getChildren()){
                    String usuario= objSnapchot.getKey();
                    categorias.add(usuario);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                        R.layout.custum_spinner,
                        categorias);
                adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }

    //Metodo Para Guardar Informacion
    public void guardado1(View view){
        latit= eLatitud.getText().toString().toLowerCase().trim();
        double dlat = Double.parseDouble(latit);
        longi=eLongitud.getText().toString().trim();
        double dlon = Double.parseDouble(longi);
        String infoSel = spinner.getSelectedItem().toString();
        System.out.println(infoSel);
        databaseReference.child("UsersRegis").child("Usuarios").child(usuarioActual).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(snapshot);
                String nombre = snapshot.child("Conductores").child(infoSel).child("nombre").getValue(String.class);
                long numParadas = (long) snapshot.child("Conductores").child(infoSel).child("paradas").getValue();
                String titulo = infoSel + numParadas;

                Map<String,Object> taskMap = new HashMap<>();
                taskMap.put("Conductor", nombre);
                taskMap.put("Latitud", dlat);
                taskMap.put("Longitud", dlon);
                databaseReference.child("UsersRegis").child("Usuarios").child(usuarioActual).child("Paradas").child(titulo).setValue(taskMap);
                databaseReference.child("UsersRegis").child("Usuarios").child(usuarioActual).child("Conductores").child(infoSel).child("paradas").setValue(numParadas+1);
                eLatitud.setText("");
                eLongitud.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void volver(View view){
        //Se regresa a la pestaña anterior
        Intent intent = new Intent(this, AdministrarParadas.class);
        startActivity(intent);
        finish();
    }
}