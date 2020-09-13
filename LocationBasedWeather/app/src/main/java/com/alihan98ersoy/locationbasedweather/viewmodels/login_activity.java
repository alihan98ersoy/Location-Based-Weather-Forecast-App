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

public class login_activity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;

    EditText mEmailEt,mPasswordEt;
    TextView notHaveAccountTv;
    Button mloginButton;

    private Firebase firebase = new Firebase(FirebaseApp.getInstance(),this);
    private FirebaseAnalytics mFirebaseAnalytics;
    ProgressDialog pd;
    private InitApplication init;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init = new InitApplication(this);
        if (init.isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_login_activity);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);



        if(firebase.getCurrentUser() != null){
            String[][] array = {{"Uid",firebase.getCurrentUser().getUid()},{"Email",firebase.getCurrentUser().getEmail()}};
            firebase.UseAnalytic("WelcomeBack",array,mFirebaseAnalytics);
            startActivity(new Intent(login_activity.this,homepage_activity.class));
        }

        mEmailEt = (EditText) findViewById(R.id.EmailEt);
        mPasswordEt = (EditText) findViewById(R.id.PasswordEt);
        notHaveAccountTv= (TextView) findViewById(R.id.nothave_accountTv);
        mloginButton = (Button) findViewById(R.id.loginButton);

        mloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmailEt.getText().toString();
                String passw = mPasswordEt.getText().toString().trim();

                if(! Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);

                }else{

                    loginUser(email,passw);

                }

            }
        });

        notHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(login_activity.this, register_activity.class));
                finish();

            }
        });



        pd = new ProgressDialog(this);

    }


    private void loginUser(String email, String passw) {

        pd.setMessage("Logging In...");
        pd.show();

        firebase.signInWithEmailAndPassword(email, passw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            // Sign in success, update UI with the signed-in user's information

                            String[][] array = {{"Uid",firebase.getCurrentUser().getUid()},{"Email",firebase.getCurrentUser().getEmail()}};
                            firebase.UseAnalytic("Login",array,mFirebaseAnalytics);

                            startActivity(new Intent(login_activity.this, homepage_activity.class));
                            finish();
                        } else {
                            pd.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(login_activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(login_activity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }

}