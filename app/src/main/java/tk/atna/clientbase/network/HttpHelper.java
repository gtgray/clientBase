package tk.atna.clientbase.network;


import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {

    private static final String SERVER_URL = "http://condor.alarstudios.com/test/";
    private static final String AUTH_ENDPOINT = "auth.cgi";
    private static final String DATA_ENDPOINT = "data.cgi";

    private static final String UTF_8 = "UTF-8";

    private static final String POST = "POST";
    private static final String GET = "GET";

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private static final String CODE = "?code=";
    private static final String PAGE = "&p=";
    private static final int COUNT_PER_PAGE = 10;

    private static String AUTH_CODE = "";


    public HttpHelper(Context context) {
        //
    }

    public void setAuthCode(String code) {
        AUTH_CODE = code;

//        Log.w("", "--------------- new auth-code " + AUTH_CODE);
    }

    public String signIn(String name, String pass) {
        Uri uri = new Uri.Builder()
                .appendQueryParameter(USERNAME, name)
                .appendQueryParameter(PASSWORD, pass)
                .build();
        return makeRequest(POST, AUTH_ENDPOINT, uri);
    }

    public String getClientsList(int count) {
        int page = count < 1 ? 0 : count / COUNT_PER_PAGE + 1;
        String endpoint = DATA_ENDPOINT
                + CODE + AUTH_CODE
                + PAGE + page;
        return makeRequest(GET, endpoint, null);
    }

    private String makeRequest(String method, String endpoint, Uri params) {
        try {
            URL url = new URL(SERVER_URL + endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestProperty("Content-type", "application/json");
            connection.setRequestMethod(method);
            connection.setDoInput(true);
            // params for post request
            if(POST.equals(method))
                addParams(connection, params);
            // do request
            connection.connect();
            // parse input stream
            String response = parseResponse(connection.getInputStream());
            connection.disconnect();

//            Log.w("", "--------------- " + response);

            return response;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addParams(HttpURLConnection connection, Uri params) {
        if(params != null) {
            connection.setDoOutput(true);
            try {
                PrintWriter writer = new PrintWriter(connection.getOutputStream());
                writer.print(params.getEncodedQuery());
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String parseResponse(InputStream inputStream) {
        Reader reader = null;
        try {
            reader = new InputStreamReader(inputStream, UTF_8);
            StringBuilder buffer = new StringBuilder();
            int data = reader.read();
            while (data != -1) {
                buffer.append((char) data);
                data = reader.read();
            }
            reader.close();
            return buffer.toString();

        } catch(IOException e) {
            e.printStackTrace();
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            }
        }
        return null;
    }


}

