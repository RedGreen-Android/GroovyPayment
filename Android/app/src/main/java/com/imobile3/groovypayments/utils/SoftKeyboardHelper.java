package com.imobile3.groovypayments.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.imobile3.groovypayments.logging.LogHelper;

import androidx.annotation.Nullable;

import java.util.logging.Level;

public class SoftKeyboardHelper {

    private static final String TAG = SoftKeyboardHelper.class.getSimpleName();

    private SoftKeyboardHelper() {
    }

    /**
     * Requests to hide the soft input window from the context of the window
     * that is currently accepting input.
     */
    public static void hide(@Nullable Activity activity) {
        if (activity == null) {
            LogHelper.writeWithTrace(Level.WARNING, TAG, "Activity is null!");
            return;
        }

        InputMethodManager mgr = (InputMethodManager)activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mgr != null) {
            mgr.hideSoftInputFromWindow(activity
                    .getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
