package com.mikesaurio.pastillero.servicio;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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
import com.mikesaurio.pastillero.bean.DatosBean;
import com.mikesaurio.pastillero.utilerias.Utilerias;

/**
 * Servicio que controla las notificaciones
 * @author mikesaurio
 *
 */
public class servicio_alarma extends Service {

	private static Timer timer[];
	private DatosBean datosBean; 
    private final static int CUSTOM_VIEW = 0x04;
	private static final int ELIMINADO = 0;
	private static final int ONCE = 1;
	private static final int NORMAL = 2;
      NotificationManager mNotificationManager;
      private static int not=0;
	
    public void onCreate() 
    {
          super.onCreate(); 
          not=0;
          cargarDatos();
          
    }

    /**
     * Crea un TimerTask por evento
     * @throws ParseException
     */
    @SuppressLint("SimpleDateFormat")
	private void startService() throws ParseException
    {          
    	  Log.d("Alarma iniciado******", "*********");
    	iniciarDatos();
    }
    
    /**
     * prepara todo para iniciar las medicinas
     * @throws ParseException
     */
    private void iniciarDatos() throws ParseException{
    	matarHilos();
    	
    	timer = new Timer[datosBean.getId().length];
    	HiloTask[] task= new HiloTask[datosBean.getId().length];
    	
    	Calendar now = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    	String fechaCel= now.get(Calendar.DAY_OF_MONTH)+"/"+((now.get(Calendar.MONTH))+1)+"/"+now.get(Calendar.YEAR)+
				" "+now.get(Calendar.HOUR_OF_DAY)+":"+now.get(Calendar.MINUTE)+":"+now.get(Calendar.SECOND);
    	
    	for(int val = 0; val< timer.length ; val++){	
    		    
    			String fechaInicio=datosBean.getFecha_inicio()[val]+" "+datosBean.getHora_inicio()[val]+":00";
    			
    			Date date_telefono = formatter.parse(fechaCel);  
    			Date date_inicio = formatter.parse(fechaInicio);
    			
    			
    			long diff = date_inicio.getTime() - date_telefono.getTime();
    			
    			long intervalo_alarma = TimeUnit.HOURS.toMillis(Integer.parseInt(datosBean.getFrecuencia()[val]+""));
    			

    			timer[val]= new Timer();
    			task[val]= new HiloTask(datosBean.getNombre()[val], datosBean.getId()[val],
    					datosBean.getFecha_fin()[val], fechaCel, intervalo_alarma);
    			
    			
    			if(diff>=0){
    				timer[val].scheduleAtFixedRate( task[val], diff,intervalo_alarma);
    			}else{

    				long dif = date_inicio.getTime();
    				
	    				do{
	    					dif += intervalo_alarma;   					
	    				}while(dif<=date_telefono.getTime());  
    				
    				String fecha_siguiente=Utilerias.getDate(dif, formatter);
    				diff =  formatter.parse(fecha_siguiente).getTime()-date_telefono.getTime();
        			
    				timer[val].schedule( task[val], diff,intervalo_alarma);
    			}
    			Log.d("*******inicio", diff+"");
    			Log.d("*******intervalo", intervalo_alarma+"");
    	}

		
	}

	public void matarHilos() {
		if(timer!=null){
	    	for(int val=0;val<timer.length;val++)	{	
	    		if(timer[val]!=null){
	    			timer[val].cancel();
	    			timer[val].purge();
	    			timer[val]=null;
	    		}
	    	}
    	}
		
	}

	/**
     * Carga los datos de la base de datos
     */
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

   
	/**
	 * al destruir el servicio limpiamos todo
	 */
    public void onDestroy() 
    {
    	not=0;
    	datosBean = null;
    	 Log.d("Alarma terminado******", "*********");
          super.onDestroy();
         
    }

    @SuppressLint("HandlerLeak")
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

		@Override
		protected void onPreExecute() {
			 Vibrator v = (Vibrator) servicio_alarma.this.getSystemService(Context.VIBRATOR_SERVICE);
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
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(not, PendingIntent.FLAG_UPDATE_CURRENT);
        
        not+=1;

        RemoteViews expandedView = new RemoteViews(this.getPackageName(), R.layout.notification_custom);
        expandedView.setTextViewText(R.id.notificacion_custom_tv_titulo,getString(R.string.titulo_notificacion)+" "+ titulo);

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
    public class HiloTask extends TimerTask{
    	String titulo= getString(R.string.app_name);
		private String id;
		private String fecha_fin;
		private String fecha_cel;
		private long intervalo;
    	
    	/**
    	 * construtor
    	 * @param titulo
    	 * @param id
    	 * @param fecha_fin
    	 * @param fecha_cel
    	 * @param intervalo
    	 */
    	public HiloTask(String titulo,String id,String fecha_fin,String fecha_cel,long intervalo){
    		this.titulo=titulo;
    		this.id=id;
    		this.fecha_fin=fecha_fin;
    		this.fecha_cel=fecha_cel;
    		this.intervalo=intervalo;
    	}
    	
		@Override
		public void run() {
			int respuesta=eliminaEvento(id, fecha_fin, fecha_cel, intervalo);
	        	if(respuesta==NORMAL){
		        	  Message message = toastHandler.obtainMessage(); 
	 		          message.obj = titulo;
	 		          toastHandler.sendMessage(message);
	        	}else if(respuesta==ONCE){
	        		 Message message = toastHandler.obtainMessage(); 
	 		         message.obj = titulo;
	 		         toastHandler.sendMessage(message);
	 		         borrarEvento(id);
	        	}
		}
    	
    }
   
    /**
     * valida y elimina los eventos caducos
     * @param id
     * @param fecha_fin
     * @param fecha_cel
     * @param intervalo
     * @return
     */
    public int eliminaEvento(String id,String fecha_fin,String fecha_cel,long intervalo){
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			Date	date_telefono = formatter.parse(fecha_cel);
			Date date_fin = formatter.parse(fecha_fin+" "+"24:00:00");
			
			long actualMasUnCiclo = date_telefono.getTime()+intervalo;
			
			
			if( date_telefono.getTime()>date_fin.getTime()){
				borrarEvento(id);
	    		return ELIMINADO;
	    	}else if(actualMasUnCiclo>date_fin.getTime()){//la hora actual mas un ciclo es menor que el final
	    		return ONCE;
	    	}
		
		} catch (ParseException e) {
			e.printStackTrace();
		}  

		
    
		return NORMAL;
    	
    }
    
    /**
	 * Elimina un eevnto en especifico
	 * @param id
	 */
	public void borrarEvento(String id) {
		
		try {
			DBHelper	BD = new DBHelper(this);
			SQLiteDatabase bd = BD.loadDataBase(BD);
			BD.borrarDato(bd,id);
			BD.close();
			cargarDatos();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
