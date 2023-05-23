package ie.mvo.simplecryptocoininfo.dataUtils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ie.mvo.simplecryptocoininfo.CoinDataContainer;
import ie.mvo.simplecryptocoininfo.intergaces.CoinListener;

public class DbHelper {
    private SQLiteDatabase dataBase;
    private static DbHelper instance;
    private List<CoinListener> subscribers = new ArrayList<>();

    public DbHelper(SQLiteDatabase dataBase) {
        this.dataBase = dataBase;
    }

    public static synchronized DbHelper getInstance(SQLiteDatabase dataBase) {
        if (instance == null) {
            instance = new DbHelper(dataBase);
        }
        return instance;
    }

    public void insertInToCoins(String name, String symbol, String coin_id, String image, String price, String change24h, String high24h, String low24h, String volume, String marketCap, String rank, long position) {
        // Create a ContentValues object with the values to insert
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("symbol", symbol);
        values.put("coin_id", coin_id);
        values.put("image", image);
        values.put("price", price);
        values.put("change24h", change24h);
        values.put("high24h", high24h);
        values.put("low24h", low24h);
        values.put("volume", volume);
        values.put("marketCap", marketCap);
        values.put("rank", rank);
        values.put("position", position);

        // Insert the values into the "coins" table
        dataBase.insert(DataBase.COIN_TABLE, null, values);
        notifySubscribers();
    }

    public void updateCoinInfo(String coinId, String column, String data) {
        ContentValues values = new ContentValues();
        values.put(column, data);

        // Define the WHERE clause
        String selection = "coin_id = ?";
        String[] selectionArgs = {coinId};

        // Update the rows that match the WHERE clause
        int count = dataBase.update(DataBase.COIN_TABLE, values, selection, selectionArgs);
    }

    public List<HashMap<String, String>> getAllCoins() {
        List<HashMap<String, String>> coins = new ArrayList<>();

        // Define the columns to retrieve
        String[] projection = {"*"};

        // Define the WHERE clause
//        String selection = "age > ?";
//        String[] selectionArgs = {"20"};

        // Define the sort order
        String sortOrder = "position DESC";

        // Query the database and get a Cursor object
        Cursor cursor = dataBase.query(DataBase.COIN_TABLE, projection, null, null, null, null, sortOrder);

        // Iterate over the rows in the Cursor and extract the data
        while (cursor.moveToNext()) {
            HashMap<String, String> coinData = new HashMap<>();
            String coinId = cursor.getString(cursor.getColumnIndexOrThrow("coin_id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
            int position = cursor.getInt(cursor.getColumnIndexOrThrow("position"));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            coinData.put("name", name);
            coinData.put("position", String.valueOf(position));
            coinData.put("id", String.valueOf(id));
            coinData.put("coin_id", coinId);
            coinData.put("symbol", symbol);
            coins.add(coinData);
            // Do something with the data...
        }

        // Close the Cursor when you're done
        cursor.close();
        return coins;
    }

    public int getRowCount() {
        Cursor cursor = dataBase.rawQuery("SELECT COUNT(*) FROM coins", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public boolean isCoinInDataBase(String coinIdName) {
        String query = "SELECT * FROM coins WHERE coin_id = ?";
        String[] selectionArgs = {coinIdName};
        Cursor cursor = dataBase.rawQuery(query, selectionArgs);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public void deleteCoin(String coin_id) {
        // Define the WHERE clause
        String selection = "coin_id = ?";
        String[] selectionArgs = {coin_id};
        // Delete the rows that match the WHERE clause
        int count = dataBase.delete(DataBase.COIN_TABLE, selection, selectionArgs);
        notifySubscribers();
    }

    public long maxPosition() {
        String[] projection = {"MAX(position) AS max_pos"};
        // Query the database and get a Cursor object
        Cursor cursor = dataBase.query(DataBase.COIN_TABLE, projection, null, null, null, null, null);

        // Extract the maximum ID value from the Cursor
        long maxId = -1;
        if (cursor.moveToFirst()) {
            maxId = cursor.getLong(cursor.getColumnIndexOrThrow("max_pos"));
        }

        // Close the Cursor when you're done
        cursor.close();
        return maxId;
    }

    public CoinDataContainer selectCoinByPosition(long position) {
        // Define the columns to retrieve
        String[] projection = {"coin_id", "name", "price", "position", "image"};

        // Define the WHERE clause
        String selection = null;
        String[] selectionArgs = null;

        // Define the sort order
        String sortOrder = "id ASC";

        // Define the position of the row to select

        // Define the number of rows to retrieve (in this case, only 1)
        int limit = 1;

        // Query the database and get a Cursor object
        Cursor cursor = dataBase.query(DataBase.COIN_TABLE, projection, selection, selectionArgs, null, null, sortOrder, position + "," + limit);
        CoinDataContainer coin_data = new CoinDataContainer();
        // Extract the data from the Cursor
        if (cursor.moveToFirst()) {
            String coin_id = cursor.getString(cursor.getColumnIndexOrThrow("coin_id"));
            int coin_position = cursor.getInt(cursor.getColumnIndexOrThrow("position"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));
            String image = cursor.getString(cursor.getColumnIndexOrThrow("image"));
            coin_data.put("coin_id", coin_id);
            coin_data.put("position", String.valueOf(coin_position));
            coin_data.put("name", name);
            coin_data.put("price", price);
            coin_data.put("image", image);
            return coin_data;
        }

        // Close the Cursor when you're done
        cursor.close();
        return null;
    }

    public void addListener(CoinListener listener) {
        subscribers.add(listener);
    }

    public void notifySubscribers() {
        for (CoinListener listener : subscribers) {
            listener.updateInfo();
        }
    }


}
