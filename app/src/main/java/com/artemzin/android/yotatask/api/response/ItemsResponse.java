package com.artemzin.android.yotatask.api.response;

import android.support.annotation.NonNull;
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

        @SerializedName("id")
        @NonNull private String mId;

        @SerializedName("pos")
        private int mPos;

        @SerializedName("name")
        @Nullable private String mName;

        @SerializedName("price")
        @NonNull private BigDecimal mPrice;

        @NonNull public String getId() {
            return mId;
        }

        public int getPos() {
            return mPos;
        }

        @Nullable public String getName() {
            return mName;
        }

        @NonNull public BigDecimal getPrice() {
            return mPrice;
        }

        @Override public int hashCode() {
            return mId.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item item = (Item) o;

            if (mPos != item.mPos) return false;
            if (!mId.equals(item.mId)) return false;
            if (mName != null ? !mName.equals(item.mName) : item.mName != null) return false;
            if (!mPrice.equals(item.mPrice)) return false;

            return true;
        }
    }
}
