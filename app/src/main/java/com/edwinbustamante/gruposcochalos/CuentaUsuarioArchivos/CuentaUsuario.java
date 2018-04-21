package com.edwinbustamante.gruposcochalos.CuentaUsuarioArchivos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edwinbustamante.gruposcochalos.ImagenFull.FulImagen;
import com.edwinbustamante.gruposcochalos.LoginActivity;
import com.edwinbustamante.gruposcochalos.Objetos.FirebaseReferences;
import com.edwinbustamante.gruposcochalos.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CuentaUsuario extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth.AuthStateListener mAuthListener;//se encarga de poder guardar la sesion iniciada sino se cierra la sesion
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private int CAMERA_REQUEST_CODE = 0;
    private ProgressDialog progressDialogFotoSubir;
    private Toolbar toolbar;
    private ImageView cuenta_perfil;
    private TextView nombreGrupo, generoMusica;
    private Button cerrarSesion;
    private LinearLayout editMainCuenta;
    private boolean confotodeperfil = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta_usuario);


        mAuth = FirebaseAuth.getInstance();//INSTANCIAMOS
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();


        //database.setPersistenceEnabled(true);//Habilitamos la persistencia de datos de firebase
        mDatabase = database.getReference().child(FirebaseReferences.USERS_REFERENCE);//hacemos referencia a la base de datos usuario tabla que tenemops como referencia en otra clase
        cuenta_perfil = (ImageView) findViewById(R.id.cuenta_perfil);
        progressDialogFotoSubir = new ProgressDialog(this);
        nombreGrupo = (TextView) findViewById(R.id.nombregrupo);
        generoMusica = (TextView) findViewById(R.id.texgeneroMusica);
        cerrarSesion = (Button) findViewById(R.id.cerrar_sesion);

        nombreGrupo.setOnClickListener(this);
        generoMusica.setOnClickListener(this);
        cuenta_perfil.setOnClickListener(this);
        cerrarSesion.setOnClickListener(this);


        /* ESTABLECEMOS UN ESCUCHADOR DE LOS CAMBIOS
        * */
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {//verificamos que el usuario este logueado

                    mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {//obteniendo el identificador de usuario accedemos a su informacionn del usuario
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            /*
                            ANIADIENDO LOS ATRIBUTOS DESDE LA BASE DE DATOS AL XML DE LA CUENTA DE UN USUARIO
                            * */
                            nombreGrupo.setText(dataSnapshot.child("nombre").getValue().toString());
                            generoMusica.setText(dataSnapshot.child("genero").getValue().toString());

                            String imageUrl = dataSnapshot.child("perfil").getValue().toString();
                            if (!imageUrl.equals("default") || TextUtils.isEmpty(imageUrl)) {

                                confotodeperfil = true;
                                Glide.with(CuentaUsuario.this)
                                        .load(imageUrl)
                                        .fitCenter()
                                        // .skipMemoryCache(true)//Almacenando en cache
                                        .centerCrop()
                                        .into(cuenta_perfil);
                                // otro tipo Picasso.with(CuentaUsuario.this).load(Uri.parse(dataSnapshot.child("perfil").getValue().toString())).into(cuenta_perfil);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    //si el usuario no esta loguedo redireccionamos al login
                    startActivity(new Intent(CuentaUsuario.this, LoginActivity.class));
                    finish();
                }
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);//una vez que se inicia la actividad verificara que si el usuario esta logueado
    }

    /**
     * ################################################################################
     * TODOS LOS CLICK AQUI ESTAN
     * ##############################################################################
     */

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nombregrupo:
                /*
                ALERT DIALOG QUE SE LANZA PARA CAMBIAR EL NOMBRE DEL GRUPO MUSICAL

                **/
                final EditText input;
                AlertDialog.Builder dialogoEditNombre = new AlertDialog.Builder(this);
                dialogoEditNombre.setMessage("Desea cambiar el nombre del Grupo Musical..?");
                input = new EditText(this);
                input.setText(nombreGrupo.getText().toString());
                input.setSelection(nombreGrupo.getText().toString().length());
                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});//maximo de caracterres
                dialogoEditNombre.setView(input);

                dialogoEditNombre.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Agarramos el id del usuario siempre verificando que este logueado y cambiamos su atributo de nombre agarrando de los cambios
                        DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());
                        //en la tablas usuario cambiamos el valor del nombre
                        currentUserDB.child("nombre").setValue(input.getText().toString());
                    }
                });
                dialogoEditNombre.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog editarNombre = dialogoEditNombre.create();
                editarNombre.show();
                break;
            case R.id.texgeneroMusica:
                 /*
                ALERT DIALOG QUE SE LANZA PARA CAMBIAR EL NOMBRE DEL GRUPO MUSICAL

                **/
                final EditText inputGenero;
                AlertDialog.Builder dialogoEditGenero = new AlertDialog.Builder(this);
                dialogoEditGenero.setMessage("Desea cambiar el género música..?");
                inputGenero = new EditText(this);
                inputGenero.setText(generoMusica.getText().toString());
                inputGenero.setSelection(generoMusica.getText().toString().length());
                inputGenero.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});//maximo de caracterres
                dialogoEditGenero.setView(inputGenero);

                dialogoEditGenero.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Agarramos el id del usuario siempre verificando que este logueado y cambiamos su atributo de nombre agarrando de los cambios
                        DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());
                        //en la tablas usuario cambiamos el valor del nombre
                        currentUserDB.child("genero").setValue(inputGenero.getText().toString());
                    }
                });
                dialogoEditGenero.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog editarGenero = dialogoEditGenero.create();
                editarGenero.show();
                break;
            case R.id.cuenta_perfil:
               /* Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(intent, "Seleciona una imagen para el Perfil"), CAMERA_REQUEST_CODE);
                    //aqui esperamos un resultado
                }
                */
                //pasando una imagen a otra actividad
                Intent i = new Intent(CuentaUsuario.this, FulImagen.class);
               // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);



                break;
            case R.id.cerrar_sesion:
                if (mAuth.getCurrentUser() != null) {

                    mAuth.signOut();//si el usuario se desloguea cerramos sesion y el escuchador se encarga de cerrar la sesion
                }
                break;
        }

    }
}
