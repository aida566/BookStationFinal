package com.example.daniel.proyectobiblioteca.POJOS;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Lectura implements Parcelable {

    private int idLectura, valoracion, estado;
    private String titulo, resumen, fbkey;
    private Autor autor;
    private Uri imagen;
    private String fechaInicio, fechaFin;
    private boolean fav;

    public Lectura(String titulo, Autor autor, Uri imagen, boolean fav, String fechaInicio, String fechaFin,
                   int valoracion, int estado, String resumen, String fbkey) {

        this.titulo = titulo;
        this.autor = autor;
        this.imagen = imagen;
        this.fav = fav;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.valoracion = valoracion;
        this.estado = estado;
        this.resumen = resumen;
        this.fbkey = fbkey;
    }

    public Lectura(){

        this.titulo = "";
        this.autor = new Autor();
        this.imagen = null;
        this.fav = false;
        this.fechaInicio = "";
        this.fechaFin = "";
        this.valoracion = 0;
        this.estado = 1;
        this.resumen = "";
        this.fbkey = "";
    }

    protected Lectura(Parcel in) {
        idLectura = in.readInt();
        titulo = in.readString();
        autor = in.readParcelable(Autor.class.getClassLoader());
        imagen = in.readParcelable(Uri.class.getClassLoader());
        fav = in.readByte() != 0;
        //Pongo las fechas como Strings ya que no hay opción para Date.
        fechaInicio = in.readString();
        fechaFin = in.readString();
        valoracion = in.readInt();
        estado = in.readInt();
        resumen = in.readString();
        fbkey = in.readString();
    }

    public static final Creator<Lectura> CREATOR = new Creator<Lectura>() {
        @Override
        public Lectura createFromParcel(Parcel in) {
            return new Lectura(in);
        }

        @Override
        public Lectura[] newArray(int size) {
            return new Lectura[size];
        }
    };

    public int getIdLectura() {
        return idLectura;
    }

    public Lectura setIdLectura(int idLectura) {
        this.idLectura = idLectura;
        return this;  //lo he pouesto como lo tenia carmelo en su ejemplo que contectaba con la base de datos
    }

    public int getValoracion() {
        return valoracion;
    }

    public void setValoracion(int valoracion) {
        this.valoracion = valoracion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public Uri getImagen() {
        return imagen;
    }

    public void setImagen(Uri imagen) {
        this.imagen = imagen;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean getFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    @Exclude
    public void setFav(int fav) {
        this.fav  = (fav != 0);
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getFbkey() {
        return fbkey;
    }

    public void setFbkey(String fbkey) {
        this.fbkey = fbkey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idLectura);
        dest.writeString(titulo);
        dest.writeParcelable(autor, flags);
        dest.writeParcelable(imagen, flags);
        dest.writeByte((byte) (fav ? 1 : 0));
        dest.writeString(fechaInicio);
        dest.writeString(fechaFin);
        dest.writeInt(valoracion);
        dest.writeInt(estado);
        dest.writeString(resumen);
        dest.writeString(fbkey);
    }


    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("titulo", titulo);
        result.put("idAutor", autor.getNombre());
        result.put("imagen", imagen.toString());
        result.put("fav", fav);
        result.put("fechaInicio", fechaInicio);
        result.put("fechaFin", fechaFin);
        result.put("valoracion", valoracion);
        result.put("estado", estado);
        result.put("resumen", resumen);

        return result;
    }

    @Override
    public String toString() {
        return "Lectura{" +
                "idLectura=" + idLectura +
                ", valoracion=" + valoracion +
                ", estado=" + estado +
                ", titulo='" + titulo + '\'' +
                ", resumen='" + resumen + '\'' +
                ", fbkey='" + fbkey + '\'' +
                ", autor=" + autor +
                ", imagen=" + imagen +
                ", fechaInicio='" + fechaInicio + '\'' +
                ", fechaFin='" + fechaFin + '\'' +
                ", fav=" + fav +
                '}';
    }
}
