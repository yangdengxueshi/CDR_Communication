package com.dexin.cdr_communication.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.dexin.cdr_communication.application.AppConfig;

/**
 * BaseActivity
 */
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, BaseActivity.class);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    View view = getCurrentFocus();
                    if (AppConfig.isShouldHideKeyboard(view, ev)) {
                        InputMethodManager lInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (lInputMethodManager != null) {
                            lInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                    break;
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.dispatchTouchEvent(ev);
    }
}
