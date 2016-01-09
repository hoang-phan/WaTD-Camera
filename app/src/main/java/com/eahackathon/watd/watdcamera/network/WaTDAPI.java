package com.eahackathon.watd.watdcamera.network;

import com.eahackathon.watd.watdcamera.models.ResponseModel;
import com.squareup.okhttp.RequestBody;

import retrofit.Call;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;

/**
 * Created by KienDu on 1/9/2016.
 */
public interface WaTDAPI {
    @Multipart
    @POST("/images")
    Call<ResponseModel> uploadImage(@Part("image[data]") RequestBody data);
}
