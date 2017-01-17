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
import rx.functions.Action1;

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

        //before you start to use any method of ScorocodeSdk you
        //should init SDK first.
        //For this use your keys which you can find in settings of
        //your application in scorocode website.
        //many keys are nullable
        //make sure you init SDK only once
        //reinitialization will change sessionId
        //for more information see scorocode documentation
        ScorocodeSdk.initWith(
                getString(R.string.appKey),
                getString(R.string.clientKey),
                getString(R.string.masterKey),
                null, null, null, null
        );
        ButterKnife.bind(this);
        initScreenState();

        //here we define login callback
        //we will use it callback in login() method
        //to handle login result
        callbackLoginUser = new CallbackLoginUser() {
            @Override
            public void onLoginSucceed(ResponseLogin responseLogin) {
                //if user account exist in server (inside users collection)
                //when login will be successful
                ListActivity.display(LoginActivity.this);
            }

            @Override
            public void onLoginFailed(String errorCode, String errorMessage) {
                //if login failed you can handle this situation. You can also see the reason
                //why login operation was failed
                Toast.makeText(LoginActivity.this, getString(R.string.cant_login) + "\n" + errorMessage, Toast.LENGTH_SHORT).show();
            }
        };

    }

    private void initScreenState() {
        InputHelper.disableButton(btnLogin);
        Action1<CharSequence> action = new Action1<CharSequence>() {
            @Override
            public void call(CharSequence charSequence) {
                if(InputHelper.isNotEmpty(etEmail) && InputHelper.isNotEmpty(etPassword)) {
                    InputHelper.enableButton(btnLogin);
                } else {
                    InputHelper.disableButton(btnLogin);
                }
            }
        };

        InputHelper.checkForEmptyEnter(etEmail, action);
        InputHelper.checkForEmptyEnter(etPassword, action);
    }

    @OnClick(R.id.btnLogin)
    public void onLoginButtonClicked(View loginButton) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()) {
            //To get if user exist in server (inside users collection) you should:
            //1. Create new user object
            User user = new User();
            //2. Use login() method of user class
            //with email, password and callback (which we have defined previously)
            //as a method's parameters
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
