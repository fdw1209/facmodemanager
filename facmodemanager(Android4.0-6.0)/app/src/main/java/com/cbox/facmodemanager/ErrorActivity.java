package com.cbox.facmodemanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ErrorActivity extends Activity {
    public TextView tv1, tv2, tv3;
    private String errorcode, errorstate;

    //Timer
    private Timer mTimer;

    private static final String mURL = "http://127.0.0.1:18176/rpc";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final int CONNECT_TIMEOUT = 5;
    private static final int READ_TIMEOUT = 5;
    private static final int WRITE_TIMEOUT = 5;
    private static OkHttpClient mHttpClient = new OkHttpClient.Builder().readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS).connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS).build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        Log.i("pppp", "-----------in ErrorActivity onCreate-----------");
        tv1 = (TextView) findViewById(R.id.tv_1);
        tv2 = (TextView) findViewById(R.id.tv_2);
        tv3 = (TextView) findViewById(R.id.tv_3);
        errorcode = getIntent().getStringExtra("errorcode");

        //循环执行
        mTimer = new Timer();
        mTimer.schedule(timerTask, 0, 500);

//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.intent.action.PACKAGE_REMOVED");
//        filter.addDataScheme("package");
//        registerReceiver(AppRemovedReceiver, filter);

    }

    private BroadcastReceiver AppRemovedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getData().getSchemeSpecificPart();
//            if (packageName.equals("com.changhong.factorymode")) {
//                Log.i("pppp", "--------in ErrorActivity Receiver------------");
//                SystemProperties.set("persist.sys.factorymodestate", "0");
//                Log.i("pppp", " factorymode state>>>0 setted");
//                //同步
//                docmd("sync");
//                Log.i("pppp", "sync6 done unintalled success");
//                Log.i("pppp ", "removed do kill");
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
//                                Log.i("pppp", "----------in okhttp----- fail");
//                                SystemProperties.set("sys.factoryErrorstate", "1");
//                                Message msg = new Message();
//                                msg.what = 1;
//                                handler.sendMessage(msg);
//                            } else {
//
//                                String res = response.body().string();
//
//                                try {
//                                    JSONObject obj = new JSONObject(res);
//                                    SystemProperties.set("sys.factoryErrorstate", "0");
//                                    Message msg = new Message();
//                                    msg.what = 1;
//                                    handler.sendMessage(msg);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                    SystemProperties.set("sys.factoryErrorstate", "2");
//                                    Message msg = new Message();
//                                    msg.what = 1;
//                                    handler.sendMessage(msg);
//                                }
//                            }
//                            } catch(IOException e){
//                                e.printStackTrace();
//                                SystemProperties.set("sys.factoryErrorstate", "3");
//                            Message msg = new Message();
//                            msg.what = 1;
//                            handler.sendMessage(msg);
//                            }
//
//
//                        //httppost();
//                    }
//                }).start();
//            }
        }
    };

    //循环执行
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Message msg1 = new Message();
            msg1.what = 1;
            handler.sendMessage(msg1);
        }
    };

    Handler handler = new Handler() {
      public void handleMessage(Message msg){
          errorstate = SystemProperties.get("sys.factoryErrorstate");
          Log.i("pppp", " ------chuli--------->errcode>>>" + errorstate);
              switch (msg.what) {
                  case 1:
                      if (errorstate.equals("0")) {
                          mTimer.cancel();
                          //写入标志位成功
                          tv1.setText(getResources().getString(R.string.ok));
                          tv1.setTextColor(getResources().getColor(R.color.colorAccent3));
                      } else if (errorstate.equals("1")) {
                          mTimer.cancel();
                          //写入失败，错误码1
                          //显示有问题机器
                          tv1.setText(getResources().getString(R.string.wentiji));
                          tv1.setTextColor(getResources().getColor(R.color.colorAccent1));
                          //显示错误码
                          tv2.setVisibility(View.VISIBLE);
                          tv3.setVisibility(View.VISIBLE);
                          tv3.setText(getResources().getString(R.string.errorcode1));
                      } else if (errorstate.equals("2")) {
                          mTimer.cancel();
                          //写入失败，错误码2
                          //显示有问题机器
                          tv1.setText(getResources().getString(R.string.wentiji));
                          tv1.setTextColor(getResources().getColor(R.color.colorAccent1));
                          //显示错误码
                          tv2.setVisibility(View.VISIBLE);
                          tv3.setVisibility(View.VISIBLE);
                          tv3.setText(getResources().getString(R.string.errorcode2));
                      } else if (errorstate.equals("3")) {
                          mTimer.cancel();
                          //写入失败，错误码3
                          //显示有问题机器
                          tv1.setText(getResources().getString(R.string.wentiji));
                          tv1.setTextColor(getResources().getColor(R.color.colorAccent1));
                          //显示错误码
                          tv2.setVisibility(View.VISIBLE);
                          tv3.setVisibility(View.VISIBLE);
                          tv3.setText(getResources().getString(R.string.errorcode3));
                      } else if (errorstate.equals("4")) {
                          mTimer.cancel();
                          //写入失败，错误码4
                          //显示有问题机器
                          tv1.setText(getResources().getString(R.string.wentiji));
                          tv1.setTextColor(getResources().getColor(R.color.colorAccent1));
                          //显示错误码
                          tv2.setVisibility(View.VISIBLE);
                          tv3.setVisibility(View.VISIBLE);
                          tv3.setText(getResources().getString(R.string.errorcode4));
                      }
                      else if (errorstate.equals("5")) {
                          mTimer.cancel();
                          //写入失败，错误码4
                          //显示有问题机器
                          tv1.setText(getResources().getString(R.string.wentiji));
                          tv1.setTextColor(getResources().getColor(R.color.colorAccent1));
                          //显示错误码
                          tv2.setVisibility(View.VISIBLE);
                          tv3.setVisibility(View.VISIBLE);
                          tv3.setText(getResources().getString(R.string.errorcode5));
                      }

                      break;
                  default:
                      break;
              }
          }

    };

    private void httppost() {
        //xutils
        //setValue
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("method", "setValue");
            //params
            JSONObject obj2 = new JSONObject();
            obj2.put("key", "fact_uninstalled");
            obj2.put("value", "true");
            jsonObject.put("params", obj2);
            jsonObject.put("id", 1);

        } catch (JSONException e) {

        }

        //httpPostSetValue();

        //getValue
