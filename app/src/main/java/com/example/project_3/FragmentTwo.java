package com.example.project_3;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTwo extends Fragment {
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CAPTURE_IMAGE = 2;
    private static final int CLOTHES_ADD = 3;

    private FloatingActionButton fab_main, fab_sub1, fab_sub2;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;

    static Bitmap cur_image;

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
                    gp_camera();
                    break;
                case R.id.fab_sub2:
                    toggleFab();
                    go_album();
                    break;
            }
        }
    };

    private void go_album() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FROM_ALBUM);
    }

    private void gp_camera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_FROM_ALBUM) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(getContext(), "ALBUM CANCEL" ,Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                    cur_image = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();

                    Intent intent = new Intent(getContext(), ClothesAddActivity.class);
                    startActivityForResult(intent, CLOTHES_ADD);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (requestCode == CAPTURE_IMAGE) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(getContext(), "CAMERA CANCEL" ,Toast.LENGTH_SHORT).show();
            }
            else {
                cur_image = (Bitmap) data.getExtras().get("data");

                Intent intent = new Intent(getContext(), ClothesAddActivity.class);
                startActivityForResult(intent, CLOTHES_ADD);
            }
        }
        else if (requestCode == CLOTHES_ADD) {

        }
    }

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