package com.artemzin.android.yotatask.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.artemzin.android.yotatask.R;
import com.artemzin.android.yotatask.YotaTaskApp;
import com.artemzin.android.yotatask.api.response.ItemsResponse;
import com.artemzin.android.yotatask.model.ItemsActiveModel;
import com.artemzin.android.yotatask.model.Task;
import com.artemzin.android.yotatask.ui.activity.AddItemsToCartEvent;
import com.artemzin.android.yotatask.ui.adapter.ItemQuantityChangedEvent;
import com.artemzin.android.yotatask.ui.adapter.ItemsAdapter;
import com.artemzin.android.yotatask.ui.adapter.ResetQuantityOfItemsEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class ItemsListFragment extends Fragment {

    @NonNull public static ItemsListFragment newInstance() {
        return new ItemsListFragment();
    }

    //region Ui

    @InjectView(R.id.items_list_loading_ui)
    View mLoadingUi;

    @InjectView(R.id.items_list_error_ui)
    View mErrorUi;

    @InjectView(R.id.items_list_empty_ui)
    View mEmptyUi;

    @InjectView(R.id.items_list_content_ui)
    View mContentUi;

    @InjectView(R.id.items_list_content_ui_recycler_view)
    RecyclerView mRecyclerView;

    @NonNull ItemsAdapter mItemsAdapter;

    @InjectView(R.id.items_list_content_ui_total_sum_text_view)
    TextView mTotalSumTextView;

    //endregion

    @Inject Bus mEventBus;

    @Inject ItemsActiveModel mItemsActiveModel;

    // map for storing pairs [(item_id, item_quantity)]
    Map<String, Integer> mItemIdAndQuantityMap = new HashMap<String, Integer>();

    // map for storing pairs [(item_id, item_info)]
    Map<String, ItemsResponse.Item> mItemIdAndItemMap = new HashMap<String, ItemsResponse.Item>();

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        YotaTaskApp.get(getActivity()).inject(this);
        mItemsAdapter = new ItemsAdapter(getActivity());

        mEventBus.register(this);

        reloadItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_items_list, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mItemsAdapter);

        recalculateCart();
    }

    @Override public void onDestroy() {
        mEventBus.unregister(this);
        mItemsAdapter.release();
        super.onDestroy();
    }

    private void setUiStateLoading() {
        mLoadingUi.setVisibility(View.VISIBLE);
        mErrorUi.setVisibility(View.GONE);
        mEmptyUi.setVisibility(View.GONE);
        mContentUi.setVisibility(View.GONE);
    }

    private void setUiStateError() {
        mLoadingUi.setVisibility(View.GONE);
        mErrorUi.setVisibility(View.VISIBLE);
        mEmptyUi.setVisibility(View.GONE);
        mContentUi.setVisibility(View.GONE);
    }

    private void setUiStateEmpty() {
        mLoadingUi.setVisibility(View.GONE);
        mErrorUi.setVisibility(View.GONE);
        mEmptyUi.setVisibility(View.VISIBLE);
        mContentUi.setVisibility(View.GONE);
    }

    private void setUiStateContent() {
        mLoadingUi.setVisibility(View.GONE);
        mErrorUi.setVisibility(View.GONE);
        mEmptyUi.setVisibility(View.GONE);
        mContentUi.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.items_list_error_try_again_button)
    public void reloadItems() {
        mItemsActiveModel.asyncLoadItems(new Task.Listener<List<ItemsResponse.Item>>() {
            @Override public void onPreExecute() {
                setUiStateLoading();
            }

            @Override public void onExceptionOccurred(Throwable e) {
                setUiStateError();
            }

            @Override public void onDataProcessed(List<ItemsResponse.Item> items) {
                mItemIdAndItemMap.clear();
                // storing items as map for Cart functionality
                for (ItemsResponse.Item item : items) {
                    mItemIdAndItemMap.put(item.getId(), item);
                }

                mItemsAdapter.setData(items);

                if (items.size() == 0) {
                    setUiStateEmpty();
                } else {
                    setUiStateContent();
                }
            }
        });
    }

    // works via EventBus
    @Subscribe
    public void onItemQuantityChanged(@NonNull ItemQuantityChangedEvent event) {
        mItemIdAndQuantityMap.put(event.itemId, event.quantity);
        recalculateCart();
    }

    void recalculateCart() {
        BigDecimal totalCartSum = new BigDecimal(0);

        for (String itemId : mItemIdAndQuantityMap.keySet()) {
            int quantity = mItemIdAndQuantityMap.get(itemId);

            if (quantity > 0) {
                totalCartSum = totalCartSum.add(mItemIdAndItemMap.get(itemId).getPrice().multiply(new BigDecimal(quantity)));
            }
        }

        mTotalSumTextView.setText(getString(R.string.items_list_content_ui_total_sum, ItemsAdapter.formatPrice(totalCartSum)));
    }

    @OnClick(R.id.items_list_content_ui_add_to_cart_button)
    void addSelectedItemsToCart() {
        // map with [(item, quantity)] to add to global cart
        Map<ItemsResponse.Item, Integer> itemAndQuantityMap = new HashMap<ItemsResponse.Item, Integer>();

        for (String itemId : mItemIdAndQuantityMap.keySet()) {
            Integer quantity = mItemIdAndQuantityMap.get(itemId);

            if (quantity > 0) {
                itemAndQuantityMap.put(mItemIdAndItemMap.get(itemId), quantity);
            }
        }

        // notifying global cart about addition
        mEventBus.post(new AddItemsToCartEvent(itemAndQuantityMap));

        // resetting list state
        mEventBus.post(new ResetQuantityOfItemsEvent());

        // clearing local info about quantity of items
        mItemIdAndQuantityMap.clear();

        // recalculating current selected items
        recalculateCart();
    }
}
