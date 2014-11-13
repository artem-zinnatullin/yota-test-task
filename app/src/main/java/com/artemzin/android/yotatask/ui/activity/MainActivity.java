package com.artemzin.android.yotatask.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.artemzin.android.yotatask.R;
import com.artemzin.android.yotatask.YotaTaskApp;
import com.artemzin.android.yotatask.api.response.ItemsResponse;
import com.artemzin.android.yotatask.storage.Cart;
import com.artemzin.android.yotatask.storage.SharedPreferencesManager;
import com.artemzin.android.yotatask.ui.adapter.ItemsAdapter;
import com.artemzin.android.yotatask.ui.fragment.ItemsListFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.math.BigDecimal;

import javax.inject.Inject;

public class MainActivity extends ActionBarActivity {

    @Inject Bus mEventBus;

    @Inject SharedPreferencesManager mSharedPreferencesManager;

    @Nullable MenuItem mCartMenuItem;

    @NonNull Cart mCart = new Cart();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YotaTaskApp.get(this).inject(this);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mSharedPreferencesManager.saveCart(null); // clearing cart on app start

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame_layout, ItemsListFragment.newInstance())
                    .commit();
        }

        mEventBus.register(this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.cart, menu);
        mCartMenuItem = menu.findItem(R.id.menu_item_cart_total_sum);

        recalculateCart();

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_cart_total_sum) {
            saveCart();
            startActivity(new Intent(this, CartActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override protected void onStart() {
        super.onStart();
        reloadCart();
    }

    @Override protected void onDestroy() {
        mEventBus.unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onAddItemsToCartEvent(@NonNull AddItemsToCartEvent event) {
        for (ItemsResponse.Item item : event.itemAndQuantityMap.keySet()) {
            mCart.add(new Cart.CartItem(item, event.itemAndQuantityMap.get(item)));
        }

        recalculateCart();
    }

    void reloadCart() {
        mCart = mSharedPreferencesManager.readCart();
        recalculateCart();
    }

    void recalculateCart() {
        BigDecimal totalCartSum = mCart.calculateTotalSum();

        if (mCartMenuItem != null) {
            if (totalCartSum.equals(new BigDecimal(0))) {
                mCartMenuItem.setTitle(R.string.main_cart_menu_item_empty_title);
            } else {
                mCartMenuItem.setTitle(getString(R.string.items_list_content_ui_total_sum, ItemsAdapter.formatPrice(totalCartSum)));
            }
        }
    }

    void saveCart() {
        mSharedPreferencesManager.saveCart(mCart);
    }
}
