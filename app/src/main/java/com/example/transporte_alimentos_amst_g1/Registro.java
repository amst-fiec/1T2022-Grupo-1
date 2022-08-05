package com.example.transporte_alimentos_amst_g1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.transporte_alimentos_amst_g1.Clases.usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Registro extends AppCompatActivity {
    EditText user, nombre, apellido, correo, password;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String datoUser, datoNombre, datoApellido, datoCorreo, datoContra;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);


        spinner = findViewById(R.id.spinnerTipo);
        ArrayList<String> categorias = new ArrayList<String>();
        categorias.add("Administrador");
        categorias.add("Conductor");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, categorias);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        user=findViewById(R.id.ingresoUsuario);
        nombre=findViewById(R.id.ingresoNombre);
        apellido=findViewById(R.id.ingresoApellido);
        correo=findViewById(R.id.ingresoCorreo);
        password=findViewById(R.id.ingresoPassword);

        //Se inicializa la base de datos de firebase
        inicializarFirebase();

    }

    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }


    //metodo para validar el ingreso del usuario es unico y no se repite en la base de datos
    private void validarUsuarioIngreso() {
        databaseReference.child("UsersRegis").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("OSSS");
                usuario userIngresado = new usuario();
                userIngresado.setUsuario(datoUser);
                userIngresado.setNombre(datoNombre);
                userIngresado.setApellidos(datoApellido);
                userIngresado.setCorreo(datoCorreo);
                userIngresado.setClave(datoContra);
                String infoSel = spinner.getSelectedItem().toString();
                userIngresado.setClase(infoSel);

                if (!snapshot.hasChild(userIngresado.getUsuario())) {
                    databaseReference.child("UsersRegis").child("Usuarios").child(userIngresado.getUsuario()).child("info").setValue(userIngresado);
                    //databaseReference.child("UsersRegis").child(per.getUsuario()).setValue(0);
                    Toast.makeText(Registro.this, "Agregado", Toast.LENGTH_SHORT).show();
                    if (infoSel.equals("Conductor")){
                        databaseReference.child("UsersRegis").child("Usuarios").child(userIngresado.getUsuario()).child("localizacion").child("Longitud").setValue(0.0);
                        databaseReference.child("UsersRegis").child("Usuarios").child(userIngresado.getUsuario()).child("localizacion").child("Latitud").setValue(0.0);
                    }
                    vaciarCeldas();
                } else {
                    user.setError("Se usuario ya en uso");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean ingresoValidaciones(){
        datoUser= user.getText().toString().toLowerCase().trim();
        datoNombre=nombre.getText().toString().trim();
        datoApellido= apellido.getText().toString().trim();
        datoCorreo=correo.getText().toString().trim();
        datoContra= password.getText().toString().trim();


        if (datoUser.equals("") || datoNombre.equals("") || datoApellido.equals("") ||
                datoCorreo.equals("")|| datoContra.equals("")){
            if (datoUser.equals(""))
                user.setError("Se requiere el Usuario");
            if (datoNombre.equals(""))
                nombre.setError("Se requiere el Nombre");
            if (datoCorreo.equals(""))
                correo.setError("Se requiere el Correo");
            if (datoContra.equals(""))
                password.setError("Se requiere la Contraseña");
            if (datoApellido.equals(""))
                apellido.setError("Se requiere el Apellido");

            return false;
        }

        else
            return true;

    }

    private void vaciarCeldas(){
        user.setText("");
        nombre.setText("");
        apellido.setText("");
        correo.setText("");
        password.setText("");
    }

    public void regresarMenuPrincipal(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.drawable.edit_aviso);
        builder.setMessage("Seguro quiere regresar?");
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

    public void registrar(View v){
        if (ingresoValidaciones()) {
            System.out.println("OSSS");
            validarUsuarioIngreso();
        }
    }
}