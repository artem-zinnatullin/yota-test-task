package com.artemzin.android.yotatask.ui.adapter;

import android.support.annotation.NonNull;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class ItemQuantityChangedEvent {

    @NonNull public final String itemId;

    public final int quantity;

    public ItemQuantityChangedEvent(@NonNull String itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
