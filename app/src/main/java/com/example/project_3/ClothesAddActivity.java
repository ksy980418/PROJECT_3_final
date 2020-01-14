package com.example.project_3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

public class ClothesAddActivity extends AppCompatActivity {
    private Spinner group1;
    private Spinner group2;
    private ImageView clothes;
    private Button add_button;

    private String [] item1;
    private String [] item2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_add);

        group1 = findViewById(R.id.spinner1);
        group2 = findViewById(R.id.spinner2);
        clothes = findViewById(R.id.clothes_image);
        add_button = findViewById(R.id.add_button);

        item1 = new String[]{"상의", "하의", "아우터"};
        item2 = new String[]{"티셔츠", "후드티", "맨투맨"};

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, item1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, item2);

        adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        group1.setAdapter(adapter1);
        group2.setAdapter(adapter2);

        clothes.setImageBitmap(FragmentTwo.mBitmap);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                finish();
            }
        });
    }
}
