package com.example.project_3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClothesAddActivity extends AppCompatActivity {
    private Spinner group1;
    private Spinner group2;
    private ImageView clothes;
    private Button add_button;

    private String [] item;
    private ArrayList<String []> items;

    private ArrayList<ArrayAdapter<String>> adapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_add);

        group1 = findViewById(R.id.spinner1);
        group2 = findViewById(R.id.spinner2);
        clothes = findViewById(R.id.clothes_image);
        add_button = findViewById(R.id.add_button);

        clothes.setImageBitmap(FragmentTwo.mBitmap);

        items = new ArrayList<>();
        adapters = new ArrayList<>();

        item = new String[]{"상의", "하의", "아우터"};

        items.add(new String[]{"button-down", "flannel", "henley", "hoodie", "sweater", "tee", "turtleneck"});
        items.add(new String[]{"chinos", "jeans", "shorts", "sweatpants"});
        items.add(new String[]{"anorak", "blazer", "bomber", "cardigan", "coat", "jacket", "parka", "peacoat"});

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        for (int i = 0; i < 3; i++) {
            adapters.add(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, items.get(i)));
            adapters.get(i).setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        }

        group1.setAdapter(adapter);
        group2.setAdapter(adapters.get(0));

        group1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                group2.setAdapter(adapters.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonObject.put("id", SignupActivity.user_id);
                    jsonObject.put("filename", FragmentTwo.curfile.getName());
                    jsonObject.put("group1", group1.getSelectedItem());
                    jsonObject.put("group2", group2.getSelectedItem());
                    jsonArray.put(jsonObject);
                    new JSONTask(jsonArray).execute("add_info");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                finish();
            }
        });
    }
}
