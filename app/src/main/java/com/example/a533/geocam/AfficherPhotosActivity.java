package com.example.a533.geocam;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.a533.geocam.model.Picture;
import com.example.a533.geocam.repository.PictureRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AfficherPhotosActivity extends AppCompatActivity {

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<Bitmap> mImages = new ArrayList<>();
    private ArrayList<String> mPositions = new ArrayList<>();
    private ArrayList<String> mImageCompleteName = new ArrayList<>();
    private PictureRepository pictureRepository = new PictureRepository();
    Context context = this;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficher_photos);

        db = FirebaseFirestore.getInstance();

        initTest();

    }

    private void initTest(){
        PictureRepository.MyCallbackFillListPictures todoWhenFinished = new PictureRepository.MyCallbackFillListPictures() {
            @Override
            public void onQueryFinished(List<Picture> pictures) {
                fillRecyclerViewData(pictures);
            }
        };
        pictureRepository.getAll(todoWhenFinished);

    }

    private void fillRecyclerViewData(List<Picture> pictures){
        for (Picture picture: pictures) {
            mImages.add(BitmapFactory.decodeFile("/storage/emulated/0/Android/data/com.example.a533.geocam/files/Pictures/" + picture.name));
            mImageCompleteName.add(picture.name);
            mImageNames.add(picture.name.split("_")[0]);
            mPositions.add(String.format("%s, %s", picture.lat, picture.lng));
        }
        initRecyclerView();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_photos);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mImageNames, mImages, mPositions, mImageCompleteName);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
