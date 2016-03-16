package tk.atna.clientbase.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static tk.atna.clientbase.provider.ClientBaseContract.*;

public class ClientBaseProvider extends ContentProvider {

    private static final String AUTHORITY = ClientBaseContract.AUTHORITY;

    private static final int MATCH_FEED = 0x00000011;
    private static final int MATCH_FEED_ITEM = 0x00000012;

    private ClientBaseDB db;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, Clients.TABLE, MATCH_FEED);
        uriMatcher.addURI(AUTHORITY, Clients.TABLE + "/*", MATCH_FEED_ITEM);
    }


    @Override
    public boolean onCreate() {
        db = new ClientBaseDB(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MATCH_FEED:
                return Clients.CONTENT_TYPE;

            case MATCH_FEED_ITEM:
                return Clients.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String table = Clients.TABLE;
        String where = null;

        switch (uriMatcher.match(uri)) {
            case MATCH_FEED:
//                sortOrder = Clients._ID + " ASC";
                break;

            case MATCH_FEED_ITEM:
                where = Clients._ID + " = '" + uri.getLastPathSegment() + "'";
                break;

            default:
                return null;
        }

        Cursor cursor = db.getWritableDatabase()
                          .query(table, null, where, null, null, null, sortOrder);
        if(getContext() != null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case MATCH_FEED:
            default:
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);

            case MATCH_FEED_ITEM:
                break;
        }

        String table = Clients.TABLE;
        // insert row
        long row = db.getWritableDatabase().insert(table, null, values);
//        notifyChange(uri);

        return ContentUris.withAppendedId(uri, row);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase dBase = db.getWritableDatabase();
        String table;
        String where;

        switch (uriMatcher.match(uri)) {
            case MATCH_FEED:
                db.dropTableClients(dBase);
                db.createTableClients(dBase);
                return 0;

            case MATCH_FEED_ITEM:
                table = Clients.TABLE;
                where = Clients._ID + " = '" + uri.getLastPathSegment() + "'";
                break;

            default:
                throw new UnsupportedOperationException("Unknown delete uri: " + uri);
        }

        selection = (selection == null || selection.length() == 0)
                ? where : selection + " AND " + where;

        int count = dBase.delete(table, selection, null);
//        notifyChange(uri);

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case MATCH_FEED:
            default:
                throw new UnsupportedOperationException("Unknown update uri: " + uri);

            case MATCH_FEED_ITEM:
                break;
        }
            int rows = delete(uri, selection, null);
            insert(uri, values);
            notifyChange(uri);
            return rows;
    }

    private void notifyChange(Uri uri) {
        if(getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);
    }

}
