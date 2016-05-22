package com.wallet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wallet.Constants;
import com.wallet.R;
import com.wallet.model.SpendItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class AdapterCategorySpendDetail extends ArrayAdapter<Void> {

    private Context mContext;
    private LayoutInflater mLInflater;
    private List<SpendItem> mSpendItem;
    private SimpleDateFormat mDateFormatter;
    private IItemEventListener mEventListener;


    public interface IItemEventListener {
        void onClickSomeChange(int position);
    }

    public void setEventClickListener(IItemEventListener listener) {
        mEventListener = listener;
    }

    public AdapterCategorySpendDetail(Context context, List<SpendItem> spendItem) {
        super(context, 0);

        mContext = context;
        mSpendItem = spendItem;
        mLInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT + " " + Constants.TIME_FORMAT);
    }

    @Override
    public int getCount() {
        return mSpendItem.size();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null)
            view = mLInflater.inflate(R.layout.adapter_item_category_info, parent, false);

        SpendItem currentItem = mSpendItem.get(position);

        ((TextView) view.findViewById(R.id.dateTime)).setText(getTimeInSimpleFormat(currentItem.getDateTime()));
        ((TextView) view.findViewById(R.id.sumOfOperation)).setText(String.valueOf(currentItem.getCash()));
        ((TextView) view.findViewById(R.id.comment)).setText(currentItem.getComment());

        ImageView checkImg = (ImageView) view.findViewById(R.id.checkImage);
        if (!currentItem.getPhoto().isEmpty()) {
            Picasso.with(mContext.getApplicationContext().getApplicationContext()).load(currentItem.getPhoto()).into(checkImg);
        } else {
            checkImg.setImageDrawable(null);
        }

        view.findViewById(R.id.imageSomethingChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventListener != null) {
                    mEventListener.onClickSomeChange(position);
                }
            }
        });

        return view;
    }

    public String getTimeInSimpleFormat(long milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return mDateFormatter.format(calendar.getTime());
    }

}