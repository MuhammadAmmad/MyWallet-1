package com.wallet.data;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ApiImpl {

    public interface ICurrencyListener{
        void onSuccess(double currencyValue);
        void onFailure();
    }

    private final String URL = "https://api.privatbank.ua/";
    public Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URL)
            .build();

    public ApiImpl(){

    }

    public double getCurrency(final String currencyName, final ICurrencyListener listener){

        CurrencyPrivatBankApi service = retrofit.create(CurrencyPrivatBankApi.class);
        Call<ResponseBody> result = service.getCurrency();
        final double[] res = {0};

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    CurrencyApiParser parser = new CurrencyApiParser(response.body().string());
                    res[0] = Double.valueOf(parser.getBuyPrice(currencyName));
                    res[0] = Math.round(res[0] * 100.0) / 100.0;
                    if (listener != null){
                        listener.onSuccess(res[0]);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onFailure();
            }

        });

        return res[0];
    }

}
