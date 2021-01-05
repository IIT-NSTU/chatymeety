package com.arnab.chatymeety;

import android.app.Application;

import androidx.annotation.NonNull;

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
        }

    }
}
