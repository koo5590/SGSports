package com.example.sgsports;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PopupFilterActivity extends Activity{

    //filter data
    int rate;
    ArrayList<String> checkedType = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //create pop up screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_filter);

        //set list of ratings
        ArrayList<Integer> rateList = new ArrayList<Integer>();
        for(int i=1; i<=5; i++)
            rateList.add(i);
        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, rateList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rate = 1;

        //set spinner
        Spinner ratingSpinner = (Spinner)findViewById(R.id.rating_spinner);
        ratingSpinner.setAdapter(spinnerArrayAdapter);
        ratingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rate = (Integer)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //checkbox for type of facilities
        final ArrayList<String> typeItems = new ArrayList<>();
        typeItems.add("swimming"); typeItems.add("gym"); typeItems.add("etc");

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, typeItems);
        final ListView typeList = (ListView) findViewById(R.id.typeList);
        typeList.setAdapter(adapter);

        Button filterButton = (Button)findViewById(R.id.submitsearch);
        filterButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                SparseBooleanArray checkedItems = typeList.getCheckedItemPositions();
                int count = adapter.getCount();

                //get list of checked type
                for(int i=0; i<count; i++) {
                    if (checkedItems.get(i))
                        checkedType.add(typeItems.get(i));
                }

                Intent intent  = new Intent();
                //send filter data to search activity
                intent.putExtra("rate", rate);
                intent.putExtra("type_list", checkedType);
                setResult(RESULT_OK, intent);

                //finish this activity
                finish();
            }
        });

    }
}
