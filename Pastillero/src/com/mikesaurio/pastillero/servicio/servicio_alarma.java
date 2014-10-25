package com.mikesaurio.pastillero.servicio;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.mikesaurio.pastillero.bd.DBHelper;
import com.mikesaurio.pastillero.bean.DatosBean;

public class servicio_alarma extends Service {

	private static Timer timer[];
	private DatosBean datosBean; 
	int val=0;
	
    public void onCreate() 
    {
          super.onCreate(); 
          cargarDatos();
          
    }

    private void startService() throws ParseException
    {          
    	timer = new Timer[datosBean.getId().length];
    	for( val=0;val<datosBean.getId().length;val++)	{	
    			Calendar now = Calendar.getInstance();
    			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
    			String fechaCel= now.get(Calendar.DAY_OF_MONTH)+"/"+((now.get(Calendar.MONTH))+1)+"/"+now.get(Calendar.YEAR)+
    					" "+now.get(Calendar.HOUR_OF_DAY)+":"+now.get(Calendar.MINUTE)+":"+now.get(Calendar.SECOND);
    			String fechaInicio=datosBean.getFecha_inicio()[val]+" "+datosBean.getHora_inicio()[val]+":00";
    			Date date1 = formatter.parse(fechaCel);  
    			Date date2 = formatter.parse(fechaInicio);
    			long diff = date2.getTime() - date1.getTime();
    			Log.d("******************Nombre", datosBean.getNombre()[val]+"");
    			Log.d("******************Cel", fechaCel+"");
    			Log.d("******************Ini", fechaInicio+"");
    			Log.d("******************", diff+"");
    			
    			timer[val].scheduleAtFixedRate( new TimerTask() {
    				public void run() {
    					Handler handler = new Handler();
    					handler.post(new Runnable() {
    						public void run() {
    							String[] messageString = new String[1];
    					          Message message = toastHandler.obtainMessage();
    					          messageString[0]=datosBean.getNombre()[val]+"";
    					          message.obj = messageString;
    					          toastHandler.sendMessage(message);
    						}
    					});
    				}
    			}, 0, 300000);
    	}

    }
    
    public void cargarDatos() {
		datosBean= new DatosBean();
		try {
			DBHelper	BD = new DBHelper(this);
			SQLiteDatabase bd = BD.loadDataBase(BD);
			datosBean =	BD.getDatos(bd);
			BD.close();
			if(datosBean!=null){
				startService();
			}else{
				this.stopSelf();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

   /* private class mainTask extends TimerTask
    { 
    	String nombre="zazaza";

		public mainTask(String nombre) {
			this.nombre=nombre;
			
		}

		public void run() 
        {
		   	  String[] messageString = new String[1];
	          Message message = toastHandler.obtainMessage();
	          messageString[0]=nombre;
	          message.obj = messageString;
	          toastHandler.sendMessage(message);
        }
    }    */

    public void onDestroy() 
    {
          super.onDestroy();
         
    }

    private final Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
        	String[] status = (String[]) msg.obj;
            Toast.makeText(getApplicationContext(),status[0], Toast.LENGTH_SHORT).show();

        }
    };   
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent,int flags,int startId) {
	    super.onStartCommand(intent, flags, startId);

	    return START_STICKY;
	}


}
