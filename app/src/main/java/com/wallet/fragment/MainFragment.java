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
import com.wallet.model.IncomeItem;
import com.wallet.model.SpendItem;


public class MainFragment extends Fragment {

    private View mView;
    private Fragment mFragment;
    private FragmentTransaction mFragmentTransaction;
    private TextView mSpendOfDay, mSpendWeek, mSpendMonth, mIncomeMonth, mAllBalance;
    private ImageButton mAddIncomeImg, mAddSpendImg, mViewBalanceImg, mViewAllIncomeButton, mViewAllSpendButton;

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
        double allSpendOfWeek = SpendItem.getSpendOfWeek();
        mSpendWeek.setText(String.valueOf(allSpendOfWeek));

        // sets the spend money of day
        double allSpendOfDay = SpendItem.getSpendOfDay();
        mSpendOfDay.setText(String.valueOf(allSpendOfDay));

        // sets the spend money of month
        double allSpendOfMonth = SpendItem.getSpendOfMonth();
        mSpendMonth.setText(String.valueOf(allSpendOfMonth));

        // sets the spend money of month
        double allSIncomeOfMonth = IncomeItem.getIncomeOfMonth();
        mIncomeMonth.setText(String.valueOf(allSIncomeOfMonth));

        // sets the balance of all time
        double allSumSpend = SpendItem.getAllSum();
        double allSumIncome = IncomeItem.getAllSum();
        mAllBalance.setText(String.valueOf(allSumIncome - allSumSpend));
    }

    private void initView() {
        mSpendWeek = (TextView) mView.findViewById(R.id.spend_week);
        mSpendMonth = (TextView) mView.findViewById(R.id.spend_month);
        mSpendOfDay = (TextView) mView.findViewById(R.id.spend_today);
        mIncomeMonth = (TextView) mView.findViewById(R.id.income_month);
        mAllBalance = (TextView) mView.findViewById(R.id.balance_all_time);

        mAddSpendImg = (ImageButton) mView.findViewById(R.id.add_spend_button);
        mAddIncomeImg = (ImageButton) mView.findViewById(R.id.add_income_button);
        mViewAllSpendButton = (ImageButton) mView.findViewById(R.id.allSpendButton);
        mViewBalanceImg = (ImageButton) mView.findViewById(R.id.view_balance_button);
        mViewAllIncomeButton = (ImageButton) mView.findViewById(R.id.allIncomeButton);
    }

    private void setClickListeners() {
        mFragmentTransaction = getFragmentManager().beginTransaction();

        // click listener on the imageButton add new income money
        mAddIncomeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment = new NewEventFragment();
                Bundle bundle = new Bundle();
                bundle.putString("tagEvent", Constants.CATEGORY_EVENT_INCOME);
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
                bundle.putString("tagEvent", Constants.CATEGORY_EVENT_SPEND);
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
                mFragment = new AllEventFragment();
                Bundle bundle = new Bundle();
                bundle.putString("tagEvent", Constants.CATEGORY_EVENT_INCOME);
                mFragment.setArguments(bundle);
                mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                mFragmentTransaction.replace(R.id.custom_fragment, mFragment).commit();
            }
        });

        // click listener on the imageButton view all list spend money
        mViewAllSpendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment = new AllEventFragment();
                Bundle bundle = new Bundle();
                bundle.putString("tagEvent", Constants.CATEGORY_EVENT_SPEND);
                mFragment.setArguments(bundle);
                mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                mFragmentTransaction.replace(R.id.custom_fragment, mFragment).commit();
            }
        });
    }

}
