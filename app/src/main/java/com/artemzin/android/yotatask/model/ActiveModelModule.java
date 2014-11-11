package com.artemzin.android.yotatask.model;

import android.content.Context;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
@Module(library = true, complete = false)
public class ActiveModelModule {

    @Provides @NonNull ItemsActiveModel provideItemsActiveModel(@NonNull Context context) {
        return new ItemsActiveModel(context);
    }
}
