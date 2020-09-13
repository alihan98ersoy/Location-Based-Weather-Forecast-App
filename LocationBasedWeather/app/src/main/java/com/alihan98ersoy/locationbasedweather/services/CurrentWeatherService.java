package com.alihan98ersoy.locationbasedweather.services;


import android.app.Activity;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Not an actual {@link android.app.Service} because we are using {@link Volley}
 */
public class CurrentWeatherService {

    private static final String TAG = CurrentWeatherService.class.getSimpleName();
    //api.openweathermap.org/data/2.5/forecast?q={city name}&appid={your api key}
    private static final String URL = "https://api.openweathermap.org/data/2.5/forecast";//"https://api.openweathermap.org/data/2.5/weather";
    private static final String CURRENT_WEATHER_TAG = "CURRENT_WEATHER";
    private static final String API_KEY = "1bd9703b648b0af82bff18a9e4d8b28f";

    private RequestQueue queue;

    public CurrentWeatherService(@NonNull final Activity activity) {
        queue = Volley.newRequestQueue(activity.getApplicationContext());
    }

    public interface CurrentWeatherCallback {
        @MainThread
        void onCurrentWeather(@NonNull final CurrentWeather currentWeather);

        @MainThread
        void onError(@Nullable Exception exception);
    }

    public void getCurrentWeather(@NonNull final String locationNamee, @NonNull final CurrentWeatherCallback callback) {
        final ArrayList<CurrentWeather> currentWeather = new ArrayList<>();
        final String url = String.format("%s?q=%s&appId=%s", URL, locationNamee, API_KEY);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            final JSONObject currentWeatherJSONObject = new JSONObject(response);
                            for(int i=0;i<currentWeatherJSONObject.getJSONArray("list").length();i++) {

                                final JSONArray list = currentWeatherJSONObject.getJSONArray("list");
                                System.out.println("!! Currentweather getCurrentWeather list: "+list);
                                final JSONArray weather = list.getJSONObject(i).getJSONArray("weather");
                                System.out.println("!! Currentweather getCurrentWeather weather: "+weather);
                                final JSONObject weatherCondition = weather.getJSONObject(0);
                                System.out.println("!! Currentweather getCurrentWeather weatherCondition: "+weatherCondition);
                                final String locationName = currentWeatherJSONObject.getJSONArray("list").getJSONObject(i).getString("dt_txt");;

                                final int conditionId = weatherCondition.getInt("id");
                                System.out.println("!! Currentweather getCurrentWeather conditionId: "+conditionId);
                                final String conditionName = weatherCondition.getString("main");
                                System.out.println("!! Currentweather getCurrentWeather conditionName: "+conditionName);
                                final double tempKelvin = currentWeatherJSONObject.getJSONArray("list").getJSONObject(i).getJSONObject("main").getDouble("temp");
                                System.out.println("!! Currentweather getCurrentWeather tempKelvin: "+tempKelvin);
                                currentWeather.add(new CurrentWeather(locationName, conditionId, conditionName, tempKelvin));
                                callback.onCurrentWeather(currentWeather.get(i));
                            }
                        } catch (JSONException e) {
                            System.out.println("!! Currentweather getCurrentWeather JSONException: "+e.toString());
                            callback.onError(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
        stringRequest.setTag(CURRENT_WEATHER_TAG);
        queue.add(stringRequest);
    }
/*
public void getCurrentWeather(@NonNull final String locationNamee, @NonNull final CurrentWeatherCallback callback) {
    final ArrayList<CurrentWeather> currentWeather = new ArrayList<>();
    final String url = String.format("%s?q=%s&month=1&day=1&appId=%s", "https://history.openweathermap.org/data/2.5/aggregated/day", locationNamee, API_KEY);
    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        final JSONObject currentWeatherJSONObject = new JSONObject(response);
                        for(int i=0;i<currentWeatherJSONObject.getJSONArray("list").length();i++) {

                            final JSONArray list = currentWeatherJSONObject.getJSONArray("list");
                            System.out.println("!! Currentweather getCurrentWeather list: "+list);
                            final JSONArray weather = list.getJSONObject(i).getJSONArray("weather");
                            System.out.println("!! Currentweather getCurrentWeather weather: "+weather);
                            final JSONObject weatherCondition = weather.getJSONObject(0);
                            System.out.println("!! Currentweather getCurrentWeather weatherCondition: "+weatherCondition);
                            final String locationName = currentWeatherJSONObject.getJSONArray("list").getJSONObject(i).getString("dt_txt");;

                            final int conditionId = weatherCondition.getInt("id");
                            System.out.println("!! Currentweather getCurrentWeather conditionId: "+conditionId);
                            final String conditionName = weatherCondition.getString("main");
                            System.out.println("!! Currentweather getCurrentWeather conditionName: "+conditionName);
                            final double tempKelvin = currentWeatherJSONObject.getJSONArray("list").getJSONObject(i).getJSONObject("main").getDouble("temp");
                            System.out.println("!! Currentweather getCurrentWeather tempKelvin: "+tempKelvin);
                            currentWeather.add(new CurrentWeather(locationName, conditionId, conditionName, tempKelvin));
                            callback.onCurrentWeather(currentWeather.get(i));
                        }
                    } catch (JSONException e) {
                        System.out.println("!! Currentweather getCurrentWeather JSONException: "+e.toString());
                        callback.onError(e);
                    }
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            callback.onError(error);
        }
    });
    stringRequest.setTag(CURRENT_WEATHER_TAG);
    queue.add(stringRequest);
}*/
    public void cancel() {
        queue.cancelAll(CURRENT_WEATHER_TAG);
    }
}