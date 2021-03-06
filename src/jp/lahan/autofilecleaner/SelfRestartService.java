package jp.lahan.autofilecleaner;

import jp.lahan.autofilecleaner.DirDB.DataColumns;
import jp.lahan.autofilecleaner.FileCleaner.FileCleanException;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
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
		}else if(type.equals("register")){
			registerService();
		}
	}

	private void startService() {		
		// データベースアクセス
		DirDB dirDB = new DirDB(SelfRestartService.this);
				
		// データベース内容でループ
		Cursor c = dirDB.query(null, null, null, null);			
		if(c.moveToFirst()){
			do{
				//final String id =  c.getString(c.getColumnIndex(DataColumns._ID));
				String dir = c.getString(c.getColumnIndex(DataColumns.DIR));
				int fileNum = c.getInt(c.getColumnIndex(DataColumns.FILE_NUM));
				
				// ファイル削除
				try {
					final int deleteNum = FileCleaner.clean(dir, fileNum);
					System.out.println(dir + " delete " + deleteNum + " files.");
					Toast.makeText(SelfRestartService.this, dir + " delete " + deleteNum + " files.", Toast.LENGTH_LONG).show();					
				} catch (final FileCleanException e) {
					e.printStackTrace();
					Toast.makeText(SelfRestartService.this, "error : " + e.getMessage(), Toast.LENGTH_LONG).show();					
				}				
			}while(c.moveToNext());
		}			
		c.close();
		dirDB.close();			
		
		registerService();	
	}

	private void registerService() {
		// 再実行
		Intent intent = new Intent(SelfRestartService.this, SelfRestartService.class);
		intent.putExtra("type", "start"); 		// typeは必ず設定する
		PendingIntent alarmSender =PendingIntent.getService(SelfRestartService.this, 0, intent, 0); 
		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
		long now = System.currentTimeMillis();
		manager.set(AlarmManager.RTC, now + 12 * 60 * 60 * 1000, alarmSender);	// 12時間に一度に設定
		SelfRestartService.this.stopSelf();
	}

	
	public void cancelService(){
		Toast.makeText(SelfRestartService.this, R.string.stopServiceString, Toast.LENGTH_SHORT).show();
		
		PendingIntent alarmSender = PendingIntent.getService(SelfRestartService.this, 0,
				new Intent(SelfRestartService.this, SelfRestartService.class), 0);
		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
		manager.cancel(alarmSender);				
	}
	
}
