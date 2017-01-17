package com.peterstaranchuk.scorocodetemplate;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Peter Staranchuk.
 */

public class InputHelper {

    public static void checkForEmptyEnter(EditText viewForCheck, Action1<CharSequence> callbackAction) {
        RxTextView.textChanges(viewForCheck)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callbackAction);
    }

    public static boolean isNotEmpty(EditText editText) {
        if(!editText.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    public static void enableButton(Button button) {
        //we extract this method for case if we will need to
        //change behaviour or appearance of all disabled buttons in app
        button.setEnabled(true);
    }

    public static void disableButton(Button button) {
        //we extract this method for case if we will need to
        //change behaviour or appearance of all disabled buttons in app
        button.setEnabled(false);
    }

    @NonNull
    public static String getStringFrom(EditText editText) {
        if(editText != null) {
            return editText.getText().toString();
        } else {
            return "";
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        if(activity.getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
