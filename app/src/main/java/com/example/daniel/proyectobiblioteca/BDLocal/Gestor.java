package com.example.daniel.proyectobiblioteca.BDLocal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.daniel.proyectobiblioteca.POJOS.Autor;
import com.example.daniel.proyectobiblioteca.POJOS.Lectura;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

//en esta clase estarán los métodos para crear, editar etc de la tabla lecturas y de la tabla autores
public class Gestor {

    private Ayudante abd;
    private SQLiteDatabase bd;
    private static final String TAG = "XYZ";

    public Gestor(Context c){
        this(c, true);
    }

    public Gestor(Context c, boolean write){
        //Log.v(String.valueOf(LOG), "constructor gestor");
        this.abd = new Ayudante(c);

        if (write){
            bd = abd.getWritableDatabase();
        }else{
            bd = abd.getReadableDatabase();
        }
    }

    public void cerrar(){
        abd.close();
    }

    //---------Metodos Lecturas---------------

    //Hay que rellenar la clase Utilidades y revisar

    public long insertarLectura(Lectura l){
        return bd.insert(Contrato.TablaLectura.TABLA_NOMBRE, null,
                Utilidades.contentValuesLectura(l));


    }

    public int eliminarLectura(long id){
        String condicion = Contrato.TablaLectura._ID + " = ?";
        String[] argumentos = {id + ""};
        return bd.delete(Contrato.TablaLectura.TABLA_NOMBRE, condicion, argumentos);

    }

    public int eliminarLectura(Lectura l){
        //String condicion = Contrato.TablaContacto._ID + " = ?";
        //String[] argumentos = {c.getId() + ""};
        //return bd.delete(Contrato.TablaContacto.TABLE_NAME, condicion, argumentos);
        return eliminarLectura(l.getIdLectura());
    }

    public int eliminarLectura (String titulo){
        String condicion = Contrato.TablaLectura.COL_TITULO + " = ?";
        String[] argumentos = {titulo};
        return bd.delete(Contrato.TablaLectura.TABLA_NOMBRE, condicion, argumentos);
    }

    public int editarLectura(Lectura l){
        return bd.update(Contrato.TablaLectura.TABLA_NOMBRE,
                Utilidades.contentValuesLectura(l),
                Contrato.TablaLectura._ID + " = ? ",
                new String[]{l.getIdLectura() + ""});
    }



    public Cursor getCursorLecturas(String condicion, String[] argumentos) {
        return bd.query(Contrato.TablaLectura.TABLA_NOMBRE,
                null,
                condicion,
                argumentos,
                null,
                null,
                Contrato.TablaLectura.COL_TITULO + " DESC");
    }

    public Cursor getCursorLecturas(){
        return getCursorLecturas(null, null);
    }

    public Lectura getLectura(Cursor c){

        Lectura lectura = new Lectura();

        lectura.setIdLectura((int) c.getLong(c.getColumnIndex(Contrato.TablaLectura._ID)));
        lectura.setTitulo(c.getString(c.getColumnIndex(Contrato.TablaLectura.COL_TITULO)));

        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd"); //formato para pasar las fechas de String a date

        Date fechaInicio;
        Date fechaFin;

        String fechaI = c.getString((c.getColumnIndex(String.valueOf((Contrato.TablaLectura.COL_FECHACOMIENZO)))));
        String fechaF = c.getString(c.getColumnIndex(String.valueOf((Contrato.TablaLectura.COL_FECHAFINALIZACION))));

        //Log.v(TAG, "String de la fechaI" + fechaI);
        //Log.v(TAG, "String de la fechaF" + fechaF);

        lectura.setFechaInicio(fechaI);
        lectura.setFechaFin(fechaF);

        lectura.setResumen(c.getString(c.getColumnIndex(Contrato.TablaLectura.COL_RESUMEN)));
        lectura.setFav(c.getColumnIndex(Contrato.TablaLectura.COL_FAVORITO));

        //lectura.setImagen(c.getColumnIndex(Contrato.TablaLectura.COLUMN_NAME_IMAGEN));

        long idAutor = c.getInt(c.getColumnIndex(Contrato.TablaLectura.COL_IDAUTOR)); // ---------------------

        Autor autor = getAutor(idAutor);

        lectura.setAutor(autor);

        int valoracion = c.getInt(c.getColumnIndex(Contrato.TablaLectura.COL_VALORACION));

        lectura.setValoracion(valoracion);

        return lectura;
    }

    public Lectura getLectura(long id){
        Lectura lectura = null;
        ArrayList<Lectura> lecturas = getLecturas(Contrato.TablaLectura._ID + " = ?", new String[]{id + ""});
        if(lecturas.size()>0){
            lectura = lecturas.get(0);
        }
        return lectura;
    }

