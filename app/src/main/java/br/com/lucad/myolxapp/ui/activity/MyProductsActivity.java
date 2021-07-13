package br.com.lucad.myolxapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.lucad.myolxapp.R;
import br.com.lucad.myolxapp.adapter.AdapterAnuncios;
import br.com.lucad.myolxapp.helper.Constants;
import br.com.lucad.myolxapp.helper.FirebaseHelper;
import br.com.lucad.myolxapp.model.Anuncio;

public class MyProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference anuncioUsuarioRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_products);

        configuracaoInicial();
        initializeComponents();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), AddProductActivity.class));
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        configuraRecycleView();
        recuperaAnuncios();

    }

    private void configuracaoInicial() {
        anuncioUsuarioRef = FirebaseHelper.getFirebaseReference()
                .child(Constants.MEUS_ANUNCIOS)
                .child(FirebaseHelper.getIdUsuario());
    }

    private void recuperaAnuncios() {
        anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                anuncios.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    anuncios.add(ds.getValue(Anuncio.class));
                }
                Collections.reverse(anuncios);
                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void configuraRecycleView() {
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);

        configuraAdapter();
    }

    private void configuraAdapter() {
        adapterAnuncios = new AdapterAnuncios(anuncios, this);
        recyclerAnuncios.setAdapter(adapterAnuncios);
    }

    private void initializeComponents() {
        recyclerAnuncios = findViewById(R.id.recycle_produtos);
    }

}