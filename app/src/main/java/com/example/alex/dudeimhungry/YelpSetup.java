package com.example.alex.dudeimhungry;

import android.location.Location;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;
import com.yelp.clientlib.entities.Business;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.lang.*;

import retrofit.Call;
import retrofit.Response;
import retrofit.Callback;
import retrofit.Retrofit;

import com.example.alex.dudeimhungry.LaunchActivity;

/**
 * Created by pointatnick on 3/1/16.
 */
public class YelpSetup {
    // API keys
    String CONSUMER_KEY = "COmG92hPzvo9Jo3Y-wtvag";
    String CONSUMER_SECRET = "CF_mB0o-CYb1orOtjgx-ZdSTCaM";
    String TOKEN = "ofVcS6smNc6Is42h2YBrfbOGP7AevBbi";
    String TOKEN_SECRET = "fW5zIji0LEMutS4JE2hI7AuMTXg";

    // Restaurant field to pass to LaunchActivity
    static String businessName = "";
    static double rating = 0.0;
    static double busDist = 0.0;
    static double busLat = 0.0;
    static double busLong = 0.0;
    static double myLat;
    static double myLong;
    static int totalResults = 0;

    static ArrayList<Business> businesses;
    private static YelpAPI yelpAPI;

    //constructor
    public YelpSetup() {
        setUp();
    }

    public void setUp() {
        // Creating the API object from YelpAPI library
        YelpAPIFactory apiFactory = new YelpAPIFactory(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
        yelpAPI = apiFactory.createAPI();
    }

    public void searchByCoordinate() {
        myLat = LaunchActivity.getUserLat();
        myLong = LaunchActivity.getUserLong();
        // Creating the map to call from
        Map<String, String> params = new HashMap<>();
        // Search in a 5-mile radius
        params.put("radius-filter", "3000");
        Call<SearchResponse> call = yelpAPI.search("Los Angeles", params);
        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                SearchResponse searchResponse = response.body();
                int busNum = LaunchActivity.hitCount;
                totalResults = searchResponse.total();
                businesses = searchResponse.businesses();
                businessInfo(busNum);
            }
            @Override
            public void onFailure(Throwable t) {
                System.err.println("HTTP error: " + t.getMessage());
            }
        };
        call.enqueue(callback);
    }

    public static void businessInfo(int busNum) {
        // Get name, rating, distance
        // Display one-by-one in order that Yelp returns data to us
        if(busNum > totalResults) {
            busNum %= totalResults; //quick wrap around function
        }
        businessName = businesses.get(busNum).name();
        rating = businesses.get(busNum).rating();
        busLat = businesses.get(busNum).location().coordinate().latitude();
        busLong = businesses.get(busNum).location().coordinate().longitude();
        busDist = distance(myLat, myLong, busLat, busLong);
    }

    public double getBusLong() {
        return busLong;
    }
    public double getBusLat() {
        return busLat;
    }

    // Calculates distance between two coordinates in miles
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) +
                      Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
