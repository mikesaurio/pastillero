package com.mikesaurio.pastillero.servicio;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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
  	int m = 0;
	
    public void onCreate() 
    {
          super.onCreate(); 
          cargarDatos();
          
    }

    private void startService() throws ParseException
    {          
    	timer = new Timer[datosBean.getId().length];
    	m=0;
    	for(int val = 0; val< timer.length ; val++){	
    		 TimerTask task = new TimerTask() {
     	        @Override
     	        public void run() {

     		          Message message = toastHandler.obtainMessage(); 
     		          message.obj = datosBean.getNombre()[m]+"";
     		          m+=1;
     		          toastHandler.sendMessage(message);
     	        }
     	    };
    		
    			Calendar now = Calendar.getInstance();
    			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
    			String fechaCel= now.get(Calendar.DAY_OF_MONTH)+"/"+((now.get(Calendar.MONTH))+1)+"/"+now.get(Calendar.YEAR)+
    					" "+now.get(Calendar.HOUR_OF_DAY)+":"+now.get(Calendar.MINUTE)+":"+now.get(Calendar.SECOND);
    			String fechaInicio=datosBean.getFecha_inicio()[val]+" "+datosBean.getHora_inicio()[val]+":00";
    			Date date1 = formatter.parse(fechaCel);  
    			Date date2 = formatter.parse(fechaInicio);
    			long diff = date2.getTime() - date1.getTime();
    			long cada = TimeUnit.HOURS.toMillis(Integer.parseInt(datosBean.getFrecuencia()[val]+""));
    			timer[val]= new Timer();
    			timer[val].scheduleAtFixedRate( task, diff,cada);

    			
    	}

    }
    
    public void cargarDatos() {
    	datosBean = null;
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

   

    public void onDestroy() 
    {
    	for(int val=0;val<timer.length;val++)	{	
    		timer[0].cancel();
    	}
    	datosBean = null;
          super.onDestroy();
         
    }

    private final Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
        	String status = (String) msg.obj;
           // Toast.makeText(getApplicationContext(),status, Toast.LENGTH_SHORT).show();
            //enviar Alarma
             
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
