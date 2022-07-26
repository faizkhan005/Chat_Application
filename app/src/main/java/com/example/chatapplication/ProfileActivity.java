package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView circleImageViewProfile;
    Button buttonUpdate;
    TextInputEditText editTextuserNameProfile;

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    Uri imageUri;
    boolean imageControl = false;

    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        circleImageViewProfile = findViewById(R.id.circleImageViewProfile);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        editTextuserNameProfile = findViewById(R.id.userNameProfile);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://chatapplication-7858d-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference = database.getReference();
        user = auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        getUserInfo();
        circleImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });
    }
    public void updateInfo(){
        String userName = editTextuserNameProfile.getText().toString();

        databaseReference.child("Users").child(user.getUid()).child("userName").setValue(userName);

        if(imageControl){
            UUID randomID = UUID.randomUUID();
            final String imageName = "images/"+randomID+".jpg";
            storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference myStorageRef = firebaseStorage.getReference(imageName);
                    myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String filePath = uri.toString();
                            databaseReference.child("Users").child(auth.getUid()).child("image").setValue(filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ProfileActivity.this,"Write to database is successful",Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ProfileActivity.this,"Write to database is unsuccessful",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
            });
        }
        else{
            databaseReference.child("Users").child(auth.getUid()).child("image").setValue(image);
        }

        Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void getUserInfo(){
        databaseReference.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String name = snapshot.child("userName").getValue().toString();
                image = snapshot.child("image").getValue().toString();

                editTextuserNameProfile.setText(name);

                if(image.equals("null")){
                    circleImageViewProfile.setImageResource(R.drawable.profile);
                }
                else{
                    Picasso.get().load(image).into(circleImageViewProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(circleImageViewProfile);
            imageControl = true;
        }else{
            imageControl = false;
        }
    }

}