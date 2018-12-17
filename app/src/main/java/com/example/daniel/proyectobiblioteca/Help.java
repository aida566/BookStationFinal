package com.example.daniel.proyectobiblioteca;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class Help extends AppCompatActivity {
private TextView tx;
private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        tx = findViewById(R.id.tvAyuda);
        toolbar = findViewById(R.id.tbHelp);
        setSupportActionBar(toolbar);

        Resources res = getResources();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
}
