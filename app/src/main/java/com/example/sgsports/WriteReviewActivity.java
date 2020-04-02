package com.example.sgsports;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class WriteReviewActivity extends BaseActivity {
    RatingBar reviewRating;
    float i;
    int rating;

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
        final Facility curFac = (Facility)intent.getSerializableExtra("facility");

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

        reviewRating = (RatingBar) findViewById(R.id.reviewRatingBar);


        reviewDesc = (EditText)findViewById(R.id.reviewDesc);

        submitReview = (Button)findViewById(R.id.submitReview);
        submitReview.setOnClickListener(new Button.OnClickListener(){
            public void onClick (View V){
                reviewText = reviewDesc.getText().toString();
                i = reviewRating.getRating();
                rating = Math.round(i);
                createNewReview(currentUserID, facilityName, facTypeFinal, reviewText, rating);
                Intent intent2 = new Intent(getApplicationContext(), MapSearchActivity.class);
                intent2.putExtra("facility", curFac);
                startActivity(intent2);
            }
        });

        cancelReview = (Button) findViewById(R.id.cancelReview);
        cancelReview.setOnClickListener(new Button.OnClickListener(){
            public void onClick (View V){
                Intent intent2 = new Intent(getApplicationContext(), MapSearchActivity.class);
                intent2.putExtra("facility", curFac);
                startActivity(intent2);
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
