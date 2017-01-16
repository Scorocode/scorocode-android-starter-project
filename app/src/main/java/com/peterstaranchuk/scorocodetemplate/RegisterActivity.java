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

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackRegisterUser;
import ru.profit_group.scorocode_sdk.scorocode_objects.User;
import rx.android.schedulers.AndroidSchedulers;
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

        checkForEmptyEnter(etUserName);
        checkForEmptyEnter(etEmail);
        checkForEmptyEnter(etPassword);
        checkForEmptyEnter(etRepeatPassword);
    }

    private void setInitialScreenState() {
        disableRegisterButton();
        btnLogin.setVisibility(View.GONE);
        llRepeatPassword.setVisibility(View.VISIBLE);
        llUserName.setVisibility(View.VISIBLE);
    }

    private void checkForEmptyEnter(EditText viewForCheck) {
        RxTextView.textChanges(viewForCheck)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence userName) {
                        if(isAllFieldsValid()) {
                            enableRegisterButton();
                        } else {
                            if(isNotEmpty(etPassword) && isNotEmpty(etRepeatPassword) && !isPasswordsMatch()) {
                                Toast.makeText(RegisterActivity.this, R.string.notMatchPasswords, Toast.LENGTH_SHORT).show();
                            }
                            disableRegisterButton();
                        }
                    }
                });
    }

    @OnClick(R.id.btnRegister)
    public void onBtnRegisterClicked(View registerButton) {
        String userName = etUserName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        new User().register(userName, email, password, new CallbackRegisterUser() {
            @Override
            public void onRegisterSucceed() {
                finish();
            }

            @Override
            public void onRegisterFailed(String errorCode, String errorMessage) {
                Toast.makeText(RegisterActivity.this, R.string.errorDuringRegister, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isAllFieldsValid() {
        if(isNotEmpty(etUserName) && isNotEmpty(etEmail) && isNotEmpty(etPassword) && isNotEmpty(etRepeatPassword) && isPasswordsMatch()) {
            return true;
        }
        return false;
    }

    private boolean isNotEmpty(EditText editText) {
        if(!editText.getText().toString().isEmpty()) {
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

    private void enableRegisterButton() {
        btnRegister.setEnabled(true);
    }

    private void disableRegisterButton() {
        btnRegister.setEnabled(false);
    }

    public static void display(Context context) {
        context.startActivity(new Intent(context, RegisterActivity.class));
    }
}
