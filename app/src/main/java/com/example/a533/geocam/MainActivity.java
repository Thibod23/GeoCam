package com.example.a533.geocam;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a533.geocam.error.CouldNotSavePictureException;
import com.example.a533.geocam.model.Picture;
import com.example.a533.geocam.repository.PictureRepository;
import com.example.a533.geocam.utils.LocationFinder;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Main_Activity";

    PictureRepository pictureRepository;
    LocationFinder locationFinder;

    Button takePictureButton;
    String currentPhotoPath;
    String NomPhoto;
    TextView txt_nomPhoto;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        pictureRepository = new PictureRepository();
        locationFinder = new LocationFinder((LocationManager) getSystemService(Context.LOCATION_SERVICE), this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_nomPhoto = findViewById(R.id.txt_nomPhoto);
        setListener();
    }

    private void setListener(){
        takePictureButton = (Button)findViewById(R.id.btnTakePicture);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        txt_nomPhoto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txt_nomPhoto.getText().length() == 0){
                    takePictureButton.setEnabled(false);
                }else{
                    takePictureButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            //Create the File where the photo should go
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }catch (IOException ex){
                Log.d("TAG",ex.getMessage());
            }
            // Continue only if the File was successfully created
            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException{
        //Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = txt_nomPhoto.getText().toString() + "_" + timeStamp + "_";
        Log.d("TakePicture", imageFileName);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(!storageDir.exists()){
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName, /*prefix*/
                ".jpg", /*suffix*/
                storageDir    /*directory*/
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;

    }

    private String galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        return f.getName();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try{
                String fileName = galleryAddPic();
                this.pictureRepository.save(new Picture(fileName, String.valueOf(this.locationFinder.findLat()), String.valueOf(this.locationFinder.findLng())));
            } catch (CouldNotSavePictureException e) {
                e.printStackTrace();
                Toast.makeText(this, "Could not save image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
