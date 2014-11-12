package com.artemzin.android.yotatask.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artemzin.android.yotatask.R;
import com.artemzin.android.yotatask.YotaTaskApp;
import com.artemzin.android.yotatask.storage.Cart;
import com.artemzin.android.yotatask.storage.SharedPreferencesManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class CartFragment extends Fragment {

    @NonNull public static CartFragment newInstance() {
        return new CartFragment();
    }

    @InjectView(R.id.cart_recycler_view)
    RecyclerView mRecyclerView;

    @Inject SharedPreferencesManager mSharedPreferencesManager;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YotaTaskApp.get(getActivity()).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);


    }

    void reloadCart() {
        Cart cart = mSharedPreferencesManager.readCart();

        if (cart == null) {
            getActivity().finish();
            return;
        }
    }
}
