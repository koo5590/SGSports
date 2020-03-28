package com.example.sgsports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private Spinner spinnerReport;
    int rating;
    List<String> week;
    String weekOne, weekTwo, weekThree, weekFour,weekFive;
    String weekSelected;
    TextView TvWeekSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        week = new ArrayList<String>();
        week.add("Week 1 of Mar, 2020");
        week.add("Week 2 of Mar, 2020");
        week.add("Week 3 of Mar, 2020");
        week.add("Week 4 of Mar, 2020");

        weekOne = "1/03/2020";
        weekTwo = "9/03/2020";
        weekThree = "16/03/2020";
        weekFour = "23/03/2020";
        weekFive = "30/03/2020";

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
                        weekSelected = weekOne;
                        break;
                    case 1:
                        weekSelected = weekTwo;
                        break;
                    case 2:
                        weekSelected = weekThree;
                        break;
                    case 3:
                        weekSelected = weekFour;
                        break;

                }

                TvWeekSelected = findViewById(R.id.weekSelected);
                TvWeekSelected.setText(weekSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
