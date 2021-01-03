package com.arnab.chatymeety;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputEditText mStatus;
    private Button mChange;
    private DatabaseReference mRef;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mStatus=findViewById(R.id.status_text);
        mToolbar=findViewById(R.id.status_toolbar);
        mChange=findViewById(R.id.status_cng);
        mRef= FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status=getIntent().getExtras().get("status").toString();
        mStatus.setText(status);

        mChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //show progressbar here

                String currentStatus=mStatus.getText().toString();
                mRef.setValue(currentStatus);
            }
        });

    }
}
