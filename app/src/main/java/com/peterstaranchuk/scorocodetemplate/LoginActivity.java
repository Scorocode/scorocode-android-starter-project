package com.peterstaranchuk.scorocodetemplate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackLoginUser;
import ru.profit_group.scorocode_sdk.Responses.user.ResponseLogin;
import ru.profit_group.scorocode_sdk.ScorocodeSdk;
import ru.profit_group.scorocode_sdk.scorocode_objects.User;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.btnRegister) Button btnRegister;

    private CallbackLoginUser callbackLoginUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ScorocodeSdk.initWith(
                getString(R.string.appKey),
                getString(R.string.clientKey),
                getString(R.string.masterKey),
                null, null, null, null
        );
        ButterKnife.bind(this);

        callbackLoginUser = new CallbackLoginUser() {
            @Override
            public void onLoginSucceed(ResponseLogin responseLogin) {
                ListActivity.display(LoginActivity.this);
            }

            @Override
            public void onLoginFailed(String errorCode, String errorMessage) {
                Toast.makeText(LoginActivity.this, getString(R.string.cant_login) + "\n" + errorMessage, Toast.LENGTH_SHORT).show();
            }
        };

    }

    @OnClick(R.id.btnLogin)
    public void onLoginButtonClicked(View loginButton) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()) {
            User user = new User();
            user.login(email, password, callbackLoginUser);
        } else {
            callbackLoginUser.onLoginFailed("", getString(R.string.wrong_data_error));
        }
    }

    @OnClick(R.id.btnRegister)
    public void onRegisterButtonClicked(View loginButton) {
        RegisterActivity.display(this);
    }
}
