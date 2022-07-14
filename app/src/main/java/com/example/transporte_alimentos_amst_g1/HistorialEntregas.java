package com.example.transporte_alimentos_amst_g1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HistorialEntregas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_entregas);
    }

    public void regresar(View view){
        //Se inicializa el siguiente
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}