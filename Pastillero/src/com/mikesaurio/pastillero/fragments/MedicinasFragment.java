package com.mikesaurio.pastillero.fragments;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikesaurio.pastillero.R;
import com.mikesaurio.pastillero.bd.DBHelper;
import com.mikesaurio.pastillero.bean.DatosBean;
import com.mikesaurio.pastillero.dialogos.DatosDialogActivity;
import com.mikesaurio.pastillero.servicio.servicio_alarma;
import com.mikesaurio.pastillero.utilerias.Utilerias;

/**
 * clase que controla el add de eventos
 * 
 * @author mikesaurio
 * 
 */
public class MedicinasFragment extends Fragment  {

	LinearLayout ll_eventos;
	Activity activity;
	private DatosBean datosBean;
	private AlertDialog customDialog;
	private View view;
	public static String id_ =null;
	private static final int ELIMINADO = 0;
	private static final int ONCE = 1;
	private static final int NORMAL = 2;
	
	
	/**
	 * Constructor
	 * @param activity
	 */
	public MedicinasFragment(Activity activity){
		this.activity=activity;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.activity_capsule_time, container,false);
		
		ll_eventos =(LinearLayout)v.findViewById(R.id.capsule_time_ll_eventos);
		
		 view = inflater.inflate(R.layout.row_default_pastillero, container,false);
		 TextView tv_rowdefault = (TextView)view.findViewById(R.id.tv_rowdefault);
		 tv_rowdefault.setText(getResources().getString(R.string.sin_eventos_medicina));
		 tv_rowdefault.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,(Utilerias.getTamanoPantalla(activity).y/2)-Utilerias.getTamanoActionBar(activity)));
		
		cargarDatos();
		
		return v;
	}
	
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if (requestCode == 0) {
		        if(resultCode == DatosDialogActivity.OK){
		            String result=data.getStringExtra("resultado");
		            llenarEvento(result);
		        }
		    }
		 if (requestCode == 1) {
		        if(resultCode == DatosDialogActivity.OK){
		            String result=data.getStringExtra("resultado");
		            udateEvento(result,id_);
		            id_= null;

		        }
		    }
		super.onActivityResult(requestCode, resultCode, data);
	}


	
	
	/**
	 * Remueve una vista
	 */
	public void removeView() {
		ll_eventos.removeAllViews();
		
		
	}

	/**
	 * agrega una vista
	 * @param linear
	 */
	public void addView(View linear){
		ll_eventos.addView(linear);
	}
	
	/**
	 * inicia la actividad
	 */
	public void setMensaje(){
		startActivityForResult(new Intent(activity, DatosDialogActivity.class),0);
	}
	
	/**
	 * Dialogo para borrar un registro app
	 *
	 * @param Activity (actividad que llama al di‡logo)
	 * @return Dialog (regresa el dialogo creado)
	 **/
	
	public  Dialog showDialogEdit(final String id){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    View view = activity.getLayoutInflater().inflate(R.layout.dialogo_edit_borrar, null);
	    builder.setView(view);
	    builder.setCancelable(true);
	    

        ((Button) view.findViewById(R.id.dialogo_btn_editar)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
            	for(int i=0;i<datosBean.getId().length;i++){
            		if(datosBean.getId()[i].equals(id)){
            			String info[] = new String[]{datosBean.getNombre()[i],
            					datosBean.getFecha_inicio()[i],
            					datosBean.getFecha_fin()[i],
            					datosBean.getHora_inicio()[i],
            					datosBean.getFrecuencia()[i]	
            			};
            			id_=id;
            			startActivityForResult(new Intent(activity, DatosDialogActivity.class).putExtra("info", info),1);
            		}
            	}
            	
            	 customDialog.dismiss(); 
            	 
            	 
            }
        });

        ((Button) view.findViewById(R.id.dialogo_btn_borrar)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
            	borrarEvento(id);
                customDialog.dismiss();    
            }
        });
        return (customDialog=builder.create());
    }  
	
	
	
	
	/**
	 * Termina e inicia el servicio de nuevo
	 */
	public void iniciarServicio(){
		activity.stopService(new Intent(activity, servicio_alarma.class));
		activity.startService(new Intent(activity, servicio_alarma.class));
	}
	public void detenerServicio(){
		activity.stopService(new Intent(activity, servicio_alarma.class));
	}
	
	
	
	/*******************************************************Metodo de interaccion con la Base de datos*****************************************/
	/**
	 * Actualiza un evento
	 * @param result
	 * @param id
	 */
	private void udateEvento(String result,String id) {
		String[] valores = result.split("@");
		try {
			DBHelper	BD = new DBHelper(activity);
			SQLiteDatabase bd = BD.loadDataBase(BD);
			BD.updateDatos(bd,valores,id);
			BD.close();
			
			cargarDatos();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}



	/**
	 * Obtiene los datos de la BD	
	 */
	public void cargarDatos() {
		datosBean= new DatosBean();
		try {
			DBHelper	BD = new DBHelper(activity);
			SQLiteDatabase bd = BD.loadDataBase(BD);
			datosBean =	BD.getDatos(bd);
			BD.close();
			

			removeView();
			
			if(datosBean!=null){

				iniciarDatos();
				iniciarServicio();
			}
			else{
				detenerServicio();
				addView(view);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Inicia los objetos con la informacion de la BD
	 */
	public void iniciarDatos() {
		Calendar now = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String fechaCel= now.get(Calendar.DAY_OF_MONTH)+"/"+((now.get(Calendar.MONTH))+1)+"/"+now.get(Calendar.YEAR)+
				" "+now.get(Calendar.HOUR_OF_DAY)+":"+now.get(Calendar.MINUTE)+":"+now.get(Calendar.SECOND);
		
		for(int i=0;i<datosBean.getId().length;i++){
			if(eliminaEvento(datosBean.getId()[i],datosBean.getFecha_fin()[i],fechaCel,TimeUnit.HOURS.toMillis(Integer.parseInt(datosBean.getFrecuencia()[i]+"")))==NORMAL){
			
				LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final	View view =  inflater.inflate(R.layout.row_evento, null);
			
			final	TextView evento_titulo =(TextView)view.findViewById(R.id.row_evento_titulo);
				evento_titulo.setText(datosBean.getNombre()[i]);
				
				final TextView tv_inicio =(TextView)view.findViewById(R.id.row_evento_tv_inicio);
				tv_inicio.setText(datosBean.getFecha_inicio()[i]);
				
				final TextView tv_fin =(TextView)view.findViewById(R.id.row_evento_tv_fin);
				tv_fin.setText(datosBean.getFecha_fin()[i]);
				
				final TextView tv_hora =(TextView)view.findViewById(R.id.row_evento_tv_hora);
				tv_hora.setText(datosBean.getHora_inicio()[i]+" hrs");
				
				final TextView tv_tiempo =(TextView)view.findViewById(R.id.row_evento_tv_tiempo);
				tv_tiempo.setText(datosBean.getFrecuencia()[i]+" hrs");
	
				view.setTag(datosBean.getId()[i]);
				view.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
	
						showDialogEdit(v.getTag()+"").show();
						return false;
					}
				});
				view.setOnTouchListener(new OnTouchListener() {

				    @Override
				    public boolean onTouch(View v, MotionEvent event) {
				    	TransitionDrawable transition = (TransitionDrawable) v.getBackground();
				        switch(event.getAction()) {

				        case MotionEvent.ACTION_DOWN:
				        	transition.startTransition(1000);
				        	evento_titulo.setBackgroundColor(getResources().getColor(R.color.color_blanco));
				        	evento_titulo.setTextColor(getResources().getColor(R.color.color_base));
				        	tv_inicio.setTextColor(getResources().getColor(R.color.color_blanco));
				        	tv_fin.setTextColor(getResources().getColor(R.color.color_blanco));
				        	tv_hora.setTextColor(getResources().getColor(R.color.color_blanco));
				        	tv_tiempo.setTextColor(getResources().getColor(R.color.color_blanco));
				        	((TextView)view.findViewById(R.id.row_evento_tv_inicio_base)).setTextColor(getResources().getColor(R.color.color_blanco));
				        	((TextView)view.findViewById(R.id.row_evento_tv_fin_base)).setTextColor(getResources().getColor(R.color.color_blanco));
				        	((TextView)view.findViewById(R.id.row_evento_tv_hora_base)).setTextColor(getResources().getColor(R.color.color_blanco));
				        	((TextView)view.findViewById(R.id.row_evento_tv_tiempo_base)).setTextColor(getResources().getColor(R.color.color_blanco));
				        	return false;

				        case MotionEvent.ACTION_UP:			        	
				        	transition.reverseTransition(1000); 
				        	evento_titulo.setBackgroundColor(getResources().getColor(R.color.color_base));
				        	evento_titulo.setTextColor(getResources().getColor(R.color.color_blanco));
				        	tv_inicio.setTextColor(getResources().getColor(R.color.color_negro));
				        	tv_fin.setTextColor(getResources().getColor(R.color.color_negro));
				        	tv_hora.setTextColor(getResources().getColor(R.color.color_negro));
				        	tv_tiempo.setTextColor(getResources().getColor(R.color.color_negro));
				        	((TextView)view.findViewById(R.id.row_evento_tv_inicio_base)).setTextColor(getResources().getColor(R.color.color_negro));
				        	((TextView)view.findViewById(R.id.row_evento_tv_fin_base)).setTextColor(getResources().getColor(R.color.color_negro));
				        	((TextView)view.findViewById(R.id.row_evento_tv_hora_base)).setTextColor(getResources().getColor(R.color.color_negro));
				        	((TextView)view.findViewById(R.id.row_evento_tv_tiempo_base)).setTextColor(getResources().getColor(R.color.color_negro));
				        	return false;
				        	
				        case MotionEvent.ACTION_CANCEL:			        	
				        	transition.reverseTransition(1000); 
				        	evento_titulo.setBackgroundColor(getResources().getColor(R.color.color_base));
				        	evento_titulo.setTextColor(getResources().getColor(R.color.color_blanco));
				        	tv_inicio.setTextColor(getResources().getColor(R.color.color_negro));
				        	tv_fin.setTextColor(getResources().getColor(R.color.color_negro));
				        	tv_hora.setTextColor(getResources().getColor(R.color.color_negro));
				        	tv_tiempo.setTextColor(getResources().getColor(R.color.color_negro));
				        	((TextView)view.findViewById(R.id.row_evento_tv_inicio_base)).setTextColor(getResources().getColor(R.color.color_negro));
				        	((TextView)view.findViewById(R.id.row_evento_tv_fin_base)).setTextColor(getResources().getColor(R.color.color_negro));
				        	((TextView)view.findViewById(R.id.row_evento_tv_hora_base)).setTextColor(getResources().getColor(R.color.color_negro));
				        	((TextView)view.findViewById(R.id.row_evento_tv_tiempo_base)).setTextColor(getResources().getColor(R.color.color_negro));
				        	return false;
				        	
				            
				        }

				        return true;
				    }

				});
				addView(view);
			}
		}
		
	}
	
	
	

	
	/**
	 * Carga un evento en la BD
	 * @param result
	 */
	public void llenarEvento(String result) {
		
		String[] valores = result.split("@");
		try {
			DBHelper	BD = new DBHelper(activity);
			SQLiteDatabase bd = BD.loadDataBase(BD);
			BD.setDatos(bd,valores);
			BD.close();
			
			cargarDatos();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Elimina un eevnto en especifico
	 * @param id
	 */
	public void borrarEvento(String id) {
		
		try {
			DBHelper	BD = new DBHelper(activity);
			SQLiteDatabase bd = BD.loadDataBase(BD);
			BD.borrarDato(bd,id);
			BD.close();
			
			cargarDatos();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*******************************************************Termina Metodo de interaccion con la Base de datos*****************************************/	
	
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
			
			if( date_telefono.getTime()>date_fin.getTime()){
				borrarEvento(id);
	    		return ELIMINADO;
	    	}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return NORMAL;
    }
}
