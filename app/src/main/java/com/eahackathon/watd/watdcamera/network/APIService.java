package com.eahackathon.watd.watdcamera.network;

import android.provider.SyncStateContract;
import android.util.Log;

import com.eahackathon.watd.watdcamera.Constants;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by KienDu on 1/9/2016.
 */
public class APIService {
    private static WaTDAPI instance;

    public static WaTDAPI getInstance() {
        if (instance == null) {
            Log.e("instance", "new instance");
            Retrofit restAdapter = new Retrofit.Builder().baseUrl(Constants.API_ENDPOINT).addConverterFactory(JacksonConverterFactory.create()).build();
            instance = restAdapter.create(WaTDAPI.class);
        } else {
            Log.e("instance", "old instance");
        }
        return instance;
    }

    public static void setInstance(WaTDAPI apiInstance) {
        Log.e("instance: ",String.valueOf(apiInstance));
        instance = apiInstance;
    }
}
