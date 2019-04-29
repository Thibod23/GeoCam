package com.example.a533.geocam.repository;

import android.app.VoiceInteractor;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.a533.geocam.error.CouldNotSavePictureException;
import com.example.a533.geocam.model.Picture;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PictureRepository {
    private static final String PICTURE_COLLECTION_NAME = "Pictures";
    private FirebaseFirestore database;

    public PictureRepository() {
        this.database = FirebaseFirestore.getInstance();
    }

    public interface MyCallbackFillListPictures{
        void onQueryFinished(List<Picture> pictures);
    }

    public void getAll(final MyCallbackFillListPictures todoWhenFinished) {

        database.collection("Pictures")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Picture> pictures = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                pictures.add(new Picture(document.get("name").toString(), document.get("lat").toString(), document.get("lng").toString()));
                            }
                            //fillList(pictures, PictureRepository.MyCallbackFillListPictures);
                            todoWhenFinished.onQueryFinished(pictures);
                        }
                    }
                });

    }

    public void fillList(final List<Picture> pictures, final PictureRepository.MyCallbackFillListPictures myCallback){

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
