package br.com.lucad.myolxapp.model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import br.com.lucad.myolxapp.helper.Constants;
import br.com.lucad.myolxapp.helper.FirebaseHelper;

public class Anuncio {

    private String idAnuncio;
    private String estado;
    private String categoria;
    private String titulo;
    private String valor;
    private String telefone;
    private String descricao;
    private List<String> fotos;

    public Anuncio() {
        DatabaseReference anuncioRef = FirebaseHelper.getFirebaseReference()
                .child(Constants.MEUS_ANUNCIOS);
        setIdAnuncio(anuncioRef.push().getKey());
    }

    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }


    public void salvar() {

        String idUsuario = FirebaseHelper.getIdUsuario();
        DatabaseReference anuncioRef = FirebaseHelper.getFirebaseReference()
                .child(Constants.MEUS_ANUNCIOS);

        anuncioRef.child(idUsuario)
                .child(getIdAnuncio())
                .setValue(this);

        savarAnuncioPublico();
    }

    public void savarAnuncioPublico() {

        DatabaseReference anuncioRef = FirebaseHelper.getFirebaseReference()
                .child(Constants.ANUNCIOS);

        anuncioRef.child(getEstado())
                .child(getCategoria())
                .child(getIdAnuncio())
                .setValue(this);
    }

    public void remover() {
        String idUsuario = FirebaseHelper.getIdUsuario();
        DatabaseReference anuncioRef = FirebaseHelper.getFirebaseReference()
                .child(Constants.MEUS_ANUNCIOS)
                .child(idUsuario)
                .child(getIdAnuncio());
        anuncioRef.removeValue();
        removerAnuncioPublico();
    }

    public void removerAnuncioPublico() {
        DatabaseReference anuncioRef = FirebaseHelper.getFirebaseReference()
                .child(Constants.ANUNCIOS)
                .child(getEstado())
                .child(getCategoria())
                .child(getIdAnuncio());
        anuncioRef.removeValue();
    }

}
