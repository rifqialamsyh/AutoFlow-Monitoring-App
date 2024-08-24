package com.example.autoflow.network;

import com.example.autoflow.model.DataModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("data")
    Call<List<DataModel>> getData();
    @DELETE("data/{id}")
    Call<Void> deleteData(@Path("id") String id);
}