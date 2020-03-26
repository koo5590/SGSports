package com.example.sgsports;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PopupFilterActivity extends Activity{

    //filter data
    int rate;
    ArrayList<String> checkedType = new ArrayList<>();
    CheckBox swimming,field,basketball,hockey,tabletennis,tennis,badminton,stadium,gym,squash;
    ArrayList<String> types;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //create pop up screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_filter);

        //set list of ratings
        ArrayList<Integer> rateList = new ArrayList<Integer>();
        for(int i=1; i<=5; i++)
            rateList.add(i);
        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, rateList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rate = 1;

        //set spinner
        Spinner ratingSpinner = (Spinner)findViewById(R.id.rating_spinner);
        ratingSpinner.setAdapter(spinnerArrayAdapter);
        ratingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rate = (Integer)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        types = new ArrayList<>();
        //set checkboxes
        swimming = findViewById(R.id.swimmingB);
        field = findViewById(R.id.fieldB);
        basketball = findViewById(R.id.basketballcourtB);
        hockey = findViewById(R.id.hockeyB);
        tabletennis = findViewById(R.id.tabletennisB);
        tennis = findViewById(R.id.tenniscourtB);
        badminton = findViewById(R.id.badmintonB);
        stadium = findViewById(R.id.stadiumB);
        gym = findViewById(R.id.gymB);
        squash = findViewById(R.id.squashB);



        Button filterButton = (Button)findViewById(R.id.submitsearch);
        filterButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                if (swimming.isChecked())
                    types.add(" SwimmingPool");

                if (field.isChecked())
                    types.add(" Field");

                if (basketball.isChecked())
                    types.add(" BasketballCourt");

                if (hockey.isChecked())
                    types.add(" HockeyCourt");

                if (tabletennis.isChecked())
                    types.add(" TableTennisCourt");

                if (tennis.isChecked())
                    types.add(" TennisCourt");

                if (badminton.isChecked())
                    types.add(" BadmintonCourt");

                if (stadium.isChecked())
                    types.add( " Stadium");

                if (gym.isChecked())
                    types.add(" Gym");

                if (squash.isChecked())
                    types.add(" SquashCourt");

                Intent intent  = new Intent();
                //send filter data to search activity
                intent.putExtra("rate", rate);
                intent.putExtra("type_list", types);
                setResult(RESULT_OK, intent);

                //finish this activity
                finish();
            }
        });

    }
}
