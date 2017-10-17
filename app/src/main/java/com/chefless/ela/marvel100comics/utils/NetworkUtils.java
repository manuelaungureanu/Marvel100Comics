package com.chefless.ela.marvel100comics.utils;

import android.text.TextUtils;

import com.chefless.ela.marvel100comics.data.models.Comic;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by ela on 26/09/2017.
 */

public class NetworkUtils {

    public static final String STATIC_COMICS_BASE_URL = "https://gateway.marvel.com:443/v1/public/comics";
    public static final String STATIC_AUTH_PARAMS = "?ts=1&hash=359e14db6b6a7bed5c31d81b2c00f36b&apikey=54306733de0f5cd1418aa05a85fa062a";
    public static final String COMICS_LIMIT_PARAM = "&limit=100";
    public static final String STATIC_COMICS_RESPONSE_ROOT_NAME = "data";
    public static final String STATIC_COMICS_RESPONSE_RESULTS_NODE = "results";
    public static final String JSON_EXTENSION = ".json";

    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static Comic[] parseJSONToComics(String response) {
        if(response==null || TextUtils.isEmpty(response))
            return null;

        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, Comic[].class);
    }

    public static Comic parseJSONToComic(String response) {
        if(response==null || TextUtils.isEmpty(response))
            return null;

        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, Comic.class);
    }
}
