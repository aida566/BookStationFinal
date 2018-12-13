package com.example.daniel.proyectobiblioteca;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Inicial extends AppCompatActivity {

    Button btIniciarSesion;
    Button btRegistrarse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);

        btIniciarSesion = findViewById(R.id.bt_iniciar_sesion);
        btRegistrarse = findViewById(R.id.bt_registrarse);

        btIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(Inicial.this, Login.class);
                startActivity(login);
            }
        });

        btRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(Inicial.this, Register.class);
                startActivity(register);
            }
        });
    }
}
