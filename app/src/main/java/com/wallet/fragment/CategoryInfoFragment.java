package com.wallet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.activeandroid.ActiveAndroid;
import com.wallet.Constants;
import com.wallet.R;
import com.wallet.adapter.AdapterCategoryIncomeDetail;
import com.wallet.adapter.AdapterCategorySpendDetail;
import com.wallet.model.IncomeTable;
import com.wallet.model.SpendTable;

import java.util.List;


public class CategoryInfoFragment extends Fragment implements Constants{

    View mView;
    String mEventCategory;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_category_info, container, false);
        ActiveAndroid.initialize(getContext());

        Bundle bundle = getArguments();
        String categoryName = bundle.getString("catName");
        mEventCategory = bundle.getString("tagEvent");

        ListView lvMain = (ListView)mView.findViewById(R.id.lvCatInfoMain);

        if (mEventCategory.equals(CATEGORY_EVENT_SPEND)) {
            List<SpendTable> spendTable = SpendTable.getAllByName(categoryName);
            AdapterCategorySpendDetail detail = new AdapterCategorySpendDetail(mView.getContext(), spendTable);
            lvMain.setAdapter(detail);
        } else if (mEventCategory.equals(CATEGORY_EVENT_INCOME)){
            List<IncomeTable> incomeTable = IncomeTable.getAllByName(categoryName);
            AdapterCategoryIncomeDetail detail = new AdapterCategoryIncomeDetail(mView.getContext(), incomeTable);
            lvMain.setAdapter(detail);
        }

        return mView;
    }

}
