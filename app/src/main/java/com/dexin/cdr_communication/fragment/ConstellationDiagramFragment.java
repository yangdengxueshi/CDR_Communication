package com.dexin.cdr_communication.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dexin.cdr_communication.R;
import com.dexin.cdr_communication.activity.MainActivity;
import com.dexin.cdr_communication.application.AppConfig;
import com.dexin.cdr_communication.utility.ApplicationUtility;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.vondear.rxtools.RxRegTool;
import com.vondear.rxtools.view.RxToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 星座图 Fragment
 */
public class ConstellationDiagramFragment extends BaseFragment {
    private Unbinder unbinder;
    @BindView(R.id.sc_constellation_diagram)
    ScatterChart mScConstellationDiagram;
    private XAxis mXAxis;
    private YAxis mLeftYAxis;
    private LimitLine _60LimitLine = new LimitLine(-60F, ""), _40LimitLine = new LimitLine(-40F, ""), _20LimitLine = new LimitLine(-20F, ""),
            _0LimitLine = new LimitLine(0F, ""),
            __20LimitLine = new LimitLine(20F, ""), __40LimitLine = new LimitLine(40F, ""), __60LimitLine = new LimitLine(60F, "");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_constellation_diagram, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) Objects.requireNonNull(getActivity())).setToolbarTitle(R.string.constellation_diagram);
        loadCacheValueInOnActivityCreated();
        {
            mScConstellationDiagram.setTouchEnabled(false);//设置是否可以触摸
            mScConstellationDiagram.setDragEnabled(false);//设置可以拖拽
            mScConstellationDiagram.setScaleEnabled(false);//设置可以缩放
            mScConstellationDiagram.setPinchZoom(false);//设置是否可以捏合缩放
//            mScConstellationDiagram.setDrawGridBackground(false);//设置绘制网格背景
//            mScConstellationDiagram.setMaxHighlightDistance(50F);//设置最大高光距离
//            mScConstellationDiagram.setMaxVisibleValueCount(100 * 100);//设置最大可见值的数量
            {//1.描述
                Description lDescription = mScConstellationDiagram.getDescription();
                lDescription.setEnabled(false);
            }
            {//2.图例
                Legend lLegend = mScConstellationDiagram.getLegend();
                lLegend.setEnabled(false);
//                lLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//                lLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//                lLegend.setOrientation(Legend.LegendOrientation.VERTICAL);
//                lLegend.setDrawInside(false);
//                lLegend.setTypeface(mTfLight);
//                lLegend.setXOffset(-20F);
            }
            {//3.限制线
                _60LimitLine.enableDashedLine(10F, 10F, 0F);
                _60LimitLine.setLineColor(R.color.colorGray);
                _40LimitLine.enableDashedLine(10F, 10F, 0F);
                _40LimitLine.setLineColor(R.color.colorGray);
                _20LimitLine.enableDashedLine(10F, 10F, 0F);
                _20LimitLine.setLineColor(R.color.light_black);
                _0LimitLine.enableDashedLine(10F, 0F, 0F);
                _0LimitLine.setLineColor(R.color.colorGray);
                __20LimitLine.enableDashedLine(10F, 10F, 0F);
                __20LimitLine.setLineColor(R.color.colorGray);
                __40LimitLine.enableDashedLine(10F, 10F, 0F);
                __40LimitLine.setLineColor(R.color.colorGray);
                __60LimitLine.enableDashedLine(10F, 10F, 0F);
                __60LimitLine.setLineColor(R.color.colorGray);
            }
            {//X轴
                mXAxis = mScConstellationDiagram.getXAxis();
                mXAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
                mXAxis.setAxisMinimum(-80F);
                mXAxis.setAxisMaximum(80F);
                mXAxis.setDrawLabels(false);
                mXAxis.setDrawGridLines(false);
//                xAxis.setTypeface();
//                xAxis.enableGridDashedLine(10F, 10F, 0F);
//                xAxis.setGranularityEnabled(true);
//                xAxis.setGranularity(20F);
//                xAxis.setLabelCount(5);
//                xAxis.setValueFormatter(new AxisValueFormatter());
//                lLimitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
//                lLimitLine.setTextSize(10F);
            }
            {//Y轴
                {//左Y轴
                    mLeftYAxis = mScConstellationDiagram.getAxisLeft();
                    mLeftYAxis.setAxisMinimum(-80F);//代替 setStartAtZero(true)
                    mLeftYAxis.setAxisMaximum(80F);
                    mLeftYAxis.setDrawLabels(false);
                    mLeftYAxis.setDrawGridLines(false);
//                    leftYAxis.setTypeface();
//                    leftYAxis.enableGridDashedLine(10F, 10F, 0F);
//                    leftYAxis.disableAxisLineDashedLine();
//                    leftYAxis.setMaxWidth(20F);
//                    leftYAxis.setMinWidth(20F);
//                    leftYAxis.setDrawZeroLine(true);
//                    leftYAxis.setGranularityEnabled(true);
//                    leftYAxis.setGranularity(20F);
                }
                {//右Y轴
                    YAxis rightYAxis = mScConstellationDiagram.getAxisRight();
                    rightYAxis.setAxisMinimum(-80F);
                    rightYAxis.setAxisMaximum(80F);
                    rightYAxis.setDrawLabels(false);
                    rightYAxis.setDrawGridLines(false);
//                    rightYAxis.setTypeface();
                }
            }
            mScConstellationDiagram.setData(new ScatterData());
            mScConstellationDiagram.invalidate();
            dynamicAdjustLimitLine();
        }
        initLocalReceiverInOnActivityCreated();
    }

    @Override
    public void onStop() {
        releaseLocalReceiverResourceInOnStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        ((MainActivity) Objects.requireNonNull(getActivity())).setToolbarTitle(R.string.app_name);
        {//重新绘制menu
            ApplicationUtility.getSPUtils().put(AppConfig.KEY_CONSTELLATION_DIAGRAM_TYPE_MENU_VISIABLE, false);
            getActivity().invalidateOptionsMenu();
        }
        super.onDestroyView();
        if (mScConstellationDiagram != null) {
            mScConstellationDiagram.invalidate();
        }
        unbinder.unbind();
    }

    private void dynamicAdjustLimitLine() {//动态调整“限制线”
        mXAxis.removeAllLimitLines();
        mLeftYAxis.removeAllLimitLines();
        mXAxis.addLimitLine(_0LimitLine);
        mLeftYAxis.addLimitLine(_0LimitLine);
        switch (ApplicationUtility.getSPUtils().getString(AppConfig.KEY_MSC_QAM_VALUE, " 4-QAM")) {
            case " 4-QAM":
                break;
            case "16-QAM":
                mXAxis.addLimitLine(_40LimitLine);
                mXAxis.addLimitLine(__40LimitLine);

                mLeftYAxis.addLimitLine(_40LimitLine);
                mLeftYAxis.addLimitLine(__40LimitLine);
                break;
            case "64-QAM":
                mXAxis.addLimitLine(_60LimitLine);
                mXAxis.addLimitLine(_40LimitLine);
                mXAxis.addLimitLine(_20LimitLine);
                mXAxis.addLimitLine(__20LimitLine);
                mXAxis.addLimitLine(__40LimitLine);
                mXAxis.addLimitLine(__60LimitLine);


                mLeftYAxis.addLimitLine(_60LimitLine);
                mLeftYAxis.addLimitLine(_40LimitLine);
                mLeftYAxis.addLimitLine(_20LimitLine);
                mLeftYAxis.addLimitLine(__20LimitLine);
                mLeftYAxis.addLimitLine(__40LimitLine);
                mLeftYAxis.addLimitLine(__60LimitLine);
                break;
            default:
        }
        if (mScConstellationDiagram.getData() != null) {
            mScConstellationDiagram.getData().notifyDataChanged();
            mScConstellationDiagram.notifyDataSetChanged();
            mScConstellationDiagram.invalidate();
        }
    }

    @NonNull
    public static ConstellationDiagramFragment newInstance() {
        return new ConstellationDiagramFragment();
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------TODO 本地广播模块--------------------------------------------------------------------
    //---------------------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓--------------------------------------------------------------------
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
    @BindView(R.id.tv_ber_value)
    TextView mTvBerValue;
//    @BindView(R.id.tv_service_data_modulation_mode)
//    TextView mTvServiceDataModulationMode;
//    @BindView(R.id.tv_subf_mode)
//    TextView mTvSubfMode;
//    @BindView(R.id.tv_info_modulation_mode)
//    TextView mTvInfoModulationMode;
//    @BindView(R.id.tv_ldpc_code_rate)
//    TextView mTvLdpcCodeRate;

    private void loadCacheValueInOnActivityCreated() {
        mRbSyncStatus.setChecked(ApplicationUtility.getSPUtils().getBoolean(AppConfig.KEY_SYNC_STATE_VALUE));
        mTvRadioPower.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_RADIO_FREQ_VALUE));
        mTvCnrValue.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_CNR_VALUE));
        mTvMerValue.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_MER_VALUE));
        mTvFreqOffsetEstimationValue.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_FREQ_OFFSET_VALUE));
        mTvClockDeviationValue.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_CLK_OFFSET_VALUE));
        mTvBerValue.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_BER_VALUE));
