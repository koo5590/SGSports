package com.example.sgsports;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private FirebaseFirestore firestoreDB;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
