package com.wallet.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.activeandroid.query.Delete;
import com.wallet.Constants;
import com.wallet.R;
import com.wallet.adapter.AdapterCategoryIncomeDetail;
import com.wallet.adapter.AdapterCategorySpendDetail;
import com.wallet.model.IncomeItem;
import com.wallet.model.SpendItem;

import java.util.List;


public class
CategoryInfoFragment extends Fragment {

    private int mPosition;
    private long mIdInData;
    private List<SpendItem> mSpendItem;
    private String mEventCategory = "";
    private List<IncomeItem> mIncomeItem;
    private AdapterCategorySpendDetail mSpendItemDetail;
    private AdapterCategoryIncomeDetail mIncomeItemDetail;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_info, container, false);

        Bundle bundle = getArguments();
        String categoryName = bundle.getString("catName");
        mEventCategory = bundle.getString("tagEvent");

        ListView lvMain = (ListView) view.findViewById(R.id.lvCatInfoMain);

        if (mEventCategory.equals(Constants.CATEGORY_EVENT_SPEND)) {
            mSpendItem = SpendItem.getAllByName(categoryName);
            mSpendItemDetail = new AdapterCategorySpendDetail(view.getContext(), mSpendItem);
            lvMain.setAdapter(mSpendItemDetail);

            mSpendItemDetail.setEventClickListener(new AdapterCategorySpendDetail.IItemEventListener() {
                @Override
                public void onClickSomeChange(int position) {
                    showMenuAlert(position);
                }
            });
        } else if (mEventCategory.equals(Constants.CATEGORY_EVENT_INCOME)) {
            mIncomeItem = IncomeItem.getAllByName(categoryName);
            mIncomeItemDetail = new AdapterCategoryIncomeDetail(view.getContext(), mIncomeItem);
            lvMain.setAdapter(mIncomeItemDetail);

            mIncomeItemDetail.setEventClickListener(new AdapterCategoryIncomeDetail.IItemEventListener() {
                @Override
                public void onClickSomeChange(int position) {
                    showMenuAlert(position);
                }
            });
        }

        return view;
    }

    private void showMenuAlert(int position) {
        mPosition = position;
        if (mEventCategory.equals(Constants.CATEGORY_EVENT_SPEND)) {
            mIdInData = mSpendItem.get(position).getId();
        } else if (mEventCategory.equals(Constants.CATEGORY_EVENT_INCOME)) {
            mIdInData = mIncomeItem.get(position).getId();
        }

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.select_action)
                .setPositiveButton(R.string.delete, onClickListenerPositiveButton)
                .setNeutralButton(R.string.cancel, null)
                .setNegativeButton(R.string.change, onClickListenerNegativeButton)
                .show();
    }


    private OnClickListener onClickListenerPositiveButton = new OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            // opens a dialog box to confirm the deletion of records
            new AlertDialog.Builder(getContext())
                    .setMessage(R.string.confirm_deletion_action)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.yes, onClickDeleteRecord)
                    .show();
        }
    };

    private OnClickListener onClickDeleteRecord = new OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            if (mEventCategory.equals(Constants.CATEGORY_EVENT_SPEND)) {
                new Delete().from(SpendItem.class).where("Id = ?", mIdInData).execute();
                mSpendItem.remove(mPosition);
                mSpendItemDetail.notifyDataSetChanged();
            } else if (mEventCategory.equals(Constants.CATEGORY_EVENT_INCOME)) {
                new Delete().from(IncomeItem.class).where("Id = ?", mIdInData).execute();
                mIncomeItem.remove(mPosition);
                mIncomeItemDetail.notifyDataSetChanged();
            }
        }
    };

    // sends to other fragment data of event to be able to edit it
    private OnClickListener onClickListenerNegativeButton = new OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            Fragment fragment = new NewEventFragment();
            Bundle bundle = new Bundle();
            bundle.putString("tagEvent", mEventCategory);
            bundle.putString("id", String.valueOf(mIdInData));
            fragment.setArguments(bundle);
            ((FragmentActivity) getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.custom_fragment, fragment)
                    .commit();
        }
    };


}
