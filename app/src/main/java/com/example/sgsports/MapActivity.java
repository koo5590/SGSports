package com.example.sgsports;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends BaseActivity implements OnMapReadyCallback{

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private GoogleMap map;

    LinearLayout infoL;
    ListView reviewList;

    ArrayList<ReviewData> reviews;

    //new
    private boolean mLocationPermissionGranted=false;
    private FusedLocationProviderClient mFusedLocationClient;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Marker mCurrLocationMarker;
    Location mLastLocation;
    CollectionReference facilityref;
    //firebase database
    FirebaseFirestore mFireStore;
    ArrayList<Facility> allFacilities;
    private ChildEventListener mChildEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_map, contentFrameLayout);

        //initialize map and current location
        initMap();
        //book button
        findViewById(R.id.bookapp).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MapActivity.this, BookAppointmentActivity.class);
                startActivity(intent);
            }
        });
        //write a review button
        findViewById(R.id.writereview).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MapActivity.this, WriteReviewActivity.class);
                startActivity(intent);
            }
        });

        infoL = (LinearLayout)findViewById(R.id.infoLayout);
        reviewList = (ListView)findViewById(R.id.reviewList);

        //information button
        findViewById(R.id.infoB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                infoL.setVisibility(View.VISIBLE);
                reviewList.setVisibility(View.GONE);
            }
        });

        //review list button
        findViewById(R.id.reviewB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                infoL.setVisibility(View.GONE);
                reviewList.setVisibility(View.VISIBLE);
            }
        });


    }


    private void readData(final FireStoreCallback fireStoreCallback){
        facilityref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot document:task.getResult()){
                        Facility facility = document.toObject(Facility.class);
                        allFacilities.add(facility);
                    }
                    fireStoreCallback.onCallback(allFacilities);
                }
            }
        });
    }
    private interface FireStoreCallback{
        void onCallback(ArrayList<Facility> List);
    }
    void initMap(){
        //set Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mFireStore = FirebaseFirestore.getInstance();
        allFacilities = new ArrayList<>();
        facilityref = mFireStore.collection("Facility");
        readData(new FireStoreCallback() {
            private static final String TAG = "error";

            @Override
            public void onCallback(ArrayList<Facility> list) {
                for (int i = 0; i < allFacilities.size(); i++) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng location = new LatLng(allFacilities.get(i).getLatitude(), allFacilities.get(i).getLongitude());
                    markerOptions.position(location);
                    String name = allFacilities.get(i).getName();
                    markerOptions.title(name);
                    markerOptions.snippet(allFacilities.get(i).getType());
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                    map.addMarker(markerOptions);
                }
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            TextView namefac = findViewById(R.id.name);
            TextView typefac = findViewById(R.id.typeText);
            TextView address = findViewById(R.id.addrText);
            @Override
            public boolean onMarkerClick(Marker marker) {
                String markertitle = marker.getTitle();
                String snip = marker.getSnippet();
                for (Facility fac : allFacilities){
                    String checkname = fac.getName();
                    // check the marker with the database data
                    if (checkname.equals(markertitle)) {
                        namefac.setText(markertitle);
                        String type = fac.getType();
                        if(type.startsWith(" "))
                            type = type.substring(1);
                        typefac.setText(type.replaceAll(" ", ", "));
                        address.setText(fac.getAddress());
                        break;
                    }
                }

                reviews = new ArrayList<>();
                mFireStore.collection("Review").whereEqualTo("facilityName", markertitle).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc: task.getResult()){
                                ReviewData review = doc.toObject(ReviewData.class);
                                reviews.add(review);
                            }

                            ReviewListAdapter adapter = new ReviewListAdapter(reviews, getApplicationContext());
                            reviewList.setAdapter(adapter);
                        }
                    }
                });

//
//
                return false;
            }
        });


//
//        LatLng NTU = new LatLng(1.3483153, 103.680946);
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(NTU);
//        markerOptions.title("NTU");
//        markerOptions.snippet("Nanyang Technological University");
//        map.addMarker(markerOptions);


//        try {
//            GeoJsonLayer Layer = new GeoJsonLayer(map, R.raw.sports, this);
//            GeoJsonPolygonStyle polygonStyle = Layer.getDefaultPolygonStyle();
//            polygonStyle.setStrokeColor(Color.GREEN);
//            polygonStyle.setStrokeWidth(10);
//            Layer.addLayerToMap();
//        } catch (IOException e) {
//
//        } catch (JSONException e) {
//        }


        mLocationRequest = new LocationRequest();
        //  mLocationRequest.setInterval(120000); // two minute interval
        //mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                map.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            map.setMyLocationEnabled(true);
        }
    }

    private void getLastKnownLocation(){
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
                }
            }
        });


    }


    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getLastKnownLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    getLastKnownLocation();
                }
                else{
                    getLocationPermission();
                }            }        }
    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                mCurrLocationMarker = map.addMarker(markerOptions);
                //move map camera
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            }
        }};

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                        new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        } }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        map.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

}