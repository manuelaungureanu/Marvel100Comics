/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chefless.ela.marvel100comics.data.source.remote;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.chefless.ela.marvel100comics.data.models.Comic;
import com.chefless.ela.marvel100comics.data.source.ComicsDataSource;
import com.chefless.ela.marvel100comics.utils.NetworkUtils;
import com.google.common.collect.Lists;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the data source that adds a latency simulating network.
 */
public class ComicsRemoteDataSource implements ComicsDataSource {

    private static ComicsRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private final static Map<String, Comic> COMICS_SERVICE_DATA;

    static {
        COMICS_SERVICE_DATA = new LinkedHashMap<>(2);
//        addComic("Build tower in Pisa", "Ground looks good, no foundation work required.");
//        addComic("Finish bridge in Tacoma", "Found awesome girders at half the cost!");
    }

    public static ComicsRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComicsRemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private ComicsRemoteDataSource() {}

    @Override
    public List<Comic> getComics() {

        try {
            URL comicsUrl = new URL(NetworkUtils.STATIC_COMICS_BASE_URL + NetworkUtils.STATIC_AUTH_PARAMS + NetworkUtils.COMICS_LIMIT_PARAM);
            String response = NetworkUtils.getResponseFromHttpUrl(comicsUrl);
            JSONObject jsonRootObject = new JSONObject(response).getJSONObject(NetworkUtils.STATIC_COMICS_RESPONSE_ROOT_NAME);
            String comicsJsonAsString = jsonRootObject.getString(NetworkUtils.STATIC_COMICS_RESPONSE_RESULTS_NODE);

            Comic[] arrayResult =  NetworkUtils.parseJSONToComics(comicsJsonAsString);

            if(arrayResult==null || arrayResult.length==0)
                return new ArrayList<>();

            return new ArrayList<>(Arrays.asList(arrayResult));

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    public Comic getComic(@NonNull String comicId) {
        if(TextUtils.isEmpty(comicId))
            return null;
        try {
            String url = String.format("%s/%s/%s", NetworkUtils.STATIC_COMICS_BASE_URL, comicId, NetworkUtils.STATIC_AUTH_PARAMS);
            URL comicUrl = new URL(url);
            String response = NetworkUtils.getResponseFromHttpUrl(comicUrl);
            JSONObject jsonRootObject = new JSONObject(response).getJSONObject(NetworkUtils.STATIC_COMICS_RESPONSE_ROOT_NAME);
            String comicsJsonAsString = jsonRootObject.getString(NetworkUtils.STATIC_COMICS_RESPONSE_RESULTS_NODE);

            Comic[] arrayResult =  NetworkUtils.parseJSONToComics(comicsJsonAsString);

            if(arrayResult==null || arrayResult.length==0)
                return null;

            return arrayResult[0];

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void refreshComics() {
        // Not required because the {@link ComicsRepository} handles the logic of refreshing the
        // Comics from all the available data sources.
    }
}
