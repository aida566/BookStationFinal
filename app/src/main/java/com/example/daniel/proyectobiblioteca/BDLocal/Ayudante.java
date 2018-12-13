package com.example.daniel.proyectobiblioteca.BDLocal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Ayudante extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "biblioteca.sqlite"; //Nombre del archivo en el que vamos adaptador guardar la bd, cualquier extensión vale.
    public static final int DATABASE_VERSION = 1; //Siempre se empieza por la versión uno, va incrementando. -----
    static final String TAG = "XYZ";

    public Ayudante(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.v(TAG, "onCreate");

        db.execSQL(Contrato.TablaLectura.SQL_CREAR_LECTURA_V1);
        db.execSQL(Contrato.TablaAutor.SQL_CREAR_AUTOR_V1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
