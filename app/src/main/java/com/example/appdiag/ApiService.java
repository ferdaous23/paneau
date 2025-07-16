package com.example.appdiag;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import java.lang.Void;
public interface ApiService {
    @GET("api/intersections/autocomplete")
    Call<List<String>> getSuggestions(@Query("query") String input);

    @GET("api/intersections/departs")
    Call<List<String>> getDeparts(@Query("rue") String rue);


    @Multipart
    @POST("api/panneaux/{id}/ajouter")
    Call<ResponseBody> envoyerInfosPanneau(
            @Path("id") Long id,
            @Part("remarque") RequestBody remarque,
            @Part List<MultipartBody.Part> etats,
            @Part List<MultipartBody.Part> photos
    );
}