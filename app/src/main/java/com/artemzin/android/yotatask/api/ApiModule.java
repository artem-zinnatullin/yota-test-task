package com.artemzin.android.yotatask.api;

import android.support.annotation.NonNull;

import com.artemzin.android.yotatask.BuildConfig;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
@Module(library = true)
public class ApiModule {

    @Provides @NonNull public YotaTaskApi provideYotaTaskApi() {
        return new RestAdapter.Builder()
                .setEndpoint("https://www.dropbox.com")
                .setLog(new AndroidLog("YotaTask"))
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build()
                .create(YotaTaskApi.class);
    }
}
