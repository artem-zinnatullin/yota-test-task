package com.artemzin.android.yotatask;

import android.content.Context;
import android.support.annotation.NonNull;

import com.artemzin.android.yotatask.api.ApiModule;
import com.artemzin.android.yotatask.model.ActiveModelModule;
import com.artemzin.android.yotatask.model.ItemsActiveModel;
import com.artemzin.android.yotatask.storage.SharedPreferencesManager;
import com.artemzin.android.yotatask.storage.StorageModule;
import com.artemzin.android.yotatask.ui.activity.CartActivity;
import com.artemzin.android.yotatask.ui.activity.MainActivity;
import com.artemzin.android.yotatask.ui.adapter.CartItemsAdapter;
import com.artemzin.android.yotatask.ui.adapter.ItemsAdapter;
import com.artemzin.android.yotatask.ui.fragment.CartFragment;
import com.artemzin.android.yotatask.ui.fragment.ItemsListFragment;
import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
@Module(
        includes = {
            ApiModule.class,
            ActiveModelModule.class,
            StorageModule.class
        },
        injects = {
            ItemsActiveModel.class,
            ItemsListFragment.class,
            ItemsAdapter.class,
            MainActivity.class,
            SharedPreferencesManager.class,
            CartFragment.class,
            CartItemsAdapter.class,
            CartActivity.class
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

    @Provides @NonNull @Singleton public Bus provideEventBus() {
        return new Bus(ThreadEnforcer.MAIN);
    }

    @Provides @NonNull public Gson provideGson() {
        return new Gson();
    }
}
