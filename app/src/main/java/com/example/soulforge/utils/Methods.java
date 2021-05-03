package com.example.soulforge.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import com.example.soulforge.R;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Methods extends Constants {
    public Activity mActivity;
    private KProgressHUD hud;

    public void setCurrentActivity(Activity mAct) {
        this.mActivity = mAct;
    }

    public void startActivity(Class aClass, int status) {
        if (status == START_ACTIVITY) {
            mActivity.startActivity(new Intent(mActivity, aClass));
            mActivity.overridePendingTransition(R.anim.trans_left_in,
                    R.anim.trans_left_out);
        } else if (status == START_ACTIVITY_WITH_FINISH) {
            mActivity.startActivity(new Intent(mActivity, aClass));
            mActivity.overridePendingTransition(R.anim.trans_left_in,
                    R.anim.trans_left_out);
            mActivity.finish();
        } else if (status == START_ACTIVITY_WITH_CLEAR_BACK_STACK) {
            mActivity.startActivity(new Intent(mActivity, aClass).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            mActivity.overridePendingTransition(R.anim.trans_left_in,
                    R.anim.trans_left_out);
        } else if (status == START_ACTIVITY_WITH_TOP) {
            mActivity.startActivity(new Intent(mActivity, aClass).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            mActivity.overridePendingTransition(R.anim.trans_left_in,
                    R.anim.trans_left_out);
        }
    }

    public void startActivityWithDataBundle(Class aClass, Bundle bundle, int status) {
        if (status == 0) {
            mActivity.startActivity(new Intent(mActivity, aClass).putExtras(bundle));
            mActivity.overridePendingTransition(R.anim.trans_left_in,
                    R.anim.trans_left_out);
        } else if (status == 1) {
            mActivity.startActivity(new Intent(mActivity, aClass).putExtras(bundle));
            mActivity.overridePendingTransition(R.anim.trans_left_in,
                    R.anim.trans_left_out);
            mActivity.finish();
        } else if (status == 2) {
            mActivity.startActivity(new Intent(mActivity, aClass).putExtras(bundle)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            mActivity.overridePendingTransition(R.anim.trans_left_in,
                    R.anim.trans_left_out);
        } else if (status == 3) {
            mActivity.startActivity(new Intent(mActivity, aClass).putExtras(bundle).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            mActivity.overridePendingTransition(R.anim.trans_left_in,
                    R.anim.trans_left_out);
        }
    }

    public void finishActivity() {
        mActivity.finish();
        mActivity.overridePendingTransition(R.anim.trans_right_in,
                R.anim.trans_right_out);
    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
            if (imm == null) throw new AssertionError("assertion Error! Imm is null");
            imm.hideSoftInputFromWindow(Objects.requireNonNull(mActivity.getCurrentFocus()).getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }

    public boolean isValidEmail(String email) {
        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public boolean isEmptyField(String str) {
        if (TextUtils.isEmpty(str)) return false;
        return true;
    }

    public boolean isValidLength(String str, int length) {
        if (str.length() < length) return false;
        return true;
    }

    public void showProgress(String titleStr) {
        hud = KProgressHUD.create(mActivity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(titleStr)
                .setDimAmount(0.5f)
                .show();
    }

    public void showProgress(Context mContext, String title, String detailStr) {
        hud = KProgressHUD.create(mContext)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setDetailsLabel(detailStr)
                .setDimAmount(0.5f)
                .show();
    }

    public void dismissProgress() {
        if (hud != null && hud.isShowing())
            hud.dismiss();
    }
}
