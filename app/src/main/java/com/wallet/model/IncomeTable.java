package com.wallet.model;

import android.content.ClipData;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Table(name = "IncomeTable")
public class IncomeTable extends Model {
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


    public IncomeTable() {
        super();
    }

    public IncomeTable(int dateTime, Double cash, String category, String photo, String comment) {
        super();
        this.dateTime = dateTime;
        this.cash = cash;
        this.category = category;
        this.photo = photo;
        this.comment = comment;
    }

    public static List<IncomeTable> getAll() {
        return new Select()
                .from(IncomeTable.class)
                .execute();
    }

    public static IncomeTable getLast() {
        return new Select()
                .from(IncomeTable.class).orderBy("id DESC")
                .executeSingle();
    }

    public static List<IncomeTable> getAllByName(String categoryName) {
        return new Select()
                .from(IncomeTable.class)
                .where("category = '" + categoryName + "'")
                .execute();
    }

    public void changeCategoryName(String oldName, String newName) {
         new Update(IncomeTable.class)
                .set("category = '" + newName + "'")
                .where("category = '" + oldName + "'")
                .execute();
    }

    public static double getAllSum() {
        List<IncomeTable> incomeTable = new Select()
                .from(IncomeTable.class)
                .execute();

        return getSum(incomeTable);
    }

    public static double getIncomeOfMonth() {
        List<IncomeTable> incomeTable = new Select()
                .from(IncomeTable.class)
                .where("strftime('%Y-%m-%d %H:%M', dateTime / 1000, 'unixepoch') >= datetime('now', '-1 month', '-5 minutes')")
                .execute();

        return getSum(incomeTable);
    }

    private static double getSum(List<IncomeTable> incomeTable) {
        double res = 0.00;
        for (int i = 0; i < incomeTable.size(); i++) {
            res += incomeTable.get(i).cash;
        }
        return res;
    }

    public static double[] getIncomeArrayOfYear(int numberOfYear) {
        double[] res = new double[12];
        List<IncomeTable> incomeTable = new Select()
                .from(IncomeTable.class)
                .where("strftime('%Y', dateTime / 1000, 'unixepoch') = '" + numberOfYear + "'")
                .execute();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        for (int i = 0; i < incomeTable.size(); i++){
            calendar.setTimeInMillis(incomeTable.get(i).dateTime);
            int month = calendar.get(Calendar.MONTH);
            res[month] += incomeTable.get(i).cash;
        }

        return res;
    }

    public static double getAllSumFromYear(int numberOfYear) {
        double res = 0.0;
        List<IncomeTable> incomeTable = new Select()
                .from(IncomeTable.class)
                .where("strftime('%Y', dateTime / 1000, 'unixepoch') = '" + numberOfYear + "'")
                .execute();

        for (int i = 0; i < incomeTable.size(); i++){
            res += incomeTable.get(i).cash;
        }

        return res;
    }

    public static IncomeTable getFromId(String id) {
        return new Select()
                .from(IncomeTable.class)
                .where("Id = ?", id)
                .executeSingle();
    }
}