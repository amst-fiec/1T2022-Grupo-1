package com.example.transporte_alimentos_amst_g1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.transporte_alimentos_amst_g1.Clases.ClaseUsando;
import com.example.transporte_alimentos_amst_g1.Clases.usuario;

public class MenuPrincipalConductor extends AppCompatActivity {

    TextView txt_titulo;
    String nombre_persona, status_persona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal_conductor);

        //Se busca los componentes en el layout
        txt_titulo = findViewById(R.id.textoBienvenidaConductor);

        //Se descomprime la informacion de la clase estatica
        usuario user = ClaseUsando.usuarioUsando;
        nombre_persona = user.getNombre();
        status_persona= user.getClase();

        txt_titulo.setText("Bienvenido "+nombre_persona+"\n"
                +"Clase: "+status_persona);
    }


    public void agregar_parada(View view){
        //Se inicializa el siguiente
        Intent intent = new Intent(this, ObservarUbicacion.class);
        startActivity(intent);
    }

    public void registrarAdministrador(View view){
        //Se inicializa el siguiente
        Intent intent = new Intent(this, MostrarParadas.class);
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