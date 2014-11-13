package com.artemzin.android.yotatask.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.artemzin.android.yotatask.R;
import com.artemzin.android.yotatask.YotaTaskApp;
import com.artemzin.android.yotatask.api.response.ItemsResponse;
import com.artemzin.android.yotatask.storage.Cart;
import com.squareup.otto.Bus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.CartItemViewHolder> {

    @Inject Bus mEventBus;

    @NonNull private List<Cart.CartItem> mCartItems = new ArrayList<Cart.CartItem>(0);

    public CartItemsAdapter(@NonNull Context context) {
        YotaTaskApp.get(context).inject(this);
    }

    public void setCart(@NonNull List<Cart.CartItem> cartItems) {
        mCartItems = cartItems;
        notifyDataSetChanged();
    }

    @Override public CartItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        CartItemViewHolder viewHolder = new CartItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_cart_item, viewGroup, false));
        viewHolder.removeButton.setOnClickListener(new OnRemoveButtonClickListener()); // memory optimization
        return viewHolder;
    }

    @Override public void onBindViewHolder(CartItemViewHolder viewHolder, int position) {
        Cart.CartItem cartItem  = mCartItems.get(position);
        ItemsResponse.Item item = cartItem.getItem();

        Context context = viewHolder.itemView.getContext();

        viewHolder.nameTextView.setText(item.getName());
        viewHolder.priceTextView.setText(context.getString(R.string.cart_item_price, ItemsAdapter.formatPrice(item.getPrice())));
        viewHolder.quantityTextView.setText(context.getString(R.string.cart_item_quantity, String.valueOf(cartItem.getQuantity())));
        viewHolder.totalTextView.setText(context.getString(R.string.cart_item_total_sum, ItemsAdapter.formatPrice(item.getPrice().multiply(new BigDecimal(cartItem.getQuantity())))));

        // working with remove button click listener via tags for better memory usage
        viewHolder.removeButton.setTag(position);
    }

    @Override public int getItemCount() {
        return mCartItems.size();
    }

    class OnRemoveButtonClickListener implements View.OnClickListener {
        @Override public void onClick(View v) {
            Integer position = (Integer) v.getTag();
            mEventBus.post(new OnItemRemoveFromCartEvent(mCartItems.get(position)));
        }
    }

    static class CartItemViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.cart_item_name_text_view)
        TextView nameTextView;

        @InjectView(R.id.cart_item_price_text_view)
        TextView priceTextView;

        @InjectView(R.id.cart_item_quantity_text_view)
        TextView quantityTextView;

        @InjectView(R.id.cart_item_total_text_view)
        TextView totalTextView;

        @InjectView(R.id.cart_item_remove_button)
        Button removeButton;

        public CartItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
