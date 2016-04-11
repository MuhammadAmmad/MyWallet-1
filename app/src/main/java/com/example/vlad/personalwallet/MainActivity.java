package com.example.vlad.personalwallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public String APP_PREFERENCES_CATEGORY_SPEND = "mywalletcategoryspend";
    public String APP_PREFERENCES_CATEGORY_INCOME = "mywalletcategoryincome";

    public String FILTER_DAY = "day";
    public String FILTER_WEEK = "week";
    public String FILTER_MONTH = "month";
    public String FILTER_ALL = "all";

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume(){
        super.onResume();

        // initialization file settings where saved category
        SharedPreferences settingsCategory = getSharedPreferences(APP_PREFERENCES_CATEGORY_SPEND, Context.MODE_PRIVATE);
        if (!settingsCategory.contains("Еда")){
            settingsCategory.edit().putString("Еда", "Еда").apply();
        }

        SharedPreferences settingsCategoryIncome = getSharedPreferences(APP_PREFERENCES_CATEGORY_INCOME, Context.MODE_PRIVATE);
        if (!settingsCategoryIncome.contains("Аванс")){
            settingsCategoryIncome.edit().putString("Аванс", "Аванс").apply();
        }

        setParameters();
    }

    /**
     * sets the amount expenses and income for different periods of time
     */
    private void setParameters() {
        double sum;

        // from last day
        sum = getSumFromPeriod(FILTER_DAY, DatabaseHelper.CATEGORY_SPEND_COLUMN);
        TextView consumptionToday = (TextView) findViewById(R.id.consumption_balance_today);
        consumptionToday.setText(String.valueOf(sum));

        // from last week
        sum = getSumFromPeriod(FILTER_WEEK, DatabaseHelper.CATEGORY_SPEND_COLUMN);
        TextView consumptionWeek = (TextView) findViewById(R.id.consumption_balance_week);
        consumptionWeek.setText(String.valueOf(sum));

        // from last month
        sum = getSumFromPeriod(FILTER_MONTH, DatabaseHelper.CATEGORY_SPEND_COLUMN);
        TextView consumptionMonth = (TextView) findViewById(R.id.consumption_balance_month);
        consumptionMonth.setText(String.valueOf(sum));

        // income money for one month
        sum = getSumFromPeriod(FILTER_MONTH, DatabaseHelper.CATEGORY_INCOME_COLUMN);
        TextView incomeMonth = (TextView) findViewById(R.id.income_balance);
        incomeMonth.setText(String.valueOf(sum));

        // balance difference
        mDatabaseHelper = new DatabaseHelper(this);
        mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();

        Double sumAllConsumption = getSumFromPeriod(FILTER_ALL, DatabaseHelper.CATEGORY_SPEND_COLUMN);
        Double sumAllIncome = getSumFromPeriod(FILTER_ALL, DatabaseHelper.CATEGORY_INCOME_COLUMN);
        sum = sumAllIncome - sumAllConsumption;

        TextView currentBalance = (TextView) findViewById(R.id.current_balance);
        currentBalance.setText(String.valueOf(sum));
    }

    /**
     * Makes a request to the database, according to the
     * amount for a specified period of time and returns it
     *
     * @param period - Period of time for which need to calculate sum of (day, week, month, all the time)
     * @param spendOrIncome - column name in the database table (income or spend)
     * @return - sum
     */
    private double getSumFromPeriod(String period, String spendOrIncome) {
        mDatabaseHelper = new DatabaseHelper(this);
        mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();


        String filter = "";
        if (period.equals(FILTER_DAY)){
            filter = "date(" + DatabaseHelper.DATE_COLUMN + ") >= date('now') AND ";
        } else if(period.equals(FILTER_WEEK)){
            filter = "date(" + DatabaseHelper.DATE_COLUMN + ") >= date('now', '-7 day') AND ";
        } else if (period.equals(FILTER_MONTH)) {
            filter = "date(" + DatabaseHelper.DATE_COLUMN + ") >= date('now', '-1 month') AND ";
        }

        String query = "SELECT " + DatabaseHelper.CASH_COLUMN + "" + " FROM " + DatabaseHelper.DATABASE_TABLE_WALLET
                + " WHERE " + filter + spendOrIncome + " IS NOT NULL";

        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);

        double sum = 0.0;
        while(cursor.moveToNext()){
            sum += cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.CASH_COLUMN));
        }
        cursor.close();
        return sum;
    }

    /**
     * By clicking on the button "consumption" calls activity to add a new consumption
     */
    public void onButtonAddConsumptionClick(View view){
        Intent intent = new Intent(MainActivity.this, ConsumptionMoney.class);
        startActivity(intent);
    }

    /**
     * By clicking on the button "income money" calls activity to add a new income
     */
    public void onButtonAddIncomeClick(View view){
        Intent intent = new Intent(MainActivity.this, IncomeMoney.class);
        startActivity(intent);
    }

    /**
     * By clicking on the button "balance" calls activity to show balance
     */
    public void onViewBalanceButtonClick(View view){
        Intent intent = new Intent(MainActivity.this, Balance.class);
        startActivity(intent);
    }

    /**
     * By clicking on the image of "all costs" calls the activity
     * in which one can see all costs and change them if necessary
     */
    public void onAllCostsImageClick(View view){
        Intent intent = new Intent(MainActivity.this, AllCosts.class);
        startActivity(intent);
    }

    /**
     * By clicking on the image of "all income" calls the activity
     * in which one can see all income and change them if necessary
     */
    public void onAllIncomeImageClick(View view){
        Intent intent = new Intent(MainActivity.this, AllIncome.class);
        startActivity(intent);
    }

}
