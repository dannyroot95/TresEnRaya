package com.soft.tresenrayamultiplayer.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.soft.tresenrayamultiplayer.Models.Playing;
import com.soft.tresenrayamultiplayer.Models.Users;
import com.soft.tresenrayamultiplayer.Providers.AuthProviders;
import com.soft.tresenrayamultiplayer.R;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class Game extends AppCompatActivity {

    List<ImageView>      boxes;
    TextView             TxtnamePlayerOne;
    TextView             TxtnamePlayerTwo;
    AuthProviders        mAuth = new AuthProviders();
    FirebaseFirestore    db;
    Playing              play;
    ListenerRegistration lr = null;
    String               playerName;
    Users                userPlayer1,
                         userPlayer2;
    String               id,
                         playRoomId ="",
                         namePLayerOne="",
                         namePLayerTwo="",
                         playerWinnerId="";
    private Dialog       mDialog;
    TextView             points;
    Button               close;
    ImageView            image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.ThemePrimary);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        db            = FirebaseFirestore.getInstance();
        id            = mAuth.getId();
        Bundle extras = getIntent().getExtras();
        playRoomId    = extras.getString("room");
        mDialog       = new Dialog(this);
        mDialog.setContentView(R.layout.dialog_finish_game);
        points        =(TextView)mDialog.findViewById(R.id.txtPoints);
        close         =(Button)mDialog.findViewById(R.id.btnClose);
        image         =(ImageView) mDialog.findViewById(R.id.imageState);
        iniViews();
    }

    private void iniViews(){
        TxtnamePlayerOne = findViewById(R.id.textViewPlayer1);
        TxtnamePlayerTwo = findViewById(R.id.textViewPlayer2);

        boxes = new ArrayList<>();
        boxes.add((ImageView)findViewById(R.id.imageView0));
        boxes.add((ImageView)findViewById(R.id.imageView1));
        boxes.add((ImageView)findViewById(R.id.imageView2));
        boxes.add((ImageView)findViewById(R.id.imageView3));
        boxes.add((ImageView)findViewById(R.id.imageView4));
        boxes.add((ImageView)findViewById(R.id.imageView5));
        boxes.add((ImageView)findViewById(R.id.imageView6));
        boxes.add((ImageView)findViewById(R.id.imageView7));
        boxes.add((ImageView)findViewById(R.id.imageView8));

    }

    @Override
    protected void onStart() {
        super.onStart();
        playListener();
    }

    private void playListener() {
        lr = db.collection("Jugadas").document(playRoomId).addSnapshotListener(Game.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error !=null){
                    Toasty.error(Game.this, "", Toast.LENGTH_SHORT).show();
                    return;
                }

                String source = value != null && value.getMetadata().hasPendingWrites() ? "Local":"Server";

                if (value.exists() && source.equals("Server")){
                    play = value.toObject(Playing.class);
                    if(namePLayerOne.isEmpty() || namePLayerTwo.isEmpty()){
                        getPlayerData();
                    }
                    updateUI();
                }
                updatePlayerUI();
            }
        });
    }

    private void getPlayerData() {
        db.collection("Usuarios").document(play.getPlayerOneId()).get().addOnSuccessListener(Game.this, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userPlayer1 = documentSnapshot.toObject(Users.class);
                namePLayerOne = documentSnapshot.get("nick").toString();
                TxtnamePlayerOne.setText(namePLayerOne);

                if(play.getPlayerOneId().equals(id)){
                    playerName = namePLayerOne;
                }

            }
        });

        db.collection("Usuarios").document(play.getPlayerTwoId()).get().addOnSuccessListener(Game.this, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userPlayer2 = documentSnapshot.toObject(Users.class);
                namePLayerTwo = documentSnapshot.get("nick").toString();
                TxtnamePlayerTwo.setText(namePLayerTwo);

                if(play.getPlayerTwoId().equals(id)){
                    playerName = namePLayerTwo;
                }

            }
        });

    }


    private void updateUI() {
        for(int i=0; i<9; i++) {
            int box = play.getSelectedRow().get(i);
            ImageView ImageBox = boxes.get(i);

            if(box == 0) {
                ImageBox.setImageResource(R.drawable.ic_empty_square);
            } else if(box == 1) {
                ImageBox.setImageResource(R.drawable.ic_player_one);
            } else {
                ImageBox.setImageResource(R.drawable.ic_player_two);
            }

        }
    }


    private void updatePlayerUI() {
        if(play.isTurnPlayerOne()){
            TxtnamePlayerOne.setTextColor(getResources().getColor(R.color.errorColor));
            TxtnamePlayerTwo.setTextColor(getResources().getColor(R.color.white));
        }
        else{
            TxtnamePlayerOne.setTextColor(getResources().getColor(R.color.white));
            TxtnamePlayerTwo.setTextColor(getResources().getColor(R.color.warningColor));
        }

        if (!play.getWinnerId().isEmpty()){
            playerWinnerId = play.getWinnerId();
            showGameOver();
        }

    }

    public void checkedBox(View view){
        if(!play.getWinnerId().isEmpty()) {
            Toast.makeText(this, "La partida ha terminado", Toast.LENGTH_SHORT).show();
        } else {
            if(play.isTurnPlayerOne() && play.getPlayerOneId().equals(id)) {
                // Está jugando el jugador 1
                updateMove(view.getTag().toString());
            } else if(!play.isTurnPlayerOne() && play.getPlayerTwoId().equals(id)) {
                // Está jugando el jugador 2
                updateMove(view.getTag().toString());
            } else {
                Toast.makeText(this, "No es tu turno aún", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateMove(String numBox){
        int positionBox = Integer.parseInt(numBox);

        if(play.getSelectedRow().get(positionBox) != 0) {
            Toast.makeText(this, "Seleccione una casilla libre", Toast.LENGTH_SHORT).show();
        } else {
            if (play.isTurnPlayerOne()) {
                boxes.get(positionBox).setImageResource(R.drawable.ic_player_one);
                play.getSelectedRow().set(positionBox, 1);
            } else {
                boxes.get(positionBox).setImageResource(R.drawable.ic_player_two);
                play.getSelectedRow().set(positionBox, 2);
            }

            if(solve()) {
                play.setWinnerId(id);
                //Toast.makeText(this, "Ganaste", Toast.LENGTH_SHORT).show();
            } else if(tie()) {
                play.setWinnerId("EMPATE");
                //Toast.makeText(this, "Empate", Toast.LENGTH_SHORT).show();
            } else {
                changeTurn();
            }

            // Actualizar en Firestore los datos de la jugada
            db.collection("Jugadas")
                    .document(playRoomId)
                    .set(play)
                    .addOnSuccessListener(Game.this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(Game.this, new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.w("ERROR", "Error al guardar la jugada");
                }
            });
        }
    }

    private boolean solve(){
        boolean exist = false;

        List<Integer> selectedCells = play.getSelectedRow();
        if(selectedCells.get(0) == selectedCells.get(1)
                && selectedCells.get(1) == selectedCells.get(2)
                && selectedCells.get(2) != 0) { // 0 - 1 - 2
            exist = true;
        } else if(selectedCells.get(3) == selectedCells.get(4)
                && selectedCells.get(4) == selectedCells.get(5)
                && selectedCells.get(5) != 0) { // 3 - 4 - 5
            exist = true;
        } else if(selectedCells.get(6) == selectedCells.get(7)
                && selectedCells.get(7) == selectedCells.get(8)
                && selectedCells.get(8) != 0) { // 6 - 7 - 8
            exist = true;
        } else if(selectedCells.get(0) == selectedCells.get(3)
                && selectedCells.get(3) == selectedCells.get(6)
                && selectedCells.get(6) != 0) { // 0 - 3 - 6
            exist = true;
        } else if(selectedCells.get(1) == selectedCells.get(4)
                && selectedCells.get(4) == selectedCells.get(7)
                && selectedCells.get(7) != 0) { // 1 - 4 - 7
            exist = true;
        } else if(selectedCells.get(2) == selectedCells.get(5)
                && selectedCells.get(5) == selectedCells.get(8)
                && selectedCells.get(8) != 0) { // 2 - 5 - 8
            exist = true;
        } else if(selectedCells.get(0) == selectedCells.get(4)
                && selectedCells.get(4) == selectedCells.get(8)
                && selectedCells.get(8) != 0) { // 0 - 4 - 8
            exist = true;
        } else if(selectedCells.get(2) == selectedCells.get(4)
                && selectedCells.get(4) == selectedCells.get(6)
                && selectedCells.get(6) != 0) { // 2 - 4 - 6
            exist = true;
        }

        return exist;
    }

    private boolean tie(){
        boolean exist = false;
        // Empate
        boolean freeBox = false;
        for(int i=0; i<9; i++) {
            if(play.getSelectedRow().get(i) == 0) {
                freeBox = true;
                break;
            }
        }
        if(!freeBox)
            exist = true;
        return exist;
    }

    private void changeTurn(){
        play.setTurnPlayerOne(!play.isTurnPlayerOne());
    }

    private void updatePoints(int points){
        Users updatePlayer = null;
        if(playerName.equals(userPlayer1.getNick())) {
            userPlayer1.setPoints(userPlayer1.getPoints() + points);
            userPlayer1.setStats(userPlayer1.getStats() + 1);
            updatePlayer = userPlayer1;
        } else {
            userPlayer2.setPoints((userPlayer2.getPoints() + points));
            userPlayer2.setStats(userPlayer2.getStats() + 1);
            updatePlayer = userPlayer2;
        }

        db.collection("Usuarios")
                .document(id)
                .set(updatePlayer)
                .addOnSuccessListener(Game.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        lr.remove();
                        //startActivity(new Intent(Game.this,MenuGame.class));
                        //finish();
                    }
                })
                .addOnFailureListener(Game.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    private void showGameOver(){
        if(playerWinnerId.equals("EMPATE")) {

            updatePoints(1);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mDialog.setCancelable(false);
            mDialog.show();
            image.setImageResource(R.drawable.tie);
            points.setText("+1");
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCollection();
                    mDialog.dismiss();
                    startActivity(new Intent(Game.this,MenuGame.class));
                    finish();
                }
            });


        } else if(playerWinnerId.equals(id)) {

            updatePoints(3);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mDialog.setCancelable(false);
            mDialog.show();
            image.setImageResource(R.drawable.win);
            points.setText("+3");
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCollection();
                    mDialog.dismiss();
                    startActivity(new Intent(Game.this,MenuGame.class));
                    finish();
                }
            });



        } else {

            updatePoints(0);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mDialog.setCancelable(false);
            mDialog.show();
            image.setImageResource(R.drawable.loss);
            points.setText("+0");
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCollection();
                    mDialog.dismiss();
                    startActivity(new Intent(Game.this,MenuGame.class));
                    finish();
                }
            });

        }

    }

    private void deleteCollection(){
        db.collection("Jugadas")
                .document(playRoomId)
                .delete();
    }

    @Override
    protected void onStop() {
        if (lr !=null){
            lr.remove();
        }
        super.onStop();
    }

}