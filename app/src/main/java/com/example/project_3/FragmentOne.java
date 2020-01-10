package com.example.project_3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentOne extends Fragment {
    private View view1;

    static TextView weather;
    static ImageView w_image;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view1 = inflater.inflate(R.layout.tab1, container,false);

        weather = view1.findViewById(R.id.weather_text);
        w_image = view1.findViewById(R.id.weather_image);

        RadioGroup radioGroup = view1.findViewById(R.id.radio);

        weather.setText(Main2Activity.Weather);
        w_image.setImageResource(Main2Activity.w_type);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.checkBox1: {
                        Toast.makeText(getContext(), "1_click", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.checkBox2: {
                        Toast.makeText(getContext(), "2_click", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.checkBox3: {
                        Toast.makeText(getContext(), "3_click", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.checkBox4: {
                        Toast.makeText(getContext(), "4_click", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.checkBox5: {
                        Toast.makeText(getContext(), "5_click", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        });

        return view1;
    }
}