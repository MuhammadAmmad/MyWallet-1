package com.example.vlad.personalwallet;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vlad on 30.03.2016.
 *
 * This class selects from the database all records of expenditure.
 * It allows you to view the sum of all expenses in general and individually
 * in each category. Just by clicking on a category name to call other activity
 * in which you can see a list of all the selected expense category
 */
public class AllCosts extends AppCompatActivity {

    // type category, consumption or income
    private static final String CONSUMPTION_OR_INCOME = "consumption";

    // variable to indicate current ID
    private int mCurrentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        setContentView(R.layout.all_costs);

        // Map of all transactions recorded in database, where category name is key, and sum is value
        Map<String, Double> allCategoryAndSum = getAllCategoryAndSum();
        createLayout(allCategoryAndSum);

    }

    /**
     * Reads from database all categories of expenditure and the amount.
     * Returns map where key is the name of category, and value is the total amount of costs in this category.
     */
    private Map<String, Double> getAllCategoryAndSum() {
        Map<String, Double> catAndSum = new HashMap<>();

        DatabaseHelper mDatabaseHelper = new DatabaseHelper(this);
        SQLiteDatabase mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();

        String query = "SELECT " + DatabaseHelper.CASH_COLUMN + ", " + DatabaseHelper.CATEGORY_SPEND_COLUMN +
                " " + " FROM " + DatabaseHelper.DATABASE_TABLE_WALLET
                + " WHERE " + DatabaseHelper.CATEGORY_SPEND_COLUMN + " IS NOT NULL";

        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);

        while(cursor.moveToNext()){
            String catName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CATEGORY_SPEND_COLUMN));
            Double sum = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.CASH_COLUMN));
            if (catAndSum.containsKey(catName)){
                Double lastSum = catAndSum.get(catName);
                catAndSum.put(catName, lastSum + sum);
            } else {
                catAndSum.put(catName, sum);
            }
        }
        cursor.close();
        return catAndSum;
    }

    /**
     * Automatically creates the layout for all categories
     */
    private void createLayout(Map<String, Double> allCategoryAndSum) {
        TableLayout tableLayout = (TableLayout)findViewById(R.id.container_costs);
        TextView allCashView = (TextView)findViewById(R.id.ALL_CASH_CONSUMPTION);

        // ArrayList of unique names category. Their index into array coincides with sequence number on the screen
        List<String> categoryList = new ArrayList<>();

        // ArrayList of all rows with the information on the screen
        List<TableRow> tableRowList = new ArrayList<>();

        // Click listener for row
        View.OnClickListener clickListenerRow;

        int i = 0;
        Double allCash = 0.0;
        for (Map.Entry<String, Double> entry : allCategoryAndSum.entrySet()){

            String categoryName = entry.getKey();
            Double sum = entry.getValue();
            allCash += sum;

            TableRow tableRow = makeTableRow(i);
            TextView textViewCategory = makeCategoryView(i, categoryName);
            TextView textViewSum = makeSumFromCategory(i, sum.toString());
            clickListenerRow = makeClickListenerRow(categoryList);

            categoryList.add(i, textViewCategory.getText().toString());
            tableRowList.add(i, tableRow);
            tableRow.setOnClickListener(clickListenerRow);

            tableRow.addView(textViewCategory);
            tableRow.addView(textViewSum);

            tableLayout.addView(tableRow);
            i++;
        }
        allCashView.setText(String.valueOf(allCash));
    }

    /**
     * By clicking on a row, calls a new activity which transmits
     * name of the category that was in this line
     */
    private View.OnClickListener makeClickListenerRow(final List<String> categoryList) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurrentId = v.getId();

                Intent intent = new Intent(AllCosts.this, ListCategoryEvent.class);
                intent.putExtra("catName", String.valueOf(categoryList.get(mCurrentId)));
                intent.putExtra("ConsumptionOrIncome", CONSUMPTION_OR_INCOME);
                startActivity(intent);
            }
        };
    }

    /**
     * Creates new textView for sum of category, and set parameters
     *
     * @param count - id for textView
     * @param sum - total amount of the category
     * @return - new textView
     */
    private TextView makeSumFromCategory(int count, String sum) {
        TableRow.LayoutParams lp = new TableRow.LayoutParams();
        lp.weight = 1;
        lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.4f);

        TextView textView = new TextView(this);
        textView.setText(sum);
        textView.setTextSize(23);
        textView.setTextColor(Color.BLACK);
        textView.setId(count);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.END);

        return textView;
    }

    /**
     * Creates new textView for category name, and set parameters
     *
     * @param count - id for category name
     * @param categoryName - category name
     * @return - new textView
     */
    private TextView makeCategoryView(int count, String categoryName) {
        TableRow.LayoutParams lp = new TableRow.LayoutParams();
        lp.weight = 1;
        lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.6f);

        TextView textView = new TextView(this);
        textView.setText(categoryName);
        textView.setTextSize(23);
        textView.setTextColor(Color.BLACK);
        textView.setId(count);
        textView.setLayoutParams(lp);

        return textView;
    }

    /**
     * Creates new tableRow, and set parameters
     *
     * @param count - id for tableRow
     * @return - new tableRow
     */
    private TableRow makeTableRow(int count) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFFFFFFFF);
        gd.setStroke(1, 0xFFA5A5A5);

        TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
        tableRowParams.setMargins(-1, -1, -1, 10);

        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(20, 20, 20, 20);
        tableRow.setWeightSum(1);
        tableRow.setId(count);
        tableRow.setBackground(gd);
        tableRow.setLayoutParams(tableRowParams);

        return tableRow;
    }

}
