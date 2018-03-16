package com.edwinbustamante.gruposcochalos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    // UI references.
    private AutoCompleteTextView in_correo;
    private EditText in_contrasenia;
    private Button ingresar;
    private TextView registrarUsuario;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private FirebaseAuth.AuthStateListener mAuthListener;//se encarga de poder guardar la sesion iniciada sino se cierra la sesion


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        in_correo = (AutoCompleteTextView) findViewById(R.id.correo);
        in_contrasenia = (EditText) findViewById(R.id.contrasenia);
        ingresar = (Button) findViewById(R.id.btn_ingresar);
        registrarUsuario = (TextView) findViewById(R.id.registrar);

        mAuth = FirebaseAuth.getInstance();//INSTANCIAMOS
        mProgress = new ProgressDialog(this);

        ingresar.setOnClickListener(this);// el click esta siendo controlado por un metodo de la clase
        registrarUsuario.setOnClickListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {//si estamos logueados comenzamos una bnueva actividad
                    Intent i = new Intent(LoginActivity.this, Cuenta.class);
                    startActivity(i);
                    finish();
                }
            }
        };

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_ingresar:

                String correo = in_correo.getText().toString().trim();
                String contrasenia = in_contrasenia.getText().toString().trim();
                if (!TextUtils.isEmpty(correo) && !TextUtils.isEmpty(contrasenia)) {//compromamos que no este vacio
                    iniciarSesion(correo, contrasenia);
                } else {
                    Toast.makeText(this, "LOS CAMPOS DEBEN SER LLENADOS CORRECTAMENTE PARA INGRESAR AL SISTEMA", Toast.LENGTH_SHORT).show();
                }

                break;


            case R.id.registrar:
                Intent inRegis = new Intent(LoginActivity.this, RegistrarUsuario.class);
                startActivity(inRegis);
                break;
        }

    }

    private void iniciarSesion(String correo, String contrasenia) {

        mProgress.setMessage("Ingresando al sistema, espere un momento");
        mProgress.show();
        mAuth.signInWithEmailAndPassword(correo, contrasenia)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgress.dismiss();
                        if (task.isSuccessful()) {

                           /*
                             AQUI YA NO ESTOY LLAMANDO A LA ACTIVIDAD CUENTA PORQUE MI ESCUCHADOR DE MI LOGUO SE ENCARGA DE VER SI ESTOY LOGUEADO O NO

                            */

                            String user = mAuth.getCurrentUser().getUid().toString();

                            Toast.makeText(LoginActivity.this, "Sesion iniciada Correctamente de " + user, Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);// enlaza el cambio de estado
    }


}