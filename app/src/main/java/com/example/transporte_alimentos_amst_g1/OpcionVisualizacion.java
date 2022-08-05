package com.example.transporte_alimentos_amst_g1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.transporte_alimentos_amst_g1.Clases.ClaseUsando;
import com.example.transporte_alimentos_amst_g1.Clases.usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OpcionVisualizacion extends AppCompatActivity {
    ListView listaParadas;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String usuarioActual;
    ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opcion_visualizacion);

        listaParadas=findViewById(R.id.listParadas);

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

                    double latitud = (double) objSnapchot.child("Latitud").getValue();
                    double longitud = (double) objSnapchot.child("Longitud").getValue();

                    String union = "Latitud:  "+Double.toString(latitud)+"Longitud:  "+Double.toString(longitud);


                    arrayList.add(union);
                }
                System.out.println(arrayList);
                ArrayAdapter adapter = new ArrayAdapter(OpcionVisualizacion.this, android.R.layout.simple_list_item_1,arrayList);
                listaParadas.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void atras1(View view){
        finish();
    }

}