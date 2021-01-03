package com.arnab.chatymeety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView mCircleImageView;
    private TextView mName,mStatus;
    private Button mImageChange,mStatusChange;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mCircleImageView=findViewById(R.id.user_image);
        mName=findViewById(R.id.settings_name);
        mStatus=findViewById(R.id.settings_status);
        mImageChange=findViewById(R.id.settings_image_cng);
        mStatusChange=findViewById(R.id.settings_status_cng);
        mAuth=FirebaseAuth.getInstance();
        mRef= FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid());

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String imageLink=dataSnapshot.child("imageLink").getValue().toString();
                String thumbnail=dataSnapshot.child("thumbnail").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mStatusChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),StatusActivity.class).putExtra("status",mStatus.getText().toString()));

            }
        });

        mImageChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery=new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(gallery,1);
            }
        });







    }
}
