package com.mikesaurio.pastillero.servicio;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
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
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.mikesaurio.pastillero.PastilleroActivity;
import com.mikesaurio.pastillero.R;
import com.mikesaurio.pastillero.bd.DBHelper;
import com.mikesaurio.pastillero.bean.CitasBean;

/**
 * Servicio que controla las notificaciones
 * @author mikesaurio
 *
 */
public class servicio_citas extends Service {

	private static Timer timer_citas[];
	private CitasBean citasBean; 
    private final static int CUSTOM_VIEW_citas = 0x04;
	private static final int ELIMINADO_citas = 0;
	private static final int NORMAL_citas = 2;
      NotificationManager mNotificationManager_citas;
      private static int not_citas=1000;
	
    public void onCreate() 
    {
          super.onCreate(); 
          not_citas=0;
          cargarDatos();
          
    }

    /**
     * Crea un TimerTask por evento
     * @throws ParseException
     */
    @SuppressLint("SimpleDateFormat")
	private void startService() throws ParseException
    {          Log.d("CITAS iniciado******", "*********");
    	iniciarCitas();
    }
    
    /**
     * prepara todo para iniciar las medicinas
     * @throws ParseException
     */
    private void iniciarCitas() throws ParseException{
    	
    	matarHilos();
    	
    	timer_citas = new Timer[citasBean.getId().length];
    	HiloTask_citas[] task_citas= new HiloTask_citas[citasBean.getId().length];
    	
    	Calendar now_citas = Calendar.getInstance();
		SimpleDateFormat formatter_citas = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    	String fechaCel= now_citas.get(Calendar.DAY_OF_MONTH)+"/"+((now_citas.get(Calendar.MONTH))+1)+"/"+now_citas.get(Calendar.YEAR)+
				" "+now_citas.get(Calendar.HOUR_OF_DAY)+":"+now_citas.get(Calendar.MINUTE)+":"+now_citas.get(Calendar.SECOND);
    	
    	for(int val = 0; val< timer_citas.length ; val++){	
    		    
    			String fechaInicio=citasBean.getFecha()[val]+" "+citasBean.getHora()[val]+":00";
    			
    			Date date_telefono = formatter_citas.parse(fechaCel);  
    			Date date_inicio = formatter_citas.parse(fechaInicio);
    			
    			
    			long diff = date_inicio.getTime() - date_telefono.getTime();
    			
    			

    			timer_citas[val]= new Timer();
    			task_citas[val]= new HiloTask_citas(citasBean.getNombre()[val], citasBean.getId()[val]);
    			
    			
    			if(diff>=0){
    				timer_citas[val].schedule( task_citas[val], diff,3600000);
    			}else{
    				borrarEvento_citas(citasBean.getId()[val]);
    			}
    			Log.d("CITAS******", diff+"");
    			
    	}

		
	}

	public void matarHilos() {
    	if(timer_citas!=null){
	    	for(int val=0;val<timer_citas.length;val++)	{	
	    		if(timer_citas[val]!=null){
		    		timer_citas[val].cancel();
		    		timer_citas[val].purge();
		    		timer_citas[val]=null;
	    		}
	    	}
    	}

		
	}

	/**
     * Carga los datos de la base de datos
     */
    public void cargarDatos() {
    	citasBean = null;
    	citasBean= new CitasBean();
		try {
			DBHelper	BD = new DBHelper(this);
			SQLiteDatabase bd = BD.loadDataBase(BD);
			citasBean =	BD.getDatosCitas(bd);
			BD.close();
			if(citasBean!=null){
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

   
	/**
	 * al destruir el servicio limpiamos todo
	 */
    public void onDestroy() 
    {
		not_citas=1000;
    	matarHilos();
    	citasBean = null;
    	  Log.d("CITAS destruido******", "*********");
          super.onDestroy();
         
    }

    @SuppressLint("HandlerLeak")
	private final Handler toastHandler_citas = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
        	String status = (String) msg.obj;
        	new CreateNotification_citas(CUSTOM_VIEW_citas,status).execute();
             
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
    public class CreateNotification_citas extends AsyncTask<Void, Void, Void> {
    	NotificationManager mNotificationManager = (NotificationManager) servicio_citas.this.getSystemService(Context.NOTIFICATION_SERVICE);
        int style_citas = CUSTOM_VIEW_citas;
        String titulo;
       

        /**
         * Main constructor for AsyncTask that accepts the parameters below.
         *
         * @param style {@link #NORMAL}, {@link #BIG_TEXT_STYLE}, {@link #BIG_PICTURE_STYLE}, {@link #INBOX_STYLE}
         * @see #doInBackground
         */
        public CreateNotification_citas(int style,String titulo) {
            this.style_citas = style;
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

            switch (style_citas)
            {
                case CUSTOM_VIEW_citas:
                    noti = setCustomViewNotification(titulo);
                    break;

            }

            noti.defaults |= Notification.DEFAULT_LIGHTS;
            noti.defaults |= Notification.DEFAULT_VIBRATE;
            noti.defaults |= Notification.DEFAULT_SOUND;

            noti.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

            mNotificationManager.notify(not_citas, noti);

            return null;

        }
        @Override
		protected void onPreExecute() {
			 Vibrator v = (Vibrator) servicio_citas.this.getSystemService(Context.VIBRATOR_SERVICE);
			 v.vibrate(1000);
			super.onPreExecute();
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
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(not_citas, PendingIntent.FLAG_UPDATE_CURRENT);
        
        not_citas+=1;

        RemoteViews expandedView = new RemoteViews(this.getPackageName(), R.layout.notification_custom);
        expandedView.setTextViewText(R.id.notificacion_custom_tv_titulo,getString(R.string.titulo_notificacion_)+" "+ titulo);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(false)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(getString(R.string.app_name)).build();

        notification.contentView = expandedView;

        return notification;
    }
	
    
    
    /**
     * timer que maneja  las notificaciones
     * @author mikesaurio
     *
     */
    public class HiloTask_citas extends TimerTask{
    	String titulo= getString(R.string.app_name);
		private String id;
    	
    	/**
    	 * construtor
    	 * @param titulo
    	 * @param id
    	 * @param fecha_fin
    	 * @param fecha_cel
    	 * @param intervalo
    	 */
    	public HiloTask_citas(String titulo,String id){
    		this.titulo=titulo;
    		this.id=id;
    	}
    	
		@Override
		public void run() {
			
		        	  Message message = toastHandler_citas.obtainMessage(); 
	 		          message.obj = titulo;
	 		          toastHandler_citas.sendMessage(message);
	 		          borrarEvento_citas(id);
		}
    	
    }
   
   
   
    
    /**
	 * Elimina un eevnto en especifico
	 * @param id
	 */
	public void borrarEvento_citas(String id) {
		
		try {
			DBHelper	BD = new DBHelper(this);
			SQLiteDatabase bd = BD.loadDataBase(BD);
			BD.borrarCita(bd,id);
			BD.close();
			cargarDatos();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
