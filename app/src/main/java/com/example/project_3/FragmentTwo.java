package com.example.project_3;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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

    static Bitmap mBitmap;
    static File curfile;

    private ApiService apiService;

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

        initRetrofitClient();

        return view2;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab_main:
                    toggleFab();
                    break;
                case R.id.fab_sub1:
                    toggleFab();
                    go_camera();
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

    private void go_camera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_FROM_ALBUM) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(getContext(), "ALBUM CANCEL" ,Toast.LENGTH_SHORT).show();
            }
            else {
                Uri selectedImage = data.getData();

                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String FilePath = cursor.getString(columnIndex);

                cursor.close();

                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                curfile = new File(FilePath);

                multipartImageUpload();
            }
        }
        else if (requestCode == CAPTURE_IMAGE) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(getContext(), "CAMERA CANCEL" ,Toast.LENGTH_SHORT).show();
            }
            else {
                mBitmap = (Bitmap) data.getExtras().get("data");

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = SignupActivity.user_id + "_" + timeStamp;

                File filesDir = getActivity().getFilesDir();
                curfile = new File(filesDir, imageFileName + ".png");

                OutputStream os;
                try {
                    os = new FileOutputStream(curfile);
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(curfile);
                    try {
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                multipartImageUpload();
            }
        }
        else if (requestCode == CLOTHES_ADD) {

        }
    }

    private void initRetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        apiService = new Retrofit.Builder().baseUrl("http://192.168.0.78:3000").client(client).build().create(ApiService.class);
    }

    private void multipartImageUpload() {
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), curfile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", curfile.getName(), reqFile);
        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SignupActivity.user_id));

        Call<ResponseBody> req = apiService.postImage(body, user_id);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(getContext(), response.message() + " ", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), ClothesAddActivity.class);
                startActivityForResult(intent, CLOTHES_ADD);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Request failed", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
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