package alreyesh.android.scanmarketclient.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

import alreyesh.android.scanmarketclient.R;

public class RegisterActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private TextView textReset;
    private Switch switchShow;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    private static final String TAG = "EmailPassword";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bindUI();
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirm = editTextConfirmPassword.getText().toString();

               // Toast.makeText(RegisterActivity.this,"1: "+password,Toast.LENGTH_SHORT).show();
               // Toast.makeText(RegisterActivity.this,"2: "+confirm,Toast.LENGTH_SHORT).show();
                if(verify(email,password,confirm)){
                    mDialog.setMessage("Registrando cuenta...");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    crearCuenta(email,password);

                }
            }
        });
        textReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, ResetActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        switchShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    editTextConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    //Toast.makeText(RegisterActivity.this,"Chekeado",Toast.LENGTH_SHORT).show();
                }else{
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    //Toast.makeText(RegisterActivity.this,"No Chekeado",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
    private void bindUI(){
        editTextEmail = (EditText) findViewById(R.id.editTextRecuperarEmail);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        editTextConfirmPassword=(EditText)findViewById(R.id.editTextConfirmPassword);
        textReset=(TextView)findViewById(R.id.textReset);
        switchShow = (Switch)findViewById(R.id.switchShow);
        btnLogin=(Button) findViewById(R.id.buttonLogin);
    }
    private void crearCuenta(String email,String password){
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            goToMain();
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "El correo electronico ya existe en nuestra base de datos",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                       mDialog.dismiss();
                    }
                });
    }

    private boolean verify(String email,String password,String confirm){
        if(!isValidEmail(email)){
            Toast.makeText(this,"Ingrese un correo electronico valido",Toast.LENGTH_SHORT).show();
            return false;
        }else if(!isValidPassword(password)){
            Toast.makeText(this,"Ingrese una contraseña",Toast.LENGTH_SHORT).show();
            return false;
        }else if(!password.equals(confirm) ){
            Toast.makeText(this,"La contraseña de confirmación no coinciden",Toast.LENGTH_SHORT).show();
            return false;
        }else if(!isValidSpace(email,password)){
            Toast.makeText(this,"correo y/o contraseña contiene espacio",Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }
    }
    private boolean isValidEmail(String email){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidPassword(String password){
        return password.length()>4;
    }

    private boolean isValidSpace(String email,String password){
        boolean emailspace = Pattern.matches("\\s+", email);
        boolean passspace = Pattern.matches("\\s+", password);
        if(Boolean.TRUE.equals(emailspace)) {
            Toast.makeText(this, "Correo Invalido", Toast.LENGTH_SHORT).show();
            return false;
        }else if(Boolean.TRUE.equals(passspace)){
            Toast.makeText(this, "Password Invalido", Toast.LENGTH_SHORT).show();
            return false;
        }
            return true;
    }


    private void goToMain(){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("register", "on");
        startActivity(intent);
    }

}