package com.andrewc.tipcalculator2;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

//import org.w3c.dom.Text;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    private double subtotal;
    private double tax;
    private double taxRate = 0.08;
    private double total;
    private double tip;
    double tipPercent = 0.15;
    private double grandTotal;

    private EditText subEditText;
    private TextView taxTextView;
    private EditText taxRateEditText;
    private EditText totalEditText;
    private EditText tipEditText;
    private TextView tipAmountTextView;
    private EditText grandEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subEditText = (EditText) findViewById(R.id.subEditText);
        taxTextView= (TextView) findViewById(R.id.taxTextView);
        taxRateEditText = (EditText) findViewById(R.id.taxRateEditText);
        totalEditText = (EditText) findViewById(R.id.totalEditText);
        tipEditText = (EditText) findViewById(R.id.tipEditText);
        grandEditText = (EditText) findViewById(R.id.grandEditText);
        tipAmountTextView = (TextView) findViewById(R.id.tipAmountTextView);

        SeekBar tipSeekBar = (SeekBar) findViewById(R.id.tipSeekBar);
        tipSeekBar.setOnSeekBarChangeListener(tipSeekBarChangeListener);

        subEditText.addTextChangedListener(subEditTextWatcher);
        taxRateEditText.addTextChangedListener(taxRateEditTextWatcher);
        totalEditText.addTextChangedListener(totalEditTextWatcher);
        tipEditText.addTextChangedListener(tipEditTextWatcher);
        grandEditText.addTextChangedListener(grandEditTextWatcher);

        if (getPreferences(MODE_PRIVATE) != null) {
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            String string = preferences.getString("tax_rate_save", "8");
            // or restores as 8% tax rate by default if missing
            if (string.length() > 0) {
                taxRate = Double.parseDouble(string);
                if (taxRate > 100) {
                    taxRate = 8;
                }
                taxRateEditText.setText(String.valueOf(taxRate));
                taxRate *= 0.01;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // don't need below line anymore
        if (taxRate <= 100) {
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("tax_rate_save", taxRateEditText.getText().toString());

            editor.apply();
        }
    }

    //  calculating from subtotal
    private void calculateFromSub() {
        tax = subtotal * taxRate;
        total = subtotal + tax;
        tip = subtotal * tipPercent;
        grandTotal = total + tip;

        taxTextView.setText(currencyFormat.format(tax));
        totalEditText.setText(currencyFormat.format(total));
        tipEditText.setText(currencyFormat.format(tip));
        grandEditText.setText(currencyFormat.format(grandTotal));
    }

    private final TextWatcher subEditTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (subEditText.isFocused() && charSequence.length() != 0) {
                try {
                    subtotal = Double.parseDouble(charSequence.toString());
                } catch (NumberFormatException e) {
                    subEditText.setText("");
                    subtotal = 0.0;
                }
                calculateFromSub();
            }
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    // tax rate change
    private void calculateFromTaxRate() {
        tax = subtotal * taxRate;
        total = subtotal + tax;
        grandTotal = total + tip;

        taxTextView.setText(currencyFormat.format(tax));
        totalEditText.setText(currencyFormat.format(total));
        grandEditText.setText(currencyFormat.format(grandTotal));
    }

    private final TextWatcher taxRateEditTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (taxRateEditText.isFocused() && charSequence.length() != 0) {
                try {
                    taxRate = Double.parseDouble(charSequence.toString()) * 0.01;

                } catch (NumberFormatException e) {
                    taxRateEditText.setText("");
                    taxRate = 0.0;
                }
                calculateFromTaxRate();
            }
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    //  calculating from total
    private void calculateFromTotal() {
        subtotal = total / (1 + taxRate);
        tax = total - subtotal;
        tip = subtotal * tipPercent;
        grandTotal = total + tip;

        subEditText.setText(currencyFormat.format(subtotal));
        taxTextView.setText(currencyFormat.format(tax));
        tipEditText.setText(currencyFormat.format(tip));
        grandEditText.setText(currencyFormat.format(grandTotal));
    }

    private final TextWatcher totalEditTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (totalEditText.isFocused() && charSequence.length() != 0) {
                try {
                    total = Double.parseDouble(charSequence.toString());
                } catch (NumberFormatException e) {
                    totalEditText.setText("");
                    total = 0.0;
                }
                calculateFromTotal();
            }
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    //  calculating from grand total
    private void calculateFromGrand() {
        subtotal = grandTotal / (1 + taxRate + tipPercent);
        tax = subtotal * taxRate;
        total = subtotal + tax;
        tip = subtotal * tipPercent;

        subEditText.setText(currencyFormat.format(subtotal));
        taxTextView.setText(currencyFormat.format(tax));
        totalEditText.setText(currencyFormat.format(total));
        tipEditText.setText(currencyFormat.format(tip));
    }

    private final TextWatcher grandEditTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (grandEditText.isFocused() && charSequence.length() != 0) {
                try {
                    grandTotal = Double.parseDouble(charSequence.toString());
                } catch (NumberFormatException e) {
                    grandEditText.setText("");
                    grandTotal = 0.0;
                }
                calculateFromGrand();
            }
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    //  calculate from tip amount changes
    private void calculateFromTip() {
        tipPercent = tip / subtotal;
        grandTotal = total + tip;

        tipAmountTextView.setText(percentFormat.format(tipPercent));
        grandEditText.setText(currencyFormat.format(grandTotal));
    }

    private final TextWatcher tipEditTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (tipEditText.isFocused() && charSequence.length() != 0) {
                try {
                    tip = Double.parseDouble(charSequence.toString());
                } catch (NumberFormatException e) {
                    tipEditText.setText("");
                    tip = 0.0;
                }
                calculateFromTip();
            }
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    //  calculate from changing tip percentage
    private void calculateFromTipPercent() {
        tip = tipPercent * subtotal;
        grandTotal = total + tip;


        tipEditText.setText(currencyFormat.format(tip));
        grandEditText.setText(currencyFormat.format(grandTotal));
    }

    private final OnSeekBarChangeListener tipSeekBarChangeListener=
            new OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (tipEditText.isFocused()) {
                        tipEditText.clearFocus();
                    }
                    else if (grandEditText.isFocused()) {
                        grandEditText.clearFocus();
                    }
                    tipPercent = i * 0.01;
                    tipAmountTextView.setText(percentFormat.format(tipPercent));

                    calculateFromTipPercent();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            };
}