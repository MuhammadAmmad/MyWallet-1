package com.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.wallet.R;

import java.util.Stack;
import java.util.Vector;

/**
 * This class is a calculator. Using the algorithm sorting
 * station turns into a kind of equation Reverse Polish Notation,
 * and then solves equation on the basis of this entry.
 */

public class CalculatorActivity extends AppCompatActivity implements OnClickListener {

    private EditText mViewResCalc;
    private TextView mViewResText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        initViews();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_calc_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuSaveResult) {
            transferResult();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void transferResult() {
        String resTxt = mViewResCalc.getText().toString();

        // If result is not correct, it cuts off only last number
        int startIndex = 0;
        for (int i = resTxt.length() - 1; i >= 0; i--) {
            if (isNumber(resTxt.charAt(i))) {
                startIndex = i;
            } else {
                break;
            }
        }
        resTxt = resTxt.substring(startIndex, resTxt.length());

        Intent intent = new Intent();
        intent.putExtra("calcRes", resTxt);
        setResult(Activity.RESULT_OK, intent);

        finish();
    }

    private void checkAndAddPoint() {

        // check if there is already a point in the number, it does not set a new
        String string = mViewResCalc.getText().toString();
        Character lastChar = ' ';
        if (string.length() > 0) {
            if (string.lastIndexOf(".") <= string.lastIndexOf("+") ||
                    string.lastIndexOf(".") <= string.lastIndexOf("-") ||
                    string.lastIndexOf(".") <= string.lastIndexOf("*") ||
                    string.lastIndexOf(".") <= string.lastIndexOf("/") ||
                    string.lastIndexOf(".") <= string.lastIndexOf("(") ||
                    string.lastIndexOf(".") <= string.lastIndexOf(")")) {
                lastChar = string.charAt(string.length() - 1);

                if (lastChar == '/' || lastChar == '+' || lastChar == '-' || lastChar == '*') {
                    mViewResCalc.setText(string + "0.");
                } else {
                    addToEquation(".");
                }
            }
        } else {
            addToEquation("0."); // if the point is added to very first expression, then it puts zero
        }
    }

    /**
     * Pressing the "/" button is added to the equation division sign
     */
    private void addDivision() {
        String string = mViewResCalc.getText().toString();
        Character lastChar = ' ';
        // check that division was not the first sign in the equation
        if (string.length() > 0) {
            lastChar = string.charAt(string.length() - 1);

            // checks if the last character of the equation sign of mathematical
            // operation, it changes to a division sign
            if (lastChar == '/' || lastChar == '+' || lastChar == '-' || lastChar == '*') {
                mViewResCalc.setText(string.substring(0, string.length() - 1) + "/");
            } else {
                addToEquation("/");
            }
        } else {
            addToEquation("0/");
        }
    }

    private void addPlus() {
        String string = mViewResCalc.getText().toString();
        Character lastChar = ' ';
        // check that plus was not the first sign in the equation
        if (string.length() > 0) {
            lastChar = string.charAt(string.length() - 1);

            // checks if the last character of the equation sign of mathematical
            // operation, it changes to a plus sign
            if (lastChar == '/' || lastChar == '+' || lastChar == '-' || lastChar == '*') {
                mViewResCalc.setText(string.substring(0, string.length() - 1) + "+");
            } else {
                addToEquation("+");
            }
        } else {
            addToEquation("0+");
        }
    }

    private void addMultiply() {
        String string = mViewResCalc.getText().toString();
        Character lastChar = ' ';
        // check that multiplication was not the first sign in the equation
        if (string.length() > 0) {
            lastChar = string.charAt(string.length() - 1);

            // checks if the last character of the equation sign of mathematical
            // operation, it changes to a multiplication sign
            if (lastChar == '/' || lastChar == '+' || lastChar == '-' || lastChar == '*') {
                mViewResCalc.setText(string.substring(0, string.length() - 1) + "*");
            } else {
                addToEquation("*");
            }
        } else {
            addToEquation("0*");
        }
    }

    private void addMinus() {
        String string = mViewResCalc.getText().toString();
        Character lastChar = ' ';
        // check that minus was not the first sign in the equation
        if (string.length() > 0) {
            lastChar = string.charAt(string.length() - 1);

            // checks if the last character of the equation sign of mathematical
            // operation, it changes to a minus sign
            if (lastChar == '/' || lastChar == '+' || lastChar == '-' || lastChar == '*') {
                mViewResCalc.setText(string.substring(0, string.length() - 1) + "-");
            } else {
                addToEquation("-");
            }
        } else {
            addToEquation("0-");
        }
    }

    /**
     * by pressing the "C" button clears the current and last equation
     */
    private void clearResView() {
        mViewResCalc.setText("");
        mViewResCalc.setHint("0");
        mViewResText.setText("");
    }

    /**
     * By clicking on the button "<" removes the last character in the equation
     */
    private void clearOneSymbol() {
        String string = mViewResCalc.getText().toString();
        if (string.length() > 0) {
            mViewResCalc.setText(string.substring(0, string.length() - 1));
        } else { // when deleting last character then installed value by default
            mViewResCalc.setHint("0");
        }
    }

    private void addToEquation(String text) {
        String string = mViewResCalc.getText().toString();
        string += text;
        mViewResCalc.setText(string);
    }

    private void solveEquation() {
        String equation = mViewResCalc.getText().toString();
        if (equation.length() > 0) { // checks that the equation was not empty

            // counts the number of operators and parentheses
            int colRightBkt = 0, colLeftBkt = 0, colOperator = 0;

            for (int i = 0; i < equation.length(); i++) {
                if (equation.charAt(i) == '(') colLeftBkt++;
                if (equation.charAt(i) == ')') colRightBkt++;
                if (isOperator(equation.charAt(i))) colOperator++;
            }

            // if left in equation parentheses more than right, they are balanced
            for (int i = 0; i < colLeftBkt - colRightBkt; i++) {
                equation += ")";
            }

            TextView viewResText = (TextView) findViewById(R.id.lastEquation);

            int firstLeftBkt = 0, firstRightBkt = 0;
            if (equation.indexOf("(") > 0) firstLeftBkt = equation.indexOf("(");
            if (equation.indexOf(")") > 0) firstRightBkt = equation.indexOf(")");

            if (firstLeftBkt <= firstRightBkt) { // checks to left bracket was not after the right bracket

                if (colOperator > 0) { // equation will be counted if the operator is greater than zero

                    // gets equation in the form of reverse Polish notation
                    Vector<String> polishRecord = polishInvertedRecord(equation);

                    // takes equation in the form of reverse Polish notation, and returns the result
                    String res = getResult(polishRecord);

                    // if the number is not fractional, then cuts zero and point
                    int indexPoint = res.indexOf(".");
                    if (res.charAt(indexPoint + 1) == '0' && res.length() - 1 == indexPoint + 1) {
                        res = res.substring(0, indexPoint);
                    }
                    if (res.equals(getString(R.string.error))) { // writes an error if the equation has been set incorrectly
                        viewResText.setText(R.string.error);
                        mViewResCalc.setText("");
                    } else { // sets the whole equation, and the result
                        mViewResCalc.setText(String.valueOf(res));
                        viewResText.setText(equation + "=");
                    }
                } else { // writes an error if there are no operators
                    viewResText.setText(R.string.error);
                    mViewResCalc.setText("");
                }
            } else {
                viewResText.setText(R.string.error);
                mViewResCalc.setText("");
            }
        }

    }

    /**
     * Accepts a string equation. Using algorithm sorting station,
     * returns a vector of elements in the form of a reverse polish notation.
     */
    private Vector<String> polishInvertedRecord(String equation) {
        Stack<String> stack = new Stack<>();
        String output = "";
        Vector<String> res = new Vector<>();
        for (int i = 0; i < equation.length(); i++) {
            // Checks whether a character is a number
            if (isNumber(equation.charAt(i)) || (equation.charAt(i) == '-' && i == 0) ||
                    (i > 0 && equation.charAt(i - 1) == '(' && equation.charAt(i) == '-')) {
                // check whether a character is, the latest in a number of
                if (i == equation.length() - 1 || !isNumber(equation.charAt(i + 1))) {
                    output += equation.charAt(i);
                    res.add(output);
                    output = "";
                } else {
                    output += equation.charAt(i);
                }
            } else if (equation.charAt(i) == '(') {
                stack.push("(");
            } else if (equation.charAt(i) == ')') {
                while (stack.peek().equals("(")) {
                    res.add(stack.pop());
                }
                stack.pop();
            } else if (isOperator(equation.charAt(i))) {
                while (!stack.isEmpty() && (operatorPriority(stack.peek().charAt(0)) >= operatorPriority(equation.charAt(i)))) {
                    res.add(stack.pop());
                }
                stack.push(Character.toString(equation.charAt(i)));
            }
        }
        // Takes out remaining values from the stack
        while (!stack.isEmpty()) {
            res.add(stack.pop());
        }
        return res;
    }

    private boolean isOperator(char ch) {
        return (ch == '+' || ch == '-' || ch == '/' || ch == '*' || ch == '^');
    }

    private boolean isNumber(char ch) {
        return (('0' <= ch && ch <= '9') || (ch == '.'));
    }

    private int operatorPriority(Character ch) {
        int res = 0;
        if (ch >= 'a' && ch <= 'z') {
            res = 4;
        } else if (ch == '^') {
            res = 3;
        } else if (ch == '*' || ch == '/') {
            res = 2;
        } else if (ch == '+' || ch == '-') {
            res = 1;
        }
        return res;
    }

    /**
     * Takes each element of the vector values and  places numbers in the stack, if the value is an
     * operator then removed numbers from stack and made mathematical equations and places back in
     * stack. So is continued until vector will not empty and in the stack remains only a single
     * number. It will be result.
     */
    private String getResult(Vector<String> record) {
        Stack<Double> stack = new Stack<>();

        for (int i = 0; i < record.size(); i++) {
            double res = 0;
            String element = record.get(i);

            if (isNumber(element.charAt(0)) || (element.length() > 1 && isNumber(element.charAt(1)))) {
                stack.push(Double.parseDouble(element));
            } else if (isOperator(element.charAt(0))) {

                double firstOperand = stack.pop();
                double secondOperand = stack.pop();

                if (element.charAt(0) == '+') {
                    res = secondOperand + firstOperand;
                } else if (element.charAt(0) == '-') {
                    res = secondOperand - firstOperand;
                } else if (element.charAt(0) == '*') {
                    res = secondOperand * firstOperand;
                } else if (element.charAt(0) == '/') {
                    res = secondOperand / firstOperand;
                }
                stack.push(res);
            } else {
                return getString(R.string.error);
            }
        }
        return stack.pop().toString();
    }


    private void initViews() {
        mViewResText = (TextView) findViewById(R.id.lastEquation);
        mViewResCalc = (EditText) findViewById(R.id.viewResInCalc);

        findViewById(R.id.button0).setOnClickListener(this);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
        findViewById(R.id.button8).setOnClickListener(this);
        findViewById(R.id.button9).setOnClickListener(this);
        findViewById(R.id.buttonPlus).setOnClickListener(this);
        findViewById(R.id.buttonSlash).setOnClickListener(this);
        findViewById(R.id.buttonMinus).setOnClickListener(this);
        findViewById(R.id.buttonPoint).setOnClickListener(this);
        findViewById(R.id.buttonEqually).setOnClickListener(this);
        findViewById(R.id.buttonLeftBkt).setOnClickListener(this);
        findViewById(R.id.buttonMultiply).setOnClickListener(this);
        findViewById(R.id.buttonClearText).setOnClickListener(this);
        findViewById(R.id.buttonBackSymbol).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonPlus: addPlus(); break;
            case R.id.buttonMinus: addMinus(); break;
            case R.id.buttonSlash: addDivision(); break;
            case R.id.button0: addToEquation("0"); break;
            case R.id.button1: addToEquation("1"); break;
            case R.id.button2: addToEquation("2"); break;
            case R.id.button3: addToEquation("3"); break;
            case R.id.button4: addToEquation("4"); break;
            case R.id.button5: addToEquation("5"); break;
            case R.id.button6: addToEquation("6"); break;
            case R.id.button7: addToEquation("7"); break;
            case R.id.button8: addToEquation("8"); break;
            case R.id.button9: addToEquation("9"); break;
            case R.id.buttonMultiply: addMultiply(); break;
            case R.id.buttonEqually: solveEquation(); break;
            case R.id.buttonPoint: checkAndAddPoint(); break;
            case R.id.buttonClearText: clearResView(); break;
            case R.id.buttonLeftBkt: addToEquation("("); break;
            case R.id.buttonBackSymbol: clearOneSymbol(); break;
            case R.id.buttonRightBkt: addToEquation(")"); break;
        }
    }
}