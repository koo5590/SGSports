package com.example.sgsports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class FacilitiesAdd extends AppCompatActivity {
    EditText name, latfac, longfac, facdescrip;
    String factype =" ";
    CheckBox swimming,field;
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
        facdescrip = findViewById(R.id.facdescription);
        facilitiesadd = findViewById(R.id.facilitiesadd);
        facility = new Facility();
        db = FirebaseFirestore.getInstance();

        swimming = findViewById(R.id.swimming);
        field = findViewById(R.id.field);




        facilitiesadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double Lat = Double.parseDouble(latfac.getText().toString().trim());
                Double Long = Double.parseDouble(longfac.getText().toString().trim());
                String description = facdescrip.getText().toString().trim();
                String namefac = name.getText().toString().trim();
                String newname = namefac.toLowerCase();
                if (swimming.isChecked()) {

                    String swimming = " SwimmingPool";

                    factype = swimming + factype;
                }

                if (field.isChecked()) {
                    String field = " Field";
                    factype = field + factype;
                }


                facility.setName(namefac);
                facility.setLatitude(Lat);
                facility.setLongitude(Long);
                facility.setDescription(description);
                facility.setType(factype);

                //is this correct way of checking duplicates?
                db.collection("Facility").whereEqualTo("name".toLowerCase(), newname).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().isEmpty()) {
                                        db.collection("Facility").document().set(facility);
                                        Toast.makeText(getApplicationContext(), "Facility added", Toast.LENGTH_SHORT).show();
                                    }

                                    else
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

