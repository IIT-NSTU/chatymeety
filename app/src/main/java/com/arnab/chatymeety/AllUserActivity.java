package com.arnab.chatymeety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class AllUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private DatabaseReference dataRef;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);

        recyclerView=findViewById(R.id.all_user_list);
        toolbar=findViewById(R.id.all_user_toolbar);
        dataRef= FirebaseDatabase.getInstance().getReference().child("user");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(dataRef, User.class)
                        .build();
        Log.d("debug","here");

        adapter = new FirebaseRecyclerAdapter<User, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_user, parent, false);
                Log.d("debug","here2");
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(ViewHolder holder, int position, User model) {
                Log.d("debug",model.toString());
                holder.setName(model.getName());
                holder.setStatus(model.getStatus());
                holder.setImage(model.getThumbnail());
                final String uid=getRef(position).getKey();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(AllUserActivity.this,ProfileActivity.class).putExtra("uid",uid));
                    }
                });
                Log.d("debug","here2");
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)adapter.stopListening();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("debug","here2");
        }

        public void setName(String name){
            TextView userName=itemView.findViewById(R.id.user_name);
            userName.setText(name);
        }
        public void setStatus(String status){
            TextView userStatus=itemView.findViewById(R.id.user_status);
            userStatus.setText(status);
        }
        public void setImage(String imageLink){
            ImageView userImage=itemView.findViewById(R.id.user_pic);
            if (imageLink.equals("default")){
                userImage.setImageResource(R.drawable.defaultpic);
            }
            else Picasso.get().load(imageLink).into(userImage);
        }
    }
}
