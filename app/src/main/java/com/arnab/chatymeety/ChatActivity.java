package com.arnab.chatymeety;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        rootRef=FirebaseDatabase.getInstance().getReference();

        addBtn=findViewById(R.id.chat_add);
        sendBtn=findViewById(R.id.chat_send);
        text=findViewById(R.id.chat_text);



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
        final String chatUid=getIntent().getExtras().getString("uid");
        //String online=getIntent().getExtras().getString("online");

        final String currentUid=currentUser.getUid();

        chatName=findViewById(R.id.chat_bar_name);
        chatIcon=findViewById(R.id.chat_bar_image);
        chatTime=findViewById(R.id.chat_bar_time);

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

                    rootRef.child("message").child(currentUid).child(chatUid).push().setValue(msg);
                    rootRef.child("message").child(chatUid).child(currentUid).push().setValue(msg);


                }

            }
        });







    }
}
