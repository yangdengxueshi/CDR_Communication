package com.dexin.cdr_communication.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dexin.cdr_communication.R;
import com.dexin.cdr_communication.activity.MainActivity;
import com.dexin.cdr_communication.adapter.OperateModuleAdapter;
import com.dexin.cdr_communication.application.AppConfig;
import com.dexin.cdr_communication.application.CustomApplication;
import com.dexin.cdr_communication.entity.OperateModuleBean;
import com.dexin.cdr_communication.utility.ApplicationUtility;
import com.dexin.cdr_communication.utility.CalendarDateTimeUtility;
import com.vondear.rxtools.RxRegTool;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment的一个简单子类
 * 使用这个Fragment的 {@link MainFragment#newInstance} 静态工厂方法 来创建Fragment的实例
 */
public class MainFragment extends BaseFragment {
    Unbinder unbinder;

    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------TODO 生命周期方法----------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ApplicationUtility.getSPUtils().put(AppConfig.KEY_MAIN_FRAGMENT_VISIBILITY, true);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) Objects.requireNonNull(getActivity())).setToolbarTitle(R.string.app_name);
        {//重新绘制menu
            ApplicationUtility.getSPUtils().put(AppConfig.KEY_CONSTELLATION_DIAGRAM_TYPE_MENU_VISIABLE, false);
            getActivity().invalidateOptionsMenu();
        }

        initAdBannerInOnActivityCreated();
        startAdBannerInOnActivityCreated();

        initOperateModuleInOnCreate();

        loadCacheValueInOnActivityCreated();
        initLocalReceiverInOnActivityCreated();
    }

    @Override
    public void onStop() {
        pauseAdBannerInOnStop();
        releaseLocalReceiverResourceInOnStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        ApplicationUtility.getSPUtils().put(AppConfig.KEY_MAIN_FRAGMENT_VISIBILITY, false);
        unbinder.unbind();
        super.onDestroyView();
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * 用本静态工厂方法及其提供的参数去创建一个Fragment的实例
     *
     * @return Fragment的一个实例.
     */
    @NonNull
    public static MainFragment newInstance() {
        return new MainFragment();
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------TODO 设置Banner --------------------------------------------------------------------
    //---------------------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓--------------------------------------------------------------------
    @BindView(R.id.mzbv_ad_banner)
    MZBannerView mMzbvAdBanner;//因为在 onDestroyView 中 有 unBind 操作,所以不再处理释放 MZBannerView 资源

    private void initAdBannerInOnActivityCreated() {
        List<Integer> lAdImgIdList = new ArrayList<>();
        lAdImgIdList.add(R.drawable.banner_ad_1);
        lAdImgIdList.add(R.drawable.banner_ad_2);
        lAdImgIdList.add(R.drawable.banner_ad_3);
        mMzbvAdBanner.setPages(lAdImgIdList, ADBannerViewHolder::new);
    }

    private void pauseAdBannerInOnStop() {
        mMzbvAdBanner.pause();//暂停轮播
    }

    private void startAdBannerInOnActivityCreated() {
        mMzbvAdBanner.start();//开始轮播
    }

    private static class ADBannerViewHolder implements MZViewHolder<Integer> {
        private ImageView mImageView;

        @Override   //FIXME 返回页面布局
        public View createView(Context context) {
            mImageView = new ImageView(CustomApplication.getContext());
            mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return mImageView;
        }

        @Override   //FIXME 绑定数据
        public void onBind(Context context, int position, Integer resId) {
            Glide.with(context).load(resId).diskCacheStrategy(DiskCacheStrategy.RESULT).skipMemoryCache(true).into(mImageView);
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------TODO 操作模块 ----------------------------------------------------------------------
    //---------------------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓--------------------------------------------------------------------
    @BindView(R.id.rv_operation_module)
    RecyclerView mRvOperationModule;//因为在 onDestroyView 中 有 unBind 操作,所以不再处理释放 RecyclerView 资源
    @BindString(R.string.config_param)
    String mConfigParamStr;
    @BindString(R.string.constellation_diagram)
    String mConstellationDiagram;

    private void initOperateModuleInOnCreate() {
        List<OperateModuleBean> lOperateModuleBeanList = new ArrayList<>();
        lOperateModuleBeanList.add(new OperateModuleBean(R.drawable.icon_config_param, mConfigParamStr));
        lOperateModuleBeanList.add(new OperateModuleBean(R.drawable.icon_constellation_diagram, mConstellationDiagram));
        OperateModuleAdapter lOperateModuleAdapter = new OperateModuleAdapter(lOperateModuleBeanList);
        lOperateModuleAdapter.setOnItemClickListener((itemView, position) -> {
            switch (position) {
                case 0://FIXME 参数设置
                    ((MainActivity) Objects.requireNonNull(getActivity())).loadFragment(ConfigParamFragment.newInstance(), true);
                    break;
                case 1:
                    ((MainActivity) Objects.requireNonNull(getActivity())).loadFragment(ConstellationDiagramFragment.newInstance(), true);
                {//重新绘制menu
                    ApplicationUtility.getSPUtils().put(AppConfig.KEY_CONSTELLATION_DIAGRAM_TYPE_MENU_VISIABLE, true);
                    getActivity().invalidateOptionsMenu();
                }
                break;
                default:
            }
        });
        mRvOperationModule.setLayoutManager(new GridLayoutManager(CustomApplication.getContext(), 1, LinearLayoutManager.HORIZONTAL, false));
        mRvOperationModule.setAdapter(lOperateModuleAdapter);
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------TODO 本地广播模块--------------------------------------------------------------------
    //---------------------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓--------------------------------------------------------------------
    @BindView(R.id.tv_receive)
    TextView mTvReceive;
    @BindView(R.id.rb_sync_status)
    RadioButton mRbSyncStatus;
    @BindView(R.id.tv_radio_power)
    TextView mTvRadioPower;
    @BindView(R.id.tv_cnr_value)
    TextView mTvCnrValue;
    @BindView(R.id.tv_mer_value)
    TextView mTvMerValue;
    @BindView(R.id.tv_freq_offset_estimation_value)
    TextView mTvFreqOffsetEstimationValue;
    @BindView(R.id.tv_clock_deviation_value)
    TextView mTvClockDeviationValue;
    @BindView(R.id.tv_service_data_modulation_mode)
    TextView mTvServiceDataModulationMode;
    @BindView(R.id.tv_ber_value)
    TextView mTvBerValue;
    @BindView(R.id.tv_subf_mode)
    TextView mTvSubfMode;
    @BindView(R.id.tv_info_modulation_mode)
    TextView mTvInfoModulationMode;
    @BindView(R.id.tv_ldpc_code_rate)
    TextView mTvLdpcCodeRate;

    private void loadCacheValueInOnActivityCreated() {
        mRbSyncStatus.setChecked(ApplicationUtility.getSPUtils().getBoolean(AppConfig.KEY_SYNC_STATE_VALUE));
        mTvRadioPower.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_RADIO_FREQ_VALUE));
        mTvCnrValue.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_CNR_VALUE));
        mTvMerValue.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_MER_VALUE));
        mTvFreqOffsetEstimationValue.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_FREQ_OFFSET_VALUE));
        mTvClockDeviationValue.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_CLK_OFFSET_VALUE));
        mTvServiceDataModulationMode.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_MSC_QAM_VALUE));
        mTvBerValue.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_BER_VALUE));
        mTvSubfMode.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_SUBF_MODE_VALUE));
        mTvInfoModulationMode.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_CIC_QAM_VALUE));
        mTvLdpcCodeRate.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_LDPC_CR_VALUE));
    }

    private LocalReceiver mLocalReceiver;
    private IntentFilter mIntentFilter;

    private void initLocalReceiverInOnActivityCreated() {
        {
            mTvReceive.setMovementMethod(new ScrollingMovementMethod());
            mTvRadioPower.setSelected(true);
            mTvCnrValue.setSelected(true);
            mTvMerValue.setSelected(true);
            mTvFreqOffsetEstimationValue.setSelected(true);
            mTvClockDeviationValue.setSelected(true);
            mTvServiceDataModulationMode.setSelected(true);
            mTvBerValue.setSelected(true);
            mTvSubfMode.setSelected(true);
            mTvInfoModulationMode.setSelected(true);
            mTvLdpcCodeRate.setSelected(true);
        }

        mLocalReceiver = new LocalReceiver();
        mIntentFilter = new IntentFilter(AppConfig.ACTION_SHOW_RECEIVED_PARAM);
        AppConfig.LOCAL_BROADCAST_MANAGER.registerReceiver(mLocalReceiver, mIntentFilter);
    }

    private void releaseLocalReceiverResourceInOnStop() {
        AppConfig.LOCAL_BROADCAST_MANAGER.unregisterReceiver(mLocalReceiver);
        mLocalReceiver = null;
        mIntentFilter = null;
    }

    private class LocalReceiver extends BroadcastReceiver {
        private String mLastParamReceiveStr;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            switch (Objects.requireNonNull(intent.getAction())) {
                case AppConfig.ACTION_SHOW_RECEIVED_PARAM://TODO 显示接收到的数据
                    mTvReceive.setText(MessageFormat.format("{0}\n{1}", CalendarDateTimeUtility.getSpecialDateFormat(new Date(), 0, "yyyy年MM月dd日 HH:mm:ss "), intent.getStringExtra(AppConfig.KEY_RECEIVED_DATA)));

                    String paramReceivedStr = intent.getStringExtra(AppConfig.KEY_RECEIVED_DATA);
                    if (TextUtils.isEmpty(paramReceivedStr) || !paramReceivedStr.contains("=") || Objects.equals(mLastParamReceiveStr, paramReceivedStr)) break;
                    mLastParamReceiveStr = paramReceivedStr;//更新收到的参数字符串
                    String[] lKeyValueStrArray = paramReceivedStr.split("&");
                    if (lKeyValueStrArray.length > 0) {
                        for (String lKeyValueStr : lKeyValueStrArray) {
                            if (lKeyValueStr.contains("=") && (!lKeyValueStr.startsWith("=") && !lKeyValueStr.endsWith("="))) {
                                String[] lKeyValueGroup = lKeyValueStr.split("=");
                                switch (lKeyValueGroup[0]) {
                                    case "syn_state":
                                        boolean synStateValue = Objects.equals(lKeyValueGroup[1], "1");
                                        if (synStateValue != ApplicationUtility.getSPUtils().getBoolean(AppConfig.KEY_SYNC_STATE_VALUE)) {
                                            mRbSyncStatus.setChecked(synStateValue);
                                            ApplicationUtility.getSPUtils().put(AppConfig.KEY_SYNC_STATE_VALUE, synStateValue);
                                        }
                                        break;
                                    case "rf_power":
                                        String rfPowerValue = AppConfig.rfPowerPlusTen(lKeyValueGroup[1]);
                                        if (!Objects.equals(rfPowerValue, ApplicationUtility.getSPUtils().getString(AppConfig.KEY_RADIO_FREQ_VALUE))) {
                                            mTvRadioPower.setText(rfPowerValue);
                                            ApplicationUtility.getSPUtils().put(AppConfig.KEY_RADIO_FREQ_VALUE, rfPowerValue);
                                        }
                                        break;
                                    case "cnr":
                                        String cnrValue = lKeyValueGroup[1];
                                        if (!Objects.equals(cnrValue, ApplicationUtility.getSPUtils().getString(AppConfig.KEY_CNR_VALUE))) {
                                            mTvCnrValue.setText(cnrValue);
                                            ApplicationUtility.getSPUtils().put(AppConfig.KEY_CNR_VALUE, cnrValue);
                                        }
                                        break;
                                    case "mer":
                                        String merValue = lKeyValueGroup[1];
                                        if (!Objects.equals(merValue, ApplicationUtility.getSPUtils().getString(AppConfig.KEY_MER_VALUE))) {
                                            mTvMerValue.setText(merValue);
                                            ApplicationUtility.getSPUtils().put(AppConfig.KEY_MER_VALUE, merValue);
                                        }
                                        break;
                                    case "freq_offset":


                                        if (RxRegTool.isMatch("^(-?\\d+)(\\.\\d+)?$", lKeyValueGroup[1])) {
                                            String freqOffsetValue = String.valueOf(Float.parseFloat(lKeyValueGroup[1]) / 1000);
                                            mTvFreqOffsetEstimationValue.setText(freqOffsetValue);
                                            ApplicationUtility.getSPUtils().put(AppConfig.KEY_FREQ_OFFSET_VALUE, freqOffsetValue);
                                        } else {
                                            mTvFreqOffsetEstimationValue.setText(lKeyValueGroup[1]);
                                            ApplicationUtility.getSPUtils().put(AppConfig.KEY_FREQ_OFFSET_VALUE, lKeyValueGroup[1]);
                                        }
                                        break;
                                    case "clk_offset":
                                        String clkOffsetValue = lKeyValueGroup[1];
                                        if (!Objects.equals(clkOffsetValue, ApplicationUtility.getSPUtils().getString(AppConfig.KEY_CLK_OFFSET_VALUE))) {
                                            mTvClockDeviationValue.setText(clkOffsetValue);
                                            ApplicationUtility.getSPUtils().put(AppConfig.KEY_CLK_OFFSET_VALUE, clkOffsetValue);
                                        }
                                        break;
                                    case "msc_qam":
                                        String mscQamValue = lKeyValueGroup[1];
                                        switch (mscQamValue) {
                                            case "1":
                                                mscQamValue = " 4-QAM";
                                                break;
                                            case "2":
                                                mscQamValue = "16-QAM";
                                                break;
                                            case "3":
                                                mscQamValue = "64-QAM";
                                                break;
                                            default:
                                                mscQamValue = " 4-QAM";
                                        }
                                        if (!Objects.equals(mscQamValue, ApplicationUtility.getSPUtils().getString(AppConfig.KEY_MSC_QAM_VALUE, " 4-QAM"))) {
                                            mTvServiceDataModulationMode.setText(mscQamValue);
                                            ApplicationUtility.getSPUtils().put(AppConfig.KEY_MSC_QAM_VALUE, mscQamValue);
                                        }
                                        break;
                                    case "cic_qam":
                                        String cicQamValue = lKeyValueGroup[1];
                                        switch (cicQamValue) {
                                            case "1":
                                                cicQamValue = " 4-QAM";
                                                break;
                                            case "2":
                                                cicQamValue = "16-QAM";
                                                break;
                                            case "3":
                                                cicQamValue = "64-QAM";
                                                break;
                                            default:
                                        }
                                        if (!Objects.equals(cicQamValue, ApplicationUtility.getSPUtils().getString(AppConfig.KEY_CIC_QAM_VALUE))) {
                                            mTvInfoModulationMode.setText(cicQamValue);
                                            ApplicationUtility.getSPUtils().put(AppConfig.KEY_CIC_QAM_VALUE, cicQamValue);
                                        }
                                        break;
                                    case "ldpc_cr":
                                        String ldpcCrValue = lKeyValueGroup[1];
                                        switch (ldpcCrValue) {
                                            case "1":
                                                ldpcCrValue = "1/4";
                                                break;
                                            case "2":
                                                ldpcCrValue = "1/3";
                                                break;
                                            case "3":
                                                ldpcCrValue = "1/2";
                                                break;
                                            case "4":
                                                ldpcCrValue = "3/4";
                                                break;
                                            default:
                                        }
                                        if (!Objects.equals(ldpcCrValue, ApplicationUtility.getSPUtils().getString(AppConfig.KEY_LDPC_CR_VALUE))) {
                                            mTvLdpcCodeRate.setText(ldpcCrValue);
                                            ApplicationUtility.getSPUtils().put(AppConfig.KEY_LDPC_CR_VALUE, ldpcCrValue);
                                        }
                                        break;
                                    case "BER":
                                        String BERValue = AppConfig.strToScientificNotation(lKeyValueGroup[1]);
                                        if (!Objects.equals(BERValue, ApplicationUtility.getSPUtils().getString(AppConfig.KEY_BER_VALUE))) {
                                            mTvBerValue.setText(BERValue);
                                            ApplicationUtility.getSPUtils().put(AppConfig.KEY_BER_VALUE, BERValue);
                                        }
                                        break;
                                    case "subf_mode"://子帧分配方式
                                        String subfModeValue = lKeyValueGroup[1];
                                        if (!Objects.equals(subfModeValue, ApplicationUtility.getSPUtils().getString(AppConfig.KEY_SUBF_MODE_VALUE))) {
                                            mTvSubfMode.setText(subfModeValue);
                                            ApplicationUtility.getSPUtils().put(AppConfig.KEY_SUBF_MODE_VALUE, subfModeValue);
                                        }
                                        break;
                                    default:
                                }
                            }
                        }
                    }
                    break;
                default:
            }
        }
    }
}
