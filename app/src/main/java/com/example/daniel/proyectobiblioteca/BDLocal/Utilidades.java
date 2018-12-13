package com.example.daniel.proyectobiblioteca.BDLocal;

import android.content.ContentValues;

import com.example.daniel.proyectobiblioteca.POJOS.Autor;
import com.example.daniel.proyectobiblioteca.POJOS.Lectura;

public class Utilidades {


    public static ContentValues contentValuesLectura(Lectura lectura){
        ContentValues contentValues = new ContentValues();
        //contentValues.put(Contrato.TablaLectura._ID, lectura.getIdLectura());
        contentValues.put(Contrato.TablaLectura.COL_TITULO, lectura.getTitulo());
        contentValues.put(Contrato.TablaLectura.COL_IDAUTOR, lectura.getAutor().getId()); // -----

        if(lectura.getImagen() != null){
            contentValues.put(Contrato.TablaLectura.COL_IMAGEN, lectura.getImagen().toString());
        }
        contentValues.put(Contrato.TablaLectura.COL_FAVORITO, lectura.getValoracion());
        contentValues.put(Contrato.TablaLectura.COL_FECHACOMIENZO, lectura.getFechaInicio().toString());
        contentValues.put(Contrato.TablaLectura.COL_FECHAFINALIZACION, lectura.getFechaFin().toString());
        contentValues.put(Contrato.TablaLectura.COL_VALORACION, lectura.getValoracion());
        contentValues.put(Contrato.TablaLectura.COL_ESTADO, lectura.getEstado());
        contentValues.put(Contrato.TablaLectura.COL_RESUMEN, lectura.getResumen());
        contentValues.put(Contrato.TablaLectura.COL_FBKEY, lectura.getFbkey());


        return contentValues;
    }


    public static ContentValues contentValuesAutor(Autor autor){
        ContentValues contentValues = new ContentValues();
        //contentValues.put(Contrato.TablaAutor._ID, autor.getId());
        contentValues.put(Contrato.TablaAutor.COL_NOMBRE, autor.getNombre());
        return contentValues;
    }


}
