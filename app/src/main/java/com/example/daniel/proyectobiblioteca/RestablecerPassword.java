package com.example.daniel.proyectobiblioteca;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.daniel.proyectobiblioteca.Firebase.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class RestablecerPassword extends AppCompatActivity {
    private EditText txEmail;
    private Button btRestablecer;
    private ImageView logo;
    private TextInputLayout tlEmail;
    Firebase firebase;
    FirebaseAuth autentificador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restablecer_password);
        inicializar();
    }

    public void inicializar(){
        txEmail = findViewById(R.id.txEmail_actRestablecer);
        btRestablecer=findViewById(R.id.bt_restablecer);
        logo = findViewById(R.id.image_actRestablecer);
        tlEmail=findViewById(R.id.til_email_restablecer);


        FirebaseApp.initializeApp(this);
        firebase = new Firebase(getApplicationContext());

        autentificador=  FirebaseAuth.getInstance();



        btRestablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txEmail.getText().toString().isEmpty()){
                    restablecerPassword(txEmail.getText().toString());
                }else{
                    tlEmail.setError(getString(R.string.email_vacio));
                }
            }
        });
    }

    public void restablecerPassword(String email){
        autentificador.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RestablecerPassword.this, getString(R.string.correo_enviado_restablecer), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RestablecerPassword.this, getString(R.string.error_restablecer), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}