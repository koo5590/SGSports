package com.example.sgsports;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class BookAppointmentActivity extends AppCompatActivity{
    //date selection
    CalendarView calendar;
    String date;
    Calendar calendarDate;

    // timeslot selection spinner
    Spinner timeStart;
    String timeSlot;

    // book appointment selection
    Button book;
    Button cancel;

    // firebase
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore mDatabase;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookappointment);


        // record selected date
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        calendarDate = Calendar.getInstance();
        date = df.format(calendarDate.getTime());
        calendar = (CalendarView) findViewById(R.id.calendar);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date = dayOfMonth + "/" + (month + 1) + "/" + year;
            }
        });

        // Spinner Drop down elements

        List<String> optionTime = new ArrayList<>();
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


        timeStart = (Spinner) findViewById(R.id.timeStart);
        // Creating adapter for spinner
       ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(BookAppointmentActivity.this, android.R.layout.simple_spinner_item, optionTime);


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

        //get firebase authentication instance
        mAuth = FirebaseAuth.getInstance();
        //get firestore db instance
        mDatabase  = FirebaseFirestore.getInstance();

        //book button
        book = (Button) findViewById(R.id.book);
        book.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                currentUser = mAuth.getCurrentUser();
                currentUserID = currentUser.getUid();
                checkValid(currentUserID, date, timeSlot);
            }
        });
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new Button.OnClickListener(){
            public void onClick (View V){
                Intent intent = new Intent(BookAppointmentActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

    /** check if user inputs are valid **/
    private void checkValid(final String currentUserID, final String date, final String timeSlot){

        //check if time frame is available for booking

        mDatabase.collection("Appointment").whereEqualTo("date",date).whereEqualTo("timeslot",timeSlot).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        createNewAppointment(currentUserID, date, timeSlot);
                        Intent intent = new Intent(BookAppointmentActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Someone has already booked at this timing", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    };

    /** create new appointment **/
    private void createNewAppointment(final String currentUserID, final String date, final String timeSlot) {
        //save user data to database
        AppointmentData newAppointment = new AppointmentData(currentUserID, date, timeSlot);

        mDatabase.collection("Appointment").document().set(newAppointment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(BookAppointmentActivity.this, "Appointment successfully booked", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
