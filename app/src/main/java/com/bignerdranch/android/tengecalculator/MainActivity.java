package com.bignerdranch.android.tengecalculator;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.*;
import java.security.spec.ECField;

public class MainActivity extends AppCompatActivity {

    TextView resultField; // текстовое поле для вывода результата
    EditText numberField;   // поле для ввода числа
    TextView operationField;    // текстовое поле для вывода знака операции
    Double operand = null;  // операнд операции
    String lastOperation = "="; // последняя операция
    double kztusd;
    double rubusd;
    double kztrub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultField = findViewById(R.id.resultField);
        numberField = findViewById(R.id.numberField);
        operationField = findViewById(R.id.operationField);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://cdn.cur.su/api/latest.json";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {

            try {
            JSONObject jsonObject = response.getJSONObject("rates");
             kztusd = jsonObject.getDouble("KZT");
             rubusd = jsonObject.getDouble("RUB");
             kztrub = kztusd / rubusd;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }, Throwable::printStackTrace);
        queue.add(request);
    }


    // сохранение состояния
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("OPERATION", lastOperation);
        if (operand != null)
            outState.putDouble("OPERAND", operand);
        super.onSaveInstanceState(outState);
    }

    // получение ранее сохраненного состояния
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lastOperation = savedInstanceState.getString("OPERATION");
        operand = savedInstanceState.getDouble("OPERAND");
        resultField.setText(operand.toString());
        operationField.setText(lastOperation);
    }

    // обработка нажатия на числовую кнопку
    public void onNumberClick(View view) {

        Button button = (Button) view;
        numberField.append(button.getText());

//        if (lastOperation.equals("=") && operand != null) {
//            operand = null;
//        }
    }

    // обработка нажатия на кнопку операции
    public void onOperationClick(View view) {

        Button button = (Button) view;
        String op = button.getText().toString();
        String number = numberField.getText().toString();
        lastOperation = op;

        // если введенно что-нибудь
        if (number.length() > 0) {
            number = number.replace(',', '.');
            try {
                performOperation(Double.valueOf(number), op);
            } catch (NumberFormatException e) {
                numberField.setText("");
            }
        }
        operationField.setText(lastOperation);
    }

    private void performOperation(Double number, String operation) {

        // если операнд ранее не был установлен (при вводе самой первой операции)
        if (operand == null) {
            operand = number;
        }
            switch (lastOperation) {
                case "Convert to tenge":
                    operand = number * kztrub;
                    break;
                case "Convert to ruble":
                    operand = number / kztrub;
                    break;
            }
            resultField.setText(operand.toString().replace('.', ','));
            numberField.setText("");
        }

}
