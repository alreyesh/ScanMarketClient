package alreyesh.android.scanmarketclient.activities;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Logger;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.utils.Util;

public class LoginActivity extends AppCompatActivity {
     SharedPreferences prefs;
     EditText editTextEmail;
     EditText editTextPassword;
     Switch switchRemember;
     TextView textRegister;
     TextView textReset;
    private Button btnLogin;

    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private static final String MESSAGE = "Authentication failed.";

    private ProgressBar pBar;
    //google
    private Button btnGoogle;
     GoogleSignInClient mGoogleSignInClient;
      GoogleSignInAccount account;
    private FirebaseFirestore db;
    // Facebook
      CallbackManager mCallbackManager;
     Button btnFacebook;

    //DatabaseCloud

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindUI();
        createRequest();
        String masterKeyAlias = null;
        prefs = Util.getSP(getApplication());
        //Util.getSP(context);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        pBar= (ProgressBar) findViewById(R.id.progressbar);
        pBar.setVisibility(View.INVISIBLE);
        setCredentialsIfExists();
        textRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

            startActivity(intent);
        });
        textReset.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetActivity.class);

            startActivity(intent);
        });
        btnLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if(login(email,password)){
                pBar.setVisibility(View.VISIBLE);

                auth(email,password);

            }
        });
        //google
        btnGoogle.setOnClickListener(v -> signIn());


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
        btnGoogle=(Button)findViewById(R.id.btnGoogle);

        textRegister=(TextView) findViewById(R.id.textRegistrar);
        textReset = (TextView) findViewById(R.id.textLost);
    }

    private void setCredentialsIfExists(){

        try{
            String email = Util.getUserMailPrefs(prefs);
            String password = Util.getUserPassPrefs(prefs);
            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                editTextEmail.setText(email);
                editTextPassword.setText(password);
                switchRemember.setChecked(true);
            }

        }catch(NullPointerException e){
            System.out.print("NullPointerException caught");
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

            editor.apply();

        }
    }
    private void saveAccountOptions(String option){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("account",option);
        editor.apply();
    }
    private boolean isValidEmail(String email){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidPassword(String password){
        return password.length()>4;
    }
    private void goToMain(){

        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail();

        db.collection("usuarios").whereEqualTo("uid", userEmail)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Intent intent;
                if ( !queryDocumentSnapshots.isEmpty()){
                      intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);

                }else{
                     intent = new Intent(LoginActivity.this, AccountInfoActivity.class);

                }
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });









    }
    private void auth(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        saveAccountOptions("firebase");
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");



                        goToMain();
                        saveOnPreferences(email,password);

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, MESSAGE,
                                Toast.LENGTH_SHORT).show();

                    }
                    pBar.setVisibility(View.INVISIBLE);
                });
    }
    //GoogleAccount
    private void createRequest(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id2))
                .requestEmail()
                .build();

      mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    private void signIn() {

        exampleActivityResult.launch(mGoogleSignInClient.getSignInIntent());


    }
    ActivityResultLauncher<Intent> exampleActivityResult= registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());

                    handleSignInResult(task);
                }
            });

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account  = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "Google sign in failed", e);

        }
    }



    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        saveAccountOptions("google");
                        Log.d(TAG, "signInWithCredential:success");

                        goToMain();

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, MESSAGE,
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }
    public void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        goToMain();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, MESSAGE,
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }



}