package com.cbox.facmodemanager;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.icu.util.IslamicCalendar;
import android.net.DhcpInfo;
import android.net.NetworkUtils;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    private final static String TAG = "CH_JM_FM";
    private Context mContext = MyService.this;
    private TextView tv = null;
    private String progress, market, MAC, MACChanged, manu;
    private WifiManager mWifiManager;
    //蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    private PackageManager pm;
    //U盘
    private String s = "";
    //private StringBuffer sb = new StringBuffer(s);
    private String path = null;
    private String path1 = null;
    private String path2 = null;
    private String path3 = null;
    //Timer
    private Timer mTimer;
    //师父的方法
    private String interfaceUrl = "http://127.0.0.1:18176/rpc";

    private static int flag = 1;

    private EthernetManager mEthManager;

    private List<ResolveInfo> apps = new ArrayList<>();

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //1.16修改
        manu = SystemProperties.get("ro.product.ch.chip.manu");
        Log.i(TAG, "----------------------manu--------------->" + manu);
        progress = SystemProperties.get("persist.sys.factory.progress");
        market = SystemProperties.get("ro.product.ch.target");
        MAC = systemProperiesInvoke.get(mContext, "ro.mac");
        MACChanged = exChange(MAC);
        pm = getPackageManager();
        //蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //wifiManager
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        //EtherManager
        mEthManager = (EthernetManager) getApplicationContext().getSystemService(Context.ETHERNET_SERVICE);
        //打开wifi
        //mWifiManager.setWifiEnabled(true);
        //Log.i("pppp", "wifi enabled");
        //循环执行
        mTimer = new Timer();
        //1.5s响一次
        mTimer.schedule(timerTask, 0, 500);


        //u盘
        x.Ext.init(getApplication());

        //系统启动时判断是否安装了吞吐量apk及U盘中是否有该文件

        ArrayList<StorageItem> list = getMounted();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String path1 = list.get(i).path;
                Log.i(TAG, "path  = " + path1);
                Log.i(TAG, "filesocket ex?>>>" + SocketIsExists(path1) + "");
                if (SocketIsExists(path1)) {
                    path = path1;
                    Log.i(TAG, " set sys.sockettest");
                    //设置属性，表明sockettest存在
                    SystemProperties.set("sys.sockettest", "1");
                    //安装并启动sockettest
//                    docmd("cp " + path + "/factory/SocketTest.apk " + "/data/app");
//                    docmd("chmod 644 /data/app" + "/SocketTest.apk");
//                    docmd("pm install /data/app" + "/SocketTest.apk");
//                    Log.i("pppp ", "socket exists | do in");
                }
            }
        }

        String socket = SystemProperties.get("sys.sockettest");
        Log.i(TAG, "sockettest is ???" + socket);
        //判断socket的值，1则为存在特定文件
        if (socket.equals("1")) {
            //打开wifi
//            mWifiManager.setWifiEnabled(true);
//            Log.i("pppp", "wifi enabled");
//            Toast.makeText(this, "SocketTest in U>>>Start SocketTest", Toast.LENGTH_SHORT).show();
//            if (isAppInstalled(mContext, "com.henggucn.installtest.sockettest")) {
//                Log.i("pppp>>>socket", " socket installed start ");
//                Intent intent1 = new Intent();
//                intent1.setComponent(new ComponentName("com.henggucn.installtest.sockettest", "com.henggucn.installtest.sockettest.SocketActivity"));
//                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent1);
//            } else {
//                //安装并启动sockettest
//                docmd("cp " + path + "/factory/SocketTest.apk " + "/data/app");
//                docmd("chmod 644 /data/app" + "/SocketTest.apk");
//                //docmd("pm install /data/app" + "/SocketTest.apk");
//                Log.i("pppp ", "socket exists | do in");
//            }
        } else {
            Log.i(TAG, "socket is not in u");
            Log.i(TAG, "socket installed??? " + isSocketAppInstalled(mContext, "com.henggucn.installtest.sockettest"));
            if (isSocketAppInstalled(mContext, "com.henggucn.installtest.sockettest")) {
                //Log.i("pppp>>>socket", "socket is not in u | socket is installed");
                //执行一次卸载sockettest操作
                Log.i(TAG, "do uninstall sockettest");
                docmd("pm uninstall com.henggucn.installtest.sockettest");
                Log.i(TAG, "set sys.mount -->1");
                SystemProperties.set("sys.mount", "1");
            }

//            if (isAppInstalled(mContext, "com.changhong.factorymode")) {
//                Log.i(TAG, "factorymode installed do launch");
//                Intent intent1 = new Intent();
//                intent1.setComponent(new ComponentName("com.changhong.factorymode", "com.changhong.factorymode.MainActivity"));
//                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                try {
//                    mContext.startActivity(intent1);
//                    Log.i(TAG, "installed try launch factorymode");
//                    //1.16 修改
//                    SystemProperties.set("sys.limit.install.app", "false");
//                    Log.i(TAG, "sys.limit.install.app >>> false");
//
//                    SystemProperties.set("persist.sys.factorymodestate", "1");
//                    Log.i(TAG, " factorymode state>>>1 setted");
//                } catch (Exception e) {
//                    Log.i(TAG, "launch failed>>>" + e.toString());
//                }
//            }
            //系统启动时判断是否安装了工厂菜单
            //如果安装了 则启动
            //如果没安装则调用U盘安装的命令

        }
        return flags;
    }


    //循环执行
    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            Message msg1 = new Message();
            msg1.what = 1;
            //handler.sendMessage(msg1);
        }
    };

