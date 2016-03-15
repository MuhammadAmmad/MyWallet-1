package com.example.vlad.mywallet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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


public class Consumption extends AppCompatActivity {

    final int CAMERA_CAPTURE = 1;
    private Uri picUri;

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption);

        // creates a database object
        sqlHelper = new DatabaseHelper(getApplicationContext());

        // set default
        TextView dateDefault = (TextView) findViewById(R.id.txtDateConsumption);
        TextView timeDefault = (TextView) findViewById(R.id.txtTimeConsumption);

        final Calendar c = Calendar.getInstance();

        // sets date and time format and set default values
        SimpleDateFormat dateFormat = new SimpleDateFormat("d-MM-yyyy");
        dateDefault.setText(dateFormat.format(c.getTime()));
        SimpleDateFormat timeFormat = new SimpleDateFormat("k:m");
        timeDefault.setText(timeFormat.format(c.getTime()));

    }

    @Override
    public void onResume() {
        super.onResume();

        // open connection to database
        db = sqlHelper.getReadableDatabase();

        // get data from database
        userCursor = db.query(DatabaseHelper.DATABASE_TABLE_WALLET, new String[]{DatabaseHelper.CATEGORY_SPEND_COLUMN,
                        DatabaseHelper.COMMENT_COLUMN},
                null, null,
                null, null, null);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключения
        db.close();
        userCursor.close();
    }

    // метод подымает календарь в диалоге и дает возможность выбрать дату
    public void onTxtDateConsumptionClick(View view) {
        final TextView txtDate = (TextView) findViewById(R.id.txtDateConsumption);

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

    // метод подымает часы в диалоге и дает возможность выбрать время
    public void onTxtTimeConsumptionClick(View view) {
        final TextView txtTime = (TextView) findViewById(R.id.txtTimeConsumption);

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
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

    public void onButtonCalculatorClick(View view) {
        Intent intent = new Intent(Consumption.this, Calculator.class);
        startActivity(intent);
    }

    public void onImageSetLocClick(View view) {
        EditText address = (EditText) findViewById(R.id.locationAddressText);

        String geoUriString = "geo:0,0?q=" + address.getText() + "&z=14";
        Uri geoUri = Uri.parse(geoUriString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        startActivity(mapIntent);
    }

    public void onButtonSaveImageClick(View view) {
        setContentView(R.layout.activity_consumption);
    }

    public void makePhotoOfCheck(View view) {
        setContentView(R.layout.make_photo);
        setTitle("Сделайте фото");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStoragePublicDirectory("myWallet"), "Pic1.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        picUri = Uri.fromFile(photo);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (requestCode == CAMERA_CAPTURE) {
                if (resultCode == Activity.RESULT_OK) {

                    getContentResolver().notifyChange(picUri, null);
                    ImageView imageView = (ImageView) findViewById(R.id.picture);
                    File file = new File(picUri.toString(), "Pic1.jpg");
                    Uri uri = Uri.fromFile(file);
                    imageView.setImageURI(uri);

                    try {
                        Bitmap bitmap = BitmapFactory.decodeFile(picUri.toString());
                        imageView.setImageBitmap(bitmap);
                        Toast.makeText(this, picUri.toString(),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }
            }
        } else if (requestCode == 2){
            // set category name
            if (resultCode == RESULT_OK){
                String newCatNameTxt = data.getStringExtra("catName");
                TextView categoryConsumption = (TextView)findViewById(R.id.txtCategoryConsumption);
                categoryConsumption.setText(newCatNameTxt);
            }
        }
    }

    public void onButtonAddConsumptionCatClick(final View view){
        Intent intent = new Intent(this, Category.class);
        startActivityForResult(intent, 2);
    }

    public void onButtonSaveConsumptionClick(View view) {
        db = sqlHelper.getWritableDatabase();

        TextView textDataView = (TextView)findViewById(R.id.txtDateConsumption);
        TextView textTimeView = (TextView)findViewById(R.id.txtTimeConsumption);
        TextView textCategoryView = (TextView)findViewById(R.id.txtCategoryConsumption);
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
            Intent intent = new Intent(Consumption.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Введите сумму", Toast.LENGTH_SHORT).show();
        }
    }

}
