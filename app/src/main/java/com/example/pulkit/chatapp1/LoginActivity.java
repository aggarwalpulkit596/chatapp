package com.example.pulkit.chatapp1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class LoginActivity extends AppCompatActivity {

    private EditText mLoginEmail, mLoginPassword;
    private Button mLoginBtn;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private DatabaseReference mUserDatabase;
    private ProgressDialog mLoginProcess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginBtn = findViewById(R.id.login_btn);
        mLoginEmail = findViewById(R.id.login_email);
        mLoginPassword = findViewById(R.id.login_password);


        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mLoginProcess = new ProgressDialog(this);

        mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mLoginEmail.getText().toString();
                String password = mLoginPassword.getText().toString();

                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {

                    mLoginProcess.setTitle("Logging In");
                    mLoginProcess.setMessage("PLease Wait...");
                    mLoginProcess.setCanceledOnTouchOutside(false);
                    mLoginProcess.show();
                    loginUser(email, password);
                }

            }
        });
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mLoginProcess.dismiss();

                    String device_token = FirebaseInstanceId.getInstance().getToken();
                    String current_uid = mAuth.getCurrentUser().getUid();
                    mUserDatabase.child(current_uid).child("device_token").setValue(device_token).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        }
                    });

                } else {
                    // If sign in fails, display a message to the user.
                    mLoginProcess.hide();

                    String task_result = task.getException().getMessage();


                    Toast.makeText(LoginActivity.this, "Error : " + task_result, Toast.LENGTH_LONG).show();

                }
            }
        });

    }
}
