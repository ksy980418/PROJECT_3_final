package com.example.project_3;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTwo extends Fragment {

    private FloatingActionButton fab_main, fab_sub1, fab_sub2;

    private Animation fab_open, fab_close;

    private boolean isFabOpen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view2 = inflater.inflate(R.layout.tab2 , container, false);

        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);

        fab_main = view2.findViewById(R.id.fab_main);
        fab_sub1 = view2.findViewById(R.id.fab_sub1);
        fab_sub2 = view2.findViewById(R.id.fab_sub2);

        fab_main.setOnClickListener(listener);
        fab_sub1.setOnClickListener(listener);
        fab_sub2.setOnClickListener(listener);

        return view2;
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab_main:
                    toggleFab();
                    break;
                case R.id.fab_sub1:
                    toggleFab();
                    Toast.makeText(getContext(), "Camera Open-!", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.fab_sub2:
                    toggleFab();
                    Toast.makeText(getContext(), "Album Open-!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void toggleFab() {
        if (isFabOpen) {
            fab_main.setImageResource(R.drawable.ic_add);
            fab_sub1.startAnimation(fab_close);
            fab_sub2.startAnimation(fab_close);
            fab_sub1.setClickable(false);
            fab_sub2.setClickable(false);
            isFabOpen = false;
        } else {
            fab_main.setImageResource(R.drawable.ic_close);
            fab_sub1.startAnimation(fab_open);
            fab_sub2.startAnimation(fab_open);
            fab_sub1.setClickable(true);
            fab_sub2.setClickable(true);
            isFabOpen = true;
        }
    }
}