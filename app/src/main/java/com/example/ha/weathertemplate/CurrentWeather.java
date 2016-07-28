package com.example.ha.weathertemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Ha on 7/26/2016.
 */
public class CurrentWeather {

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {

        mTime = time;
    }

    public int getTemperature() {
        return (int) Math.round(mTemperature);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public String getHumidity() {
        return mHumidity;
    }

    public void setHumidity(String humidity) {
        mHumidity = humidity;
    }

    public String getFeelsLike() {
        return mFeelsLike;
    }

    public void setFeelsLike(String feelsLike) {
        mFeelsLike = feelsLike;
    }

    public int getFormattedFeelsLike() {
        double FormattedTemp = Double.parseDouble(getFeelsLike());
        return (int) Math.round(FormattedTemp);
    }


    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }


    public String getFormattedTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));

        int tempcon = Integer.parseInt(getTime());
        Date dateTime = new Date(tempcon * 1000);
        String timeString = formatter.format(dateTime);

        return timeString;

    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    private String mTime;
    private double mTemperature;
    private String mHumidity;
    private String mFeelsLike;
    private String mSummary;
    private String mTimeZone;
    private String mLocation;
    private String mIcon;

}
