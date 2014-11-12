package com.artemzin.android.yotatask.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.artemzin.android.yotatask.Loggi;
import com.artemzin.android.yotatask.YotaTaskApp;
import com.artemzin.android.yotatask.api.response.ItemsResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public void saveCart(@NonNull Map<ItemsResponse.Item, Integer> itemAndQuantityMap) {
        List<Cart.CartItem> cartItems = new ArrayList<Cart.CartItem>(itemAndQuantityMap.size());

        for (ItemsResponse.Item item : itemAndQuantityMap.keySet()) {
            cartItems.add(new Cart.CartItem(item, itemAndQuantityMap.get(item)));
        }

        Cart cart = new Cart(cartItems);

        mSharedPreferences
                .edit()
                .putString(PREFERENCE_CART, mGson.toJson(cart))
                .apply();
    }

    @Nullable public Cart readCart() {
        try {
            return mGson.fromJson(mSharedPreferences.getString(PREFERENCE_CART, ""), Cart.class);
        } catch (Throwable throwable) {
            Loggi.e(Loggi.classNameAsTag(this), "can not read saved cart value", throwable);
            return null;
        }
    }
}
