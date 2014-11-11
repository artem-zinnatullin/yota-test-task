package com.artemzin.android.yotatask.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.artemzin.android.yotatask.R;
import com.artemzin.android.yotatask.api.response.ItemsResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    @NonNull private List<ItemWithMetaInfo> mItems = new ArrayList<ItemWithMetaInfo>(0);

    public void setData(@NonNull List<ItemsResponse.Item> items) {
        mItems.clear();

        for (ItemsResponse.Item item : items) {
            mItems.add(new ItemWithMetaInfo(item));
        }

        notifyDataSetChanged();
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final ItemWithMetaInfo itemWithMetaInfo = mItems.get(position);

        viewHolder.nameTextView.setText(itemWithMetaInfo.item.getName());

        //noinspection ConstantConditions
        viewHolder.priceTextView.setText(formatPrice(itemWithMetaInfo.item.getPrice()));

        viewHolder.quantitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                itemWithMetaInfo.setQuantity(progress);

                final BigDecimal price = itemWithMetaInfo.item.getPrice();
                viewHolder.sumTextView.setText(String.format("%s x %d = %s", price.toString(), progress, formatPrice(price.multiply(new BigDecimal(progress)))));
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {
                // no impl
            }

            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                // no impl
            }
        });

        viewHolder.quantitySeekBar.setProgress(itemWithMetaInfo.getQuantity());
    }

    @Override public int getItemCount() {
        return mItems.size();
    }

    @NonNull private String formatPrice(@NonNull BigDecimal price) {
        return price + " rub";
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.list_item_name_text_view)
        TextView nameTextView;

        @InjectView(R.id.list_item_price_text_view)
        TextView priceTextView;

        @InjectView(R.id.list_item_quantity_seek_bar)
        SeekBar quantitySeekBar;

        @InjectView(R.id.list_item_sum_text_view)
        TextView sumTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public static class ItemWithMetaInfo {

        @NonNull public final ItemsResponse.Item item;

        private int mQuantity;

        public ItemWithMetaInfo(@NonNull ItemsResponse.Item item) {
            this.item = item;
        }

        public int getQuantity() {
            return mQuantity;
        }

        public void setQuantity(int quantity) {
            mQuantity = quantity;
        }
    }
}
