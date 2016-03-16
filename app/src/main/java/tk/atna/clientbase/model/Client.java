package tk.atna.clientbase.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Client {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String IMAGE = "image";
    private static final String LAT = "lat";
    private static final String LON = "lon";

    long id;
    String name;
    String email;
    String image;
    double lat;
    double lon;


    public Client(long id) {
        this.id = id;
    }

    public static List<Client> parse(String json) {
        ArrayList<Client> clientList = new ArrayList<>();
        try {
            JSONArray jClients = new JSONArray(json);
            int size = jClients.length();
            for(int i = 0; i < size; i++) {
                JSONObject jClient = jClients.optJSONObject(i);
                if(jClient != null) {
                    Client client = parse(jClient);
                    if(client != null)
                        clientList.add(client);
                }
            }
            return clientList;

        } catch(JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Client parse(JSONObject jClient) {
        try {
            return new Client(jClient.getLong(ID))
                    .setName(jClient.getString(NAME))
                    .setEmail(jClient.getString(EMAIL))
                    .setImage(jClient.getString(IMAGE))
                    .setLat(jClient.getDouble(LAT))
                    .setLon(jClient.getDouble(LON));

        } catch(JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getId() {
        return id;
    }

    public double getLon() {
        return lon;
    }

    public Client setLon(double lon) {
        this.lon = lon;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public Client setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Client setImage(String image) {
        this.image = image;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Client setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getName() {
        return name;
    }

    public Client setName(String name) {
        this.name = name;
        return this;
    }
}
