/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Wesley, Jordan, Clare
 * This file handles converting lat and long into country, state, and county.
 * We do not recommend changing this file. 
 */

public class Location {
    
    // class variables to hold data and api calls
    String country;
    String state;
    String county;
    String USUrl;
    String CountryUrl;
    
    public Location(double latitude, double longitude) {
        // assign api calls variables 
        USUrl = "https://geo.fcc.gov/api/census/area?lat=" + latitude + "&lon=" + longitude + "&censusYear=2020&format=json";
        CountryUrl = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + latitude + "&lon=" + longitude;

        // read from the country api call to get country data
        JSONObject countryjson;
        try {
            countryjson = readJsonFromUrl(CountryUrl);
            if (countryjson.has("error")) {
                // if the api returns an error (ie. the user clicked in the ocean) 
                System.out.println("Please select a country");
                country = "null";
            } else {
                // otherwise get the data 
                if (!countryjson.getJSONObject("address").isNull("country_code")) {
                    country = countryjson.getJSONObject("address").getString("country_code");
                } 
            }
        } catch (IOException ex) {
            Logger.getLogger(Location.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(Location.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // if the country is us, read from the us api call to get state, county data
        if (country.equals("us")) {
            JSONObject json;
            try {
                json = readJsonFromUrl(USUrl);
                if (!json.getJSONArray("results").isNull(0)) {
                    county = json.getJSONArray("results").getJSONObject(0).getString("county_name");
                    state = json.getJSONArray("results").getJSONObject(0).getString("state_name");
                } 
            } catch (IOException ex) {
                Logger.getLogger(Location.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(Location.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
    
    // print the location to terminal, this is a check used to make sure variables are not curropted
    public void printLocation() {
        if (country.equals("null")) {
            System.out.println("Not a valid country");
        } else if (country.equals("us")) {
            System.out.println(county + ", " + state);
        } else {
            System.out.println(country);
        }
    }

}