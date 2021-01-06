package com.arnab.chatymeety;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView chatName,chatTime;
    private CircleImageView chatIcon;
    private DatabaseReference chatDataRef,rootRef;
    private FirebaseUser currentUser;
    private ImageView addBtn,sendBtn;
    private EditText text;
    private List<Message> msgList=new ArrayList<>();
    private MessageAdapter msgAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private String chatUid,currentUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        currentUid=currentUser.getUid();
        rootRef=FirebaseDatabase.getInstance().getReference();

        addBtn=findViewById(R.id.chat_add);
        sendBtn=findViewById(R.id.chat_send);
        text=findViewById(R.id.chat_text);
        recyclerView=findViewById(R.id.chat_recycler);
        linearLayoutManager=new LinearLayoutManager(this);
        msgAdapter=new MessageAdapter(msgList,currentUid);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(msgAdapter);


        toolbar=findViewById(R.id.chat_tool_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView=inflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarView);

        String name=getIntent().getExtras().getString("name");
        String thumbnail=getIntent().getExtras().getString("thumbnail");
        chatUid=getIntent().getExtras().getString("uid");
        //String online=getIntent().getExtras().getString("online");



        chatName=findViewById(R.id.chat_bar_name);
        chatIcon=findViewById(R.id.chat_bar_image);
        chatTime=findViewById(R.id.chat_bar_time);

        loadMessages();

        chatName.setText(name);
        Picasso.get().load(thumbnail).into(chatIcon);

        chatDataRef= FirebaseDatabase.getInstance().getReference().child("chat");

        chatDataRef.child(currentUid).child(chatUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()==null){
                    Map addThis=new HashMap();
                    addThis.put("seen",false);
                    addThis.put("time", ServerValue.TIMESTAMP);
                    Map updateThis=new HashMap();
                    updateThis.put("chat/"+currentUid+"/"+chatUid,addThis);
                    updateThis.put("chat/"+chatUid+"/"+currentUid,addThis);

                    rootRef.updateChildren(updateThis);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chatText=text.getText().toString();
                if(!TextUtils.isEmpty(chatText)){
                    text.setText("");
                    Map msg=new HashMap();
                    msg.put("message",chatText);
                    msg.put("time",ServerValue.TIMESTAMP);
                    msg.put("type","text");
                    msg.put("seen",false);
                    msg.put("from",currentUid);

                    rootRef.child("message").child(currentUid).child(chatUid).push().setValue(msg);
                    rootRef.child("message").child(chatUid).child(currentUid).push().setValue(msg);


                }

            }
        });







    }
    void loadMessages(){
        rootRef.child("message").child(currentUid).child(chatUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message=snapshot.getValue(Message.class);
                msgList.add(message);
                msgAdapter.notifyDataSetChanged();
                Log.d("debug","here");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
