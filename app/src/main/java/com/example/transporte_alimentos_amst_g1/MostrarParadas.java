package com.example.transporte_alimentos_amst_g1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MostrarParadas extends AppCompatActivity {
    ListView listaParadas;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String usuarioActual;

    private List<String> listaTit= new ArrayList<>();
    private List<Integer> listaImg = new ArrayList<>();
    private List<String> listaLong= new ArrayList<>();
    private  List<String> listaLat= new ArrayList<>();
    String[] arrayTit;
    Integer[] arrayimg;
    String[] arrayLat;
    String[] arrayLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_paradas);

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


    public class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String[] titulo;
        String[] latitud;
        String[] longitud;
        Integer[] imagenes;

        MyAdapter(Context c, String[] titulo, Integer[] imagenes, String[] latitud, String[] longitud){
            super(c, R.layout.row,R.id.interTitulo,titulo);
            this.context=c;
            this.titulo=titulo;
            this.latitud = latitud;
            this.longitud = longitud;
            this.imagenes=imagenes;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater= (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row= layoutInflater.inflate(R.layout.row1,parent,false);
            ImageView images = row.findViewById(R.id.image);
            TextView myDispositivos = row.findViewById(R.id.interTitulo);
            TextView myLatit = row.findViewById(R.id.interLat);
            TextView myLong = row.findViewById(R.id.interLong);

            images.setImageResource(imagenes[position]);
            myDispositivos.setText(titulo[position]);
            myLatit.setText(latitud[position]);
            myLong.setText(longitud[position]);

            return row;
        }
    }

    //metodo donde se obtiene la informacion y se la agrega al listview
    private void obtenerInfo() {
        databaseReference.child("UsersRegis").child("Usuarios").child(usuarioActual).child("Paradas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final DecimalFormat df = new DecimalFormat("0.00000");
                listaTit.clear();
                listaImg.clear();
                listaLat.clear();
                listaLong.clear();
                //arrayList = new ArrayList<>();
                for (DataSnapshot objSnapchot : snapshot.getChildren()){
                    String etiqueta= objSnapchot.getKey();
                    double latitud = (double) objSnapchot.child("Latitud").getValue();
                    double longitud = (double) objSnapchot.child("Longitud").getValue();

                    listaTit.add(etiqueta);
                    listaLat.add(Double.toString(Double.parseDouble(df.format(latitud))));
                    listaLong.add(Double.toString(Double.parseDouble(df.format(longitud))));
                    listaImg.add(R.drawable.ic_locat);

                    arrayTit=new String[listaTit.size()];
                    arrayTit = listaTit.toArray(arrayTit);
                    arrayimg=new Integer[listaImg.size()];
                    arrayimg = listaImg.toArray(arrayimg);
                    arrayLat=new String[listaLat.size()];
                    arrayLat = listaLat.toArray(arrayLat);
                    arrayLong=new String[listaLong.size()];
                    arrayLong = listaLong.toArray(arrayLong);
                    MostrarParadas.MyAdapter adapter =new MostrarParadas.MyAdapter(MostrarParadas.this,arrayTit,arrayimg,arrayLat,arrayLong);
                    listaParadas.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void atras(View view){
        finish();
    }

}