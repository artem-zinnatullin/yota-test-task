package com.artemzin.android.yotatask.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.artemzin.android.yotatask.R;
import com.artemzin.android.yotatask.ui.fragment.ItemsListFragment;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame_layout, ItemsListFragment.newInstance())
                    .commit();
        }
    }
}