//        mTvServiceDataModulationMode.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_MSC_QAM_VALUE));
//        mTvSubfMode.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_SUBF_MODE_VALUE));
//        mTvInfoModulationMode.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_CIC_QAM_VALUE));
//        mTvLdpcCodeRate.setText(ApplicationUtility.getSPUtils().getString(AppConfig.KEY_LDPC_CR_VALUE));
    }

    private LocalReceiver mLocalReceiver;
    private IntentFilter mIntentFilter;

    private void initLocalReceiverInOnActivityCreated() {
        {
            mTvRadioPower.setSelected(true);
            mTvCnrValue.setSelected(true);
            mTvMerValue.setSelected(true);
            mTvFreqOffsetEstimationValue.setSelected(true);
            mTvClockDeviationValue.setSelected(true);
            mTvBerValue.setSelected(true);
//            mTvServiceDataModulationMode.setSelected(true);
//            mTvSubfMode.setSelected(true);
//            mTvInfoModulationMode.setSelected(true);
//            mTvLdpcCodeRate.setSelected(true);
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
        private List<String> mScattValueList = new ArrayList<>();
        private List<Entry> mEntryList = new ArrayList<>();

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || !AppConfig.isComponentAlive(ConstellationDiagramFragment.this)) return;  // 10,000 * 1 K
            switch (Objects.requireNonNull(intent.getAction())) {
                case AppConfig.ACTION_SHOW_RECEIVED_PARAM://TODO 显示接收到的数据:
                    String paramReceivedStr = intent.getStringExtra(AppConfig.KEY_RECEIVED_DATA);
                    if (TextUtils.isEmpty(paramReceivedStr) || !paramReceivedStr.contains("=") || Objects.equals(mLastParamReceiveStr, paramReceivedStr)) break;
                    mLastParamReceiveStr = paramReceivedStr;//更新收到的参数字符串
                    String[] lKeyValueStrArray = paramReceivedStr.split("&");
                    if (lKeyValueStrArray.length > 0) {
                        for (String lKeyValueStr : lKeyValueStrArray) {
                            if (lKeyValueStr.contains("=") && (!lKeyValueStr.startsWith("=") && !lKeyValueStr.endsWith("="))) {
                                String[] lKeyValueGroup = lKeyValueStr.split("=");
                                switch (lKeyValueGroup[0]) {
                                    case "scatt":
                                        String scattValueStr = lKeyValueGroup[1];
                                        if (mScattValueList.size() > 10) mScattValueList.remove(0);
                                        if (!mScattValueList.contains(scattValueStr)) {
                                            mScattValueList.add(scattValueStr);
                                            if (TextUtils.isEmpty(scattValueStr) || (scattValueStr.length() % 4 != 0) || !RxRegTool.isMatch("^[A-Fa-f0-9]+$", scattValueStr)) {
                                                RxToast.warning("星座数据格式非法");
                                                break;
                                            }

                                            int tempInt, parseXInt, parseYInt;
                                            for (int i = 0; i + 3 < scattValueStr.length(); i = i + 4) {
                                                tempInt = Integer.parseInt(scattValueStr.substring(i, i + 2), 16);
                                                parseXInt = (tempInt > 128) ? (tempInt - 256) : tempInt;
                                                tempInt = Integer.parseInt(scattValueStr.substring(i + 2, i + 4), 16);
                                                parseYInt = (tempInt > 128) ? (tempInt - 256) : tempInt;
                                                if (mEntryList.size() > 100 * 100) mEntryList.remove(0);//TODO 一定要进行旧数据的移除,根据条件移除队首元素
                                                if ((parseXInt <= -80 || parseXInt >= 80) || (parseYInt <= -80 || parseYInt >= 80)) continue;
                                                mEntryList.add(new Entry(parseXInt, parseYInt));
                                            }

                                            ScatterDataSet lScatterDataSet = new ScatterDataSet(mEntryList, "");//创建一个 DataSet 并给它一个 type
                                            lScatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
                                            lScatterDataSet.setColor(ColorTemplate.COLORFUL_COLORS[0]);
                                            lScatterDataSet.setScatterShapeSize(4F);
                                            ScatterData lScatterData = new ScatterData(lScatterDataSet);
                                            mScConstellationDiagram.setData(lScatterData);
                                            mScConstellationDiagram.invalidate();
                                        }
                                        break;
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
//                                            mTvServiceDataModulationMode.setText(mscQamValue);
                                            ApplicationUtility.getSPUtils().put(AppConfig.KEY_MSC_QAM_VALUE, mscQamValue);
                                            dynamicAdjustLimitLine();
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
//                                            mTvInfoModulationMode.setText(cicQamValue);
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
//                                            mTvLdpcCodeRate.setText(ldpcCrValue);
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
                                    case "subf_mode"://子帧分配方式:
                                        String subfModeValue = lKeyValueGroup[1];
                                        if (!Objects.equals(subfModeValue, ApplicationUtility.getSPUtils().getString(AppConfig.KEY_SUBF_MODE_VALUE))) {
//                                            mTvSubfMode.setText(subfModeValue);
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
