package com.example.trash.placereminder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

public class StartActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;

    private GoogleApiClient mGoogleApiClient;

    FloatingActionButton fab;
    FloatingActionButton fabMap;
    FloatingActionButton fabPlaces;

    Toolbar mToolbar;

    private TextView textView;

    Location location;

    private Animation fabOpen, fabClose, rotateForward, rotateBackward;

    private int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 4;
    private int PLACE_PICKER_REQUEST = 1;

    boolean isOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        checkPermissions();
        findElements();

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API).enableAutoManage(this, this).build();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });
        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Intent intent = new Intent(StartActivity.this, MapsActivity.class);
                startActivityForResult(intent, 2);
            }
        });
        fabPlaces.setOnClickListener(new View.OnClickListener() {
            @Override()
            public void onClick(View v) {
                animateFab();
                Intent intent = new Intent(StartActivity.this, AddMapRemindActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String loc = data.getStringExtra("loc");
        textView.setText(loc);

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getId());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void findElements() {
        fab = findViewById(R.id.fab);
        fabMap = findViewById(R.id.fab_map);
        fabPlaces = findViewById(R.id.fab_places);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItem = item.getItemId();

        switch (menuItem) {
            case R.id.action_settings:
                Toast.makeText(StartActivity.this, "Menu", Toast.LENGTH_SHORT).show();
                Intent settingsIntent = new Intent(StartActivity.this, SettingsMainActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.action_help:
                Intent helpIntent = new Intent(StartActivity.this, HelpMainActivity.class);
                startActivity(helpIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void animateFab() {
        if (isOpen) {
            fab.startAnimation(rotateBackward);
            fabMap.startAnimation(fabClose);
            fabPlaces.startAnimation(fabClose);
            fabMap.setClickable(false);
            fabPlaces.setClickable(false);
            isOpen = false;
        } else {
            fab.startAnimation(rotateForward);
            fabMap.startAnimation(fabOpen);
            fabPlaces.startAnimation(fabOpen);
            fabMap.setClickable(true);
            fabPlaces.setClickable(true);
            isOpen = true;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Toast.makeText(this, "Все разрешения уже выданы", Toast.LENGTH_SHORT).show();
        }
    }
}
