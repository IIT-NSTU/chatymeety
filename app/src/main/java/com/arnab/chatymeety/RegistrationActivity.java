package com.arnab.chatymeety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private Button mbutton;
    private TextInputEditText mName,mEmail,mPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mtoolbar=findViewById(R.id.reg_toolbar);
        mAuth=FirebaseAuth.getInstance();
        mRef=FirebaseDatabase.getInstance().getReference().child("user");
        mbutton=findViewById(R.id.reg_button);
        mName=findViewById(R.id.reg_name);
        mEmail=findViewById(R.id.reg_email);
        mPassword=findViewById(R.id.reg_pass);


        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name=mName.getText().toString();
                String email=mEmail.getText().toString();
                String password=mPassword.getText().toString();
                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(RegistrationActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<6){
                    Toast.makeText(RegistrationActivity.this, "Choose a strong password", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                HashMap<String,String> info=new HashMap<>();
                                info.put("name",name);
                                info.put("status","Hi I'm "+name);
                                info.put("imageLink","default");
                                info.put("thumbnail","default");

                                String uid=mAuth.getCurrentUser().getUid();
                                mRef.child(uid).setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(RegistrationActivity.this, "registration successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegistrationActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        finish();
                                    }
                                });

                            }
                            else{
                                Toast.makeText(RegistrationActivity.this, "registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }
}
