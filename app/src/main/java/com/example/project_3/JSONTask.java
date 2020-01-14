package com.example.project_3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONTask extends AsyncTask<String, String ,String > {
    private JSONArray jsonArray;
    private final String server = "http://192.168.0.78:3000/";
    private String process;

    public JSONTask(JSONArray _jsonArray) {
        jsonArray = _jsonArray;
    }

    @Override
    protected String doInBackground (String... urls) {
        try {
            HttpURLConnection con = null;
            BufferedReader reader = null;

            try{
                process = urls[0];
                URL url = new URL(server + urls[0]);
                con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("POST");
                con.setRequestProperty("Cache-Control", "no-cache");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "text/html");
                con.setDoOutput(true);
                con.setDoInput(true);
                con.connect();

                String s = jsonArray.toString();
                int len = s.length();

                OutputStream outStream = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream), len);

                Log.e("data", s);
                System.out.println(len);
                writer.write(s);
                writer.flush();
                writer.close();

                InputStream stream = con.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();

                String line;
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                return buffer.toString();

            } catch (IOException e){
                e.printStackTrace();
            } finally {
                if(con != null){
                    con.disconnect();
                }
                try {
                    if(reader != null){
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        switch (process) {
            case "login_try" :
                JSONObject json1;
                try {
                    json1 = new JSONObject(result);
                    if (json1.getBoolean("result"))
                        SignupActivity.s_this.handler.sendEmptyMessage(2);
                    else
                        SignupActivity.s_this.handler.sendEmptyMessage(3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "signed_up" :
                JSONObject json2;
                try {
                    json2 = new JSONObject(result);
                    if (json2.getBoolean("result"))
                        SignupActivity.s_this.handler.sendEmptyMessage(3);
                    else
                        SignupActivity.s_this.handler.sendEmptyMessage(2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "get_clothes" :
                Main2Activity.clothes_list.clear();
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            clothes c = new clothes(getBitmapFromString(jsonArray.getJSONObject(i).getString("_cloth_image"))
                                    , jsonArray.getJSONObject(i).getString("_group1")
                                    , jsonArray.getJSONObject(i).getString("_group2"));
                            Main2Activity.clothes_list.add(c);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "add_cloth" :

                break;
        }
    }

    private Bitmap byteArray2Bitmap(byte[] bytes) {return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);}

    private Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
