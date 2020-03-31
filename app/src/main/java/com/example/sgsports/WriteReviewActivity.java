package com.example.sgsports;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class WriteReviewActivity extends BaseActivity {
    private Spinner spinnerRating;
    int rating;
    List<Integer> reviewRating;

    private Spinner spinnerFacilityType;
    List<String> facilityType;
    String[] s;
    String facType;
    String facTypeFinal;


    EditText reviewDesc;
    String reviewText;

    Button submitReview;
    Button cancelReview;

    // firebase
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore mDatabase;
    String currentUserID;
    String facilityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_writereview, contentFrameLayout);

        Intent intent = getIntent();
        Facility curFac = (Facility)intent.getSerializableExtra("facility");

        mAuth = FirebaseAuth.getInstance();
        mDatabase  = FirebaseFirestore.getInstance();

        currentUserID = userName;


        facilityName = curFac.getName();
        facilityType = new ArrayList<>();
        Log.d("types: ", curFac.getType());
        facType = curFac.getType();
        if(facType.startsWith(" "))
            facType = facType.substring(1);
        s = facType.split(" ");
        for (int i = 0; i < s.length; i++) {
            facilityType.add(s[i]);
        }

        ArrayAdapter<String> spinnerFacType = new ArrayAdapter<String>(WriteReviewActivity.this, android.R.layout.simple_spinner_item, facilityType);
        spinnerFacType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFacilityType = (Spinner)findViewById(R.id.spinnerFacilityType);
        spinnerFacilityType.setAdapter(spinnerFacType);
        spinnerFacilityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                facTypeFinal = facilityType.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        reviewRating = new ArrayList<Integer>();
        reviewRating.add(0);
        reviewRating.add(1);
        reviewRating.add(2);
        reviewRating.add(3);
        reviewRating.add(4);
        reviewRating.add(5);

        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, reviewRating);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //default value is 0
        rating = 0;

        //set spinner
        spinnerRating = (Spinner)findViewById(R.id.spinnerRating);
        spinnerRating.setAdapter(spinnerArrayAdapter);
        spinnerRating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rating = (Integer)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        reviewDesc = (EditText)findViewById(R.id.reviewDesc);

        submitReview = (Button)findViewById(R.id.submitReview);
        submitReview.setOnClickListener(new Button.OnClickListener(){
            public void onClick (View V){
                reviewText = reviewDesc.getText().toString();
                createNewReview(currentUserID, facilityName, facTypeFinal, reviewText, rating);
                Intent intent = new Intent(WriteReviewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        cancelReview = (Button) findViewById(R.id.cancelReview);
        cancelReview.setOnClickListener(new Button.OnClickListener(){
            public void onClick (View V){
                Intent intent = new Intent(WriteReviewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
    /** create new review **/
    private void createNewReview(final String currentUserID, final String facilityName, final String facTypeFinal, String reviewText, int rating) {
        //save user data to database
        ReviewData newReview = new ReviewData(currentUserID, facilityName, facTypeFinal, reviewText, rating);

        mDatabase.collection("Review").document().set(newReview).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(WriteReviewActivity.this, "Review successfully submitted", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
