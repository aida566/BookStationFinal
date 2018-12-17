package com.example.daniel.proyectobiblioteca;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.daniel.proyectobiblioteca.POJOS.Autor;
import com.example.daniel.proyectobiblioteca.POJOS.Lectura;

import java.util.ArrayList;

import static com.example.daniel.proyectobiblioteca.Lecturas.INICIAR_DETALLE;

public class Filtrar extends AppCompatActivity {

    private android.widget.Spinner spEstado;
    private android.widget.Spinner spAutor;

    private static final String TAG = "FILTRARAIDA";

    private RecyclerView rvFiltrar;
    private AdaptadorLibros adaptador;
    private RecyclerView.LayoutManager lymanager;

    ArrayList<Lectura> lecturasResultado = new ArrayList<>();
    ArrayList<String> nombresAutores = new ArrayList<>();
    private android.widget.Button btBuscar;

    private ArrayList<Lectura> lecturasLeidas = new ArrayList<>();
    private ArrayList<Lectura> lecturasLeyendo = new ArrayList<>();
    private ArrayList<Lectura> lecturasPorLeer = new ArrayList<>();
    private ArrayList<Lectura> lecturasTodas = new ArrayList<>();

    private int estado;
    private String nombreAutor;

    //String de cada estado.
    private String leido, leyendo, porleer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtrar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tbFiltrar);
        setSupportActionBar(toolbar);

        this.btBuscar = (Button) findViewById(R.id.btBuscar);
        this.rvFiltrar = (RecyclerView) findViewById(R.id.rvFiltrar);
        this.spAutor = (Spinner) findViewById(R.id.spAutor);
        this.spEstado = (Spinner) findViewById(R.id.spEstado);

        getLecturasDelIntent();

        getAutores();

        setSpinnerAutores();
        setSpinnerEstados();

        setSpinnersListeners();
        setBtBuscarListener();
        setAdapter(lecturasResultado);
        rvFiltrar.setAdapter(adaptador);
        lymanager = new LinearLayoutManager(this);
        rvFiltrar.setLayoutManager(lymanager);
    }

    private void getAutores() {

        for(Lectura lec: lecturasTodas){


        }
    }

    private void getLecturasDelIntent() {
        Intent i = getIntent();

        lecturasLeidas = i.getParcelableArrayListExtra("leidas");
        lecturasLeyendo= i.getParcelableArrayListExtra("leyendo");
        lecturasPorLeer = i.getParcelableArrayListExtra("porleer");

        lecturasTodas.addAll(lecturasLeidas);
        lecturasTodas.addAll(lecturasLeyendo);
        lecturasTodas.addAll(lecturasPorLeer);
    }

    private void setAdapter(ArrayList<Lectura> nuevaLecturas) {
        //Reemplaza el adaptador por una nueva instancia con un nuevo dataset.
        adaptador = new AdaptadorLibros(nuevaLecturas, new AdaptadorLibros.OnItemClickListener() {
            @Override
            public void onItemClick(Lectura l) {

                Intent i = new Intent(Filtrar.this, LecturaDetalle.class);
                i.putExtra("lectura", l);
                startActivityForResult(i, INICIAR_DETALLE);
            }
        });
        rvFiltrar.setAdapter(adaptador);
    }

    public void setBtBuscarListener(){
        btBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLecturaEspecifica();
                setAdapter(lecturasResultado);
                adaptador.notifyDataSetChanged();
            }
        });
    }

    public void getLecturaEspecifica(){

        switch (estado){
            //Leídos
            case 0:
                getLecturasPorAutor(lecturasLeidas);
                break;

            //Leyendo
            case 1:
                getLecturasPorAutor(lecturasLeyendo);
                break;

            //Por leer
            case 2:
                getLecturasPorAutor(lecturasPorLeer);
                break;

            default:
                //En caso de que no se seleccione nada
                getLecturasPorAutor(lecturasTodas);
                break;
        }

        setAdapter(lecturasResultado);

        Log.v(TAG, "Numero de lecturasResultado encontradas: " + lecturasResultado.size());
    }

    public void getLecturasPorAutor(ArrayList<Lectura> lecturasPorEstado){

        lecturasResultado = new ArrayList<>();

        for(Lectura lec: lecturasPorEstado){

            if(lec.getAutor().getNombre().equalsIgnoreCase(nombreAutor)){

                lecturasResultado.add(lec);
            }

        }

    }

    public void initSpinners(){

    }

    private void setSpinnerEstados() {
        ArrayList<String> estados = new ArrayList<>();

        Resources res = getResources();
        leido = res.getString(R.string.rb_leido);
        leyendo = res.getString(R.string.rb_no_leido);
        porleer = res.getString(R.string.rb_want_to_read);

        estados.add(leido); //position = 0
        estados.add(leyendo); //position = 1
        estados.add(porleer); //position = 2

        ArrayAdapter adaptadorE = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, estados);
        adaptadorE.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adaptadorE);
    }

    private void setSpinnerAutores() {
        int i=0;
        for(Lectura lect: lecturasTodas){
            nombresAutores.add(lect.getAutor().getNombre());
            Log.v(TAG, "autor añadido: " + lect.getTitulo() + " - " + lect.getAutor());
            i++;
        }
        ArrayAdapter adaptadorA = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, nombresAutores );
        adaptadorA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAutor.setAdapter(adaptadorA);
    }

    public void setSpinnersListeners(){
        spAutor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nombreAutor = nombresAutores.get(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                estado = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


}