package com.example.sgsports;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.google.android.gms.tasks.OnSuccessListener;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.Objects;


public class ViewProfileActivity extends BaseActivity {


    private TextView t1Name, t1Age, t1Gender, t1Mobile, t1Email;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String UserId;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_viewprof, contentFrameLayout);

        mAuth = FirebaseAuth.getInstance();
        UserId = mAuth.getCurrentUser().getUid();
        t1Email = (TextView)findViewById(R.id.emailProf);
        t1Age = (TextView)findViewById(R.id.ageProf);
        t1Gender = (TextView)findViewById(R.id.genderProf);
        t1Name = (TextView)findViewById(R.id.nameProf);
        t1Mobile = findViewById(R.id.mobileProf);

        //retrieve user info
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users").document(UserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //String age = documentSnapshot.getString("age");
                        String age = documentSnapshot.getString("age");
                        String email  = documentSnapshot.getString("useremail");
                        String gender  = documentSnapshot.getString("gender");
                        String name  = documentSnapshot.getString("username");
                        String mobile  = documentSnapshot.getString("mobilenum");

                        //UserData newUser = new UserData(user.getDisplayName(), user.getEmail(),null, null);
                        t1Age.setText(age);
                        t1Email.setText(email);
                        t1Gender.setText(gender);
                        t1Name.setText(name);
                        t1Mobile.setText(mobile);
                    }
                });
        //edit profile button
        findViewById(R.id.editB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(ViewProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.cancelB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(ViewProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //logout button
        findViewById(R.id.logoutB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){

                //log out
                mAuth.getInstance().signOut();
                //display success message
                Toast.makeText(ViewProfileActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                //go back to login page
                Intent intent = new Intent(ViewProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }
}
