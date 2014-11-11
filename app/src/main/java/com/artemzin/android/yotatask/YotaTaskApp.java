package com.artemzin.android.yotatask;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import dagger.ObjectGraph;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class YotaTaskApp extends Application {

    @NonNull public static YotaTaskApp get(@NonNull Context context) {
        return (YotaTaskApp) context.getApplicationContext();
    }

    @NonNull private ObjectGraph mObjectGraph;

    @Override public void onCreate() {
        super.onCreate();
        mObjectGraph = ObjectGraph.create(getModules());
    }

    @NonNull private Object[] getModules() {
        return new Object[] {
            new AppModule(this)
        };
    }

    public void inject(@NonNull Object o) {
        mObjectGraph.inject(o);
    }
}
