package br.com.lucad.myolxapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.santalu.maskara.widget.MaskEditText;

import java.util.Locale;

import br.com.lucad.myolxapp.R;

public class AddProductActivity extends AppCompatActivity {

    private EditText titleField, descriptionField;
    private CurrencyEditText moneyField;
    private MaskEditText phoneField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        initializeComponents();
    }

    private void initializeComponents(){
        titleField = findViewById(R.id.edit_titulo);
        descriptionField = findViewById(R.id.edit_descricao);
        moneyField = findViewById(R.id.edit_valor);
        phoneField = findViewById(R.id.edit_phone);

        //Conf to pt-br
        Locale locale = new Locale("pt", "BR");
        moneyField.setLocale(locale);

    }

    public void saveProduct(View view) {
    }
}