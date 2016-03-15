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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    private List<TextView> mTextViewList = new ArrayList<>();
    View.OnClickListener mClickListenerText;

    private List<ImageButton> mImageButtonChange = new ArrayList<>();
    View.OnClickListener mClickListenerButtonChange;

    private List<ImageButton> mImageButtonDelete = new ArrayList<>();
    View.OnClickListener mClickListenerButtonDelete;

    private List<TableRow> mTableRow = new ArrayList<>();

    private int mCurrentId;


    public String APP_PREFERENCES_CATEGORY_SPEND = "mywalletcategoryspend";

    public SharedPreferences settingsCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_consmuption_category);
        settingsCategory = getSharedPreferences(APP_PREFERENCES_CATEGORY_SPEND, Context.MODE_PRIVATE);


        createLayout();
    }

    private void createLayout(){

        settingsCategory = getSharedPreferences(APP_PREFERENCES_CATEGORY_SPEND, Context.MODE_PRIVATE);
        TableLayout tableLayout = (TableLayout)findViewById(R.id.container);

        Map<String, ?> allEntries = settingsCategory.getAll();
        List<String> list = new ArrayList<String>() ;
        for (Map.Entry<String, ?> entry : allEntries.entrySet()){
            list.add(entry.getValue().toString());
        }

        for (int i = 0; i < list.size(); i++){
            String category = list.get(i);

           // userCursor.moveToLast();
           // String catName = userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.COMMENT_COLUMN));

            TableRow tableRow = new TableRow(this);
            tableRow.setPadding(0, 10, 0, 10);
            tableRow.setWeightSum(1);
            tableRow.setId(i);


            TextView textView = new TextView(this);
            textView.setText(category);
            textView.setTextSize(23);
            textView.setTextColor(Color.BLACK);
            textView.setId(i);
            TableRow.LayoutParams lp = new TableRow.LayoutParams();
            lp.weight = 1;
            lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f);
            textView.setLayoutParams(lp);

            final ImageButton imageButton1 = new ImageButton(this);
            imageButton1.setImageResource(R.mipmap.ic_change);
            imageButton1.setBackgroundColor(Color.WHITE);
            imageButton1.setPadding(5, 5, 5, 5);
            imageButton1.setId(i);
            lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f);
            imageButton1.setLayoutParams(lp);

            ImageButton imageButton2 = new ImageButton(this);
            imageButton2.setImageResource(R.mipmap.ic_delete);
            imageButton2.setBackgroundColor(Color.WHITE);
            imageButton2.setPadding(5, 5, 5, 5);
            imageButton2.setId(i);
            imageButton2.setLayoutParams(lp);

            mClickListenerText = new View.OnClickListener() {

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

            mClickListenerButtonDelete =  new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mCurrentId = v.getId();

                    confirmDeletionDialogFragment confirmDeletionDialogFragment = new confirmDeletionDialogFragment();
                    confirmDeletionDialogFragment.show(getSupportFragmentManager(), "dialogDelete");

                }
            };

            mClickListenerButtonChange = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mCurrentId = v.getId();
                    final TextView currViewTxt = mTextViewList.get(mCurrentId);

//                    ChangeNameCategoryDialogFragment newNameCatFragment = new ChangeNameCategoryDialogFragment();
//                    newNameCatFragment.show(getSupportFragmentManager(), "dialogChangeCat");
                    AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                    final EditText edittext = new EditText(v.getContext());
                    edittext.setText(currViewTxt.getText().toString());
                    alert.setMessage("Введите новое название");
                    alert.setTitle("Изменить категорию");

                    alert.setView(edittext);

                    alert.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("AddCat", "1");
                            String changeCatName = edittext.getText().toString();
                            Log.d("AddCat", "2");
                            if(changeCatName.length() > 0) {
                                Log.d("AddCat", "3");
                                String oldNameCategory = currViewTxt.getText().toString();

                                Log.d("AddCat", "4");

                                ContentValues newCatValue = new ContentValues(); // изменяет название в базе данных
                                Log.d("AddCat", "5");
                                DatabaseHelper databaseHelper  = new DatabaseHelper(getApplicationContext());
                                Log.d("AddCat", "6");

                                newCatValue.put(DatabaseHelper.CATEGORY_SPEND_COLUMN, changeCatName);
                                Log.d("AddCat", "7");

                                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                                Log.d("AddCat", "8");
                                db.update(DatabaseHelper.DATABASE_TABLE_WALLET,
                                        newCatValue, DatabaseHelper.CATEGORY_SPEND_COLUMN + " =? ", new String[]{oldNameCategory});
                                Log.d("AddCat", "9");

                                currViewTxt.setText(changeCatName); // меняет текст новой категории
                                settingsCategory.edit().remove(oldNameCategory).apply();// удаляет старую категорию из файла
                                settingsCategory.edit().putString(changeCatName, changeCatName).apply(); // добавляет новое название категории

                                Toast.makeText(getApplicationContext(), changeCatName, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    alert.setNegativeButton("Отмена", null);
                    alert.show();
                }
            };

            textView.setOnClickListener(mClickListenerText);
            mTextViewList.add(i, textView);

            imageButton1.setOnClickListener(mClickListenerButtonChange);
            mImageButtonChange.add(i, imageButton1);

            imageButton2.setOnClickListener(mClickListenerButtonDelete);
            mImageButtonDelete.add(i, imageButton2);

            mTableRow.add(i, tableRow);

            tableRow.addView(textView);
            tableRow.addView(imageButton1);
            tableRow.addView(imageButton2);
            tableLayout.addView(tableRow);
        }
    }

    // add new category
    public void onButtonAddNewCategoryClick(View view){
        EditText newCatName = (EditText)findViewById(R.id.textNewCategory);
        String newCatNameTxt = newCatName.getText().toString();

        if (newCatNameTxt.length() > 0) {
            Toast.makeText(getApplicationContext(), "Создана новая категория: " + newCatNameTxt, Toast.LENGTH_SHORT).show();

            settingsCategory.edit().putString(newCatNameTxt, newCatNameTxt).apply();

            Intent intent = new Intent();
            intent.putExtra("catName", newCatNameTxt);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    public void okClickedYesDelete() {
        TextView currViewTxt = mTextViewList.get(mCurrentId);
        TableRow tr = mTableRow.get(mCurrentId);
        settingsCategory.edit().remove(currViewTxt.getText().toString()).apply();

                    currViewTxt.setVisibility(View.GONE);
                    tr.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Категория "
                            + currViewTxt.getText() + " удалена", Toast.LENGTH_SHORT).show();
    }

    public void cancelClickedNoDelete() {
      //  Toast.makeText(getApplicationContext(), "Вы выбрали кнопку отмены!",
      //          Toast.LENGTH_LONG).show();
    }
}
