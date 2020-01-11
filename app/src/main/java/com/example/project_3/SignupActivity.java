package com.example.project_3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.ApiErrorCode;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {
    static String nickname;
    static String email;
    static Bitmap img;
    static long user_id;
    static int age;
    static boolean is_male;

    static SignupActivity s_this;

    private ImageView profile;
    private String profile_img;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        s_this = SignupActivity.this;

        requestMe();
    }

    protected void showSignup() {
        setContentView(R.layout.signup);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tb.setTitle("회원가입");

        profile = findViewById(R.id.profile_image);

        final EditText name_text = findViewById(R.id.nickname_text);
        final EditText email_text = findViewById(R.id.email_text);
        final EditText age_text = findViewById(R.id.age_text);
        Button sign_up = findViewById(R.id.sign_up_button);

        RadioGroup radioGroup = findViewById(R.id.sex);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.male)
                    is_male = true;
                else
                    is_male = false;
            }
        });

        name_text.setText(nickname);
        email_text.setText(email);

        new Thread() {
            @Override
            public void run() {
                img = getBitmap(profile_img);
                handler.sendEmptyMessage(1);
            }
        }.start();

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(name_text.getText())
                        && !TextUtils.isEmpty(email_text.getText())
                        && !TextUtils.isEmpty(age_text.getText())) {
                    nickname = name_text.getText().toString();
                    email = email_text.getText().toString();
                    age = Integer.parseInt(age_text.getText().toString());

                    JSONObject jsonObject = new JSONObject();
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonObject.put("id", user_id);
                        jsonObject.put("nickname", nickname);
                        jsonObject.put("email", email);
                        jsonObject.put("age", age);
                        jsonObject.put("is_male", is_male);
                        if (img != null)
                            jsonObject.put("profile_img", bitmap2ByteArray(img));
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new JSONTask(jsonArray).execute("signed_up");
                }
                else {
                    Toast.makeText(SignupActivity.this, "모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                if (img != null)
                    profile.setImageBitmap(img);
                else
                    profile.setImageResource(R.drawable.ic_profile_121261);
            }
            else if (msg.what == 2) {
                showSignup();
            }
            else if (msg.what == 3)
                redirectMainActivity();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    redirectLoginActivity();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void requestMe() {
        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("properties.profile_image");
        keys.add("kakao_account.email");

        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                int result = errorResult.getErrorCode();
                if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
                    Toast.makeText(SignupActivity.this, errorResult.toString(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Logger.e("onSessionClosed");
                redirectLoginActivity();
            }

            @Override
            public void onSuccess(MeV2Response result) {
                user_id = result.getId();
                nickname = result.getNickname();
                email = result.getKakaoAccount().getEmail();
                profile_img = result.getProfileImagePath();

                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonObject.put("id", user_id);
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new JSONTask(jsonArray).execute("login_try");
            }
        });
    }

    private Bitmap getBitmap(String url) {
        try{
            URL imgUrl = new URL(url);
            System.out.println(imgUrl);
            URLConnection connection =imgUrl.openConnection();
            connection.connect(); //연결
            BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
            Bitmap retBitmap = BitmapFactory.decodeStream(bufferedInputStream);
            bufferedInputStream.close();
            return retBitmap;
        }catch(Exception e) {
            System.out.println("error occur");
            e.printStackTrace();
            return null;
        }
    }

    private byte[] bitmap2ByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }

    private void redirectLoginActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void redirectMainActivity() {
        Intent intent = new Intent(this, Main2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}