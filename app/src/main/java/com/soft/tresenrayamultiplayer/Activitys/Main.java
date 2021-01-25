package com.soft.tresenrayamultiplayer.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;
import com.soft.tresenrayamultiplayer.Providers.AuthProviders;
import com.soft.tresenrayamultiplayer.R;

public class Main extends AppCompatActivity {

    CardView          CRDLogin, CRDRegister;
    FirebaseFirestore mFirestore;
    AuthProviders     mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.ThemePrimary);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CRDLogin    = findViewById(R.id.btnLogin);
        CRDRegister = findViewById(R.id.btnRegister);
        mFirestore  = FirebaseFirestore.getInstance();
        mAuth       = new AuthProviders();

        CRDLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main.this,Login.class));
            }
        });

        CRDRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main.this,RegisterUser.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String id = mAuth.getId();
        if (id !=null){
            startActivity(new Intent(Main.this, MenuGame.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        else {
        }
    }
}