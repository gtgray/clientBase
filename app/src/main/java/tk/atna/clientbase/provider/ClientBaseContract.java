package tk.atna.clientbase.provider;

import android.net.Uri;

public final class ClientBaseContract {

    public static final String AUTHORITY = "tk.atna.clientbase.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String TYPE_PREFIX = "vnd.android.cursor.dir/vnd.clientbase.";
    public static final String ITEM_TYPE_PREFIX = "vnd.android.cursor.item/vnd.clientbase.";


    private ClientBaseContract() {
        // nothing here
    }


    interface BaseColumn {

        String _ID = "_id";
    }


    interface ClientsColumns {

        String CLIENT_NAME = "client_name";
        String CLIENT_EMAIL = "client_email";
        String CLIENT_IMAGE = "client_image";
        String CLIENT_LAT = "client_lat";
        String CLIENT_LON = "client_lon";
    }


    public static class Clients implements BaseColumn, ClientsColumns {

        public static final String TABLE = "clients";

        public static final String CONTENT_TYPE = getContentType(TABLE);
        public static final String CONTENT_ITEM_TYPE = getContentItemType(TABLE);

        public static final Uri CONTENT_URI = getContentUri(TABLE);
    }

    private static String getContentType(String table) {
        return TYPE_PREFIX + table;
    }

    private static String getContentItemType(String table) {
        return ITEM_TYPE_PREFIX + table;
    }

    private static Uri getContentUri(String table) {
        return BASE_CONTENT_URI.buildUpon().appendPath(table).build();
    }
}
