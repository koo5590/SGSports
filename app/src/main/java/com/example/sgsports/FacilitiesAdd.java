package com.example.sgsports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;


public class FacilitiesAdd extends AppCompatActivity {
    EditText name, latfac, longfac;
    Button facilitiesadd;
    FirebaseFirestore db;
    Facility facility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facilitiesadd);
        name = findViewById(R.id.namefac);
        latfac = findViewById(R.id.latfac);
        longfac = findViewById(R.id.longfac);
        facilitiesadd = findViewById(R.id.facilitiesadd);
        facility = new Facility();
        db = FirebaseFirestore.getInstance();

        facilitiesadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double Lat = Double.parseDouble(latfac.getText().toString().trim());
                Double Long = Double.parseDouble(longfac.getText().toString().trim());

                facility.setName(name.getText().toString().trim());
                facility.setLatitude(Lat);
                facility.setLongitude(Long);

                db.collection("Facility").document().set(facility)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Facility added", Toast.LENGTH_SHORT).show();}
                                else{
                                    Toast.makeText(getApplicationContext(), "Facility already exists", Toast.LENGTH_SHORT).show();
                                }
                            }


                        });

            }

        });

        findViewById(R.id.back).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(FacilitiesAdd.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}

