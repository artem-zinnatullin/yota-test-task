package com.artemzin.android.yotatask.storage;

import android.support.annotation.NonNull;

import com.artemzin.android.yotatask.api.response.ItemsResponse;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class Cart {

    @SerializedName("cart_items")
    @NonNull private Map<String, CartItem> mItemIdAndCartItemMap = new LinkedHashMap<String, CartItem>(0);

    @NonNull public List<CartItem> toList() {
        List<CartItem> cartAsList = new ArrayList<CartItem>(mItemIdAndCartItemMap.size());

        for (String itemId : mItemIdAndCartItemMap.keySet()) {
            cartAsList.add(mItemIdAndCartItemMap.get(itemId));
        }

        return cartAsList;
    }

    public void add(@NonNull CartItem item) {
        CartItem savedCartItem = mItemIdAndCartItemMap.get(item.getItem().getId());
        mItemIdAndCartItemMap.put(item.getItem().getId(), new CartItem(item.getItem(), savedCartItem != null ? savedCartItem.getQuantity() + item.getQuantity() : item.getQuantity()));
    }

    public void remove(@NonNull CartItem item) {
        mItemIdAndCartItemMap.remove(item.getItem().getId());
    }

    public int size() {
        return mItemIdAndCartItemMap.size();
    }

    @NonNull public BigDecimal calculateTotalSum() {
        BigDecimal totalCartSum = new BigDecimal(0);

        for (Cart.CartItem cartItem : toList()) {
            if (cartItem.getQuantity() > 0) {
                totalCartSum = totalCartSum.add(cartItem.getItem().getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            }
        }

        return totalCartSum;
    }

    public static class CartItem {

        @SerializedName("item")
        @NonNull private ItemsResponse.Item mItem;

        @SerializedName("quantity")
        @NonNull private Integer mQuantity;

        @SuppressWarnings("UnusedDeclaration") // for Gson
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
