package com.cbox.facmodemanager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.DhcpInfo;
import android.net.NetworkUtils;
import android.net.ethernet.EthernetManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.Toast;

import com.cbox.facmodemanager.utils.PackageUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.WIFI_SERVICE;

public class MountedReceiver extends BroadcastReceiver {
    private final static String TAG = "CH_JM_FM";
    private Context mContext;
    private String path = null;
    private String progress, market, MAC, MACChanged;
    private Timer mTimer, mTimer2;
    private WifiManager mWifiManager;
    //蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    private EthernetManager mEthManager;

    private String mount, CHX_mount;
    private List<ResolveInfo> apps = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {

        mount = SystemProperties.get("sys.factory.mount");
        CHX_mount = SystemProperties.get("sys.chx.mount");

        mContext = context;
        Log.i(TAG, " -------------------------------------------------");
        Log.i(TAG, " media mounted");
        Log.i(TAG, " -------------------------------------------------");
        //SystemProperties.set("sys.limit.install.app", "false");
        progress = SystemProperties.get("persist.sys.factory.progress");
        market = SystemProperties.get("ro.product.ch.target");
        MAC = systemProperiesInvoke.get(mContext, "ro.mac");
        Log.i(TAG, "mac:" + MAC);
        MACChanged = exChange(MAC);

        //蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //wifiManager
        mWifiManager = (WifiManager) mContext.getSystemService(WIFI_SERVICE);
        //EtherManager
        mEthManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);

        //循环执行
        mTimer = new Timer();
        //0.5s响一次,启动Timer
        //mTimer.schedule(timerTask, 0, 500);

        mTimer2 = new Timer();

        //已安装工厂菜单的情况下 需要更新版本时
//        if (isAppInstalled(mContext, "com.changhong.factorymode")) {
//            ArrayList<StorageItem> list1 = getMounted();
//            if (list1 != null && list1.size() > 0) {
//                for (int i = 0; i < list1.size(); i++) {
//                    String path1 = list1.get(i).path;
//                    Log.i(TAG, "path  = " + path1);
//                    Log.i(TAG, "newfactory ex?>>>" + NewFacIsExists(path1) + "");
//                    if (NewFacIsExists(path1)) {
//                        path = path1;
//                        docmd("pm install -r " + path1 + "/factory/NewFactoryMode.apk");
//                        return;
//                    }
//                }
//            }
//        }

