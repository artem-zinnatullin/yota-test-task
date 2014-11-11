package com.artemzin.android.yotatask.api.response;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class ItemsResponse {

    @SerializedName("items")
    @Nullable private List<Item> mItems;

    @Nullable public List<Item> getItems() {
        return mItems;
    }

    public static class Item {

        @SerializedName("pos")
        private int mPos;

        @SerializedName("name")
        @Nullable private String mName;

        @SerializedName("price")
        @Nullable private BigDecimal mPrice;

        public int getPos() {
            return mPos;
        }

        @Nullable public String getName() {
            return mName;
        }

        @Nullable public BigDecimal getPrice() {
            return mPrice;
        }
    }
}
