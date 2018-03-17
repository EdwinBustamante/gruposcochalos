package com.edwinbustamante.gruposcochalos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Cuenta extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private Toolbar toolbar;

    private FirebaseAuth.AuthStateListener mAuthListener;//se encarga de poder guardar la sesion iniciada sino se cierra la sesion
    private ImageView cuenta_perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("MISION RESCATE");
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();//INSTANCIAMOS
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");//hacemos referencia a la base de datos
        cuenta_perfil = (ImageView) findViewById(R.id.cuenta_perfil);
        cuenta_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        /* ESTABLECEMOS UN ESCUCHADOR DE LOS CAMBIOS
        * */
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            toolbar.setTitle(dataSnapshot.child("name").getValue().toString());// colocando el nombre en el toolbar
                            String imageUrl = dataSnapshot.child("perfil").getValue().toString();
                            if (!imageUrl.equals("default") || TextUtils.isEmpty(imageUrl)) {
                                Picasso.with(Cuenta.this).load(Uri.parse(dataSnapshot.child("perfil").getValue().toString())).into(cuenta_perfil);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cuenta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.cerrar_sesion:

                if (mAuth.getCurrentUser() != null) {

                    Intent cerrar_sesion = new Intent(Cuenta.this, LoginActivity.class);
                    startActivity(cerrar_sesion);
                    mAuth.signOut();//Cerramos sesion de la autentificacion
                    finish();
                }


                break;


        }

        return true;
    }
}
