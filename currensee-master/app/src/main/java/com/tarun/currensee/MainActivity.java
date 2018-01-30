package com.tarun.currensee;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DenominationsChange{

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;


    TextView balanceTextView;
    TextView amountTenTextView;
    TextView amountTwentyTextView;
    TextView amountFiftyTextView;
    TextView amountHundredTextView;
    TextView amountFiveHundredTextView;
    TextView amountTwoThousandTextView;

    List<Integer> denominations = Arrays.asList(10,20,50,100,500,2000);
    List<TextView> amountTextViews = new ArrayList<>();

    Button detectNoteButton;

    AccessibilityManager manager;

    int balance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getSharedPreferences("money",
                Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        balanceTextView = findViewById(R.id.textview_balance);

        amountTenTextView = findViewById(R.id.amount_ten);
        amountTwentyTextView = findViewById(R.id.amount_twenty);
        amountFiftyTextView = findViewById(R.id.amount_fifty);
        amountHundredTextView = findViewById(R.id.amount_hundred);
        amountFiveHundredTextView = findViewById(R.id.amount_fivehundred);
        amountTwoThousandTextView = findViewById(R.id.amount_twothousand);

        detectNoteButton = findViewById(R.id.button_detect_note);

        amountTextViews = Arrays.asList(amountTenTextView,
                amountTwentyTextView,
                amountFiftyTextView,
                amountHundredTextView,
                amountFiveHundredTextView,
                amountTwoThousandTextView);

        manager = (AccessibilityManager)getSystemService(Context.ACCESSIBILITY_SERVICE);

        updateViews();

        detectNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                startActivity(intent);
            }
        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        101);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViews();

    }

    @Override
    public void addMoney(int denomination) {
        int count = sharedPreferences.getInt(String.valueOf(denomination), 0);
        sharedPreferencesEditor.putInt(String.valueOf(denomination), count+1).commit();
        Log.e("sid", "addmoney" + String.valueOf(10));
        updateViews();
    }

    @Override
    public void deductMoney(int denomination) {
        int count = sharedPreferences.getInt(String.valueOf(denomination), 0);
        if (count == 0) {
            return; // already zero
        }
        sharedPreferencesEditor.putInt(String.valueOf(denomination), count-1).commit();
        updateViews();
    }

    @Override
    public void updateViews() {
        int balance = 0;
        for (int i=0; i<denominations.size(); i++) {
            String denominationString = String.valueOf(denominations.get(i));
            String countString = String.valueOf(sharedPreferences.getInt(denominationString, 0));
            String totalAmount = String.valueOf(Integer.valueOf(denominationString) * Integer
                    .valueOf
                    (countString));
            balance += Integer.parseInt(totalAmount);

            amountTextViews.get(i).setText(getResources().getString(R.string.note_count,
                    countString, denominationString, totalAmount));
            amountTextViews.get(i).setContentDescription(getResources().getString(R.string
                    .note_count_description, countString, denominationString, totalAmount));
        }

        balanceTextView.setText("Balance Rs " + String.valueOf(balance));

        if (manager.isEnabled()) {
            AccessibilityEvent e = AccessibilityEvent.obtain();
            e.setEventType(AccessibilityEvent.TYPE_ANNOUNCEMENT);
            e.setClassName(getClass().getName());
            e.setPackageName(getPackageName());
            e.getText().add("Wallet Balance Rs " + String.valueOf(balance));
            manager.sendAccessibilityEvent(e);
        }
    }

    @Override
    public void saveToDatabase() {

    }
}
