package com.tarun.currensee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ProcessNoteActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    TextView detectedNoteTextView;
    String denomination;
    Map<String, String> map = new HashMap<>();
    Button addMoneyButton, deductMoneyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_note);

        sharedPreferences = this.getSharedPreferences("money",
                Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        map.put("10rs","10");
        map.put("20rs","20");
        map.put("100rs", "100");
        map.put("500rs", "500");
        map.put("2000rs", "2000");

        addMoneyButton = findViewById(R.id.button_add_money);
        deductMoneyButton = findViewById(R.id.button_deduct_money);

        denomination = map.get(getIntent().getStringExtra("denomination"));

        Toast.makeText(ProcessNoteActivity.this, "Detected Rs " + denomination + " note",
                Toast.LENGTH_SHORT).show();

        detectedNoteTextView = findViewById(R.id.detected_note_name);
        detectedNoteTextView.setText(getResources().getString(R.string.note_detected_name, denomination));

        addMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMoney(denomination);
                Intent intent = new Intent(ProcessNoteActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Toast.makeText(ProcessNoteActivity.this, "Added Rs " + denomination + " to "
                                + "wallet",
                        Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        deductMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deductMoney(denomination);
                Intent intent = new Intent(ProcessNoteActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Toast.makeText(ProcessNoteActivity.this, "Deducted Rs " + denomination + " from "
                                + "wallet",
                        Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
    }



    void addMoney(String denomination) {
        int count = sharedPreferences.getInt(String.valueOf(denomination), 0);
        sharedPreferencesEditor.putInt(denomination, count+1).commit();
    }

    void deductMoney(String denomination) {
        int count = sharedPreferences.getInt(String.valueOf(denomination), 0);
        sharedPreferencesEditor.putInt(denomination, count-1).commit();
    }

}
