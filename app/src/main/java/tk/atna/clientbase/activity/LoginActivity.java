package tk.atna.clientbase.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import tk.atna.clientbase.R;
import tk.atna.clientbase.stuff.ContentManager;

public class LoginActivity extends AppCompatActivity {

    private ViewGroup progress;
    private EditText etName;
    private EditText etPass;
    private TextView tvError;
    private TextView btnSingIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // lock orientation changes only for auth
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // prepare views
        initViews();
    }

    private void initViews() {
        progress = (ViewGroup) findViewById(R.id.progress);
        etName = (EditText) findViewById(R.id.et_name);
        etPass = (EditText) findViewById(R.id.et_password);
        tvError = (TextView) findViewById(R.id.tv_error);
        btnSingIn = (TextView) findViewById(R.id.btn_sign_in);
        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        btnSingIn.setEnabled(false);
        etName.setError(null);
        etPass.setError(null);
        showError(false);
        boolean cancel;

        if(cancel = invalidName())
            etName.setError(getString(R.string.name_error));

        if(cancel = invalidPass())
            etPass.setError(getString(R.string.pass_error));

        if(cancel)
            btnSingIn.setEnabled(true);
        else {
            showProgress(true);
            ContentManager.get().signIn(etName.getText().toString(), etPass.getText().toString(),
                    new ContentManager.ContentCallback() {
                        @Override
                        public void onResult(Object result, Exception exception) {
                            if(exception != null) {
                                showProgress(false);
                                btnSingIn.setEnabled(true);
                                showError(true);

                            } else {
                                MainActivity.display(LoginActivity.this, MainActivity.class);
                                finish();
                            }
                        }
                    });
        }

    }

    private boolean invalidName() {
        // fake check
        return etName.getText().length() < 4;
    }

    private boolean invalidPass() {
        // fake check
        return etPass.getText().length() < 3;
    }

    private void showProgress(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showError(boolean show) {
        tvError.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
