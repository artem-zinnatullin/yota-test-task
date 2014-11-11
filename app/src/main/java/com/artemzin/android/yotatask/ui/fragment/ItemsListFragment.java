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

import com.artemzin.android.yotatask.R;
import com.artemzin.android.yotatask.YotaTaskApp;
import com.artemzin.android.yotatask.api.response.ItemsResponse;
import com.artemzin.android.yotatask.model.ItemsActiveModel;
import com.artemzin.android.yotatask.model.Task;
import com.artemzin.android.yotatask.ui.adapter.ItemsAdapter;

import java.util.List;

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

    @NonNull RecyclerView mRecyclerView;

    @NonNull ItemsAdapter mItemsAdapter;

    //endregion

    @Inject ItemsActiveModel mItemsActiveModel;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        YotaTaskApp.get(getActivity()).inject(this);
        mItemsAdapter = new ItemsAdapter();

        reloadItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_items_list, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        mRecyclerView = (RecyclerView) mContentUi;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mItemsAdapter);
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
                mItemsAdapter.setData(items);

                if (items.size() == 0) {
                    setUiStateEmpty();
                } else {
                    setUiStateContent();
                }
            }
        });
    }
}
