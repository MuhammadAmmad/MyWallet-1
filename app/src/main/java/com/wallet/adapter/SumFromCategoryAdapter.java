package com.wallet.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wallet.R;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.wallet.fragment.CategoryInfoFragment;

import java.util.List;

public class SumFromCategoryAdapter extends BaseAdapter{
    Context mContext;
    List<String> mCatName;
    List<String> mCatSum;
    LayoutInflater mLInflater;
    String mEventCategory;

    public SumFromCategoryAdapter(Context context, List<String> name, List<String> sum, String eventCategory) {
        mContext = context;
        mCatName = name;
        mCatSum = sum;
        mEventCategory = eventCategory;
        mLInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mCatName.size();
    }

    @Override
    public Object getItem(int position) {
        mCatName.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mLInflater.inflate(R.layout.adapter_item_balance, parent, false);
        }

        ((TextView)view.findViewById(R.id.monthName)).setText(mCatName.get(position));
        ((TextView)view.findViewById(R.id.sumOfMonth)).setText(mCatSum.get(position));

        RelativeLayout layout = (RelativeLayout)view.findViewById(R.id.mainLinear);
        layout.setTag(position);
        layout.setOnClickListener(categoryRowClickListener());

        return view;
    }

    private View.OnClickListener categoryRowClickListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int position = Integer.valueOf(v.getTag().toString());

                FragmentTransaction fragmentTransaction = ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction();

                CategoryInfoFragment fragment = new CategoryInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("catName", mCatName.get(position));
                bundle.putString("tagEvent", mEventCategory);
                fragment.setArguments(bundle);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.replace(R.id.custom_fragment, fragment).commit();

            }
        };
    }

}
