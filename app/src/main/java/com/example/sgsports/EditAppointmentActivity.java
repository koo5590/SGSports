package com.example.sgsports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditAppointmentActivity extends BaseActivity {
    String apptID;

    String tempName;
    String tempType;

    //date selection
    CalendarView calendarUpdate;
    String dateUpdate;
    Calendar calendarDateUpdate;

    // timeslot selection spinner
    Spinner timeStartUpdate;
    String timeSlotUpdate;

    // book appointment selection
    Button update;
    Button cancelUpdate;

    // firebase
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore mDatabase;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_editappointment, contentFrameLayout);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            apptID = extras.getString("apptID");
        }

        //get firebase authentication instance
        mAuth = FirebaseAuth.getInstance();
        //get firestore db instance
        mDatabase  = FirebaseFirestore.getInstance();

        DocumentReference docRef = mDatabase.collection("Appointment").document(apptID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        tempName = document.getData().get("facilityName").toString();
                        tempType = document.getData().get("facilityType").toString();
                    } else {

                    }
                } else {

                }
            }
        });


        // record selected date
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        calendarDateUpdate = Calendar.getInstance();
        dateUpdate = df.format(calendarDateUpdate.getTime());
        calendarUpdate = (CalendarView) findViewById(R.id.calendarUpdate);
        calendarUpdate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String mon = (month<9)?"0"+(month+1):""+(month+1);
                dateUpdate = dayOfMonth + "/" + mon + "/" + year;
            }
        });

        // Spinner Drop down elements

        List<String> optionTimeUpdate = new ArrayList<>();
        optionTimeUpdate.add("1000 - 1100");
        optionTimeUpdate.add("1100 - 1200");
        optionTimeUpdate.add("1200 - 1300");
        optionTimeUpdate.add("1300 - 1400");
        optionTimeUpdate.add("1400 - 1500");
        optionTimeUpdate.add("1500 - 1600");
        optionTimeUpdate.add("1600 - 1700");
        optionTimeUpdate.add("1700 - 1800");
        optionTimeUpdate.add("1800 - 1900");
        optionTimeUpdate.add("1900 - 2000");
        optionTimeUpdate.add("2000 - 2100");
        optionTimeUpdate.add("2100 - 2200");


        timeStartUpdate = (Spinner) findViewById(R.id.timeUpdate);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EditAppointmentActivity.this, android.R.layout.simple_spinner_item, optionTimeUpdate);


        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        timeStartUpdate.setAdapter(dataAdapter);
        timeStartUpdate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                timeSlotUpdate= parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //book button
        update = (Button) findViewById(R.id.updateAppt);
        update.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                currentUser = mAuth.getCurrentUser();
                currentUserID = currentUser.getUid();
                checkValid(currentUserID, tempName, tempType, dateUpdate, timeSlotUpdate);
            }
        });
        cancelUpdate = (Button) findViewById(R.id.cancelUpdate);
        cancelUpdate.setOnClickListener(new Button.OnClickListener(){
            public void onClick (View V){
                Intent intent = new Intent(EditAppointmentActivity.this, DeleteAppointmentActivity.class);
                startActivity(intent);
            }
        });

    }

    /** check if user inputs are valid **/
    private void checkValid(final String currentUserID, final String tempName, final String tempType, final String dateUpdate, final String timeSlotUpdate){

        //check if time frame is available for booking

        mDatabase.collection("Appointment").whereEqualTo("date",dateUpdate).whereEqualTo("timeslot",timeSlotUpdate).whereEqualTo("facilityName", tempName).whereEqualTo("facilityType",tempType).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        updateAppointment(currentUserID, tempName, tempType, dateUpdate, timeSlotUpdate);
                        Intent intent = new Intent(EditAppointmentActivity.this, ViewAppointmentActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Someone has already booked at this timing. Please select a different timing.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    };

    /** update appointment **/
    private void updateAppointment(final String currentUserID, final String tempName, final String tempType, final String dateUpdate, final String timeSlotUpdate) {
        //save user data to database
        AppointmentData newAppointment = new AppointmentData(currentUserID, tempName, tempType, dateUpdate, timeSlotUpdate);

        mDatabase.collection("Appointment").document(apptID).set(newAppointment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditAppointmentActivity.this, "Appointment details has been updated.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
