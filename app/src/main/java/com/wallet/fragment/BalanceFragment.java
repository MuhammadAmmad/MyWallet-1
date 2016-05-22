package com.wallet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.activeandroid.ActiveAndroid;
import com.wallet.R;
import com.wallet.model.IncomeItem;
import com.wallet.model.SpendItem;

import android.support.v4.app.Fragment;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class BalanceFragment extends Fragment {

    private View mView;
    private ImageButton mPrevYear, mNextYear;
    private TextView mCurrentYear, mBalanceFromYear;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_balance, container, false);
        ActiveAndroid.initialize(mView.getContext());

        initView();
        setValue();
        setClickListeners();

        return mView;
    }

    private void initView() {
        mPrevYear = (ImageButton) mView.findViewById(R.id.imageButtonPrevYear);
        mNextYear = (ImageButton) mView.findViewById(R.id.imageButtonNextYear);
        mCurrentYear = (TextView) mView.findViewById(R.id.currentNumberOfYear);
        mBalanceFromYear = (TextView) mView.findViewById(R.id.balanceOfYear);
    }

    private void setValue() {
        // set default year
        String defaultYear = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime());
        mCurrentYear.setText(defaultYear);

        // set default all balance from year
        Double balanceSumOfYear = IncomeItem.getAllSumFromYear(Integer.parseInt(defaultYear))
                - SpendItem.getAllSumFromYear(Integer.parseInt(defaultYear));
        mBalanceFromYear.setText(String.valueOf(balanceSumOfYear));

        double[] balanceArray = getArrayOfBalanceFromYear(Integer.valueOf(defaultYear));
        String[] monthArray = getResources().getStringArray(R.array.all_month);
        setSimpleAdapter(monthArray, balanceArray);

    }

    private void setClickListeners() {
        mPrevYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = Integer.parseInt(mCurrentYear.getText().toString()) - 1;
                mCurrentYear.setText(String.valueOf(year));

                Double balanceSumOfYear = IncomeItem.getAllSumFromYear(year)
                        - SpendItem.getAllSumFromYear(year);
                mBalanceFromYear.setText(String.valueOf(balanceSumOfYear));

                double[] balanceArray = getArrayOfBalanceFromYear(year);
                String[] monthArray = getResources().getStringArray(R.array.all_month);
                setSimpleAdapter(monthArray, balanceArray);
            }
        });

        mNextYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = Integer.parseInt(mCurrentYear.getText().toString()) + 1;
                mCurrentYear.setText(String.valueOf(year));

                Double balanceSumOfYear = IncomeItem.getAllSumFromYear(year)
                        - SpendItem.getAllSumFromYear(year);
                mBalanceFromYear.setText(String.valueOf(balanceSumOfYear));

                double[] balanceArray = getArrayOfBalanceFromYear(year);
                String[] monthArray = getResources().getStringArray(R.array.all_month);
                setSimpleAdapter(monthArray, balanceArray);
            }
        });
    }

    private double[] getArrayOfBalanceFromYear(Integer numberOfYear) {
        double[] balanceFromYear = new double[12];

        double[] sumSpendFromYear = SpendItem.getSpendArrayOfYear(numberOfYear);
        double[] sumIncomeFromYear = IncomeItem.getIncomeArrayOfYear(numberOfYear);

        for (int i = 0; i < balanceFromYear.length; i++) {
            balanceFromYear[i] = sumIncomeFromYear[i] - sumSpendFromYear[i];
        }

        return balanceFromYear;
    }

    private void setSimpleAdapter(String[] monthArray, double[] balanceArray) {
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(monthArray.length);
        Map<String, Object> map;
        for (int i = 0; i < monthArray.length; i++) {
            map = new HashMap<>();
            map.put("monthName", monthArray[i]);
            map.put("balanceValueOfMonth", balanceArray[i]);
            data.add(map);
        }

        String[] from = {"monthName", "balanceValueOfMonth"};
        int to[] = {R.id.monthName, R.id.sumOfMonth};

        SimpleAdapter simpleAdapter = new SimpleAdapter(mView.getContext(), data, R.layout.adapter_item_balance, from, to);
        ((ListView) mView.findViewById(R.id.lvBalanceMain)).setAdapter(simpleAdapter);
    }
}
