package com.todpop.sweetenglish;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

public class LockScreenService extends Service{
	public static boolean isRunning;
	
	BroadcastReceiver mReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate(){
		super.onCreate();
		
		isRunning = true;
		
		mReceiver = new LockScreenReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);
		
		if(Build.VERSION.SDK_INT >= 18){
	        Intent notificationIntent = new Intent(this, OpeningActivity.class);
	        PendingIntent pIntent = PendingIntent.getActivity(this, 0, notificationIntent , 0);
	        
			Notification notification;
			notification  = new Notification.Builder(getApplicationContext())
	        .setContentTitle(getResources().getString(R.string.app_name))
	        .setContentText("Lock Screen On")
	        .setSmallIcon(R.drawable.screenlock_3_btn_unlock_pressed)
	        .setContentIntent(pIntent).build();
			startForeground(1, notification);		
		}
		else{
			startForeground(1, new Notification());			
		}
		
		if(intent != null){
			if(intent.getAction() == null){
				if(mReceiver == null){
					mReceiver = new LockScreenReceiver();
					IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
					registerReceiver(mReceiver, filter);
				}
			}
		}
		return START_REDELIVER_INTENT;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		isRunning = false;
		
		if(mReceiver != null){
			unregisterReceiver(mReceiver);
		}
	}
}