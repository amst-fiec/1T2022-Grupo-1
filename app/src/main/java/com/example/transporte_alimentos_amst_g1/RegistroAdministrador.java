package com.example.transporte_alimentos_amst_g1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.transporte_alimentos_amst_g1.clases.ClaseUsando;
import com.example.transporte_alimentos_amst_g1.clases.usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegistroAdministrador extends AppCompatActivity {
    EditText userAdmin, placa;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String datoUser, datoPlaca;
    String nombre_persona, status_persona,usuario_persona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_administrador);

        userAdmin=findViewById(R.id.ingresoUsuario);
        placa=findViewById(R.id.ingresoPlaca);

        usuario user = ClaseUsando.usuarioUsando;
        usuario_persona = user.getUsuario();
        nombre_persona = user.getNombre();
        status_persona= user.getClase();

        //Se inicializa la base de datos de firebase
        inicializarFirebase();
    }

    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }

    private boolean validarIngresos(){
        datoUser= userAdmin.getText().toString().toLowerCase().trim();
        datoPlaca=placa.getText().toString().trim();

        if (datoUser.equals("") || datoPlaca.equals("")){
            if (datoUser.equals(""))
                userAdmin.setError("Se requiere el Usuario");
            if (datoPlaca.equals(""))
                placa.setError("Se requiere la Placa");
            return false;
        }

        else
            return true;

    }

    private void obtenerStatus(){
        if (validarIngresos()){
            databaseReference.child("UsersRegis").child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(datoUser) ) {
                        databaseReference.child("UsersRegis").child("Usuarios").child(datoUser).child("Conductores").child(usuario_persona).child("nombre").setValue(nombre_persona);
                        databaseReference.child("UsersRegis").child("Usuarios").child(datoUser).child("Conductores").child(usuario_persona).child("placa").setValue(datoPlaca);
                        databaseReference.child("UsersRegis").child("Usuarios").child(datoUser).child("Conductores").child(usuario_persona).child("paradas").setValue(0);
                        databaseReference.child("UsersRegis").child("Usuarios").child(usuario_persona).child("Administrador").setValue(datoUser);

                    }else{
                        Toast.makeText(RegistroAdministrador.this, "El usuario no se encontro",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        } else{
            Toast.makeText(RegistroAdministrador.this, "Rellene los datos correctamente",
                    Toast.LENGTH_SHORT).show();

        }

    }
    private void vaciarCeldas(){
        userAdmin.setText("");
        placa.setText("");
    }

    public void guardarInforamcion(View v){
        obtenerStatus();
        vaciarCeldas();
    }
    public void regresar(View view){
        finish();
    }


}