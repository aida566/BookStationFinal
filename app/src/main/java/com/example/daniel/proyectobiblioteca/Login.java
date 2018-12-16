package com.example.daniel.proyectobiblioteca;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.daniel.proyectobiblioteca.Firebase.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    private EditText txEmail;
    private EditText txPassword;
    private Button btIniciarSesion;
    private Button btRestablecerPass;
    private ImageView imagenUsuario;
    private Button btRedirectRegister;
    private Firebase firebase;
    private TextInputLayout tlLogin;
    private TextInputLayout tlPass;
    private FirebaseAuth autentificador;
    private CheckBox checkBoxGuardarSesion;
    PreferenciasCompartidas preferencias;

    private String prefEmail;
    private String prefPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inicializar();

        /*
        -Las preferencias devuelven en un String con el user y la contraseña en la misma bvariable separado por un guion
        los separamos en un array para coger los valores por separado y mandarlos a iniciar sesion */

       if (preferencias.getSesion() != null){
        String sesion =preferencias.getSesion();
        String[] arraySesion = sesion.split("-");
            try {
                prefEmail = arraySesion[0];
                prefPassword = arraySesion[1];
              iniciarSesion(prefEmail, prefPassword);
            }catch (ArrayIndexOutOfBoundsException ex){
               // Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }

        btIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txEmail.getText().toString().isEmpty() && !txPassword.getText().toString().isEmpty()) {
                    iniciarSesion(txEmail.getText().toString(), txPassword.getText().toString());
                } else {
                    controlErrores(txEmail.getText().toString(),
                            txPassword.getText().toString());
                }
            }
        });

        btRestablecerPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent restablecer = new Intent(Login.this, RestablecerPassword.class);
                startActivity(restablecer);
            }
        });

        btRedirectRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(getApplicationContext(), Register.class);
                startActivity(register);
            }
        });
        checkBoxGuardarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxGuardarSesion.isChecked()){


                }
            }
        });


    }



    public void inicializar(){
        txEmail=findViewById(R.id.txEmail_actLogin);
        txPassword=findViewById(R.id.txPassword_actLogin);
        btIniciarSesion=findViewById(R.id.bt_login);
        btRestablecerPass=findViewById(R.id.bt_olvidar_contra);
        tlLogin = findViewById(R.id.til_username);
        tlPass = findViewById(R.id.til_password);
        btRedirectRegister = findViewById(R.id.bt_redirect_register);

        imagenUsuario=findViewById(R.id.image_actLogin);
        checkBoxGuardarSesion=findViewById(R.id.checkBoxGuardarSesion);
        preferencias = new PreferenciasCompartidas(getApplicationContext());

        //-----firebase----
        FirebaseApp.initializeApp(this);
        firebase = new Firebase(getApplicationContext());
        autentificador=  FirebaseAuth.getInstance();

    }


    public void controlErrores(String user, String pass){
        if (user.isEmpty()){
            tlLogin.setError(getString(R.string.email_vacio));
        }
        else if (pass.isEmpty()){
            tlPass.setError(getString(R.string.password_vacia));
        }
    }


    public void iniciarSesion(String email, String password){
        final String finalEmail = email;
        final String  finalPass=password;

        autentificador.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Sesión iniciada", Toast.LENGTH_SHORT).show();
                    if (checkBoxGuardarSesion.isChecked()){
                       preferencias.guardarPreferencias(finalEmail, finalPass);
                    }
                    Intent i = new Intent(Login.this, Lecturas.class);
                    startActivity(i);
                } else {
                    try{
                        preferencias.eliminarPreferencias();
                    } catch (NullPointerException ex){
                    }
                    Toast.makeText(Login.this, "El usuario o contraseña no son correctos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}