package com.example.vlad.mywallet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ConsumptionMoney extends AppCompatActivity {

    final int CAMERA_CAPTURE = 1;
    final int CATEGORY_CAPTURE = 2;
    final int CALCULATOR_CAPTURE = 3;
    private Uri mPicUri;

    DatabaseHelper mSqlHelper;
    SQLiteDatabase db;
    Cursor mUserCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        // creates database object
        mSqlHelper = new DatabaseHelper(getApplicationContext());

        // set default time and date
        TextView dateDefault = (TextView) findViewById(R.id.txtDate);
        TextView timeDefault = (TextView) findViewById(R.id.txtTime);

        final Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d-MM-yyyy");
        dateDefault.setText(dateFormat.format(c.getTime()));
        SimpleDateFormat timeFormat = new SimpleDateFormat("k:m");
        timeDefault.setText(timeFormat.format(c.getTime()));
    }

    @Override
    public void onResume() {
        super.onResume();

        // open connection to database
        db = mSqlHelper.getReadableDatabase();

        // get data from database
        mUserCursor = db.query(DatabaseHelper.DATABASE_TABLE_WALLET, new String[]{DatabaseHelper.CATEGORY_SPEND_COLUMN,
                        DatabaseHelper.COMMENT_COLUMN},
                null, null,
                null, null, null);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        db.close();
        mUserCursor.close();
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

                        txtDate.setText(dayOfMonthStr + "-" + monthOfYearStr + "-" + year);
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
     * By clicking on the button location, called the map where you
     * can see the correct address, or see already entered address
     */
    public void onImageSetLocClick(View view) {
        EditText address = (EditText) findViewById(R.id.locationAddressText);

        String geoUriString = "geo:0,0?q=" + address.getText() + "&z=14";
        Uri geoUri = Uri.parse(geoUriString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        startActivity(mapIntent);
    }

    /**
     * pressing the camera button creates a file and calls camera to create and save picture
     */
    public void makePhotoOfCheck(View view) {
        setContentView(R.layout.make_photo);
        setTitle("Сделайте фото");

        Calendar time = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d-MM-yyyy-k-m-s");
        String uniqueName = dateFormat.format(time.getTime());

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(),
                uniqueName);
        mPicUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPicUri);
        startActivityForResult(intent, CAMERA_CAPTURE);
    }

    public void onButtonSaveImageClick(View view) {
        setContentView(R.layout.activity_new_event);
    }

    /**
     * Calls Activity for create or select category
     */
    public void onButtonAddNewCategoryClick(final View view){
        Intent intent = new Intent(this, Category.class);
        intent.putExtra("className",getLocalClassName());
        startActivityForResult(intent, CATEGORY_CAPTURE);
    }

    /**
     * Calls method that calls Activity for create or select category
     */
    public void onTextAddCategoryClick(final View view){
        onButtonAddNewCategoryClick(view);
    }

    /**
     * Calls calculator
     */
    public void onButtonCalculatorClick(View view) {
        Intent intent = new Intent(ConsumptionMoney.this, Calculator.class);
        startActivityForResult(intent, CALCULATOR_CAPTURE);
    }

    /**
     * Accepts results other activities
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE) { // processing of data on camera activity
            ImageView mImageView = (ImageView)findViewById(R.id.picture);
            Toast.makeText(getApplicationContext(), mPicUri.toString(), Toast.LENGTH_LONG).show();
                // Проверяем, содержит ли результат маленькую картинку
                if (data != null) {
                    if (data.hasExtra("data")) {
                        Bitmap thumbnailBitmap = data.getParcelableExtra("data");
                        mImageView.setImageBitmap(thumbnailBitmap);
                    }
                } else {
                    mImageView.setImageURI(mPicUri);
                }
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
                EditText editTxtSumm = (EditText) findViewById(R.id.editTxtSumm);
                editTxtSumm.setText(sumConsumption);
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
        EditText textSumConsumption = (EditText)findViewById(R.id.editTxtSumm);
        EditText textLocation = (EditText)findViewById(R.id.locationAddressText);
        EditText textPhoto = (EditText)findViewById(R.id.makePhotoText);
        EditText textComment = (EditText)findViewById(R.id.editComment);

        if (textSumConsumption.getText().toString().length() > 0) { // check empty sum

            // set new values to database
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.DATE_COLUMN, textDataView.getText().toString());
            values.put(DatabaseHelper.TIME_COLUMN, textTimeView.getText().toString());
            values.put(DatabaseHelper.CASH_COLUMN, Double.valueOf(textSumConsumption.getText().toString()));
            values.put(DatabaseHelper.CATEGORY_SPEND_COLUMN, textCategoryView.getText().toString());
            values.put(DatabaseHelper.LOCATION_COLUMN, textLocation.getText().toString());
            values.put(DatabaseHelper.PHOTO_COLUMN, textPhoto.getText().toString());
            values.put(DatabaseHelper.COMMENT_COLUMN, textComment.getText().toString());

            db.insert(DatabaseHelper.DATABASE_TABLE_WALLET, null, values);

            Toast.makeText(getApplicationContext(), "Расход сохранен", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ConsumptionMoney.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Введите сумму", Toast.LENGTH_SHORT).show();
        }
    }

}
