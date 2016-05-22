package com.wallet.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import com.activeandroid.query.Delete;
import com.squareup.picasso.Picasso;
import com.wallet.Constants;
import com.wallet.R;
import com.wallet.activity.CalculatorActivity;
import com.wallet.activity.CategoryActivity;
import com.wallet.activity.CurrencyRatesActivity;
import com.wallet.activity.MainActivity;
import com.wallet.model.IncomeItem;
import com.wallet.model.SpendItem;
import com.wallet.utils.CacheUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class NewEventFragment extends Fragment {

    private View mView;
    private Uri mPicUri;
    private Spinner mSpinner;
    private Button mButtonSave;
    private RelativeLayout mRowChangeCategory;
    private String mEventCategory, mIdFromData;
    private EditText mEditTxtSum, mTextComment;
    private TextView mTxtTime, mTxtDate, mCategory;
    private Calendar mCalendar = Calendar.getInstance();
    private ImageView mCalcImg, mPhotoImg, mChangeCategoryImg, mCheckImg;
    private SimpleDateFormat mTimeFormat = new SimpleDateFormat(Constants.TIME_FORMAT);
    private SimpleDateFormat mDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_new_event, container, false);

        Bundle bundle = getArguments();
        mEventCategory = bundle.getString("tagEvent");
        mIdFromData = bundle.getString("id");

        initView();
        setDefaultValue();
        setClickListeners();

        return mView;
    }

    private void initView() {
        mTxtTime = (TextView) mView.findViewById(R.id.txtTime);
        mTxtDate = (TextView) mView.findViewById(R.id.txtDate);
        mCheckImg = (ImageView) mView.findViewById(R.id.checkImage);
        mCategory = (TextView) mView.findViewById(R.id.txtCategory);
        mEditTxtSum = (EditText) mView.findViewById(R.id.editTxtSum);
        mSpinner = (Spinner) mView.findViewById(R.id.spinnerCurrency);
        mTextComment = (EditText) mView.findViewById(R.id.editComment);
        mCalcImg = (ImageView) mView.findViewById(R.id.calculator_img);
        mPhotoImg = (ImageView) mView.findViewById(R.id.makePhotoIcon);
        mButtonSave = (Button) mView.findViewById(R.id.buttonSaveEvent);
        mChangeCategoryImg = (ImageView) mView.findViewById(R.id.changeCategoryImage);
        mRowChangeCategory = (RelativeLayout) mView.findViewById(R.id.rowChangeCategory);
    }

    private void setDefaultValue() {

        mTxtDate.setText(mDateFormat.format(mCalendar.getTime()));
        mTxtTime.setText(mTimeFormat.format(mCalendar.getTime()));

        // set last used category
        if (mEventCategory.equals(Constants.CATEGORY_EVENT_INCOME)) {
            IncomeItem lastCategory = IncomeItem.getLast();
            if (lastCategory != null)
                mCategory.setText(lastCategory.getCategory());

        } else if (mEventCategory.equals(Constants.CATEGORY_EVENT_SPEND)) {
            SpendItem lastCategory = SpendItem.getLast();
            if (lastCategory != null)
                mCategory.setText(lastCategory.getCategory());

        }

        //If variable exists, then event need to set all default value, instead to create a new
        if (mIdFromData != null) {
            if (mEventCategory.equals(Constants.CATEGORY_EVENT_INCOME)) {
                IncomeItem lastCategory = IncomeItem.getFromId(mIdFromData);
                mTxtDate.setText(getDateTime(lastCategory.getDateTime(), "date"));
                mTxtTime.setText(getDateTime(lastCategory.getDateTime(), "time"));
                mEditTxtSum.setText(String.valueOf(lastCategory.getCash()));
                mTextComment.setText(String.valueOf(lastCategory.getComment()));
                mCategory.setText(lastCategory.getCategory());

            } else if (mEventCategory.equals(Constants.CATEGORY_EVENT_SPEND)) {
                SpendItem lastCategory = SpendItem.getFromId(mIdFromData);
                mTxtDate.setText(getDateTime(lastCategory.getDateTime(), "date"));
                mTxtTime.setText(getDateTime(lastCategory.getDateTime(), "time"));
                mEditTxtSum.setText(String.valueOf(lastCategory.getCash()));
                mTextComment.setText(String.valueOf(lastCategory.getComment()));
                mCategory.setText(lastCategory.getCategory());
            }
        }


    }

    private String getDateTime(long milliSeconds, String type) {
        String format = "";
        if (type.equals("date")) {
            format = Constants.DATE_FORMAT;
        } else if (type.equals("time")) {
            format = Constants.TIME_FORMAT;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(format);
        mCalendar.setTimeInMillis(milliSeconds);
        return formatter.format(mCalendar.getTime());
    }

    private void setClickListeners() {
        // click listener on the spinner
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                String[] choose = getResources().getStringArray(R.array.currencyList);

                if (!choose[selectedItemPosition].equals(getString(R.string.carrency))) {
                    Intent currencyIntent = new Intent(getActivity(), CurrencyRatesActivity.class);
                    currencyIntent.putExtra("CurrencyName", choose[selectedItemPosition]);
                    if (mEditTxtSum != null){
                        currencyIntent.putExtra("CurrencySum", mEditTxtSum.getText().toString());
                    }
                    startActivityForResult(currencyIntent, Constants.CURRENCY);
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
                startActivityForResult(intent, Constants.CATEGORY_CAPTURE);
            }
        });

        // click listener on row category
        mRowChangeCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("tagEvent", mEventCategory);
                startActivityForResult(intent, Constants.CATEGORY_CAPTURE);
            }
        });

        // click listener on image photo
        mPhotoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar = Calendar.getInstance();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(CacheUtils.getCheckImageFolder(mView.getContext()),
                        "" + mCalendar.getTimeInMillis());
                mPicUri = Uri.fromFile(file);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, mPicUri);
                startActivityForResult(intent, Constants.CAMERA_CAPTURE);
            }
        });

        // click listener on image calculator
        mCalcImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalculatorActivity.class); //
                startActivityForResult(intent, Constants.CALCULATOR_CAPTURE);
            }
        });

        // click listener on view time
        mTxtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView txtTime = (TextView) mView.findViewById(R.id.txtTime);
                int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = mCalendar.get(Calendar.MINUTE);
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
                int year = mCalendar.get(Calendar.YEAR);
                int month = mCalendar.get(Calendar.MONTH);
                int day = mCalendar.get(Calendar.DAY_OF_MONTH);

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

                    SimpleDateFormat dtFormat = new SimpleDateFormat(Constants.DATE_FORMAT + Constants.TIME_FORMAT);
                    long timeInMilliseconds = 0;
                    try {
                        Date date = dtFormat.parse(mTxtDate.getText().toString() + mTxtTime.getText().toString());
                        timeInMilliseconds = date.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (mEventCategory.equals(Constants.CATEGORY_EVENT_INCOME)) {
                        if (mIdFromData != null) {
                            new Delete().from(IncomeItem.class).where("Id = ?", mIdFromData).execute();
                        }
                        IncomeItem incomeItem = new IncomeItem();
                        incomeItem.setDateTime(timeInMilliseconds);
                        incomeItem.setCash(Double.valueOf(mEditTxtSum.getText().toString()));
                        if (!Uri.EMPTY.equals(mPicUri)) {
                            incomeItem.setPhoto(String.valueOf(mPicUri));
                        }
                        incomeItem.setCategory(mCategory.getText().toString());
                        incomeItem.setComment(mTextComment.getText().toString());
                        incomeItem.save();
                    } else if (mEventCategory.equals(Constants.CATEGORY_EVENT_SPEND)) {
                        if (mIdFromData != null) {
                            new Delete().from(SpendItem.class).where("Id = ?", mIdFromData).execute();
                        }
                        SpendItem spendItem = new SpendItem();
                        spendItem.setDateTime(timeInMilliseconds);
                        spendItem.setCash(Double.valueOf(mEditTxtSum.getText().toString()));
                        if (!Uri.EMPTY.equals(mPicUri)) {
                            spendItem.setPhoto(String.valueOf(mPicUri));
                        }
                        spendItem.setCategory(mCategory.getText().toString());
                        spendItem.setComment(mTextComment.getText().toString());
                        spendItem.save();
                    }

                    Toast.makeText(getActivity().getApplicationContext(), R.string.saved, Toast.LENGTH_SHORT).show();

                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);

                    Activity act = getActivity();
                    if (act != null && act instanceof MainActivity){
                        ((MainActivity) act).openMainFragment();
                    }

                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.may_be_something_empty, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CAMERA_CAPTURE) { // processing of data on camera activity
            Picasso.with(getActivity().getApplicationContext()).load(mPicUri.toString()).into(mCheckImg);
        } else if (requestCode == Constants.CATEGORY_CAPTURE) { // processing of data on category activity
            // set category name
            if (resultCode == Activity.RESULT_OK) {
                String newCatNameTxt = data.getStringExtra(Constants.CATEGORY_NAME);
                mCategory.setText(newCatNameTxt);
            }
        } else if (requestCode == Constants.CALCULATOR_CAPTURE) { // processing of data on calculator activity
            if (resultCode == Activity.RESULT_OK) {
                String sum = data.getStringExtra("calcRes");
                mEditTxtSum.setText(sum);
            }
        } else if (requestCode == Constants.CURRENCY) { // processing of data on currency rates activity
            if (resultCode == Activity.RESULT_OK) {
                String sum = data.getStringExtra("currencyResSum");
                mEditTxtSum.setText(sum);
            }
        }
    }

}