package com.artemzin.android.yotatask.ui.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.artemzin.android.yotatask.Loggi;
import com.artemzin.android.yotatask.R;
import com.artemzin.android.yotatask.YotaTaskApp;
import com.artemzin.android.yotatask.storage.Cart;
import com.artemzin.android.yotatask.storage.SharedPreferencesManager;
import com.artemzin.android.yotatask.ui.adapter.CartItemsAdapter;
import com.artemzin.android.yotatask.ui.adapter.OnItemRemoveFromCartEvent;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class CartFragment extends Fragment {

    @NonNull public static CartFragment newInstance() {
        return new CartFragment();
    }

    @InjectView(R.id.cart_recycler_view)
    RecyclerView mRecyclerView;

    @NonNull CartItemsAdapter mCartItemsAdapter;

    @Inject SharedPreferencesManager mSharedPreferencesManager;

    @Inject Bus mEventBus;

    @Inject Gson mGson;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YotaTaskApp.get(getActivity()).inject(this);
        mCartItemsAdapter = new CartItemsAdapter(getActivity());
        mEventBus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mCartItemsAdapter);
    }

    @Override public void onStart() {
        super.onStart();
        reloadCart();
    }

    @Override public void onDestroy() {
        mEventBus.unregister(this);
        super.onDestroy();
    }

    @NonNull Cart reloadCart() {
        Cart cart = mSharedPreferencesManager.readCart();
        mCartItemsAdapter.setCart(cart.toList());
        return cart;
    }

    @Subscribe public void onItemRemoveFromCart(@NonNull OnItemRemoveFromCartEvent event) {
        Cart cart = mSharedPreferencesManager.readCart();
        cart.remove(event.mCartItem);
        mSharedPreferencesManager.saveCart(cart);
        reloadCart();

        mEventBus.post(new OnCartChangedEvent());
    }

    @OnClick(R.id.cart_send_order_button)
    public void onSendOrderButtonClick() {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("message/rfc822");

        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.order_email_body, getCurrentPhoneNumber(), getUserEmail()));

        try {
            File orderFile = saveCartToFile("yota_order.json", reloadCart());
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + orderFile));
        } catch (Throwable t) {
            Loggi.e(Loggi.classNameAsTag(this), "can not serialize cart to file", t);
            Toast.makeText(getActivity(), R.string.order_email_can_not_create_order_file, Toast.LENGTH_LONG).show();
        }

        mSharedPreferencesManager.saveCart(null); // clearing cart
        mEventBus.post(new OnCartChangedEvent());

        startActivity(Intent.createChooser(intent, getString(R.string.order_email_chooser_dialog_title)));
    }

    @NonNull File saveCartToFile(@NonNull String fileName, @NonNull Cart cart) throws Exception {
        String cartForEmail = mGson.toJson(new CartForEmailProjection(cart));

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

        outputStreamWriter.write(cartForEmail);
        outputStreamWriter.close();

        return file;
    }

    @NonNull String getCurrentPhoneNumber() {
        String phoneNumber = ((TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        return phoneNumber != null && phoneNumber.length() != 0 ? phoneNumber : getString(R.string.order_email_no_phone_number);
    }

    @NonNull String getUserEmail() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;

        Account[] accounts = AccountManager.get(getActivity()).getAccounts();

        String email = null;

        if (accounts == null || accounts.length == 0) {
            email = null;
        } else {
            // peeking only one email
            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    email = account.name;
                    break;
                }
            }
        }

        return email != null ? email : getString(R.string.order_email_no_user_email);
    }

    private static class CartForEmailProjection {

        @SerializedName("items")
        @NonNull List<ItemForEmail> mItems;

        private CartForEmailProjection(@NonNull Cart cart) {
            List<Cart.CartItem> cartItems = cart.toList();
            mItems = new ArrayList<ItemForEmail>(cartItems.size());

            for (Cart.CartItem cartItem : cartItems) {
                mItems.add(new ItemForEmail(cartItem));
            }
        }

        static class ItemForEmail {

            @SerializedName("id")
            @NonNull String mId;

            @SerializedName("name")
            @NonNull String mName;

            @SerializedName("quantity")
            @NonNull int mQuantity;

            ItemForEmail(@NonNull Cart.CartItem cartItem) {
                mId = cartItem.getItem().getId();
                mName = cartItem.getItem().getName();
                mQuantity = cartItem.getQuantity();
            }
        }
    }
}
