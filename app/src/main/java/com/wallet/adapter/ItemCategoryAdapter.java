package com.wallet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wallet.R;

import java.util.List;


public class ItemCategoryAdapter extends ArrayAdapter<Void> {

    private LayoutInflater mLInflater;
    private List<String> mCategoryNameList;
    private IItemEventListener mEventListener;


    public interface IItemEventListener {
        void onClickDelete(int position);
        void onClickChange(int position);
        void onClickRow(int position);
    }

    public void setEventClickListener(IItemEventListener listener) {
        mEventListener = listener;
    }

    public ItemCategoryAdapter(Context context, List<String> categoryName) {
        super(context, 0);

        mCategoryNameList = categoryName;
        mLInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return mCategoryNameList.size();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        if (view == null)
            view = mLInflater.inflate(R.layout.adapter_item_category, parent, false);

        String name = mCategoryNameList.get(position);
        ((TextView) view.findViewById(R.id.catName)).setText(name);


        view.findViewById(R.id.mainLinear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventListener != null) {
                    mEventListener.onClickRow(position);
                }
            }
        });

        view.findViewById(R.id.changeImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventListener != null) {
                    mEventListener.onClickChange(position);
                }
            }
        });

        view.findViewById(R.id.deleteImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventListener != null) {
                    mEventListener.onClickDelete(position);
                }
            }
        });

        return view;
    }

}
