package com.edwinbustamante.gruposcochalos;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.edwinbustamante.gruposcochalos.Objetos.FirebaseReferences;
import com.edwinbustamante.gruposcochalos.Objetos.GrupoMusical;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity {

    private RecyclerView recyclerViewGrupos;
    private RecyclerViewAdaptadorPrincipal recyclerViewAdaptadorPrincipal;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference gruposCochalosRef = database.getReference(FirebaseReferences.GRUPOS_COCHALOS_REFERENCE);
    private String TAG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        gruposCochalosRef.child(FirebaseReferences.GRUPO_MUSICAL_REFERENCE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        recyclerViewGrupos = (RecyclerView) findViewById(R.id.recyclerGrupos);
        recyclerViewGrupos.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewAdaptadorPrincipal = new RecyclerViewAdaptadorPrincipal(obtenerGruposMusicales());
        recyclerViewGrupos.setAdapter(recyclerViewAdaptadorPrincipal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public List<GrupoMusical> obtenerGruposMusicales() {
        List<GrupoMusical> listagrupos = new ArrayList<>();
        listagrupos.add(new GrupoMusical(R.drawable.portada, "MISION RESCATE", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "BONANZA", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "PANDORA DE COLOMI", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "MISION RESCATE", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "MISION RESCATE", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "MISION RESCATE", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "BONANZA", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "PANDORA DE COLOMI", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "MISION RESCATE", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "MISION RESCATE", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "BONANZA", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "PANDORA DE COLOMI", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "MISION RESCATE", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "MISION RESCATE", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "MISION RESCATE", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "BONANZA", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "PANDORA DE COLOMI", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "MISION RESCATE", "Cumbia Tropical"));
        listagrupos.add(new GrupoMusical(R.drawable.portada, "MISION RESCATE", "Cumbia Tropical"));
        return listagrupos;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.ingresar_al_sistema:

                Intent ingresar= new Intent(Main.this,LoginActivity.class);
                startActivity(ingresar);

                break;


        }

        return true;
    }
}
