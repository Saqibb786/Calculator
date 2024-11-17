package com.example.mycalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText display;
    private String operator = "";
    private boolean isOperatorPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        display = findViewById(R.id.display);
        setNumericButtonListeners();
        setOperatorButtonListeners();
    }

    private void setNumericButtonListeners() {
        int[] numericButtonIds = {
                R.id.button_0, R.id.button_1, R.id.button_2,
                R.id.button_3, R.id.button_4, R.id.button_5,
                R.id.button_6, R.id.button_7, R.id.button_8,
                R.id.button_9, R.id.button_decimal
        };

        for (int id : numericButtonIds) {
            findViewById(id).setOnClickListener(view -> {
                Button button = (Button) view;
                display.append(button.getText().toString());
                isOperatorPressed = false;
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void setOperatorButtonListeners() {
        // Handle the +/- button
        findViewById(R.id.button_plus_minus).setOnClickListener(view -> {
            String currentText = display.getText().toString();
            if (!currentText.isEmpty() && !currentText.equals("0")) {
                if (currentText.startsWith("-")) {
                    display.setText(currentText.substring(1));
                } else {
                    display.setText("-" + currentText);
                }
            }
        });

        // Handle the Clear button
        findViewById(R.id.button_clear).setOnClickListener(view -> {
            display.setText("");
            operator = "";
            isOperatorPressed = false;
        });

        // Handle operators (+, -, ×, ÷, %)
        int[] operatorButtonIds = {
                R.id.button_plus, R.id.button_minus,
                R.id.button_multiply, R.id.button_divide, R.id.button_percent
        };

        for (int id : operatorButtonIds) {
            findViewById(id).setOnClickListener(view -> {
                Button button = (Button) view;
                String currentText = display.getText().toString();

                if (!currentText.isEmpty() && !isOperatorPressed) {
                    // Append operator if valid
                    display.append(button.getText().toString());
                    operator = button.getText().toString();
                    isOperatorPressed = true;
                } else if (!currentText.isEmpty()) {
                    // Replace last character if it's an operator
                    char lastChar = currentText.charAt(currentText.length() - 1);
                    if (!Character.toString(lastChar).equals(button.getText().toString())) {
                        currentText = currentText.substring(0, currentText.length() - 1) + button.getText().toString();
                        display.setText(currentText);
                        display.setSelection(currentText.length());
                    }
                }
            });
        }

        // Handle the Equals button
        findViewById(R.id.button_equals).setOnClickListener(view -> {
            String currentText = display.getText().toString();
            if (!currentText.isEmpty() && !operator.isEmpty()) {
                calculateResult();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void calculateResult() {
        String input = display.getText().toString();

        // Normalize the input to handle different symbols
        input = input.replace("−", "-"); // Replace special minus symbol with a normal dash

        if (input.isEmpty() || operator.isEmpty() || isOperatorPressed) {
            display.setText("Error");
            return;
        }

        try {
            // Split the input based on the operator
            String[] parts;
            if (operator.equals("-") || operator.equals("−")) {
                parts = input.split("(?<!^)-"); // Correct split for subtraction
            } else {
                parts = input.split("\\" + operator); // Split for other operators (escaped for regex)
            }

            // Ensure exactly two operands are present
            if (parts.length != 2) {
                display.setText("Error");
                return;
            }

            // Parse the operands into double values
            double firstValue = Double.parseDouble(parts[0].trim());
            double secondValue = Double.parseDouble(parts[1].trim());
            double result;

            // Perform the calculation based on the operator
            switch (operator) {
                case "+":
                    result = firstValue + secondValue;
                    break;
                case "-":
                case "−":
                    result = firstValue - secondValue;
                    break;
                case "×":
                    result = firstValue * secondValue;
                    break;
                case "÷":
                    if (secondValue != 0) {
                        result = firstValue / secondValue;
                    } else {
                        display.setText("Error: Division by zero");
                        return;
                    }
                    break;
                case "%":
                    // Calculate percentage (firstValue % of secondValue)
                    result = (firstValue / 100) * secondValue;
                    break;
                default:
                    display.setText("Error");
                    return;
            }

            // Display the result
            display.setText(String.valueOf(result));

            // Reset state
            operator = "";
            isOperatorPressed = false;

        } catch (Exception e) {
            display.setText("Error");
        }
    }
}
