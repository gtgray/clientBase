package tk.atna.clientbase.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import tk.atna.clientbase.R;
import tk.atna.clientbase.fragment.BaseFragment;
import tk.atna.clientbase.fragment.ClientsFragment;
import tk.atna.clientbase.fragment.DetailsFragment;


public class MainActivity extends BaseActivity
                          implements BaseFragment.FragmentActionCallback,
                                     FragmentManager.OnBackStackChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // just started
        if (savedInstanceState == null)
            loadFragment(ClientsFragment.class, null);

        updateActionBar();
        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
    }

    @Override
    public void onSetTitle(int title) {
        actionbar.setTitle(title);
    }

    @Override
    public void onAction(int action, Bundle data) {
        switch (action) {
            case BaseFragment.ACTION_FINISH:
                onBackPressed();
                break;

            case BaseFragment.ACTION_CLIENT_DETAILS:
                loadFragment(DetailsFragment.class, data);
                break;
        }
    }

    @Override
    public void onBackStackChanged() {
        updateActionBar();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 1)
            finish();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
    }

    private void updateActionBar() {
        // show back arrow
        if(actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(
                    getSupportFragmentManager().getBackStackEntryCount() > 1);
    }

    private <T extends BaseFragment> void loadFragment(Class<T> clazz, Bundle data) {
        FragmentManager fm = getSupportFragmentManager();
        String tag = clazz.getName();
        // if fragment created earlier
        Fragment fragment = fm.findFragmentByTag(tag);
        // no such fragment found
        if (fragment == null)
            fragment = BaseFragment.newInstance(clazz, data);
        // place result fragment
        fm.beginTransaction()
                .replace(R.id.container, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

}
