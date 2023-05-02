package alreyesh.android.scanmarketclient.activities;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.utils.ValidatedInfo;

public class AccountInfoActivity extends AppCompatActivity {
    EditText editTextNombre;
    EditText editTextApellidos;
    EditText editTextCelular;
    EditText editNumDocumento;
    Spinner spinnerTipoDocumento;
    private Button btnSiguiente;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        bindUI();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> categorias = new ArrayList<>();
        categorias.add("DNI");
        categorias.add("RUC");
        categorias.add("CEX");
        categorias.add("Pasaporte");
        ArrayAdapter<String> adapter = new ArrayAdapter< > (this,android.R.layout.simple_spinner_item,categorias);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoDocumento.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        spinnerTipoDocumento.setSelection(0);
        FirebaseUser user = mAuth.getCurrentUser();

        btnSiguiente.setOnClickListener(v -> {
            String txtNombre = editTextNombre.getText().toString();
            String txtApellidos = editTextApellidos.getText().toString();
            String txtCelular = editTextCelular.getText().toString();
            String numDocumento = editNumDocumento.getText().toString();
            String  textSpinner = spinnerTipoDocumento.getSelectedItem().toString();
            String userEmail = user.getEmail();
            if(Boolean.TRUE.equals(ValidatedInfo.validaty(getApplication(),txtNombre,txtApellidos,txtCelular,textSpinner,numDocumento)) ){
                Map<String, Object> map = new HashMap<>();
                map.put("uid", userEmail.toLowerCase());
                map.put("nombre", txtNombre.toLowerCase());
                map.put("apellidos", txtApellidos.toLowerCase());
                map.put("celular", txtCelular.toLowerCase());
                map.put("documento", numDocumento.toLowerCase());
                map.put("tipodocumento", textSpinner);
                db.collection("usuarios").add(map).addOnSuccessListener(documentReference -> {
                    editTextNombre.setText("");
                    editTextApellidos.setText("");
                    editTextCelular.setText("");
                    editNumDocumento.setText("");
                    Intent intent;
                    intent = new Intent(AccountInfoActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                }).addOnFailureListener(e -> Toast.makeText(getApplication(), "Error al Ingresar", Toast.LENGTH_SHORT).show());
            }




        });



    }





    public void bindUI(){
          editTextNombre = (EditText) findViewById(R.id.editTextNombre);
          editTextApellidos= (EditText) findViewById(R.id.editTextApellidos);
          editTextCelular = (EditText) findViewById(R.id.editTextCelular);
          editNumDocumento = (EditText) findViewById(R.id.editNumDocumento);
          spinnerTipoDocumento= findViewById(R.id.spinnerTipoDocumento);
            btnSiguiente = (Button) findViewById(R.id.btnSiguiente);

    }
}