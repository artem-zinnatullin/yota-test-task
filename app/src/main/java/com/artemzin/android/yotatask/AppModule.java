package com.artemzin.android.yotatask;

import android.content.Context;
import android.support.annotation.NonNull;

import com.artemzin.android.yotatask.api.ApiModule;
import com.artemzin.android.yotatask.model.ActiveModelModule;
import com.artemzin.android.yotatask.model.ItemsActiveModel;
import com.artemzin.android.yotatask.ui.fragment.ItemsListFragment;

import dagger.Module;
import dagger.Provides;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
@Module(
        includes = {
            ApiModule.class,
            ActiveModelModule.class
        },
        injects = {
            ItemsActiveModel.class,
            ItemsListFragment.class
        }
)
public class AppModule {

    @NonNull private final YotaTaskApp mYotaTaskApp;

    public AppModule(@NonNull YotaTaskApp yotaTaskApp) {
        mYotaTaskApp = yotaTaskApp;
    }

    @Provides @NonNull public Context provideApplicationContext() {
        return mYotaTaskApp;
    }
}
