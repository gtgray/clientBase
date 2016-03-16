package tk.atna.clientbase.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;

import static tk.atna.clientbase.provider.ClientBaseContract.Clients;

public class ContentMapper {


    public static void pushClientsToProvider(Context context, List<Client> clients) {
        if(context != null
                && clients != null) {
            for (Client client : clients) {
                ContentMapper.pushClientToProvider(context.getContentResolver(), client);
            }
        }
    }

     private static void pushClientToProvider(ContentResolver cr, Client client) {
        if(cr != null
                && client != null) {
            ContentValues cv = new ContentValues();
            cv.put(Clients._ID, client.id);
            cv.put(Clients.CLIENT_NAME, client.name);
            cv.put(Clients.CLIENT_EMAIL, client.email);
            cv.put(Clients.CLIENT_IMAGE, client.image);
            cv.put(Clients.CLIENT_LAT, String.valueOf(client.lat));
            cv.put(Clients.CLIENT_LON, String.valueOf(client.lon));
            // update values
            cr.update(Uri.withAppendedPath(
                    Clients.CONTENT_URI, String.valueOf(client.id)), cv, null, null);
        }
    }

    public static Client pullClientFromProvider(Context context, long id) {
        if(context != null) {
            Cursor cursor = context.getContentResolver().query(Uri.withAppendedPath(
                    Clients.CONTENT_URI, String.valueOf(id)), null, null, null, null);
            if (cursor != null) {
                Client client = cursorToClient(cursor);
                cursor.close();
                return client;
            }
        }
        return null;
    }

    private static Client cursorToClient(Cursor cursor) {
        if(cursor != null
                && cursor.moveToFirst()) {
            return new Client(cursor.getLong(cursor.getColumnIndex(Clients._ID)))
                    .setName(cursor.getString(
                            cursor.getColumnIndex(Clients.CLIENT_NAME)))
                    .setEmail(cursor.getString(
                            cursor.getColumnIndex(Clients.CLIENT_EMAIL)))
                    .setImage(cursor.getString(
                            cursor.getColumnIndex(Clients.CLIENT_IMAGE)))
                    .setLat(Double.valueOf(cursor.getString(
                            cursor.getColumnIndex(Clients.CLIENT_LAT))))
                    .setLon(Double.valueOf(cursor.getString(
                            cursor.getColumnIndex(Clients.CLIENT_LON))));
        }
        return null;
    }

}
