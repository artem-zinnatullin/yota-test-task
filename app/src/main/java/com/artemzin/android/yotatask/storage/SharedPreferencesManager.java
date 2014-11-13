package com.artemzin.android.yotatask.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.artemzin.android.yotatask.Loggi;
import com.artemzin.android.yotatask.YotaTaskApp;
import com.google.gson.Gson;

import javax.inject.Inject;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class SharedPreferencesManager {

    private static final String SHARED_PREFERENCES_NAME = "shared_preferences";

    private static final String PREFERENCE_CART = "PREFERENCE_CART";

    @NonNull private final SharedPreferences mSharedPreferences;

    @Inject Gson mGson;

    public SharedPreferencesManager(@NonNull Context context) {
        YotaTaskApp.get(context).inject(this);
        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void saveCart(@Nullable Cart cart) {
        mSharedPreferences
                .edit()
                .putString(PREFERENCE_CART, cart != null ? mGson.toJson(cart) : "")
                .apply();
    }

    @NonNull public Cart readCart() {
        try {
            Cart cart = mGson.fromJson(mSharedPreferences.getString(PREFERENCE_CART, ""), Cart.class);
            return cart != null ? cart : new Cart();
        } catch (Throwable throwable) {
            Loggi.e(Loggi.classNameAsTag(this), "can not read saved cart value", throwable);
            return new Cart(); // empty cart
        }
    }
}
