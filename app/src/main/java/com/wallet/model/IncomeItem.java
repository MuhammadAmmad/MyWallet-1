package com.wallet.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Table(name = "IncomeItem")
public class IncomeItem extends Model {
    @Column(name = "dateTime")
    private long dateTime;

    @Column(name = "cash")
    private double cash;

    @Column(name = "category")
    private String category;

    @Column(name = "photo")
    private String photo;

    @Column(name = "comment")
    private String comment;

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public IncomeItem() {
        super();
    }

    public IncomeItem(int dateTime, Double cash, String category, String photo, String comment) {
        super();
        this.dateTime = dateTime;
        this.cash = cash;
        this.category = category;
        this.photo = photo;
        this.comment = comment;
    }

    public static List<IncomeItem> getAll() {
        return new Select()
                .from(IncomeItem.class)
                .execute();
    }

    public static IncomeItem getLast() {
        return new Select()
                .from(IncomeItem.class).orderBy("id DESC")
                .executeSingle();
    }

    public static List<IncomeItem> getAllByName(String categoryName) {
        return new Select()
                .from(IncomeItem.class)
                .where("category = '" + categoryName + "'")
                .execute();
    }

    public void changeCategoryName(String oldName, String newName) {
        new Update(IncomeItem.class)
                .set("category = '" + newName + "'")
                .where("category = '" + oldName + "'")
                .execute();
    }

    public static double getAllSum() {
        List<IncomeItem> incomeItem = new Select()
                .from(IncomeItem.class)
                .execute();

        return getSum(incomeItem);
    }

    public static double getIncomeOfMonth() {
        List<IncomeItem> incomeItem = new Select()
                .from(IncomeItem.class)
                .where("strftime('%Y-%m-%d %H:%M', dateTime / 1000, 'unixepoch') >= datetime('now', '-1 month', '-5 minutes')")
                .execute();

        return getSum(incomeItem);
    }

    private static double getSum(List<IncomeItem> incomeItem) {
        double res = 0.00;
        for (int i = 0; i < incomeItem.size(); i++) {
            res += incomeItem.get(i).cash;
        }
        return res;
    }

    public static double[] getIncomeArrayOfYear(int numberOfYear) {
        double[] res = new double[12];
        List<IncomeItem> incomeItem = new Select()
                .from(IncomeItem.class)
                .where("strftime('%Y', dateTime / 1000, 'unixepoch') = '" + numberOfYear + "'")
                .execute();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        for (int i = 0; i < incomeItem.size(); i++) {
            calendar.setTimeInMillis(incomeItem.get(i).dateTime);
            int month = calendar.get(Calendar.MONTH);
            res[month] += incomeItem.get(i).cash;
        }

        return res;
    }

    public static double getAllSumFromYear(int numberOfYear) {
        double res = 0.0;
        List<IncomeItem> incomeItem = new Select()
                .from(IncomeItem.class)
                .where("strftime('%Y', dateTime / 1000, 'unixepoch') = '" + numberOfYear + "'")
                .execute();

        for (int i = 0; i < incomeItem.size(); i++) {
            res += incomeItem.get(i).cash;
        }

        return res;
    }

    public static IncomeItem getFromId(String id) {
        return new Select()
                .from(IncomeItem.class)
                .where("Id = ?", id)
                .executeSingle();
    }
}