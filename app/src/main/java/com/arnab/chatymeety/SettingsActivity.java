package com.arnab.chatymeety;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView mCircleImageView;
    private TextView mName,mStatus;
    private Button mImageChange,mStatusChange;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private StorageReference mStorageRef;
    private DatabaseReference dataRefForOnline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mCircleImageView=findViewById(R.id.settings_image);
        mName=findViewById(R.id.settings_name);
        mStatus=findViewById(R.id.settings_status);
        mImageChange=findViewById(R.id.settings_image_cng);
        mStatusChange=findViewById(R.id.settings_status_cng);
        mAuth=FirebaseAuth.getInstance();
        mRef= FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //------for online check-------//
        dataRefForOnline = FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid());


        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String imageLink=dataSnapshot.child("imageLink").getValue().toString();
                String thumbnail=dataSnapshot.child("thumbnail").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
                if(imageLink.equals("default")){
                    mCircleImageView.setImageResource(R.drawable.defaultpic);
                }
                else Picasso.get().load(imageLink).into(mCircleImageView);

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
                File resultImage=new File(resultUri.getPath());


                byte[] byteImageMain = imageFileToByte(resultImage,400,400,50);
                final byte[] byteImageThumb = imageFileToByte(resultImage,100,100,50);


                //upload to fireBase
                final StorageReference filePath= mStorageRef.child("user").child(mAuth.getCurrentUser().getUid()+".jpg");
                final StorageReference thumbFilePath= mStorageRef.child("user").child("thumbnails").child(mAuth.getCurrentUser().getUid()+".jpg");

                //main image uploading part
                filePath.putBytes(byteImageMain).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                            final String imageLink=task.getResult().toString();

                            ////now thumbnail/////////////////

                            thumbFilePath.putBytes(byteImageThumb).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return thumbFilePath.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()){
                                        final String thumbnailLink=task.getResult().toString();

                                        //update links & images/////////////////
                                        HashMap<String,Object> tmp=new HashMap<>();
                                        tmp.put("imageLink",imageLink);
                                        tmp.put("thumbnail",thumbnailLink);
                                        mRef.updateChildren(tmp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                    //dismiss progressbar

                                                    Picasso.get().load(imageLink).into(mCircleImageView);
                                                    Toast.makeText(SettingsActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        Toast.makeText(SettingsActivity.this, "Uploading", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(SettingsActivity.this, "Uploading Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            //////////////////////////////////
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

    public byte[] imageFileToByte(File image,int maxWidth,int maxHeight,int quality){
        //compression to bitmap
        Bitmap bitmapImage=null;
        try {
            bitmapImage = new Compressor(this)
                    .setMaxWidth(maxWidth)
                    .setMaxHeight(maxHeight).setQuality(quality).compressToBitmap(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteImage = baos.toByteArray();

        return byteImage;
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataRefForOnline.child("online").setValue(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataRefForOnline.child("online").setValue(false);
    }
}
