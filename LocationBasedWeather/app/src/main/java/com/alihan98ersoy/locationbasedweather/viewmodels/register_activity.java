package com.alihan98ersoy.locationbasedweather.viewmodels;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alihan98ersoy.locationbasedweather.R;
import com.alihan98ersoy.locationbasedweather.models.theme.InitApplication;
import com.alihan98ersoy.locationbasedweather.services.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class register_activity extends AppCompatActivity {

    //private FirebaseAuth mAuth;
    EditText mEmailEt,mPasswordEt;
    Button mRegisterButton;
    TextView mHaveAccountTv;
    ProgressDialog progressDialog;
    private InitApplication init;
    private Firebase firebase = new Firebase(FirebaseApp.getInstance(),this);
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init = new InitApplication(this);
        if (init.isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_register_activity);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        ActionBar actionBar = getSupportActionBar();

        mEmailEt = (EditText) findViewById(R.id.EmailEt);
        mPasswordEt = (EditText) findViewById(R.id.PasswordEt);
        mRegisterButton = (Button) findViewById(R.id.register);
        mHaveAccountTv = (TextView) findViewById(R.id.have_accountTv);

        mHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(register_activity.this,login_activity.class));
                finish();

            }
        });



        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmailEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();

                if(! Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {

                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);

                }
                else if (password.length() < 6)
                {

                    mPasswordEt.setError("Password length at least 6 characters");
                    mPasswordEt.setFocusable(true);

                }else
                {

                    registerUser(email,password);

                }

            }
        });

    }

    private void registerUser(final String email, final String password) {

        progressDialog.show();

        firebase.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information




                            String[][] array = {{"Email",email}};
                            firebase.UseAnalytic("Register",array,mFirebaseAnalytics);

                            startActivity(new Intent(register_activity.this, homepage_activity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(register_activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(register_activity.this, ""+e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    public boolean onSupportNavigateUp(){

        onBackPressed();
        return super.onSupportNavigateUp();

    }
}