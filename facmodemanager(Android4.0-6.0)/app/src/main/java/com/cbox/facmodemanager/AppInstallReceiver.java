package com.cbox.facmodemanager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemProperties;
import android.util.Log;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by RyanJ on 2017/11/20.
 */
public class AppInstallReceiver extends BroadcastReceiver {

    private final static String TAG = "CH_JM_FM";
    private static final String mURL = "http://127.0.0.1:18176/rpc";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final int CONNECT_TIMEOUT = 5;
    private static final int READ_TIMEOUT = 5;
    private static final int WRITE_TIMEOUT = 5;
    private static OkHttpClient mHttpClient = new OkHttpClient.Builder().readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS).connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS).build();

    @Override
    public void onReceive(final Context context, Intent intent) {
        PackageManager manager = context.getPackageManager();

        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {

            String packageName = intent.getData().getSchemeSpecificPart();
            //Toast.makeText(context, "安装成功"+packageName, Toast.LENGTH_LONG).show();
            if (packageName.equals("com.henggucn.installtest.sockettest")) {
                Intent intent1 = new Intent();
                intent1.setComponent(new ComponentName("com.henggucn.installtest.sockettest", "com.henggucn.installtest.sockettest.SocketActivity"));
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    context.startActivity(intent1);
                    Log.i(TAG, "added try launch sockettest");

                } catch (Exception e) {
                    Log.i(TAG, "launch failed>>>" + e.toString());
                }
                Log.i(TAG, "added do launch");
            }
//            if (packageName.equals("com.changhong.factorymode")) {
//                SystemProperties.set("sys.factory.mount", "false");
//                Intent intent1 = new Intent();
//                intent1.setComponent(new ComponentName("com.changhong.factorymode", "com.changhong.factorymode.MainActivity"));
//                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                try {
//                    context.startActivity(intent1);
//                    Log.i(TAG, "added try launch factorymode");
//                    //1.16 修改
//                    SystemProperties.set("sys.limit.install.app", "false");
//                    Log.i(TAG, "sys.limit.install.app >>> false");
//
//                    SystemProperties.set("persist.sys.factorymodestate", "1");
//                    Log.i(TAG, " factorymode state>>>1 setted");
//                    docmd("sync");
//                } catch (Exception e) {
//                    Log.i(TAG, "launch failed>>>" + e.toString());
//                }
//                Log.i(TAG, "added do launch");
//                //3.12 修改 在安装成功后将唐艳的那个属性重新设置 让她不要误会
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //okhttp
//                        JSONObject jsonObject = new JSONObject();
//                        try {
//
//                            jsonObject.put("jsonrpc", "2.0");
//                            jsonObject.put("method", "setValue");
//                            JSONObject obj2 = new JSONObject();
//                            obj2.put("key", "fact_uninstalled");
//                            obj2.put("value", "false");
//                            jsonObject.put("params", obj2);
//                            jsonObject.put("id", 1);
//
//                        } catch (JSONException e) {
//
//                        }
//                        String jsonStr = jsonObject.toString();
//
//                        RequestBody body = RequestBody.create(JSON, jsonStr);
//
//                        Request request = new Request.Builder().url(mURL).addHeader("content-type", "application/json;charset:utf-8")
//                                .post(body).build();
//
//                        try {
//                            Response response = mHttpClient.newCall(request).execute();
//                            if (!response.isSuccessful()) {
//                                Log.i(TAG, "---inAdd-------in okhttp----- fail");
//
//
//                            } else {
//
//                                String res = response.body().string();
//
//                                try {
//                                    JSONObject obj = new JSONObject(res);
//                                    Log.i(TAG, "---inAdd-------in okhttp----- success");
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                    Log.i(TAG, "---inAdd-------in okhttp----- exception1");
//
//                                }
//                            }
//                        } catch(IOException e){
//                            e.printStackTrace();
//                            Log.i(TAG, "---inAdd-------in okhttp----- exception2");
//
//                        }
//
//
//                        //httppost();
//                    }
//                }).start();
//
//            }
            if (packageName.equals("com.changhong.packetcapturetool")) {
                Intent intent1 = new Intent();
                intent1.setComponent(new ComponentName("com.changhong.packetcapturetool", "com.changhong.packetcapturetool.MainActivity"));
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    context.startActivity(intent1);
                    Log.i(TAG, "added try launch packetcapturetool");
                } catch (Exception e) {
                    Log.i(TAG, "launch failed>>>" + e.toString());
                }
                Log.i(TAG, "added do launch");
            }
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {

            String packageName = intent.getData().getSchemeSpecificPart();
            Log.i(TAG, "!!!!!!!!!!!!!!!!! moved" + packageName);
            //Toast.makeText(context, "卸载成功"+packageName, Toast.LENGTH_LONG).show();
//            if (packageName.equals("com.changhong.factorymode")) {
//                //4.13修改 为了防止收到广播迅速关机的情况下 pm uninstall 没有完成操作的情况
//                docmd("rm -rf com.changhong.factorymode*");
//                docmd("rm -rf FactoryMode.apk");
//                SystemProperties.set("persist.sys.factorymodestate", "0");
//                Log.i(TAG, " factorymode state>>>0 setted");
//                //同步
//                docmd("sync");
//                Log.i(TAG, "sync6 done unintalled success");
//                Log.i(TAG, "removed do kill");
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //okhttp
//                        JSONObject jsonObject = new JSONObject();
//                        try {
//
//                            jsonObject.put("jsonrpc", "2.0");
//                            jsonObject.put("method", "setValue");
//                            JSONObject obj2 = new JSONObject();
//                            obj2.put("key", "fact_uninstalled");
//                            obj2.put("value", "true");
//                            jsonObject.put("params", obj2);
//                            jsonObject.put("id", 1);
//
//                        } catch (JSONException e) {
//
//                        }
//                        String jsonStr = jsonObject.toString();
//
//                        RequestBody body = RequestBody.create(JSON, jsonStr);
//
//                        Request request = new Request.Builder().url(mURL).addHeader("content-type", "application/json;charset:utf-8")
//                                .post(body).build();
//
//                        try {
//                            Response response = mHttpClient.newCall(request).execute();
//                            if (!response.isSuccessful()) {
//                                Log.i(TAG, "----------in okhttp----- fail");
//                                SystemProperties.set("sys.factoryErrorstate", "1");
//
//                            } else {
//
//                                String res = response.body().string();
//
//                                try {
//                                    JSONObject obj = new JSONObject(res);
//                                    SystemProperties.set("sys.factoryErrorstate", "0");
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                    SystemProperties.set("sys.factoryErrorstate", "2");
//
//                                }
//                            }
//                        } catch(IOException e){
//                            e.printStackTrace();
//                            SystemProperties.set("sys.factoryErrorstate", "3");
//
//                        }
//
//
//                        //httppost();
//                    }
//                }).start();
//            }
            //卸载sockettest
            if (packageName.equals("com.henggucn.installtest.sockettest")) {
                //同步
                docmd("sync");
                Log.i(TAG, "sync6 done unintalled socket success");
                Log.i(TAG, "set sys.mount -->2");
                SystemProperties.set("sys.mount", "2");
                //Log.i(TAG, "removed do kill");
                //int pid = android.os.Process.myPid();
                //android.os.Process.killProcess(pid);

            }


        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            Log.i(TAG, "!!!!!!!!!!!!!!!!!in replaced");
//            String packageName = intent.getData().getSchemeSpecificPart();
//            //Toast.makeText(context, "替换成功"+packageName, Toast.LENGTH_LONG).show();
//            if (packageName.equals("com.changhong.factorymode")) {
//                Intent intent1 = manager.getLaunchIntentForPackage("com.changhong.factorymode");
//                try {
//                    context.startActivity(intent1);
//                    Log.i("pppp", "relaced try launch");
//                } catch (Exception e) {
//                    Log.i("pppp", "replaced launch failed>>>" + e.toString());
//                }
//                Log.i("pppp ", "replaced do launch");
//            }

        }


    }

    //发送okhttp请求
    /**
     * 发出httpPostSetValue请求，设置一个值给工厂菜单读
     */
    private static int httpPostSetValue() {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("method", "setValue");
            JSONObject obj2 = new JSONObject();
            obj2.put("key", "fact_uninstalled");
            obj2.put("value", "true");
            jsonObject.put("params", obj2);
            jsonObject.put("id", 1);

        } catch (JSONException e) {

        }
        String jsonStr = jsonObject.toString();

        RequestBody body = RequestBody.create(JSON, jsonStr);

        Request request = new Request.Builder().url(mURL).addHeader("content-type", "application/json;charset:utf-8")
                .post(body).build();

        try {
            Response response = mHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                Log.i(TAG, "----------in okhttp----- fail");
                SystemProperties.set("sys.factoryErrorstate", "1");
                return -1;
            }

            String res = response.body().string();

            try {
                JSONObject obj = new JSONObject(res);
                SystemProperties.set("sys.factoryErrorstate", "0");
                return obj.optInt("result");
            } catch (JSONException e) {
                e.printStackTrace();
                SystemProperties.set("sys.factoryErrorstate", "2");
            }

        } catch (IOException e) {
            e.printStackTrace();
            SystemProperties.set("sys.factoryErrorstate", "3");
        }

        return -1;
    }


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

    //showDialog
    private void showDialog(Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("提示！！！");
        dialog.setMessage("已退出工厂菜单，可以断电！！！");
        dialog.setCancelable(true);

        AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }

    //start Error Activity
    private void showError(String errorcode, Context context) {
        Intent intent = new Intent(context, ErrorActivity.class);
        intent.putExtra("errorcode", errorcode);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
