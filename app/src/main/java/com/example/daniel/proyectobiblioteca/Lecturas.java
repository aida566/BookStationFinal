package com.example.daniel.proyectobiblioteca;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.daniel.proyectobiblioteca.BDLocal.Ayudante;
import com.example.daniel.proyectobiblioteca.BDLocal.Gestor;
import com.example.daniel.proyectobiblioteca.Firebase.Firebase;
import com.example.daniel.proyectobiblioteca.POJOS.Autor;
import com.example.daniel.proyectobiblioteca.POJOS.Lectura;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Lecturas extends AppCompatActivity {

    public static final int INICIAR_DETALLE = 0;
    public static final int INICIAR_ADD = 1;
    private static final String TAG = "XYZ";

    private RecyclerView rvLecturas;
    private AdaptadorLibros adaptador;
    private RecyclerView.LayoutManager lymanager;

    private Ayudante ayudante;
    private Gestor gestor;

    private ArrayList<Lectura> lecturasLeidas = new ArrayList<>(); //primer array donde guardamos los libros leidos
    private ArrayList<Lectura> lecturasLeyendo = new ArrayList<>(); //segundo array donde guardamos los libros no leidos
    private ArrayList<Lectura> lecturasPorLeer = new ArrayList<>(); //tercer array donde guardamos los libros por leer
    private ArrayList<Autor> autores = new ArrayList<>();

    private ArrayList<Lectura> lecturasFirebase = new ArrayList<>();

    PreferenciasCompartidas preferencias ;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        /*
        Número asignado a cada estado:
            read = 1
            not_read = 2
            want_to_read = 3
         */

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.read:

                    adaptador.setArray(lecturasLeidas);
                    adaptador.notifyDataSetChanged();
                    /**/

                    return true;

                case R.id.not_read:

                    adaptador.setArray(lecturasLeyendo);
                    adaptador.notifyDataSetChanged();

                    return true;

                case R.id.want_to_read:

                    adaptador.setArray(lecturasPorLeer);
                    adaptador.notifyDataSetChanged();

                    return true;
            }
            return false;
        }
    };
    private BottomNavigationView navigation;
    private android.support.constraint.ConstraintLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturas);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarLecturas);
        setSupportActionBar(toolbar);

        Resources res = getResources();
        getSupportActionBar().setIcon(res.getDrawable(R.mipmap.ic_logo));
        getSupportActionBar().setTitle("");

        preferencias =  new PreferenciasCompartidas(getApplicationContext());;

        this.container = (ConstraintLayout) findViewById(R.id.container);
        this.navigation = (BottomNavigationView) findViewById(R.id.navigation);
        this.rvLecturas = (RecyclerView) findViewById(R.id.rvLecturas);

        ayudante = new Ayudante(this);
        gestor = new Gestor(this, true);

        //Obtenemos las lecturas de la bd
        //getLecturasBD();

        getLecturasFirebase();
        //clasificaLecturasFireBase();
        
        //Cargamos el adaptador inicialmente con las lectuas leídas
        //setAdapter(lecturasLeidas);
        setAdapter(lecturasLeidas);

        lymanager = new LinearLayoutManager(this);

        rvLecturas.setLayoutManager(lymanager);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Log.v(TAG, "ONCREATE");
    }

    private void setAdapter(ArrayList<Lectura> nuevaLecturas){

        //Reemplaza el adaptador por una nueva instancia con un nuevo dataset.

        adaptador = new AdaptadorLibros(nuevaLecturas, new AdaptadorLibros.OnItemClickListener() {
            @Override
            public void onItemClick(Lectura l) {

                Log.v(TAG, "Lectura clickeada: " + l.getTitulo() + " - " + l.getAutor().getId());

                Intent i = new Intent(Lecturas.this, LecturaDetalle.class);
                i.putExtra("lectura", l);

                startActivityForResult(i, INICIAR_DETALLE);
            }
        });

        rvLecturas.setAdapter(adaptador);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_book) {

            Intent i = new Intent(Lecturas.this, LecturaDetalle.class);

            Lectura lec =  new Lectura();

            //Le pasamos una lectura vacía
            i.putExtra("lectura", lec);

            startActivityForResult(i, INICIAR_ADD);

            return true;
        }

        if (id == R.id.filtrar) {

            Intent i = new Intent(Lecturas.this, Filtrar.class);

            i.putParcelableArrayListExtra("leidas", lecturasLeidas);
            i.putParcelableArrayListExtra("leyendo", lecturasLeyendo);
            i.putParcelableArrayListExtra("porleer", lecturasPorLeer);

            startActivity(i);

            return true;
        }
        if (id == R.id.logout) {
        Firebase firebase = new Firebase(getApplicationContext());
        firebase.cerrarSesion();
        PreferenciasCompartidas pref = new PreferenciasCompartidas(getApplicationContext());
            pref.eliminarPreferencias();
        Intent i = new Intent(Lecturas.this, Inicial.class);
      //  i.putExtra("cerrarSesion", 1);
        startActivity(i);
        return true;
        }

        if (id == R.id.help_menu) {
            Intent i = new Intent(Lecturas.this, Help.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INICIAR_ADD){
            if(resultCode == RESULT_OK){

                //Actualizamos los arrays de lecturas con los nuevos datos de la BD.
                //getLecturasBD();

                getLecturasFirebase();

                //clasificaLecturasFireBase();

                //setAdapter(lecturasLeidas);mmmm

            }
        }else if(requestCode == INICIAR_DETALLE) {
            if (resultCode == RESULT_OK) {

                //Actualizamos los arrays de lecturas con los nuevos datos de la BD.
                //getLecturasBD();

                getLecturasFirebase();

                //clasificaLecturasFireBase();


            }
        }

    }

    public void getLecturasBD(){

        lecturasLeidas = gestor.getLecturasPorEstado(1);
        lecturasLeyendo = gestor.getLecturasPorEstado(2);
        lecturasPorLeer = gestor.getLecturasPorEstado(3);

    }


    public void getLecturasFirebase(){

        lecturasFirebase = new ArrayList<>();

        Log.v(TAG, "getLecturasFirebase");
        Firebase firebase = new Firebase(getApplicationContext());
        FirebaseUser usuario = firebase.getUsuario();
        Log.v(TAG, "getLecturasFirebase " + usuario.getEmail());

        Query listaLibros =
                FirebaseDatabase.getInstance().getReference()
                        .child("/correo/" + usuario.getUid() +"-"+ usuario.getDisplayName()+"/libro/");
                        //.orderByKey();

        listaLibros.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot hijo: dataSnapshot.getChildren()){
                    System.out.println("NODO "+ hijo.getValue().toString());

                    Lectura lecFB = new Lectura();

                    String titulo = (String) hijo.child("titulo").getValue();
                    lecFB.setTitulo(titulo);
                    String nombreAutor = (String) hijo.child("idAutor").getValue();
                    lecFB.setAutor(new Autor(nombreAutor));

                    String imagen = (String) hijo.child("imagen").getValue();

                    if(imagen != null){

                        lecFB.setImagen(Uri.parse(imagen));

                    }else{

                        Log.v("HOLA", "Problema con la uri");
                    }

                    Boolean fav = (Boolean) hijo.child("fav").getValue();
                    lecFB.setFav(fav);
                    String fechaI = (String) hijo.child("fechaInicio").getValue();
                    lecFB.setFechaInicio(fechaI);
                    String fechaF = (String) hijo.child("fechaFin").getValue();
                    lecFB.setFechaFin(fechaF);
                    int valoracion = Integer.parseInt(((Long) hijo.child("valoracion").getValue() + ""));
                    lecFB.setValoracion(valoracion);
                    int estado = Integer.parseInt(((Long) hijo.child("estado").getValue() + ""));
                    lecFB.setEstado(estado);
                    String resumen = (String) hijo.child("resumen").getValue();
                    lecFB.setResumen(resumen);

                    Log.v("HOLA", "DAtos letura pasados: " + lecFB.toString());

                    lecturasFirebase.add(lecFB);

                    Log.v("HOLA", "autor" + lecturasFirebase.get(0).getAutor());
                    Log.v("HOLA", "nombre" + lecturasFirebase.get(0).getAutor().getNombre());

                }
                clasificaLecturasFireBase();
                adaptador.setArray(lecturasLeidas);
                adaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void clasificaLecturasFireBase(){

        Log.v(TAG, "clasifica las lecturas");

        lecturasLeidas = new ArrayList<>();
        lecturasLeyendo = new ArrayList<>();
        lecturasPorLeer = new ArrayList<>();

        Log.v(TAG, "1" + lecturasFirebase.toString());

        for(Lectura lec: lecturasFirebase){

            Log.v(TAG, "Entra en el for");

            int i = 0;

            int estado = lec.getEstado();

            if(estado == 1){

                lecturasLeidas.add(lec);

            }else if(estado == 2){

                lecturasLeyendo.add(lec);

            }else if(estado == 3){
                lecturasPorLeer.add(lec);
            }

        }
    }

}
