package com.wallet.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.wallet.Constants;
import com.wallet.R;
import com.wallet.fragment.NewEventFragment;
import com.wallet.model.IncomeTable;
import com.wallet.model.SpendTable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class AdapterCategoryIncomeDetail extends BaseAdapter implements Constants{

    List<IncomeTable> mIncomeTable;
    LayoutInflater mLInflater;
    Context mContext;

    public AdapterCategoryIncomeDetail(Context context, List<IncomeTable> incomeTable) {
        mContext = context;
        mIncomeTable = incomeTable;
        mLInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mIncomeTable.size();
    }

    @Override
    public Object getItem(int position) {
        return mIncomeTable.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mIncomeTable.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = mLInflater.inflate(R.layout.adapter_item_category_info, parent, false);

        ((TextView)view.findViewById(R.id.dateTime)).setText(getDateTime(mIncomeTable.get(position).dateTime));
        ((TextView)view.findViewById(R.id.sumOfOperation)).setText(String.valueOf(mIncomeTable.get(position).cash));
        ((TextView)view.findViewById(R.id.comment)).setText(mIncomeTable.get(position).comment);
        ImageButton menuButton = (ImageButton) view.findViewById(R.id.imageSomethingChange);
        menuButton.setTag(position);
        menuButton.setOnClickListener(getClickListenerButtonMenu());
        return view;
    }

    public  static  String getDateTime(long milliSeconds){
        String dateFormat = DATE_FORMAT + " " + TIME_FORMAT;

        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private View.OnClickListener getClickListenerButtonMenu() {
        return new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                final int position = (int) v.getTag();
                final long idInData = mIncomeTable.get(position).getId();

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                alertDialog.setTitle(R.string.select_action);

                // by clicking button "delete", calls new dialog box to confirm deletion
                alertDialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(alertDialog.getContext());
                        alertDialog2.setMessage(R.string.confirm_deletion_action);
                        alertDialog2.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //...
                            }
                        });
                        alertDialog2.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new Delete().from(SpendTable.class).where("Id = ?", idInData).execute();
                                mIncomeTable.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                        alertDialog2.show();
                    }
                });

                alertDialog.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //...
                    }
                });

                alertDialog.setNegativeButton(R.string.change, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        FragmentTransaction fragmentTransaction = ((FragmentActivity)v.getContext()).getSupportFragmentManager().beginTransaction();
                        Fragment fragment = new NewEventFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("tagEvent", CATEGORY_EVENT_INCOME);
                        bundle.putString("id", String.valueOf(idInData));
                        fragment.setArguments(bundle);
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.replace(R.id.custom_fragment, fragment).commit();
                    }
                });
                alertDialog.show();
            }
        };
    }
}
