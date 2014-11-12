package com.artemzin.android.yotatask.storage;

import android.content.Context;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
@Module(library = true, complete = false)
public class StorageModule {

    @Provides @NonNull public SharedPreferencesManager provideStorageManager(@NonNull Context context) {
        return new SharedPreferencesManager(context);
    }
}
