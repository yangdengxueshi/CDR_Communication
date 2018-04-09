package com.dexin.cdr_communication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.dexin.cdr_communication.R;
import com.dexin.cdr_communication.activity.MainActivity;
import com.dexin.cdr_communication.application.AppConfig;
import com.dexin.cdr_communication.application.CustomApplication;
import com.dexin.cdr_communication.utility.ApplicationUtility;
import com.orhanobut.logger.Logger;
import com.vondear.rxtools.RxNetTool;
import com.vondear.rxtools.view.RxToast;

import java.text.MessageFormat;
import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * "参数设置"Fragment
 */
public class ConfigParamFragment extends BaseFragment {
    private Unbinder unbinder;
    @BindView(R.id.actv_radio_freq)
    AutoCompleteTextView mActvRadioFreq;//射频频率
    @BindView(R.id.s_transmission_mode)
    Spinner mSTransmissionMode;//传输模式
    @BindView(R.id.s_spectrum_mode)
    Spinner mSSpectrumMode;//频谱模式
    @BindString(R.string.data_area_error)
    String mDataAreaErrorStr;
    @BindString(R.string.transmission_mode)
    String mTransmissionModeStr;
    @BindString(R.string.spectrum_mode)
    String mSpectrumModeStr;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config_param, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) Objects.requireNonNull(getActivity())).setToolbarTitle(R.string.config_param);

        mActvRadioFreq.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_RADIO_FREQ, "101"));
        mSTransmissionMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//传输模式选择框
            private boolean isFirstIn = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                view.setVisibility((position == 0) ? View.GONE : View.VISIBLE);
                mSTransmissionMode.setSelected(position != 0);
                if ((position != 0) && !isFirstIn) {
                    RxToast.info(MessageFormat.format("{0}{1}", mTransmissionModeStr, mSTransmissionMode.getSelectedItem()));
                }
                isFirstIn = false;
                inspectDataTyped();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        setSpinnerItemSelectedByValue(mSTransmissionMode, ApplicationUtility.getSPUtils().getString(AppConfig.KEY_TRANSMISSION_MODE, "1"));
        mSSpectrumMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//频谱模式选择框
            private boolean isFirstIn = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                view.setVisibility((position == 0) ? View.GONE : View.VISIBLE);
                mSSpectrumMode.setSelected(position != 0);
                if ((position != 0) && !isFirstIn) {
                    RxToast.info(MessageFormat.format("{0}{1}", mSpectrumModeStr, mSSpectrumMode.getSelectedItem()));
                }
                isFirstIn = false;
                inspectDataTyped();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        setSpinnerItemSelectedByValue(mSSpectrumMode, ApplicationUtility.getSPUtils().getString(AppConfig.KEY_SPECTRUM_MODE, "1"));
    }

    @Override
    public void onDestroyView() {
        ((MainActivity) Objects.requireNonNull(getActivity())).setToolbarTitle(R.string.app_name);
        unbinder.unbind();
        super.onDestroyView();
    }

    @NonNull
    public static ConfigParamFragment newInstance() {
        return new ConfigParamFragment();
    }

    void inspectDataTyped() {
        String lRadioFreq = mActvRadioFreq.getText().toString();
        if (!TextUtils.isEmpty(lRadioFreq) && ((Float.valueOf(lRadioFreq) < 50.0) || (Float.valueOf(lRadioFreq) > 180.0))) {
            mActvRadioFreq.setError(mDataAreaErrorStr);
        }
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------------TODO 生命周期模块---------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * 根据值设置Spinner默认选中项
     *
     * @param spinner Spinner对象
     * @param value   值
     */
    private static void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter lSpinnerAdapter = spinner.getAdapter();//得到SpinnerAdapter对象
        int lCount = lSpinnerAdapter.getCount();
        for (int i = 0; i < lCount; i++) {
            if (Objects.equals(lSpinnerAdapter.getItem(i).toString(), value)) {
                spinner.setSelection(i);// 默认选中项
                break;
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------------TODO 发送命令模块---------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    @BindString(R.string.please_select)
    String mPleaseSelectStr;
    private final Intent mSendConfigParamIntent = new Intent(AppConfig.ACTION_SEND_CONFIG_PARAM);

    @OnClick(R.id.btn_send_msg)
    public void onViewClicked() {
        try {
            if (AppConfig.IS_TRUE_ENVIRONMENT && !RxNetTool.isWifiConnected(CustomApplication.getContext())) {
                RxToast.warning(AppConfig.TOAST_PLEASE_CONNECT_TO_CDR_WIFI_FIRST);
                return;
            }
            String lRadioFreq = mActvRadioFreq.getText().toString();
            if (!TextUtils.isEmpty(lRadioFreq) && ((Float.valueOf(lRadioFreq) < 50.0) || (Float.valueOf(lRadioFreq) > 180.0))) {
                RxToast.info("请输入 正确范围 的\n射频频率 (50.0~180.0)");
                return;
            }
            String lJointRadioFreq = TextUtils.isEmpty(lRadioFreq) ? "" : MessageFormat.format("&RF={0}", lRadioFreq);

            String lTransmissionMode = (Objects.equals(mSTransmissionMode.getSelectedItem().toString(), mPleaseSelectStr) ? "" : mSTransmissionMode.getSelectedItem().toString());
            String lJointTransmissionMode = TextUtils.isEmpty(lTransmissionMode) ? "" : MessageFormat.format("&trans_mode={0}", lTransmissionMode);

            String lSpectrumMode = (Objects.equals(mSSpectrumMode.getSelectedItem().toString(), mPleaseSelectStr) ? "" : mSSpectrumMode.getSelectedItem().toString());
            String lJointSpectrumMode = TextUtils.isEmpty(lSpectrumMode) ? "" : MessageFormat.format("&freq_mode={0}", lSpectrumMode);

            String lJointConfigParamsStr = MessageFormat.format("{0}{1}{2}\n", lJointRadioFreq, lJointTransmissionMode, lJointSpectrumMode).substring(1);
            mSendConfigParamIntent.putExtra(AppConfig.KEY_CONFIG_PARAM, lJointConfigParamsStr);
            AppConfig.LOCAL_BROADCAST_MANAGER.sendBroadcast(mSendConfigParamIntent);
            RxToast.info("命令已发送");

            ApplicationUtility.getSPUtils().put(AppConfig.KEY_RADIO_FREQ, lRadioFreq);
            ApplicationUtility.getSPUtils().put(AppConfig.KEY_TRANSMISSION_MODE, lTransmissionMode);
            ApplicationUtility.getSPUtils().put(AppConfig.KEY_SPECTRUM_MODE, lSpectrumMode);
        } catch (Exception e) {
            Logger.t(TAG).e(e, "onViewClicked: ");
        }
    }
}
