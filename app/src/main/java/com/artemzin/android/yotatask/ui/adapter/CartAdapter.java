package com.artemzin.android.yotatask.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return null;
    }

    @Override public void onBindViewHolder(ViewHolder viewHolder, int i) {

    }

    @Override public int getItemCount() {
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
