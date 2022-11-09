package com.bignerdranch.android.tengecalculator;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.*;

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("OPERATION", lastOperation);
        if (operand != null)
            outState.putDouble("OPERAND", operand);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lastOperation = savedInstanceState.getString("OPERATION");
        operand = savedInstanceState.getDouble("OPERAND");
        resultField.setText(operand.toString());
        operationField.setText(lastOperation);
    }

    public void onNumberClick(View view) {
        Button button = (Button) view;
        numberField.append(button.getText());
    }

    public void onOperationClick(View view) {

        Button button = (Button) view;
        String op = button.getText().toString();
        String number = numberField.getText().toString();
        lastOperation = op;

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
