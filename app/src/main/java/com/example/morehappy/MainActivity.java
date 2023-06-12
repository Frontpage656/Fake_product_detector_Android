package com.example.morehappy;

import static java.lang.String.valueOf;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

public class MainActivity extends AppCompatActivity {
    Button buttonScan;
    TextView category, product, county_code, exDate, timeRemain, code_number, price, description, years, months, days, exDateLabel;
    String results_code;
    int remainYears, remainMonth, remainDays;
    LinearLayout expireLinear;
    TableLayout table;
    MaterialCardView theTable;
    char char_value;
    String to_be_incypted;

    String countyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonScan = findViewById(R.id.buttonScan);
        category = findViewById(R.id.category);
        product = findViewById(R.id.product);
        county_code = findViewById(R.id.county_code);
        theTable = findViewById(R.id.theTable);
        exDate = findViewById(R.id.exDate);
        code_number = findViewById(R.id.code_number);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        exDateLabel = findViewById(R.id.exDateLabel);
        expireLinear = findViewById(R.id.expireLinear);
        table = findViewById(R.id.table);

        years = findViewById(R.id.years);
        months = findViewById(R.id.months);
        days = findViewById(R.id.days);

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this)
                        .setPrompt("Use volume up for flash light!")
                        .setBeepEnabled(true)
                        .setCaptureActivity(Capture.class)
                        .setBarcodeImageEnabled(true)
                        .setOrientationLocked(true);
                integrator.initiateScan();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult.getContents() != null) {
            String results = intentResult.getContents();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                byte[] decript = Base64.getMimeDecoder().decode(results);
                results_code = new String(decript);
            }
            execution();
        }
    }

    private void execution() {

        try {

            String categoryType = results_code.substring(0, results_code.indexOf("@"));
            String code = results_code.substring(results_code.indexOf("@") + 1, results_code.indexOf("["));
            String productName = results_code.substring(results_code.indexOf("[") + 1, results_code.indexOf("]"));
            String productPrice = results_code.substring(results_code.indexOf("]") + 1, results_code.indexOf("*"));
            String eDate = results_code.substring(results_code.indexOf("*") + 1, results_code.indexOf("~"));
            String eMonth = results_code.substring(results_code.indexOf("~") + 1, results_code.indexOf("!"));
            String eYear = results_code.substring(results_code.indexOf("!") + 1, results_code.indexOf("{"));
            String explanation = results_code.substring(results_code.indexOf("{") + 1, results_code.indexOf("}"));


            String dateDate = eDate + "/" + eMonth + "/" + eYear;

            int date = Integer.parseInt(eDate);
            int month = Integer.parseInt(eMonth);
            int year = Integer.parseInt(eYear);
            int categoryNo = Integer.parseInt(categoryType);


            LocalDate localDate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                localDate = LocalDate.now();
                LocalDate expireDate = LocalDate.of(year, month, date);

                if (localDate.equals(expireDate)) {

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                    builder.setTitle("Warning")
                            .setMessage("Product Expired!!!!!!!!!! today " + dateDate)
                            .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "Details sent", Toast.LENGTH_LONG).show();
                                }
                            }).show();

                } else if (localDate.isAfter(expireDate)) {

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                    builder.setTitle("Warning")
                            .setMessage("Product Expired!!!!!!!!!! since " + dateDate)
                            .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "Details sent", Toast.LENGTH_LONG).show();
                                }
                            }).show();

                } else {

                    //I did not know before!!!
                    int duration = (int) ChronoUnit.DAYS.between(localDate, expireDate);

                    switch (categoryNo) {
                        case 1:
                            category.setText("Foods & Drinks");
                            break;
                        case 2:
                            category.setText("Medicine");
                            break;
                        case 3:
                            category.setText("Pesticides");
                            break;
                        case 4:
                            category.setText("Others");
                            county_code.setText(code);
                            product.setText(productName);
                            price.setText(productPrice);
                            description.setText(explanation);
                            expireLinear.setVisibility(View.GONE);
                            timeRemain.setVisibility(View.GONE);
                            table.setVisibility(View.GONE);
                            theTable.setVisibility(View.GONE);
                            break;
                    }


                    if (code.equals("255")){
                        countyName="TANZ";
                    }else if (code.equals("254")){
                        countyName = "KENY";
                    }else {
                        countyName = "IND";
                    }

                    county_code.setText(countyName);
                    product.setText(productName);
                    price.setText(productPrice);
                    exDate.setText(dateDate);
                    description.setText(explanation);


                    // Interval function
                    remainYears = duration / 365;
                    remainMonth = (duration % 365) / 30;
                    remainDays = duration - ((remainYears * 365) + (remainMonth * 30));

                    years.setText(valueOf(remainYears));
                    months.setText(valueOf(remainMonth));
                    days.setText(valueOf(remainDays));
                }
            }

        } catch (Exception e) {

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                            builder.setTitle("Warning!!!")
                                    .setMessage("Product is unknown")
                                    .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
        }
    }

}
