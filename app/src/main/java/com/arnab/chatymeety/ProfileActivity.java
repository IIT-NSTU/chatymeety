package com.arnab.chatymeety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference mRef;
    private ImageView profileImage;
    private TextView profileName,profileStatus;
    private Button reqBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        String uid=getIntent().getExtras().getString("uid");
        profileImage=findViewById(R.id.profile_image);
        profileName=findViewById(R.id.profile_name);
        profileStatus=findViewById(R.id.profile_status);
        reqBtn=findViewById(R.id.profile_req_btn);
        mRef= FirebaseDatabase.getInstance().getReference().child("user").child(uid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name=snapshot.child("name").getValue().toString();
                String status=snapshot.child("status").getValue().toString();
                String imageLink=snapshot.child("imageLink").getValue().toString();
                profileName.setText(name);
                profileStatus.setText(status);
                Picasso.get().load(imageLink).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
