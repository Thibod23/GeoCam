package com.example.a533.geocam;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class AfficherPhotosActivity extends AppCompatActivity {

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<Bitmap> mImages = new ArrayList<>();
    private ArrayList<String> mPositions = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficher_photos);
        initTest();

    }

    private void initTest(){


        mImages.add(BitmapFactory.decodeFile("/storage/emulated/0/Android/data/com.example.a533.geocam/files/Pictures/test1_20190411_182639_7283655670496709015.jpg"));
        mImages.add(BitmapFactory.decodeFile("/storage/emulated/0/Android/data/com.example.a533.geocam/files/Pictures/test2_20190411_182622_159972999237839762.jpg" ));
        mImages.add(BitmapFactory.decodeFile("/storage/emulated/0/Android/data/com.example.a533.geocam/files/Pictures/test3_20190411_182628_7934302385539901012.jpg"));
        mImages.add(BitmapFactory.decodeFile("/storage/emulated/0/Android/data/com.example.a533.geocam/files/Pictures/test4_20190411_182633_7183693964757802967.jpg"));

        mImageNames.add("Test 1");
        mImageNames.add("Test 2");
        mImageNames.add("Test 3");
        mImageNames.add("Test 4");

        mPositions.add("1312 642");
        mPositions.add("5456 686");
        mPositions.add("6795 153");
        mPositions.add("5626 844");

        initRecyclerView();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_photos);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mImageNames, mImages, mPositions);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
