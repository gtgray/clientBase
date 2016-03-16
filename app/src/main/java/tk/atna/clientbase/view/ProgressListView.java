package tk.atna.clientbase.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import tk.atna.clientbase.R;


public class ProgressListView extends ListView {

    private static final int RESOURCE = R.layout.progress_list_view;

    private View footer;


    public ProgressListView(Context context) {
        super(context);

        initProgress();
    }

    public ProgressListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initProgress();
    }

    public ProgressListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initProgress();
    }

    public void showFooterProgress(boolean show) {
        if(show && getFooterViewsCount() == 0)
            addFooterView(footer, null, false);

        else if(!show && getFooterViewsCount() == 1)
            removeFooterView(footer);
    }

    private void initProgress() {
        footer = LayoutInflater.from(getContext()).inflate(RESOURCE, this, false);
        addFooterView(footer, null, false);
    }

}