package com.wallet.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.wallet.Constants;
import com.wallet.R;
import com.wallet.activity.CalculatorActivity;
import com.wallet.activity.CategoryActivity;
import com.wallet.activity.CurrencyRatesActivity;
import com.wallet.model.IncomeTable;
import com.wallet.model.SpendTable;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class NewEventFragment extends Fragment implements Constants {

    private Uri mPicUri;

    View mView;
    TextView mTxtTime, mTxtDate, mCategory;
    EditText textPhoto, mEditTxtSum, mTextComment;
    ImageView mCalcImg, mPhotoImg, mChangeCategoryImg;
    RelativeLayout mRowChangeCategory;
    Button mButtonSave;
    Spinner mSpinner;
    String mEventCategory;
    String idFromData;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_new_event, container, false);
        ActiveAndroid.initialize(mView.getContext());

        Bundle bundle = getArguments();
        mEventCategory = bundle.getString("tagEvent");
        idFromData = bundle.getString("id");

        initView();
        setDefaultValue();
        setClickListeners();

        return mView;
    }

    private void initView() {
        mTxtTime = (TextView) mView.findViewById(R.id.txtTime);
        mTxtDate = (TextView) mView.findViewById(R.id.txtDate);
        textPhoto = (EditText) mView.findViewById(R.id.makePhotoText);
        mTextComment = (EditText) mView.findViewById(R.id.editComment);
        mCategory = (TextView) mView.findViewById(R.id.txtCategory);
        mEditTxtSum = (EditText) mView.findViewById(R.id.editTxtSum);
        mCalcImg = (ImageView) mView.findViewById(R.id.calculator_img);
        mPhotoImg = (ImageView) mView.findViewById(R.id.makePhotoIcon);
        mChangeCategoryImg = (ImageView) mView.findViewById(R.id.changeCategoryImage);
        mRowChangeCategory = (RelativeLayout) mView.findViewById(R.id.rowChangeCategory);
        mButtonSave = (Button) mView.findViewById(R.id.buttonSaveEvent);
        mSpinner = (Spinner) mView.findViewById(R.id.spinnerCurrency);
    }

    private void setDefaultValue() {

        // set default time and date
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        mTxtDate.setText(dateFormat.format(c.getTime()));
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        mTxtTime.setText(timeFormat.format(c.getTime()));

        // set last used category
        if (mEventCategory.equals(CATEGORY_EVENT_INCOME)) {
            IncomeTable lastCategory = IncomeTable.getLast();
            if (lastCategory != null) {
                mCategory.setText(lastCategory.category);
            }
        } else if (mEventCategory.equals(CATEGORY_EVENT_SPEND)){
            SpendTable lastCategory = SpendTable.getLast();
            if (lastCategory != null) {
                mCategory.setText(lastCategory.category);
            }
        }

        // if If variable exists, then the event need to set all default value, instead to create a new
        if (idFromData != null){
            if (mEventCategory.equals(CATEGORY_EVENT_INCOME)) {
                IncomeTable lastCategory = IncomeTable.getFromId(idFromData);
                mTxtDate.setText(getDateTime(lastCategory.dateTime, "date"));
                mTxtTime.setText(getDateTime(lastCategory.dateTime, "time"));
                mEditTxtSum.setText(String.valueOf(lastCategory.cash));
                mTextComment.setText(String.valueOf(lastCategory.comment));
                mCategory.setText(lastCategory.category);

            } else if (mEventCategory.equals(CATEGORY_EVENT_SPEND)){
                SpendTable lastCategory = SpendTable.getFromId(idFromData);
                mTxtDate.setText(getDateTime(lastCategory.dateTime, "date"));
                mTxtTime.setText(getDateTime(lastCategory.dateTime, "time"));
                mEditTxtSum.setText(String.valueOf(lastCategory.cash));
                mTextComment.setText(String.valueOf(lastCategory.comment));
                mCategory.setText(lastCategory.category);
            }
        }


    }

    public  static  String getDateTime(long milliSeconds, String type){
        String format = "";
        if (type.equals("date")){
            format = DATE_FORMAT;
        } else if (type.equals("time")){
            format = TIME_FORMAT;
        }


        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void setClickListeners() {
        // click listener on the spinner
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                String[] choose = getResources().getStringArray(R.array.currencyList);

                if (!choose[selectedItemPosition].equals(getString(R.string.carrency))) {
                    final Intent currencyIntent = new Intent(getActivity(), CurrencyRatesActivity.class);
                    currencyIntent.putExtra("CurrencyName", choose[selectedItemPosition]);
                    getActivity().startActivityForResult(currencyIntent, CURRENCY);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // click listener on arrow image
        mChangeCategoryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("tagEvent", mEventCategory);
                startActivityForResult(intent, CATEGORY_CAPTURE);
            }
        });

        // click listener on row category
        mRowChangeCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("tagEvent", mEventCategory);
                startActivityForResult(intent, CATEGORY_CAPTURE);
            }
        });

        // click listener on image photo
        mPhotoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar time = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT + "-" + TIME_FORMAT);
                String uniqueName = dateFormat.format(time.getTime());

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory(),
                        uniqueName);
                mPicUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mPicUri);
                startActivityForResult(intent, CAMERA_CAPTURE);
            }
        });

        // click listener on image calculator
        mCalcImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalculatorActivity.class); //
                startActivityForResult(intent, CALCULATOR_CAPTURE);
            }
        });

        // click listener on view time
        mTxtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView txtTime = (TextView) mView.findViewById(R.id.txtTime);

                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                String hourOfDayTxt = Integer.toString(hourOfDay);
                                String minuteTxt = Integer.toString(minute);

                                // if the number is less than 10, 0 is added to correct the date
                                if (hourOfDay < 10) hourOfDayTxt = "0" + hourOfDay;
                                if (minute < 10) minuteTxt = "0" + minute;

                                txtTime.setText(hourOfDayTxt + ":" + minuteTxt);
                            }
                        }, hour, minute, true);
                tpd.show();
            }
        });

        // click listener on view date
        mTxtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                monthOfYear += 1; // 1 is added, because the format (0 - 11)
                                String dayOfMonthStr = Integer.toString(dayOfMonth);
                                String monthOfYearStr = Integer.toString(monthOfYear);

                                // if the number is less than 10, 0 is added to correct the date
                                if (monthOfYear < 10) monthOfYearStr = "0" + monthOfYear;
                                if (dayOfMonth < 10) dayOfMonthStr = "0" + dayOfMonth;

                                mTxtDate.setText(year + "-" + monthOfYearStr + "-" + dayOfMonthStr);
                            }
                        }, year, month, day);
                dpd.show();
            }
        });

        // click listener on button save
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // collect data from views and set new values to database
                if (mEditTxtSum.getText().toString().length() > 0
                        && !mCategory.getText().equals(getString(R.string.no_selected))) { // check empty sum and category

                    SimpleDateFormat dtFormat = new SimpleDateFormat(DATE_FORMAT + TIME_FORMAT);
                    long timeInMilliseconds = 0;
                    try {
                        Date date = dtFormat.parse(mTxtDate.getText().toString() + mTxtTime.getText().toString());
                        timeInMilliseconds = date.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (mEventCategory.equals(CATEGORY_EVENT_INCOME)) {
                        if (idFromData != null){
                            new Delete().from(IncomeTable.class).where("Id = ?", idFromData).execute();
                        }
                        IncomeTable incomeTable = new IncomeTable();
                        incomeTable.dateTime = timeInMilliseconds;
                        incomeTable.cash = Double.valueOf(mEditTxtSum.getText().toString());
                        incomeTable.category = mCategory.getText().toString();
                        incomeTable.comment = mTextComment.getText().toString();
                        incomeTable.save();
                    } else if (mEventCategory.equals(CATEGORY_EVENT_SPEND)){
                        if (idFromData != null){
                            new Delete().from(SpendTable.class).where("Id = ?", idFromData).execute();
                        }
                        SpendTable spendTable = new SpendTable();
                        spendTable.dateTime = timeInMilliseconds;
                        spendTable.cash = Double.valueOf(mEditTxtSum.getText().toString());
                        spendTable.category = mCategory.getText().toString();
                        spendTable.comment = mTextComment.getText().toString();
                        spendTable.save();
                    }

                    Toast.makeText(getActivity().getApplicationContext(), R.string.saved, Toast.LENGTH_SHORT).show();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    MainFragment fragment = new MainFragment();
                    fragmentTransaction.replace(R.id.custom_fragment, fragment).commit();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.may_be_something_empty, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * Accepts results other activities
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE) { // processing of data on camera activity
            //   ImageView mImageView = (ImageView)findViewById(R.id.picture);
            Toast.makeText(getActivity().getApplicationContext(), mPicUri.toString(), Toast.LENGTH_LONG).show();
            // Check does the thumbnail results
//                if (data != null) {
//                    if (data.hasExtra("data")) {
//                        Bitmap thumbnailBitmap = data.getParcelableExtra("data");
//                        mImageView.setImageBitmap(thumbnailBitmap);
//                    }
//                } else {
//                    mImageView.setImageURI(mPicUri);
//                }
        } else if (requestCode == CATEGORY_CAPTURE) { // processing of data on category activity
            // set category name
            if (resultCode == Activity.RESULT_OK) {
                String newCatNameTxt = data.getStringExtra(CATEGORY_NAME);
                mCategory.setText(newCatNameTxt);
            }
        } else if (requestCode == CALCULATOR_CAPTURE) { // processing of data on calculator activity
            if (resultCode == Activity.RESULT_OK) {
                String sumConsumption = data.getStringExtra("calcRes");
                mEditTxtSum.setText(sumConsumption);
            }
        } else if (requestCode == CURRENCY) { // processing of data on currency rates activity
            if (resultCode == Activity.RESULT_OK) {
                String sumConsumption = data.getStringExtra("currencyResSum");
                mEditTxtSum.setText(sumConsumption);
            }
        }
    }

}