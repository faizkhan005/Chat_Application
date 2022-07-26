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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private TextInputEditText editTextEmailSignUp , editTextPasswordSignUp , editTextUserName ;
    private Button buttonRegister;

    boolean imageControl = false;

    Uri imageUri;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmailSignUp = findViewById(R.id.editTextEmailSIgnUp);
        editTextPasswordSignUp =  findViewById(R.id.editTextPasswordSignUP);
        editTextUserName =  findViewById(R.id.editTextUserNameSIgnUp);
        circleImageView =  findViewById(R.id.circleImageView);
        buttonRegister =  findViewById(R.id.buttonRegister);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://chatapplication-7858d-default-rtdb.asia-southeast1.firebasedatabase.app");
        reference = database.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmailSignUp.getText().toString();
                String password = editTextPasswordSignUp.getText().toString();
                String userName = editTextUserName.getText().toString();

                if(!email.equals("") && !password.equals("") && !userName.equals("")){
                    signup(email,password,userName);
                }
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
            Picasso.get().load(imageUri).into(circleImageView);
            imageControl = true;
        }else{
            imageControl = false;
        }
    }

    public void signup(String email,String password, String userName){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    reference.child("Users").child(auth.getUid()).child("userName").setValue(userName);

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
                                        reference.child("Users").child(auth.getUid()).child("image").setValue(filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(SignUpActivity.this,"Write to database is successful",Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignUpActivity.this,"Write to database is unsuccessful",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                    else{
                        reference.child("Users").child(auth.getUid()).child("image").setValue("null");
                    }

                    Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(SignUpActivity.this,"There is a problem creating account.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}