package com.artemzin.android.yotatask.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.artemzin.android.yotatask.api.YotaTaskApi;
import com.artemzin.android.yotatask.api.response.ItemsResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class ItemsActiveModel extends BaseActiveModel {

    @Inject YotaTaskApi mYotaTaskApi;

    @Override protected boolean shouldInject() {
        return true;
    }

    public ItemsActiveModel(@NonNull Context context) {
        super(context);
    }

    public void asyncLoadItems(@NonNull Task.Listener<List<ItemsResponse.Item>> listener) {
        executeTask(new Task<List<ItemsResponse.Item>>(listener) {
            @Override protected List<ItemsResponse.Item> doWork() throws Exception {
                // TODO use new data only if file size changed (read task)
                final ItemsResponse itemsResponse = mYotaTaskApi.getItems("53h769d7084emao/yota_items.json");
                final List<ItemsResponse.Item> itemsFromResponse = itemsResponse.getItems();

                if (itemsFromResponse == null || itemsFromResponse.size() == 0) {
                    return new ArrayList<ItemsResponse.Item>(0);
                }

                List<ItemsResponse.Item> items = new ArrayList<ItemsResponse.Item>(itemsFromResponse.size());

                for (ItemsResponse.Item item : itemsFromResponse) {
                    //noinspection ConstantConditions
                    if (item.getId() == null || item.getPrice() == null) {
                        continue;
                    }

                    items.add(item);
                }

                // sorting items by positions
                Collections.sort(items, new ItemsPositionComparator());

                return items;
            }
        });
    }

    private static class ItemsPositionComparator implements Comparator<ItemsResponse.Item> {
        @Override public int compare(ItemsResponse.Item lhs, ItemsResponse.Item rhs) {
            return compareIntegers(lhs.getPos(), rhs.getPos());
        }

        // copy from JDK 1.7 Integer.compare() for work on Android API below 19
        private static int compareIntegers(int lhs, int rhs) {
            return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
        }
    }
}