    public Lectura getLectura(String titulo, int idAutor){
        Lectura lectura = null;
        ArrayList<Lectura> lecturas = getLecturas(Contrato.TablaLectura.COL_TITULO + " = ? and " +
                                                Contrato.TablaLectura.COL_IDAUTOR + " = ? ",
                                                new String[]{titulo, idAutor + ""});
        if(lecturas.size()>0){
            lectura = lecturas.get(0);
        }
        return lectura;
    }

    public ArrayList<Lectura> getLecturas(String condicion, String[] argumentos){

        ArrayList<Lectura> lecturas = new ArrayList<>();

        Cursor cursor = getCursorLecturas(condicion, argumentos);

        while(cursor.moveToNext()){
            lecturas.add(getLectura(cursor));
            //Log.v(TAG, "Ha encontrato un match");
        }

        cursor.close();

        return lecturas;
    }

    public ArrayList<Lectura> getLecturas(){

        return getLecturas(null, null);
    }

    public ArrayList<Lectura> getLecturasPorEstado(int estado){

        String condicion;
        String[] argumentos;

        condicion = Contrato.TablaLectura.COL_ESTADO + " = ? ";
        argumentos = new String[]{estado + ""};

        return getLecturas(condicion, argumentos);
    }

    public ArrayList<Lectura> getLecturasPorAutorEstado(int idAutor, int estado){

        String condicion;
        String[] argumentos;

        condicion = Contrato.TablaLectura.COL_IDAUTOR + " = ? and " +
                    Contrato.TablaLectura.COL_ESTADO + " = ? ";;

        argumentos = new String[]{idAutor + "", estado + 1 + ""};

        return getLecturas(condicion, argumentos);
    }

    //------------Metodos para la tabla autores---------------


    public long insertarAutor(Autor a ){
        return bd.insert(Contrato.TablaAutor.TABLA_NOMBRE, null,
                Utilidades.contentValuesAutor(a));
    }

    public long eliminarAutor(int id){
        String condicion = Contrato.TablaAutor._ID + " = ?";
        String[] argumentos = {id + ""};
        return bd.delete(Contrato.TablaAutor.TABLA_NOMBRE, condicion, argumentos);

    }

    public long eliminarAutor(Autor a ){

        return eliminarAutor(a.getId());
    }

    public int eliminarAutor (String nombre){
        String condicion = Contrato.TablaAutor.COL_NOMBRE + " = ?";
        String[] argumentos = {nombre};
        return bd.delete(Contrato.TablaAutor.TABLA_NOMBRE, condicion, argumentos);
    }

    public int editarAutor(Autor a){

        return bd.update(Contrato.TablaAutor.TABLA_NOMBRE,
                Utilidades.contentValuesAutor(a),
                Contrato.TablaAutor._ID + " = ? ",
                new String[]{a.getId() + ""});
    }



    public Cursor getCursorAutor(String condicion, String[] argumentos) {
        return bd.query(Contrato.TablaAutor.TABLA_NOMBRE,
                null,
                condicion,
                argumentos,
                null,
                null,
                Contrato.TablaAutor.COL_NOMBRE + " DESC");
    }

    public Cursor getCursorAutor(){
        return getCursorAutor(null, null);
    }

    public static Autor getAutor(Cursor c){
        Autor autor = new Autor();
        autor.setId((int) c.getLong(c.getColumnIndex(Contrato.TablaAutor._ID)));
        autor.setNombre(c.getString(c.getColumnIndex(Contrato.TablaAutor.COL_NOMBRE)));
        return autor;
    }

    public Autor getAutor(long id){
        Autor autor = null;
        ArrayList<Autor> autores = getAutores(Contrato.TablaAutor._ID + " = ? ", new String[]{id + ""});

        if(autores.size()>0){
            autor = autores.get(0);
        }
        return autor;
    }

    public ArrayList<Autor> getAutores(String condicion, String[] argumentos){
        ArrayList<Autor> autores = new ArrayList<>();
        Cursor cursor = getCursorAutor(condicion, argumentos);
        while(cursor.moveToNext()){
            autores.add(getAutor(cursor));
        }
        cursor.close();

        //Log.v(TAG, "Tamaño autores: " + autores.size());
        return autores;
    }

    public ArrayList<Autor> getAutores(String condicion){
        ArrayList<Autor> autores = new ArrayList<>();
        Cursor cursor = getCursorAutor(condicion, null);
        cursor.moveToFirst();
        while(cursor.moveToNext()){
            autores.add(Gestor.getAutor((cursor)));
        }
        cursor.close();
        return autores;
    }

    public ArrayList<Autor> getAutores(){

        return getAutores(null, null);
    }

    public int getLastIDAutor() {

        final String MY_QUERY = "SELECT MAX(_id) FROM " + Contrato.TablaAutor.TABLA_NOMBRE;
        Cursor cur = bd.rawQuery(MY_QUERY, null);

        cur.moveToFirst();

        int ID = cur.getInt(0);

        cur.close();

        return ID;
    }
}



