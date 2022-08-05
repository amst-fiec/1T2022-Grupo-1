package com.example.transporte_alimentos_amst_g1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.transporte_alimentos_amst_g1.Clases.ClaseUsando;
import com.example.transporte_alimentos_amst_g1.Clases.usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdministrarParadas extends AppCompatActivity {
    ListView listaStops;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String usuarioActual;
    ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrar_paradas);

        listaStops=findViewById(R.id.listParadas);

        usuario user = ClaseUsando.usuarioUsando;
        usuarioActual = user.getUsuario();

        inicializarFirebase();
        obtenerInfo();
    }

    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }


    //metodo donde se obtiene la informacion y se la agrega al listview
    private void obtenerInfo() {
        databaseReference.child("UsersRegis").child("Usuarios").child(usuarioActual).child("Paradas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList = new ArrayList<>();
                for (DataSnapshot objSnapchot : snapshot.getChildren()){

                    String etiqueta= objSnapchot.getKey();
                    System.out.println(etiqueta);
                    String nombre= (String) objSnapchot.child("Conductor").getValue();
                    double latitud = (double) objSnapchot.child("Latitud").getValue();
                    double longitud = (double) objSnapchot.child("Longitud").getValue();

                    String union = etiqueta+"  "+ nombre+"  "+Double.toString(latitud)+"  "+Double.toString(longitud);


                    arrayList.add(union);

                }
                System.out.println(arrayList);
                ArrayAdapter adapter = new ArrayAdapter(AdministrarParadas.this, android.R.layout.simple_list_item_1,arrayList);
                listaStops.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void regresar(View view){
        //Se regresa a la pestaña anterior
        finish();
    }

    public void guardar(View view){
        //Se inicializa el siguiente
        Intent intent = new Intent(this, GuardarInfo.class);
        startActivity(intent);
    }

    public void editar(View view){
        //Se inicializa el siguiente
        Intent intent = new Intent(this, EditarInfo.class);
        startActivity(intent);
    }

    public void borrar(View view){
        //Se inicializa el siguiente
    }

}