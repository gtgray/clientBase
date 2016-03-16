package tk.atna.clientbase.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import tk.atna.clientbase.R;
import tk.atna.clientbase.adapter.ClientBaseCursorAdapter;
import tk.atna.clientbase.provider.ClientBaseContract;
import tk.atna.clientbase.stuff.ContentManager;
import tk.atna.clientbase.view.ProgressListView;

public class ClientsFragment extends BaseFragment
                          implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int TITLE = R.string.clients;

    public static final int CLIENTS_CURSOR_LOADER = 0x00000cc1;

    private ContentManager contentManager = ContentManager.get();

    private ClientBaseCursorAdapter adapter;

    private ProgressListView clientsList;

    // current list position
    private int currItem;


    public ClientsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clients, container, false);
        clientsList = (ProgressListView) view.findViewById(R.id.clients_list);
        if(adapter == null)
            adapter = new ClientBaseCursorAdapter(inflater.getContext(),
                    new ClientBaseCursorAdapter.OnNeedLoadListener() {
                        @Override
                        public void onNeedLoad(int count) {
                            clientsList.showFooterProgress(true);
                            contentManager.getClientsList(count);
                        }
                    });
        clientsList.setAdapter(adapter);
        clientsList.setSelection(currItem);
        clientsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // calls details fragment
                Bundle data = new Bundle();
                data.putInt(CLIENT_ID, (int) id);
                makeFragmentAction(ACTION_CLIENT_DETAILS, data);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // initializes loader manager for cursor
        if(getActivity() != null)
            getActivity().getSupportLoaderManager()
                         .initLoader(CLIENTS_CURSOR_LOADER, null, this);
        // refresh list at start
        if(savedInstanceState == null)
            contentManager.getClientsList(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // remember list position
        currItem = clientsList.getFirstVisiblePosition();
    }

    @Override
    int getTitle() {
        return TITLE;
    }

    @Override
    public void onReceive(int action, Bundle data) {
        switch (action) {
            // just show loading stopped
            case ContentManager.Actions.CLIENTS_LIST:
            case ContentManager.Actions.CLIENTS_LIST_FAILED:
                stopLoading();
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                                ClientBaseContract.Clients.CONTENT_URI,
                                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == CLIENTS_CURSOR_LOADER) {
            adapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.changeCursor(null);
    }

    private void stopLoading() {
        // hide progress
        if(clientsList != null)
            clientsList.showFooterProgress(false);
        // notify adapter
        if(adapter != null)
            adapter.canLoadMore();
    }

}
