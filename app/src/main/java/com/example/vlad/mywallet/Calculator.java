package com.example.vlad.mywallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    }

    public void onButton1Click(View view){
        addTextToString("1");
    }

    public void onButton2Click(View view) {
        addTextToString("2");
    }

    public void onButton3Click(View view) {
        addTextToString("3");
    }

    public void onButton4Click(View view) {
        addTextToString("4");
    }

    public void onButton5Click(View view) {
        addTextToString("5");
    }

    public void onButton6Click(View view) {
        addTextToString("6");
    }

    public void onButton7Click(View view) {
        addTextToString("7");
    }

    public void onButton8Click(View view) {
        addTextToString("8");
    }

    public void onButton9Click(View view) {
        addTextToString("9");
    }

    public void onButton0Click(View view) {
        addTextToString("0");
    }

    public void onButtonPointClick(View view) {
        EditText mViewResCalc = (EditText)findViewById(R.id.viewResInCalc);
        String string = mViewResCalc.getText().toString();
        Character lastChar = ' ';
        if(string.length() > 0) {
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
            addTextToString("0.");
        }
    }

    public void onButtonSlashClick(View view) {
        EditText mViewResCalc = (EditText)findViewById(R.id.viewResInCalc);
        String string = mViewResCalc.getText().toString();
        Character lastChar = ' ';
        if(string.length() > 0) {
            lastChar = string.charAt(string.length() - 1);

            if (lastChar == '/' || lastChar == '+' || lastChar == '-' || lastChar == '*') {
                mViewResCalc.setText(string.substring(0, string.length() - 1) + "/");
            } else {
                addTextToString("/");
            }
        } else {
            addTextToString("0/");
        }
    }

    public void onButtonPlusClick(View view) {
        EditText mViewResCalc = (EditText)findViewById(R.id.viewResInCalc);
        String string = mViewResCalc.getText().toString();
        Character lastChar = ' ';
        if(string.length() > 0) {
            lastChar = string.charAt(string.length() - 1);

            if (lastChar == '/' || lastChar == '+' || lastChar == '-' || lastChar == '*') {
                mViewResCalc.setText(string.substring(0, string.length() - 1) + "+");
            } else {
                addTextToString("+");
            }
        } else {
            addTextToString("0+");
        }
    }

    public void onButtonMultiplyClick(View view) {
        EditText mViewResCalc = (EditText)findViewById(R.id.viewResInCalc);
        String string = mViewResCalc.getText().toString();
        Character lastChar = ' ';
        if(string.length() > 0) {
            lastChar = string.charAt(string.length() - 1);

            if (lastChar == '/' || lastChar == '+' || lastChar == '-' || lastChar == '*') {
                mViewResCalc.setText(string.substring(0, string.length() - 1) + "*");
            } else {
                addTextToString("*");
            }
        } else {
            addTextToString("0*");
        }
    }

    public void onButtonMinusClick(View view) {
        EditText mViewResCalc = (EditText)findViewById(R.id.viewResInCalc);
        String string = mViewResCalc.getText().toString();
        Character lastChar = ' ';
        if(string.length() > 0) {
            lastChar = string.charAt(string.length() - 1);

            if (lastChar == '/' || lastChar == '+' || lastChar == '-' || lastChar == '*') {
                mViewResCalc.setText(string.substring(0, string.length() - 1) + "-");
            } else {
                addTextToString("-");
            }
        } else {
            addTextToString("0-");
        }
    }

    public void onButtonRightBktClick(View view) { addTextToString(")"); }

    public void onButtonLeftBktClick(View view) { addTextToString("("); }


    public void onButtonClearTextClick(View view) {
        EditText editResCalc = (EditText)findViewById(R.id.viewResInCalc);
        editResCalc.setText("");
        editResCalc.setHint("0");

        TextView viewResText = (TextView)findViewById(R.id.lastEquation);
        viewResText.setText("");
    }

    public void onButtonBackSimbolClick(View view) {
        EditText mViewResCalc = (EditText)findViewById(R.id.viewResInCalc);
        String string = mViewResCalc.getText().toString();
        if (string.length() > 0) {
            mViewResCalc.setText(string.substring(0, string.length()-1));
        } else {
            mViewResCalc.setHint("0");
        }
    }

    public void onButtonEquallyClick(View view) {
        EditText editResCalc = (EditText)findViewById(R.id.viewResInCalc);
        String equation = editResCalc.getText().toString();
        if (equation.length() > 0) {

            // уравнивает количество скобок ")" к количеству "("
            int colRightBkt = 0;
            int colLeftBkt = 0;
            int colOperator = 0;
            for (int i = 0; i < equation.length(); i++){
                if (equation.charAt(i) == '(') colLeftBkt++;
                if (equation.charAt(i) == ')') colRightBkt++;
                if (isOperator(equation.charAt(i))) colOperator++;
            }
            for(int i = 0; i < colLeftBkt - colRightBkt; i++){
                equation += ")";
            }
            // конец уравнения скобок

            TextView viewResText = (TextView)findViewById(R.id.lastEquation);
            if (colOperator > 0) { // выражение будет посчитано если операторов больше 0
                Vector<String> polishRecord = polishInvertedRecord(equation);

                String res = getResult(polishRecord);
                int indexPoint = res.indexOf(".");
                // обрезает ноли после точки если после точки ноль и он последний в строке
                if (res.charAt(indexPoint + 1) == '0' && res.length() - 1 == indexPoint + 1) {
                    res = res.substring(0, indexPoint);
                }
                if (res == "Error"){
                    viewResText.setText("Error");
                    editResCalc.setText("");
                } else {
                    editResCalc.setText(String.valueOf(res));
                    viewResText.setText(equation + "=");
                }
            } else {
                viewResText.setText("Error");
                editResCalc.setText("");
            }
        }


    }

    private void addTextToString(String text){
        EditText mViewResCalc = (EditText)findViewById(R.id.viewResInCalc);
        String string = mViewResCalc.getText().toString();
        string += text;
        mViewResCalc.setText(string);
    }



    private Vector<String> polishInvertedRecord(String equation) {
        Stack<String> stack = new Stack();
        String output = "";
        Vector<String> res = new Vector();
        for (int i = 0; i < equation.length(); i++){
            if (isNumber(equation.charAt(i)) || (equation.charAt(i) == '-' && i == 0) ||
                    (i > 0 && equation.charAt(i-1) == '(' && equation.charAt(i) == '-')){
                if (i == equation.length() - 1 || !isNumber(equation.charAt(i + 1))){
                    output += equation.charAt(i);
                    res.add(output);
                    output = "";
                } else {
                    output += equation.charAt(i);
                }
            } else if (equation.charAt(i) == '('){
                stack.push("(");
            } else if (equation.charAt(i) == ')'){
                while (stack.peek() != "("){
                    res.add(stack.pop());
                }
                stack.pop();
            } else if (isOperator(equation.charAt(i))){
                while (!stack.isEmpty() && (operatorPriority(stack.peek().charAt(0)) >= operatorPriority(equation.charAt(i)))){
                    res.add(stack.pop());
                }
                stack.push(Character.toString(equation.charAt(i)));
            }
        }

        while (!stack.isEmpty()){
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
        if (ch >= 'a' && ch <= 'z'){
            res = 4;
        } else if (ch == '^'){
            res = 3;
        } else if (ch == '*' || ch == '/'){
            res = 2;
        } else if (ch == '+' || ch == '-'){
            res = 1;
        }
        return res;
    }

    private String getResult(Vector<String> record) {
        Stack <Double> stack = new Stack();

        for (int i = 0; i < record.size(); i++){
            double res = 0;
            String element = record.get(i);
            if (isNumber(element.charAt(0)) || ( element.length() > 1 && isNumber(element.charAt(1)))){
                stack.push(Double.parseDouble(element));
            } else if (isOperator(element.charAt(0))){
                double firstOperand = stack.pop();
                double secondOperand = stack.pop();
                if (element.charAt(0) == '+'){
                    res = secondOperand + firstOperand;
                } else if (element.charAt(0) == '-'){
                    res = secondOperand - firstOperand;
                } else if (element.charAt(0) == '*'){
                    res = secondOperand * firstOperand;
                } else if (element.charAt(0) == '/'){
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
