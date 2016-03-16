package tk.atna.clientbase.fragment;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import tk.atna.clientbase.receiver.LocalBroadcaster;

public abstract class BaseFragment extends Fragment
                                   implements LocalBroadcaster.LocalActionListener {

    /**
     * Actions to use in activity-fragment communication.
     * As usual represents fragment clicks
     */
    public static final int ACTION_CLIENT_DETAILS = 0x00000a1;
    public static final int ACTION_FINISH = 0x00000a2;

    public static final String CLIENT_ID = "client_id";

    private LocalBroadcaster broadcaster;

    /**
     * Fragment action listener
     */
	private FragmentActionCallback callback;


    public static <T extends Fragment> T newInstance(Class<T> clazz, Bundle data) {
        try {
            T fragment = clazz.newInstance();
            fragment.setRetainInstance(true);
            // args are always present(not null),
            // even if they are not necessary
            fragment.setArguments(data);
            return fragment;

        } catch (InstantiationException | java.lang.InstantiationException
                | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Invokes fragment action callback
     *
     * @param action needed fragment command
     * @param data additional data to send
     */
	public void makeFragmentAction(int action, Bundle data) {
		if (callback != null)
			callback.onAction(action, data);
	}

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        // initializes actions callback and
        // proves that hoster activity implements it
		try {
			callback = (FragmentActionCallback) getActivity();

		} catch (ClassCastException e) {
			e.printStackTrace();
			Log.d("myLogs", BaseFragment.class.getSimpleName()
                    + ".onActivityCreated: activity must implement "
                    + FragmentActionCallback.class.getSimpleName());
		}

        if(getArguments() != null)
            processArguments(getArguments());

        if (callback != null)
            callback.onSetTitle(getTitle());

        broadcaster = new LocalBroadcaster(this);
        // start listen to local broadcaster
        if(getActivity() != null)
            getActivity().registerReceiver(broadcaster,
                    new IntentFilter(LocalBroadcaster.LOCAL_BROADCAST_FILTER));
	}

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // forget local broadcaster
        if(getActivity() != null)
            getActivity().unregisterReceiver(broadcaster);
    }

    /**
     * A way to process init arguments
     *
     * @param args init args
     */
    void processArguments(@NonNull Bundle args) {
        // override to use
    }

    /**
     * Returns fragment title resource
     *
     * @return title resource
     */
    abstract int getTitle();


    /**
     * Callback interface to deliver fragment actions to activity
     */
    public interface FragmentActionCallback {

        /**
         * Called on fragment action event
         *
         * @param action needed command
         * @param data additional data to send
         */
		void onAction(int action, Bundle data);

        /**
         * Called to set toolbar title
         *
         * @param title title to be set
         */
        void onSetTitle(int title);
	}

}
