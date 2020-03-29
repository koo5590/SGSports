package com.example.sgsports;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ReadActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_readreview, contentFrameLayout);

        Intent intent = getIntent();
        ReviewData reviewData = (ReviewData)intent.getSerializableExtra("review");
        if(reviewData!=null){

            TextView name = findViewById(R.id.facilityName);
            TextView rating = findViewById(R.id.ratingText);
            TextView text = findViewById(R.id.reviewText);

            name.setText(reviewData.getFacilityName());
            for(int i=0; i<reviewData.getRating(); i++)
                rating.append("★");
            text.setText(reviewData.getReview());

        }
    }
}
