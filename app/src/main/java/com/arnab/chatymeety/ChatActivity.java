package com.arnab.chatymeety;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
    private DatabaseReference dataRefForOnline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        currentUid=currentUser.getUid();
        rootRef=FirebaseDatabase.getInstance().getReference();

        //------for online check-------//
        dataRefForOnline = FirebaseDatabase.getInstance().getReference().child("user").child(currentUid);

        String name=getIntent().getExtras().getString("name");
        String thumbnail=getIntent().getExtras().getString("thumbnail");
        chatUid=getIntent().getExtras().getString("uid");
        //String online=getIntent().getExtras().getString("online");

        addBtn=findViewById(R.id.chat_add);
        sendBtn=findViewById(R.id.chat_send);
        text=findViewById(R.id.chat_text);
        recyclerView=findViewById(R.id.chat_recycler);
        linearLayoutManager=new LinearLayoutManager(this);
        msgAdapter=new MessageAdapter(msgList,currentUid,thumbnail);

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

        findViewById(R.id.chat_main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("debug","here1");
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("debug","here2");
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(getCurrentFocus()!=null){
                    IBinder iBinder=getCurrentFocus().getWindowToken();
                    if(imm!=null && iBinder!=null)imm.hideSoftInputFromWindow(iBinder, 0);
                }
                return false;
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

                    Map chatWon=new HashMap();
                    chatWon.put("lastMsg",chatText);
                    chatWon.put("seen",true);
                    chatWon.put("time",ServerValue.TIMESTAMP);

                    Map chatOther=new HashMap();
                    chatOther.put("lastMsg",chatText);
                    chatOther.put("seen",false);
                    chatOther.put("time",ServerValue.TIMESTAMP);

                    rootRef.child("chat").child(currentUid).child(chatUid).updateChildren(chatWon);
                    rootRef.child("chat").child(chatUid).child(currentUid).updateChildren(chatOther);

                    rootRef.child("message").child(currentUid).child(chatUid).push().setValue(msg);
                    rootRef.child("message").child(chatUid).child(currentUid).push().setValue(msg);

                    rootRef.child("notification").child(chatUid).child(chatUid).setValue(chatText);


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
                recyclerView.scrollToPosition(msgList.size()-1);
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

    @Override
    protected void onStart() {
        super.onStart();
        dataRefForOnline.child("online").setValue(true);
        rootRef.child("chat").child(currentUid).child(chatUid).child("seen").setValue(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataRefForOnline.child("online").setValue(false);
        rootRef.child("chat").child(currentUid).child(chatUid).child("seen").setValue(true);
    }


    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null && getCurrentFocus()!=sendBtn) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }*/
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

}
