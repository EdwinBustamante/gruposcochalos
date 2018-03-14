package com.edwinbustamante.gruposcochalos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Cuenta extends AppCompatActivity {

    private Button cerrarSesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);
        cerrarSesion=(Button)findViewById(R.id.cerrar_sesion);

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i=new Intent(Cuenta.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}
