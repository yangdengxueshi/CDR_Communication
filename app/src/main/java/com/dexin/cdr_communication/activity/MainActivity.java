package com.dexin.cdr_communication.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.dexin.cdr_communication.R;
import com.dexin.cdr_communication.application.AppConfig;
import com.dexin.cdr_communication.application.CustomApplication;
import com.dexin.cdr_communication.fragment.MainFragment;
import com.dexin.cdr_communication.service.CDRService;
import com.dexin.cdr_communication.utility.ApplicationUtility;
import com.vondear.rxtools.RxNetTool;
import com.vondear.rxtools.view.RxToast;
import com.vondear.rxtools.view.dialog.RxDialogLoading;

import java.text.MessageFormat;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        loadMDFrameInOnCreate();
        initLocalReceiverInOnCreate();
        initBindServiceInOnCreate();

        loadFragment(MainFragment.newInstance(), false);
    }

    @Override
    protected void onDestroy() {
        releaseMDFrameInOnDestroy();
        releaseLocalReceiverInOnDestroy();
        releaseBindServiceResourceInOnDestroy();
        super.onDestroy();
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------TODO MD(质感设计)----------------------------------------------------------------------------
    //---------------------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓----------------------------------------------------------------
    private final Intent mConnectToCDRServerIntent = new Intent(AppConfig.ACTION_CONNECT_TO_CDR_SERVER);//FIXME 被用来发送广播
    private final Intent mSendConfigParamIntent = new Intent(AppConfig.ACTION_SEND_CONFIG_PARAM);

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    public void setToolbarTitle(int resId) {
        mToolbar.setTitle(resId);
    }

    private void loadMDFrameInOnCreate() {
        mToolbar.setLogo(R.drawable.ic_logo);                                   //TODO 设置Toolbar的Logo
        setSupportActionBar(mToolbar);                                          //TODO 设置上我们的 Toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);       //TODO 启用 HomeAsUp 按钮
        //TODO 0.两者的点击事件可以在 onOptionsItemSelected(MenuItem item) 中统一书写
        //TODO 1.设置HomeAsUp按钮的点击事件(打开抽屉在这里写好,关闭界面重写onOptionsItemSelected(MenuItem item) )
        //TODO 2.设置Toolbar上的action按钮的点击事件
        mToolbar.setOnMenuItemClickListener((MenuItem item) -> {
            if (AppConfig.IS_TRUE_ENVIRONMENT && !RxNetTool.isWifiConnected(CustomApplication.getContext())) {
                RxToast.warning(AppConfig.TOAST_PLEASE_CONNECT_TO_CDR_WIFI_FIRST);
                return true;
            }
            switch (item.getItemId()) {//TODO 在此设置的Toolbar的action按钮的点击事件优先级较高,但是不能设置HomeAsUp按钮的点击事件
                case R.id.item_connect:
                    item.setChecked(true);
                    AppConfig.LOCAL_BROADCAST_MANAGER.sendBroadcast(mConnectToCDRServerIntent);//TODO "通过发送广播通知Service去连接CDR服务器"
                    break;
                case R.id.item_all_subcarriers:
                    item.setChecked(true);
                    RxToast.info("全部子载波    命令已发送");
                    sendCmdToSetConstellationDiagramOutputMode("0");
                    break;
                case R.id.item_pilot:
                    item.setChecked(true);
                    RxToast.info("导频    命令已发送");
                    sendCmdToSetConstellationDiagramOutputMode("1");
                    break;
                case R.id.item_system_info:
                    item.setChecked(true);
                    RxToast.info("系统信息    命令已发送");
                    sendCmdToSetConstellationDiagramOutputMode("2");
                    break;
                case R.id.item_sdis:
                    item.setChecked(true);
                    RxToast.info("SDIS    命令已发送");
                    sendCmdToSetConstellationDiagramOutputMode("3");
                    break;
                case R.id.item_msds:
                    item.setChecked(true);
                    RxToast.info("MSDS    命令已发送");
                    sendCmdToSetConstellationDiagramOutputMode("4");
                    break;
                default:
            }
            return true;
        });
    }

    private void sendCmdToSetConstellationDiagramOutputMode(String constellationDiagramOutputMode) {
        mSendConfigParamIntent.putExtra(AppConfig.KEY_CONFIG_PARAM, MessageFormat.format("scatt_type={0}\n", constellationDiagramOutputMode));//麻点图类型(星座图)
        AppConfig.LOCAL_BROADCAST_MANAGER.sendBroadcast(mSendConfigParamIntent);

        ApplicationUtility.getSPUtils().put(AppConfig.KEY_CONSTELLATION_DIAGRAM_OUTPUT_MODE, constellationDiagramOutputMode);
    }

    private void releaseMDFrameInOnDestroy() {
        mToolbar = null;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.group_constellation_diagram_type, ApplicationUtility.getSPUtils().getBoolean(AppConfig.KEY_CONSTELLATION_DIAGRAM_TYPE_MENU_VISIABLE));
        switch (ApplicationUtility.getSPUtils().getString(AppConfig.KEY_CONSTELLATION_DIAGRAM_OUTPUT_MODE)) {
            case "0":
                menu.findItem(R.id.item_all_subcarriers).setChecked(true);
                break;
            case "1":
                menu.findItem(R.id.item_pilot).setChecked(true);
                break;
            case "2":
                menu.findItem(R.id.item_system_info).setChecked(true);
                break;
            case "3":
                menu.findItem(R.id.item_sdis).setChecked(true);
                break;
            case "4":
                menu.findItem(R.id.item_msds).setChecked(true);
                break;
            default:
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------TODO 本地广播模块--------------------------------------------------------------------
    //---------------------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓--------------------------------------------------------------------
    private LocalReceiver mLocalReceiver;
    private IntentFilter mShowPingDialogIntentFilter;
    private IntentFilter mCancelPingDialogIntentFilter;
    private RxDialogLoading mRxDialogLoading;

    private void initLocalReceiverInOnCreate() {
        mLocalReceiver = new LocalReceiver();
        mShowPingDialogIntentFilter = new IntentFilter(AppConfig.ACTION_SHOW_PING_DIALOG);//FIXME 被用来接收广播
        AppConfig.LOCAL_BROADCAST_MANAGER.registerReceiver(mLocalReceiver, mShowPingDialogIntentFilter);
        mCancelPingDialogIntentFilter = new IntentFilter(AppConfig.ACTION_CANCEL_PING_DIALOG);//FIXME 被用来接收广播
        AppConfig.LOCAL_BROADCAST_MANAGER.registerReceiver(mLocalReceiver, mCancelPingDialogIntentFilter);
    }

    private void releaseLocalReceiverInOnDestroy() {
        AppConfig.LOCAL_BROADCAST_MANAGER.unregisterReceiver(mLocalReceiver);
        mLocalReceiver = null;
        mShowPingDialogIntentFilter = null;
        mCancelPingDialogIntentFilter = null;
        if (mRxDialogLoading != null) {
            mRxDialogLoading.cancel();
            mRxDialogLoading = null;
        }
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!AppConfig.isComponentAlive(MainActivity.this)) return;
            switch (Objects.requireNonNull(intent.getAction())) {
                case AppConfig.ACTION_SHOW_PING_DIALOG:
                    if (mRxDialogLoading == null) mRxDialogLoading = new RxDialogLoading(MainActivity.this);
                    if (mRxDialogLoading.isShowing()) mRxDialogLoading.cancel();
                    mRxDialogLoading.show();
                    break;
                case AppConfig.ACTION_CANCEL_PING_DIALOG:
                    if (mRxDialogLoading != null) {
                        boolean pingSuccess = intent.getBooleanExtra(AppConfig.KEY_PING_STATUS, false);
                        mRxDialogLoading.cancel((pingSuccess ? RxDialogLoading.RxCancelType.success : RxDialogLoading.RxCancelType.error), (pingSuccess ? "连接服务器成功!" : "连接服务器失败!"));
                    }
                    break;
                default:
            }
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------TODO Bind Service 模块-------------------------------------------------------------
    //---------------------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓--------------------------------------------------------------------
    private boolean isServiceBind;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private void initBindServiceInOnCreate() {
        isServiceBind = bindService(new Intent(CustomApplication.getContext(), CDRService.class), mServiceConnection, BIND_AUTO_CREATE);
    }

    private void releaseBindServiceResourceInOnDestroy() {
        if (isServiceBind) {
            unbindService(mServiceConnection);
            isServiceBind = false;
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------TODO 加载Fragment 模块--------------------------------------------------------------
    //---------------------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓--------------------------------------------------------------------
    private final FragmentManager mSupportFragmentManager = getSupportFragmentManager();

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        if (addToBackStack) {
            mSupportFragmentManager.beginTransaction().replace(R.id.fl_fragment_container, fragment).addToBackStack(null).commit();
        } else {
            mSupportFragmentManager.beginTransaction().replace(R.id.fl_fragment_container, fragment).commit();
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------TODO 按两次返回退出程序--------------------------------------------------------------
    //---------------------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓--------------------------------------------------------------------
    private long TIME_BACK_PRESSED;//Back键被按下的时间间隔

    @Override
    public void onBackPressed() {
        if (ApplicationUtility.getSPUtils().getBoolean(AppConfig.KEY_MAIN_FRAGMENT_VISIBILITY)) {
            if ((TIME_BACK_PRESSED + (4L * 500L)) > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                RxToast.warning("再次点击返回键退出");
            }
            TIME_BACK_PRESSED = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }
}
