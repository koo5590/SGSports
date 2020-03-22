package com.example.sgsports;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private FirebaseFirestore firestoreDB;
    private FirebaseAuth auth;
    //new
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String UserId;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView tName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            }
        });

        //set Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.profileB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, ViewProfileActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.bookappointment).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, BookAppointmentActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.appointmentlist).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, ViewAppointmentActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.addnewfac).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, FacilitiesAdd.class);
                startActivity(intent);
            }
        });

        //access Firestore instance
        //firestoreDB = FirebaseFirestore.getInstance();
        //DBexample();

        //access Auth instance
        //auth = FirebaseAuth.getInstance();
    }



    //initialize Map: shows location of NTU with marker
    @Override
    public void onMapReady(final GoogleMap googleMap){
        map = googleMap;

        LatLng NTU = new LatLng(1.3483153, 103.680946);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(NTU);
        markerOptions.title("NTU");
        markerOptions.snippet("Nanyang Technological University");
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLng(NTU));
        map.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    //add new Document to Firestore cloud db
    public void DBexample(){
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

        firestoreDB.collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("log ", "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("fail: ", "Error adding document", e);
            }
        });
    }
}