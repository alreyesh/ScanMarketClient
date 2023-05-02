package alreyesh.android.scanmarketclient.activities;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


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
    private static final String MESSAGE = "Ingrese un correo y/o contraseña valida";
    private static final String AUTH= "152345124090-odhq31h5ggfeg7s663a49dp9i8jlr21s.apps.googleusercontent.com";
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
    private SignInClient oneTapClient;
    private  BeginSignInRequest signInRequest;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindUI();
        createRequest();

        prefs = Util.getSP(getApplication());

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
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
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

        btnLogin=(Button) findViewById(R.id.buttonLogin);
        btnGoogle=(Button)findViewById(R.id.btnGoogle);

        textRegister=(TextView) findViewById(R.id.textRegistrar);
        textReset = (TextView) findViewById(R.id.textLost);
    }

    private void setCredentialsIfExists(){

        try{
            String email = Util.getUserMailPrefs(prefs);
            String password = Util.getUserPassPrefs(prefs);


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

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email",email);
            editor.putString("pass",password);

            editor.apply();

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
                        if (!queryDocumentSnapshots.isEmpty()) {
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        } else {
                            intent = new Intent(LoginActivity.this, AccountInfoActivity.class);

                        }
                        LoginActivity.this.startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });









    }
    private void auth(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            LoginActivity.this.saveAccountOptions("firebase");
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");


                           goToMain();
                           saveOnPreferences(email, password);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, MESSAGE,
                                    Toast.LENGTH_SHORT).show();

                        }
                        pBar.setVisibility(View.INVISIBLE);
                    }
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
        saveAccountOptions("google");
   exampleActivityResult.launch(mGoogleSignInClient.getSignInIntent());
      /*  signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())

                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(AUTH)
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                . setAutoSelectEnabled(true)
                .build();

*/

    }
  ActivityResultLauncher<Intent> exampleActivityResult= registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());

                     handleSignInResult(task);
                    }
                }
            });

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        oneTapClient = Identity.getSignInClient(this);
        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with Firebase.
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                        mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
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
                                    }
                                });

                    }
                } catch (ApiException e) {
                    // ...
                }
                break;
        }






    }
*/
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
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            LoginActivity.this.saveAccountOptions("google");
                            Log.d(TAG, "signInWithCredential:success");

                            LoginActivity.this.goToMain();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, MESSAGE,
                                    Toast.LENGTH_SHORT).show();

                        }
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