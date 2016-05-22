package com.wallet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.wallet.Constants;
import com.wallet.R;
import com.wallet.adapter.SumFromCategoryAdapter;
import com.wallet.model.CategorySumItem;
import com.wallet.model.IncomeItem;
import com.wallet.model.SpendItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AllEventFragment extends Fragment {

    private String mEventCategory;
    private Map<String, Double> mMap = null;
    private List<CategorySumItem> mCategoryList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_spend_layout, container, false);

        mEventCategory = getArguments().getString("tagEvent");
        if (mEventCategory != null) {
            if (mEventCategory.equals(Constants.CATEGORY_EVENT_SPEND)) {
                ((TextView) view.findViewById(R.id.totalSum)).setText(String.valueOf(SpendItem.getAllSum()));
                mMap = getSpendCategoryAndSum();
            } else if (mEventCategory.equals(Constants.CATEGORY_EVENT_INCOME)) {
                ((TextView) view.findViewById(R.id.totalSum)).setText(String.valueOf(IncomeItem.getAllSum()));
                mMap = getIncomeCategoryAndSum();
            }
        }

        mCategoryList = getListFromCategoryAndSum(mMap);
        SumFromCategoryAdapter sumFromCategory = new SumFromCategoryAdapter(view.getContext(), mCategoryList);
        ListView lvMain = (ListView) view.findViewById(R.id.lvBalanceMain);
        lvMain.setAdapter(sumFromCategory);

        sumFromCategory.setEventClickListener(onClickRow);

        return view;
    }

    private SumFromCategoryAdapter.IItemEventListener onClickRow = new SumFromCategoryAdapter.IItemEventListener() {
        @Override
        public void onClickRow(int position) {

            CategoryInfoFragment fragment = new CategoryInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putString("catName", mCategoryList.get(position).getCategory());
            bundle.putString("tagEvent", mEventCategory);
            fragment.setArguments(bundle);
            ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.custom_fragment, fragment).commit();
        }
    };

    private List<CategorySumItem> getListFromCategoryAndSum(Map<String, Double> map) {
        List<CategorySumItem> list = new ArrayList<>();
        if (map != null) {
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                CategorySumItem categorySumItem = new CategorySumItem();
                categorySumItem.setCategory(entry.getKey());
                categorySumItem.setSum(entry.getValue().toString());
                categorySumItem.save();
                list.add(categorySumItem);
            }
        }
        return list;
    }


    private Map<String, Double> getSpendCategoryAndSum() {
        Map<String, Double> categoryAndSum = new HashMap<>();
        List<SpendItem> spendItem = SpendItem.getAll();

        for (int i = 0; i < spendItem.size(); i++) {
            String catName = spendItem.get(i).getCategory();
            Double sum = spendItem.get(i).getCash();
            if (categoryAndSum.containsKey(catName)) {
                Double lastSum = categoryAndSum.get(catName);
                categoryAndSum.put(catName, lastSum + sum);
            } else {
                categoryAndSum.put(catName, sum);
            }
        }

        return categoryAndSum;
    }

    private Map<String, Double> getIncomeCategoryAndSum() {
        Map<String, Double> categoryAndSum = new HashMap<>();
        List<IncomeItem> incomeItem = IncomeItem.getAll();

        for (int i = 0; i < incomeItem.size(); i++) {
            String catName = incomeItem.get(i).getCategory();
            Double sum = incomeItem.get(i).getCash();
            if (categoryAndSum.containsKey(catName)) {
                Double lastSum = categoryAndSum.get(catName);
                categoryAndSum.put(catName, lastSum + sum);
            } else {
                categoryAndSum.put(catName, sum);
            }
        }

        return categoryAndSum;
    }
}
