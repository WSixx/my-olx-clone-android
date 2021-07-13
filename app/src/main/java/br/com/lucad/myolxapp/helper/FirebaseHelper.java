package br.com.lucad.myolxapp.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseHelper {

    private static DatabaseReference firebaseReference;
    private static FirebaseAuth authReference;
    private static StorageReference storageReference;

    public static String getIdUsuario(){
        FirebaseAuth firebaseAuth = getFirebaseAuth();
        return firebaseAuth.getCurrentUser().getUid();
    }

    //retorna referencia do database
    public static DatabaseReference getFirebaseReference(){
        if (firebaseReference == null){
            firebaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return firebaseReference;
    }

    //retorna referencia do FirebaseAuth
    public static FirebaseAuth getFirebaseAuth(){
        if (authReference == null){
            authReference = FirebaseAuth.getInstance();
        }
        return authReference;
    }

    //retorna referencia do FirebaseStorage
    public static StorageReference getFirebaseStorage(){
        if (storageReference == null){
            storageReference = FirebaseStorage.getInstance().getReference();
        }
        return storageReference;
    }

}
