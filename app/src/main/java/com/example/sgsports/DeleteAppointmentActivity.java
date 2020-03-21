package com.example.sgsports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteAppointmentActivity extends AppCompatActivity {
    Button backtolist;
    Button editAppointment;
    Button deleteAppointment;

    String appointmentID;

    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleteappointment);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            appointmentID = extras.getString("appointmentID");
        }
        database  = FirebaseFirestore.getInstance();

        deleteAppointment = (Button) findViewById(R.id.deleteappointment);
        deleteAppointment.setOnClickListener(new Button.OnClickListener(){
            public void onClick (View V){
                database.collection("Appointment").document(appointmentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Appointment has been successfully deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DeleteAppointmentActivity.this, ViewAppointmentActivity.class);
                        startActivity(intent);
                    }
                });

            }
        });

        editAppointment = (Button) findViewById(R.id.editappointment);
        editAppointment.setOnClickListener(new Button.OnClickListener(){
            public void onClick (View V){
                Intent intent = new Intent(DeleteAppointmentActivity.this, EditAppointmentActivity.class);
                intent.putExtra("apptID", appointmentID);
                startActivity(intent);
            }
        });

        backtolist = (Button) findViewById(R.id.backtolist);
        backtolist.setOnClickListener(new Button.OnClickListener(){
            public void onClick (View V){
                Intent intent = new Intent(DeleteAppointmentActivity.this, ViewAppointmentActivity.class);
                startActivity(intent);
            }
        });
    }
}
