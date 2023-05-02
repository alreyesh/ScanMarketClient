package alreyesh.android.scanmarketclient.fragments;



import android.os.Bundle;



import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import org.apache.commons.lang3.StringUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import alreyesh.android.scanmarketclient.R;

import alreyesh.android.scanmarketclient.model.User;
import alreyesh.android.scanmarketclient.utils.ValidatedInfo;

public class AccountInfoFragment extends Fragment {
    EditText editTextNombre;
    EditText editTextApellidos;
    EditText editTextCelular;
    EditText editNumDocumento;
    Spinner  spinnerTipoDocumento;
    Button  btnGrabar;
    Button btnPrueba;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    String id;
    private static final String USUARIOS= "usuarios";
    public AccountInfoFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_account_info, container, false);
        editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextApellidos = view.findViewById(R.id.editTextApellidos);
        editTextCelular = view.findViewById(R.id.editTextCelular);
        editNumDocumento = view.findViewById(R.id.editNumDocumento);
        spinnerTipoDocumento = view.findViewById(R.id.spinnerTipoDocumento);
        btnGrabar = view.findViewById(R.id.btnGrabar);

          db = FirebaseFirestore.getInstance();
         mAuth = FirebaseAuth.getInstance();
        loadspinner();
        loadinfo();

        btnGrabar.setOnClickListener(v -> {

            String textNombre = editTextNombre.getText().toString().trim();
            String textApellidos = editTextApellidos.getText().toString().trim();
            String textCelular = editTextCelular.getText().toString().trim();
            String spinnertext = spinnerTipoDocumento.getSelectedItem().toString();
            String numDocumento = editNumDocumento.getText().toString().trim();
            if( id == null){
             if(textNombre.isEmpty() && textApellidos.isEmpty() && textCelular.isEmpty() && numDocumento.isEmpty())
                   Toast.makeText(getContext(),"Ingresar los datos", Toast.LENGTH_SHORT).show();
               else{
                   postInfo(textNombre,textApellidos,textCelular,spinnertext,numDocumento);

               }

            }else{
                updateInfo(textNombre,textApellidos,textCelular,spinnertext,numDocumento,id);


            }

        });


        return  view;
    }
    public void  postInfo(String textNombre,String textApellidos,String textCelular,String spinnertext,String numDocumento){
        if(textNombre.isEmpty()|| textApellidos.isEmpty()||textCelular.isEmpty()||numDocumento.isEmpty()){
            Toast.makeText(getContext(), "Rellenar todos los campos", Toast.LENGTH_SHORT).show();

        }else{
            if(Boolean.TRUE.equals( ValidatedInfo.validaty(getContext(), textNombre,textApellidos,textCelular,spinnertext,numDocumento))){
                FirebaseUser user = mAuth.getCurrentUser();
                String userEmail = user.getEmail();
                Map<String, Object> map = new HashMap<>();
                map.put("uid", userEmail.toLowerCase());
                map.put("nombre", textNombre.toLowerCase());
                map.put("apellidos", textApellidos.toLowerCase());
                map.put("celular", textCelular.toLowerCase());
                map.put("documento", numDocumento.toLowerCase());
                map.put("tipodocumento", spinnertext);
                db.collection(USUARIOS).add(map).addOnSuccessListener(documentReference -> {
                    editTextNombre.setText("");
                    editTextApellidos.setText("");
                    editTextCelular.setText("");
                    editNumDocumento.setText("");
                    Toast.makeText(getContext(), "Se Actualizaron los datos", Toast.LENGTH_SHORT).show();
                    loadinfo();

                }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error al Ingresar", Toast.LENGTH_SHORT).show());
            }
        }



    }
    public void  updateInfo(String textNombre,String textApellidos,String textCelular,String spinnertext,String numDocumento,String id){
            FirebaseUser user = mAuth.getCurrentUser();
            String userEmail = user.getEmail();
            Map<String, Object> map = new HashMap<>();
            map.put("uid", userEmail.toLowerCase());

                if(!textNombre.isEmpty() && ValidatedInfo.isValidName(getContext(),textNombre)){
                    map.put("nombre", textNombre.toLowerCase());
                }


                if(!textApellidos.isEmpty() && ValidatedInfo.isValidLastName(getContext(),textApellidos)){
                    map.put("apellidos", textApellidos.toLowerCase());
                }


                if(!textCelular.isEmpty() && ValidatedInfo.isValidCellPhone(getContext(),textCelular)){
                    map.put("celular", textCelular.toLowerCase());
                }



                if(!numDocumento.isEmpty() && ValidatedInfo.isValidDocument(getContext(),spinnertext,numDocumento)){
                    map.put("documento", numDocumento.toLowerCase());
                    map.put("tipodocumento", spinnertext);
                }


            db.collection(USUARIOS).document(id).update(map).addOnSuccessListener(unused -> {
                editTextNombre.setText("");
                editTextApellidos.setText("");
                editTextCelular.setText("");
                editNumDocumento.setText("");
                Toast.makeText(getContext(), "Se Actualizaron los datos", Toast.LENGTH_SHORT).show();
                loadinfo();
            }).addOnFailureListener(e -> {

            });


    }
    public void loadspinner(){
        List<String> categorias = new ArrayList<>();
        categorias.add("DNI");
        categorias.add("RUC");
        categorias.add("CEX");
        categorias.add("Pasaporte");
        ArrayAdapter<String> adapter = new ArrayAdapter<> (getContext(),android.R.layout.simple_spinner_item,categorias);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoDocumento.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }
    public void loadinfo(){
      FirebaseUser user = mAuth.getCurrentUser();
     String userEmail = user.getEmail();
     db.collection(USUARIOS).whereEqualTo("uid", userEmail).get() .addOnCompleteListener(task -> {
         if( task.isSuccessful()){
             for(QueryDocumentSnapshot document: task.getResult()){
                 User user1 = document.toObject(User.class);
                 id = document.getId();
                 editTextNombre.setHint( StringUtils.capitalize(user1.getNombre()));
                 editTextApellidos.setHint(StringUtils.capitalize(user1.getApellidos()));
                 editTextCelular.setHint(user1.getCelular());
                 editNumDocumento.setHint(user1.getDocumento());
                 switch (user1.getTipodocumento()){
                     case "DNI":
                         spinnerTipoDocumento.setSelection(0);
                         break;
                     case "RUC":
                             spinnerTipoDocumento.setSelection(1);
                         break;
                     case "CEX":
                         spinnerTipoDocumento.setSelection(2);
                         break;
                     default:
                         spinnerTipoDocumento.setSelection(3);

                 }

             }
         }
     }).addOnFailureListener(e -> {

     });


    }




}