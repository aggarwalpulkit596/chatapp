package com.example.pulkit.chatapp1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;



    private CircleImageView mImage;
    private TextView mName;
    private TextView mStatus;
    private Button mImageBtn, mStatusBtn;

    private StorageReference mStorageRef;
    private DatabaseReference mUserRef;


    private ProgressDialog mProgessDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mImage = findViewById(R.id.settings_image);
        mName = findViewById(R.id.settings_displayname);
        mStatus = findViewById(R.id.settings_status);
        mImageBtn = findViewById(R.id.setting_image_btn);
        mStatusBtn = findViewById(R.id.setting_status_btn);


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();


        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot documentSnapshot) {
                String name = documentSnapshot.child("name").getValue().toString();
                final String image = documentSnapshot.child("image").getValue().toString();
                String thumb_image = documentSnapshot.child("thumb_image").getValue().toString();
                String status = documentSnapshot.child("status").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);

                if (!image.equals("default"))
                    Picasso.with(SettingsActivity.this)
                            .load(image)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar)
                            .into(mImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {

                                    if (!image.equals("default"))
                                        Picasso.with(SettingsActivity.this)
                                                .load(image)
                                                .placeholder(R.drawable.default_avatar)
                                                .into(mImage);

                                }
                            });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status_value = mStatus.getText().toString();

                Intent status_intent = new Intent(SettingsActivity.this, StatusActivity.class);
                status_intent.putExtra("status_value", status_value);
                startActivity(status_intent);

            }
        });


        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropWindowSize(500, 500)
                        .start(SettingsActivity.this);

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgessDialog = new ProgressDialog(this);
                mProgessDialog.setMessage("Please wait while we upload the image");
                mProgessDialog.setTitle("Uploading Image...");
                mProgessDialog.setCanceledOnTouchOutside(true);
                mProgessDialog.show();


                Uri resultUri = result.getUri();

                File thumb_file = new File(resultUri.getPath());

                final String current_userid = mCurrentUser.getUid();

                Bitmap thumb_bitmap = null;
                final byte[] thumb_byte;

                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_file);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                thumb_byte = baos.toByteArray();

                StorageReference filepath = mStorageRef.child("profile_images").child(current_userid + ".jpg");
                final StorageReference thumb_filepath = mStorageRef.child("profile_images").child("thumbs").child(current_userid + ".jpg");


                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            final String download_url = task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    if (thumb_task.isSuccessful()) {

                                        String thumb_downloadurl = thumb_task.getResult().getDownloadUrl().toString();

                                        Map<String, Object> imageData = new HashMap<>();
                                        imageData.put("image", download_url);
                                        imageData.put("thumb_image", thumb_downloadurl);

                                        mUserDatabase.updateChildren(imageData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    mProgessDialog.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                                    } else {
                                        mProgessDialog.dismiss();
                                        Toast.makeText(SettingsActivity.this, "Try Again Later", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                        } else {
                            mProgessDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Try Again Later", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    

    @Override
    protected void onStart() {
        super.onStart();

        if (mCurrentUser != null) {

            mUserRef.child("online").setValue("true");
        }    }


}
