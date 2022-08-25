package com.example.transporte_alimentos_amst_g1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class AdministrarParadas extends AppCompatActivity {
    ListView listaStops;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String usuarioActual;
    String paradaSel;

    private List<String> listaTit= new ArrayList<>();
    private  List<String> listaNom= new ArrayList<>();
    private List<Integer> listaImg = new ArrayList<>();
    private List<String> listaLong= new ArrayList<>();
    private  List<String> listaLat= new ArrayList<>();
    String[] arrayTit;
    String[] arrayNom;
    Integer[] arrayimg;
    String[] arrayLat;
    String[] arrayLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrar_paradas);

        listaStops=findViewById(R.id.listParadas);

        usuario user = ClaseUsando.usuarioUsando;
        usuarioActual = user.getUsuario();

        inicializarFirebase();
        obtenerInfo();

        paradaSel="";
        //se obtiene el valor del elemento seleccionado en el listview
        listaStops.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                paradaSel= (String) adapterView.getItemAtPosition(i);
                System.out.println(paradaSel);
            }
        });

    }

    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }

    public class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String[] titulo;
        String[] nombre;
        String[] latitud;
        String[] longitud;
        Integer[] imagenes;

        MyAdapter(Context c, String[] titulo, String[] nombre, Integer[] imagenes, String[] latitud, String[] longitud){
            super(c, R.layout.row,R.id.interTitulo,titulo);
            this.context=c;
            this.titulo=titulo;
            this.nombre=nombre;
            this.latitud = latitud;
            this.longitud = longitud;
            this.imagenes=imagenes;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater= (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row= layoutInflater.inflate(R.layout.row,parent,false);
            ImageView images = row.findViewById(R.id.image);
            TextView myDispositivos = row.findViewById(R.id.interTitulo);
            TextView myStatus = row.findViewById(R.id.interStatus);
            TextView myLatit = row.findViewById(R.id.interLat);
            TextView myLong = row.findViewById(R.id.interLong);

            images.setImageResource(imagenes[position]);
            myDispositivos.setText(titulo[position]);
            myStatus.setText(nombre[position]);
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
                listaNom.clear();
                listaImg.clear();
                listaLat.clear();
                listaLong.clear();
                //arrayList = new ArrayList<>();
                for (DataSnapshot objSnapchot : snapshot.getChildren()){

                    String etiqueta= objSnapchot.getKey();
                    System.out.println(etiqueta);
                    String nombre= (String) objSnapchot.child("Conductor").getValue();
                    double latitud = (double) objSnapchot.child("Latitud").getValue();
                    double longitud = (double) objSnapchot.child("Longitud").getValue();

                    listaTit.add(etiqueta);
                    listaNom.add(nombre);
                    listaLat.add(Double.toString(Double.parseDouble(df.format(latitud))));
                    listaLong.add(Double.toString(Double.parseDouble(df.format(longitud))));
                    listaImg.add(R.drawable.ic_locat);

                    arrayTit=new String[listaTit.size()];
                    arrayTit = listaTit.toArray(arrayTit);
                    arrayNom=new String[listaNom.size()];
                    arrayNom = listaNom.toArray(arrayNom);
                    arrayimg=new Integer[listaImg.size()];
                    arrayimg = listaImg.toArray(arrayimg);
                    arrayLat=new String[listaLat.size()];
                    arrayLat = listaLat.toArray(arrayLat);
                    arrayLong=new String[listaLong.size()];
                    arrayLong = listaLong.toArray(arrayLong);
                    MyAdapter adapter =new MyAdapter(AdministrarParadas.this,arrayTit,arrayNom,arrayimg,arrayLat,arrayLong);
                    listaStops.setAdapter(adapter);

                }
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
        finish();
        Intent intent = new Intent(this, GuardarInfo.class);
        startActivity(intent);

    }

    public void borrar(View view){
        if (!paradaSel.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.drawable.edit_aviso);
            builder.setMessage("¿Quiére borrar la Parada "+ paradaSel+"?");
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    databaseReference.child("UsersRegis").child("Usuarios").child(usuarioActual).child("Paradas").child(paradaSel).removeValue();
                    paradaSel="";
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }else{
            Toast.makeText(this, "Seleccione un Dispositivo que Desee Borrar", Toast.LENGTH_SHORT).show();
        }
    }

}