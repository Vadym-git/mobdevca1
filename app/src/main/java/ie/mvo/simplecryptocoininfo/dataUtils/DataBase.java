package ie.mvo.simplecryptocoininfo.dataUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {
    private static DataBase instance;

    public static final String COIN_TABLE = "coins";
    private static final String DATABASE_NAME = "cryptoCoin.db";
    private static final int DATABASE_VERSION = 1;

    public static synchronized DataBase getInstance(Context context) {
        if (instance == null) {
            instance = new DataBase(context.getApplicationContext());
        }
        return instance;
    }

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the tables in the database
        StringBuilder createTables = new StringBuilder();
        createTables.append("CREATE TABLE ");
        createTables.append(COIN_TABLE);
        createTables.append(" (");
        // start pats you request
        createTables.append("id INTEGER PRIMARY KEY,");
        createTables.append("name TEXT, ");
        createTables.append("symbol TEXT, ");
        createTables.append("coin_id TEXT UNIQUE, ");
        createTables.append("image TEXT, ");
        createTables.append("price TEXT, ");
        createTables.append("change24h TEXT, ");
        createTables.append("high24h TEXT, ");
        createTables.append("low24h TEXT, ");
        createTables.append("volume TEXT, ");
        createTables.append("marketCap TEXT, ");
        createTables.append("rank TEXT, ");
        createTables.append("position INTEGER");
        // end of your request
        createTables.append(")");
        db.execSQL(createTables.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade the database when the version changes
        // Here you could modify the existing tables or create new ones
    }
}
