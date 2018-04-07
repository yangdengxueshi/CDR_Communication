package com.dexin.cdr_communication.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.dexin.cdr_communication.application.AppConfig;
import com.dexin.cdr_communication.application.CustomApplication;
import com.vondear.rxtools.RxNetTool;
import com.vondear.rxtools.view.RxToast;

import java.text.MessageFormat;

/**
 * BaseFragment
 */
public class BaseFragment extends Fragment {
    protected final String TAG = MessageFormat.format("TAG_{0}", getClass().getSimpleName());

    @NonNull
    public static BaseFragment newInstance() {
        return new BaseFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (AppConfig.IS_TRUE_ENVIRONMENT && !RxNetTool.isWifiConnected(CustomApplication.getContext())) RxToast.warning(AppConfig.TOAST_PLEASE_CONNECT_TO_CDR_WIFI_FIRST);
        super.onActivityCreated(savedInstanceState);
    }
}
