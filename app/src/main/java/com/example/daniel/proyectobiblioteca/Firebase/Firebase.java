package com.example.daniel.proyectobiblioteca.Firebase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.daniel.proyectobiblioteca.POJOS.Lectura;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;



public  class Firebase {
    private  final String TAG = "TAGXYZ";
    private  FirebaseAuth autentificador;
    private  FirebaseUser usuario;
    private  FirebaseDatabase  database;
    private   DatabaseReference  reference;
    private Context contexto;

    // private StorageReference mStorage;

    public Firebase(Context c){
        contexto=c;
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        autentificador = FirebaseAuth.getInstance();

    }

    public  boolean autentificar(String email, String password) {

        Toast.makeText(contexto, "Autentificando...", Toast.LENGTH_SHORT).show();

        autentificador.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    usuario = autentificador.getCurrentUser();
                    System.out.println("Sesion iniciada");


                } else {
                    Log.v(TAG, task.getException().toString());
                    System.out.println("Usuario o contraseña incorrectos");
                    usuario = null;
            }
        }
    });

        return usuarioLogueado();
    }

    public void cerrarSesion(){
        autentificador.signOut();
    }

    public boolean usuarioLogueado(){
            FirebaseUser user = autentificador.getInstance().getCurrentUser();
            if (user != null) {
                return true;
            } else {
                return false;
            }

    }

    public  void crearUsuario(String email, String password) {
        autentificador.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                // ((Executor) this, new OnCompleteListener<AuthResult>() { //El de carmelo estaba asi pero da error de casting
                        ( new OnCompleteListener<AuthResult>() { //Sin el executor
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user1 = autentificador.getCurrentUser();
                                    // Guardar en sharedPreferences la información o no, lo podemos hacer mas adelante
                                    //    guardarUsuario(user1);
                                    Toast.makeText(contexto, "Usuario registrado", Toast.LENGTH_SHORT).show();
                                    System.out.println("Usuario registrado " + user1.getUid()  + user1.getEmail());
                                } else {
                                    Log.v(TAG, task.getException().toString());
                                    System.out.println("ERROR");
                                }
                            }
                        });
    }

    public  void guardarLectura(Lectura l){ // guarda un item en el directorio indicado
        Map<String, Object> saveItem = new HashMap<>();
        String key = reference.child("lectura").push().getKey();
        saveItem.put("/lecturas/" + key + "/", l.toMap());
        reference.updateChildren(saveItem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.v(TAG, "onComplete");
                }else{
                    System.out.println("EXCEPTION " +task.getException().toString());
                    //  Log.v(TAG, task.getException().toString());
                }
            }
        });
    }

    public  void guardarLecturaAsociada(Lectura l, Bitmap foto){ // guarda un item en el directorio de usuario
        //   Item i = new Item("3", "nombre3", "mensaje3");
        Map<String, Object> saveItem = new HashMap<>();
        FirebaseUser usuarioActual= autentificador.getCurrentUser();
        String key = reference.child("lectura").push().getKey();

        saveItem.put("/correo/" + usuarioActual.getUid() +"-"+ usuarioActual.getEmail()+ "/libro/" + key + "/", l.toMap());
        reference = database.getReference();
        reference.updateChildren(saveItem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.v(TAG, "LECTURA GUARDADA");
                }else{
                    System.out.println("ERROR LECTURAASOCIADA " +task.getException().toString());
                }
            }
        });

        //le pasamos al metodo una lectura, y un bitmap de foto, si la foto no es null, la sube a firebase
        if (foto != null){
            subirFotoLibro(foto, l.getImagen(), l.getTitulo());
        }
    }

    public void subirFotoLibro(Bitmap bitmap, Uri uri, String titulo) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

      //  StorageReference filePath = storageRef.child(("/Images/"+titulo+"/")).child(uri.getLastPathSegment()); //creamos carpeta fotos dentro de firebase

        StorageReference filePath = storageRef.child(("/Images/"+titulo+"/")); //creamos carpeta fotos dentro de firebase

        //filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        //Bitmap bitmap = subirFoto.getDrawingCache();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        bmOptions.inSampleSize = 1;
        bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bmOptions.inJustDecodeBounds = false;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        final byte[] foto = baos.toByteArray();

        UploadTask uploadTask = filePath.putBytes(foto);

        //  Y para subir la foto a firebase reduciendo el tamaño (por ejemplo de 3.000 kb a 215 kb) ojo! sin reducir las propiedades de ancho y largo :

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("SE HA SUBIDO LA FOTO");
            }
        });
    }


    public Bitmap descargarFotoLibro(Lectura l) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
      //  StorageReference islandRef = storageRef.child("images/island.jpg");

        StorageReference  islandRef = storageRef.child("/Images/"+l.getTitulo());
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                System.out.println("DESCARGADAAAAA");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


        File foto =localFile; // Your image file
        String filePath = localFile.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
       return bitmap;

    }


    public  void guardarUsuario(FirebaseUser user){
        Map<String, Object> saveUser = new HashMap<>();
        saveUser.put("/correo/" + user.getUid(), user.getEmail());
        reference.updateChildren(saveUser);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                String key = dataSnapshot.getKey();
                Log.v(TAG, value.toString() + key);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.v(TAG, error.toException().toString());
            }
        });
    }
}
