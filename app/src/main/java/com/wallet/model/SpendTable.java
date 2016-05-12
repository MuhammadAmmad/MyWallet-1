package com.wallet.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.wallet.Constants;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Table(name = "SpendTable")
public class SpendTable extends Model implements Constants {
    @Column(name = "dateTime")
    public long dateTime;

    @Column(name = "cash")
    public double cash;

    @Column(name = "category")
    public String category;

    @Column(name = "photo")
    public String photo;

    @Column(name = "comment")
    public String comment;


    public SpendTable() {
        super();
    }

    public SpendTable(int dateTime, Double cash, String category, String photo, String comment) {
        super();
        this.dateTime = dateTime;
        this.cash = cash;
        this.category = category;
        this.photo = photo;
        this.comment = comment;
    }

    public static List<SpendTable> getAll() {
        return new Select()
                .from(SpendTable.class)
                .execute();
    }

    public static SpendTable getLast() {
        return new Select()
                .from(SpendTable.class).orderBy("id DESC")
                .executeSingle();
    }

    public static List<SpendTable> getAllByName(String categoryName) {
        return new Select()
                .from(SpendTable.class)
                .where("category = '" + categoryName + "'")
                .execute();
    }

    public void changeCategoryName(String oldName, String newName) {
        new Update(SpendTable.class)
                .set("category = '" + newName + "'")
                .where("category = '" + oldName + "'")
                .execute();
    }

    public static double getAllSum() {
        List<SpendTable> spendTable = new Select()
                .from(SpendTable.class)
                .execute();

        return getSum(spendTable);
    }

    public static double getSpendOfDay() {
        List<SpendTable> spendTable = new Select()
                .from(SpendTable.class)
                .where("strftime('%Y-%m-%d %H:%M', dateTime / 1000, 'unixepoch') >= datetime('now', '-1 day', '-5 minutes')")
                .execute();

        return getSum(spendTable);
    }

    public static double getSpendOfWeek() {
        List<SpendTable> spendTable = new Select()
                .from(SpendTable.class)
                .where("strftime('%Y-%m-%d %H:%M', dateTime / 1000, 'unixepoch') >= datetime('now', '-7 days', '-5 minutes')")
                .execute();

        return getSum(spendTable);
    }


    public static double getSpendOfMonth() {
        List<SpendTable> spendTable = new Select()
                .from(SpendTable.class)
                .where("strftime('%Y-%m-%d %H:%M', dateTime / 1000, 'unixepoch') >= datetime('now', '-1 month', '-5 minutes')")
                .execute();

        return getSum(spendTable);
    }

    private static double getSum(List<SpendTable> spendTable) {
        double res = 0.00;
        for (int i = 0; i < spendTable.size(); i++) {
            res += spendTable.get(i).cash;
        }
        return res;
    }

    public static double[] getSpendArrayOfYear(int numberOfYear) {
        double[] res = new double[12];
        List<SpendTable> spendTable = new Select()
                .from(SpendTable.class)
                .where("strftime('%Y', dateTime / 1000, 'unixepoch') = '" + numberOfYear + "'")
                .execute();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        for (int i = 0; i < spendTable.size(); i++){
            calendar.setTimeInMillis(spendTable.get(i).dateTime);
            int month = calendar.get(Calendar.MONTH);
            res[month] += spendTable.get(i).cash;
        }

        return res;
    }

    public static double getAllSumFromYear(int numberOfYear) {
        double res = 0.0;
        List<SpendTable> spendTable = new Select()
                .from(SpendTable.class)
                .where("strftime('%Y', dateTime / 1000, 'unixepoch') = '" + numberOfYear + "'")
                .execute();

        for (int i = 0; i < spendTable.size(); i++){
            res += spendTable.get(i).cash;
        }

        return res;
    }

    public static SpendTable getFromId(String id) {
        return new Select()
                .from(SpendTable.class)
                .where("Id = ?", id)
                .executeSingle();
    }
}