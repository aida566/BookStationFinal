package com.example.daniel.proyectobiblioteca;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.print.PrintManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.daniel.proyectobiblioteca.BDLocal.Ayudante;
import com.example.daniel.proyectobiblioteca.BDLocal.Gestor;
import com.example.daniel.proyectobiblioteca.Firebase.Firebase;
import com.example.daniel.proyectobiblioteca.POJOS.Autor;
import com.example.daniel.proyectobiblioteca.POJOS.Contacto;
import com.example.daniel.proyectobiblioteca.POJOS.Lectura;

//--

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LecturaDetalle extends AppCompatActivity {

    private static final String TAG = "XYZ";

    private ImageView imagenLibro;
    private TextInputEditText txNombreLibro, txAutor;
    private EditText txResumen;
    private Button btGuardar, btSelImagen; //Se va a poner en la toolbar
    private RatingBar rbValoracion;
    private ToggleButton tbFavorito;
    private Calendar c;
    private DatePickerDialog dpd;
    private EditText etFechaInicio, etFechaFin;
    private ArrayList<Contacto> listaContactos = new ArrayList<>();

    private static final int SELECCIONAR_IMAGEN = 2;
    final private int REQUEST_CODE_PERMISSIONS = 1;
    int permiso;

    private Lectura lec;

    private Menu menu;
    private RadioGroup radioGroup;
    private RadioButton radioButton1, radioButton2, radioButton3;
    private Uri uriImagen;
    private boolean editar ; /*variable que se usará para saber cuando en el menu de la aplicación , estara habilitado
    //                                el boton de editar o el de guardar, según el usuario esté editando  libro o no */


    private Ayudante ayudante;
    private Gestor gestor;

    //modo detalle cando se viene con una lectura llena
    //modo add cuando se viene con una lectura vacia
    private String modo;

    Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectura_detalle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detalle);
        setSupportActionBar(toolbar);

        Resources res = getResources();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setLogo(res.getDrawable(R.mipmap.ic_logo));

        inicializar();
        toggleOnClick();
        datePicker();

        firebase = new Firebase(getApplicationContext());
        btSelImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarImagen();
            }
        });

        /*  Dependiendo de si la lectura está vacía o no sabremos si
            debemos poner el modo detalle o el modo editar
         */
        lec = getIntent().getParcelableExtra("lectura");

        if (!lec.getTitulo().isEmpty()){
            Log.v(TAG, "Se envia lectura con datos.");
            //Se visualizara el detalle de la carta que se le ha pasado
            asignaLectura(lec);
            editar = false; //Controla los botones del menú
            deshabilitarEdicion();     //como se quiere ver el detalle se dehabilita la edicion de los edit text
            modo = "detalle";
        }else{
            Log.v(TAG, "Se envia lectura vacía.");
            // Se activara la edicion para añadir una nueva lectura
            editar = true;
            habilitarEdicion();
            modo = "add";
        }

        if(savedInstanceState != null){
            Log.v(TAG, "onSavedInstanceState no es null");
            txNombreLibro.setText(savedInstanceState.getString("titulo"));
            txAutor.setText(savedInstanceState.getString("nombreAutor"));
            tbFavorito.setChecked(savedInstanceState.getBoolean("fav"));
            etFechaInicio.setText(savedInstanceState.getString("fechaInicio"));
            etFechaFin.setText(savedInstanceState.getString("fechaFin"));
            rbValoracion.setRating(savedInstanceState.getInt("valoracion"));
            txResumen.setText(savedInstanceState.getString("resumen"));
            radioGroup.check(savedInstanceState.getInt("estado"));
            if(savedInstanceState.getString("uriImagen") != null){
                imagenLibro.setImageURI(Uri.parse(savedInstanceState.getString("uriImagen")));
            }
        }else{}
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String titulo = txNombreLibro.getText().toString();
        String nombreAutor = txAutor.getText().toString();
        boolean fav = tbFavorito.isChecked();
        String fechaInicio = etFechaInicio.getText().toString(); //cambiar a edittext
        String fechaFin = etFechaFin.getText().toString(); //cambiar a edittext
        int valoracion = Math.round(rbValoracion.getRating());
        String resumen = txResumen.getText().toString();

        //Comprobamos el estado.
        int btCheckedID = radioGroup.getCheckedRadioButtonId();
        int estado;

        switch (btCheckedID){
            case R.id.radioButton:
                estado = 1;
                break;

            case R.id.radioButton2:
                estado = 2;
                break;

            case R.id.radioButton3:
                estado = 3;
                break;

            default:
                estado = 1;
                break;
        }

        outState.putString("titulo", titulo);
        outState.putString("nombreAutor", nombreAutor);
        outState.putBoolean("fav", fav);
        outState.putString("fechaInicio", fechaInicio);
        outState.putString("fechaFin", fechaFin);
        outState.putInt("valoracion", valoracion);
        outState.putString("resume", resumen);
        outState.putInt("estado", estado);

        if(uriImagen != null){
            outState.putString("uriImagen", uriImagen.toString());
        }
    }

    public void inicializar(){
        imagenLibro=findViewById(R.id.imageView4);
        txNombreLibro=findViewById(R.id.txNombreLibro);
        txAutor=findViewById(R.id.txNombreAutor);
        txResumen=findViewById(R.id.txResumen);
        rbValoracion = findViewById(R.id.ratingBar);
        tbFavorito = findViewById(R.id.toggleButton3);
        etFechaInicio = findViewById(R.id.et_fecha_inicio);
        etFechaFin = findViewById(R.id.et_fecha_fin);
        radioGroup = findViewById(R.id.radioGroup);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        btSelImagen = findViewById(R.id.btSelImagen);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECCIONAR_IMAGEN){
           if(resultCode == RESULT_OK){
               uriImagen = data.getData();
               imagenLibro.setImageURI(uriImagen);
               LecturaDetalle.this.grantUriPermission(LecturaDetalle.this.getPackageName(), uriImagen, Intent.FLAG_GRANT_READ_URI_PERMISSION);
           }
        }
    }

    /* Método que que se ejecuta cada vez que se va a mostrar el menú de la aplicación */
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        Log.v(TAG, "Entra en el onPrepare");
        super.onPrepareOptionsMenu(menu);
        if (editar) {  //Si estamos editando, en el menu solo debe aparecer la opción guardar
            changeIconSave();
        } else if (!editar) {  // Si no estamos editando, en el menu solo debe aparecer la opción editar
            changeIconEdit();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(TAG, "Entra en onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_detalle, menu);
        this.menu = menu;
        return true;
    }



    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opcion_guardar:
                Boolean guardada = guardarLectura();
                if(guardada){ //Si no se ha guardado correctamente no se harán los cambios
                                //Es decir, se podrá seguir editando
                    deshabilitarEdicion();
                    editar = true;
                    changeIconEdit();
                }
                return true;

            case R.id.opcion_editar:
                editar = false;
                changeIconSave();
                habilitarEdicion();
                return true;

            case R.id.opcion_eliminar:
                eliminaLectura();
                return true;

            case R.id.opcion_imprimir:
                doPrint();
                return true;

            case R.id.opcion_compartir:
                compartir();
                return true;
        }
        return false;
    }

    private void eliminaLectura() {
        gestor = new Gestor(this);
        Log.v(TAG, "Lectura que llega a eliminar; " + lec.getTitulo());
        int numF = gestor.eliminarLectura(lec.getIdLectura());
        Log.v(TAG, "Lectura eliminada: " + numF);
        int valorResult = LecturaDetalle.RESULT_OK;
        Intent i = new Intent();
        setResult(valorResult, i);
        Log.v(TAG, "Sale de LecturaDetalle");
        finish();
    }

    private void changeIconEdit() {
        menu.findItem(R.id.opcion_guardar).setVisible(false).setEnabled(false);
        menu.findItem(R.id.opcion_editar).setVisible(true).setEnabled(true);
        menu.findItem(R.id.opcion_eliminar).setVisible(true).setEnabled(true);
        menu.findItem(R.id.opcion_imprimir).setVisible(true).setEnabled(true);
    }

    private void changeIconSave() {
        menu.findItem(R.id.opcion_editar).setVisible(false).setEnabled(false);
        menu.findItem(R.id.opcion_guardar).setVisible(true).setEnabled(true);
        menu.findItem(R.id.opcion_eliminar).setVisible(false).setEnabled(false);
        menu.findItem(R.id.opcion_imprimir).setVisible(false).setEnabled(false);
    }


    public void asignaLectura(Lectura lectura){

        imagenLibro.setImageURI(lectura.getImagen());

        imagenLibro.setImageBitmap( firebase.descargarFotoLibro(lectura));

        txNombreLibro.setText(lectura.getTitulo());
        txAutor.setText(lectura.getAutor().getNombre());
        rbValoracion.setRating(lectura.getValoracion());

        int estado = lectura.getEstado();

        if(estado == 1){
            radioGroup.check(R.id.radioButton1);

        }else if(estado == 2){

            radioGroup.check(R.id.radioButton2);

        }else{

            radioGroup.check(R.id.radioButton3);

        }

        etFechaInicio.setText( lectura.getFechaInicio());
        etFechaFin.setText( lectura.getFechaFin());

        txResumen.setText(lectura.getResumen());
        tbFavorito.setChecked(lectura.getFav());

    }

    public void deshabilitarEdicion() {
        txNombreLibro.setEnabled(false);
        txAutor.setEnabled(false);
        rbValoracion.setIsIndicator(true);
        txResumen.setEnabled(false);
        etFechaInicio.setEnabled(false);
        etFechaInicio.setFocusable(false);
        etFechaFin.setFocusable(false);
        etFechaFin.setEnabled(false);
        //dtpfInicio.setEnabled(false);
        //dtpfFin.setEnabled(false);
        tbFavorito.setEnabled(false);
        radioGroup.setEnabled(false);
        radioButton1.setEnabled(false);
        radioButton2.setEnabled(false);
        radioButton3.setEnabled(false);
        btSelImagen.setEnabled(false);
    }

    public void habilitarEdicion(){
        txNombreLibro.setEnabled(true);
        txAutor.setEnabled(true);
        rbValoracion.setIsIndicator(false);
        txResumen.setEnabled(true);
        etFechaInicio.setEnabled(true);
        etFechaInicio.setInputType(InputType.TYPE_NULL);
        etFechaFin.setInputType(InputType.TYPE_NULL);
        etFechaFin.setEnabled(true);
        //editar=false;
        tbFavorito.setEnabled(true);
        radioGroup.setEnabled(true);
        radioButton1.setEnabled(true);
        radioButton2.setEnabled(true);
        radioButton3.setEnabled(true);
        btSelImagen.setEnabled(true);
    }

    public Boolean guardarLectura(){  //metodo que se usará para guardar una lectura cuando añadimos, o cuando editamos una existente

        //Variable que controlara si se ha guardado correctamente la Lectura o no
        //En caso se ser falsa no cambiaremos el icono de guardar por el de editar
        Boolean guardada = false;

        Resources res = getResources();

        String titulo = txNombreLibro.getText().toString();
        String nombreAutor = txAutor.getText().toString();
        Autor autor = new Autor(nombreAutor);

        //Si el nombre del autor no se ha cambiado le asignaremos el antiguo id.
        //Si se ha modificado pondremos el id = -1.
        if(nombreAutor.equalsIgnoreCase(lec.getAutor().getNombre())){
            autor.setId(lec.getAutor().getId());

        }else{
            autor.setId(-1);
        }

        Log.v(TAG, "Guardar - idAutor: " + autor.getId());

        boolean fav = tbFavorito.isChecked();
        String fechaInicio = etFechaInicio.getText().toString();
        String fechaFin = etFechaFin.getText().toString();
        int valoracion = Math.round(rbValoracion.getRating());
        String resumen = txResumen.getText().toString();

        Boolean tituloCorrecto = !titulo.equalsIgnoreCase("");
        Boolean autorCorrecto = !autor.getNombre().equalsIgnoreCase("");

        if(tituloCorrecto && autorCorrecto){
            /*if(fechaFin.compareTo(fechaInicio)==-1){
                Toast.makeText(this, "La fecha de finalizacion no puede ser anterior", Toast.LENGTH_LONG).show();
            }else {*/
                //Podemos insertar/editar la lectura
                int estado = obtenerEstadoSeleccionado();
                //Key de firebasetemporal
                String key = "";
                Lectura lecturaNueva = new Lectura(titulo, autor, uriImagen, fav, fechaInicio,
                        fechaFin, valoracion, estado, resumen, key);
                Log.v("FAV", lecturaNueva.toString());
                //Variable que controlará si el resultado del activityForResult es correcto o erróneo
                //dependiendo de si se han insertado correctamente los datos en la BD.
                int valorResult;
                //Asignamos el antiguo id a la nueva lectura.
                lecturaNueva.setIdLectura(lec.getIdLectura());
                //Insertar la la lectura a la BD Local.
                ayudante = new Ayudante(this);
                gestor = new Gestor(this, true);
                int filasA = 0;
                Long numA = Long.valueOf("0");

                if (modo.equalsIgnoreCase("detalle")) {

                    Log.v(TAG, "MODO EDITA AUTOR");
                    //Editamos el autor
                    filasA = gestor.editarAutor(autor);

                } else {

                    Log.v(TAG, "MODO INSERTA AUTOR");

                    //Insertamos el autor
                    numA = gestor.insertarAutor(autor);
                }

                if (numA != -1 || filasA != 0) { //Si el autor se inserta correctamente

                    //Cogemos el id que se la ha asignado en la BD y se lo asignamos al objeto autor.
                    int idAutor = gestor.getLastIDAutor();
                    autor.setId(idAutor);

                    //Actualizamos el autor de la nueva lectura
                    lecturaNueva.setAutor(autor);

                    int filasL = 0;
                    Long numL = Long.valueOf("0");
                    /*
                    //--------SUBIDA DE LECTURA Y DE FOTO A FIREBASE
                    Bitmap fotoBitmap=null;
                    try {
                        fotoBitmap = getBitmapFromUri(lecturaNueva.getImagen());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                     if (fotoBitmap != null) {
                        Log.v(TAG, "bitMap no es null");
                         //Insertamos la lectura con imagen
                         firebase.guardarLecturaAsociada(lecturaNueva,fotoBitmap);

                     }else{

                        Log.v(TAG, "bitmap es null");
                    */
                        //Insertamos la lectura sin imagen
                         key = firebase.guardarLecturaAsociada(lecturaNueva);
                     //}

                    lecturaNueva.setFbkey(key);

                    if (modo.equalsIgnoreCase("detalle")) {
                        Log.v(TAG, "MODO EDITA");
                        //Editamos la lectura
                        filasL = gestor.editarLectura(lecturaNueva);

                    } else {
                        Log.v(TAG, "MODO INSERTA");
                        //Insertamos la nueva lectura
                        numL = gestor.insertarLectura(lecturaNueva);
                    }

                    if (numL != -1 || filasL != 0) {
                        Log.v(TAG, "Se ha insertado en la BDLocal");
                        valorResult = LecturaDetalle.RESULT_OK;
                    } else {
                        Log.v(TAG, "No se ha insertado en la BDLocal");
                        gestor.eliminarAutor(autor.getId());
                        valorResult = LecturaDetalle.RESULT_CANCELED;
                    }

                } else {
                    valorResult = LecturaDetalle.RESULT_CANCELED;
                }

                Intent i = new Intent();
                setResult(valorResult, i);
                Log.v(TAG, "Sale de LecturaDetalle");
                finish();
            //}
        }else if(!tituloCorrecto){
            txNombreLibro.setError(res.getString(R.string.error));
            editar = false;
            guardada = false;

        }else if(!autorCorrecto){
            txAutor.setError(res.getString(R.string.error));
            editar = false;
            guardada = false;
        }
        return guardada;
    }

    private int obtenerEstadoSeleccionado() {
        //Comprobamos el estado.
        int btCheckedID = radioGroup.getCheckedRadioButtonId();
        int estado;

        switch (btCheckedID){
            case R.id.radioButton1:
                estado = 1;
                break;

            case R.id.radioButton2:
                estado = 2;
                break;

            case R.id.radioButton3:
                estado = 3;
                break;

            default:
                estado = 1;
                break;
        }
        return estado;
    }

    /*
    //---------Metodo para pasarle la foto al firebase como bitmap
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
    */

    public void toggleOnClick(){
        //Accion del boton favorito
        tbFavorito.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == false){
                    //favorito = true;
                    tbFavorito.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_red_24dp));
                }else {
                    //favorito = false;
                    tbFavorito.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_favorite_red_24dp));
                }
            }
        });
    }

    public void datePicker(){
        etFechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFecha("inicio");
            }
        });
        etFechaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFecha("fin");
            }
        });
    }

    public void cargarImagen(){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, SELECCIONAR_IMAGEN);
    }

    public void seleccionarFecha(final String fecha){
        c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        dpd = new DatePickerDialog(LecturaDetalle.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int mYear, int mMonth, int mDayOfMonth) {
                if(fecha.equalsIgnoreCase("inicio")){
                    etFechaInicio.setText(mYear + "/" + (mMonth+1) + "/" + mDayOfMonth);
                }else if(fecha.equalsIgnoreCase("fin")){
                    etFechaFin.setText(mYear + "/" + (mMonth+1) + "/" + mDayOfMonth);                }
            }
        }, year, month, day);
        dpd.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void doPrint(){
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        String jobName = this.getString(R.string.app_name) + lec.getTitulo();
        printManager.print(jobName, new Imprimir(this,lec), null);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void compartir(){
        permisos();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permisos(){
        permiso = checkSelfPermission(Manifest.permission.READ_CONTACTS);
        if(permiso != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_PERMISSIONS);
        }else{
            abrirContactos();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void sendEmail(Contacto c, Lectura l){
        String email = getEmail(c, this);

        String[] TO = {(email != null?email:"")};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email para " + c.getNombre());
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hola " + c.getNombre() + " te recomiendo este libro " +
                l.getTitulo() + ", esta escrito por " + l.getAutor().getNombre() + ". A mi me ha gustado mucho.");

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar email..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(),
                    "No hay forma de enviar el email.", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirContactos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.escoge_contacto);
        getContactos();
        String[] array = new String [listaContactos.size()];
        for (int i = 0; i < listaContactos.size(); i++) {
            array[i] = (listaContactos.get(i).getNombre());
        }

        builder.setItems(array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendEmail(listaContactos.get(which), lec);
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }

    public static String getEmail(Contacto c, Context context){
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,ContactsContract.CommonDataKinds.Email.CONTACT_ID+" = ?",new String[]{c.getId()+""}, null);
        while (cursor.moveToNext()) {
            return cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
        }
        cursor.close();
        return null;
    }

    public void getContactos(){
        Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        int id = c.getColumnIndex(ContactsContract.Contacts._ID);
        int name = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        while(c.moveToNext()){
            Contacto contacto = new Contacto(c.getLong(id),c.getString(name));
            listaContactos.add(contacto);
        }
        c.close();
    }
}