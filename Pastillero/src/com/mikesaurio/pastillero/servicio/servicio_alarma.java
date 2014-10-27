package com.mikesaurio.pastillero.servicio;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.mikesaurio.pastillero.PastilleroActivity;
import com.mikesaurio.pastillero.R;
import com.mikesaurio.pastillero.bd.DBHelper;
import com.mikesaurio.pastillero.bean.DatosBean;

public class servicio_alarma extends Service {

	private static Timer timer[];
	private DatosBean datosBean; 
  	int m = 0;
    private final static int CUSTOM_VIEW = 0x04;
      NotificationManager mNotificationManager;
      private static int not=0;
	
    public void onCreate() 
    {
          super.onCreate(); 
          not=0;
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
    			Log.d("**************cada",cada+"");
    		
    			timer[val]= new Timer();
    			if(diff>=0){
    				
    				timer[val].scheduleAtFixedRate( task, diff,cada);
    			}
    			else{
    				
    				//se debe obtener la fecha de inicio y compararla con la hora actual
    				//ver cuantas milisegundos han pasado desde que la alarma inici—
    				//comparar en ciclo horainicio + = frecuencia hasta que sea mayor 
    				//se obtiene diferencia y se asigna a diff
    			
    				
    				timer[val].scheduleAtFixedRate( task, 0,cada);
    			}
    			
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
    	not=0;
    	if(timer!=null){
	    	for(int val=0;val<timer.length;val++)	{	
	    		timer[0].cancel();
	    	}
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
        	new CreateNotification(CUSTOM_VIEW,status).execute();
             
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

	 /**
     * Notification AsyncTask to create and return the
     * requested notification.
     *
     * @see CreateNotification#CreateNotification(int)
     */
    public class CreateNotification extends AsyncTask<Void, Void, Void> {
    	NotificationManager mNotificationManager = (NotificationManager) servicio_alarma.this.getSystemService(Context.NOTIFICATION_SERVICE);
        int style = CUSTOM_VIEW;
        String titulo;
       

        /**
         * Main constructor for AsyncTask that accepts the parameters below.
         *
         * @param style {@link #NORMAL}, {@link #BIG_TEXT_STYLE}, {@link #BIG_PICTURE_STYLE}, {@link #INBOX_STYLE}
         * @see #doInBackground
         */
        public CreateNotification(int style,String titulo) {
            this.style = style;
            this.titulo= titulo;
        }

        /**
         * Creates the notification object.
         *
         * @see #setNormalNotification
         * @see #setBigTextStyleNotification
         * @see #setBigPictureStyleNotification
         * @see #setInboxStyleNotification
         */
        @Override
        protected Void doInBackground(Void... params) {
            Notification noti = new Notification();

            switch (style)
            {
                case CUSTOM_VIEW:
                    noti = setCustomViewNotification(titulo);
                    break;

            }

            noti.defaults |= Notification.DEFAULT_LIGHTS;
            noti.defaults |= Notification.DEFAULT_VIBRATE;
            noti.defaults |= Notification.DEFAULT_SOUND;

            noti.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

            mNotificationManager.notify(not, noti);

            return null;

        }
    }
    
    
    /**
     * notificacion custom
     * @param titulo
     * @return
     */
    private Notification setCustomViewNotification(String titulo) {

        Intent resultIntent = new Intent(this, PastilleroActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(PastilleroActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(not, PendingIntent.FLAG_UPDATE_CURRENT);
        
        not+=1;

        RemoteViews expandedView = new RemoteViews(this.getPackageName(), R.layout.notification_custom);
        expandedView.setTextViewText(R.id.notificacion_custom_tv_titulo,getString(R.string.titulo_notificacion)+" "+ titulo);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(getString(R.string.app_name)).build();

        notification.contentView = expandedView;

        return notification;
    }
	

}
