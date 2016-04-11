package com.example.vlad.personalwallet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Balance extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ballance);
        setDefaultYear();
    }

    private void setDefaultYear() {
        Calendar time = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String defaultYear = dateFormat.format(time.getTime());

        TextView currentYear = (TextView)findViewById(R.id.currentNumberOfYear);
        currentYear.setText(String.valueOf(defaultYear));
    }

    @Override
    public void onResume(){
        super.onResume();

        Double[] arrayBalanceFromYear = getArrayBalance();
        setValue(arrayBalanceFromYear);
    }

    private void setValue(Double[] arrayBalanceFromYear) {

        for (int i = 0; i <= 12; i++){
            int id = getResources().getIdentifier("month" + i, "id", getPackageName());
            TextView month = (TextView)findViewById(id);
            month.setText(String.valueOf(arrayBalanceFromYear[i]));
        }
    }

    private Double[] getArrayBalance() {
        Double[] arrayBalanceFromYear = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

        TextView currentValueYear = (TextView)findViewById(R.id.currentNumberOfYear);
        int currentValueYearInt = Integer.parseInt(currentValueYear.getText().toString());

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DatabaseHelper.DATABASE_TABLE_WALLET;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE_COLUMN));
            int monthNumber = Integer.parseInt(date.substring(5, 7));
            int yearNumber = Integer.parseInt(date.substring(0, 4));

            if (currentValueYearInt == yearNumber) {
                if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.CATEGORY_INCOME_COLUMN)) != null) {
                    Double newSum = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.CASH_COLUMN));
                    arrayBalanceFromYear[monthNumber] += newSum;
                    arrayBalanceFromYear[0] += newSum;
                } else {
                    Double newSum = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.CASH_COLUMN));
                    arrayBalanceFromYear[monthNumber] -= newSum;
                    arrayBalanceFromYear[0] -= newSum;
                }
            }
        }
        cursor.close();
        return arrayBalanceFromYear;
    }

    public void onButtonNextYearClick(View view) {
        TextView currentYear = (TextView)findViewById(R.id.currentNumberOfYear);
        int numberYear = Integer.parseInt(currentYear.getText().toString());
        currentYear.setText(String.valueOf(++numberYear));
        onResume();
    }

    public void onButtonPrevYearClick(View view) {
        TextView currentYear = (TextView)findViewById(R.id.currentNumberOfYear);
        int numberYear = Integer.parseInt(currentYear.getText().toString());
        currentYear.setText(String.valueOf(--numberYear));
        onResume();
    }
}
