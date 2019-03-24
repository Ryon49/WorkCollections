package com.wdong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wdong.R;
import com.wdong.network.NetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActivity";

    @BindView(R.id.login_email)
    EditText mEmail;
    @BindView(R.id.login_password)
    EditText mPassword;
    @BindView(R.id.login_error)
    TextView mLoginError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.login_btn)
    public void onClick(View v) {

        final String emailContent = mEmail.getText().toString();
        if (TextUtils.isEmpty(emailContent)) {
            mLoginError.setText(getString(R.string.login_empty_email));
            mEmail.requestFocus();
            return;
        }
        final String passwordContent = mPassword.getText().toString();
        if (TextUtils.isEmpty(passwordContent)) {
            mLoginError.setText(getString(R.string.login_empty_password));
            mPassword.requestFocus();
            return;
        }

        login(emailContent, passwordContent);
    }

    private void login(final String email, final String password) {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        String url = "http://13.52.165.20:8080/Fablix/api/user/login";

        StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            boolean success = json.getBoolean("success");
                            if (!success) {
                                mLoginError.setText(json.getString("errMsg"));
                            } else {
                                mLoginError.setText("");

                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                Log.i(TAG, "use intent to jump to home page");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.getMessage());
                mLoginError.setText(getString(R.string.login_network_error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };
        queue.add(loginRequest);
    }
}
