package com.edwinbustamante.gruposcochalos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseUser;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    // UI references.
    private AutoCompleteTextView in_correo;
    private EditText in_contrasenia;

    private View mProgressView;
    private View mLoginFormView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;//swe encargar de poder guardar la sesion iniciada sino se cierra la sesion


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();//INSTANCIAMOS
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    //aqui estamos yendo a otra actividad revisando simpre que el usuario en la anterior sesion no haya cerrado su sesion
                    Intent i = new Intent(LoginActivity.this, Cuenta.class);
                    startActivity(i);
                    finish();

                }
            }
        };

        // Set up the login form.
        in_correo = (AutoCompleteTextView) findViewById(R.id.correo);
        in_contrasenia = (EditText) findViewById(R.id.contrasenia);

        Button ingresar = (Button) findViewById(R.id.btn_ingresar);
        TextView registrarUsuario = (TextView) findViewById(R.id.registrar);

        ingresar.setOnClickListener(this);// el click esta siendo controlado por un metodo de la clase
        registrarUsuario.setOnClickListener(this);

        mLoginFormView = findViewById(R.id.login_form);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_ingresar:

                String correo = in_correo.getText().toString();
                String contrasenia = in_contrasenia.getText().toString();
                if (!correo.isEmpty() && !contrasenia.isEmpty()) {//compromamos que no este vacio

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

        mAuth.signInWithEmailAndPassword(correo, contrasenia)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Intent i = new Intent(LoginActivity.this, Cuenta.class);
                            startActivity(i);
                            finish();

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Toast.makeText(LoginActivity.this, "Sesion iniciada Correctamente de ", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }

    private void registrar(String correo, String contrasenia) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo, contrasenia);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }
}