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

public class CoinGecko {
    String a = "https://dev.vk.com/method/users.get";
    public static final String BASE_URL = "https://api.coingecko.com";
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
                .appendEncodedPath("coins")
                .appendEncodedPath(coin.toLowerCase());
        // url = base_uri.appendQueryParameter("id", coin);
        URL convertedUrl = convertUrl(base_uri);
        try {
            // if coin not found CoinGecko response 404
            response = getResponse(convertedUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
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
        if (data.isEmpty()){
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
            coinData.put("image", jsonObject.getJSONObject("image").getString("large"));
            coinData.put("name", jsonObject.getString("name"));
            coinData.put("coin_id", jsonObject.getString("id").toLowerCase());
            coinData.put("symbol", jsonObject.getString("symbol").toLowerCase());
            coinData.put("price", jsonObject.getJSONObject("market_data").getJSONObject("current_price").getString("usd"));
            coinData.put("change24h", jsonObject.getJSONObject("market_data").getString("price_change_24h"));
            coinData.put("high24h", jsonObject.getJSONObject("market_data").getJSONObject("high_24h")
                    .getString("usd"));
            coinData.put("low24h", jsonObject.getJSONObject("market_data").getJSONObject("low_24h")
                    .getString("usd"));
            coinData.put("volume", jsonObject.getJSONObject("market_data").getJSONObject("total_volume")
                    .getString("usd"));
            coinData.put("marketCap", jsonObject.getJSONObject("market_data").getJSONObject("market_cap")
                    .getString("usd"));
            coinData.put("rank", jsonObject.getString("market_cap_rank"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return coinData;
    }

}
