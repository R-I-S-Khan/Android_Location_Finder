package com.risk.mapdemo;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ASUS on 8/4/2017.
 */

public class DownloadURL {

    public String readUrl(String myUrl) throws IOException {

        String data ="";
        InputStream inputStream = null;
        HttpsURLConnection urlConnection = null;
        try {
            URL url = new URL(myUrl);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer sb = new StringBuffer();
            String line ="";
            while((line = br.readLine())!= null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            inputStream.close();
            urlConnection.disconnect();
        }
        Log.d("DownloadUrl","returning data " + data);
        return data;
    }
}
