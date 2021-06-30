package utility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tinylog.Logger;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JsonReader {
    @SuppressWarnings("SpellCheckingInspection")
    private static final String GOOGLE_API_KEY = "AIzaSyAPHYqAw07v2vu7oeqlo3TymykYJIFro3M";

    private static String readAll(Reader rd) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject readJsonFromUrl(String url) throws IOException
    {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }
    public String getETA(double latX, double lngX, double latY, double lngY) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + latX + "," + lngX + "&destination=" + latY + "," + lngY + "&sensor=false&units=metric&mode=driving&key=" + GOOGLE_API_KEY;
        JSONObject json;
        String result = null;
        try {
            json = readJsonFromUrl(url);
            JSONArray array = json.getJSONArray("routes");
            JSONObject routes = array.getJSONObject(0);
            JSONArray legs = routes.getJSONArray("legs");
            JSONObject steps = legs.getJSONObject(0);
            JSONObject duration = steps.getJSONObject("duration");
            result = duration.getString("text");
        } catch (IOException e) {
            Logger.warn(e);
        }
        return result;
    }

    public Integer getValueETA(double latX, double lngX, double latY, double lngY) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + latX + "," + lngX + "&destination=" + latY + "," + lngY + "&sensor=false&units=metric&mode=driving&key=" + GOOGLE_API_KEY;
        Integer result = null;
        try {
            JSONObject json = readJsonFromUrl(url);
            JSONArray array = json.getJSONArray("routes");
            JSONObject routes = array.getJSONObject(0);
            JSONArray legs = routes.getJSONArray("legs");
            JSONObject steps = legs.getJSONObject(0);
            JSONObject duration = steps.getJSONObject("duration");
            result = duration.getInt("value");
        } catch (IOException e) {
            Logger.warn(e);
        }
        return result;
    }
}