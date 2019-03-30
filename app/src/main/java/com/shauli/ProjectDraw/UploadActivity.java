package com.shauli.ProjectDraw;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class UploadActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_activity);

        setPreviewBitmap();
        setLocationText();
    }

    public void setPreviewBitmap() {
        Intent mIntent = getIntent();
        ImageView previewImg = findViewById(R.id.previewImg);

        Bitmap bitmap = Bitmap.createBitmap((BitmapFactory.decodeByteArray(mIntent.getByteArrayExtra("bitmap"), 0, mIntent.getByteArrayExtra("bitmap").length)));
        mIntent.removeExtra("bitmap");
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 3, bitmap.getHeight() / 3, true);

        previewImg.setImageBitmap(scaledBitmap);
    }

    public void setLocationText() {
        LocationManager locationManager;
        String locationProvider;
        TextView locationText = findViewById(R.id.locationText);

        // the location manager :)
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // criteria for getting a provider
        Criteria criteria = new Criteria();

        // the provider of the location.
        locationProvider = locationManager.getBestProvider(criteria, true);

        // the location object with longitude, latitude, altitude and speed info
        Location location = locationManager.getLastKnownLocation(locationProvider);

        if (location == null)
            locationText.setText("Unknown location");
        else
            locationText.setText("Location lat: " + location.getLatitude() + ", long: " + location.getLongitude());
    }
}
