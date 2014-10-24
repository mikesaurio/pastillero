package com.mikesaurio.pastillero.servicio;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class servicio_alarma extends Service {

	private static Timer timer = new Timer(); 
    private Context ctx;
	
    public void onCreate() 
    {
          super.onCreate();
          ctx = this; 
          startService();
    }

    private void startService()
    {           
        timer.scheduleAtFixedRate(new mainTask(), 0, 5000);
    }

    private class mainTask extends TimerTask
    { 
        public void run() 
        {
            toastHandler.sendEmptyMessage(0);
        }
    }    

    public void onDestroy() 
    {
          super.onDestroy();
          Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
    }

    private final Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
        }
    };   
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
