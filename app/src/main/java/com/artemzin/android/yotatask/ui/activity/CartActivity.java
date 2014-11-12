package com.artemzin.android.yotatask.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.artemzin.android.yotatask.R;
import com.artemzin.android.yotatask.ui.fragment.CartFragment;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class CartActivity extends ActionBarActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.cart_frame_layout, CartFragment.newInstance())
                    .commit();
        }
    }
}
