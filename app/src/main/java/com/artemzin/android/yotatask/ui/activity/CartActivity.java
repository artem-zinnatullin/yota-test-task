package com.artemzin.android.yotatask.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.artemzin.android.yotatask.R;
import com.artemzin.android.yotatask.YotaTaskApp;
import com.artemzin.android.yotatask.storage.Cart;
import com.artemzin.android.yotatask.storage.SharedPreferencesManager;
import com.artemzin.android.yotatask.ui.adapter.ItemsAdapter;
import com.artemzin.android.yotatask.ui.fragment.CartFragment;
import com.artemzin.android.yotatask.ui.fragment.OnCartChangedEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.math.BigDecimal;

import javax.inject.Inject;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class CartActivity extends ActionBarActivity {

    @Inject Bus mEventBus;

    @Inject SharedPreferencesManager mSharedPreferencesManager;

    @Nullable MenuItem mCartMenuItem;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YotaTaskApp.get(this).inject(this);

        mEventBus.register(this);

        setContentView(R.layout.activity_cart);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.cart_frame_layout, CartFragment.newInstance())
                    .commit();
        }

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.cart, menu);
        mCartMenuItem = menu.findItem(R.id.menu_item_cart_total_sum);

        reloadCart();

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override protected void onDestroy() {
        mEventBus.unregister(this);
        super.onDestroy();
    }

    @Subscribe public void onCartChangedEvent(@NonNull OnCartChangedEvent event) {
        reloadCart();
    }

    void reloadCart() {
        Cart cart = mSharedPreferencesManager.readCart();

        if (cart.size() == 0) {
            finish();
            return;
        }

        BigDecimal totalCartSum = cart.calculateTotalSum();

        if (mCartMenuItem != null) {
            if (totalCartSum.equals(new BigDecimal(0))) {
                mCartMenuItem.setTitle(R.string.main_cart_menu_item_empty_title);
            } else {
                mCartMenuItem.setTitle(getString(R.string.items_list_content_ui_total_sum, ItemsAdapter.formatPrice(totalCartSum)));
            }
        }
    }
}
