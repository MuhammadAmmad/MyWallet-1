package com.wallet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.wallet.Constants;
import com.wallet.R;
import com.wallet.adapter.SumFromCategoryAdapter;
import com.wallet.model.IncomeTable;
import com.wallet.model.SpendTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AllSpendFragment extends Fragment implements Constants{

    View mView;
    String mEventCategory;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.all_spend_layout, container, false);
        ActiveAndroid.initialize(getContext());

        Bundle bundle = getArguments();
        mEventCategory = bundle.getString("tagEvent");
        Map<String, Double> map = null;

        if (mEventCategory.equals(CATEGORY_EVENT_SPEND)) {
            ((TextView) mView.findViewById(R.id.totalSum)).setText(String.valueOf(SpendTable.getAllSum()));
            map = getSpendCategoryAndSum();
        } else if (mEventCategory.equals(CATEGORY_EVENT_INCOME)){
            ((TextView) mView.findViewById(R.id.totalSum)).setText(String.valueOf(IncomeTable.getAllSum()));
            map = getIncomeCategoryAndSum();
        }



        List<String> name = new ArrayList<>() ;
        List<String> sum = new ArrayList<>() ;
        for (Map.Entry<String, ?> entry : map.entrySet()){
            name.add(entry.getKey());
            sum.add(entry.getValue().toString());
        }

        SumFromCategoryAdapter sumFromCat = new SumFromCategoryAdapter(mView.getContext(), name, sum, mEventCategory);
        ListView lvMain = (ListView)mView.findViewById(R.id.lvBalanceMain);
        lvMain.setAdapter(sumFromCat);

        return mView;
    }


    public Map<String, Double> getSpendCategoryAndSum() {
        Map<String, Double> categoryAndSum = new HashMap<>();
        List<SpendTable> spendTable = SpendTable.getAll();

        for(int i = 0; i < spendTable.size(); i++){
            String catName = spendTable.get(i).category;
            Double sum = spendTable.get(i).cash;
            if (categoryAndSum.containsKey(catName)){
                Double lastSum = categoryAndSum.get(catName);
                categoryAndSum.put(catName, lastSum + sum);
            } else {
                categoryAndSum.put(catName, sum);
            }
        }

        return categoryAndSum;
    }

    public Map<String, Double> getIncomeCategoryAndSum() {
        Map<String, Double> categoryAndSum = new HashMap<>();
        List<IncomeTable> incomeTable = IncomeTable.getAll();

        for(int i = 0; i < incomeTable.size(); i++){
            String catName = incomeTable.get(i).category;
            Double sum = incomeTable.get(i).cash;
            if (categoryAndSum.containsKey(catName)){
                Double lastSum = categoryAndSum.get(catName);
                categoryAndSum.put(catName, lastSum + sum);
            } else {
                categoryAndSum.put(catName, sum);
            }
        }

        return categoryAndSum;
    }
}
