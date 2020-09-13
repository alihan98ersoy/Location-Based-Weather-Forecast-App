package com.alihan98ersoy.locationbasedweather.services;


public class CurrentWeather {
    final String location;
    final int conditionId;
    final String weatherCondition;
    final double tempKelvin;

    public CurrentWeather(final String location,
                          final int conditionId,
                          final String weatherCondition,
                          final double tempKelvin) {
        this.location = location;
        this.conditionId = conditionId;
        this.weatherCondition = weatherCondition;
        this.tempKelvin = tempKelvin;
    }

    public int getTempFahrenheit() {
        return (int) (tempKelvin * 9/5 - 459.67);
    }

    public String getLocation() {
        return location;
    }

    public int getConditionId() {
        return conditionId;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public double getTempKelvin() {
        return tempKelvin;
    }

    public double getCelcius(){
        return tempKelvin-273.15;
    }

    @Override
    public String toString() {
        return "CurrentWeather{" +
                "location='" + location + '\'' +
                ", conditionId=" + conditionId +
                ", weatherCondition='" + weatherCondition + '\'' +
                ", tempKelvin=" + tempKelvin +
                '}';
    }
}
