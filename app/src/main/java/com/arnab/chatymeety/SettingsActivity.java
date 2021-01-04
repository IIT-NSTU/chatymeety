package com.arnab.chatymeety;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView mCircleImageView;
    private TextView mName,mStatus;
    private Button mImageChange,mStatusChange;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mCircleImageView=findViewById(R.id.user_image);
        mName=findViewById(R.id.settings_name);
        mStatus=findViewById(R.id.settings_status);
        mImageChange=findViewById(R.id.settings_image_cng);
        mStatusChange=findViewById(R.id.settings_status_cng);
        mAuth=FirebaseAuth.getInstance();
        mRef= FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String imageLink=dataSnapshot.child("imageLink").getValue().toString();
                String thumbnail=dataSnapshot.child("thumbnail").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
                Picasso.get().load(imageLink).into(mCircleImageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mStatusChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),StatusActivity.class).putExtra("status",mStatus.getText().toString()));

            }
        });

        mImageChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery=new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery,"SELECT IMAGE"),1);
            }
        });







    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            Uri imageUri=data.getData();
            CropImage.activity(imageUri).setAspectRatio(1,1)
                    .start(this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){

                //progress bar start

                Uri resultUri=result.getUri();
                Log.d("de","comi");
                final StorageReference filePath= mStorageRef.child("user").child(mAuth.getCurrentUser().getUid()+".jpg");
                filePath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            //update image
                            final String imageLink=task.getResult().toString();
                            mRef.child("imageLink").setValue(imageLink).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        //dismiss progressbar
                                        Picasso.get().load(imageLink).into(mCircleImageView);
                                    }
                                }
                            });
                            Log.d("de","com");
                            Toast.makeText(SettingsActivity.this, "Uploading", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(SettingsActivity.this, "Uploading failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error=result.getError();
            }
        }
    }
}
