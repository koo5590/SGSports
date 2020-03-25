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

        final String[] items = {"Home", "Profile", "Appointments", "Favourites", "Weekly Reports", "Logout"};
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);
        listView = (ListView)findViewById(R.id.drawer_menulist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                Intent intent;
                switch(position){
                    case 1:
                        intent = new Intent(BaseActivity.this, ViewProfileActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(BaseActivity.this, BookAppointmentActivity.class);
                        startActivity(intent);
                        break;

                }
                drawer.closeDrawer(GravityCompat.START);
            }
        });


    }

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
