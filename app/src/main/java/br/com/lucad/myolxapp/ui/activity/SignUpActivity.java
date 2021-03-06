package br.com.lucad.myolxapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import br.com.lucad.myolxapp.R;
import br.com.lucad.myolxapp.helper.FirebaseHelper;

public class SignUpActivity extends AppCompatActivity {

    private Button buttonAcess;
    private EditText emailField, passwordField;
    private Switch acessType;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeComponents();
        firebaseAuth = FirebaseHelper.getFirebaseAuth();
        buttonAcessClick();

    }

    private void buttonAcessClick() {
        buttonAcess.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Preencha os campos!", Toast.LENGTH_LONG).show();
            } else {
                if (acessType.isChecked()) {
                    singUpUser(email, password);
                } else {
                    signIn(email, password);
                }
            }
        });
    }

    private void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(
                email,
                password
        ).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(SignUpActivity.this, "Erro ao fazer Login: " + task.getException(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SignUpActivity.this, "Logado com sucesso!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), AnuncioActivity.class));
            }
        });
    }

    private void singUpUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(
                email,
                password
        ).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                signUpException(task);
                Toast.makeText(SignUpActivity.this, signUpException(task), Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(SignUpActivity.this, "Cadastro realizado com sucesso", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String signUpException(@NotNull Task<AuthResult> task) {
        String errorException = "";

        try {
            throw Objects.requireNonNull(task.getException());
        } catch (FirebaseAuthWeakPasswordException e) {
            errorException = "Digite uma senha mais forte";
        } catch (FirebaseAuthInvalidCredentialsException e) {
            errorException = "Digite um email v??lido";
        } catch (FirebaseAuthUserCollisionException e) {
            errorException = "Conta j?? existe";
        } catch (Exception e) {
            errorException = "error : " + e.getMessage();
            e.printStackTrace();
        }
        return errorException;

    }

    private void initializeComponents() {
        buttonAcess = findViewById(R.id.button_acesso);
        emailField = findViewById(R.id.edit_cadastro_email);
        passwordField = findViewById(R.id.edit_cadastro_senha);
        acessType = findViewById(R.id.switch_acesso);
    }

}