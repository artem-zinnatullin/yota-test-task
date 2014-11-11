package com.artemzin.android.yotatask.api;

import android.support.annotation.NonNull;

import com.artemzin.android.yotatask.api.response.ItemsResponse;

import retrofit.http.GET;
import retrofit.http.Path;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public interface YotaTaskApi {

    @GET("/s/{file}?dl=1")
    @NonNull ItemsResponse getItems(@NonNull @Path("file") String file);
}
