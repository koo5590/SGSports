package com.example.sgsports;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;

/*** search facilities based on conditions set by user and name typed in by user ***/
public class SearchFacilitiesActivity extends AppCompatActivity {

    private final int RC_FILTER = 456;
    boolean filterOn;

    ArrayList<Facility> resultFacilities;    //search result
    ArrayList<Facility> filteredFacilities;  //list of facilities that satisfy filter options
    ArrayList<Facility> allFacilities;       //list of all facilities

    //List view where the search result is shown
    ListView facilityListView;

    //database from where facility data is retrieved
    FirebaseFirestore mFireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchfacilities);

        //create array objects
        allFacilities = new ArrayList<>();
        filteredFacilities = new ArrayList<>();
        resultFacilities = new ArrayList<>();

        //filter is off
        filterOn = false;

        //get ListView object
        facilityListView= (ListView) findViewById(R.id.facilityList);

        //get facility data from database
        mFireStore = FirebaseFirestore.getInstance();
        getFacilities();

        //filter button
        findViewById(R.id.filterB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                //start pop up activity to get user data
                Intent intent = new Intent(getApplicationContext(), PopupFilterActivity.class);
                startActivityForResult(intent, RC_FILTER);
            }
        });

        //search button
        findViewById(R.id.searchB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                //get text input from user
                EditText text = (EditText)findViewById(R.id.searchText);
                String name = text.getText().toString();
                //search facilities
                search(name);
                //show result
                showResult();
            }
        });

        //when user press 'enter' button
        findViewById(R.id.searchText).setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    //get text input from user
                    EditText text = (EditText)findViewById(R.id.searchText);
                    String name = text.getText().toString();
                    //search facilities
                    search(name);
                    //show result
                    showResult();
                    return true;
                }
                return false;
            }
        });


    }

    /*@Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_FILTER && data!=null){  //get filter options
            //get rate and type conditions
            int rate = data.getIntExtra("rate", 0);
            ArrayList<String> types = new ArrayList<>(data.getStringArrayListExtra("type_list"));
            Log.d("type", types.toString());
            //filter out facilities
            filter(rate, types);
            //filter is on
            filterOn = true;
        }

    }

    //search facilities
    void search(String name){
        //clear result array
        resultFacilities.clear();

        //if user enters no name
        if(name.length()==0){
            if(filterOn) //add all facilities after filtering

                resultFacilities.addAll(filteredFacilities);
            else         //add all existing facilities
                resultFacilities.addAll(allFacilities);
        }

        else {
            Iterator<Facility> iterator;
            if (filterOn)    //filter is used
                iterator = filteredFacilities.iterator();
            else           //filter is not used
                iterator = allFacilities.iterator();

            //add facility that contains user input string
            while (iterator.hasNext()) {
                Facility facility = iterator.next();
                if (facility.getName().toLowerCase().contains(name.toLowerCase()))
                    resultFacilities.add(facility);
            }
        }
        filterOn = false;
    }

    //show result on the screen
    void showResult(){
        FacilityListAdapter adapter = new FacilityListAdapter(resultFacilities, getApplicationContext());
        facilityListView.setAdapter(adapter);

        facilityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //get facility data
                Facility facility = resultFacilities.get(i);

                //move to facility details page
                Intent intent = new Intent();
                //Intent intent = new Intent(getApplicationContext(), );
                intent.putExtra("facility", facility);
                startActivity(intent);
                finish();

            }
        });
    }

    //filter out facilities
    void filter(int rate, ArrayList<String> types){
        boolean condition = true;  //to check if the facility has all types required

        //clear result array
        filteredFacilities.clear();

        //iterate through the list and find facilities that satisfy the conditions
        Iterator<Facility> iterator = allFacilities.iterator();
        while(iterator.hasNext()){
            //get a facility
            Facility facility = iterator.next();

            Log.d("type", facility.getType());

            if(10>=rate){
                //check if the facility has certain types
                for(int i=0; i<types.size(); i++){
                    if(!facility.getType().toLowerCase().contains(types.get(i).toLowerCase()))
                        condition = false;  //the facility does not have a certain type
                }
                if(condition)  //the facility satisfies all conditions
                    filteredFacilities.add(facility);
                condition = true;
            }
        }
    }

    void getFacilities(){
        mFireStore.collection("Facility").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc: task.getResult()){
                        Facility facility = doc.toObject(Facility.class);
                        allFacilities.add(facility);
                    }
                }

            }
        });
    }
}
