package com.soft.tresenrayamultiplayer.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.soft.tresenrayamultiplayer.Models.Playing;
import com.soft.tresenrayamultiplayer.Providers.AuthProviders;
import com.soft.tresenrayamultiplayer.R;

import es.dmoral.toasty.Toasty;

public class MenuGame extends AppCompatActivity {

    Button                    btnSearch;
    private FirebaseFirestore db;
    AuthProviders             mAuth = new AuthProviders();
    String                    id;
    String                    playRoomId = "";
    ListenerRegistration      lsReg = null;
    Context                   ctx = MenuGame.this;
    private Dialog            mDialog;
    TextView                  state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.ThemePrimary);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_game);

        btnSearch = findViewById(R.id.btnSearchPlay);
        mDialog   = new Dialog(this);
        mAuth     = new AuthProviders();
        db        = FirebaseFirestore.getInstance();
        id        = mAuth.getId();

        mDialog.setContentView(R.layout.dialog_startgame);
        state = (TextView)mDialog.findViewById(R.id.dialogMessage);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPlayRoom();
            }
        });

    }

    private void searchPlayRoom(){

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setCancelable(false);
        mDialog.show();
        state.setText("Buscando jugador...");

        db.collection("Jugadas").whereEqualTo("playerTwoId","").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().size() ==0){
                    createNewPlayRoom();
                }
                else {
                    boolean find = false;
                    for(DocumentSnapshot ds : task.getResult().getDocuments()){
                        if(!ds.get("playerOneId").equals(id)){
                            find = true;
                            playRoomId = ds.getId();
                            Playing play = ds.toObject(Playing.class);
                            play.setPlayerTwoId(id);

                            db.collection("Jugadas").document(playRoomId).set(play).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    state.setText("Se encontró un jugador!");
                                    startGame();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    state.setText("Error de servidor!");
                                    mDialog.dismiss();
                                }
                            });
                            break;
                        }
                        if(!find){createNewPlayRoom();}
                    }
                }
            }
        });

    }

    private void waitToPlayer(){
        state.setText("Esperando jugador...");
        lsReg = db.collection("Jugadas").document(playRoomId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.get("playerTwoId").equals("")){
                    state.setText("Llegó un jugador! , empezado partida...");
                    startGame();
                }
            }
        });
    }

    private void createNewPlayRoom(){
        state.setText("Creando Sala...!");
        Playing play = new Playing(id);

        db.collection("Jugadas").add(play).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                playRoomId = documentReference.getId();
                waitToPlayer();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                state.setText("Error al crear sala!");
                mDialog.dismiss();
            }
        });

    }

    private void startGame(){
        if(lsReg!=null){
            lsReg.remove();
        }
        mDialog.dismiss();
        Intent i = new Intent(ctx,Game.class);
        i.putExtra("room",playRoomId);
        startActivity(i);
        playRoomId="";
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void deletePlayRoom(){
        if (lsReg !=null){
            lsReg.remove();
        }
        if (playRoomId !=""){
            deleteCollection();
        }
    }

    private void deleteCollection(){
        db.collection("Jugadas")
                .document(playRoomId)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                playRoomId="";
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playRoomId !=""){
         waitToPlayer();
        }
        else{
        }
    }

    @Override
    protected void onStop() {
        if(lsReg != null) {
            lsReg.remove();
        }

        if(playRoomId != "") {
            db.collection("Jugadas")
                    .document(playRoomId)
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            playRoomId = "";
                        }
                    });
        }
        super.onStop();
    }



    @Override
    public void onBackPressed() {
        finish();
    }
}