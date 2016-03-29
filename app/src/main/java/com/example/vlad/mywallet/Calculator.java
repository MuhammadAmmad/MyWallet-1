package com.example.vlad.mywallet;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Stack;
import java.util.Vector;

public class Calculator extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Create menu. Menu contains only one button, when clicked,
     * result of the equation is passed to another activity
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ok, menu);
        return true;
    }

    /**
     * Method is called when clicking on the "OK" button
     * Method reads and sends the result back
     */
    public void onButtonSaveResultClick(MenuItem item) {
        EditText editRes = (EditText) findViewById(R.id.viewResInCalc);
        String resTxt = editRes.getText().toString();

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

        // put result to send
        Intent intent = new Intent();
        intent.putExtra("calcRes", resTxt);
        setResult(Activity.RESULT_OK, intent);

        finish();
    }

    /**
     * Pressing the 1 button is added to equation an appropriate sign
     */
    public void onButton1Click(View view) {
        addTextToString("1");
    }

    /**
     * Pressing the 2 button is added to equation an appropriate sign
     */
    public void onButton2Click(View view) {
        addTextToString("2");
    }

    /**
     * Pressing the 3 button is added to equation an appropriate sign
     */
    public void onButton3Click(View view) {
        addTextToString("3");
    }

    /**
     * Pressing the 4 button is added to equation an appropriate sign
     */
    public void onButton4Click(View view) {
        addTextToString("4");
    }

    /**
     * Pressing the 5 button is added to equation an appropriate sign
     */
    public void onButton5Click(View view) {
        addTextToString("5");
    }

    /**
     * Pressing the 6 button is added to equation an appropriate sign
     */
    public void onButton6Click(View view) {
        addTextToString("6");
    }

    /**
     * Pressing the 7 button is added to equation an appropriate sign
     */
    public void onButton7Click(View view) {
        addTextToString("7");
    }

    /**
     * Pressing the 8 button is added to equation an appropriate sign
     */
    public void onButton8Click(View view) {
        addTextToString("8");
    }

    /**
     * Pressing the 9 button is added to equation an appropriate sign
     */
    public void onButton9Click(View view) {
        addTextToString("9");
    }

    /**
     * Pressing the 0 button is added to equation an appropriate sign
     */
    public void onButton0Click(View view) {
        addTextToString("0");
    }

    /**
     * Pressing the "." button is added to expression an appropriate sign
     */
    public void onButtonPointClick(View view) {
        EditText mViewResCalc = (EditText) findViewById(R.id.viewResInCalc);

        // check if there is already a point in the number, it does not set a new
        String string = mViewResCalc.getText().toString();
        Character lastChar = ' ';
        if (string.length() > 0) {
            if (string.lastIndexOf(".") <= string.lastIndexOf("+") || string.lastIndexOf(".") <= string.lastIndexOf("-") ||
                    string.lastIndexOf(".") <= string.lastIndexOf("*") || string.lastIndexOf(".") <= string.lastIndexOf("/") ||
                    string.lastIndexOf(".") <= string.lastIndexOf("(") || string.lastIndexOf(".") <= string.lastIndexOf(")")) {
                lastChar = string.charAt(string.length() - 1);

                if (lastChar == '/' || lastChar == '+' || lastChar == '-' || lastChar == '*') {
                    mViewResCalc.setText(string + "0.");
                } else {
                    addTextToString(".");
                }
            }
        } else {
            addTextToString("0."); // if the point is added to very first expression, then it puts zero
        }
    }

    /**
     * Pressing the "/" button is added to the equation division sign
     */
    public void onButtonSlashClick(View view) {
        EditText mViewResCalc = (EditText) findViewById(R.id.viewResInCalc);
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
                addTextToString("/");
            }
        } else {
            addTextToString("0/");
        }
    }

    /**
     * Pressing the "+" button is added to the equation plus sign
     */
    public void onButtonPlusClick(View view) {
        EditText mViewResCalc = (EditText) findViewById(R.id.viewResInCalc);
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
                addTextToString("+");
            }
        } else {
            addTextToString("0+");
        }
    }

    /**
     * Pressing the "*" button is added to the equation multiplication sign
     */
    public void onButtonMultiplyClick(View view) {
        EditText mViewResCalc = (EditText) findViewById(R.id.viewResInCalc);
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
                addTextToString("*");
            }
        } else {
            addTextToString("0*");
        }
    }

    /**
     * Pressing the "-" button is added to the equation minus sign
     */
    public void onButtonMinusClick(View view) {
        EditText mViewResCalc = (EditText) findViewById(R.id.viewResInCalc);
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
                addTextToString("-");
            }
        } else {
            addTextToString("0-");
        }
    }

    /**
     * Pressing the ")" button is added to equation an appropriate sign
     */
    public void onButtonRightBktClick(View view) {
        addTextToString(")");
    }

    /**
     * Pressing the "(" button is added to equation an appropriate sign
     */
    public void onButtonLeftBktClick(View view) {
        addTextToString("(");
    }

    /**
     * by pressing the "C" button clears the current and last equation
     */
    public void onButtonClearTextClick(View view) {
        EditText editResCalc = (EditText) findViewById(R.id.viewResInCalc);
        editResCalc.setText("");
        editResCalc.setHint("0");

        TextView viewResText = (TextView) findViewById(R.id.lastEquation);
        viewResText.setText("");
    }

    /**
     * By clicking on the button "<" removes the last character in the equation
     */
    public void onButtonBackSimbolClick(View view) {
        EditText mViewResCalc = (EditText) findViewById(R.id.viewResInCalc);
        String string = mViewResCalc.getText().toString();
        if (string.length() > 0) {
            mViewResCalc.setText(string.substring(0, string.length() - 1));
        } else { // when deleting the last character then installed value by default
            mViewResCalc.setHint("0");
        }
    }

    /**
     * accepts the character and concatenates it to the equation
     */
    private void addTextToString(String text) {
        EditText mViewResCalc = (EditText) findViewById(R.id.viewResInCalc);
        String string = mViewResCalc.getText().toString();
        string += text;
        mViewResCalc.setText(string);
    }

    /**
     * when clicked "=" button, the method makes the verification and
     * implementation of the methods is that the result will count
     */
    public void onButtonEquallyClick(View view) {
        EditText editResCalc = (EditText) findViewById(R.id.viewResInCalc);
        String equation = editResCalc.getText().toString();
        if (equation.length() > 0) { // checks that the equation was not empty

            // counts the number of operators and parentheses
            int colRightBkt = 0;
            int colLeftBkt = 0;
            int colOperator = 0;

            for (int i = 0; i < equation.length(); i++) {
                if (equation.charAt(i) == '(') colLeftBkt++;
                if (equation.charAt(i) == ')') colRightBkt++;
                if (isOperator(equation.charAt(i))) colOperator++;
            }

            // if left in equation parentheses more than the right, they are balanced
            for (int i = 0; i < colLeftBkt - colRightBkt; i++) {
                equation += ")";
            }

            TextView viewResText = (TextView) findViewById(R.id.lastEquation);
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
                if (res == "Error") { // writes an error if the equation has been set incorrectly
                    viewResText.setText("Error");
                    editResCalc.setText("");
                } else { // sets the whole equation, and the result
                    editResCalc.setText(String.valueOf(res));
                    viewResText.setText(equation + "=");
                }
            } else { // writes an error if there are no operators
                viewResText.setText("Error");
                editResCalc.setText("");
            }
        }

    }

    /**
     * Accepts a string equation. Using algorithm sorting station,
     * returns a vector of elements in the form of a reverse polish notation.
     */
    private Vector<String> polishInvertedRecord(String equation) {
        Stack<String> stack = new Stack();
        String output = "";
        Vector<String> res = new Vector();
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
                while (stack.peek() != "(") {
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

    /**
     * Checks whether a character is a operator and returns
     * a Boolean answer.
     */
    private boolean isOperator(char ch) {
        return (ch == '+' || ch == '-' || ch == '/' || ch == '*' || ch == '^');
    }

    /**
     * Checks whether a character is a number and returns
     * a Boolean answer. Point is counted as part of a number
     */
    private boolean isNumber(char ch) {
        return (('0' <= ch && ch <= '9') || (ch == '.'));
    }

    /**
     * Sets priority of symbols
     */
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
        Stack<Double> stack = new Stack();

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
                return "Error";
            }
        }
        return stack.pop().toString();
    }

}