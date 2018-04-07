package com.edwinbustamante.gruposcochalos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.edwinbustamante.gruposcochalos.Objetos.FirebaseReferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrarUsuario extends AppCompatActivity {
    LinearLayout linearLayoutanimacion;
    AnimationDrawable animacion;
    private EditText nombreGrupoRegistro, correoRegistro, pasRegistro1, pasRegistro2;
    private Button registarRegistro;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //############################################################################
        //ANIMACION DEL FONDO
        linearLayoutanimacion = (LinearLayout) findViewById(R.id.fondoregistroanimacion);
        animacion = (AnimationDrawable) linearLayoutanimacion.getBackground();
        animacion.setEnterFadeDuration(4500);
        animacion.setExitFadeDuration(4500);
        animacion.start();
        //############################################################################
        nombreGrupoRegistro = (EditText) findViewById(R.id.nombreGrupoRegistro);
        correoRegistro = (EditText) findViewById(R.id.correoRegistro);
        pasRegistro1 = (EditText) findViewById(R.id.contraseniaRegistro);
        pasRegistro2 = (EditText) findViewById(R.id.contraseniaRegistro2);
        registarRegistro = (Button) findViewById(R.id.btnRegistrarCuenta);

        //instanciamos el autentificador de fire base y tambien el progress Dialog
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);
        registarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });

    }

    private void startRegister() {

        final String nombreGrupo = nombreGrupoRegistro.getText().toString().trim();// con e trim eliminamos los caracteres blancos al inicio y fin de la cadena
        final String correoRegis = correoRegistro.getText().toString().trim();
        final String pas1 = pasRegistro1.getText().toString().trim();
        final String pas2 = pasRegistro2.getText().toString().trim();
        if (!TextUtils.isEmpty(nombreGrupo) && !TextUtils.isEmpty(correoRegis) && !TextUtils.isEmpty(pas1) && !TextUtils.isEmpty(pas2)) {
            if (pas1.equals(pas2)) {
                mProgress.setMessage("Registrando, espere un momento por favor...");
                mProgress.show();//lanzamos el progres Dialog
                mAuth.createUserWithEmailAndPassword(correoRegis, pas1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgress.dismiss();
                        if (task.isSuccessful()) {

                            /*hacemos la llamada a la base de datos
                            * */
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USERS_REFERENCE);
                            DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());
                            currentUserDB.child("name").setValue(nombreGrupo);
                            currentUserDB.child("perfil").setValue("default");

                            String user_id = mAuth.getCurrentUser().getUid();
                            Intent i = new Intent(RegistrarUsuario.this, LoginActivity.class);
                            startActivity(i);
                            mAuth.signOut();//DESLOGUEANDO PARA QUE EL USUARIO INGRESE A SU CIENTA POR PRIMERA VEZmAuth.signOut();//DESLOGUEANDO PARA QUE EL USUARIO INGRESE A SU CIENTA POR PRIMERA VEZ
                            finish();

                            Toast.makeText(RegistrarUsuario.this, "Usuario " + user_id + " registrado exitosamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegistrarUsuario.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Debe llenar todos los campos de manera obligatoria", Toast.LENGTH_SHORT).show();
        }
    }

}
