package com.example.transporte_alimentos_amst_g1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MenuPrincipal extends AppCompatActivity {
    TextView txt_titulo;
    ImageView imv_photo;
    String nombre_persona, correo_persona, foto_persona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        //Se obtienen los datos del intent pasado
        Intent intent = getIntent();
        HashMap<String, String> informacion_usuario = (HashMap<String, String>)intent.getSerializableExtra("info_user");

        //Se busca los componentes en el layout
        txt_titulo = findViewById(R.id.titulo_bienvenida);
        imv_photo = findViewById(R.id.imagen_persona);

        //Se descomprime la informacion
        nombre_persona = informacion_usuario.get("user_name");
        correo_persona = informacion_usuario.get("user_email");
        foto_persona = informacion_usuario.get("user_photo");

        txt_titulo.setText("Bienvenido "+nombre_persona);
        Picasso.with(getApplicationContext()).load(foto_persona).fit().into(imv_photo);
    }


    public void administrar_paradas(View view){
        //Se inicializa el siguiente
        Intent intent = new Intent(this, AdministrarParadas.class);
        startActivity(intent);
    }


    //Metodo para cerrar sesion
    public void cerrarSesion(View view){
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(this, IngresoSesion.class);
        intent.putExtra("msg", "cerrarSesion");
        startActivity(intent);
    }
}