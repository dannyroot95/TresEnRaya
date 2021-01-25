package com.soft.tresenrayamultiplayer.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NavUtils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.soft.tresenrayamultiplayer.Models.Users;
import com.soft.tresenrayamultiplayer.Providers.AuthProviders;
import com.soft.tresenrayamultiplayer.Providers.UserProviders;
import com.soft.tresenrayamultiplayer.R;

import es.dmoral.toasty.Toasty;

public class RegisterUser extends AppCompatActivity {

    AuthProviders     mAuth;
    TextInputEditText InputName,
                      InputLastName,
                      InputNickName,
                      InputEmail,
                      InputPassword,
                      InputRepeatPassword;
    CardView          CRDRegister;
    String            photo = "https://firebasestorage.googleapis.com/v0/b/jc-multiservicios.appspot.com/o/fotoDefault.jpg?alt=media&token=fc2dee88-57d0-49bc-9259-b4a99b1aa747";
    UserProviders     mUserProvider;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.ThemePrimary);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth               = new AuthProviders();
        InputName           = findViewById(R.id.edtName);
        InputLastName       = findViewById(R.id.edtLastName);
        InputNickName       = findViewById(R.id.edtNickName);
        InputEmail          = findViewById(R.id.edtEmail);
        InputPassword       = findViewById(R.id.edtPassword);
        InputRepeatPassword = findViewById(R.id.edtRepeatPassword);
        CRDRegister         = findViewById(R.id.btnRegister);
        mUserProvider       = new UserProviders();
        mDialog             = new ProgressDialog(this, R.style.ThemeOverlay);

        CRDRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               clickRegister();
            }
        });

    }

    private void clickRegister() {

        final String name = InputName.getText().toString();
        final String lastName = InputLastName.getText().toString();
        final String nickname = InputNickName.getText().toString();
        final String email = InputEmail.getText().toString();
        final String password = InputPassword.getText().toString();
        final String repeatPassword = InputRepeatPassword.getText().toString();

        if (!name.isEmpty() && !lastName.isEmpty() && !nickname.isEmpty() && !email.isEmpty() && !password.isEmpty()
        && !repeatPassword.isEmpty()){
            if (password.length() >= 6) {
                if (password.equals(repeatPassword)) {
                    registerUser(name, lastName, nickname, email, 0, 0, photo, password);
                } else {
                    Toasty.error(this, "Las contraseñas no son iguales!", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toasty.info(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toasty.info(this, "Complete los campos!", Toast.LENGTH_SHORT).show();
        }

    }


    private void registerUser(final String name , final String lastName, final String nickName, final String email,final int stats,final int points, final String photo, String passsword){

        mDialog.show();
        mDialog.setMessage("Registrando usuario...");
        mDialog.setCancelable(false);

        mAuth.Register(email,passsword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Users users = new Users(id,name,lastName,nickName,email,stats,points,photo);
                mapping(users);
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toasty.error(RegisterUser.this, "No se pudo registrar", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }
        });


    }

    void mapping(Users users){
        mUserProvider.MappingData(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mDialog.dismiss();
                    startActivity(new Intent(RegisterUser.this,Main.class));
                    finish();
                    Toasty.success(RegisterUser.this, "Usuario Registrado", Toast.LENGTH_SHORT).show();
                }
                else{
                    mDialog.dismiss();
                    Toasty.error(RegisterUser.this, "No se pudo registrar", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

}