package com.example.daniel.proyectobiblioteca.POJOS;

import android.os.Parcel;
import android.os.Parcelable;

public class Autor implements Parcelable {

    private int id;
    private String nombre;

    public Autor(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Autor(String nombre) {
        this.nombre = nombre;
    }

    public Autor() {

        this.id = 0;
        this.nombre = "";

    }

    protected Autor(Parcel in) {
        id = in.readInt();
        nombre = in.readString();
    }

    public static final Creator<Autor> CREATOR = new Creator<Autor>() {
        @Override
        public Autor createFromParcel(Parcel in) {
            return new Autor(in);
        }

        @Override
        public Autor[] newArray(int size) {
            return new Autor[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nombre);
    }
}
