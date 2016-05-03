package com.example.vlad.personalwallet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Vlad on 16.03.2016.
 *
 * This class allows the user to create a new entry costs
 * This class allows the user to create a new entry income
 */
public class IncomeMoney extends AppCompatActivity {

    final int CAMERA_CAPTURE = 1;
    final int CATEGORY_CAPTURE = 2;
    final int CALCULATOR_CAPTURE = 3;
    final int CURRENCY = 4;
    private Uri mPicUri;

    DatabaseHelper mSqlHelper;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        setDefaultValue();
    }

    /**
     * set default value
     */
    private void setDefaultValue() {
        // creates database object
        mSqlHelper = new DatabaseHelper(getApplicationContext());

        // set last used category
        db = mSqlHelper.getReadableDatabase();
        String query = "SELECT " + DatabaseHelper.CATEGORY_INCOME_COLUMN + " FROM "
                + DatabaseHelper.DATABASE_TABLE_WALLET +
                " WHERE " + DatabaseHelper.CATEGORY_INCOME_COLUMN + " IS NOT NULL ORDER BY " +
                DatabaseHelper.COLUMN_ID + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToNext()) {
            TextView categoryDefault = (TextView) findViewById(R.id.txtCategory);
            categoryDefault.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CATEGORY_INCOME_COLUMN)));
        }
        cursor.close();

        // set default time and date
        TextView dateDefault = (TextView) findViewById(R.id.txtDate);
        TextView timeDefault = (TextView) findViewById(R.id.txtTime);

        final Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateDefault.setText(dateFormat.format(c.getTime()));
        SimpleDateFormat timeFormat = new SimpleDateFormat("k:m");
        timeDefault.setText(timeFormat.format(c.getTime()));

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
        db.close();
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
     * pressing the camera button creates a file and calls camera to create and save picture
     */
      public void makePhotoOfCheck(View view) {

//        Calendar time = Calendar.getInstance();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-k-m-s");
//        String uniqueName = dateFormat.format(time.getTime());
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File file = new File(Environment.getExternalStorageDirectory(),
//                uniqueName);
//        mPicUri = Uri.fromFile(file);
//
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPicUri);
//        startActivityForResult(intent, CAMERA_CAPTURE);

          //camera stuff
          Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
          String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            //folder stuff
//          File imagesFolder = new File(Environment.getExternalStorageDirectory(), "myWallet");
//          imagesFolder.mkdirs();
          File imagesFolder = Environment.getExternalStorageDirectory();
          imagesFolder = new File(imagesFolder, "QR_" + timeStamp + ".jpg");

        //  File image = new File(imagesFolder, "QR_" + timeStamp + ".png");
          Uri uriSavedImage = Uri.fromFile(imagesFolder);

          imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
          startActivityForResult(imageIntent, CAMERA_CAPTURE);
    }

    /**
     * Calls Activity for create or select category
     */
    public void onRowChangeCategoryClick(final View view){
        Intent intent = new Intent(this, Category.class);
        intent.putExtra("className",getLocalClassName());
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
        Intent intent = new Intent(IncomeMoney.this, Calculator.class);
        startActivityForResult(intent, CALCULATOR_CAPTURE);
    }

    /**
     * Accepts results other activities
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE) { // processing of data on camera activity
          //  ImageView mImageView = (ImageView)findViewById(R.id.picture);
          //  Toast.makeText(getApplicationContext(), mPicUri.toString(), Toast.LENGTH_LONG).show();
//            // Check does the thumbnail results
//            if (data != null) {
//                if (data.hasExtra("data")) {
//                    Bitmap thumbnailBitmap = data.getParcelableExtra("data");
//                    mImageView.setImageBitmap(thumbnailBitmap);
//                }
//            } else {
//                mImageView.setImageURI(mPicUri);
//            }
        } else if (requestCode == CATEGORY_CAPTURE){ // processing of data on category activity
            // set category name
            if (resultCode == RESULT_OK){
                String newCatNameTxt = data.getStringExtra("catName");
                TextView categoryIncome = (TextView)findViewById(R.id.txtCategory);
                categoryIncome.setText(newCatNameTxt);
            }
        } else if (requestCode == CALCULATOR_CAPTURE){ // processing of data on calculator activity
            if (resultCode == RESULT_OK) {
                String sumIncome = data.getStringExtra("calcRes");
                EditText editTxtSum = (EditText) findViewById(R.id.editTxtSum);
                editTxtSum.setText(sumIncome);
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
     * Collects data and writes it to the database
     */
    public void onButtonSaveEventClick(View view) {
        db = mSqlHelper.getWritableDatabase();

        // collect data from views
        TextView textDataView = (TextView)findViewById(R.id.txtDate);
        TextView textTimeView = (TextView)findViewById(R.id.txtTime);
        TextView textCategoryView = (TextView)findViewById(R.id.txtCategory);
        EditText textSumIncome = (EditText)findViewById(R.id.editTxtSum);
        EditText textPhoto = (EditText)findViewById(R.id.makePhotoText);
        EditText textComment = (EditText)findViewById(R.id.editComment);

        if (textSumIncome.getText().toString().length() > 0
                && !textCategoryView.getText().equals("не выбрано")) { // check empty sum and category

            // set new values to database
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.DATE_COLUMN, textDataView.getText().toString() + " " + textTimeView.getText().toString());
            values.put(DatabaseHelper.CASH_COLUMN, Double.valueOf(textSumIncome.getText().toString()));
            values.put(DatabaseHelper.CATEGORY_INCOME_COLUMN, textCategoryView.getText().toString());
            values.put(DatabaseHelper.PHOTO_COLUMN, textPhoto.getText().toString());
            values.put(DatabaseHelper.COMMENT_COLUMN, textComment.getText().toString());

            db.insert(DatabaseHelper.DATABASE_TABLE_WALLET, null, values);

            Toast.makeText(getApplicationContext(), "Доход сохранен", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(IncomeMoney.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Возможно вы не ввели сумму или категорию", Toast.LENGTH_LONG).show();
        }
    }

}