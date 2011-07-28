package jp.lahan.autofilecleaner;

import jp.lahan.autofilecleaner.DirDB.DataColumns;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private DirDB dirDB;	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	
    	// DB設定
    	initDB();    	
    	
    	// ボタン動作設定
    	setStartServiceButton();
    	setStopServiceButton();
    	setCheckDBButton();
    }

    private void initDB(){
    	dirDB = new DirDB(this);
    	
    	ContentValues values = new ContentValues();
    	values.put(DataColumns.DIR, "/sdcard/Tumblife/html/");
    	values.put(DataColumns.FILE_NUM, "300");
    	dirDB.insertWithCheck(values);
    	
    	values = new ContentValues();
    	values.put(DataColumns.DIR, "/sdcard/Tumblife/img/");
    	values.put(DataColumns.FILE_NUM, "300");
    	dirDB.insertWithCheck(values);
    	
    	dirDB.close();
    } 
    
	private void setCheckDBButton() {
		((Button)this.findViewById(R.id.checkDBButtonID)).setOnClickListener(new OnClickListener() {    					
			@Override
			public void onClick(View v) {
				int count = 0;
				Cursor c = dirDB.query(null, null, null, null);				
				if(c.moveToFirst()){
					do{
						Toast.makeText(MainActivity.this, 
										c.getString(c.getColumnIndex(DataColumns._ID)) + ", "
											+ c.getString(c.getColumnIndex(DataColumns.DIR)) + ", "
											+ c.getInt(c.getColumnIndex(DataColumns.FILE_NUM)),
										Toast.LENGTH_LONG).show();										
						count++;
					}while(c.moveToNext());
					Toast.makeText(MainActivity.this, "total items : " + count, Toast.LENGTH_LONG).show();
				}				
			}
		});
	}

	private void setStopServiceButton() {
		((Button)this.findViewById(R.id.stopServiceButtonID)).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
//				((Button)MainActivity.this.findViewById(R.id.stopServiceButtonID)).setClickable(false);
//				((Button)MainActivity.this.findViewById(R.id.startServiceButtonID)).setClickable(true);				
				
				new Thread(new Runnable() {					
					@Override
					public void run() {
						Intent intent = new Intent(MainActivity.this, SelfRestartService.class);
						intent.putExtra("type", "cancel");
						startService(intent);						
					}
				}).start();
			}
		});
	}

	private void setStartServiceButton() {
		((Button)this.findViewById(R.id.startServiceButtonID)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
//				((Button)MainActivity.this.findViewById(R.id.stopServiceButtonID)).setClickable(true);
//				((Button)MainActivity.this.findViewById(R.id.startServiceButtonID)).setClickable(false);
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(MainActivity.this, SelfRestartService.class); 
						intent.putExtra("type", "start");
						startService(intent);										
					}
				}).start();
			}
		});
	}
}