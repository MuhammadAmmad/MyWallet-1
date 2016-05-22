package com.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wallet.data.ApiImpl;
import com.wallet.R;


public class CurrencyRatesActivity extends AppCompatActivity {

    private String mCurrencyName, mCurrencyBaseValue;
    private EditText mValueOfCurrency, mResult;
    private ApiImpl mApi;
    private TextView sumCurrency;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_rates);

        mCurrencyName = getIntent().getExtras().getString("CurrencyName");
        mCurrencyBaseValue = getIntent().getExtras().getString("CurrencySum");
        mApi = new ApiImpl();

        initViews();
    }

    private void initViews() {
        mValueOfCurrency = (EditText) findViewById(R.id.valueOfCurrency);
        sumCurrency = (TextView) findViewById(R.id.sumCurrency);
        mResult = (EditText) findViewById(R.id.resSum);
        ((TextView) findViewById(R.id.currencyName)).setText(mCurrencyName);
        findViewById(R.id.buttonGetCurrency).setOnClickListener(onButtonGetCurrencyClick);
        findViewById(R.id.buttonEqually).setOnClickListener(onButtonEqualsClick);
        findViewById(R.id.buttonSaveResult).setOnClickListener(onButtonSaveResultClick);

        if(!mCurrencyBaseValue.isEmpty())
            sumCurrency.setText(mCurrencyBaseValue);

    }


    private View.OnClickListener onButtonEqualsClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            double resSumDouble = Double.valueOf(sumCurrency.getText().toString()) *
                    Double.valueOf(mValueOfCurrency.getText().toString());
            // Removes zeros after decimal point
            resSumDouble = Math.round(resSumDouble * 100.0) / 100.0;
            mResult.setText(String.valueOf(resSumDouble));
        }
    };

    private View.OnClickListener onButtonSaveResultClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("currencyResSum", mResult.getText().toString());
            setResult(Activity.RESULT_OK, intent);

            finish();
        }
    };

    private View.OnClickListener onButtonGetCurrencyClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            mApi.getCurrency(mCurrencyName, new ApiImpl.ICurrencyListener() {
                @Override
                public void onSuccess(double currencyValue) {
                    mValueOfCurrency.setText(String.valueOf(currencyValue));
                }

                @Override
                public void onFailure() {
                    Toast.makeText(getApplicationContext(), R.string.something_was_wrong,
                            Toast.LENGTH_LONG).show();
                }
            });

        }
    };

}