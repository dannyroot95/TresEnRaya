package com.soft.tresenrayamultiplayer.Providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthProviders {

    FirebaseAuth mAuth;
    public AuthProviders(){
        mAuth = FirebaseAuth.getInstance();
    }
    public Task<AuthResult> Register(String email , String password){
        return  mAuth.createUserWithEmailAndPassword(email,password);
    }
    public Task<AuthResult> Login(String email , String password){
        return  mAuth.signInWithEmailAndPassword(email,password);
    }
    public String getId() {
        return mAuth.getUid();
    }
    public void Logout(){
        mAuth.signOut();
    }
    public boolean ExistSesion(){
        boolean exist = false;
        if(mAuth.getCurrentUser() !=null){
            exist =true;
        }
        return exist ;
    }
}
