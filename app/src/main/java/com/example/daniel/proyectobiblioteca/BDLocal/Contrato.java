package com.example.daniel.proyectobiblioteca.BDLocal;

import android.provider.BaseColumns;

public class Contrato{

    public Contrato() {
    }

    public static class TablaLectura implements BaseColumns {

        public static final String TABLA_NOMBRE = "lectura";

        public static final String COL_TITULO = "titulo";
        public static final String COL_IDAUTOR = "idAutor";
        public static final String COL_IMAGEN = "imagen";
        public static final String COL_FAVORITO = "fav";
        public static final String COL_FECHACOMIENZO = "fechaInicio";
        public static final String COL_FECHAFINALIZACION = "fechaFin";
        public static final String COL_VALORACION = "valoracion";
        public static final String COL_ESTADO = "estado";
        public static final String COL_RESUMEN = "resumen";
        public static final String COL_FBKEY = "fbkey";

        public static final String SQL_CREAR_LECTURA_V1 =
                "create table " + TABLA_NOMBRE + " (" +
                _ID + " integer primary key, " + //No es necesario ponerle autoincrement por lo visto.
                COL_TITULO + " text NOT NULL, " +
                COL_IDAUTOR+ " integer NOT NULL, " +
                COL_IMAGEN + " text, " +
                COL_FAVORITO + " BOOLEAN, " +
                COL_FECHACOMIENZO + " text, " +
                COL_FECHAFINALIZACION + " text, " +
                COL_VALORACION + " int(1), " +
                COL_ESTADO + " int(1), " +
                COL_RESUMEN + " text, " +
                COL_FBKEY + " text " +
                ");";
    }

    public static class TablaAutor implements BaseColumns{

        public static final String TABLA_NOMBRE = "autor";
        public static final String COL_NOMBRE = "nombre";

        public static final String SQL_CREAR_AUTOR_V1 =
                "create table " + TABLA_NOMBRE + " (" +
                _ID + " integer primary key," + //No es necesario ponerle autoincrement por lo visto.
                COL_NOMBRE + " text NOT NULL" +
                " );";
    }
}
