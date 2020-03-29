package com.example.sgsports;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewReviewActivity extends BaseActivity{
    private ListView listreview;
    Button back;

    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private ArrayList<String> reviewArray = new ArrayList<>();
    private String userID;
    private FirebaseUser user;
    ArrayList<String> appointmentIDList = new ArrayList<>();
    String facilityName;
    String facilityType;
    String reviewDesc;
    String rating;
    String reviewInfo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_viewreview, contentFrameLayout);

        listreview = (ListView) findViewById(R.id.reviewList);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ViewReviewActivity.this, android.R.layout.simple_list_item_1, reviewArray);
        listreview.setAdapter(arrayAdapter);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        database = FirebaseFirestore.getInstance();

        database.collection("Review")
                .whereEqualTo("user", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                facilityName = document.getData().get("facilityName").toString();
                                facilityType = document.getData().get("facilityType").toString();
                                reviewDesc = document.getData().get("review").toString();
                                rating = document.getData().get("rating").toString();
                                reviewInfo = facilityName + ", " + facilityType + '\n' + "Rating: " + rating + '\n' + "Review: " + reviewDesc;
                                reviewArray.add(reviewInfo);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        } else {
                        }


                    }

                });
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new Button.OnClickListener(){
            public void onClick (View V){
                Intent intent = new Intent(ViewReviewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

}
