package com.cbox.facmodemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by RyanJ on 2017/11/17.
 */

public class BootReveiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //Log.d("pppp BootReceiver", "system boot completed");
            Log.d("CHANGHONG", "system boot completed");

            // context, AutoRun.class
            Intent newIntent = new Intent(context.getApplicationContext(), MyService.class);

            /* MyActivity action defined in AndroidManifest.xml */
            newIntent.setAction("android.intent.action.MAIN");

            /* MyActivity category defined in AndroidManifest.xml */
            newIntent.addCategory("android.intent.category.LAUNCHER");

            /*
             * If activity is not launched in Activity environment, this flag is
             * mandatory to set
             */
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            /* if you want to start a service, follow below method */
            context.startService(newIntent);

        }
    }
}

