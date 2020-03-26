package com.example.sgsports;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends BaseActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private GoogleMap map;
    private FirebaseFirestore firestoreDB;
    private FirebaseAuth auth;
    //new
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String UserId;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView tName;

    //new
    private boolean mLocationPermissionGranted=false;
    private FusedLocationProviderClient mFusedLocationClient;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Marker mCurrLocationMarker;
    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_main, contentFrameLayout);
        //setContentView(R.layout.activity_main);
        //getSupportActionBar().setTitle("Map Location");


        mAuth = FirebaseAuth.getInstance();
        UserId = mAuth.getCurrentUser().getUid();
        tName = findViewById(R.id.nameMain);
        //get username
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users").document(UserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name  = documentSnapshot.getString("username");

                tName.setText(name);
                BaseActivity.userName = name;
                userNameTextView = (TextView)findViewById(R.id.user_name);
                userNameTextView.setText(name);
            }
        });


        findViewById(R.id.searchbutton).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SearchFacilitiesActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.facilityadd).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, FacilitiesAdd.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.mapbutton).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.viewapp).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, ViewAppointmentActivity.class);
                startActivity(intent);
            }
        });


        //access Firestore instance
            //firestoreDB = FirebaseFirestore.getInstance();
            //DBexample();

            //access Auth instance
            //auth = FirebaseAuth.getInstance();
    }







}