package tk.atna.clientbase.stuff;

import android.app.Application;

public class ClientBaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // init content manager
        ContentManager.init(this);
    }

}
