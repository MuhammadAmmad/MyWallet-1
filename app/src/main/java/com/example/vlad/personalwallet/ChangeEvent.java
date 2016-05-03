package com.example.vlad.personalwallet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Vlad on 03.04.2016.
 *
 * This class takes a record id, displays all data
 * of the recording and allows to any change to it
 */

public class ChangeEvent extends AppCompatActivity implements Constants{

    // this variable will store information about that to which
    // action relates record (to the consumption or income)
    String mCurrentEvent = "";

    // this variable will store name of the class in which the record was created
    String classNameEvent = "";

    // current id of event
    int mCurrentId;

    private Uri mPicUri;

    DatabaseHelper mDatabaseHelper;
    SQLiteDatabase mSqLiteDatabase;
    Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        setDefaultValue();
    }

    /**
     * Select from database all information about the
     * required entry and set values in the markup
     */
    private void setDefaultValue() {

        mCurrentId = getIntent().getExtras().getInt("recordId");

        // creates database object
        mDatabaseHelper = new DatabaseHelper(this);
        mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.DATABASE_TABLE_WALLET
                + " WHERE " + DatabaseHelper.COLUMN_ID + " = '" + mCurrentId + "'" ;

        Cursor mUserCursor = mSqLiteDatabase.rawQuery(query, null);

        mUserCursor.moveToNext();

        String comment = mUserCursor.getString(mUserCursor.getColumnIndex(DatabaseHelper.COMMENT_COLUMN));
        String categoryName = "";
        if (mUserCursor.getString(mUserCursor.getColumnIndex(DatabaseHelper.CATEGORY_SPEND_COLUMN)) != null){
            categoryName = mUserCursor.getString(mUserCursor.getColumnIndex(DatabaseHelper.CATEGORY_SPEND_COLUMN));
            mCurrentEvent = DatabaseHelper.CATEGORY_SPEND_COLUMN;
            classNameEvent = "ConsumptionMoney";
        } else if (mUserCursor.getString(mUserCursor.getColumnIndex(DatabaseHelper.CATEGORY_INCOME_COLUMN)) != null){
            categoryName = mUserCursor.getString(mUserCursor.getColumnIndex(DatabaseHelper.CATEGORY_INCOME_COLUMN));
            mCurrentEvent = DatabaseHelper.CATEGORY_INCOME_COLUMN;
            classNameEvent = "IncomeMoney";
        }

        double sum = mUserCursor.getDouble(mUserCursor.getColumnIndex(DatabaseHelper.CASH_COLUMN));
        String photoAddress = mUserCursor.getString(mUserCursor.getColumnIndex(DatabaseHelper.PHOTO_COLUMN));
        String date = mUserCursor.getString(mUserCursor.getColumnIndex(DatabaseHelper.DATE_COLUMN));
        String time = date.substring(11);
        date = date.substring(0, 10);

        // set default value
        TextView dateDefault = (TextView) findViewById(R.id.txtDate);
        dateDefault.setText(date);
        TextView timeDefault = (TextView) findViewById(R.id.txtTime);
        timeDefault.setText(time);
        TextView categoryDefault = (TextView) findViewById(R.id.txtCategory);
        categoryDefault.setText(categoryName);
        TextView sumDefault = (TextView) findViewById(R.id.editTxtSum);
        sumDefault.setText(String.valueOf(sum));


        if (photoAddress.length() > 0){
            TextView photoDefault = (TextView) findViewById(R.id.makePhotoText);
            photoDefault.setText(photoAddress);
        }
        if (comment.length() > 0){
            TextView commentDefault = (TextView) findViewById(R.id.editComment);
            commentDefault.setText(comment);
        }
        mUserCursor.close();

        // When selecting currency called the activity where you can find out the exchange rate
        Spinner spinner = (Spinner)findViewById(R.id.spinnerCurrency);
        final Intent currencyIntent = new Intent (this, CurrencyRates.class);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                String[] choose = getResources().getStringArray(R.array.currencyList);

                if (!choose[selectedItemPosition].equals("Валюта")) {
                    currencyIntent.putExtra("CurrencyName", choose[selectedItemPosition]);
                    startActivityForResult(currencyIntent, CURRENCY);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        mDatabaseHelper.close();
        mCursor.close();
    }

    /**
     * Pressing on button "calendar" in the pop-up window
     * called calendar to select a date
     */
    public void onButtonDateClick(View view) {
        final TextView txtDate = (TextView) findViewById(R.id.txtDate);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        monthOfYear += 1; // 1 is added, because the format (0 - 11)
                        String dayOfMonthStr = Integer.toString(dayOfMonth);
                        String monthOfYearStr = Integer.toString(monthOfYear);

                        // if the number is less than 10, 0 is added to correct the date
                        if(monthOfYear < 10) monthOfYearStr = "0" + monthOfYear;
                        if(dayOfMonth < 10) dayOfMonthStr = "0" + dayOfMonth;

                        txtDate.setText(year + "-" + monthOfYearStr + "-" + dayOfMonthStr);
                    }
                }, year, month, day);
        dpd.show();
    }

    /**
     * Pressing on text "time" in the pop-up window
     * called clock select a time
     */
    public void onButtonTimeClick(View view) {
        final TextView txtTime = (TextView) findViewById(R.id.txtTime);

        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hourOfDayTxt = Integer.toString(hourOfDay);
                        String minuteTxt = Integer.toString(minute);

                        // if the number is less than 10, 0 is added to correct the date
                        if(hourOfDay < 10) hourOfDayTxt = "0" + hourOfDay;
                        if (minute < 10) minuteTxt = "0" + minute;

                        txtTime.setText(hourOfDayTxt + ":" + minuteTxt);
                    }
                }, hour, minute, true);
        tpd.show();
    }

    /**
     * pressing the camera button creates a file
     * and calls camera to create and save picture
     */
    public void makePhotoOfCheck(View view) {
        Calendar time = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-k-m-s");
        String uniqueName = dateFormat.format(time.getTime());

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(),
                uniqueName);
        mPicUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPicUri);
        startActivityForResult(intent, CAMERA_CAPTURE);
    }

    /**
     * Calls Activity for create or select category
     */
    public void onRowChangeCategoryClick(final View view){
        Intent intent = new Intent(this, Category.class);

        intent.putExtra("className", classNameEvent);
        startActivityForResult(intent, CATEGORY_CAPTURE);
    }

    /**
     * Calls method that calls activity for create or select category
     */
    public void onImageChangeCategoryClick(final View view){
        onRowChangeCategoryClick(view);
    }

    /**
     * Calls calculator
     */
    public void onButtonCalculatorClick(View view) {
        Intent intent = new Intent(ChangeEvent.this, Calculator.class);
        startActivityForResult(intent, CALCULATOR_CAPTURE);
    }

    /**
     * Accepts results other activities
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE) { // processing of data on camera activity
            //ImageView mImageView = (ImageView)findViewById(R.id.picture);
            Toast.makeText(getApplicationContext(), mPicUri.toString(), Toast.LENGTH_LONG).show();
                // Check does the thumbnail results
//                if (data != null) {
//                    if (data.hasExtra("data")) {
//                        Bitmap thumbnailBitmap = data.getParcelableExtra("data");
//                        mImageView.setImageBitmap(thumbnailBitmap);
//                    }
//                } else {
//                    mImageView.setImageURI(mPicUri);
//                }
        } else if (requestCode == CATEGORY_CAPTURE){ // processing of data on category activity
            // set category name
            if (resultCode == RESULT_OK){
                String newCatNameTxt = data.getStringExtra("catName");
                TextView categoryConsumption = (TextView)findViewById(R.id.txtCategory);
                categoryConsumption.setText(newCatNameTxt);
            }
        } else if (requestCode == CALCULATOR_CAPTURE){ // processing of data on calculator activity
            if (resultCode == RESULT_OK) {
                String sumConsumption = data.getStringExtra("calcRes");
                EditText editTxtSum = (EditText) findViewById(R.id.editTxtSum);
                editTxtSum.setText(sumConsumption);
            }
        } else if (requestCode == CURRENCY){ // processing of data on currency rates activity
            if (resultCode == RESULT_OK) {
                String sumConsumption = data.getStringExtra("currencyResSum");
                EditText editTxtSum = (EditText) findViewById(R.id.editTxtSum);
                editTxtSum.setText(sumConsumption);
            }
        }
    }

    /**
     * Collects data and update it to the database
     */
    public void onButtonSaveEventClick(View view) {
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();

        // collect data from views
        TextView textDataView = (TextView)findViewById(R.id.txtDate);
        TextView textTimeView = (TextView)findViewById(R.id.txtTime);
        TextView textCategoryView = (TextView)findViewById(R.id.txtCategory);
        EditText textSumConsumption = (EditText)findViewById(R.id.editTxtSum);
        EditText textPhoto = (EditText)findViewById(R.id.makePhotoText);
        EditText textComment = (EditText)findViewById(R.id.editComment);

        if (textSumConsumption.getText().toString().length() > 0
                && !textCategoryView.getText().equals("не выбрано")) { // check empty sum and category

            // set new values to database
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.DATE_COLUMN, textDataView.getText().toString() + " " + textTimeView.getText().toString());
            values.put(DatabaseHelper.CASH_COLUMN, Double.valueOf(textSumConsumption.getText().toString()));
            values.put(mCurrentEvent, textCategoryView.getText().toString());
            values.put(DatabaseHelper.PHOTO_COLUMN, textPhoto.getText().toString());
            values.put(DatabaseHelper.COMMENT_COLUMN, textComment.getText().toString());

            mSqLiteDatabase.update(DatabaseHelper.DATABASE_TABLE_WALLET, values,
                    DatabaseHelper.COLUMN_ID + " = '" + mCurrentId + "'", null);

            Toast.makeText(getApplicationContext(), "Запись обновлена", Toast.LENGTH_SHORT).show();

            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Возможно вы не ввели сумму или категорию", Toast.LENGTH_LONG).show();
        }
    }

}
