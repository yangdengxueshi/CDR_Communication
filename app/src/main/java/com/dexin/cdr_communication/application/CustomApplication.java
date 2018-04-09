package com.dexin.cdr_communication.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.dexin.cdr_communication.BuildConfig;
import com.dexin.cdr_communication.utility.ApplicationUtility;
import com.dexin.cdr_communication.utility.CrashHandler;
import com.github.anrwatchdog.ANRWatchDog;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.vondear.rxtools.RxTool;

import org.jetbrains.annotations.Contract;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;

public class CustomApplication extends Application {
    private static final String TAG = "TAG_CustomApplication";
    private static Context sContext;//FIXME 这里static所修饰的context并不会引起内存泄漏，因为static数据与单例的ApplicationContext同生命周期

    /**
     * 全局获取 Context对象
     *
     * @return 全局的 context对象
     */
    @Contract(pure = true)
    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Objects.equals(getCurrentProcessName(), BuildConfig.APPLICATION_ID)) {
            if (!LeakCanary.isInAnalyzerProcess(this)) {//如果不在分析器进程中:此进程专注于LeakCanary堆分析,你不应该在此进程中初始化App
                LeakCanary.install(this);
                initMemberVar();//初始化成员变量
            }
        }
    }

    /**
     * 初始化成员变量
     */
    private void initMemberVar() {
        handleExceptionAndANRGlobally();
        sContext = getApplicationContext();

        AppConfig.initLogAdapter();
        RxTool.init(this);
        Utils.init(this);
        com.github.mikephil.charting.utils.Utils.init(this);
    }

    /**
     * 全局处理Exception和ANR
     */
    private static void handleExceptionAndANRGlobally() {
        try {
            CrashHandler.getInstance();//TODO 全局捕获异常
            ANRWatchDog lANRWatchDog = new ANRWatchDog();
            lANRWatchDog.setANRListener(error -> {
                StringWriter lStringWriter = null;
                PrintWriter lPrintWriter = null;
                try {
                    lStringWriter = new StringWriter();
                    lPrintWriter = new PrintWriter(lStringWriter);
                    error.printStackTrace(lPrintWriter);
                    String errorStr = lStringWriter + "\n\n\n\n\n";
                    lStringWriter.flush();
                    lPrintWriter.flush();
                    LogUtils.e(errorStr);
                    ApplicationUtility.saveStringToFile(errorStr);//TODO 文件存储
                } catch (RuntimeException e) {
                    Logger.t(TAG).e(e, "handleExceptionAndANRGlobally");
                } finally {
                    try {
                        if (lStringWriter != null) {
                            lStringWriter.close();
                        }
                        if (lPrintWriter != null) {
                            lPrintWriter.close();
                        }
                        //TODO 自定义异常出现后的逻辑
                        Thread.sleep(3 * 500);//TODO 必须睡,否则日志文件不能记录下来
//                        ApplicationUtility.rebootAPP(getContext(), 0);
                    } catch (Exception e) {
                        Logger.t(TAG).e(e, "handleExceptionAndANRGlobally");
                    }
                }
            });
            lANRWatchDog.start();
        } catch (RuntimeException e) {
            Logger.t(TAG).e(e, "handleExceptionAndANRGlobally");
        }
    }

    /**
     * 获取当前进程的名字
     *
     * @return 当前进程名
     */
    private String getCurrentProcessName() {
        String currentProcessName = "";
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
            if ((runningAppProcessInfoList != null) && !runningAppProcessInfoList.isEmpty()) {
                for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfoList) {
                    if ((runningAppProcessInfo != null) && (runningAppProcessInfo.pid == android.os.Process.myPid())) {
                        currentProcessName = runningAppProcessInfo.processName;
                        break;
                    }
                }
            }
        }
        return currentProcessName;
    }
}
