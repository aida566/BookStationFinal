package com.example.daniel.proyectobiblioteca;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.daniel.proyectobiblioteca.Firebase.Firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;


public class Register extends AppCompatActivity {
    private Button btRegistrarse;
    private  Button btIniciarSesion;
    private   EditText tvNombreUsuario;
    private  EditText tvPassword;
    private  EditText tvEmail;
    private TextInputLayout tlNameUser;
    private  TextInputLayout tlEmail;
    private  TextInputLayout tlPassword;

    private Firebase firebase;

    private FirebaseAuth autentificador;
    private FirebaseUser usuario;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inicializar();

        btRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comprobarDatos()){
                    registrarUsuario();

                    Intent iniSesion = new Intent(Register.this, Login.class);
                    iniSesion.putExtra("email", tvEmail.getText().toString());
                    iniSesion.putExtra("pass", tvPassword.getText().toString());
                    startActivity(iniSesion);
                }



            }
        });

        btIniciarSesion.setOnClickListener(new View.OnClickListener() { //Boton que lleva a la actividad de login
            @Override
            public void onClick(View v) {
                Intent iniSesion = new Intent(Register.this, Login.class);
                startActivity(iniSesion);
            }
        });
    }



    public void  inicializar(){
        btRegistrarse=(Button)findViewById(R.id.btnRegistrarse);
        btIniciarSesion=findViewById(R.id.btn_iniSesion);
        tvNombreUsuario=findViewById(R.id.txUsername);
        tvEmail=findViewById(R.id.txEmail);
        tvPassword=findViewById(R.id.txPassword);
        tlNameUser = (TextInputLayout) findViewById(R.id.til_username_regi);
        tlEmail = (TextInputLayout) findViewById(R.id.til_email_regis);
        tlPassword = (TextInputLayout) findViewById(R.id.til_password_regis);

        //-----firebase----
        FirebaseApp.initializeApp(this);
        firebase = new Firebase(getApplicationContext());

    }

    public boolean comprobarDatos(){
        if (tvNombreUsuario.getText().toString().isEmpty()){
            tlNameUser.setErrorEnabled(true);
            tlNameUser.setError(getString(R.string.user_vacio));
            return false;
        }else{
            tlNameUser.setErrorEnabled(false);
        }
        if (tvEmail.getText().toString().isEmpty()){
            tlEmail.setErrorEnabled(true);
            tlEmail.setError(getString(R.string.email_vacio));
            return false;
        }else{
            tlEmail.setErrorEnabled(false);
        }
        if (tvPassword.getText().toString().isEmpty()){
            tlPassword.setErrorEnabled(true);
            tlPassword.setError(getString(R.string.password_vacia));
            return false;
        }else{
            tlPassword.setErrorEnabled(false);
        }
        if (tvPassword.getText().toString().length() <= 5){
            tlPassword.setErrorEnabled(true);
            tlPassword.setError(getString(R.string.password_caracteres));
            return false;
        }else{
            tlPassword.setErrorEnabled(false);
        }

        return true;
    }

    public void registrarUsuario(){
        firebase.crearUsuario(tvEmail.getText().toString(), tvPassword.getText().toString());

    }

    private  void guardarUsuario(FirebaseUser user){
        Map<String, Object> saveUser = new HashMap<>();
        saveUser.put("/correo/" + user.getUid(), user.getEmail());
        reference.updateChildren(saveUser);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                String key = dataSnapshot.getKey();
                //  Log.v(TAG, value.toString() + key);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Log.v(TAG, error.toException().toString());
            }
        });
    }
}