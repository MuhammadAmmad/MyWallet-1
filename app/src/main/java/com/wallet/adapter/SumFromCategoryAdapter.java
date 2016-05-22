package com.wallet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wallet.R;

import com.wallet.model.CategorySumItem;

import java.util.List;

public class SumFromCategoryAdapter extends ArrayAdapter<Void> {

    private LayoutInflater mLInflater;
    private IItemEventListener mEventListener;
    private List<CategorySumItem> mCategorySumItem;


    public interface IItemEventListener {
        void onClickRow(int position);
    }

    public void setEventClickListener(IItemEventListener listener) {
        mEventListener = listener;
    }

    public SumFromCategoryAdapter(Context context, List<CategorySumItem> categorySumItem) {
        super(context, 0);

        mCategorySumItem = categorySumItem;
        mLInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mCategorySumItem.size();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        if (view == null)
            view = mLInflater.inflate(R.layout.adapter_item_balance, parent, false);

        ((TextView) view.findViewById(R.id.monthName)).setText(mCategorySumItem.get(position).getCategory());
        ((TextView) view.findViewById(R.id.sumOfMonth)).setText(mCategorySumItem.get(position).getSum());

        view.findViewById(R.id.mainLinear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventListener != null) {
                    mEventListener.onClickRow(position);
                }
            }
        });

        return view;
    }

}
