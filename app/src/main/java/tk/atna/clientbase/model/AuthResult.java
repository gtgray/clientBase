package tk.atna.clientbase.model;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthResult {

    private static final String STATUS = "status";
    private static final String CODE = "code";

    String status;
    String code;


    public AuthResult(String status, String code) {
        this.status = status;
        this.code = code;
    }

    public static AuthResult parse(String json) {
        try {
            JSONObject jResult = new JSONObject(json);
            return new AuthResult(jResult.getString(STATUS), jResult.optString(CODE, null));

        } catch(JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }


    public interface Status {

        String OK = "ok";
        String WRONG = "wrong";
    }

}
