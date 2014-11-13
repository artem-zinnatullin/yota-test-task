package com.artemzin.android.yotatask.ui.adapter;

import android.support.annotation.NonNull;

import com.artemzin.android.yotatask.storage.Cart;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class OnItemRemoveFromCartEvent {

    @NonNull public final Cart.CartItem mCartItem;

    public OnItemRemoveFromCartEvent(@NonNull Cart.CartItem cartItem) {
        mCartItem = cartItem;
    }
}
