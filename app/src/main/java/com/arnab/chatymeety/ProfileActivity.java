package com.arnab.chatymeety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

enum State{
    FRIEND,NOT_FRIEND,SENT,RECEIVED
}

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference mRef;
    private ImageView profileImage;
    private TextView profileName,profileStatus;
    private Button multiBtn;
    private FirebaseUser curUser;
    private State state;
    private DatabaseReference db;
    private String profileUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        profileUid=getIntent().getExtras().getString("uid");
        profileImage=findViewById(R.id.profile_image);
        profileName=findViewById(R.id.profile_name);
        profileStatus=findViewById(R.id.profile_status);
        multiBtn=findViewById(R.id.profile_req_btn);
        curUser=FirebaseAuth.getInstance().getCurrentUser();
        db=FirebaseDatabase.getInstance().getReference();

        mRef= FirebaseDatabase.getInstance().getReference().child("user").child(profileUid);
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

        updateState();

        multiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiBtn.setClickable(false);
                if(state==State.NOT_FRIEND){

                    db.child("relation").child(curUser.getUid()).child(profileUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue()==null){
                                db.child("relation").child(curUser.getUid()).child(profileUid).setValue("sent").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        db.child("relation").child(profileUid).child(curUser.getUid()).setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ProfileActivity.this, "Request sent!", Toast.LENGTH_SHORT).show();
                                                multiBtn.setText("DELETE SENT REQUEST?");
                                                multiBtn.setClickable(true);
                                                state=State.SENT;

                                            }
                                        });
                                    }
                                });
                            }
                            else{
                                recreate();
                                Toast.makeText(ProfileActivity.this, "you have update!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
                else if (state==State.FRIEND){
                    db.child("relation").child(curUser.getUid()).child(profileUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue()!=null && snapshot.getValue().toString().equals("friend")){

                                db.child("relation").child(curUser.getUid()).child(profileUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        db.child("relation").child(profileUid).child(curUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ProfileActivity.this, "You guys are no longer friend", Toast.LENGTH_SHORT).show();
                                                multiBtn.setText("SENT REQUEST");
                                                multiBtn.setClickable(true);
                                                state=State.NOT_FRIEND;

                                            }
                                        });
                                    }
                                });
                            }
                            else{
                                recreate();
                                Toast.makeText(ProfileActivity.this, "you have update!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else if (state==State.SENT){
                    db.child("relation").child(curUser.getUid()).child(profileUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue()!=null && snapshot.getValue().toString().equals("sent")){
                                //
                                db.child("relation").child(curUser.getUid()).child(profileUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        db.child("relation").child(profileUid).child(curUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ProfileActivity.this, "Request cancelled", Toast.LENGTH_SHORT).show();
                                                multiBtn.setText("SENT REQUEST");
                                                multiBtn.setClickable(true);
                                                state=State.NOT_FRIEND;

                                            }
                                        });
                                    }
                                });
                            }
                            else{
                                recreate();
                                Toast.makeText(ProfileActivity.this, "you have update!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else {
                    //state received
                    db.child("relation").child(curUser.getUid()).child(profileUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue()!=null && snapshot.getValue().toString().equals("received")){
                                //
                                db.child("relation").child(curUser.getUid()).child(profileUid).setValue("friend").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        db.child("relation").child(profileUid).child(curUser.getUid()).setValue("friend").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ProfileActivity.this, "Say hello to your new friend", Toast.LENGTH_SHORT).show();
                                                multiBtn.setText("UNFRIEND");
                                                multiBtn.setClickable(true);
                                                state=State.FRIEND;

                                            }
                                        });
                                    }
                                });
                            }
                            else{
                                recreate();
                                Toast.makeText(ProfileActivity.this, "you have update!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(curUser.getUid().equals(profileUid)){
            startActivity(new Intent(ProfileActivity.this,SettingsActivity.class));
            finish();
        }
    }

    public void updateState(){
        db.child("relation").child(curUser.getUid()).child(profileUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue()==null){
                    state=State.NOT_FRIEND;
                    multiBtn.setText("SENT REQUEST");
                }
                else{
                    String tmp=snapshot.getValue().toString();
                    if(tmp.equals("friend")){
                        state=State.FRIEND;
                        multiBtn.setText("UNFRIEND");
                    }
                    else if(tmp.equals("sent")){
                        state=State.SENT;
                        multiBtn.setText("DELETE SENT REQUEST?");
                    }
                    else {
                        state=State.RECEIVED;
                        multiBtn.setText("ACCEPT REQUEST?");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
