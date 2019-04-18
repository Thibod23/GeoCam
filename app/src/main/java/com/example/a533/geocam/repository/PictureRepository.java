package com.example.a533.geocam.repository;

import android.app.VoiceInteractor;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.a533.geocam.error.CouldNotSavePictureException;
import com.example.a533.geocam.model.Picture;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class PictureRepository {
    private static final String PICTURE_COLLECTION_NAME = "Pictures";
    private FirebaseFirestore database;


    public PictureRepository() {
        this.database = FirebaseFirestore.getInstance();
    }

    public List<Picture> getAll() {
        final List<Picture> pictures = new ArrayList<>();
        database.collection("Pictures")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                pictures.add(new Picture(document.get("name").toString(), document.get("lat").toString(), document.get("lng").toString()));
                            }
                        }
                    }
                });
        return pictures;
    }

    public void findByName(String name) {
        throw new Error("Not implemented");
    }

    public void save(Picture picture) {
        database.collection(PICTURE_COLLECTION_NAME).add(picture).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(!task.isSuccessful()) {
                    throw new CouldNotSavePictureException();
                }
            }
        });
    }

    public void delete() {
        throw new Error("Not implemented");
    }
}
