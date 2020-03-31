package com.example.sgsports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ReadActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_readreview, contentFrameLayout);

        findViewById(R.id.backB).setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });


        Intent intent = getIntent();
        ReviewData reviewData = (ReviewData)intent.getSerializableExtra("review");
        if(reviewData!=null){

            TextView name = findViewById(R.id.facilityName);
            TextView rating = findViewById(R.id.ratingText);
            TextView text = findViewById(R.id.reviewText);
            TextView userNameText = findViewById(R.id.userName);

            name.setText(reviewData.getFacilityName());
            userNameText.setText(reviewData.getUser());
            for(int i=0; i<reviewData.getRating(); i++)
                rating.append("★");
            text.setText(reviewData.getReview());

        }
    }
}
