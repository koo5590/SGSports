package com.example.sgsports;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteAppointmentActivity extends BaseActivity {
    Button backtolist;
    Button editAppointment;
    Button deleteAppointment;

    String appointmentID;

    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_deleteappointment, contentFrameLayout);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            appointmentID = extras.getString("appointmentID");
        }
        database  = FirebaseFirestore.getInstance();

        deleteAppointment = (Button) findViewById(R.id.deleteappointment);
        deleteAppointment.setOnClickListener(new Button.OnClickListener(){
            public void onClick (View V){

                //dialog to check again with user
                AlertDialog.Builder dlg = new AlertDialog.Builder(DeleteAppointmentActivity.this);
                dlg.setTitle("Cancel Appointment");
                dlg.setMessage("Are you sure you want to cancel this appointment?");

                //user wants to cancel the appointment
                dlg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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

                dlg.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                dlg.show();

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
