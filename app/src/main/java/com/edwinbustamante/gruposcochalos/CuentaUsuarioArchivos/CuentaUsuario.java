package com.edwinbustamante.gruposcochalos.CuentaUsuarioArchivos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edwinbustamante.gruposcochalos.LoginActivity;
import com.edwinbustamante.gruposcochalos.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CuentaUsuario extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener mAuthListener;//se encarga de poder guardar la sesion iniciada sino se cierra la sesion
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private int CAMERA_REQUEST_CODE = 0;
    private ProgressDialog progressDialogFotoSubir;
    private Toolbar toolbar;
    private ImageView cuenta_perfil;
    private TextView nombreGrupo;
    private Button cerrarSesion;
    private String nombreG = "Nombre de grupo";
    private LinearLayout editMainCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta_usuario);


        mAuth = FirebaseAuth.getInstance();//INSTANCIAMOS
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");//hacemos referencia a la base de datos
        cuenta_perfil = (ImageView) findViewById(R.id.cuenta_perfil);
        progressDialogFotoSubir = new ProgressDialog(this);
        nombreGrupo = (TextView) findViewById(R.id.nombregrupo);
        cerrarSesion = (Button) findViewById(R.id.cerrar_sesion);
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {

                    mAuth.signOut();//si el usuario se desloguea cerramos sesion y el escuchador se encarga de cerrar la sesion
                }
            }
        });
        editMainCuenta = (LinearLayout) findViewById(R.id.edit_main_Cuenta);
        editMainCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    Intent i = new Intent(CuentaUsuario.this, EditMainCuenta.class);
                    startActivity(i);

                }

            }
        });
        cuenta_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(intent, "Seleciona una imagen para el Perfil"), CAMERA_REQUEST_CODE);
                    //aqui esperamos un resultado
                }
            }
        });

        /* ESTABLECEMOS UN ESCUCHADOR DE LOS CAMBIOS
        * */
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {//verificamos que el usuario este logueado

                    mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {//obteniendo el identificador de usuario accedemos a su informacionn del usuario
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            nombreG = dataSnapshot.child("name").getValue().toString();
                            nombreGrupo.setText(dataSnapshot.child("name").getValue().toString());
                            String imageUrl = dataSnapshot.child("perfil").getValue().toString();
                            if (!imageUrl.equals("default") || TextUtils.isEmpty(imageUrl)) {
                                Picasso.with(CuentaUsuario.this).load(Uri.parse(dataSnapshot.child("perfil").getValue().toString())).into(cuenta_perfil);
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


}
