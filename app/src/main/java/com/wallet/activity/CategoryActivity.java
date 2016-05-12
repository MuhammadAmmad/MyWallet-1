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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CategoryActivity extends AppCompatActivity implements Constants {

    public SharedPreferences settingsCategory;
    String mEventCategory;
    ItemCategoryAdapter mCategoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Accepts name of the class that caused current activity, and
        // indicates name of file that stores category with which to work
        mEventCategory = getIntent().getExtras().getString("tagEvent");
        assert mEventCategory != null;
        if(mEventCategory.equals(CATEGORY_EVENT_SPEND)) {
            settingsCategory = getSharedPreferences(APP_PREFERENCES_CATEGORY_SPEND, Context.MODE_PRIVATE);
        } else if (mEventCategory.equals(CATEGORY_EVENT_INCOME)){
            settingsCategory = getSharedPreferences(APP_PREFERENCES_CATEGORY_INCOME, Context.MODE_PRIVATE);
        }

        Map<String, ?> allEntries = settingsCategory.getAll();
        List<String> list = new ArrayList<String>() ;
        for (Map.Entry<String, ?> entry : allEntries.entrySet()){
            list.add(entry.getValue().toString());
        }

        mCategoryAdapter = new ItemCategoryAdapter(this, list, mEventCategory);

        ListView lvMain = (ListView)findViewById(R.id.lvMain);
        lvMain.setAdapter(mCategoryAdapter);
    }


    public void onButtonAddNewCategoryClick(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        final EditText newCategory = new EditText(v.getContext());
        newCategory.setSingleLine(true);
        newCategory.setPadding(20, 5, 20, 5);
        alert.setTitle(R.string.enter_new_category_name);

        alert.setView(newCategory);

        // By clicking on the save button sends the entered data to the basic activity
        alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newCatNameTxt = newCategory.getText().toString();
                if (newCatNameTxt.length() > 0) {

                    Toast.makeText(getApplicationContext(), getString(R.string.create_new_category) +
                            newCatNameTxt, Toast.LENGTH_SHORT).show();

                    settingsCategory.edit().putString(newCatNameTxt, newCatNameTxt).apply();

                    Intent intent = new Intent();
                    intent.putExtra(CATEGORY_NAME, newCatNameTxt);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });

        alert.setNegativeButton(R.string.cancel, null);
        alert.show();
    }

}