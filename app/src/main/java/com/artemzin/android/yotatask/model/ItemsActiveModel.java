package com.artemzin.android.yotatask.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.artemzin.android.yotatask.api.YotaTaskApi;
import com.artemzin.android.yotatask.api.response.ItemsResponse;

import java.util.ArrayList;
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
                final ItemsResponse itemsResponse = mYotaTaskApi.getItems("53h769d7084emao/yota_items.json");
                final List<ItemsResponse.Item> itemsFromResponse = itemsResponse.getItems();

                if (itemsFromResponse == null || itemsFromResponse.size() == 0) {
                    return new ArrayList<ItemsResponse.Item>(0);
                }

                List<ItemsResponse.Item> items = new ArrayList<ItemsResponse.Item>(itemsFromResponse.size());

                for (ItemsResponse.Item item : itemsFromResponse) {
                    if (item.getPrice() == null) {
                        continue;
                    }

                    items.add(item);
                }

                // TODO don't forget to sort items by positions

                return items;
            }
        });
    }
}
