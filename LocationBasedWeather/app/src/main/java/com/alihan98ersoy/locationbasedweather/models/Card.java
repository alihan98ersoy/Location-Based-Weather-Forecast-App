package com.alihan98ersoy.locationbasedweather.models;


public class Card {

    private int icon,celcius;
    private String weathercondision,city;

    public Card(int icon, String weathercondision,Double celcius , String city) {
        this.icon = icon;
        this.celcius = celcius.intValue();
        this.weathercondision = weathercondision;
        this.city = city;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getCelcius() {
        return celcius;
    }

    public void setCelcius(int celcius) {
        this.celcius = celcius;
    }

    public String getWeathercondision() {
        return weathercondision;
    }

    public void setWeathercondision(String weathercondision) {
        this.weathercondision = weathercondision;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}