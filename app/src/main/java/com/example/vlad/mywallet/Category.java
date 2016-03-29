package com.example.vlad.mywallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Vlad on 14.03.2016.
 */
public class Category extends AppCompatActivity {

    private static final String IMAGE_NAME_CHANGE = "change";
    private static final String IMAGE_NAME_DELETE = "delete";

    // arrayLists where stored id of elements. Category Name, button to change category,
    // button to remove category and row,  respectively
    private List<TextView> mTextViewList = new ArrayList<>();
    View.OnClickListener mClickListenerText;

    private List<ImageButton> mImageButtonChange = new ArrayList<>();
    View.OnClickListener mClickListenerButtonChange;

    private List<ImageButton> mImageButtonDelete = new ArrayList<>();
    View.OnClickListener mClickListenerButtonDelete;

    private List<TableRow> mTableRowList = new ArrayList<>();

    private int mCurrentId;

    // names of the files that store categories of expenses and incomes
    public String APP_PREFERENCES_CATEGORY_SPEND = "mywalletcategoryspend";
    public String APP_PREFERENCES_CATEGORY_INCOME = "mywalletcategoryincome";

    public SharedPreferences settingsCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_consmuption_category);

        // Accepts name of the class that caused current activity, and
        // indicates name of file that stores category with which to work
        String parentClass = getIntent().getExtras().getString("className").toLowerCase();
        if(parentClass.equals("consumptionmoney")) {
            settingsCategory = getSharedPreferences(APP_PREFERENCES_CATEGORY_SPEND, Context.MODE_PRIVATE);
        } else if (parentClass.equals("incomemoney")){
            settingsCategory = getSharedPreferences(APP_PREFERENCES_CATEGORY_INCOME, Context.MODE_PRIVATE);
        }
        createLayout();
    }

    /**
     * automatically creates layout for all existing categories
     */
    private void createLayout(){

        TableLayout tableLayout = (TableLayout)findViewById(R.id.container);

        // put all names of categories from file to arrayList
        Map<String, ?> allEntries = settingsCategory.getAll();
        List<String> list = new ArrayList<String>() ;
        for (Map.Entry<String, ?> entry : allEntries.entrySet()){
            list.add(entry.getValue().toString());
        }

        // for each category create rows, text with name of category,
        // buttons for change name and buttons for remove category
        for (int i = 0; i < list.size(); i++){
            String category = list.get(i);

            TableRow tableRow = makeTableRow(i);
            TextView textView = makeCategoryView(i, category);
            ImageButton imageButtonChange = makeButton(i, IMAGE_NAME_CHANGE);
            ImageButton imageButtonDelete = makeButton(i, IMAGE_NAME_DELETE);

            mClickListenerText = makeClickListenerOnCategoryName();
            mClickListenerButtonDelete =  makeClickListenerButtonDelete();
            mClickListenerButtonChange = makeClickListenerButtonChange();

            textView.setOnClickListener(mClickListenerText);
            mTextViewList.add(i, textView);

            imageButtonChange.setOnClickListener(mClickListenerButtonChange);
            mImageButtonChange.add(i, imageButtonChange);

            imageButtonDelete.setOnClickListener(mClickListenerButtonDelete);
            mImageButtonDelete.add(i, imageButtonDelete);

            mTableRowList.add(i, tableRow);

            tableRow.addView(textView);
            tableRow.addView(imageButtonChange);
            tableRow.addView(imageButtonDelete);
            tableLayout.addView(tableRow);
        }
    }

    /**
     * Handles the event by clicking on a category name
     * Sends selected name of category in the previous activity
     */
    private View.OnClickListener makeClickListenerOnCategoryName() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurrentId = v.getId();
                TextView currViewTxt = mTextViewList.get(mCurrentId);

                Toast.makeText(getApplicationContext(), "Вы выбрали категорию: "
                        + currViewTxt.getText(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra("catName", currViewTxt.getText().toString());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        };
    }

    /**
     * Handles the event by clicking on a delete button
     * Calls class a dialogFragment to confirm deletion category
     */
    private View.OnClickListener makeClickListenerButtonDelete() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurrentId = v.getId();
                confirmDeletionDialogFragment confirmDeletionDialogFragment = new confirmDeletionDialogFragment();
                confirmDeletionDialogFragment.show(getSupportFragmentManager(), "dialogDelete");

            }
        };
    }

    /**
     * When confirming the removal of categories, remove it from the file
     */
    public void okClickedYesDelete() {
        TextView currViewTxt = mTextViewList.get(mCurrentId);
        TableRow tr = mTableRowList.get(mCurrentId);
        settingsCategory.edit().remove(currViewTxt.getText().toString()).apply();

        currViewTxt.setVisibility(View.GONE);
        tr.setVisibility(View.GONE);

        Toast.makeText(getApplicationContext(), "Категория "
                + currViewTxt.getText() + " удалена", Toast.LENGTH_SHORT).show();
    }

    /**
     * Action for cancellation of removal category
     */
    public void cancelClickedNoDelete() {

    }

    /**
     * Handles the event by clicking on a change button
     * Calls the dialog box with editText, and then saves a new category name in file
     */
    private View.OnClickListener makeClickListenerButtonChange() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurrentId = v.getId();
                final TextView currViewTxt = mTextViewList.get(mCurrentId);

                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                final EditText edittext = new EditText(v.getContext());
                edittext.setText(currViewTxt.getText().toString());
                alert.setMessage("Введите новое название");
                alert.setTitle("Изменить категорию");

                alert.setView(edittext);

                alert.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String changeCatName = edittext.getText().toString();
                        if(changeCatName.length() > 0) {
                            String oldNameCategory = currViewTxt.getText().toString();

                            ContentValues newCatValue = new ContentValues(); // change name in database
                            DatabaseHelper databaseHelper  = new DatabaseHelper(getApplicationContext());

                            newCatValue.put(DatabaseHelper.CATEGORY_SPEND_COLUMN, changeCatName);

                            SQLiteDatabase db = databaseHelper.getWritableDatabase();
                            db.update(DatabaseHelper.DATABASE_TABLE_WALLET,
                                    newCatValue, DatabaseHelper.CATEGORY_SPEND_COLUMN + " =? ", new String[]{oldNameCategory});

                            currViewTxt.setText(changeCatName); // change name of new category
                            settingsCategory.edit().remove(oldNameCategory).apply();// delete last category from file
                            settingsCategory.edit().putString(changeCatName, changeCatName).apply(); // add new name of category in file

                            Toast.makeText(getApplicationContext(), changeCatName, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNegativeButton("Отмена", null);
                alert.show();
            }
        };
    }

    /**
     * Creates new button, and set parameters
     *
     * @param count - id for button
     * @param buttonName - identifier for the button image
     * @return new button
     */
    private ImageButton makeButton(int count, String buttonName) {
        int iconName = 0;
        if(buttonName.equals(IMAGE_NAME_CHANGE)){
            iconName = R.mipmap.ic_change;
        } else if (buttonName.equals(IMAGE_NAME_DELETE)){
            iconName = R.mipmap.ic_delete;
        }
        TableRow.LayoutParams imagePram = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f);
        imagePram.setMargins(0, 0, 20, 0);

        final ImageButton imageButtonChange = new ImageButton(this);
        imageButtonChange.setImageResource(iconName);
        imageButtonChange.setBackgroundColor(Color.WHITE);
        imageButtonChange.setPadding(0, 5, 0, 5);
        imageButtonChange.setId(count);
        imageButtonChange.setLayoutParams(imagePram);

        return imageButtonChange;
    }

    /**
     * Creates new textView for category name, and set parameters
     *
     * @param count - id for category name
     * @param categoryName - category name
     * @return - new textView
     */
    private TextView makeCategoryView(int count, String categoryName) {
        TableRow.LayoutParams lp = new TableRow.LayoutParams();
        lp.weight = 1;
        lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f);

        TextView textView = new TextView(this);
        textView.setText(categoryName);
        textView.setTextSize(23);
        textView.setTextColor(Color.BLACK);
        textView.setId(count);
        textView.setLayoutParams(lp);

        return textView;
    }

    /**
     * Creates new tableRow, and set parameters
     *
     * @param count - id for tableRow
     * @return - new tableRow
     */
    private TableRow makeTableRow(int count) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFFFFFFFF);
        gd.setCornerRadius(5);
        gd.setStroke(1, 0xFFA5A5A5);

        TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
        tableRowParams.setMargins(0, 10, 0, 10);

        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(20, 20, 20, 20);
        tableRow.setWeightSum(1);
        tableRow.setId(count);
        tableRow.setBackground(gd);
        tableRow.setLayoutParams(tableRowParams);

        return tableRow;
    }

    /**
     *  Handles the event by clicking on a addNewCategory button
     *  Calls the dialog box with the ability enter a new category name
     */
    public void onButtonAddNewCategoryClick(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        final EditText newCategory = new EditText(v.getContext());
        newCategory.setSingleLine(true);
        newCategory.setPadding(20, 5, 20, 5);
        alert.setTitle("Введите название новой категории");

        alert.setView(newCategory);

        // By clicking on the save button sends the entered data to the basic activity
        alert.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newCatNameTxt = newCategory.getText().toString();
                if (newCatNameTxt.length() > 0) {

                    Toast.makeText(getApplicationContext(), "Создана новая категория: " +
                            newCatNameTxt, Toast.LENGTH_SHORT).show();

                    settingsCategory.edit().putString(newCatNameTxt, newCatNameTxt).apply();

                    Intent intent = new Intent();
                    intent.putExtra("catName", newCatNameTxt);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });

        alert.setNegativeButton("Отмена", null);
        alert.show();
    }




}
