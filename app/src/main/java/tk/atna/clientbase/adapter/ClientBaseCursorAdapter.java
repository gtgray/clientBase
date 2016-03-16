package tk.atna.clientbase.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import tk.atna.clientbase.R;
import tk.atna.clientbase.stuff.ContentManager;

import static tk.atna.clientbase.provider.ClientBaseContract.*;

public class ClientBaseCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private int imageIndex;
    private int nameIndex;

    private ContentManager contentManager = ContentManager.get();

    // flag to prevent multiple loading simultaneously
    private boolean isLoading = false;

    private OnNeedLoadListener loadListener;


    public ClientBaseCursorAdapter(Context context, OnNeedLoadListener listener) {
        super(context, null, 0);
        this.inflater = LayoutInflater.from(context);
        this.loadListener = listener;
    }

    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_clients_list, parent, false);
        ItemViewHolder holder = new ItemViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ItemViewHolder holder = (ItemViewHolder) view.getTag();
        if (cursor != null && !cursor.isClosed()) {
            contentManager.getImage(cursor.getString(imageIndex), holder.ivImage);
            holder.tvName.setText(cursor.getString(nameIndex));
            // load next page
            loadMore(cursor);
        }
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);

        rememberColumns(cursor);
    }

    public void canLoadMore() {
        isLoading = false;
    }

    private void rememberColumns(Cursor cursor) {
        if(cursor == null)
            return;

        this.imageIndex = cursor.getColumnIndex(Clients.CLIENT_IMAGE);
        this.nameIndex = cursor.getColumnIndex(Clients.CLIENT_NAME);
    }

    private void loadMore(@NonNull Cursor cursor) {
        // load next page
        int count = cursor.getCount();
        int position = cursor.getPosition();
        if(position > 0
                && position > count - 3
                && !isLoading) {
            if(loadListener != null) {
                loadListener.onNeedLoad(count);
                isLoading = true;
            }
        }
    }


    public interface OnNeedLoadListener {

        void onNeedLoad(int count);
    }


    class ItemViewHolder {

        private ImageView ivImage;
        private TextView tvName;


        ItemViewHolder(View view) {
            ivImage = (ImageView) view.findViewById(R.id.iv_image);
            tvName = (TextView) view.findViewById(R.id.tv_name);
        }

    }
}
