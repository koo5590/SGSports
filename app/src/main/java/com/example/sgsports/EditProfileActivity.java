package com.example.sgsports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class EditProfileActivity extends AppCompatActivity {


    private EditText t1Name, t1Age, t1Gender, t1Mobile, t1Email;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String UserId;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprof);

        mAuth = FirebaseAuth.getInstance();
        UserId = mAuth.getCurrentUser().getUid();
        t1Email = (EditText)findViewById(R.id.emailProf);
        t1Age = (EditText)findViewById(R.id.ageProf);
        t1Gender = (EditText)findViewById(R.id.genderProf);
        t1Name = (EditText) findViewById(R.id.nameProf);


        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users").document(UserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String age = documentSnapshot.getString("age");
                String email  = documentSnapshot.getString("useremail");
                String gender  = documentSnapshot.getString("gender");
                String name  = documentSnapshot.getString("username");


                //UserData newUser = new UserData(user.getDisplayName(), user.getEmail(),null, null);
                t1Age.setText(age);
                t1Email.setText(email);
                t1Gender.setText(gender);
                t1Name.setText(name);
            }
        });

        findViewById(R.id.updateB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){

                String  username= t1Name.getText().toString().trim();
                mFirestore.collection("users").document(UserId).update("username", username);

                String  age = t1Age.getText().toString().trim();
                mFirestore.collection("users").document(UserId).update("age", age);

                Toast.makeText(EditProfileActivity.this, "Edited Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ViewProfileActivity.class);
                startActivity(intent);
                finish();

            }
        });

        findViewById(R.id.cancelB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(EditProfileActivity.this, ViewProfileActivity.class);
                startActivity(intent);
            }
        });

    }
}