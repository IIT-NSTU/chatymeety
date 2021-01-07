package com.arnab.chatymeety;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.application.isradeleon.notify.Notify;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatyMeety extends Application {

    private FirebaseUser user;
    private DatabaseReference dataRef;
    @Override
    public void onCreate() {
        super.onCreate();


        user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            dataRef = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
            dataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dataRef.child("online").onDisconnect().setValue(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            FirebaseDatabase.getInstance().getReference().child("notification").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Notify.build(getApplicationContext()).setId(0)
                            .setTitle("New massage")
                            .setContent("click to go to chat")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setColor(R.color.colorAccent)
                            //.setLargeIcon("https://images.pexels.com/photos/139829/pexels-photo-139829.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=150&w=440")
                            //.largeCircularIcon()
                            //.setPicture("https://images.pexels.com/photos/1058683/pexels-photo-1058683.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940")
                            .setAction(new Intent(getApplicationContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                            .show(); // Show notification
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



    }
}
