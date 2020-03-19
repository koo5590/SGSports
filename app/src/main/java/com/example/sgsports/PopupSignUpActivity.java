package com.example.sgsports;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/** this activity is for pop up screen which makes new user input more data when they sign in with google for the first time **/
public class PopupSignUpActivity extends Activity {

    /** username **/
    private EditText userNameText;
    private String username;

    /** mobile number **/
    private EditText mobileNumText;
    private String mobileNum;

    /** gender selection radio button **/
    private RadioButton maleR;
    private RadioButton femaleR;
    private String gender;

    /** age selection spinner **/
    private Spinner ageSpinner;
    private int age;

    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //show no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_signup);

        //get database
        database = FirebaseFirestore.getInstance();

        //get EditText object
        mobileNumText = (EditText)findViewById(R.id.mobileNum_gmail);
        userNameText = (EditText)findViewById(R.id.newUsername_gmail);

        //get RadioButton objects
        maleR = (RadioButton)findViewById(R.id.maleR_gmail);
        femaleR = (RadioButton)findViewById(R.id.femaleR_gmail);
        gender = "Male";

        //set Radio buttons
        RadioButton.OnClickListener onClickListener = new RadioButton.OnClickListener(){
            public void onClick(View v){
                if(maleR.isChecked()) //user chose Male
                    gender = "Male";
                else                  //user chose Female
                    gender = "Female";
            }
        };
        maleR.setOnClickListener(onClickListener);
        femaleR.setOnClickListener(onClickListener);
        //set default value as Male
        maleR.setChecked(true);

        //set list of age (range from 1 to 100)
        ArrayList<Integer> ageList = new ArrayList<Integer>();
        for(int i=1; i<=100; i++)
            ageList.add(i);
        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, ageList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        age = 1;

        //set spinner
        ageSpinner = (Spinner)findViewById(R.id.ageSpinner_gmail);
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

    }

    public void checkUserName(final View v){
        username = userNameText.getText().toString().trim();

        //check if user name already exists
        database.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //user name already exists
                            if (!task.getResult().isEmpty()){
                                Toast.makeText(getApplicationContext(), "username already exists", Toast.LENGTH_SHORT).show();
                            }
                            else
                                onClose(v);
                        }
                    }
                });
    }

    //close popup screen
    public void onClose(View v){
        mobileNum = mobileNumText.getText().toString().trim();
        //check if mobile number is valid
        if(mobileNum.length()!=8 || mobileNum.matches("([^0-9])")){
            Toast.makeText(PopupSignUpActivity.this, "Please check your mobile number again", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent  = new Intent();
        //send data (gender, age, mobilenum) to LoginActivity
        intent.putExtra("gender", gender);
        intent.putExtra("age", age);
        intent.putExtra("mobilenum", mobileNum);
        intent.putExtra("username", username);
        setResult(RESULT_OK, intent);

        //finish this activity
        finish();
    }

    //nothing happens when user presses the screen outside of this popup layer
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }

    //back not working
    @Override
    public void onBackPressed(){
        return;
    }
}
