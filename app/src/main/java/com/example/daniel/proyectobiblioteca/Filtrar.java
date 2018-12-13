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

import com.example.daniel.proyectobiblioteca.BDLocal.Ayudante;
import com.example.daniel.proyectobiblioteca.BDLocal.Gestor;
import com.example.daniel.proyectobiblioteca.POJOS.Autor;
import com.example.daniel.proyectobiblioteca.POJOS.Lectura;

import java.util.ArrayList;

import static com.example.daniel.proyectobiblioteca.Lecturas.INICIAR_DETALLE;

public class Filtrar extends AppCompatActivity {

    private android.widget.Spinner spEstado;
    private android.widget.Spinner spAutor;

    private static final String TAG = "XYZ";

    private RecyclerView rvFiltrar;
    private AdaptadorLibros adaptador;
    private RecyclerView.LayoutManager lymanager;

    private Ayudante ayudante;
    private Gestor gestor;

    ArrayList<Lectura> lecturas = new ArrayList<>();
    ArrayList<Autor> autores = new ArrayList<>();
    private android.widget.Button btBuscar;

    private int estado;
    private int idAutor;

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

        ayudante = new Ayudante(this);
        gestor = new Gestor(this, true);

        initSpinners();
        setSpinnersListeners();
        setBtBuscarListener();
        setAdapter(lecturas);
        rvFiltrar.setAdapter(adaptador);
        lymanager = new LinearLayoutManager(this);
        rvFiltrar.setLayoutManager(lymanager);
    }

    private void setAdapter(ArrayList<Lectura> nuevaLecturas) {
        //Reemplaza el adaptador por una nueva instancia con un nuevo dataset.
        adaptador = new AdaptadorLibros(nuevaLecturas, new AdaptadorLibros.OnItemClickListener() {
            @Override
            public void onItemClick(Lectura l) {
                Log.v(TAG, "Lectura clickeada: " + l.getTitulo() + " - " + l.getAutor().getId());
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
                getLecturas();
                setAdapter(lecturas);
                adaptador.notifyDataSetChanged();
            }
        });
    }

    public void getLecturas(){
        lecturas = gestor.getLecturasPorAutorEstado(idAutor, estado);
        Log.v(TAG, "Numero de lecturas encontradas: " + lecturas.size());
    }

    public void initSpinners(){
        //Spinner de los autores
        autores = gestor.getAutores();
        ArrayList<String> nombresAutores = new ArrayList<>();
        for(Autor a: autores){
            nombresAutores.add(a.getNombre());
        }
        ArrayAdapter adaptadorA = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, nombresAutores );
        adaptadorA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAutor.setAdapter(adaptadorA);
        //Spinner de los estados
        ArrayList<String> estados = new ArrayList<>();
        Resources res = getResources();
        estados.add(res.getString(R.string.rb_leido));
        estados.add(res.getString(R.string.rb_no_leido));
        estados.add(res.getString(R.string.rb_want_to_read));
        ArrayAdapter adaptadorE = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, estados);
        adaptadorE.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adaptadorE);
    }

    public void setSpinnersListeners(){
        spAutor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idAutor = autores.get(position).getId();
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