package com.example.transporte_alimentos_amst_g1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.transporte_alimentos_amst_g1.Clases.ClaseUsando;
import com.example.transporte_alimentos_amst_g1.Clases.usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPrincipalConductor extends AppCompatActivity {

    TextView txt_titulo;
    String nombre_persona, status_persona;
    EditText log, lat;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal_conductor);

        //Se busca los componentes en el layout
        txt_titulo = findViewById(R.id.textoBienvenidaConductor);
        log = findViewById(R.id.edit_longitud);
        lat = findViewById(R.id.edit_latitud);

        //Se descomprime la informacion de la clase estatica
        usuario user = ClaseUsando.usuarioUsando;
        nombre_persona = user.getNombre();
        status_persona= user.getClase();
        //datoLong= log.getText().toString().trim();
        //datoLat= lat.getText().toString().trim();
        txt_titulo.setText("Bienvenido "+nombre_persona+"\n"
                +"Clase: "+status_persona);

        inicializarFirebase();
        databaseReference.child("UsersRegis").child("Usuarios").child(nombre_persona).child("localizacion").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double lat = (double) snapshot.child("Latitud").getValue();
                double lon = (double) snapshot.child("Longitud").getValue();
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


    public void actualizar_coordenadas(View view){

    }

    public void registrarAdministrador(View view){
        //Se inicializa el siguiente
        Intent intent = new Intent(this, RegistroAdministrador.class);
        startActivity(intent);
    }

    //Metodo para cerrar sesion
    public void cerrarSesion(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log Out");
        builder.setIcon(R.drawable.edit_aviso);
        builder.setMessage("Seguro quiere salir?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}