//    @SuppressLint("HandlerLeak")
//    Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:
//                    if (manu.equals("mstar")) {
//                        progress = SystemProperties.get("persist.sys.factory.progress");
//                        //Log.i("pppp", progress);
//                        if (progress.equals("1")) {
//                            //version4.5 修改
//                            //暂停timertask
//                            mTimer.cancel();
//                            //同步
//                            Log.i(TAG, "progress1 = 1");
//                            //关闭WiFi
//                            mWifiManager.setWifiEnabled(false);
//                            //关闭蓝牙
//                            mBluetoothAdapter.disable();
//                            //设置 persist.sys.factory.progress为2.
//                            SystemProperties.set("persist.sys.factory.progress", "2");
//                            progress = SystemProperties.get("persist.sys.factory.progress");
//                            Log.i(TAG, " progress setted >>>" + progress);
//
//                            //删除所有设置属性
//                            docmd("rm -rf /data/data/com.android.providers.settings/databases");
//                            docmd("rm /data/property/persist.sys.wire.mode");
//                            SystemProperties.set("persist.sys.wire.mode", "dhcp");
//                            docmd("sync");
//
//                            //销毁工厂菜单
//                            //第一种方法
//                            //pm uninstall的卸载方法 可能在广播发出后 还需要一点时间来完成操作
//                            // 所以如果在收到广播之后迅速断电 则可能造成未卸载成功的问题
//                            //但是直接rm 不会发卸载的广播，所以现在处理方式为在卸载广播中rm一次
//                            docmd("pm uninstall com.changhong.factorymode");
//                            //第二种方法
////                            String grep = null;
////                            try {
////                                grep = playRunTime("ls /data/app | grep -E 'factory|Factory'");
////                                Log.i("apppp", "grep reslut:" + grep);
////                            } catch (Exception e) {
////                                e.printStackTrace();
////                            }
////                            if (grep != null) {
////                                Log.i("apppp", "do rm " + grep);
////                                docmd("rm -rf /data/app/" + grep);
////                            }
//                            Log.i(TAG, "ps=1 fm>uninstall isdone");
//                            SystemProperties.set("sys.install.flag", "1");
//                            //同步
//                            docmd("sync");
//                            Log.i(TAG, "sync2 done");
//                            //直接进入ErrorActivity
//                            Intent errorintent1 = new Intent(mContext, ErrorActivity.class);
//                            errorintent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(errorintent1);
//
//
//                            //设置网络状态改为删除数据库的形式，不再单独设置
//                            /** switch (market) {
//                             case "gdmobile":
//                             Log.i("ppppp", "market is gdmobile");
//
//                             //需要循环执行的代码
//                             //设置IPOE方式
//
//                             //MSTAR 设置IPOE方式
//                             //1.12修改
//                             resetTODhcp();
//
//                             SystemProperties.set("persist.sys.isipoe", "true");
//                             //设置账号
//                             SystemProperties.set("persist.sys.bytuetech.username", MACChanged);
//                             //设置密码
//                             SystemProperties.set("persist.sys.bytuetech.password", "GdMCC68@OTV");
//
//                             //AMLOGIC 设置IPOE方式
//
//                             //                                EthernetDevInfo info1 = new EthernetDevInfo();
//                             //                                info1.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_DHCP_AUTH);
//                             //                                info1.setIfName("eth0");
//                             //                                info1.setIpAddress(null);
//                             //                                info1.setRouteAddr(null);
//                             //                                info1.setDnsAddr(null);
//                             //                                info1.setNetMask(null);
//                             //                                mEthManager.setAuthState(MACChanged, "GdMCC68@OTV", null, true);
//                             //                                //	mEthManager.setAuthState(name + "@iptv.ln", password, null, true);
//                             //                                mEthManager.updateEthDevInfo(info1);
//
//                             Log.i("ppppp", "ipoe is done");
//
//                             //同步
//                             docmd("sync");
//                             Log.i("pppp", "sync3 done");
//                             break;
//                             case "scmobile":
//                             Log.i("ppppp", "market is scmobile");
//
//                             //需要循环执行的代码
//                             //设置IPOE方式
//
//                             //MSTAR 设置IPOE方式
//                             //1.12修改
//                             resetTODhcp();
//
//                             SystemProperties.set("persist.sys.isipoe", "true");
//                             //设置账号
//                             SystemProperties.set("persist.sys.bytuetech.username", MACChanged);
//                             //设置密码
//                             SystemProperties.set("persist.sys.bytuetech.password", "ScMCC68@OTV");
//
//                             //AMLOGIC 设置IPOE方式
//                             //                                EthernetDevInfo info2 = new EthernetDevInfo();
//                             //                                info2.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_DHCP_AUTH);
//                             //                                info2.setIfName("eth0");
//                             //                                info2.setIpAddress(null);
//                             //                                info2.setRouteAddr(null);
//                             //                                info2.setDnsAddr(null);
//                             //                                info2.setNetMask(null);
//                             //                                mEthManager.setAuthState(MACChanged, "ScMCC68@OTV", null, true);
//                             //                                //	mEthManager.setAuthState(name + "@iptv.ln", password, null, true);
//                             //                                mEthManager.updateEthDevInfo(info2);
//                             Log.i("ppppp", "ipoe is done");
//
//                             //同步
//                             docmd("sync");
//                             Log.i("pppp", "sync4 done");
//                             break;
//                             case "public":
//                             Log.i("pppp", "market is public");
//                             //设置为DHCP方式
//
//                             // Mstar设置DHCP方法
//                             SystemProperties.set("persist.sys.isipoe", "false");
//
//                             //Amlogic设置DHCP方法
//                             resetTODhcp();
//
//                             //同步
//                             docmd("sync");
//                             Log.i("pppp", "sync5 done");
//                             break;
//                             default:
//                             Log.i("pppp", "market is default");
//                             //设置为DHCP方式
//
//                             // Mstar设置DHCP方法
//                             SystemProperties.set("persist.sys.isipoe", "false");
//
//                             //Amlogic设置DHCP方法
//                             resetTODhcp();
//
//                             //同步
//                             docmd("sync");
//                             Log.i("pppp", "sync6 done");
//                             break;
//
//                             } */
//                        }
//                    } else if (manu.equals("amlogic")) {
//                        progress = SystemProperties.get("persist.sys.factory.progress");
//                        if (progress.equals("1")) {
//                            //暂停timertask
//                            mTimer.cancel();
//                            Log.i(TAG, "progress1 = 1");
//
//                            //关闭WiFi
//                            mWifiManager.setWifiEnabled(false);
//                            //关闭蓝牙
//                            mBluetoothAdapter.disable();
//                            //设置 persist.sys.factory.progress为2.
//                            SystemProperties.set("persist.sys.factory.progress", "2");
//                            progress = SystemProperties.get("persist.sys.factory.progress");
//                            Log.i(TAG, " progress setted >>>" + progress);
//
//                            docmd("rm -rf /data/data/com.android.providers.settings/databases");
//                            docmd("rm /data/misc/etc/dhcpcd-eth0-auth.conf");
//                            docmd("sync");
//                            //销毁工厂菜单
//                            //第一种方法
//                            docmd("pm uninstall com.changhong.factorymode");
//                            //第二种方法
////                            String grep = null;
////                            try {
////                                grep = playRunTime("ls /data/app | grep -E 'factory|Factory'");
////                                Log.i("apppp", "grep reslut:" + grep);
////                            } catch (Exception e) {
////                                e.printStackTrace();
////                            }
////                            if (grep != null) {
////                                Log.i("apppp", "do rm " + grep);
////                                docmd("rm -rf /data/app/" + grep);
////                            }
//                            Log.i(TAG, "ps=1 fm>uninstall isdone");
//                            SystemProperties.set("sys.install.flag", "1");
//                            //同步
//                            docmd("sync");
//                            Log.i(TAG, "sync2 done");
//                            //直接进入ErrorActivity
//                            Intent errorintent2 = new Intent(mContext, ErrorActivity.class);
//                            errorintent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(errorintent2);
//
//
//                            /** switch (market)
//                             {
//                             case "gdmobile":
//                             Log.i("ppppp", "market is gdmobile");
//
//                             //需要循环执行的代码
//                             //设置IPOE方式
//
//                             //MSTAR 设置IPOE方式
//                             //1.12修改
//                             //resetTODhcp();
//
//                             //SystemProperties.set("persist.sys.isipoe", "true");
//                             //                                    //设置账号
//                             //                                    SystemProperties.set("persist.sys.bytuetech.username", MACChanged);
//                             //                                    //设置密码
//                             //                                    SystemProperties.set("persist.sys.bytuetech.password", "GdMCC68@OTV");
//
//                             //AMLOGIC 设置IPOE方式
//
//                             EthernetDevInfo info1 = new EthernetDevInfo();
//                             info1.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_DHCP_AUTH);
//                             info1.setIfName("eth0");
//                             info1.setIpAddress(null);
//                             info1.setRouteAddr(null);
//                             info1.setDnsAddr(null);
//                             info1.setNetMask(null);
//                             mEthManager.setAuthState(MACChanged, "GdMCC68@OTV", null, true);
//                             //	mEthManager.setAuthState(name + "@iptv.ln", password, null, true);
//                             mEthManager.updateEthDevInfo(info1);
//
//                             Log.i("ppppp", "ipoe is done");
//
//                             //同步
//                             docmd("sync");
//                             Log.i("pppp", "sync3 done");
//                             break;
//                             case "scmobile":
//                             Log.i("ppppp", "market is scmobile");
//
//                             //需要循环执行的代码
//                             //设置IPOE方式
//
//                             //MSTAR 设置IPOE方式
//                             //1.12修改
//                             //resetTODhcp();
//
//                             //                                    SystemProperties.set("persist.sys.isipoe", "true");
//                             //                                    //设置账号
//                             //                                    SystemProperties.set("persist.sys.bytuetech.username", MACChanged);
//                             //                                    //设置密码
//                             //                                    SystemProperties.set("persist.sys.bytuetech.password", "ScMCC68@OTV");
//
//                             //AMLOGIC 设置IPOE方式
//                             EthernetDevInfo info2 = new EthernetDevInfo();
//                             info2.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_DHCP_AUTH);
//                             info2.setIfName("eth0");
//                             info2.setIpAddress(null);
//                             info2.setRouteAddr(null);
//                             info2.setDnsAddr(null);
//                             info2.setNetMask(null);
//                             mEthManager.setAuthState(MACChanged, "ScMCC68@OTV", null, true);
//                             //	mEthManager.setAuthState(name + "@iptv.ln", password, null, true);
//                             mEthManager.updateEthDevInfo(info2);
//                             //                                    final ContentResolver cr = mContext.getContentResolver();
//                             //                                    Settings.Secure.putInt(cr, Settings.Secure.ETH_CONF,1);
//                             //                                    Settings.Secure.putString(cr, Settings.Secure.ETH_IFNAME, "eth0");
//                             //                                    Settings.Secure.putString(cr, Settings.Secure.ETH_MODE, EthernetDevInfo.ETH_CONN_MODE_DHCP_AUTH);
//                             //                                    Settings.Secure.putString(cr, Settings.Secure.ETH_PROXY_HOST, "");
//                             //                                    Settings.Secure.putInt(cr, Settings.Secure.ETH_PROXY_PORT, 8080);
//                             //                                    Settings.Secure.putString(cr, Settings.Secure.ETH_PROXY_EXCLUSION_LIST, "");
//                             Log.i("ppppp", "ipoe is done");
//
//                             //同步
//                             docmd("sync");
//                             Log.i("pppp", "sync4 done");
//                             break;
//                             case "public":
//                             Log.i("pppp", "market is public");
//                             //设置为DHCP方式
//
//                             // Mstar设置DHCP方法
//                             SystemProperties.set("persist.sys.isipoe", "false");
//
//                             //Amlogic设置DHCP方法
//                             resetTODhcp();
//
//                             //同步
//                             docmd("sync");
//                             Log.i("pppp", "sync5 done");
//                             break;
//                             default:
//                             Log.i("pppp", "market is default");
//                             //设置为DHCP方式
//
//                             // Mstar设置DHCP方法
//                             SystemProperties.set("persist.sys.isipoe", "false");
//
//                             //Amlogic设置DHCP方法
//                             resetTODhcp();
//
//                             //同步
//                             docmd("sync");
//                             Log.i("pppp", "sync6 done");
//                             break;
//
//                             } */
//
//                        }
//                    } else if (manu.equals("hisilicon")) {
//                        progress = SystemProperties.get("persist.sys.factory.progress");
//                        //Log.i("pppp", progress);
//                        if (progress.equals("1")) {
//                            //暂停timertask
//                            mTimer.cancel();
//                            Log.i(TAG, "progress1 = 1");
//                            //关闭WiFi
//                            mWifiManager.setWifiEnabled(false);
//                            //关闭蓝牙
//                            mBluetoothAdapter.disable();
//                            //设置 persist.sys.factory.progress为2.
//                            SystemProperties.set("persist.sys.factory.progress", "2");
//                            progress = SystemProperties.get("persist.sys.factory.progress");
//                            Log.i(TAG, " progress setted >>>" + progress);
//
//                            docmd("rm -rf /data/data/com.android.providers.settings/databases");
//                            docmd("sync");
//
//                            //销毁工厂菜单
//                            //第一种方法
//                            docmd("pm uninstall com.changhong.factorymode");
//                            //第二种方法
////                            String grep = null;
////                            try {
////                                //grep = playRunTime("ls /data/app | grep -E 'factory|Factory'");
////                                grep = playRunTime("ls /data/app | grep ");
////                                Log.i("apppp", "grep reslut:" + grep);
////                            } catch (Exception e) {
////                                e.printStackTrace();
////                                Log.i("apppp", "exception:" + e.toString());
////                            }
////                            if (grep != null) {
////                                Log.i("apppp", "do rm " + grep);
////                                docmd("rm -rf /data/app/" + grep);
////                            }
//                            Log.i(TAG, "ps=1 fm>uninstall isdone");
//                            SystemProperties.set("sys.install.flag", "1");
//                            //同步
//                            docmd("sync");
//                            Log.i(TAG, "sync2 done");
//                            //直接进入ErrorActivity
//                            Intent errorintent3 = new Intent(mContext, ErrorActivity.class);
//                            errorintent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(errorintent3);
//
////                            switch (market) {
////                                case "gdmobile":
////                                    Log.i("ppppp", "market is gdmobile");
////
////                                    //需要循环执行的代码
////                                    //设置IPOE方式
////
////                                    //HISI 设置IPOE方式
////                                    //先设置为DHCP
////                                    resetTODhcp();
////
////                                    Log.i("pppp", "hisi dhcp is done");
////                                    //Log.i("ppppp", "ipoe is done");
////
////                                    //同步
////                                    docmd("sync");
////                                    Log.i("pppp", "sync3 done");
////                                    break;
////                                case "scmobile":
////                                    Log.i("ppppp", "market is scmobile");
////
////                                    //需要循环执行的代码
////                                    //设置IPOE方式
////
////                                    //MSTAR 设置IPOE方式
////                                    //先设置DHCP
////                                    resetTODhcp();
////                                    Log.i("pppp", "hisi dhcp is done");
////
////                                    //Log.i("ppppp", "ipoe is done");
////
////                                    //同步
////                                    docmd("sync");
////                                    Log.i("pppp", "sync4 done");
////                                    break;
////                                case "public":
////                                    Log.i("pppp", "market is public");
////                                    //设置为DHCP方式
////
////                                    // Mstar设置DHCP方法
////                                    SystemProperties.set("persist.sys.isipoe", "false");
////
////                                    //Amlogic设置DHCP方法
////                                    resetTODhcp();
////
////                                    //同步
////                                    docmd("sync");
////                                    Log.i("pppp", "sync5 done");
////                                    break;
////                                default:
////                                    Log.i("pppp", "market is default");
////                                    //设置为DHCP方式
////
////                                    // Mstar设置DHCP方法
////                                    SystemProperties.set("persist.sys.isipoe", "false");
////
////                                    //Amlogic设置DHCP方法
////                                    resetTODhcp();
////
////                                    //同步
////                                    docmd("sync");
////                                    Log.i("pppp", "sync6 done");
////                                    break;
////
////                            }
//                        }
//                    }
//                    break;
//                case 2:
//                    Intent intent1 = pm.getLaunchIntentForPackage("com.changhong.factorymode");
//                    try {
//                        startActivity(intent1);
//                    } catch (Exception e) {
//                        Log.i(TAG, "launch failed>>>" + e.toString());
//                    }
//                    Log.i(TAG, "case2 do launch");
//                    break;
//                default:
//                    break;
//            }
//
//        }
//
//    };

    //U盘挂载广播接收器
    private BroadcastReceiver mediaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, " media mounted");
            ArrayList<StorageItem> list = getMounted();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    String path1 = list.get(i).path;
                    Log.i(TAG, "path  = " + path1);
                    Log.i(TAG, SocketIsExists(path1) + "");
                    if (SocketIsExists(path1)) {
                        path = path1;
                        //设置属性，表明sockettest存在
                        SystemProperties.set("sys.sockettest", "1");
                    }
                }
            }
            String socket = SystemProperties.get("sys.sockettest");
            Log.i(TAG, "sockettest is i u ???" + socket);
            if (socket.equals("1")) {
                if (isSocketAppInstalled(mContext, "com.henggucn.installtest.sockettest")) {
                    Log.i(TAG, " socket installed start ");
                    Intent intent1 = new Intent();
                    intent1.setComponent(new ComponentName("com.henggucn.installtest.sockettest", "com.henggucn.installtest.sockettest.SocketActivity"));
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                } else {
                    //安装并启动sockettest
                    docmd("cp " + path + "/factory/SocketTest.apk " + "/data/app");
                    docmd("chmod 644 /data/app" + "/SocketTest.apk");
                    docmd("pm install /data/app" + "/SocketTest.apk");
                    Log.i(TAG, "socket exists | do in");
                }
            } else {
                //执行一次卸载sockettest操作
                Log.i(TAG, "do uninstall sockettest");
                docmd("pm uninstall com.henggucn.installtest.sockettest");
            }
        }
    };

    //运行cmd命令
    private void docmd(String keyCommand) {
        try {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(keyCommand);
            //Log.i("zendd>>>JayChou", "success");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Log.i("zendd>>>JayChou", "failed");
            //Log.i("zendd>>>JayChou", e.toString());
        }
    }


    /**
     * 根据包名判断应用是否已安装
     */
