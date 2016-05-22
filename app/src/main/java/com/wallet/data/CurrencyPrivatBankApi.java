package com.wallet.data;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CurrencyPrivatBankApi {
    @GET("p24api/pubinfo?exchange&json&coursid=11")
    Call<ResponseBody> getCurrency();
}
