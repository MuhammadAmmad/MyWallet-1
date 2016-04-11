package com.example.vlad.personalwallet;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Vlad on 06.03.2016.
 */
public class FragmentIncome extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        View rootView =
                inflater.inflate(R.layout.fragment_income,
                        container, false);
        return rootView;
    }
}
