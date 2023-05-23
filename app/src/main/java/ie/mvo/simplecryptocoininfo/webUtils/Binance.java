package ie.mvo.simplecryptocoininfo.webUtils;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import ie.mvo.simplecryptocoininfo.CoinDataContainer;

public class Binance {
    public static final String BASE_URL = "https://api.binance.com";
    private static final String BASE_METHOD = "/api";
    private static final String API_VERSION_3 = "v3";

    private static Uri.Builder generateBaseUrl() {
        return Uri.parse(BASE_URL + BASE_METHOD).buildUpon();
    }

    private static URL convertUrl(Uri.Builder rawUrl) {
        // Final convertation from Uri.Builder to URL
        URL finalUrl = null;
        try {
            finalUrl = new URL(rawUrl.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return finalUrl;
    }

    public static String getCoin(String coin) {
        String response = "";
        Uri.Builder base_uri = generateBaseUrl()
                .appendEncodedPath(API_VERSION_3)
                .appendEncodedPath("ticker/price");
//                .appendEncodedPath(coin.toLowerCase());
        base_uri.appendQueryParameter("symbol", (coin+"USDT").toUpperCase());
        URL convertedUrl = convertUrl(base_uri);
        try {
            // if coin not found CoinGecko response 404
            response = getResponse(convertedUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.isEmpty()){
            return null;
        }else {
            return response;
        }
    }

    private static String getResponse(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            boolean request = scanner.hasNext();
            if (request) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static CoinDataContainer parseBaseInfoCoin(String data) {
        if (data == null){
            return null;
        }
        CoinDataContainer coinData = new CoinDataContainer();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(data);
        }catch (JSONException e){
            return null;
        }
        try {
            coinData.put("price", jsonObject.getString("price"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return coinData;
    }

}
