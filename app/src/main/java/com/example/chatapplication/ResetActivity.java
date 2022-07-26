package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {
    private Button buttonReset;
    private TextInputEditText editTextReset;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        buttonReset = (Button) findViewById(R.id.buttonReset);
        editTextReset = (TextInputEditText) findViewById(R.id.editTextReset);

        auth = FirebaseAuth.getInstance();

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextReset.getText().toString();
                if(!email.equals(""))
                    passwordReset(email);
            }
        });

    }

    public void passwordReset(String email){
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(ResetActivity.this,"Please check your email. ",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(ResetActivity.this,"Wrong Email address. ",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}