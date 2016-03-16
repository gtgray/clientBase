package tk.atna.clientbase.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static tk.atna.clientbase.provider.ClientBaseContract.*;

public class ClientBaseDB extends SQLiteOpenHelper {

    static final String DB_NAME = "clientbase.db";
    static final int DB_VERSION = 1;

    // table clients
    private static final String CREATE_TABLE_CLIENTS =
            "CREATE TABLE " + Clients.TABLE + " ("
                    + BaseColumn._ID + " INTEGER PRIMARY KEY, "
                    + Clients.CLIENT_NAME + " TEXT NOT NULL, "
                    + Clients.CLIENT_EMAIL + " TEXT, "
                    + Clients.CLIENT_IMAGE + " TEXT, "
                    + Clients.CLIENT_LAT + " TEXT, "
                    + Clients.CLIENT_LON + " TEXT);";

    private static final String DROP_TABLE_CLIENTS =
            "DROP TABLE IF EXISTS " + Clients.TABLE + ";";


	public ClientBaseDB(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        createTableClients(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// nothing here
    }

    void createTableClients(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_CLIENTS);
    }

    void dropTableClients(SQLiteDatabase db) {
		db.execSQL(DROP_TABLE_CLIENTS);
    }

}
