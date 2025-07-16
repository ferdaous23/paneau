package com.example.appdiag;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/intersections/autocomplete")
    Call<List<String>> getSuggestions(@Query("query") String input);
}