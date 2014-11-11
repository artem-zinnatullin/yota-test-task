package com.artemzin.android.yotatask.ui.activity;

import android.support.annotation.NonNull;

import com.artemzin.android.yotatask.api.response.ItemsResponse;

import java.util.Map;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class AddItemsToCartEvent {

    @NonNull public final Map<ItemsResponse.Item, Integer> itemAndQuantityMap;

    public AddItemsToCartEvent(@NonNull Map<ItemsResponse.Item, Integer> itemAndQuantityMap) {
        this.itemAndQuantityMap = itemAndQuantityMap;
    }
}
