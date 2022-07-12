package alreyesh.android.scanmarketclient.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import alreyesh.android.scanmarketclient.R;

public class ResetActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText  editTextEmail;
    private Button mButtonResetPassword;
    private String email = "";
    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        mAuth =FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        editTextEmail = (EditText) findViewById(R.id.editTextRecuperarEmail);
        mButtonResetPassword = (Button) findViewById(R.id.buttonReset);

        mButtonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                email = editTextEmail.getText().toString();
                if(!email.isEmpty()){
                    mDialog.setMessage("Espere un momento");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    resetPassword();
                }else{
                    Toast.makeText(ResetActivity.this,"Debe ingresar Correo Electronico",Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
    public void resetPassword(){
            mAuth.setLanguageCode("es");
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ResetActivity.this,"Se ha enviado un correo para reestabler tu contraseña  ",Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(ResetActivity.this,"No se pudo enviar el correo de reestablecer contraseña ",Toast.LENGTH_SHORT).show();

                    }

                    mDialog.dismiss();
                }
            });
    }

}