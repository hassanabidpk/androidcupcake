package com.hassanabid.androidcupcake.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hassanabid on 8/5/16.
 */
public interface CupCakeApi {

    @GET("api/v1/cupcakes/")
    Call<CupCakeResponse[]> getCupcakesList(@Query("format") String format);
}
