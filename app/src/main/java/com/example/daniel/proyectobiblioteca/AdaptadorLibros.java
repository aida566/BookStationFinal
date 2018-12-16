package com.example.daniel.proyectobiblioteca;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.daniel.proyectobiblioteca.POJOS.Lectura;

import java.util.ArrayList;


public class AdaptadorLibros extends RecyclerView.Adapter<AdaptadorLibros.ViewHolder>{

    private static final String TAG = "XYZ";
    private final OnItemClickListener listener;
    private ArrayList<Lectura> listaLecturas;

    //---Constructor que recibe el array de lecturas----

    public AdaptadorLibros(ArrayList<Lectura> arrayLecturas, OnItemClickListener listener) {
        this.listaLecturas = arrayLecturas;
        this.listener = listener;
    }

    public void setArray(ArrayList<Lectura> arrayLecturas) {
        this.listaLecturas = arrayLecturas;
    }

    @NonNull
    @Override
    public AdaptadorLibros.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_libro, viewGroup, false);
        return new AdaptadorLibros.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorLibros.ViewHolder viewHolder, int i) {
        Lectura lectura = (Lectura) listaLecturas.get(i);
        viewHolder.txTitulo.setText(lectura.getTitulo());
        viewHolder.txAutor.setText(String.valueOf(lectura.getAutor().getNombre()));

        viewHolder.valoracionLectura.setRating(lectura.getValoracion());

        if(lectura.getImagen() != null){

            Log.v(TAG, "En adaptador: uri no es null");
            viewHolder.imagenLectura.setImageURI(lectura.getImagen());
        }

        viewHolder.imagenLectura.setImageURI(lectura.getImagen());

        viewHolder.bind(listaLecturas.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return listaLecturas.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Lectura l);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imagenLectura;
        TextView txTitulo;
        TextView txAutor;
        RatingBar valoracionLectura;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagenLectura=itemView.findViewById(R.id.imagenLecturaRecycler);
            txTitulo=itemView.findViewById(R.id.txTituloRecycler);
            txAutor=itemView.findViewById(R.id.txAutorRecycler);

            valoracionLectura=itemView.findViewById(R.id.ratingBarRecycler);
            valoracionLectura.setIsIndicator(true);

        }

        public void bind(final Lectura l, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.onItemClick(l);
                }
            });
        }
    }
}
