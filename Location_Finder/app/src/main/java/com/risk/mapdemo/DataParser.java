package com.risk.mapdemo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.id.list;

/**
 * Created by ASUS on 8/4/2017.
 */

public class DataParser {

    private HashMap<String, String> getPlace(JSONObject googlePlaceJSON){
        HashMap<String,String> googlePlaces =  new HashMap<>();
        String placeName = "--NA--";
        String vicinity = "--NA--";
        String latitude = "";
        String longitude = "";
        String reference = "";
        Log.d("DataParser","jsonobject ="+ googlePlaceJSON.toString());
        try {
            if (!googlePlaceJSON.isNull("name")) {
                    placeName = googlePlaceJSON.getString("name");

            }
            if(!googlePlaceJSON.isNull("vicintiy")){
                vicinity = googlePlaceJSON.getString("vicinity");
            }
            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJSON.getString("reference");
            googlePlaces.put("place_name",placeName);
            googlePlaces.put("vicinity",vicinity);
            googlePlaces.put("lat",latitude);
            googlePlaces.put("lng",longitude);
            googlePlaces.put("reference",reference);

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaces;

    }
    private List< HashMap<String,String> > getPlaces(JSONArray jsonArray){

        int count = jsonArray.length();
        List< HashMap<String,String>> placeList = new ArrayList<>();
        HashMap<String,String > placeMap = null;
        for(int i= 0; i< count; i++){
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i) );
                placeList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placeList;
    }

    public List<HashMap<String,String>> parse(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        Log.d("json data",jsonData);
        try {
            jsonObject =  new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);

    }
}
