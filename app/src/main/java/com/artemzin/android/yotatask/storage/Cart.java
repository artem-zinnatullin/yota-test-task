package com.artemzin.android.yotatask.storage;

import android.support.annotation.NonNull;

import com.artemzin.android.yotatask.api.response.ItemsResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class Cart {

    @SerializedName("cart")
    @NonNull private List<CartItem> mCart;

    public Cart() { }

    public Cart(@NonNull List<CartItem> cart) {
        mCart = cart;
    }

    @NonNull public List<CartItem> getCart() {
        return mCart;
    }

    public static class CartItem {

        @SerializedName("item")
        @NonNull private ItemsResponse.Item mItem;

        @SerializedName("quantity")
        @NonNull private Integer mQuantity;

        public CartItem() { }

        public CartItem(@NonNull ItemsResponse.Item item, @NonNull Integer quantity) {
            mItem = item;
            mQuantity = quantity;
        }

        @NonNull public ItemsResponse.Item getItem() {
            return mItem;
        }

        @NonNull public Integer getQuantity() {
            return mQuantity;
        }
    }
}
