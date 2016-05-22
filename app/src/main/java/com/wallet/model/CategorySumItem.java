package com.wallet.model;

import com.activeandroid.Model;

public class CategorySumItem extends Model {

    private String category;
    private String sum;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }


    public CategorySumItem() {
        super();
    }

    public CategorySumItem(String photo, String comment) {
        super();
        this.category = photo;
        this.sum = comment;
    }
}