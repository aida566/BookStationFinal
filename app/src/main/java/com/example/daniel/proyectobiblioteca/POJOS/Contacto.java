package com.example.daniel.proyectobiblioteca.POJOS;

public class Contacto {

    private long id;
    private String nombre;

    public Contacto(long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public void Contacto(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
