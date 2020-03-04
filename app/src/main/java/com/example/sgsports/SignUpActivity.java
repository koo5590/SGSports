package com.example.sgsports;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** create new user with email, username, password **/
public class SignUpActivity extends Activity {

    /** user input text **/
    EditText userEmailText;
    EditText usernameText;
    EditText passwordText;
    EditText confirmPWText;

    /** gender selection radio button **/
    RadioButton maleR;
    RadioButton femaleR;
    String gender;

    /** age selection spinner **/
    Spinner ageSpinner;
    int age;

    /** firebase **/
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;

    boolean result;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //get EditText objects
        userEmailText = (EditText)findViewById(R.id.newUserEmail);
        usernameText = (EditText)findViewById(R.id.newUsername);
        passwordText = (EditText)findViewById(R.id.newUserPW);
        confirmPWText = (EditText)findViewById(R.id.confirmPW);

        //get RadioButton objects
        maleR = (RadioButton)findViewById(R.id.maleR);
        femaleR = (RadioButton)findViewById(R.id.femaleR);
        gender = "Male";

        //set Radio buttons
        RadioButton.OnClickListener onClickListener = new RadioButton.OnClickListener(){
            public void onClick(View v){
                if(maleR.isChecked())
                    gender = "Male";
                else
                    gender = "Female";
            }
        };
        maleR.setOnClickListener(onClickListener);
        femaleR.setOnClickListener(onClickListener);
        maleR.setChecked(true);

        //set list of age (range from 1 to 100)
        ArrayList<Integer> ageList = new ArrayList<Integer>();
        for(int i=1; i<=100; i++)
            ageList.add(i);
        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, ageList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        age = 1;

        //set spinner
        ageSpinner = (Spinner)findViewById(R.id.ageSpinner);
        ageSpinner.setAdapter(spinnerArrayAdapter);
        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                age = (Integer)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //get firebase authentication instance
        mAuth = FirebaseAuth.getInstance();
        //get firestore db instance
        database  = FirebaseFirestore.getInstance();

        //sign up button
        findViewById(R.id.signUpB2).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                String userEmail = userEmailText.getText().toString().trim();
                String  username= usernameText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();
                String confirmpw = confirmPWText.getText().toString().trim();

                checkValid(userEmail, username, password, confirmpw);
            }
        });
    }

    /** check if user inputs are valid **/
    private void checkValid(final String email, final String name, final String pw, final String confirmpw){

        //check if any input is empty
        if(email.equals("") ||  name.equals("") || pw.equals("") || confirmpw.equals("")){
            Toast.makeText(SignUpActivity.this, "Please fill out the form" ,Toast.LENGTH_SHORT).show();
            return;
        }

        //check password
        if(!pw.equals(confirmpw)){
            Toast.makeText(SignUpActivity.this, "Please confirm your password again", Toast.LENGTH_SHORT).show();
            return;
        }

        //check if user name already exists
        database.collection("users")
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty())
                                createNewAccount(email, name, pw);
                            else
                                Toast.makeText(getApplicationContext(), "username alreayd exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    /** create new user account through firebase Auth **/
    private void createNewAccount(final String email, final String name, final String pw){
        mAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){ //successful
                    //get current user
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(SignUpActivity.this, "welcome!", Toast.LENGTH_SHORT).show();

                    //save user data to database
                    Map<String, Object> newUser = new HashMap<>();
                    newUser.put("name", name);
                    newUser.put("email", email);
                    newUser.put("gender", gender);
                    newUser.put("age", age);
                    newUser.put("explanation", null);

                    database.collection("users").add(newUser).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{ //failed
                    try{
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {    //password exception
                        Toast.makeText(SignUpActivity.this, "Your password should be more than 5 characters", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthUserCollisionException e) {  //user with same email already exists
                        Toast.makeText(SignUpActivity.this, "User email already exists", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {                            //wrong email address
                        Toast.makeText(SignUpActivity.this, "Please enter correct email address", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }
}
