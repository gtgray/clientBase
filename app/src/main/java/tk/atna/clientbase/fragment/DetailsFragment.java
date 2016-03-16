package tk.atna.clientbase.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import tk.atna.clientbase.R;
import tk.atna.clientbase.model.Client;
import tk.atna.clientbase.stuff.ContentManager;

public class DetailsFragment extends BaseFragment implements OnMapReadyCallback {

    public static final int TITLE = R.string.client_details;

    private ContentManager contentManager = ContentManager.get();

    private Client client;

    private ImageView ivImage;
    private TextView tvName;
    private TextView tvEmail;
    private TextView tvPoint;


    public DetailsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // process init arguments
        if(getArguments() != null) {
            // seeking for id
            int id = getArguments().getInt(CLIENT_ID);
            // no client id - finish
            if(id == 0) {
                finish();
                return;
            }
            // remember id
            client = new Client(id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ivImage = (ImageView) view.findViewById(R.id.iv_image);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvEmail = (TextView) view.findViewById(R.id.tv_email);
        tvPoint = (TextView) view.findViewById(R.id.tv_point);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // add map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // pulls client data from provider
        pullClient();
    }

    @Override
    int getTitle() {
        return TITLE;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        showPoint(googleMap);
    }

    @Override
    public void onReceive(int action, Bundle data) {
        // nothing to catch
    }

    private void finish() {
        makeFragmentAction(ACTION_FINISH, null);
        if(getActivity() != null)
            Toast.makeText(getActivity(), R.string.no_client, Toast.LENGTH_LONG).show();
    }

    private void pullClient() {
        contentManager.getClient(client.getId(),
                new ContentManager.ContentCallback<Client>() {
                    @Override
                    public void onResult(Client result, Exception exception) {
                        if (exception != null)
                            finish();
                        else
                            populateViews(client = result);
                    }
                });
    }

    private void populateViews(Client client) {
        contentManager.getImage(client.getImage(), ivImage);
        tvName.setText(client.getName());
        tvEmail.setText(client.getEmail());
        tvPoint.setText(client.getLat() + ", " + client.getLon());
    }

    private void showPoint(final GoogleMap map) {
        if(client != null
                && !TextUtils.isEmpty(client.getName())) {
            LatLng point = new LatLng(client.getLat(), client.getLon());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 13));
            map.addMarker(new MarkerOptions().title(client.getName()).position(point));
        } else
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPoint(map);
                }
            }, 500);
    }

}
