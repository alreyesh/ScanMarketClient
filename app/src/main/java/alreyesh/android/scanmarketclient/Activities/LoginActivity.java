package alreyesh.android.scanmarketclient.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.Utils.Util;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Switch switchRemember;
    private TextView textRegister;
    private TextView textReset;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindUI();
        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        setCredentialsIfExists();
        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        textReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if(login(email,password)){
                    mDialog.setMessage("Iniciando Sesión");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    Auth(email,password);

                }
            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void bindUI(){
        editTextEmail = (EditText) findViewById(R.id.editTextRecuperarEmail);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        switchRemember = (Switch)findViewById(R.id.switchRemember);
        btnLogin=(Button) findViewById(R.id.buttonLogin);
        textRegister=(TextView) findViewById(R.id.textRegistrar);
        textReset = (TextView) findViewById(R.id.textLost);
    }

    private void setCredentialsIfExists(){
       String email = Util.getUserMailPrefs(prefs);
       String password = Util.getUserPassPrefs(prefs);
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            editTextEmail.setText(email);
            editTextPassword.setText(password);
            switchRemember.setChecked(true);
        }
    }

    private boolean login(String email,String password){
        if(!isValidEmail(email)){
            Toast.makeText(this,"Ingrese Correo Valido",Toast.LENGTH_SHORT).show();
            return false;
        }else if(!isValidPassword(password)){
            Toast.makeText(this,"Ingrese Contraseña Valida",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }
    private void saveOnPreferences(String email,String password){
        if(switchRemember.isChecked()){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email",email);
            editor.putString("pass",password);
            // sincrona editor.commit();
            //Asincrona
            editor.apply();

        }
    }
    private boolean isValidEmail(String email){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidPassword(String password){
        return password.length()>4;
    }
    private void goToMain(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void Auth(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);
                            goToMain();
                            saveOnPreferences(email,password);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        mDialog.dismiss();
                    }
                });
    }

}