package com.example.sgsports;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.lang.*;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportActivity extends BaseActivity {

    private ListView listreview;
    private Spinner spinnerReport;
    int rating;
    List<String> week;
    String weekZero, weekOne, weekTwo, weekThree, weekFour,weekFive,weekSix;
    String weekSelected, weekBefore, weekAfter;
    TextView TvWeekSelected , tvTotalHours, tvMoreThan, tvMostActiveDay,tvMostActivityHours;
    Integer totalhours = 0 ,lastweekhours = 0 , morethanhours = 0, swimhours = 0, basketballhours = 0, gymhours = 0, runhours = 0, tennishours = 0;
    Integer monhrs = 0, tueshrs = 0, wedhrs = 0, thurshrs = 0, frihrs = 0;
    Integer mon = 0, tues = 0, wed = 0, thurs = 0, fri = 0;
    Integer swim = 0, basketball = 0, field = 0, squash, tennis = 0, badminton, stadium, gym = 0;
    String timeslot;
    String factype;
    String date;
    String day;
    String apptInfo;
    ImageView i;






    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private ArrayList<String> reportArray = new ArrayList<>();
    private ArrayList<String> lastweekreportArray = new ArrayList<>();
    private String userID;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_report, contentFrameLayout);


        //initialise spinner items
        week = new ArrayList<String>();
        week.add("Week 1 of Mar, 2020");
        week.add("Week 2 of Mar, 2020");
        week.add("Week 3 of Mar, 2020");
        week.add("Week 4 of Mar, 2020");
        week.add("Week 1 of Apr, 2020");

        weekZero = "22/02/2020";
        weekOne = "01/03/2020";
        weekTwo = "09/03/2020";
        weekThree = "16/03/2020";
        weekFour = "23/03/2020";
        weekFive = "30/03/2020";
        weekSix = "7/04/2020";

        //default, week is current week
       // weekSelected = weekOne;
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
                        showCurrentDateInfo(weekBefore, weekSelected, weekAfter);
                        break;
                    case 1:
                        weekBefore = weekOne;
                        weekSelected = weekTwo;
                        weekAfter = weekThree;
                        showCurrentDateInfo(weekBefore, weekSelected, weekAfter);
                        break;
                    case 2:
                        weekBefore = weekOne;
                        weekSelected = weekThree;
                        weekAfter = weekFour;
                        showCurrentDateInfo(weekBefore, weekSelected, weekAfter);
                        break;
                    case 3:
                        weekBefore = weekThree;
                        weekSelected = weekFour;
                        weekAfter = weekFive;
                        showCurrentDateInfo(weekBefore, weekSelected, weekAfter);
                        break;
                    case 4:
                        weekBefore = weekFour;
                        weekSelected = weekFive;
                        weekAfter = weekSix;
                        showCurrentDateInfo(weekBefore, weekSelected, weekAfter);
                        break;

                }




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
        morethanhours = 0;
        totalhours = 0;
        lastweekhours = 0;
        swimhours = 0;
        basketballhours = 0;
        gymhours = 0;
        runhours = 0;
        swim = 0; gym = 0; basketball = 0; field = 0;
        mon = 0; tues = 0; wed = 0; thurs = 0; fri = 0;
        monhrs = 0; tueshrs = 0; wedhrs = 0; thurshrs = 0; frihrs = 0;

        // Instantiate an ImageView and define its properties






        //listreview = (ListView) findViewById(R.id.reportList);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ReportActivity.this, android.R.layout.simple_list_item_1, reportArray);
        //listreview.setAdapter(arrayAdapter);


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
                                day = document.getData().get("Day").toString();
                                apptInfo = timeslot + ", " + factype + '\n' + "Date: " + date + '\n';
                                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                try {

                                    Date start_date = df.parse(weekSelected);
                                    Date end_date= df.parse(weekAfter);
                                    Date appt_date = df.parse(date);
                                    Date prev_week = df.parse(weekBefore);

                                    //get most active activity

                                    //get most active days
                                    if (factype.equals("Swim")&& appt_date.before(end_date) && appt_date.after(start_date))
                                    {
                                        swim++;
                                        swimhours++;
                                    }
                                    if (factype.equals("Basketball")&& appt_date.before(end_date) && appt_date.after(start_date))
                                    {
                                        basketball++;
                                        basketballhours++;
                                    }
                                    if (factype.equals("Gym")&& appt_date.before(end_date) && appt_date.after(start_date))
                                    {
                                        gym++;
                                        gymhours++;
                                    }
                                    if (factype.equals("Field")&& appt_date.before(end_date) && appt_date.after(start_date))
                                    {
                                        field++;
                                        runhours++;
                                    }



                                    if (day.contentEquals("Monday")&& appt_date.before(end_date) && appt_date.after(start_date))
                                    {
                                        mon++;
                                        monhrs++;
                                    }

                                    if (day.equals("Tuesday")&& appt_date.before(end_date) && appt_date.after(start_date))
                                    {
                                        tues++;
                                        tueshrs++;
                                    }

                                    if (day.equals("Wednesday")&& appt_date.before(end_date) && appt_date.after(start_date))
                                    {
                                        wed++;
                                        wedhrs++;
                                    }

                                    if (day.equals("Thursday")&& appt_date.before(end_date) && appt_date.after(start_date))
                                    {
                                        thurs++;
                                        thurshrs++;
                                    }

                                    if (day.contentEquals("Friday") && appt_date.before(end_date) && appt_date.after(start_date))
                                    {
                                        fri++;
                                        frihrs++;
                                    }

                                    if (appt_date.before(end_date) && appt_date.after(start_date)) {
                                        //reportArray.add(apptInfo);
                                        totalhours = totalhours + 1;
                                    }
                                    if (appt_date.before(start_date) && appt_date.after(prev_week)) {
                                        //lastweekreportArray.add(apptInfo);
                                        lastweekhours = lastweekhours + 1;
                                    }
                                    //arrayAdapter.notifyDataSetChanged();
                                    morethanhours = totalhours - lastweekhours;
                                    //initialise all textview items
                                    tvTotalHours = findViewById(R.id.tvTotalHours);
                                    tvMoreThan = findViewById(R.id.tvMoreThan);
                                    tvMostActiveDay = findViewById(R.id.tvMostActiveDay);
                                    tvMostActivityHours = findViewById(R.id.tvMostActivityHours);

                                    tvTotalHours.setText(totalhours.toString() + " Hours");

                                    if (morethanhours < 0) {
                                        morethanhours = Math.abs(morethanhours);
                                        tvMoreThan.setText("(" + morethanhours.toString() + " hours less than last week)");
                                    }
                                    else
                                    {
                                        tvMoreThan.setText("(" + morethanhours.toString() + " hours more than last week)");
                                    }

                                    //find most active date
                                    Map<String,Integer> vars = new HashMap<>();
                                    // HashMap<String, Integer> vars = new HashMap<String,Integer>();
                                    vars.put("Monday", new Integer(mon));
                                    vars.put("Tuesday", new Integer(tues));
                                    vars.put("Wednesday", new Integer(wed));
                                    vars.put("Thursday", new Integer(thurs));
                                    vars.put("Friday", new Integer(fri));
                                    int max = Collections.max(vars.values());
                                    String key = null;
                                    //List<String> keys = new ArrayList<>();
                                    for (Map.Entry<String, Integer> entry : vars.entrySet()) {
                                        if (entry.getValue()==max) {
                                            key = entry.getKey();
                                        }
                                    }
                                    tvMostActiveDay.setText(key);

                                    //find most done sport
                                    Map<String,Integer> sports = new HashMap<>();
                                    // HashMap<String, Integer> vars = new HashMap<String,Integer>();
                                    sports.put("Gym", new Integer(gym));
                                    sports.put("Swim", new Integer(swim));
                                    sports.put("Basketball", new Integer(basketball));
                                    sports.put("Field", new Integer(runhours));

                                    int max2 = Collections.max(sports.values());
                                    String key2 = null;
                                    //List<String> keys = new ArrayList<>();
                                    for (Map.Entry<String, Integer> entry2 : sports.entrySet()) {
                                        if (entry2.getValue()==max2) {
                                            key2 = entry2.getKey();
                                        }
                                    }

                                    i = findViewById(R.id.imageView);

                                    if (key2.equals("Gym")) {
                                        tvMostActivityHours.setText(gymhours.toString() + " hours");
                                        i.setImageResource(R.drawable.gym);
                                    }
                                    else if (key2.equals("Swim")) {
                                        tvMostActivityHours.setText(swimhours.toString() + " hours");
                                        i.setImageResource(R.drawable.swimming);
                                    }
                                    else if (key2.equals("Field")) {
                                        tvMostActivityHours.setText(runhours.toString() + " hours");
                                        i.setImageResource(R.drawable.running);
                                    }
                                    else {
                                        tvMostActivityHours.setText(basketballhours.toString() + " hours");
                                        i.setImageResource(R.drawable.bball);


                                    }
                                    //plot graph for days

                                    if (appt_date.before(end_date) && appt_date.after(start_date)) {
                                        GraphView barchart = (GraphView) findViewById(R.id.graphDays);
                                        BarGraphSeries<DataPoint> barchat = new BarGraphSeries<DataPoint>(new DataPoint[] {
                                                new DataPoint(1, monhrs),
                                                new DataPoint(2,tueshrs),
                                                new DataPoint(3,wedhrs),
                                                new DataPoint(4,thurshrs),
                                                new DataPoint(5,frihrs)
                                        });

                                        barchat.setSpacing(50); // 50% spacing between bars
                                        barchat.setAnimated(true);
                                        barchart.removeAllSeries();
                                        barchart.addSeries(barchat);
                                        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(barchart);
                                        staticLabelsFormatter.setHorizontalLabels(new String[] {"mon", "tues", "wed","thurs","fri"});
                                        barchart.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

                                        // set the viewport wider than the data, to have a nice view
                                        barchart.getViewport().setMinX(1d);
                                        barchart.getViewport().setMaxX(5d);
                                        barchart.getViewport().setXAxisBoundsManual(true);
                                        // styling
                                        barchat.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                                            @Override
                                            public int get(DataPoint data) {
                                                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
                                            }
                                        });


                                        // draw values on top
                                        barchat.setDrawValuesOnTop(true);
                                        barchat.setValuesOnTopColor(Color.BLUE);
                                        //series.setValuesOnTopSize(50);



                                    }

                                    //plot graph for sports

                                    if (appt_date.before(end_date) && appt_date.after(start_date)) {
                                        GraphView sportchart = (GraphView) findViewById(R.id.graphSports);
                                        BarGraphSeries<DataPoint> sportchat = new BarGraphSeries<DataPoint>(new DataPoint[]{
                                                new DataPoint(1, basketballhours),
                                                new DataPoint(2, swimhours),
                                                new DataPoint(3, gymhours),
                                                new DataPoint(4, runhours),
                                                new DataPoint(5, 0)


                                        });

                                        sportchat.setSpacing(40); // 50% spacing between bars
                                        sportchat.setAnimated(true);
                                        sportchart.removeAllSeries();
                                        sportchart.addSeries(sportchat);
                                        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(sportchart);
                                        staticLabelsFormatter.setHorizontalLabels(new String[]{"bball", "swim", "gym", "run", "tennis"});
                                        sportchart.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

                                        // set the viewport wider than the data, to have a nice view
                                        sportchart.getViewport().setMinX(1d);
                                        sportchart.getViewport().setMaxX(5d);
                                        //sportchart.getViewport().setScalable(true);  // activate horizontal zooming and scrolling
                                        //sportchart.getViewport().setScrollable(true);  // activate horizontal scrolling
                                        //sportchart.getViewport().setScalableY(true);
                                        sportchart.getViewport().setXAxisBoundsManual(true);
                                        // styling
                                        sportchat.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                                            @Override
                                            public int get(DataPoint data) {
                                                return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
                                            }
                                        });


                                        // draw values on top
                                        sportchat.setDrawValuesOnTop(true);
                                        sportchat.setValuesOnTopColor(Color.BLUE);
                                        //series.setValuesOnTopSize(50);


                                    }



                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        } else {
                        }



                }






    });
}}