//                JSONObject jsonObject1 = new JSONObject();
//                try{
//                    jsonObject1.put("jsonrpc", "2.0");
//                    jsonObject1.put("method", "getValue");
//                    //params
//                    JSONObject obj3 = new JSONObject();
//                    obj3.put("key", "fact_uninstalled");
//                    jsonObject1.put("params", obj3);
//                    jsonObject1.put("id", 1);
//                }catch (JSONException e){
//
//                }

        String interfaceUrl = "http://127.0.0.1:18176/rpc";
        RequestParams params = new RequestParams(interfaceUrl);
        params.setMaxRetryCount(3);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());

        //params.setBodyContent(jsonObject1.toString());

        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("pppp", "-------------------------writehao success, result------------");
                Log.i("pppp", "result:" + result);
                try {
                    if (result != null) {
                        JSONObject resultObject = new JSONObject(result);
                        int writeresult = (int) resultObject.get("result");
                        Log.i("pppp", "writereslut is " + writeresult);
                        if (writeresult == 0) {
                            //success
                            //docmd("sync");
                            //showError("success", context);
                            //SystemProperties.set("sys.factoryErrorstate", "0");
                            //写入标志位成功
                            tv1.setText(getResources().getString(R.string.ok));
                            tv1.setTextColor(getResources().getColor(R.color.colorAccent3));
                        } else {

                            //showError(context.getResources().getString(R.string.errorcode4), context);
                            //SystemProperties.set("sys.factoryErrorstate", "1");
                            //写入失败，错误码1
                            //显示有问题机器
                            tv1.setText(getResources().getString(R.string.wentiji));
                            tv1.setTextColor(getResources().getColor(R.color.colorAccent1));
                            //显示错误码
                            tv2.setVisibility(View.VISIBLE);
                            tv3.setVisibility(View.VISIBLE);
                            tv3.setText(getResources().getString(R.string.errorcode1));
                        }

                    }else {
                        //result = null
                        //SystemProperties.set("sys.factoryErrorstate", "5");
                        //写入失败，错误码4
                        //显示有问题机器
                        tv1.setText(getResources().getString(R.string.wentiji));
                        tv1.setTextColor(getResources().getColor(R.color.colorAccent1));
                        //显示错误码
                        tv2.setVisibility(View.VISIBLE);
                        tv3.setVisibility(View.VISIBLE);
                        tv3.setText(getResources().getString(R.string.errorcode5));
                    }

                } catch (JSONException e) {
                    //调用了错误方法
                    Log.i("pppp", "in JSONException e>>>" + e.toString());
//                            Toast.makeText(context, context.getResources().getString(
//                                             R.string.toast_write_fail) + "， 错误码：1",
//                                             Toast.LENGTH_LONG).show();
                    //打开ErrorActivity
                    //showError(context.getResources().getString(R.string.errorcode1), context);
                    //SystemProperties.set("sys.factoryErrorstate", "2");

                    //写入失败，错误码2
                    //显示有问题机器
                    tv1.setText(getResources().getString(R.string.wentiji));
                    tv1.setTextColor(getResources().getColor(R.color.colorAccent1));
                    //显示错误码
                    tv2.setVisibility(View.VISIBLE);
                    tv3.setVisibility(View.VISIBLE);
                    tv3.setText(getResources().getString(R.string.errorcode2));
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("pppp", "setValue failed , in onError>>>" + ex.toString());
//                        Toast.makeText(context, context.getResources().getString(
//                                R.string.toast_write_fail) + "， 错误码：2",
//                                Toast.LENGTH_LONG).show();
                //showError(context.getResources().getString(R.string.errorcode2), context);
                //SystemProperties.set("sys.factoryErrorstate", "3");
                //写入失败，错误码3
                //显示有问题机器
                tv1.setText(getResources().getString(R.string.wentiji));
                tv1.setTextColor(getResources().getColor(R.color.colorAccent1));
                //显示错误码
                tv2.setVisibility(View.VISIBLE);
                tv3.setVisibility(View.VISIBLE);
                tv3.setText(getResources().getString(R.string.errorcode3));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("pppp", "setValue failed , in onCancelled>>>" + cex.toString());
//                        Toast.makeText(context, context.getResources().getString(
//                                R.string.toast_write_fail) + "错误码：3",
//                                Toast.LENGTH_LONG).show();
                //showError(context.getResources().getString(R.string.errorcode3), context);
                //SystemProperties.set("sys.factoryErrorstate", "4");
                //写入失败，错误码4
                //显示有问题机器
                tv1.setText(getResources().getString(R.string.wentiji));
                tv1.setTextColor(getResources().getColor(R.color.colorAccent1));
                //显示错误码
                tv2.setVisibility(View.VISIBLE);
                tv3.setVisibility(View.VISIBLE);
                tv3.setText(getResources().getString(R.string.errorcode4));
            }

            @Override
            public void onFinished() {
                Log.i("pppp", "in onFinished");
            }

            @Override
            public boolean onCache(String result) {
                Log.i("pppp", "in onCache");
                return false;
            }
        });
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
                Log.i("pppp", "----------in okhttp----- fail");
                SystemProperties.set("sys.factoryErrorstate", "1");
               Message msg = new Message();
                msg.what = 1;

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

    @Override
    protected void onResume() {
        super.onResume();
//        errorcode = getIntent().getStringExtra("errorcode");
//        Log.i("pppp", "---------------in ErrorActivity--------");
//        Log.i("pppp", " errorcode is>>>" + errorcode);
//        if (errorcode != null){
//            if (errorcode.equals("success")){
//                //显示已退出工厂菜单
//                tv1.setText(getResources().getString(R.string.ok));
//                tv1.setTextColor(getResources().getColor(R.color.colorAccent3));
//            } else {
//                //显示有问题机器
//                tv1.setText(getResources().getString(R.string.wentiji));
//                tv1.setTextColor(getResources().getColor(R.color.colorAccent1));
//                //显示错误码
//                tv2.setVisibility(View.VISIBLE);
//                tv3.setVisibility(View.VISIBLE);
//                tv3.setText(errorcode);
//            }
//
//        }
    }

    @Override
    protected void onDestroy() {
        //停止服务
        //注销广播
        unregisterReceiver(AppRemovedReceiver);
        super.onDestroy();
    }


}