//    private boolean isAppInstalled(Context context, String packagename) {
//        PackageInfo packageInfo;
//        try {
//            loadApps(context);
//            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
//        } catch (PackageManager.NameNotFoundException e) {
//            packageInfo = null;
//            //3.1修改
//            //e.printStackTrace();
//        }
//        if (packageInfo == null) {
//            //System.out.println("没有安装");
//            return false;
//        } else {
//            //System.out.println("已经安装");
//            return true;
//        }
//    }
    private boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            //3.1修改
            e.printStackTrace();
        }
        if (packageInfo == null) {
            Log.i(TAG, "packageInfo == null");
            File file = new File("/data/app/FactoryMode.apk");
            if (file.exists()) {
                Log.i(TAG, "FactoryMode.apk exist");
                return true;
            }
            file = new File("/data/app/com.changhong.factorymode-1.apk");
            if (file.exists()) {
                Log.i(TAG, "/data/app/com.changhong.factorymode-1.apk exist");
                return true;
            }

            file = new File("/data/app/com.changhong.factorymode-2.apk");
            if (file.exists()) {
                Log.i(TAG, "/data/app/com.changhong.factorymode-2.apk exist");
                return true;
            }
            file = new File("/data/app/com.changhong.factorymode-3.apk");
            if (file.exists()) {
                Log.i(TAG, "/data/app/com.changhong.factorymode-3.apk exist");
                return true;
            }
            file = new File("/data/app/com.changhong.factorymode-4.apk");
            if (file.exists()) {
                Log.i(TAG, "/data/app/com.changhong.factorymode-4.apk exist");
                return true;
            }
            file = new File("/data/app/com.changhong.factorymode-5.apk");
            if (file.exists()) {
                Log.i(TAG, "/data/app/com.changhong.factorymode-5.apk exist");
                return true;
            }
            file = new File("/data/app/com.changhong.factorymode-6.apk");
            if (file.exists()) {
                Log.i(TAG, "/data/app/com.changhong.factorymode-6.apk exist");
                return true;
            }
            file = new File("/data/app/com.changhong.factorymode-7.apk");
            if (file.exists()) {
                Log.i(TAG, "/data/app/com.changhong.factorymode-7.apk exist");
                return true;
            }
            file = new File("/data/app/com.changhong.factorymode-8.apk");
            if (file.exists()) {
                Log.i(TAG, "/data/app/com.changhong.factorymode-8.apk exist");
                return true;
            }
            file = new File("/data/app/com.changhong.factorymode-9.apk");
            if (file.exists()) {
                Log.i(TAG, "/data/app/com.changhong.factorymode-9.apk exist");
                return true;
            }
            file = new File("/data/app/com.changhong.factorymode-10.apk");
            if (file.exists()) {
                Log.i(TAG, "/data/app/com.changhong.factorymode-10.apk exist");
                return true;
            }
            //开机这个文件一直存在，
            //海思上面该标志位是在/private下， 但Amloigc是在/params下，
            // 所以还是不要判断它了，没有必要
//            file = new File("/private/symbol_fact_apks_installed");
//            if (!file.exists()) {
//                Log.i(TAG, TAG + "/private/symbol_fact_apks_installed not exist");
//                return true;
//            }
            return false;
        } else {

            return true;
        }
    }


    /**
     * 根据包名判断SocketTest应用是否已安装
     */
    private boolean isSocketAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            //loadApps(context);
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            //3.1修改
            //e.printStackTrace();
        }
        if (packageInfo == null) {
            //System.out.println("没有安装");
            return false;
        } else {
            //System.out.println("已经安装");
            return true;
        }
    }


    //列出所有包名
    private void loadApps(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        apps = context.getPackageManager().queryIntentActivities(intent, 0);
        //for循环遍历ResolveInfo对象获取包名和类名
        for (int i = 0; i < apps.size(); i++) {
            ResolveInfo info = apps.get(i);
            String packageName = info.activityInfo.packageName;
            CharSequence cls = info.activityInfo.name;
            CharSequence name = info.activityInfo.loadLabel(context.getPackageManager());
            Log.e("pppp ---in service---", name + "----" + packageName + "----" + cls);
        }
    }

    //判断FactoryMode文件是否存在
    public boolean fileIsExists(String pathh) {
        try {
            File f = new File(pathh + "/factory/FactoryMode.apk");
            f.getAbsolutePath();
            Log.i(TAG + " path>>>", f.getAbsolutePath());
            if (!f.exists()) {
                Log.i(TAG, "factoryapk not exist");
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        Log.i(TAG, "factoryapk exists");
        return true;
    }

    //判断SocketTest文件是否存在
    public boolean SocketIsExists(String pathh) {
        try {
            File f = new File(pathh + "/factory/SocketTest.apk");
            f.getAbsolutePath();
            Log.i(TAG + "path>>>", f.getAbsolutePath());
            if (!f.exists()) {
                Log.i(TAG, "SocketTest not exist");
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        Log.i(TAG, "SocketTest exists");
        return true;
    }


    //U盘检测
    private ArrayList<StorageItem> getMounted() {

        ArrayList<StorageItem> list = (ArrayList<StorageItem>) StorageUtil.listAllStorage(this);

        return list;

//        StringBuffer sb = new StringBuffer(s);
//        if (list != null) {
//            for (StorageItem o : list) {
//                sb.append(o.path); // �ַ���׷��
//            }
//        } else {
//            Log.e("pppp isnull", "null");
//            return null;
//        }
//        int num = sb.indexOf("0");
//        path = sb.substring(num + 1);
//
//
//        Log.i("pppp path 0 is>>>", path);
//        return path;
    }

//    private String getMounted() {
//
//        ArrayList<StorageItem> list = (ArrayList<StorageItem>) StorageUtil.listAllStorage(this);
//        StringBuffer sb = new StringBuffer(s);
//        if (list != null) {
//            for (StorageItem o : list) {
//                sb.append(o.path); // �ַ���׷��
//            }
//        } else {
//            Log.e("pppp isnull", "null");
//            return null;
//        }
//        int num = sb.indexOf("0");
//        path = sb.substring(num + 1);
//
//
//        Log.i("pppp path 0 is>>>", path);
//        return path;
//    }

    //把一个字符串中的小写转换为大写
    public static String exChange(String str) {
        StringBuffer sb = new StringBuffer();
        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (Character.isLowerCase(c)) {
                    sb.append(Character.toUpperCase(c));
                } else {
                    sb.append(c);
                }
            }
        }

        return sb.toString();
    }

    //检测应用是否处于前台
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals("com.changhong.factorymode")) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                } else {
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    //师父的方法 调用底层服务运行安装命令
    private void pminstall(String str) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "pm install");
            jsonObject.put("path", str);
            jsonObject.put("apkname", "FactoryMode.apk");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(interfaceUrl);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    if (result != null) {
                        JSONObject resultObject = new JSONObject(result);
                        int writeresult = (int) resultObject.get("result");
                        if (writeresult == 1) {
                            Toast.makeText(
                                    mContext,
                                    getResources().getString(
                                            R.string.toast_write_fail),
                                    Toast.LENGTH_SHORT).show();
                        } else if (writeresult == 0) {

                            //mHandler.sendEmptyMessage(MSG_READ_S_M);
                            //mHandler.sendEmptyMessage(MSG_GETSTBINFO);
                            Toast.makeText(
                                    mContext,
                                    getResources().getString(
                                            R.string.toast_write_success),
                                    Toast.LENGTH_SHORT).show();
                            Intent intent2 = pm.getLaunchIntentForPackage("com.changhong.factorymode");
                            startActivity(intent2);
                            Log.i(TAG, "222launch factorymode is done");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void pmuninstall() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "pm uninstall");
            jsonObject.put("package", "com.changhong.factorymode");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(interfaceUrl);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    if (result != null) {
                        JSONObject resultObject = new JSONObject(result);
                        int writeresult = (int) resultObject.get("result");
                        if (writeresult == 1) {
                            Toast.makeText(
                                    mContext,
                                    getResources().getString(
                                            R.string.toast_write_fail),
                                    Toast.LENGTH_SHORT).show();
                        } else if (writeresult == 0) {

                            //mHandler.sendEmptyMessage(MSG_READ_S_M);
                            //mHandler.sendEmptyMessage(MSG_GETSTBINFO);
                            Toast.makeText(
                                    mContext,
                                    getResources().getString(
                                            R.string.toast_write_success),
                                    Toast.LENGTH_SHORT).show();
                            Intent intent2 = pm.getLaunchIntentForPackage("com.changhong.factorymode");
                            startActivity(intent2);
                            Log.i(TAG, "222uninstall factorymode is done");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {

            }
        });

    }

    //amlogic 设置DHCP 方法
    private void resetTODhcp() {

        DhcpInfo dhcpInfo = new DhcpInfo();

        InetAddress ipaddr = NetworkUtils.numericToInetAddress("0.0.0.0");
        InetAddress getwayaddr = NetworkUtils.numericToInetAddress("0.0.0.0");
        InetAddress inetmask = NetworkUtils.numericToInetAddress("255.255.255.255");
        InetAddress idns1 = NetworkUtils.numericToInetAddress("0.0.0.0");
        InetAddress idns2 = NetworkUtils.numericToInetAddress("0.0.0.0");

        try {
            dhcpInfo.ipAddress = NetworkUtils.inetAddressToInt(ipaddr);
            dhcpInfo.gateway = NetworkUtils.inetAddressToInt(getwayaddr);
            dhcpInfo.netmask = NetworkUtils.inetAddressToInt(inetmask);
            dhcpInfo.dns1 = NetworkUtils.inetAddressToInt(idns1);
            dhcpInfo.dns2 = NetworkUtils.inetAddressToInt(idns2);
        } catch (IllegalArgumentException e) {

        }
        Log.i(TAG, "set ETHERNET_CONNECT_MODE_MANUAL -> " + dhcpInfo.toString());
        mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_MANUAL, dhcpInfo);
        mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_DHCP, null);
        mEthManager.setEthernetEnabled(false);
        mEthManager.setEthernetEnabled(true);

    }

    //返回结果的cmd命令
    private String playRunTime(String cmd) throws Exception {
        Process p = Runtime.getRuntime().exec(cmd);
        InputStream is = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            Log.i(TAG, "line:" + line);
            return line;
        }
        p.waitFor();
        is.close();
        reader.close();
        p.destroy();
        return null;
    }

}
