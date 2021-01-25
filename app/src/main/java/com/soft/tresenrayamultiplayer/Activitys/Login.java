package com.soft.tresenrayamultiplayer.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NavUtils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.soft.tresenrayamultiplayer.Providers.AuthProviders;
import com.soft.tresenrayamultiplayer.R;

import es.dmoral.toasty.Toasty;

public class Login extends AppCompatActivity {

    AuthProviders          mAuth;
    TextInputEditText      InputEmail,InputPassword;
    private ProgressDialog mDialog;
    CardView               CRDLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.ThemePrimary);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth         = new AuthProviders();
        CRDLogin      = findViewById(R.id.btnLogin);
        InputEmail    = findViewById(R.id.edtEmail);
        InputPassword = findViewById(R.id.edtPassword);
        InputEmail    = findViewById(R.id.edtEmail);
        mDialog       = new ProgressDialog(this,R.style.ThemeOverlay);

        CRDLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }

    private void loginUser() {
        String email = InputEmail.getText().toString();
        String passsword = InputPassword.getText().toString();


        if(!email.isEmpty() && !passsword.isEmpty()) {
            if (passsword.length() >= 6) {
                mDialog.show();
                mDialog.setMessage("Iniciando sesión...");
                mAuth.Login(email, passsword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mDialog.dismiss();
                            Intent intent = new Intent(Login.this, MenuGame.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                        } else {
                            Toasty.error(Login.this, "Contraseña incorrecta ó Error de servidor!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Toasty.info(Login.this, "Contraseña incorrecta ó Error de servidor!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else {
                Toasty.info(this,"La contraseña debe tener al menos 6 caracteres",Toast.LENGTH_LONG,true).show();
            }
        }
        else {
            Toasty.info(this, "Complete los campos!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }
}