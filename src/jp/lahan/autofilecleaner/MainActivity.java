package jp.lahan.autofilecleaner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);

    	((Button)this.findViewById(R.id.startButtonID)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("activity");
				
				Intent intent = new Intent(MainActivity.this, SelfRestartService.class); 
				intent.putExtra("type", "start");
				startService(intent);
				
				Toast.makeText(MainActivity.this, R.string.hello, Toast.LENGTH_SHORT).show();
			}
		});

    	((Button)this.findViewById(R.id.stopServiceButton)).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				System.out.println("stop service");
				
				Intent intent = new Intent(MainActivity.this, SelfRestartService.class);
				intent.putExtra("type", "cancel");
				startService(intent);
			}
		});
    }
}