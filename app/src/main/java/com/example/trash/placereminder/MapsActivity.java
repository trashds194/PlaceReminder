package com.example.trash.placereminder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private Location placeLocation;
    private Marker mLocationMarker;
    GoogleMapOptions googleMapOptions;
    MapView mapView;

    List<HashMap<String, String>> placeData;

    double mLat, mLng;
    double placeLat, placeLng;

    int radius = 300;

    boolean firstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mSupportMapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        //Initialize Google Play Services
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    public StringBuilder stringBuilderMethod(Location mLocation) {
        //current location
        double mLatitude = mLocation.getLatitude();
        double mLongitude = mLocation.getLongitude();

        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location=").append(mLatitude).append(",").append(mLongitude);
        stringBuilder.append("&keyword=Пятерочка | пятерочка | ПЯТЕРОЧКА | Магнит | магнит | МАГНИТ");
        stringBuilder.append("&language=ru");
        stringBuilder.append("&radius=").append(radius);
        stringBuilder.append("&sensor=true");

        stringBuilder.append("&key=AIzaSyCnC0PVJ-dshihBvgv8IM5XgVxME_ZoNKk");

        Log.d("Map", "<><>api: " + stringBuilder.toString());

        return stringBuilder;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
        this.mLocation = location;
        Intent intent = new Intent(MapsActivity.this, StartActivity.class);
        intent.putExtra("loc", this.mLocation.toString());
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(final Location location) {
        mLocation = location;

        if (mLocationMarker != null) {
            mLocationMarker.remove();
        }

        mLat = location.getLatitude();
        mLng = location.getLongitude();

        mLocation.setLatitude(mLat);
        mLocation.setLongitude(mLng);

        //Place current location marker
        LatLng latLng = new LatLng(mLat, mLng);

        Circle circle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius).strokeColor(Color.argb(50, 255, 0, 0))
                .fillColor(Color.argb(100, 255, 0, 0)));

//        MarkerOptions markerOptions = new markerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Position");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//        mLocationMarker = mMap.addMarker(markerOptions);

        //Move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    //Query places with current location
                    StringBuilder stringBuilderValue = new StringBuilder(stringBuilderMethod(location));
                    PlacesTask placesTask = new PlacesTask();
                    placesTask.execute(stringBuilderValue.toString());

                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.start();

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (IOException e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(final String result) {
            Log.d("results", "<><> result: " + result);
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParserTask
            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            inputStream = urlConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            data = stringBuilder.toString();

            bufferedReader.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            inputStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jsonObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSON placeJSON = new PlaceJSON();

            try {
                jsonObject = new JSONObject(jsonData[0]);

                places = placeJSON.parse(jsonObject);
            } catch (JSONException e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
            Log.d("Map", "list size: " + list.size());
            // Clears all the existing markers;
            if (!firstRun) {
                mMap.clear();
            }
            firstRun = false;

            placeData = list;

            markerUpdates();
        }
    }

    void markerUpdates() {
        //Creating a market
        MarkerOptions markerOptions = new MarkerOptions();
        placeLocation = new Location("point B");
        for (int i = 0; i < placeData.size(); i++) {

            //Getting a place from the Places list
            HashMap<String, String> hashMapPlace = placeData.get(i);

            //Getting Latitude of the place
            placeLat = Double.parseDouble(hashMapPlace.get("lat"));

            //Getting longitude of the place
            placeLng = Double.parseDouble(hashMapPlace.get("lng"));

            //Getting name
            String name = hashMapPlace.get("place_name");

            Log.d("Map", "place name: " + name);

            //Getting vicinity
            String vicinity = hashMapPlace.get("vicinity");

            LatLng latLng = new LatLng(placeLat, placeLng);

            placeLocation.setLatitude(placeLat);
            placeLocation.setLongitude(placeLng);

            double distance = mLocation.distanceTo(placeLocation);

            //Setting the position for the market
            markerOptions.position(latLng);

            BigDecimal bd = new BigDecimal(distance).setScale(1, RoundingMode.HALF_EVEN);
            distance = bd.doubleValue();

            if (distance < radius) {
                markerOptions.title(name + " : " + distance + " метров");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                //Placed a marker on the touched position
                mMap.addMarker(markerOptions);
                Log.d("Markers", "Markers: " + markerOptions);
            }
        }
    }

    public class PlaceJSON {
        /*
          Receives a JSONObject and returns a list
         */
        public List<HashMap<String, String>> parse(JSONObject jsonObject) {
            JSONArray jsonArrayPlaces = null;
            try {
                jsonArrayPlaces = jsonObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return getPlaces(jsonArrayPlaces);
        }

        private List<HashMap<String, String>> getPlaces(JSONArray jsonPlaces) {
            int placesCount = jsonPlaces.length();
            List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> place = null;

            /* Taking each place, parses and adds to list object */
            for (int i = 0; i < placesCount; i++) {
                try {
                    /* Call getPlace with place JSON object to parse the place */
                    place = getPlace((JSONObject) jsonPlaces.get(i));
                    placesList.add(place);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return placesList;
        }

        /*
         * Parsing the Place JSON object
         */
        private HashMap<String, String> getPlace(JSONObject jsonPlace) {
            HashMap<String, String> place = new HashMap<String, String>();
            String placeName = "-RU-";
            String vicinity = "-RU-";
            String latitude = "";
            String longitude = "";
            String reference = "";

            try {
                // Extracting Place name, if available
                if (!jsonPlace.isNull("name")) {
                    placeName = jsonPlace.getString("name");
                }

                // Extracting Place Vicinity, if available
                if (!jsonPlace.isNull("vicinity")) {
                    vicinity = jsonPlace.getString("vicinity");
                }

                latitude = jsonPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = jsonPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
                reference = jsonPlace.getString("reference");

                place.put("place_name", placeName);
                place.put("vicinity", vicinity);
                place.put("lat", latitude);
                place.put("lng", longitude);
                place.put("reference", reference);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return place;
        }
    }
}
