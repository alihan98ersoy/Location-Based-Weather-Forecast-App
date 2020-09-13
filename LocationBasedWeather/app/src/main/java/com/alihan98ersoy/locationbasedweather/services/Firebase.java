package com.alihan98ersoy.locationbasedweather.services;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Firebase extends FirebaseAuth {


    private FirebaseAnalytics firebaseAnalytics;
    private Context context;

    public Firebase(FirebaseApp firebaseApp,Context context) {
        super(firebaseApp);
        this.context = context;
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Nullable
    @Override
    public FirebaseUser getCurrentUser() {
        return super.getCurrentUser();
    }

    @NonNull
    @Override
    public Task<AuthResult> signInWithEmailAndPassword(@NonNull String s, @NonNull String s1) {
        return super.signInWithEmailAndPassword(s, s1);
    }

    @NonNull
    @Override
    public Task<AuthResult> createUserWithEmailAndPassword(@NonNull String s, @NonNull String s1) {
        return super.createUserWithEmailAndPassword(s, s1);
    }

    @Override
    public void signOut() {
        super.signOut();
    }

    public void UseAnalytic(String eventname,String[][]parameternameandparameter,FirebaseAnalytics mFirebaseAnalytics)
    {
        Bundle params = new Bundle();
        String txt="";



        for(int i=0;i<parameternameandparameter.length;i++)
        {

            txt=parameternameandparameter[i][0]+"!^!"+parameternameandparameter[i][1];
            System.out.println("!! Firebase UseAnalytic params: "+txt);
            params.putString(parameternameandparameter[i][0], parameternameandparameter[i][1]);
        }
        System.out.println("!! firebase UseAnalytic params: "+params.toString());

        mFirebaseAnalytics.logEvent(eventname, params);
        System.out.println("!! firebase UseAnalytic  firebaseAnalytics.getAppInstanceId():  "+firebaseAnalytics.getAppInstanceId());

    }


}
