package com.example.sgsports;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private ListView listreview;
    private Spinner spinnerReport;
    int rating;
    List<String> week;
    String weekZero, weekOne, weekTwo, weekThree, weekFour,weekFive,weekSix;
    String weekSelected, weekBefore, weekAfter;
    TextView TvWeekSelected;
    Double totalhours;
    String timeslot;
    String factype;
    String date;
    String apptInfo;

    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private ArrayList<String> reportArray = new ArrayList<>();
    private ArrayList<String> lastweekreportArray = new ArrayList<>();
    private String userID;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        week = new ArrayList<String>();
        week.add("Week 1 of Mar, 2020");
        week.add("Week 2 of Mar, 2020");
        week.add("Week 3 of Mar, 2020");
        week.add("Week 4 of Mar, 2020");
        week.add("Week 1 of Apr, 2020");

        weekZero = "22/02/2020";
        weekOne = "1/03/2020";
        weekTwo = "9/03/2020";
        weekThree = "16/03/2020";
        weekFour = "23/03/2020";
        weekFive = "30/03/2020";
        weekSix = "7/04/2020";

        //default, week is current week
        weekSelected = weekOne;
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, week);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //set spinner
        spinnerReport = (Spinner)findViewById(R.id.spinnerDate);
        spinnerReport.setAdapter(spinnerArrayAdapter);
        spinnerReport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinnerSelected = (String)adapterView.getItemAtPosition(i);

                switch(i){
                    case 0:
                        weekBefore = weekZero;
                        weekSelected = weekOne;
                        weekAfter = weekTwo;
                        break;
                    case 1:
                        weekBefore = weekOne;
                        weekSelected = weekTwo;
                        weekAfter = weekThree;
                        break;
                    case 2:
                        weekBefore = weekOne;
                        weekSelected = weekThree;
                        weekAfter = weekFour;
                        break;
                    case 3:
                        weekBefore = weekThree;
                        weekSelected = weekFour;
                        weekAfter = weekFive;
                        break;
                    case 4:
                        weekBefore = weekFour;
                        weekSelected = weekFive;
                        weekAfter = weekSix;
                        break;

                }

                TvWeekSelected = findViewById(R.id.weekSelected);
                TvWeekSelected.setText(weekSelected);
                 showCurrentDateInfo(weekBefore, weekSelected, weekAfter);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }




    public void showCurrentDateInfo(final String weekBefore, final String weekSelected, final String weekAfter)
    {
        //get all appts by this user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        database = FirebaseFirestore.getInstance();


        listreview = (ListView) findViewById(R.id.reportList);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ReportActivity.this, android.R.layout.simple_list_item_1, reportArray);
        listreview.setAdapter(arrayAdapter);


        database.collection("mockAppointment")
                .whereEqualTo("user", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                timeslot = document.getData().get("timeslot").toString();
                                factype = document.getData().get("facType").toString();
                                date = document.getData().get("date").toString();
                                apptInfo = timeslot + ", " + factype + '\n' + "Date: " + date + '\n';
                                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                try {

                                    Date start_date = df.parse(weekSelected);
                                    Date end_date= df.parse(weekAfter);
                                    Date appt_date = df.parse(date);
                                    Date prev_week = df.parse(weekBefore);

                                    if (appt_date.before(end_date) && appt_date.after(start_date))
                                        reportArray.add(apptInfo);
                                    else if (appt_date.before(start_date) && appt_date.after(end_date))
                                        lastweekreportArray.add(apptInfo);
                                    arrayAdapter.notifyDataSetChanged();

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        } else {
                        }


                    }

                });




    }}

