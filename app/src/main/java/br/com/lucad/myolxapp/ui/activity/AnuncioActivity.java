package br.com.lucad.myolxapp.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.lucad.myolxapp.R;
import br.com.lucad.myolxapp.adapter.AdapterAnuncios;
import br.com.lucad.myolxapp.helper.Constants;
import br.com.lucad.myolxapp.helper.FirebaseHelper;
import br.com.lucad.myolxapp.model.Anuncio;
import dmax.dialog.SpotsDialog;

public class AnuncioActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerAnunciosPublicos;
    private Button buttonRegiao, buttonCategoria;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;
    private AlertDialog alertDialog;
    private Spinner spinnerFiltro;
    private Spinner spinnerCategoria;
    private String filtroEstado = "";
    private String filtroCategoria = "";
    private Boolean filtrandoEstado = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncio);

        initializeComponents();
        firebaseInit();
        configuraRecycleView();
        recuperarAnunciosPublicos();
    }

    public void filtrarEstado(View view) {
        AlertDialog.Builder dialogEstado = createAlertDialog("Selecione um estado");

        spinnerEstadoConfig(dialogEstado);

        dialogEstadoConfirmButton(dialogEstado);
        dialogEstadoNegativeButton(dialogEstado);

        AlertDialog alertDialog = dialogEstado.create();
        alertDialog.show();
    }

    public void filtrarCategoria(View view) {

        if (filtrandoEstado == false) {
            Toast.makeText(this, "Escolha uma região antes", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder dialogCategoria = createAlertDialog("Selecione uma categoria");

            spinnerCategoriaConfig(dialogCategoria);

            dialogCategoriaConfirmButton(dialogCategoria);
            dialogCategoriaNegativeButton(dialogCategoria);

            AlertDialog alertDialog = dialogCategoria.create();
            alertDialog.show();
        }
    }


    private void spinnerEstadoConfig(AlertDialog.Builder dialogEstado) {
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        dialogEstado.setView(viewSpinner);
        spinnerFiltro = viewSpinner.findViewById(R.id.spinner_filtro);
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                estados
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(adapter);
    }

    private void spinnerCategoriaConfig(AlertDialog.Builder dialogCategoria) {
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        dialogCategoria.setView(viewSpinner);
        spinnerFiltro = viewSpinner.findViewById(R.id.spinner_categoria);
        String[] categorias = getResources().getStringArray(R.array.categoria);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categorias
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(adapter);
    }

    private void dialogCategoriaNegativeButton(AlertDialog.Builder dialogCategoria) {
        dialogCategoria.setNegativeButton("Cancelar", (dialog, which) -> {
        });
    }

    private void dialogCategoriaConfirmButton(AlertDialog.Builder dialogCategoria) {
        dialogCategoria.setPositiveButton("OK", (dialog, which) -> {
            filtroCategoria = spinnerCategoria.getSelectedItem().toString();
            recuperarAnunciosPorCategoria();
        });
    }

    private void dialogEstadoNegativeButton(AlertDialog.Builder dialogEstado) {
        dialogEstado.setNegativeButton("Cancelar", (dialog, which) -> {
        });
    }

    private void dialogEstadoConfirmButton(AlertDialog.Builder dialogEstado) {
        dialogEstado.setPositiveButton("OK", (dialog, which) -> {
            filtroEstado = spinnerFiltro.getSelectedItem().toString();
            recuperarAnunciosPorEstado();
            filtrandoEstado = true;
        });
    }


    @NotNull
    private AlertDialog.Builder createAlertDialog(String title) {
        AlertDialog.Builder dialogEstado = new AlertDialog.Builder(this);
        dialogEstado.setTitle(title);
        return dialogEstado;
    }

    private void firebaseInit() {
        firebaseAuth = FirebaseHelper.getFirebaseAuth();
        anunciosPublicosRef = FirebaseHelper.getFirebaseReference()
                .child(Constants.ANUNCIOS);
    }

    private void recuperarAnunciosPorEstado() {
        anunciosPublicosRef = FirebaseHelper.getFirebaseReference()
                .child(Constants.ANUNCIOS)
                .child(filtroEstado);
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                myDialog();
                limparLista();
                for (DataSnapshot categorias : snapshot.getChildren()) {
                    for (DataSnapshot anuncios : categorias.getChildren()) {
                        Anuncio anuncio = anuncios.getValue(Anuncio.class);
                        listaAnuncios.add(anuncio);
                    }
                }
                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void recuperarAnunciosPorCategoria() {
        anunciosPublicosRef = FirebaseHelper.getFirebaseReference()
                .child(Constants.ANUNCIOS)
                .child(filtroEstado)
                .child(filtroCategoria);
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                myDialog();
                limparLista();
                for (DataSnapshot anuncios : snapshot.getChildren()) {
                    Anuncio anuncio = anuncios.getValue(Anuncio.class);
                    listaAnuncios.add(anuncio);
                }
                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void recuperarAnunciosPublicos() {
        myDialog();
        limparLista();
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot estados : snapshot.getChildren()) {
                    for (DataSnapshot categorias : estados.getChildren()) {
                        for (DataSnapshot anuncios : categorias.getChildren()) {
                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            listaAnuncios.add(anuncio);
                        }
                    }
                }
                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void limparLista() {
        listaAnuncios.clear();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_cadastrar:
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                break;
            case R.id.menu_sair:
                firebaseAuth.signOut();
                invalidateOptionsMenu();
                break;
            case R.id.menu_anuncios:
                startActivity(new Intent(getApplicationContext(), MyProductsActivity.class));
                invalidateOptionsMenu();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (firebaseAuth.getCurrentUser() == null) {
            menu.setGroupVisible(R.id.group_deslogado, true);
        } else {
            menu.setGroupVisible(R.id.group_logado, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void configuraRecycleView() {
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);

        configuraAdapter();
    }

    private void configuraAdapter() {
        adapterAnuncios = new AdapterAnuncios(listaAnuncios, this);
        recyclerAnunciosPublicos.setAdapter(adapterAnuncios);
    }

    private void initializeComponents() {
        recyclerAnunciosPublicos = findViewById(R.id.recycle_anuncios_publicos);
    }

    private void myDialog() {
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Crregando Anuncios")
                .setCancelable(false)
                .build();
        alertDialog.show();
    }
}