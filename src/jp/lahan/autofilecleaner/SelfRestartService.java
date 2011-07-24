package jp.lahan.autofilecleaner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.widget.Toast;

public class SelfRestartService extends Service {	
	
	private final IBinder binder = new Binder(){
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
			return super.onTransact(code, data, reply, flags);
		};
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onStart(Intent intent, int startId) {
				
		String type = intent.getStringExtra("type");
		System.out.println(type);
		if(type.equals("start")){		
			startService();
		}else if(type.equals("cancel")){
			cancelService();
		}
	}

	private void startService() {
		Toast.makeText(SelfRestartService.this, R.string.startServiceString, Toast.LENGTH_SHORT).show();
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				long now = System.currentTimeMillis();
				Intent intent = new Intent(SelfRestartService.this, SelfRestartService.class);
				intent.putExtra("type", "start");		// これがないとonStart()でnull pointer
				PendingIntent alarmSender = PendingIntent.getService(SelfRestartService.this, 0, intent, 0);				
				AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
				manager.set(AlarmManager.RTC, now + 10000, alarmSender);
				SelfRestartService.this.stopSelf();
			}
		};
		new Thread(null, runnable, "AlarmService_Service").start();
	}
	
	public void cancelService(){
		Toast.makeText(SelfRestartService.this, R.string.stopServiceString, Toast.LENGTH_SHORT).show();
		
		PendingIntent alarmSender = PendingIntent.getService(SelfRestartService.this, 0,
				new Intent(SelfRestartService.this, SelfRestartService.class), 0);
		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
		manager.cancel(alarmSender);				
	}
}
