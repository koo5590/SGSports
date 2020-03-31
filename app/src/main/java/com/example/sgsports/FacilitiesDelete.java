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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FacilitiesDelete extends BaseActivity{
    private ListView facilitiylist;
    Button back;

    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private ArrayList<String> mArray = new ArrayList<>();
    private String userID;
    private FirebaseUser user;
    ArrayList<String> facilityIDList = new ArrayList<>();
    String FacilityName;
    String FacilityType;
    String FacilityDetails;
    String FacilityID;
    String facilityIDpassed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_facilitydelete, contentFrameLayout);

        facilitiylist = (ListView) findViewById(R.id.facilityList);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(FacilitiesDelete.this, android.R.layout.simple_list_item_1, mArray);
        facilitiylist.setAdapter(arrayAdapter);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        database = FirebaseFirestore.getInstance();

        database.collection("Facility").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                FacilityName = document.getData().get("name").toString();
                                FacilityType = document.getData().get("address").toString();
                                FacilityID = document.getId();
                                facilityIDList.add(FacilityID);

                                FacilityDetails = FacilityName + '\n' + FacilityType;
                                mArray.add(FacilityDetails);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        } else {
                        }


                    }

                });

        facilitiylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                facilityIDpassed = facilityIDList.get(position);
                //dialog to check again with user
                AlertDialog.Builder dlg = new AlertDialog.Builder(FacilitiesDelete.this);
                dlg.setTitle("Delete Facility");
                dlg.setMessage("Are you sure you want to delete this Facility?");

                //user wants to cancel the appointment
                dlg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        database.collection("Facility").document(facilityIDpassed).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Facility has been successfully deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(FacilitiesDelete.this, MainActivity.class);
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




        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new Button.OnClickListener(){
            public void onClick (View V){
                Intent intent = new Intent(FacilitiesDelete.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
