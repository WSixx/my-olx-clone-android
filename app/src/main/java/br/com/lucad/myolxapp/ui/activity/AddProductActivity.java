package br.com.lucad.myolxapp.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import br.com.lucad.myolxapp.R;
import br.com.lucad.myolxapp.helper.Constants;
import br.com.lucad.myolxapp.helper.FirebaseHelper;
import br.com.lucad.myolxapp.helper.Permissoes;
import br.com.lucad.myolxapp.model.Anuncio;
import dmax.dialog.SpotsDialog;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText titleField, descriptionField;
    private CurrencyEditText moneyField;
    private MaskEditText phoneField;
    private ImageView image1, image2, image3;
    private Spinner spinnerEstado, spinnerCategoria;

    Anuncio anuncio;
    private StorageReference storage;
    private AlertDialog alertDialog;

    private String estado;
    private String categoria;
    private String titulo;
    private String valor;
    private String telefone;
    private String descricao;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaURLFotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        //Validar Permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);

        //Firebase init Config
        storage = FirebaseHelper.getFirebaseStorage();

        initializeComponents();
        carregarDadosSpinner();
    }


    private void initializeComponents() {
        titleField = findViewById(R.id.edit_titulo);
        descriptionField = findViewById(R.id.edit_descricao);
        moneyField = findViewById(R.id.edit_valor);
        phoneField = findViewById(R.id.edit_phone);
        spinnerEstado = findViewById(R.id.spinner_estado);
        spinnerCategoria = findViewById(R.id.spinner_categoria);
        image1 = findViewById(R.id.image_cadastro_1);
        image2 = findViewById(R.id.image_cadastro_2);
        image3 = findViewById(R.id.image_cadastro_3);

        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);

        //Conf to pt-br
        Locale locale = new Locale("pt", "BR");
        moneyField.setLocale(locale);
    }

    private void carregarDadosSpinner() {
        //Estado Spinner
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                estados
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);

        //Categoria Spinner
        String[] categorias = getResources().getStringArray(R.array.categoria);
        ArrayAdapter<String> adapterC = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categorias
        );
        adapterC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterC);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_cadastro_1:
                escolherImagem(1);
                break;
            case R.id.image_cadastro_2:
                escolherImagem(2);
                break;
            case R.id.image_cadastro_3:
                escolherImagem(3);
                break;
        }
    }

    public void escolherImagem(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Recupera imagem
            assert data != null;
            Uri imagemSelecionada = data.getData();
            String caimnhoImagem = imagemSelecionada.toString();

            //Configura imagem no ImageView
            if (requestCode == 1) {
                image1.setImageURI(imagemSelecionada);
            } else if (requestCode == 2) {
                image2.setImageURI(imagemSelecionada);
            } else if (requestCode == 3) {
                image3.setImageURI(imagemSelecionada);
            }
            listaFotosRecuperadas.add(caimnhoImagem);

        }
    }


    public void saveProduct() {
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Anúncio")
                .setCancelable(false)
                .build();
        alertDialog.show();
        //Salvar imagem no Storage
        int listaFotosSize = listaFotosRecuperadas.size();
        for (int i = 0; i < listaFotosSize; i++) {
            String urlImagem = listaFotosRecuperadas.get(i);
            saveFotosStorage(urlImagem, listaFotosSize, i);
        }
    }

    private void saveFotosStorage(String urlImagem, int listaFotosSize, int contador) {

        //Criar nó no Storage
        StorageReference imagemAnuncio = storage.child(Constants.IMAGENS)
                .child(Constants.ANUNCIOS)
                .child(anuncio.getIdAnuncio())
                .child(Constants.IMAGEM + contador);

        //Fazer upload do Arq
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> imagemAnuncio.getDownloadUrl().addOnSuccessListener(uri -> {
            String urlConvertida = uri.toString();      //Esta url funciona!!!
            listaURLFotos.add(urlConvertida);

            if (listaURLFotos.size() == listaFotosSize) {
                anuncio.setFotos(listaFotosRecuperadas);
                anuncio.salvar();

                alertDialog.dismiss();
                finish();
            }
        })).addOnFailureListener(failure -> {
            exibirMensagemError("Falha ao fazer upload");
        });
    }


    private Anuncio configuraAnuncio() {
        getAllFields();
        Anuncio anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setDescricao(descricao);
        anuncio.setTitulo(titulo);
        anuncio.setTelefone(telefone);
        anuncio.setValor(valor);

        return anuncio;
    }


    public void validarDadosAnuncio(View view) {

        anuncio = configuraAnuncio();
        valor = String.valueOf(moneyField.getRawValue());


        if (listaFotosRecuperadas.size() == 0) {
            exibirMensagemError("Selecione ao menos uma imagem!");
        } else {
            if (anuncio.getEstado().isEmpty()) {
                exibirMensagemError("Selecione um estado!");
            } else {
                if (anuncio.getCategoria().isEmpty()) {
                    exibirMensagemError("Selecione uma categoria!");
                } else {
                    if (anuncio.getTitulo().isEmpty()) {
                        exibirMensagemError("Selecione um titulo!");
                    } else {
                        if (anuncio.getTelefone().isEmpty() || anuncio.getTelefone().length() < 10) {
                            exibirMensagemError("Selecione um telefone válido!");
                        } else {
                            if (anuncio.getDescricao().isEmpty()) {
                                exibirMensagemError("Selecione uma descricao!");
                            } else {
                                if (valor.isEmpty() || valor.equals("0")) {
                                    exibirMensagemError("Adicione um valor");
                                } else {
                                    saveProduct();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void getAllFields() {
        estado = spinnerEstado.getSelectedItem().toString();
        categoria = spinnerCategoria.getSelectedItem().toString();
        titulo = titleField.getText().toString();
        valor = moneyField.getText().toString();
        telefone = phoneField.getUnMasked();
        descricao = descriptionField.getText().toString();
    }

    private void exibirMensagemError(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissionResult : grantResults) {
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                validationAlert();
            }
        }
    }

    private void validationAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para adicionar um produto é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confimar", (dialog, which) -> {
            finish();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}