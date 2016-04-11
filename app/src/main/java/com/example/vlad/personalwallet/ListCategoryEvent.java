package com.example.vlad.personalwallet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vlad on 31.03.2016.
 *
 * This class takes name of a category and displays a list of all
 * detailed information about this category
 */

public class ListCategoryEvent extends AppCompatActivity {

    // type category, consumption or income
    private static final String CATEGORY_TYPE_CONSUMPTION = "consumption";
    private static final String CATEGORY_TYPE_INCOME = "income";

    private static String mTypeCategory = "";

    // Name of category which it is necessary display information
    String mCatName;

    // Click listener for button menu
    View.OnClickListener mClickListenerMenuButton;

    DatabaseHelper mDatabaseHelper;
    SQLiteDatabase mSqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCatName = getIntent().getExtras().getString("catName");
        setTitle(mCatName);

        mTypeCategory = getIntent().getExtras().getString("ConsumptionOrIncome");
    }

    @Override
    public void onResume(){
        super.onResume();
        setContentView(R.layout.list_category_costs);

        createLayout();
    }

    /**
     * Select all records required type categories (income or expenditure).
     * Creates layout with all records of category
     */
    private void createLayout() {
        mDatabaseHelper = new DatabaseHelper(this);
        mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();

        // Map where key is the ID of database, and a value is ordinal number on the page
        Map<String, Integer> menuButtonMap = new HashMap<>();

        // ArrayList of all tableLayout
        List<TableLayout> tableLayoutList = new ArrayList<>();

        String categoryColumn = "";
        if (mTypeCategory.equals(CATEGORY_TYPE_CONSUMPTION)){
            categoryColumn = DatabaseHelper.CATEGORY_SPEND_COLUMN;
        } else if (mTypeCategory.equals(CATEGORY_TYPE_INCOME)){
            categoryColumn = DatabaseHelper.CATEGORY_INCOME_COLUMN;
        }

        String query = "SELECT * FROM " + DatabaseHelper.DATABASE_TABLE_WALLET
                + " WHERE " + categoryColumn + " IS NOT NULL ORDER BY "
                + DatabaseHelper.DATE_COLUMN + " DESC";
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);

        TableLayout mainTableLayout = (TableLayout) findViewById(R.id.container_costs_detail);

        int i = 0;
        while (cursor.moveToNext()) {
            String currentCatName = cursor.getString(cursor.getColumnIndex(categoryColumn));
            if (currentCatName.equals(mCatName)) {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                TableLayout tableLayout = getTableLayout(id);
                TableRow tableRow1 = makeTableRow();
                TableRow tableRow2 = makeTableRow();
                TextView date = getTextViewDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE_COLUMN)));
                TextView cash = getTextViewCash(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.CASH_COLUMN)));
                TextView comment = getTextViewComment(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COMMENT_COLUMN)));
                ImageButton buttonMenu = makeButtonMenu(i);

                buttonMenu.setId(id);

                mClickListenerMenuButton = getClickListenerButtonMenu();
                buttonMenu.setOnClickListener(mClickListenerMenuButton);

                String idStr = String.valueOf(id);
                menuButtonMap.put(idStr, i);

                tableRow1.addView(date);
                tableRow1.addView(cash);

                tableRow2.addView(comment);
                tableRow2.addView(buttonMenu);

                tableLayout.addView(tableRow1);
                tableLayout.addView(tableRow2);

                tableLayoutList.add(i, tableLayout);
                mainTableLayout.addView(tableLayout);

                i++;
            }
        }
        cursor.close();
    }

    /**
     * When clicking on the menu button brings up a dialog box
     * to select the action (to cancel, modify, or delete)
     */
    private View.OnClickListener getClickListenerButtonMenu() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final int idFromDataBase = v.getId();

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                alertDialog.setTitle("Выберите действие");

                // by clicking button "delete", calls new dialog box to confirm deletion
                alertDialog.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(alertDialog.getContext());
                        alertDialog2.setMessage("Вы уверены что хотите удалить эту запись?");
                        alertDialog2.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //...
                            }
                        });
                        alertDialog2.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mDatabaseHelper = new DatabaseHelper(alertDialog2.getContext());
                                mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
                                mSqLiteDatabase.delete(DatabaseHelper.DATABASE_TABLE_WALLET,
                                        DatabaseHelper.COLUMN_ID + " = " + idFromDataBase, null);

                                onResume();
                            }
                        });
                        alertDialog2.show();
                    }
                });

                alertDialog.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //...
                    }
                });

                alertDialog.setNegativeButton("Изменить", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ListCategoryEvent.this, ChangeEvent.class);
                        intent.putExtra("recordId", idFromDataBase);
                        startActivity(intent);
                    }
                });
                alertDialog.show();
            }
        };
    }

    /**
     * Creates new imageButton "menu", and set parameters
     *
     * @param count - id for button
     * @return new imageButton
     */
    private ImageButton makeButtonMenu(int count) {
        int iconName = R.mipmap.icon_sandwich;
        TableRow.LayoutParams imagePram = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f);
        imagePram.setMargins(0, 0, 0, 0);
        imagePram.gravity = Gravity.END;
        imagePram.span = 1;

        final ImageButton imageButtonChange = new ImageButton(this);
        imageButtonChange.setImageResource(iconName);
        imageButtonChange.setBackgroundColor(Color.WHITE);
        imageButtonChange.setPadding(0, 0, 0, 0);
        imageButtonChange.setId(count);
        imageButtonChange.setLayoutParams(imagePram);

        return imageButtonChange;
    }

    /**
     * Creates new textView "comment", and set parameters
     *
     * @return new textView
     */
    private TextView getTextViewComment(String text) {
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.9f);
        lp.column = 1;
        lp.span = 1;

        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(13);
        textView.setTextColor(Color.GRAY);
        textView.setLayoutParams(lp);

        return textView;
    }

    /**
     * Creates new textView "sum", and set parameters
     *
     * @return new textView
     */
    private TextView getTextViewCash(Double sum) {
        TableRow.LayoutParams lp = new TableRow.LayoutParams();
        lp.weight = 1;
        lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.4f);
        lp.column = 2;
        lp.span = 1;

        TextView textView = new TextView(this);
        textView.setText(String.valueOf(sum) + " грн");
        textView.setTextSize(14);
        textView.setGravity(Gravity.END);
        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(lp);

        return textView;
    }

    /**
     * Creates new textView "date", and set parameters
     *
     * @return new textView
     */
    private TextView getTextViewDate(String text) {
        TableRow.LayoutParams lp = new TableRow.LayoutParams();
        lp.weight = 1;
        lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.6f);
        lp.column = 1;

        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(14);
        textView.setTextColor(Color.GRAY);
        textView.setLayoutParams(lp);

        return textView;
    }

    /**
     * Create new layout, and set parameters
     *
     * @param count - id for tableLayout
     * @return new tableLayout
     */
    private TableLayout getTableLayout(int count) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFFFFFFFF);
        gd.setStroke(1, 0xFFA5A5A5);

        TableLayout.LayoutParams tableLayoutParams =
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        tableLayoutParams.setMargins(-1, -1, -1, 10);

        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setPadding(20, 20, 20, 20);
        tableLayout.setBackground(gd);
        tableLayout.setId(count);
        tableLayout.setLayoutParams(tableLayoutParams);
        return tableLayout;
    }

    /**
     * Create new tableRow, and set parameters
     *
     * @return new tableRow
     */
    private TableRow makeTableRow() {
        TableLayout.LayoutParams tableRowParams =
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);

        TableRow tableRow = new TableRow(this);
        tableRow.setWeightSum(1);
        tableRow.setLayoutParams(tableRowParams);

        return tableRow;
    }
/*
    @Override
    public void onBackPressed() {
        if (mTypeCategory.equals(CATEGORY_TYPE_CONSUMPTION)){
            Intent setIntent = new Intent(ListCategoryEvent.this, AllCosts.class);
            startActivity(setIntent);
        } else if (mTypeCategory.equals(CATEGORY_TYPE_INCOME)){
            Intent setIntent = new Intent(ListCategoryEvent.this, AllIncome.class);
            startActivity(setIntent);
        }

    }
    */

}