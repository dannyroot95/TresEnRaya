package com.soft.tresenrayamultiplayer.Providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.soft.tresenrayamultiplayer.Models.Users;

import java.util.HashMap;
import java.util.Map;

public class UserProviders {

    FirebaseFirestore mFirestore;

    public UserProviders(){
         mFirestore = FirebaseFirestore.getInstance();
    }

    public Task<Void> MappingData(Users user) {

        Map<String , Object> map = new HashMap<>();
        map.put("id",user.getId());
        map.put("name",user.getName());
        map.put("lastName",user.getLastName());
        map.put("nick",user.getNick());
        map.put("stats",user.getStats());
        map.put("points",user.getPoints());
        map.put("photo",user.getPhoto());

        return mFirestore.collection("Usuarios").document(user.getId()).set(map);

    }

    public Task<Void>UpdatePhoto(Users user) {
        Map<String , Object> map = new HashMap<>();
        map.put("photo", user.getPhoto());
        return mFirestore.collection("Usuarios").document(user.getId()).update(map);
    }

    public Task<Void>UpdatePoints(Users user) {
        Map<String , Object> map = new HashMap<>();
        map.put("points", user.getPoints());
        return mFirestore.collection("Usuarios").document(user.getId()).update(map);
    }

    public DocumentReference getData(String idAdmin){
        return mFirestore.collection("Usuarios").document(idAdmin);
    }

}
