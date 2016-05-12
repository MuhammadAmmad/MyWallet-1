package com.wallet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.wallet.Constants;
import com.wallet.R;
import com.wallet.model.IncomeTable;
import com.wallet.model.SpendTable;


public class MainFragment extends Fragment implements Constants{

    View mView;
    ImageButton mAddIncomeImg, mAddSpendImg, mViewBalanceImg, mViewAllIncomeButton, mViewAllSpendButton;
    TextView mSpendOfDay, mSpendWeek, mSpendMonth, mIncomeMonth, mAllBalance;
    FragmentTransaction mFragmentTransaction;
    Fragment mFragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        ActiveAndroid.initialize(mView.getContext());

        initView();
        setClickListeners();
        setValue();

        return mView;
    }

    private void setValue() {
        // sets the spend money of week
        double allSpendOfWeek = SpendTable.getSpendOfWeek();
        mSpendWeek.setText(String.valueOf(allSpendOfWeek));

        // sets the spend money of day
        double allSpendOfDay = SpendTable.getSpendOfDay();
        mSpendOfDay.setText(String.valueOf(allSpendOfDay));

        // sets the spend money of month
        double allSpendOfMonth = SpendTable.getSpendOfMonth();
        mSpendMonth.setText(String.valueOf(allSpendOfMonth));

        // sets the spend money of month
        double allSIncomeOfMonth = IncomeTable.getIncomeOfMonth();
        mIncomeMonth.setText(String.valueOf(allSIncomeOfMonth));

        // sets the balance of all time
        double allSumSpend = SpendTable.getAllSum();
        double allSumIncome = IncomeTable.getAllSum();
        mAllBalance.setText(String.valueOf(allSumIncome - allSumSpend));
    }

    private void initView() {
        mAddIncomeImg = (ImageButton) mView.findViewById(R.id.add_income_button);
        mAddSpendImg = (ImageButton) mView.findViewById(R.id.add_spend_button);
        mViewBalanceImg = (ImageButton) mView.findViewById(R.id.view_balance_button);
        mViewAllIncomeButton = (ImageButton) mView.findViewById(R.id.allIncomeButton);
        mViewAllSpendButton = (ImageButton) mView.findViewById(R.id.allSpendButton);

        mSpendOfDay = (TextView) mView.findViewById(R.id.spend_today);
        mSpendWeek = (TextView) mView.findViewById(R.id.spend_week);
        mSpendMonth = (TextView) mView.findViewById(R.id.spend_month);
        mIncomeMonth = (TextView) mView.findViewById(R.id.income_month);
        mAllBalance = (TextView) mView.findViewById(R.id.balance_all_time);
    }

    private void setClickListeners() {
        mFragmentTransaction = getFragmentManager().beginTransaction();

        // click listener on the imageButton add new income money
        mAddIncomeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment = new NewEventFragment();
                Bundle bundle = new Bundle();
                bundle.putString("tagEvent", CATEGORY_EVENT_INCOME);
                mFragment.setArguments(bundle);
                mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                mFragmentTransaction.replace(R.id.custom_fragment, mFragment).commit();
            }
        });

        // click listener on the imageButton add new spend money
        mAddSpendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment = new NewEventFragment();
                Bundle bundle = new Bundle();
                bundle.putString("tagEvent", CATEGORY_EVENT_SPEND);
                mFragment.setArguments(bundle);
                mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                mFragmentTransaction.replace(R.id.custom_fragment, mFragment).commit();
            }
        });

        // click listener on the imageButton view personal balance
        mViewBalanceImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment = new BalanceFragment();
                mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                mFragmentTransaction.replace(R.id.custom_fragment, mFragment).commit();
            }
        });

        // click listener on the imageButton view all list income money
        mViewAllIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment = new AllSpendFragment();
                Bundle bundle = new Bundle();
                bundle.putString("tagEvent", CATEGORY_EVENT_INCOME);
                mFragment.setArguments(bundle);
                mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                mFragmentTransaction.replace(R.id.custom_fragment, mFragment).commit();
            }
        });

        // click listener on the imageButton view all list spend money
        mViewAllSpendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment = new AllSpendFragment();
                Bundle bundle = new Bundle();
                bundle.putString("tagEvent", CATEGORY_EVENT_SPEND);
                mFragment.setArguments(bundle);
                mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                mFragmentTransaction.replace(R.id.custom_fragment, mFragment).commit();
            }
        });
    }


}
