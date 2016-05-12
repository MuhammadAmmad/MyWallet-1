package com.wallet.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.wallet.Constants;
import com.wallet.R;
import com.wallet.model.IncomeTable;
import com.wallet.model.SpendTable;

import java.util.List;


public class ItemCategoryAdapter extends BaseAdapter implements Constants {

    Context mContext;
    List<String> mCategoryNameList;
    LayoutInflater mLInflater;
    int mCurrentId;
    public SharedPreferences settingsCategory;
    public String mEventCategory;

    public ItemCategoryAdapter(Context context, List<String> categoryName, String eventCategory) {
        mContext = context;
        mCategoryNameList = categoryName;
        mLInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ActiveAndroid.initialize(mContext);

        mEventCategory = eventCategory;
        if (mEventCategory.equals(CATEGORY_EVENT_SPEND)) {
            settingsCategory = this.mContext.getSharedPreferences(APP_PREFERENCES_CATEGORY_SPEND, Context.MODE_PRIVATE);
        } else if (mEventCategory.equals(CATEGORY_EVENT_INCOME)) {
            settingsCategory = this.mContext.getSharedPreferences(APP_PREFERENCES_CATEGORY_INCOME, Context.MODE_PRIVATE);
        }
    }

    @Override
    public int getCount() {
        return mCategoryNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategoryNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = mLInflater.inflate(R.layout.adapter_item_category, parent, false);
        }

        String name = mCategoryNameList.get(position);

        ((ImageButton) view.findViewById(R.id.deleteImg)).setImageResource(R.mipmap.ic_delete);
        ((TextView) view.findViewById(R.id.catName)).setText(name);

        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.mainLinear);
        relativeLayout.setTag(position);
        relativeLayout.setOnClickListener(categoryRowClickListener());

        ImageButton imageButtonChange = (ImageButton) view.findViewById(R.id.changeImg);
        imageButtonChange.setImageResource(R.mipmap.ic_change);
        imageButtonChange.setTag(position);
        imageButtonChange.setOnClickListener(onChangeButtonClickListener());

        ImageButton imageButtonDelete = (ImageButton) view.findViewById(R.id.deleteImg);
        imageButtonDelete.setImageResource(R.mipmap.ic_delete);
        imageButtonDelete.setTag(position);
        imageButtonDelete.setOnClickListener(onDeleteButtonClickListener());

        return view;
    }

    private View.OnClickListener categoryRowClickListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int position = Integer.valueOf(v.getTag().toString());
                String categoryName = mCategoryNameList.get(position);

                Toast.makeText(v.getContext(), mContext.getString(R.string.you_choosed_category)
                        + categoryName, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra(CATEGORY_NAME, categoryName);
                ((Activity) v.getContext()).setResult(Activity.RESULT_OK, intent);
                ((Activity) v.getContext()).finish();
            }
        };
    }

    private View.OnClickListener onChangeButtonClickListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                mCurrentId = Integer.valueOf(v.getTag().toString());

                final String currViewTxt = mCategoryNameList.get(mCurrentId);

                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                final EditText edittext = new EditText(v.getContext());
                edittext.setText(currViewTxt);
                alert.setMessage(R.string.enter_new_category_name);
                alert.setTitle(R.string.change_category);

                alert.setView(edittext);

                alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newNameCategory = edittext.getText().toString();
                        if (newNameCategory.length() > 0) {

                            // change category name in data base
                            if (mEventCategory.equals(CATEGORY_EVENT_SPEND)) {
                                SpendTable spendTable = new SpendTable();
                                spendTable.changeCategoryName(currViewTxt, newNameCategory);
                            } else if (mEventCategory.equals(CATEGORY_EVENT_INCOME)) {
                                IncomeTable incomeTable = new IncomeTable();
                                incomeTable.changeCategoryName(currViewTxt, newNameCategory);
                            }

                            // change name in shared preferences
                            settingsCategory.edit().remove(currViewTxt).apply();
                            settingsCategory.edit().putString(newNameCategory, newNameCategory).apply();

                            mCategoryNameList.set(mCurrentId, newNameCategory);
                            notifyDataSetChanged();

                            Toast.makeText(mContext.getApplicationContext(), newNameCategory, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNegativeButton(R.string.cancel, null);
                alert.show();
            }
        };
    }

    private View.OnClickListener onDeleteButtonClickListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurrentId = Integer.valueOf(v.getTag().toString());

                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle(R.string.attention);
                alert.setMessage(R.string.confirm_deletion);

                alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String currViewTxt = mCategoryNameList.get(mCurrentId);
                        settingsCategory.edit().remove(currViewTxt).apply();


                        Toast.makeText(mContext.getApplicationContext(),
                                mContext.getString(R.string.category) + " " + mCategoryNameList.get(mCurrentId)
                                        + " " + mContext.getString(R.string.deleted), Toast.LENGTH_SHORT).show();

                        mCategoryNameList.remove(mCurrentId);
                        notifyDataSetChanged();
                    }
                });

                alert.setNegativeButton(R.string.cancel, null);
                alert.show();
            }
        };
    }
}
