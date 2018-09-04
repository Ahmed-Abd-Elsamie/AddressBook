package com.example.root.addressbook.user;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.root.addressbook.MainActivity;
import com.example.root.addressbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {


    private Button registerBtn ;
    private EditText txtName;
    private EditText txtPassword;
    private EditText txtEmail;
    private EditText txtRePassword;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private Button btnLoginPage;
    private ProgressBar progressBar;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        // Assign Views
        txtName = (EditText) findViewById(R.id.txt_user_name_register);
        txtPassword = (EditText) findViewById(R.id.txt_user_password_register);
        txtEmail = (EditText) findViewById(R.id.txt_user_email_register);
        txtRePassword = (EditText) findViewById(R.id.txt_user_re_password_register);
        registerBtn = (Button) findViewById(R.id.registerButton);
        btnLoginPage = (Button) findViewById(R.id.login_page);
        progressBar = (ProgressBar) findViewById(R.id.reg_progress);


        // init firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");



        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddNewUser();

            }
        });

        btnLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Register.this , Login.class));
                // start sliding Animation
                overridePendingTransition(R.anim.slide_in_right , R.anim.slide_out_right);
                finish();

            }
        });



    }

    private void AddNewUser(){


        String name = txtName.getText().toString();
        final String email = txtEmail.getText().toString().trim();
        final String pass = txtPassword.getText().toString().trim();
        String re_pass = txtRePassword.getText().toString().trim();


        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Enter your Name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(re_pass)) {
            Toast.makeText(getApplicationContext(), "Re Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }


        if (pass.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(re_pass)) {
            Toast.makeText(getApplicationContext(), "Please enter the Same Password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progressbar
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Hide progressbar
                progressBar.setVisibility(View.INVISIBLE);

                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Failed Please Check your Data!", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.signInWithEmailAndPassword(email,pass);

                    // Create user in database
                    AddUser();

                    uid = mAuth.getCurrentUser().getUid().toString();

                    mAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                        @Override
                        public void onSuccess(GetTokenResult getTokenResult) {


                            String tokenId = getTokenResult.getToken();
                            databaseReference.child(uid).child("token_id").setValue(tokenId).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    startActivity(new Intent(Register.this, MainActivity.class));
                                    // Start sliding Animation
                                    overridePendingTransition(R.anim.slide_in_right , R.anim.slide_out_right);
                                    finish();

                                }
                            });

                        }
                    });

                }
            }
        });

    }




    public void AddUser(){


        String name = txtName.getText().toString();
        String email = txtEmail.getText().toString().trim();
        String UID = mAuth.getCurrentUser().getUid().toString();
        databaseReference.child(UID).child("name").setValue(name);
        databaseReference.child(UID).child("email").setValue(email);
        databaseReference.child(UID).child("id").setValue(UID);
        databaseReference.child(UID).child("img").setValue("default");


    }
}
