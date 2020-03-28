package com.example.sgsports;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/*** base activity that contains slide menu on the left ***/
public class BaseActivity extends AppCompatActivity {

    static String userName="";

    Toolbar toolbar;
    ListView listView;
    DrawerLayout drawer;
    TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        //tool bar
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);
        drawer = (DrawerLayout)findViewById(R.id.drawer);

        //create menu list
        ArrayList<String> items = new ArrayList<>();
        items.add("Home"); items.add("Profile"); items.add("Appointments"); items.add("Favourites"); items.add("Weekly Reports");
        //check if the user is admin
        if(userName.equals("admin"))
            items.add("Add new Facility"); items.add("Manage Facilities");
        items.add("Logout");

        //menu list
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);
        listView = (ListView)findViewById(R.id.drawer_menulist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                Intent intent;
                switch(position){
                    case 1: //Profile
                        intent = new Intent(BaseActivity.this, ViewProfileActivity.class);
                        startActivity(intent);
                        break;
                    case 2: //Appointment
                        intent = new Intent(BaseActivity.this, BookAppointmentActivity.class);
                        startActivity(intent);
                        break;
                    case 4: //Report
                        intent = new Intent(BaseActivity.this, ReportActivity.class);
                        startActivity(intent);
                        break;


                }
                drawer.closeDrawer(GravityCompat.START);
            }
        });


    }

    //Hamburger button
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == android.R.id.home){
            if(!drawer.isDrawerOpen(GravityCompat.START))
                drawer.openDrawer(GravityCompat.START);
            else
                drawer.closeDrawer(GravityCompat.START);

        }
        return super.onOptionsItemSelected(item);
    }


}
