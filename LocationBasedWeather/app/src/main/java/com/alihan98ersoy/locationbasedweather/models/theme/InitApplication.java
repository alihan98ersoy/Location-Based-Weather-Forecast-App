package com.alihan98ersoy.locationbasedweather.models.theme;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


public class InitApplication extends Application {
    public static final String NIGHT_MODE = "NIGHT_MODE";
    private boolean isNightModeEnabled = false;
    private Context context;
    SharedPreferences mPrefs;
    public InitApplication(Context context)
    {
        this.context = context;
        mPrefs = context.getSharedPreferences("sharedpref",Context.MODE_PRIVATE);
        this.isNightModeEnabled = mPrefs.getBoolean(NIGHT_MODE, false);
        System.out.println("!! InitApplication constructor isNightModeEnabled: "+isNightModeEnabled);
    }


    public boolean isNightModeEnabled() {
        return isNightModeEnabled;
    }

    public void setIsNightModeEnabled(boolean isNightModeEnabled) {
        this.isNightModeEnabled = isNightModeEnabled;
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(NIGHT_MODE, isNightModeEnabled);
        editor.apply();
    }
}
