package com.peterstaranchuk.scorocodetemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackRegisterUser;
import ru.profit_group.scorocode_sdk.scorocode_objects.User;
import rx.functions.Action1;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.etUserName) EditText etUserName;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.etRepeatPassword) EditText etRepeatPassword;
    @BindView(R.id.btnRegister) Button btnRegister;
    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.llRepeatPassword) LinearLayout llRepeatPassword;
    @BindView(R.id.llUserName) LinearLayout llUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setInitialScreenState();

        Action1<CharSequence> action = userName -> {
            if(isAllFieldsValid()) {
                InputHelper.enableButton(btnRegister);
            } else {
                if(InputHelper.isNotEmpty(etPassword) && InputHelper.isNotEmpty(etRepeatPassword) && !isPasswordsMatch()) {
                    Toast.makeText(RegisterActivity.this, R.string.notMatchPasswords, Toast.LENGTH_SHORT).show();
                }
                InputHelper.disableButton(btnRegister);
            }
        };

        InputHelper.checkForEmptyEnter(etUserName, action);
        InputHelper.checkForEmptyEnter(etEmail, action);
        InputHelper.checkForEmptyEnter(etPassword, action);
        InputHelper.checkForEmptyEnter(etRepeatPassword, action);
    }

    private void setInitialScreenState() {
        InputHelper.disableButton(btnRegister);
        btnLogin.setVisibility(View.GONE);
        llRepeatPassword.setVisibility(View.VISIBLE);
        llUserName.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btnRegister)
    public void onBtnRegisterClicked(View registerButton) {
        //To register new user (add it's info in users collection) you should
        //1.Create new object of User class
        User user = new User();

        //2. Prepare information for user registration (it's userName, email and password)
        String userName = etUserName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        //3. Use register() method of User class with userName, email, password and callback
        //as a parameters
        user.register(userName, email, password, new CallbackRegisterUser() {
            @Override
            public void onRegisterSucceed() {
                //if all info's format correct and there is no any user with this
                // email in server (inside users collection)
                //new user with this data will be created
                finish();
            }

            @Override
            public void onRegisterFailed(String errorCode, String errorMessage) {
                //if user registration failed you can handle this case.
                // You can also see the reason why registration failed (code and message of error).
                Toast.makeText(RegisterActivity.this, R.string.errorDuringRegister, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isAllFieldsValid() {
        if(InputHelper.isNotEmpty(etUserName) && InputHelper.isNotEmpty(etEmail) && InputHelper.isNotEmpty(etPassword) && InputHelper.isNotEmpty(etRepeatPassword) && isPasswordsMatch()) {
            return true;
        }
        return false;
    }

    private boolean isPasswordsMatch() {
        if(etPassword.getText().toString().equals(etRepeatPassword.getText().toString())) {
            return true;
        }
        return false;
    }

    public static void display(Context context) {
        context.startActivity(new Intent(context, RegisterActivity.class));
    }
}
