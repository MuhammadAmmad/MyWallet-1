package com.wallet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.wallet.Constants;
import com.wallet.R;
import com.wallet.adapter.ItemCategoryAdapter;
import com.wallet.model.IncomeItem;
import com.wallet.model.SpendItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CategoryActivity extends AppCompatActivity {

    private SharedPreferences mShared;
    private String mEventCategory = "";
    private ItemCategoryAdapter mCategoryAdapter;
    private List<String> mCategories = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Accepts name of the class that caused current activity, and
        // indicates name of file that stores category with which to work
        mEventCategory = getIntent().getExtras().getString("tagEvent");
        if (mEventCategory.equals(Constants.CATEGORY_EVENT_SPEND)) {
            mShared = getSharedPreferences(Constants.PREFERENCES_SPEND, Context.MODE_PRIVATE);
        } else if (mEventCategory.equals(Constants.CATEGORY_EVENT_INCOME)) {
            mShared = getSharedPreferences(Constants.PREFERENCES_INCOME, Context.MODE_PRIVATE);
        }

        Map<String, ?> allEntries = mShared.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            mCategories.add(entry.getValue().toString());
        }

        findViewById(R.id.buttonAddNewCategory).setOnClickListener(addNewCategoryListener);

        mCategoryAdapter = new ItemCategoryAdapter(this, mCategories);
        ListView lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.setAdapter(mCategoryAdapter);


        mCategoryAdapter.setEventClickListener(new ItemCategoryAdapter.IItemEventListener() {
            @Override
            public void onClickDelete(int position) {
                showCategoryDeleteAlert(position);
            }

            @Override
            public void onClickChange(int position) {
                showCategoryChangeAlert(position);
            }

            @Override
            public void onClickRow(int position) {
                selectCategory(position);
            }
        });
    }

    private void selectCategory(int position) {
        String categoryName = mCategories.get(position);

        Toast.makeText(CategoryActivity.this, getString(R.string.you_choosed_category)
                + categoryName, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.putExtra(Constants.CATEGORY_NAME, categoryName);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void showCategoryChangeAlert(final int position) {
        final String currViewTxt = mCategories.get(position);
        final EditText edittext = new EditText(this);
        edittext.setText(currViewTxt);
        new AlertDialog.Builder(this)
                .setMessage(R.string.enter_new_category_name)
                .setTitle(R.string.change_category)
                .setView(edittext)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newNameCategory = edittext.getText().toString();
                        if (newNameCategory.length() > 0) {

                            // change category name in data base
                            if (mEventCategory.equals(Constants.CATEGORY_EVENT_SPEND)) {
                                SpendItem spendItem = new SpendItem();
                                spendItem.changeCategoryName(currViewTxt, newNameCategory);
                            } else if (mEventCategory.equals(Constants.CATEGORY_EVENT_INCOME)) {
                                IncomeItem incomeItem = new IncomeItem();
                                incomeItem.changeCategoryName(currViewTxt, newNameCategory);
                            }

                            // change name in shared preferences
                            mShared.edit().remove(currViewTxt).apply();
                            mShared.edit().putString(newNameCategory, newNameCategory).apply();

                            mCategories.set(position, newNameCategory);
                            mCategoryAdapter.notifyDataSetChanged();

                            Toast.makeText(CategoryActivity.this, newNameCategory, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }


    private void showCategoryDeleteAlert(final int position) {
        final String category = mCategories.get(position);
        new AlertDialog.Builder(this)
                .setTitle(R.string.attention)
                .setMessage(R.string.confirm_deletion)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mShared.edit().remove(category).apply();
                        Toast.makeText(CategoryActivity.this, getString(R.string.category) + " " + category + " " + getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                        mCategories.remove(category);
                        mCategoryAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();

    }

    private View.OnClickListener addNewCategoryListener = new View.OnClickListener() {
        public void onClick(View v) {
            final EditText newCategory = new EditText(v.getContext());
            newCategory.setSingleLine(true);
            newCategory.setPadding(20, 5, 20, 5);
            new AlertDialog.Builder(v.getContext())
                    .setTitle(R.string.enter_new_category_name)
                    .setView(newCategory)
                    // By clicking on the save button sends the entered data to the basic activity
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newCatNameTxt = newCategory.getText().toString();
                            if (newCatNameTxt.length() > 0) {

                                Toast.makeText(getApplicationContext(), getString(R.string.create_new_category) +
                                        newCatNameTxt, Toast.LENGTH_SHORT).show();

                                mShared.edit().putString(newCatNameTxt, newCatNameTxt).apply();

                                Intent intent = new Intent();
                                intent.putExtra(Constants.CATEGORY_NAME, newCatNameTxt);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
    };


}