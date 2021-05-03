package com.example.soulforge.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.soulforge.R;
import com.example.soulforge.fragments.CreateAccountFragment;
import com.example.soulforge.fragments.LoginFragment;
import com.example.soulforge.interfaces.OnServerResCallBack;
import com.example.soulforge.utils.BaseActivity;
import com.example.soulforge.utils.Constants;
import com.example.soulforge.utils.PrefManager;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;

import static com.example.soulforge.utils.Constants.notificationToken;

public class ReplacerActivity extends BaseActivity {
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_replacer);

        init();
    }

    private void init() {
        frameLayout = findViewById(R.id.frameLayout);

        setFragment(new LoginFragment());
        getDeviceToken();
    }

    private void getDeviceToken() {
        notificationToken = PrefManager.getMyStringPref(mAct, Constants.DEVICE_TOKEN, "");
        if (notificationToken.equals("")) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(mAct, instanceIdResult -> {
                notificationToken = instanceIdResult.getToken();
                PrefManager.setMyStringPref(mAct, Constants.DEVICE_TOKEN, notificationToken);
            });
        }
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        if (fragment instanceof CreateAccountFragment) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void makeStartUpCall() {
        singleton.showProgress("Please wait...");
        Call<Object> call = singleton.getApiInterface().postCall("system/get-basic-start-up-values");
        OnServerResCallBack callBack = (status, message, responseStr) -> {
            if (status == singleton.SUCCESS) {
                String tokenStr = singleton.getValueStrFromJSON(responseStr, "return_token");
            } else {
                singleton.dismissProgress();
            }
        };

        singleton.makePostCall(call, callBack);
    }
}