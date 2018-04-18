package com.dexin.cdr_communication.application;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.dexin.cdr_communication.BuildConfig;
import com.vondear.rxtools.RxRegTool;

import org.jetbrains.annotations.Contract;

import java.text.DecimalFormat;

/**
 * App配置文件
 * <p>
 * 1.更新版本号     2.配置为真实环境       3.将日志关闭         4.确认输入框是否引起崩溃
 */
public final class AppConfig {
    public static final boolean IS_TRUE_ENVIRONMENT = true;

    public static void initLogAdapter() {
//        Logger.addLogAdapter(new AndroidLogAdapter());//不是正式环境才初始化日志打印器
    }

    public static final String mHost = IS_TRUE_ENVIRONMENT ? "192.168.8.108" : "10.0.2.2";
    public static final int mPort = 30000;
    public static final String UTF_8_ENCODING = "UTF-8";

    public static final String TOAST_PLEASE_CONNECT_TO_CDR_WIFI_FIRST = "请先连接到 CDR WiFI";

    public static final LocalBroadcastManager LOCAL_BROADCAST_MANAGER = LocalBroadcastManager.getInstance(CustomApplication.getContext());
    public static final String ACTION_SHOW_PING_DIALOG = BuildConfig.APPLICATION_ID + "_ACTION_SHOW_PING_DIALOG";
    public static final String ACTION_CANCEL_PING_DIALOG = BuildConfig.APPLICATION_ID + "_ACTION_CANCEL_PING_DIALOG";
    public static final String KEY_PING_STATUS = "KEY_PING_STATUS";
    public static final String ACTION_SEND_CONFIG_PARAM = BuildConfig.APPLICATION_ID + "_ACTION_SEND_CONFIG_PARAM";//发送配置参数
    public static final String KEY_CONFIG_PARAM = "KEY_CONFIG_PARAM";
    public static final String ACTION_SHOW_RECEIVED_PARAM = BuildConfig.APPLICATION_ID + "_ACTION_SHOW_RECEIVED_PARAM";//显示接收到的参数
    public static final String KEY_RECEIVED_DATA = "KEY_RECEIVED_DATA";
    public static final String ACTION_CONNECT_TO_CDR_SERVER = BuildConfig.APPLICATION_ID + "_ACTION_CONNECT_TO_CDR_SERVER";//显示接收到的参数

    // SP_KEY
    public static final String KEY_CONNECT_MENU_VISIABLE = "KEY_CONNECT_MENU_VISIABLE";
    public static final String KEY_CONSTELLATION_DIAGRAM_TYPE_MENU_VISIABLE = "KEY_CONSTELLATION_DIAGRAM_TYPE_MENU_VISIABLE";
    public static final String KEY_MAIN_FRAGMENT_VISIBILITY = "KEY_MAIN_FRAGMENT_VISIBILITY";
    public static final String KEY_RADIO_FREQ = "KEY_RADIO_FREQ";
    public static final String KEY_TRANSMISSION_MODE = "KEY_TRANSMISSION_MODE";
    public static final String KEY_SPECTRUM_MODE = "KEY_SPECTRUM_MODE";
    public static final String KEY_CONSTELLATION_DIAGRAM_OUTPUT_MODE = "KEY_CONSTELLATION_DIAGRAM_OUTPUT_MODE";
    public static final String KEY_SYNC_STATE_VALUE = "KEY_SYNC_STATE_VALUE";
    public static final String KEY_RADIO_FREQ_VALUE = "KEY_RADIO_FREQ_VALUE";
    public static final String KEY_CNR_VALUE = "KEY_CNR_VALUE";
    public static final String KEY_MER_VALUE = "KEY_MER_VALUE";
    public static final String KEY_FREQ_OFFSET_VALUE = "KEY_FREQ_OFFSET_VALUE";
    public static final String KEY_CLK_OFFSET_VALUE = "KEY_CLK_OFFSET_VALUE";
    public static final String KEY_MSC_QAM_VALUE = "KEY_MSC_QAM_VALUE";
    public static final String KEY_CIC_QAM_VALUE = "KEY_CIC_QAM_VALUE";
    public static final String KEY_LDPC_CR_VALUE = "KEY_LDPC_CR_VALUE";
    public static final String KEY_BER_VALUE = "KEY_BER_VALUE";
    public static final String KEY_SUBF_MODE_VALUE = "KEY_SUBF_MODE_VALUE";

    /**
     * 判断组件状态是否 活跃      //FIXME 还可以考虑使用“多态”来进行封装（待续）
     *
     * @return 组件状态
     */
    public static boolean isComponentAlive(Object component) {
        if (component instanceof Application) {
            Application application = (Application) component;
            return !application.isRestricted();
        } else if (component instanceof Activity) {
            Activity activity = (Activity) component;
            return !activity.isFinishing() && !activity.isDestroyed() && !activity.isRestricted();
        } else if (component instanceof Fragment) {
            Fragment fragment = (Fragment) component;
            return !fragment.isHidden() && !fragment.isRemoving() && !fragment.isDetached();
        } else if (component instanceof Service) {
            Service service = (Service) component;
            return !service.isRestricted();
        }
        return false;
    }

    /**
     * 根据 EditText 所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
     *
     * @param view  View
     * @param event 事件
     * @return 是否隐藏键盘
     */
    @Contract("null, _ -> false")
    public static boolean isShouldHideKeyboard(View view, MotionEvent event) {
        if (view instanceof EditText) {
            int[] location = {0, 0};
            view.getLocationInWindow(location);
            int left = location[0], top = location[1], bottom = top + view.getHeight(), right = left + view.getWidth();
            return !((event.getX() > left) && (event.getX() < right) && (event.getY() > top) && (event.getY() < bottom));
        }
        return false;
    }

    private static DecimalFormat mDecimalFormat = new DecimalFormat("0.0#E0");

    public static String strToScientificNotation(String numStr) {
        if (!RxRegTool.isMatch("^(-?\\d+)(\\.\\d+)?$", numStr)) return ("非法数据:" + numStr);
        if (Double.valueOf(numStr) == 0) return "0";
        return mDecimalFormat.format(Double.valueOf(numStr));
    }

    @NonNull
    public static String rfPowerPlusTen(String rfPowerNum) {
        if (!RxRegTool.isMatch("^(-?\\d+)(\\.\\d+)?$", rfPowerNum)) return ("非法数据:" + rfPowerNum);
        return String.valueOf(Float.valueOf(rfPowerNum) + 10);
    }
}
