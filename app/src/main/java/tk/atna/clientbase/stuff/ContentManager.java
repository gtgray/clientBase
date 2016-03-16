package tk.atna.clientbase.stuff;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ImageView;

import java.util.List;

import tk.atna.clientbase.model.AuthResult;
import tk.atna.clientbase.model.Client;
import tk.atna.clientbase.model.ContentMapper;
import tk.atna.clientbase.network.HttpHelper;
import tk.atna.clientbase.network.SignInFailedException;
import tk.atna.clientbase.provider.NoSuchClientException;
import tk.atna.clientbase.receiver.LocalBroadcaster;

public class ContentManager {

    private static ContentManager INSTANCE;

    private Context context;

    private HttpHelper httpHelper;
    private ImageHelper imageHelper;


    private ContentManager(Context context) {
        this.context = context;
        this.httpHelper = new HttpHelper(context);
        this.imageHelper = new ImageHelper();
    }

    /**
     * Initializes content manager.
     * It is better to give it an application context
     *
     * @param context application context
     */
    public static synchronized void init(Context context) {
        if(context == null)
            throw new NullPointerException("Can't create instance with null context");
        if(INSTANCE != null)
            throw new IllegalStateException("Can't initialize ContentManager twice");

        INSTANCE = new ContentManager(context);
    }

    /**
     * Gets only instance of content manager.
     * Can't be called from non UI thread
     *
     * @return content manager instance
     */
    public static ContentManager get() {
        if(Looper.myLooper() != Looper.getMainLooper())
            throw new IllegalStateException("Method get() must be called from UI thread");

        if(INSTANCE == null)
            throw new IllegalStateException("ContentManager is null. It must have been"
                                          + " created at application init");

        return INSTANCE;
    }

    public void signIn(final String name, final String pass, final ContentCallback callback) {
        (new Worker.SimpleTask() {
            @Override
            public void run() {
                try {
                    String result = httpHelper.signIn(name, pass);
                    AuthResult authResult = AuthResult.parse(result);
                    if(authResult == null
                            || !AuthResult.Status.OK.equals(authResult.getStatus()))
                        throw new SignInFailedException();

                    // remember auth code
                    httpHelper.setAuthCode(authResult.getCode());

                } catch (SignInFailedException ex) {
                    ex.printStackTrace();
                    this.exception = ex;
                }
            }
        }).execute(new Worker.SimpleTask.Callback() {
            @Override
            public void onComplete(Exception ex) {
                if (callback != null)
                    callback.onResult(null, ex);
            }
        });
    }

    public void getClientsList(final int count) {
        (new Worker.Task<Integer>() {
            @Override
            public void run() {
                try {

                    String result = httpHelper.getClientsList(count);
                    List<Client> clientsList = Client.parse(result);
                    ContentMapper.pushClientsToProvider(context, clientsList);
                    this.result = clientsList.size();

                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                    this.exception = ex;
                }
            }
        }).execute(new Worker.Task.Callback<Integer>() {
            @Override
            public void onComplete(Integer result, Exception ex) {
                notifyChanges(ex == null ? Actions.CLIENTS_LIST
                                         : Actions.CLIENTS_LIST_FAILED, null);
            }
        });
    }

    public void getClient(final long clientId, final ContentCallback<Client> callback) {
        (new Worker.Task<Client>() {
            @Override
            public void run() {
                try {
                    Client client = ContentMapper.pullClientFromProvider(context, clientId);
                    if(client == null)
                        throw new NoSuchClientException();

                    this.result = client;

                } catch (NoSuchClientException ex) {
                    ex.printStackTrace();
                    this.exception = ex;
                }
            }
        }).execute(new Worker.Task.Callback<Client>() {
            @Override
            public void onComplete(Client result, Exception ex) {
                if(callback != null)
                    callback.onResult(result, ex);
            }
        });
    }

    public void getImage(String url, final ImageView view) {
        imageHelper.getImage(url, new ImageHelper.ImageCallback() {
            @Override
            public void onResult(Bitmap bitmap) {
                if(bitmap != null)
                    ImageHelper.placeBitmap(view, bitmap);
            }
        });
    }

    /**
     * Sends local broadcast notification with data
     *
     * @param action action to process
     * @param data data to act with
     */
    private void notifyChanges(int action, Bundle data) {
        LocalBroadcaster.sendLocalBroadcast(action, data, context);
    }


    public interface Actions {

        int CLIENTS_LIST = 0x0000ca10;
        int CLIENTS_LIST_FAILED = 0x0000ca11;

    }


    /**
     * Content manager callback to return data after async extraction
     *
     * @param <T> Object to receive as a result
     */
    public interface ContentCallback<T> {
        /**
         * Fires when async data extraction is completed and data/exception
         * is ready to be returned
         *
         * @param result received result
         * @param exception possible exception
         */
        void onResult(T result, Exception exception);
    }

}
