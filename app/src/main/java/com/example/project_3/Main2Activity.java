package com.example.project_3;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    static final int THREAD_HANDLER_SUCCESS_INFO = 1;
    static String Weather;
    static int w_type;
    static ArrayList<clothes> clothes_list;

    private ViewPager viewPager;
    private LocationManager locationManager;

    private ArrayList<ContentValues> mWeatherData;
    private ArrayList<WeatherInfo> mWeatherInfomation;
    private ForeCastManager mForeCast;

    public PagerAdapter pagerAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //커스텀툴바
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //getSupportActionBar().setHomeAsUpIndicator(null);

        TabLayout tablayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);

        mWeatherInfomation = new ArrayList<>();

        pagerAdapter = new PageAdapter(getSupportFragmentManager(), tablayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        clothes_list = new ArrayList<>();

        //tablayout.setTabTextColors(Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF"));

        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition()==0){
                    pagerAdapter.notifyDataSetChanged();
                }else if(tab.getPosition()==1){
                    pagerAdapter.notifyDataSetChanged();
                }else if(tab.getPosition()==2){
                    pagerAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));

        ArrayList<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                Toast.makeText(this, "We need your permission for accessing your location.", Toast.LENGTH_SHORT).show();
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
                Toast.makeText(this, "We need your permission for reading your gallery.", Toast.LENGTH_SHORT).show();
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissions.size() > 0) {
            String[] per_array = new String[permissions.size()];
            per_array = permissions.toArray(per_array);
            requestPermissions(per_array, 1);
        } else
            do_work();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.menu_logout) {
            Toast.makeText(getApplicationContext(), "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                do_work();
            }else if (grantResults.length == 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                do_work();
            }else
                finish();
        }
    }

    private void do_work() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("id", SignupActivity.user_id);
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new JSONTask(jsonArray).execute("get_clothes");

        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, mLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 1, mLocationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            locationManager.removeUpdates(mLocationListener);

            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            String lon = Double.toString(longitude);
            String lat = Double.toString(latitude);

            mForeCast = new ForeCastManager(lon, lat, Main2Activity.this);
            mForeCast.run();
        }
        public void onProviderDisabled(String provider) {}

        public void onProviderEnabled(String provider) {}

        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private String PrintValue() {
        String mData = mWeatherInfomation.get(0).getWeather_Name() + "\n" +
                "최고: " + mWeatherInfomation.get(0).getTemp_Max() + "℃"
                +  "/최저: " + mWeatherInfomation.get(0).getTemp_Min() + "℃"
                + "/체감: " + mWeatherInfomation.get(0).getTemp_Feel() + "℃";

        int weather_num = Integer.parseInt(mWeatherInfomation.get(0).getWeather_Number());

        if (200 <= weather_num && weather_num <= 232)
            w_type = R.drawable.ic_cloud_drizzle_lightning;
        else if (300 <= weather_num && weather_num <= 531)
            w_type = R.drawable.ic_cloud_rain;
        else if (600 <= weather_num && weather_num <= 622)
            w_type = R.drawable.ic_cloud_snow;
        else if (701 <= weather_num && weather_num <= 762)
            w_type = R.drawable.ic_fog;
        else if (weather_num == 771 && weather_num == 781 && 960 <= weather_num && weather_num <= 962)
            w_type = R.drawable.ic_tornado;
        else if (weather_num == 800 && weather_num == 951)
            w_type = R.drawable.ic_sun;
        else if (801 <= weather_num && weather_num <= 804)
            w_type = R.drawable.ic_cloud;
        else
            w_type = R.drawable.ic_wind;

        return mData;
    }

    private void DataChangedToHangeul() {
        for(int i = 0 ; i <mWeatherInfomation.size(); i ++) {
            WeatherToHangeul mHangeul = new WeatherToHangeul(mWeatherInfomation.get(i));
            mWeatherInfomation.set(i,mHangeul.getHangeulWeather());
        }
    }

    private void DataToInformation() {
        for(int i = 0; i < mWeatherData.size(); i++) {
            mWeatherInfomation.add(new WeatherInfo(
                    String.valueOf(mWeatherData.get(i).get("weather_Name")),  String.valueOf(mWeatherData.get(i).get("weather_Number")), String.valueOf(mWeatherData.get(i).get("weather_Much")),
                    String.valueOf(mWeatherData.get(i).get("weather_Type")),  String.valueOf(mWeatherData.get(i).get("wind_Direction")),  String.valueOf(mWeatherData.get(i).get("wind_SortNumber")),
                    String.valueOf(mWeatherData.get(i).get("wind_SortCode")),  String.valueOf(mWeatherData.get(i).get("wind_Speed")),  String.valueOf(mWeatherData.get(i).get("wind_Name")),
                    String.valueOf(mWeatherData.get(i).get("temp_Min")),  String.valueOf(mWeatherData.get(i).get("temp_Max")),  String.valueOf(mWeatherData.get(i).get("humidity")),
                    String.valueOf(mWeatherData.get(i).get("Clouds_Value")),  String.valueOf(mWeatherData.get(i).get("Clouds_Sort")), String.valueOf(mWeatherData.get(i).get("Clouds_Per")),String.valueOf(mWeatherData.get(i).get("day")),
                    String.valueOf(mWeatherData.get(i).get("temp_Feel")))
            );
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("프로그램 종료")
                .setMessage("정말로 프로그램을 종료하시겠습니까?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        System.runFinalization();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == THREAD_HANDLER_SUCCESS_INFO) {
                mWeatherData = mForeCast.getmWeather();

                DataToInformation();

                DataChangedToHangeul();
                Weather = PrintValue();
                FragmentOne.weather.setText(Weather);
                FragmentOne.w_image.setImageResource(w_type);
            }
        }
    };
}
