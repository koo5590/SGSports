package com.example.sgsports;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewAppointmentActivity extends BaseActivity {
    private ListView listappointment;
    Button back;

    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private ArrayList<String> mArray = new ArrayList<>();
    private String userID;
    private FirebaseUser user;
    ArrayList<String> appointmentIDList = new ArrayList<>();
    String appointmentFacilityName;
    String appointmentFacilityType;
    String appointmentDate;
    String appointmentTime;
    String appointmentDetails;
    String appointmentID;
    String appointmentIDpassed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_viewappointment, contentFrameLayout);

        listappointment = (ListView) findViewById(R.id.appointmentlist);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ViewAppointmentActivity.this, android.R.layout.simple_list_item_1, mArray);
        listappointment.setAdapter(arrayAdapter);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        database = FirebaseFirestore.getInstance();

        database.collection("Appointment")
                .whereEqualTo("user", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                appointmentID = document.getId();
                                appointmentIDList.add(appointmentID);
                                appointmentFacilityName = document.getData().get("facilityName").toString();
                                appointmentFacilityType = document.getData().get("facilityType").toString();
                                appointmentDate = document.getData().get("date").toString();
                                appointmentTime = document.getData().get("timeslot").toString();
                                appointmentDetails = appointmentFacilityName + '\n' + appointmentFacilityType + '\n' + appointmentDate + ", " + appointmentTime;
                                mArray.add(appointmentDetails);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        } else {
                        }


                    }

                });

        listappointment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                appointmentIDpassed = appointmentIDList.get(position);
                Intent intent = new Intent(ViewAppointmentActivity.this, DeleteAppointmentActivity.class);
                intent.putExtra("appointmentID", appointmentIDpassed);
                startActivity(intent);

            }
        });




        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new Button.OnClickListener(){
            public void onClick (View V){
                Intent intent = new Intent(ViewAppointmentActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}





