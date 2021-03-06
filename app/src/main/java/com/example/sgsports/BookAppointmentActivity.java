package com.example.sgsports;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookAppointmentActivity extends BaseActivity{
    //date selection
    CalendarView calendar;
    String date;
    Calendar calendarDate;
    String day;

    // timeslot selection spinner
    Spinner timeStart;
    String timeSlot;

    // facility type selection spinner
    Spinner facilityTypeSpinner;
    String facilityName;
    List<String> facilityType;
    String[] s;
    String facType;
    String facTypeFinal;


    // book appointment selection
    Button book;
    Button cancel;

    // firebase
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore mDatabase;
    String currentUserID;

    // array of all options of time & array of unavailable time slots
    List<String> optionTime;
    List<String> disabledTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_bookappointment, contentFrameLayout);

        final Intent intent = getIntent();
        final Facility curFac = (Facility)intent.getSerializableExtra("facility");

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

        ArrayAdapter<String> spinnerFacilityChoice = new ArrayAdapter<String>(BookAppointmentActivity.this, android.R.layout.simple_spinner_item, facilityType);
        spinnerFacilityChoice.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facilityTypeSpinner = (Spinner)findViewById(R.id.facilityTypeSpinner);
        facilityTypeSpinner.setAdapter(spinnerFacilityChoice);
        facilityTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                facTypeFinal = facilityType.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        //get firebase authentication instance
        mAuth = FirebaseAuth.getInstance();
        //get firestore db instance
        mDatabase  = FirebaseFirestore.getInstance();

        // get Spinner
        timeStart = (Spinner) findViewById(R.id.timeStart);

        // Spinner Drop down elements
        optionTime = new ArrayList<>();
        optionTime.add("1000 - 1100");
        optionTime.add("1100 - 1200");
        optionTime.add("1200 - 1300");
        optionTime.add("1300 - 1400");
        optionTime.add("1400 - 1500");
        optionTime.add("1500 - 1600");
        optionTime.add("1600 - 1700");
        optionTime.add("1700 - 1800");
        optionTime.add("1800 - 1900");
        optionTime.add("1900 - 2000");
        optionTime.add("2000 - 2100");
        optionTime.add("2100 - 2200");

        //create ArrayList instance for disabledTime
        disabledTime = new ArrayList<>();


        // record selected date
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        calendarDate = Calendar.getInstance();
        date = df.format(calendarDate.getTime());
        calendar = (CalendarView) findViewById(R.id.calendar);
        //user cannot select past date
        calendar.setMinDate(calendar.getDate());
        //allow user to only select date in between 30 days from the current date
        calendar.setMaxDate(calendar.getDate()+2592000000L);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //get new date selected
                String mon = (month<9)?"0"+(month+1):""+(month+1);
                date = dayOfMonth + "/" + mon + "/" + year;
                //create new spinner adapter for selected date
                createSpinnerAdapter();
            }
        });
        createSpinnerAdapter();


        //book button
        book = (Button) findViewById(R.id.book);
        book.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                currentUser = mAuth.getCurrentUser();
                currentUserID = currentUser.getUid();
                checkValid(currentUserID, facilityName, facTypeFinal, date, timeSlot, curFac);
                //create new appointment
                //createNewAppointment(currentUserID, facilityName, facTypeFinal, date, timeSlot);
                //go back to main activity
                //Intent intent = new Intent(BookAppointmentActivity.this, MainActivity.class);
                //startActivity(intent);
            }
        });
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new Button.OnClickListener(){
            public void onClick (View V){

                Intent intent2 = new Intent(getApplicationContext(), MapSearchActivity.class);
                intent2.putExtra("facility", curFac);
                startActivity(intent2);
            }
        });


    }

    private void createSpinnerAdapter(){
        //clear disabledTime array
        disabledTime.clear();

        //check if any time slot has already reserved
        for(int i=0; i<optionTime.size(); i++){
            final String time = optionTime.get(i);
            mDatabase.collection("Appointment").whereEqualTo("date",date).whereEqualTo("timeslot",time).whereEqualTo("facilityName", facilityName).whereEqualTo("facilityType",facTypeFinal).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        //if the same (date, timeslot) found in the database
                        if (!task.getResult().isEmpty()) {
                            //timeslot for the selected date is already reserved
                            disabledTime.add(time);
                        }
                    }
                }
            });
        }

        // Creating new adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(BookAppointmentActivity.this, android.R.layout.simple_spinner_item, optionTime){
            @Override
            public boolean isEnabled(int position){
                //disable time slot that is already reserved
                if(disabledTime.contains(optionTime.get(position)))
                    return false;
                return true;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent){
                View tView = super.getDropDownView(position, convertView, parent);
                TextView tTextView = (TextView) tView;
                //set the text color of time slot that's been reserved to gray
                if(disabledTime.contains(optionTime.get(position))) {
                    tTextView.setTextColor(Color.GRAY);
                }
                //set the text color of available time slot to black
                else {
                    tTextView.setTextColor(Color.BLACK);
                    Typeface boldT = Typeface.defaultFromStyle(Typeface.BOLD);
                    tTextView.setTypeface(boldT);
                }
                return tView;
            }
        };


        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        timeStart.setAdapter(dataAdapter);
        timeStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                timeSlot= parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    /** check if user inputs are valid **/
    private void checkValid(final String currentUserID, final String facilityName, final String facTypeFinal, final String date, final String timeSlot, final Facility curFac){

        //check if time frame is available for booking

        mDatabase.collection("Appointment").whereEqualTo("date",date).whereEqualTo("timeslot",timeSlot).whereEqualTo("facilityName", facilityName).whereEqualTo("facilityType",facTypeFinal).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                   if (task.getResult().isEmpty()) {
                        createNewAppointment(currentUserID, facilityName, facTypeFinal, date, timeSlot);
                       Intent intent2 = new Intent(getApplicationContext(), MapSearchActivity.class);
                       intent2.putExtra("facility", curFac);
                       startActivity(intent2);
                    } else {
                        Toast.makeText(getApplicationContext(), "Someone has already booked at this timing. Please select a different timing.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    };

    /** create new appointment **/
    private void createNewAppointment(final String currentUserID, final String facilityName, final String facTypeFinal, final String date, final String timeSlot) {
        //save user data to database
        AppointmentData newAppointment = new AppointmentData(currentUserID, facilityName, facTypeFinal, date, timeSlot);

        mDatabase.collection("Appointment").document().set(newAppointment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(BookAppointmentActivity.this, "Appointment successfully booked", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
