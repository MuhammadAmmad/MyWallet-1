package com.wallet.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Table(name = "SpendItem")
public class SpendItem extends Model {
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


    public SpendItem() {
        super();
    }

    public SpendItem(int dateTime, Double cash, String category, String photo, String comment) {
        super();
        this.dateTime = dateTime;
        this.cash = cash;
        this.category = category;
        this.photo = photo;
        this.comment = comment;
    }

    public static List<SpendItem> getAll() {
        return new Select()
                .from(SpendItem.class)
                .execute();
    }

    public static SpendItem getLast() {
        return new Select()
                .from(SpendItem.class).orderBy("id DESC")
                .executeSingle();
    }

    public static List<SpendItem> getAllByName(String categoryName) {
        return new Select()
                .from(SpendItem.class)
                .where("category = '" + categoryName + "'")
                .execute();
    }

    public void changeCategoryName(String oldName, String newName) {
        new Update(SpendItem.class)
                .set("category = '" + newName + "'")
                .where("category = '" + oldName + "'")
                .execute();
    }

    public static double getAllSum() {
        List<SpendItem> spendItem = new Select()
                .from(SpendItem.class)
                .execute();

        return getSum(spendItem);
    }

    public static double getSpendOfDay() {
        List<SpendItem> spendItem = new Select()
                .from(SpendItem.class)
                .where("strftime('%Y-%m-%d %H:%M', dateTime / 1000, 'unixepoch') >= datetime('now', '-1 day', '-5 minutes')")
                .execute();

        return getSum(spendItem);
    }

    public static double getSpendOfWeek() {
        List<SpendItem> spendItem = new Select()
                .from(SpendItem.class)
                .where("strftime('%Y-%m-%d %H:%M', dateTime / 1000, 'unixepoch') >= datetime('now', '-7 days', '-5 minutes')")
                .execute();

        return getSum(spendItem);
    }


    public static double getSpendOfMonth() {
        List<SpendItem> spendItem = new Select()
                .from(SpendItem.class)
                .where("strftime('%Y-%m-%d %H:%M', dateTime / 1000, 'unixepoch') >= datetime('now', '-1 month', '-5 minutes')")
                .execute();

        return getSum(spendItem);
    }

    private static double getSum(List<SpendItem> spendItem) {
        double res = 0.00;
        for (int i = 0; i < spendItem.size(); i++) {
            res += spendItem.get(i).cash;
        }
        return res;
    }

    public static double[] getSpendArrayOfYear(int numberOfYear) {
        double[] res = new double[12];
        List<SpendItem> spendItem = new Select()
                .from(SpendItem.class)
                .where("strftime('%Y', dateTime / 1000, 'unixepoch') = '" + numberOfYear + "'")
                .execute();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        for (int i = 0; i < spendItem.size(); i++) {
            calendar.setTimeInMillis(spendItem.get(i).dateTime);
            int month = calendar.get(Calendar.MONTH);
            res[month] += spendItem.get(i).cash;
        }

        return res;
    }

    public static double getAllSumFromYear(int numberOfYear) {
        double res = 0.0;
        List<SpendItem> spendItem = new Select()
                .from(SpendItem.class)
                .where("strftime('%Y', dateTime / 1000, 'unixepoch') = '" + numberOfYear + "'")
                .execute();

        for (int i = 0; i < spendItem.size(); i++) {
            res += spendItem.get(i).cash;
        }

        return res;
    }

    public static SpendItem getFromId(String id) {
        return new Select()
                .from(SpendItem.class)
                .where("Id = ?", id)
                .executeSingle();
    }
}