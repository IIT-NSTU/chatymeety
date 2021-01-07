package com.arnab.chatymeety;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView recyclerView;
    private View mainView;
    private FirebaseRecyclerAdapter adapter;
    private DatabaseReference dataRefRequest,dataRefUsers;
    private FirebaseUser user;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView= inflater.inflate(R.layout.fragment_requests, container, false);
        recyclerView=mainView.findViewById(R.id.request_view);
        user= FirebaseAuth.getInstance().getCurrentUser();
        dataRefRequest= FirebaseDatabase.getInstance().getReference().child("request").child(user.getUid());
        dataRefUsers= FirebaseDatabase.getInstance().getReference().child("user");


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));





        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Request> options =
                new FirebaseRecyclerOptions.Builder<Request>()
                        .setQuery(dataRefRequest, Request.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Request, RequestsFragment.ViewHolder>(options) {
            @Override
            public RequestsFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_request, parent, false);
                Log.d("debug","here2");
                return new RequestsFragment.ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final RequestsFragment.ViewHolder holder, int position, Request model) {
                Log.d("debug",model.toString());
                final String uid=getRef(position).getKey();

                dataRefUsers.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String name=snapshot.child("name").getValue().toString();
                        final String thumbnail=snapshot.child("thumbnail").getValue().toString();
                        //final boolean online=(boolean)snapshot.child("online").getValue();
                        holder.setName(name);
                        holder.setImage(thumbnail);
                        //holder.setOnline(online);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("debug","here345");

                                startActivity(new Intent(getContext(),ProfileActivity.class).putExtra("uid",uid));

                            }
                        });

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)adapter.stopListening();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("debug","here2");
        }

        public void setName(String name){
            TextView userName=itemView.findViewById(R.id.request_name);
            userName.setText(name);
        }
        /*public void setOnline(boolean online){
            RelativeLayout layout=itemView.findViewById(R.id.single_chat_frame);
            if(online){
                layout.setBackgroundColor(Color.parseColor("#B4369E39"));
            }
            else {
                layout.setBackgroundColor(Color.WHITE);
            }

        }*/
        public void setImage(String imageLink){
            ImageView userImage=itemView.findViewById(R.id.request_pic);
            if (imageLink.equals("default")){
                userImage.setImageResource(R.drawable.defaultpic);
            }
            else Picasso.get().load(imageLink).into(userImage);
        }
    }

}
