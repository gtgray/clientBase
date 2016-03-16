package tk.atna.clientbase.stuff;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ImageHelper {

    private static LruCache<String, Bitmap> IMAGE_CACHE;

    private static final float DEFAULT_CACHE_SIZE = 0.2f;

    private ArrayList<String> loadInProgress = new ArrayList<>();


    public ImageHelper() {
        if(IMAGE_CACHE == null)
            IMAGE_CACHE = new LruCache<String, Bitmap>(Math.round(
                    DEFAULT_CACHE_SIZE * Runtime.getRuntime().maxMemory() / 1024)) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount() / 1024;
                }
            };
    }

    public static void placeBitmap(View view, Bitmap bitmap) {
        if(view != null) {
            BitmapDrawable drawable = new BitmapDrawable(view.getResources(), bitmap);
            if (view instanceof ImageView)
                ((ImageView) view).setImageDrawable(drawable);
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                view.setBackground(drawable);
            else
                view.setBackgroundDrawable(drawable);
        }
    }

    public void getImage(String url, ImageCallback callback) {
        Bitmap bitmap = IMAGE_CACHE.get(url);
        // image in cache
        if(bitmap != null) {
            if(callback != null)
                callback.onResult(bitmap);
        // not found, need load
        } else {
            if(!loadInProgress.contains(url))
                loadImage(url, callback);
        }
    }

    private void loadImage(final String url, final ImageCallback callback) {
        loadInProgress.add(url);
        (new Worker.Task<Bitmap>() {
            @Override
            public void run() {
                try {
                    this.result = doLoad(url);

                } catch (IOException ex) {
                    ex.printStackTrace();
                    this.exception = ex;
                }
            }
        }).execute(new Worker.Task.Callback<Bitmap>() {
            @Override
            public void onComplete(Bitmap result, Exception ex) {
                loadInProgress.remove(url);
                // add to cache
                IMAGE_CACHE.put(url, result);
                // notify listener
                if(callback != null)
                    callback.onResult(result);
            }
        });
    }

    private Bitmap doLoad(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        // do request
        connection.connect();
        // parse input stream
        Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
        connection.disconnect();
        return bitmap;
    }


    public interface ImageCallback {

        void onResult(Bitmap bitmap);
    }

}
