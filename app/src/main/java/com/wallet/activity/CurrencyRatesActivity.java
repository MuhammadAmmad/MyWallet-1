package com.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wallet.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * This class allows a user to convert a chosen
 * currency into hryvnia. Used api Privat Bank
 */


public class CurrencyRatesActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText valueOfCurrency;
    String currencyValueStr;

    String apiAddress = "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11";

    String currencyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_rates);

        currencyName = getIntent().getExtras().getString("CurrencyName");
        TextView currencyNameView = (TextView)findViewById(R.id.currencyName);
        currencyNameView.setText(currencyName);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        valueOfCurrency = (EditText)findViewById(R.id.valueOfCurrency);
    }


    public void onButtonGetCurrencyClick(View view) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
            new MyTask().execute();
    }


    public void onButtonEqualsClick(View view) {
        TextView sumCurrency = (TextView)findViewById(R.id.sumCurrency);
        TextView valueOfCurrency = (TextView)findViewById(R.id.valueOfCurrency);

        double resSumDouble = Double.valueOf(sumCurrency.getText().toString()) *
                Double.valueOf(valueOfCurrency.getText().toString());
        // Removes zeros after decimal point
        resSumDouble = Math.round(resSumDouble * 100.0) / 100.0;
        TextView resSum = (TextView)findViewById(R.id.resSum);
        resSum.setText(String.valueOf(resSumDouble));
    }


    public void onButtonSaveResultClick(View view) {
        EditText result = (EditText)findViewById(R.id.resSum);
        Intent intent = new Intent();
        intent.putExtra("currencyResSum", result.getText().toString());
        setResult(Activity.RESULT_OK, intent);

        finish();
    }


    private class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... path) {

            try {
                // Falling asleep to the progress bar displays not less than the specified time
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String content;
            try {
                content = getContent(apiAddress);
                // takes api data depending on desired currency
                try {
                    JSONArray jsonArray = new JSONArray(content);
                    if (currencyName.equals("Доллар")){
                        JSONObject jsonObject = new JSONObject(jsonArray.getString(0));
                        currencyValueStr = jsonObject.getString("buy");
                    } else if (currencyName.equals("Евро")){
                        JSONObject jsonObject = new JSONObject(jsonArray.getString(1));
                        currencyValueStr = jsonObject.getString("buy");
                    } else if (currencyName.equals("Рубль")){
                        JSONObject jsonObject = new JSONObject(jsonArray.getString(2));
                        currencyValueStr = jsonObject.getString("buy");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException ex) {
                content = ex.getMessage();
            }
            return content;
        }

        @Override
        protected void onProgressUpdate(Void... items) {
        }

        @Override
        protected void onPostExecute(String content) {
            // displays resulting exchange rate
            double res = Double.valueOf(currencyValueStr);
            res = Math.round(res * 100.0) / 100.0;
            valueOfCurrency.setText(String.valueOf(res));
            progressBar.setVisibility(View.INVISIBLE);
        }

        private String getContent(String path) throws IOException {
            BufferedReader reader = null;
            try {
                URL url = new URL(path);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setReadTimeout(10000);
                c.connect();
                reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder buf = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buf.append(line + "\n");
                }
                return (buf.toString());
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
    }

}


