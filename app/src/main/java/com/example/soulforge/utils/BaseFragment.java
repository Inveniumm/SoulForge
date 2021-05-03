package com.example.soulforge.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.example.soulforge.MyApplication;
import com.google.gson.Gson;

public class BaseFragment extends Fragment {
    public Activity mActivity=getActivity();
    public View view;
    public MyApplication app;
    public SingletonClass singleton;
    public Gson gson;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        gson = new Gson();
        app = (MyApplication) getActivity().getApplication();
        singleton = SingletonClass.getInstance();
        singleton.setCurrentActivity(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
