package com.todpop.sweetenglish;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class AlamReceiver extends BroadcastReceiver {
  
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("STEVEN", "receiver works");
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);

        boolean isScreenOn = pm.isScreenOn();
        if(isScreenOn==false)
        {

        	WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(20000);
            
            WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
            wl_cpu.acquire(20000);
        }
        
        Intent notificationIntent = new Intent(context, OpeningActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, notificationIntent , 0);
        
		Notification n;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			n  = new Notification.Builder(context)
	        .setContentTitle("SweetEnglish")
	        .setContentText("time to STUDY!!")
	        .setSmallIcon(R.drawable.screenlock_3_btn_unlock_normal)
	        .setContentIntent(pIntent).build();
		} else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			n  = new Notification.Builder(context)
	        .setContentTitle("SweetEnglish")
	        .setContentText("time to STUDY!!")
	        .setSmallIcon(R.drawable.screenlock_3_btn_unlock_normal)
	        .setContentIntent(pIntent).getNotification();
		}
		else{
			n = new Notification(R.drawable.screenlock_3_btn_unlock_normal, "SweetEnglish", System.currentTimeMillis());
		}
        
        n.defaults |= Notification.DEFAULT_SOUND;
        n.flags |= Notification.FLAG_AUTO_CANCEL;

        nm.notify(0, n);
    }
}
