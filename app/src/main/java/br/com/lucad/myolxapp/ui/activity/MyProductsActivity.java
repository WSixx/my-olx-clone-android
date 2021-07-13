package br.com.lucad.myolxapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.lucad.myolxapp.R;
import br.com.lucad.myolxapp.adapter.AdapterAnuncios;
import br.com.lucad.myolxapp.model.Anuncio;

public class MyProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_products);

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

    }

    private void configuraRecycleView() {
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);

        configuraAdapter();
    }

    private void configuraAdapter(Context context) {
        AdapterAnuncios adapterAnuncios = new AdapterAnuncios(anuncios, context);
        recyclerAnuncios.setAdapter(adapterAnuncios);
    }

    private void initializeComponents() {
        recyclerAnuncios = findViewById(R.id.recycle_produtos);
    }

}