        ArrayList<StorageItem> list = getMounted();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String path1 = list.get(i).path;
                Log.i(TAG, "path  = " + path1);
                Log.i(TAG, "filesocket ex?>>>" + SocketIsExists(path1) + "");
                if (SocketIsExists(path1)) {
                    path = path1;
                    //设置属性，表明sockettest存在
                    SystemProperties.set("sys.sockettest", "1");
                }
            }
        }
        String socket = SystemProperties.get("sys.sockettest");
        Log.i(TAG, "sockettest is in u ???" + socket);
        if (socket.equals("1")) {
            //sys.mmount 为1表示U盘中没有socketTest而盒子中有，则卸载它
            //开启读取sys.mount
            mTimer2.schedule(timerTask2, 0, 500);
            //打开wifi
            mWifiManager.setWifiEnabled(true);
            Log.i(TAG, "wifi enabled");
            Toast.makeText(mContext, "SocketTest in U>>>Start SocketTest", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "socket is not in u");
            Log.i(TAG, "socket installed??? " + isSocketAppInstalled(mContext, "com.henggucn.installtest.sockettest"));
            if (isSocketAppInstalled(mContext, "com.henggucn.installtest.sockettest")) {
                //Log.i("pppp>>>socket", "socket is not in u | socket is installed");
                //执行一次卸载sockettest操作
                Log.i(TAG, "do uninstall sockettest");
                //docmd("rm /data/app/SocketTest.apk");
                //docmd("pm uninstall com.henggucn.installtest.sockettest");
                PackageManager pm = mContext.getPackageManager();

                Class<?>[] types = new Class[] {String.class, IPackageDeleteObserver.class, int.class};
                Method method = null;
                try {
                    method = pm.getClass().getMethod("deletePackage", types);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                try {
                    method.invoke(pm, new Object[] {"com.henggucn.installtest.sockettest", null, 0});
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Uninstall result Success!!!!");
                Log.i(TAG, "set sys.mount -->1");
                //sys.mmount 为1表示U盘中没有socketTest而盒子中有，则卸载它
                SystemProperties.set("sys.mount", "1");
            }


//            if (!isAppInstalled(mContext, "com.changhong.factorymode")) {
//                //系统启动时判断是否安装了工厂菜单
//                //如果安装了 则启动 工厂菜单已安装的逻辑在MyService里执行
//                //如果没安装则调用U盘安装的命令
//                if (!SystemProperties.get("sys.install.flag").equals("1")) {
//                    //ArrayList<StorageItem> list = getMounted();
//                    if (list != null && list.size() > 0) {
//                        for (int i = 0; i < list.size(); i++) {
//                            String path = list.get(i).path;
//                            Log.i(TAG, "i = " + i + "path  = " + path);
//                            Log.i(TAG, "file ex?>>>" + fileIsExists(path) + "");
//                            Log.i(TAG, " mount >>>" + mount);
//                            Log.i(TAG, "chx_mount>>>" + CHX_mount);
//                            if (fileIsExists(path) && !mount.equals("true")) {
//                                Log.i(TAG, " file in && mount != true");
//                                docmd("cp " + path + "/factory/FactoryMode.apk " + "/data/app");
//                                docmd("chmod 644 /data/app" + "/FactoryMode.apk");
//                                //docmd("pm install /data/app" + "/FactoryMode.apk");
//
//                                Log.i(TAG, "fm>unin u>in do in");
//                                SystemProperties.set("sys.factory.mount", "true");
//                            } else if (!CHX_mount.equals("true")){
//                                String path2 = path + "/factory";
//                                Log.i(TAG, "in elseeee path2: " + path2 + "i = " + i);
//                                File[] files = getFiles(path2);
//                                if (files != null) {
//                                    Log.i(TAG, "in elseeee files.lenth:" + files.length);
//                                    for (int j = 0; j < files.length; j++) {
//                                        if (files[j].getName().indexOf("CHX_") != -1) {
//
//                                            Log.i(TAG, " CHX_ File：" + files[j].getAbsolutePath() + "\n" +
//                                                    "CHX_name：" + files[j].getName());
//                                            docmd("cp " + files[j].getAbsolutePath() + " /data/app");
//                                            docmd("chmod 644 /data/app/" + files[j].getName());
//                                            docmd("pm install /data/app/" + files[i].getName());
//
//                                            SystemProperties.set("sys.chx.mount", "true");
//                                        }
//                                    }
//                                }
//                            }
//
//                            //Toast.makeText(mContext, "正在安装工厂菜单，请等待启动", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                } else if (SystemProperties.get("sys.install.flag").equals("1")) {
//                    Log.i(TAG, "factory is done and uninstalled");
//
//                    if (list != null && list.size() > 0) {
//                        for (int i = 0; i < list.size(); i++) {
//                            String path = list.get(i).path;
//                            Log.i(TAG, "i = " + i + "path  = " + path);
//                            Log.i(TAG, "file ex?>>>" + fileIsExists(path) + "");
//                            Log.i(TAG, " mount >>>" + mount);
//                            Log.i(TAG, "chx_mount>>>" + CHX_mount);
//                            if (!CHX_mount.equals("true")) {
//                                String path2 = path + "/factory";
//                                Log.i(TAG, "in flag=1 path2: " + path2 + "i = " + i);
//                                File[] files = getFiles(path2);
//                                if (files != null) {
//                                    Log.i(TAG, "in flag=1 files.lenth:" + files.length);
//                                    for (int j = 0; j < files.length; j++) {
//                                        if (files[j].getName().indexOf("CHX_") != -1) {
//                                            Log.i(TAG, " CHX_ File：" + files[j].getAbsolutePath() + "\n");
//                                            docmd("cp " + files[j].getAbsolutePath() + " /data/app");
//                                            docmd("chmod 644 /data/app/" + files[j].getName());
//                                            docmd("pm install /data/app/" + files[i].getName());
//                                            SystemProperties.set("sys.chx.mount", "true");
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        }
    }

    //循环执行1
    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            Message msg1 = new Message();
            msg1.what = 1;
            handler.sendMessage(msg1);
        }
    };
    //循环执行2 查看卸载操作是否完成
    TimerTask timerTask2 = new TimerTask() {

        @Override
        public void run() {
            Message msg1 = new Message();
            msg1.what = 2;
            handler.sendMessage(msg1);
        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Log.i(TAG, "handling message xxxxxxx");
            switch (msg.what) {
                //case1 并不会执行
//                case 1:
//                    progress = SystemProperties.get("persist.sys.factory.progress");
//                    Log.i(TAG, progress);
//                    if (progress.equals("1")) {
//                        //暂停timertask
//                        mTimer.cancel();
//                        //同步
//                        docmd("sync");
//                        Log.i(TAG, "sync1 done");
//
//                        Log.i(TAG, "progress1 = 1");
//                        //Toast.makeText(mContext, "正在卸载工厂菜单", Toast.LENGTH_SHORT).show();
//                        //关闭WiFi
//                        mWifiManager.setWifiEnabled(false);
//                        //关闭蓝牙
//                        mBluetoothAdapter.disable();
//                        //设置 persist.sys.factory.progress为2.
//                        SystemProperties.set("persist.sys.factory.progress", "2");
//                        progress = SystemProperties.get("persist.sys.factory.progress");
//                        //销毁工厂菜单
//                        //第一种方法
//                        docmd("pm uninstall com.changhong.factorymode");
//                        Log.i(TAG, "ps=1 fm>uninstall isdone");
//                        SystemProperties.set("sys.install.flag", "1");
//
//                        //同步
//                        docmd("sync");
//                        Log.i(TAG, "sync2 done");
//                        switch (market) {
//                            case "gdmobile":
//                                Log.i(TAG, "market is gdmobile");
//
//                                //需要循环执行的代码
//                                //设置IPOE方式
//
//                                //MSTAR 设置IPOE方式
//                                SystemProperties.set("persist.sys.isipoe", "true");
//                                //设置账号
//                                SystemProperties.set("persist.sys.bytuetech.username", MACChanged);
//                                //设置密码
//                                SystemProperties.set("persist.sys.bytuetech.password", "GdMCC68@OTV");
//
//                                //AMLOGIC 设置IPOE方式
//
////                                EthernetDevInfo info1 = new EthernetDevInfo();
////                                info1.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_DHCP_AUTH);
////                                info1.setIfName("eth0");
////                                info1.setIpAddress(null);
////                                info1.setRouteAddr(null);
////                                info1.setDnsAddr(null);
////                                info1.setNetMask(null);
////                                mEthManager.setAuthState(MACChanged, "GdMCC68@OTV", null, true);
////                                //	mEthManager.setAuthState(name + "@iptv.ln", password, null, true);
////                                mEthManager.updateEthDevInfo(info1);
//
//                                Log.i(TAG, "ipoe is done");
//
//                                //同步
//                                docmd("sync");
//                                Log.i(TAG, "sync3 done");
//                                break;
//                            case "scmobile":
//                                Log.i(TAG, "market is scmobile");
//
//                                //需要循环执行的代码
//                                //设置IPOE方式
//
//                                //MSTAR 设置IPOE方式
//                                SystemProperties.set("persist.sys.isipoe", "true");
//                                //设置账号
//                                SystemProperties.set("persist.sys.bytuetech.username", MACChanged);
//                                //设置密码
//                                SystemProperties.set("persist.sys.bytuetech.password", "ScMCC68@OTV");
//
//                                //AMLOGIC 设置IPOE方式
////                                EthernetDevInfo info2 = new EthernetDevInfo();
////                                info2.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_DHCP_AUTH);
////                                info2.setIfName("eth0");
////                                info2.setIpAddress(null);
////                                info2.setRouteAddr(null);
////                                info2.setDnsAddr(null);
////                                info2.setNetMask(null);
////                                mEthManager.setAuthState(MACChanged, "ScMCC68@OTV", null, true);
////                                //	mEthManager.setAuthState(name + "@iptv.ln", password, null, true);
////                                mEthManager.updateEthDevInfo(info2);
//                                Log.i(TAG, "ipoe is done");
//
//                                //同步
//                                docmd("sync");
//                                Log.i(TAG, "sync4 done");
//                                break;
//                            case "public":
//                                Log.i(TAG, "market is public");
//                                //设置为DHCP方式
//
//                                // Mstar设置DHCP方法
//                                SystemProperties.set("persist.sys.isipoe", "false");
//
//                                //Amlogic设置DHCP方法
//                                resetTODhcp();
//
//                                //同步
//                                docmd("sync");
//                                Log.i(TAG, "sync5 done");
//                                break;
//                            default:
//                                Log.i(TAG, "market is default");
//                                //设置为DHCP方式
//
//                                // Mstar设置DHCP方法
//                                SystemProperties.set("persist.sys.isipoe", "false");
//
//                                //Amlogic设置DHCP方法
//                                resetTODhcp();
//
//                                //同步
//                                docmd("sync");
//                                Log.i(TAG, "sync6 done");
//                                break;
//
//                        }
//
//                    }
//                    break;
                case 2:
                    String mountState = SystemProperties.get("sys.mount");
                    Log.i(TAG, " sys.mount is -----> " + mountState);
                    if (!mountState.equals("1")) {
                        Log.i(TAG, "sys.mount != 1");
                        mTimer2.cancel();

                        if (isSocketAppInstalled(mContext, "com.henggucn.installtest.sockettest")) {
                            Log.i(TAG, " socket installed start ");
                            Intent intent1 = new Intent();
                            intent1.setComponent(new ComponentName("com.henggucn.installtest.sockettest", "com.henggucn.installtest.sockettest.SocketActivity"));
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                mContext.startActivity(intent1);
                                Log.i(TAG, "socket installed do launch");
                            } catch (Exception e) {
                                Log.i(TAG, "launch failed>>>" + e.toString());
                            }

                        } else {
                            //安装并启动sockettest
                            Log.i(TAG, "Path:"+ path +"!!!!!!!!!!!!!!!!");
                            docmd("mount -o remount /system");
                            //docmd("cp /storage/sda1/factory/SocketTest.apk /data/app/");
                            //String result1=docmd("cp " + path + "/factory/SocketTest.apk " + "/data/app");
                            //Log.i(TAG,"!!!!!!!!Copy Result: " + result1);
                            //docmd("chmod 644 /data/app" + "/SocketTest.apk");
                            int result = PackageUtils.install(mContext,path+"/factory/SocketTest.apk");
                            //String result = docmd("pm install -r /data/app" + "/SocketTest.apk");
                            Log.i(TAG,"!!!!!!!!Inatall Result: " + result);
                            Log.i(TAG, "socket exists | do in");
                        }
                    }
                    break;
                default:
                    break;
            }

        }

    };

    //U盘检测
    private ArrayList<StorageItem> getMounted() {

        ArrayList<StorageItem> list = (ArrayList<StorageItem>) StorageUtil.listAllStorage(mContext);

        return list;

    }

    //运行cmd命令
    private String docmd(String keyCommand) {
        StringBuilder sb =new StringBuilder();
        try {
            Process process=Runtime.getRuntime().exec(keyCommand);
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while((line=bufferedReader.readLine())!=null)
            {
                sb.append(line+"\n");
            }
        } catch (Exception e) {
            return e.toString();
        }
        return sb.toString();
//        try {
//            Runtime runtime = Runtime.getRuntime();
//            runtime.exec(keyCommand);
//            Log.i(TAG,"Success!!!!!!");
//            Log.i("zendd>>>JayChou", "success");
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            Log.i(TAG,"failed!!!!!!");
//            Log.i("zendd>>>JayChou", "failed");
//            Log.i("TAG", e.toString());
//        }
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

    /**
     * 根据包名判断工厂菜单应用是否已安装
     */

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
            //开机这个文件一直存在， 不能那它来判断
//            file= new File("/private/symbol_fact_apks_installed");
//            if(!file.exists())
//            {
//                Log.i(TAG,TAG+"/private/symbol_fact_apks_installed not exist");
//                return true;
//            }
            return false;
        } else {

            return true;
        }
    }

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
            Log.e("pppp ---in receiver---", name + "----" + packageName + "----" + cls);
        }
    }


    //判断FactoryMode文件是否存在
    public boolean fileIsExists(String pathh) {
        try {
            File f = new File(pathh + "/factory/FactoryMode.apk");
            f.getAbsolutePath();
            Log.i(TAG, "file path >>>" + f.getAbsolutePath());
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

    //判断NewFactoryMode文件是否存在
    public boolean NewFacIsExists(String pathh) {
        try {
            File f = new File(pathh + "/factory/NewFactoryMode.apk");
            f.getAbsolutePath();
            Log.i(TAG, "file path >>>" + f.getAbsolutePath());
            if (!f.exists()) {
                Log.i(TAG, "NewFac not exist");
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        Log.i(TAG, "NewFac exists");
        return true;
    }

    //判断SocketTest文件是否存在
    public boolean SocketIsExists(String pathh) {
        try {
            File f = new File(pathh + "/factory/SocketTest.apk");
            f.getAbsolutePath();
            Log.i(TAG, "file path >>>" + f.getAbsolutePath());
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

    public File[] getFiles(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        return files;
    }

}
