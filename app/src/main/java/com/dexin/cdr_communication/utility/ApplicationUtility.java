package com.dexin.cdr_communication.utility;

import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.SPUtils;
import com.dexin.cdr_communication.application.AppConfig;
import com.dexin.cdr_communication.application.CustomApplication;
import com.dexin.cdr_communication.service.KillSelfService;
import com.orhanobut.logger.Logger;

import org.jetbrains.annotations.Contract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.Objects;

/**
 * 应用程序全局工具
 */
public final class ApplicationUtility {
    private static final String TAG = "TAG_ApplicationUtility";

    private ApplicationUtility() {
    }

    private static final class SPUtilsHolder {
        private static final SPUtils SP_UTILS = SPUtils.getInstance();
    }

    /**
     * 获取SP工具
     *
     * @return SP工具
     */
    @Contract(pure = true)
    public static SPUtils getSPUtils() {
        return SPUtilsHolder.SP_UTILS;
    }

    /**
     * 保存数据
     *
     * @param inputText 输入的文本
     */
    public static void saveStringToFile(String inputText) {
        FileOutputStream fileOutputStream = null;
        BufferedWriter bufferedWriter = null;
        try {
            File crashLogfile = new File(MessageFormat.format("{0}/crash.log", Objects.requireNonNull(CustomApplication.getContext().getExternalCacheDir()).getAbsolutePath()));
            fileOutputStream = new FileOutputStream(crashLogfile, true);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, AppConfig.UTF_8_ENCODING));
            bufferedWriter.write(inputText);
            fileOutputStream.flush();
            bufferedWriter.flush();
        } catch (Exception e) {
            Logger.t(TAG).e(e, "saveStringToFile");
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (Exception e) {
                Logger.t(TAG).e(e, "saveStringToFile");
            }
        }
    }

    /**
     * 重启整个APP
     *
     * @param context     程序上下文对象
     * @param delayMillis 延迟多少毫秒
     */
    public static void rebootAPP(Context context, long delayMillis) {
        Intent intent = new Intent(context, KillSelfService.class);
        intent.putExtra("packageName", context.getPackageName());
        intent.putExtra("delayMillis", delayMillis);
        context.startService(intent);//开启一个新的服务，用来重启本APP

        android.os.Process.killProcess(android.os.Process.myPid());//杀死整个进程
        System.exit(1);//退出Java虚拟机JVM
    }
}
