package com.wallet.data;

import com.wallet.model.CurrencyItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CurrencyApiParser {
    private String mApi;
    private List<CurrencyItem> mCurrencyItemList = new ArrayList<>();

    public CurrencyApiParser(String api){
        mApi = api;
        makeCurrencyItemList();
    }

    private void makeCurrencyItemList(){

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(mApi);

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                CurrencyItem currencyItem = new CurrencyItem();
                currencyItem.setBaseCurrency(jsonObject.getString("base_ccy"));
                currencyItem.setCurrency(jsonObject.getString("ccy"));
                currencyItem.setBuy(jsonObject.getString("buy"));
                currencyItem.setSale(jsonObject.getString("sale"));
                currencyItem.save();
                mCurrencyItemList.add(currencyItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public int getObjectSize(){
        return mCurrencyItemList.size();
    }

    public String getBuyPrice(String currencyName){

        for (int i = 0; i < mCurrencyItemList.size(); i++){
            if (mCurrencyItemList.get(i).getCurrency().equals(currencyName)){
                return mCurrencyItemList.get(i).getBuy();
            }
        }
        return null;
    }

    public String getSalePrice(String currencyName){

        for (int i = 0; i < mCurrencyItemList.size(); i++){
            if (mCurrencyItemList.get(i).getCurrency().equals(currencyName)){
                return mCurrencyItemList.get(i).getSale();
            }
        }
        return null;
    }

    public String getBaseCurrency(String currencyName){

        for (int i = 0; i < mCurrencyItemList.size(); i++){
            if (mCurrencyItemList.get(i).getCurrency().equals(currencyName)){
                return mCurrencyItemList.get(i).getBaseCurrency();
            }
        }
        return null;
    }

}
