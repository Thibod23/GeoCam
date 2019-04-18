package com.example.a533.geocam;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.example.a533.geocam.Constants.ERROR_DIALOG_REQUEST;
import static com.example.a533.geocam.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.a533.geocam.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Main_Activity";

    PictureRepository pictureRepository;
    LocationFinder locationFinder;

    boolean locationPermissionGranted = false;

    Button takePictureButton;
    Button showPictureButton;
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
        showPictureButton = findViewById(R.id.btn_afficherPhotos);
        showPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRecyclerPhoto = new Intent(getApplicationContext(), AfficherPhotosActivity.class);
                startActivity(intentRecyclerPhoto);
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

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (locationPermissionGranted) {
                    //getChatrooms();
                } else {
                    getLocationPermission();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkMapServices()) {
            if (locationPermissionGranted) {
                toastMessage("Localisation ok");
            } else {
                getLocationPermission();
                toastMessage("localisation pas ok");
            }
        }
    }


    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT);
    }

}
