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
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikesaurio.pastillero.R;
import com.mikesaurio.pastillero.bd.DBHelper;
import com.mikesaurio.pastillero.bean.CitasBean;
import com.mikesaurio.pastillero.dialogos.CitasDialogActivity;
import com.mikesaurio.pastillero.dialogos.DatosDialogActivity;
import com.mikesaurio.pastillero.servicio.servicio_citas;
import com.mikesaurio.pastillero.utilerias.Utilerias;

/**
 * clase que controla el add de eventos
 * 
 * @author mikesaurio
 * 
 */
public class CitasFragment extends Fragment  {
	private CitasBean citasBean;
	Activity activity;
	LinearLayout ll_eventos;
	private View view;
	public static String id_ =null;
	private static final int ELIMINADO = 0;
	private static final int ONCE = 1;
	private static final int NORMAL = 2;
	private boolean[] clicks;
	String linearLongClick= null;

	
	
	/**
	 * Constructor
	 * @param activity
	 */
	public CitasFragment(Activity activity){
		this.activity=activity;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.activity_citas, container,false);
		
		
		ll_eventos =(LinearLayout)v.findViewById(R.id.citas_ll_eventos);
		
		 view = inflater.inflate(R.layout.row_default_pastillero, container,false);
		 TextView tv_rowdefault = (TextView)view.findViewById(R.id.tv_rowdefault);
		 tv_rowdefault.setText(getResources().getString(R.string.sin_eventos_citas));
		 tv_rowdefault.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,(Utilerias.getTamanoPantalla(activity).y/2)-Utilerias.getTamanoActionBar(activity)));
		
		cargarDatos();
	
		
		return v;
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
		startActivityForResult(new Intent(activity, CitasDialogActivity.class),0);
	}
	
	
	/**
	 * Termina e inicia el servicio de nuevo
	 */
	public void iniciarServicio_citas(){
		activity.stopService(new Intent(activity, servicio_citas.class));
		activity.startService(new Intent(activity, servicio_citas.class));
	}
	private void detenerServicio_citas() {
		activity.stopService(new Intent(activity, servicio_citas.class));
		
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
			BD.updateCitas(bd,valores,id);
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
		citasBean= new CitasBean();
		try {
			DBHelper	BD = new DBHelper(activity);
			SQLiteDatabase bd = BD.loadDataBase(BD);
			citasBean =	BD.getDatosCitas(bd);
			BD.close();
			

			removeView();
			
			if(citasBean!=null){

				iniciarDatos();
				iniciarServicio_citas();
			}
			else{
				detenerServicio_citas();
				addView(view);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
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
			BD.setCitas(bd,valores);
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
			BD.borrarCita(bd,id);
			BD.close();
			
			cargarDatos();
			
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
		
		clicks= new boolean[citasBean.getId().length];
	
		
		for(int i=0;i<citasBean.getId().length;i++){
			if(eliminaEvento(citasBean.getId()[i],citasBean.getFecha()[i],fechaCel,citasBean.getHora()[i])==NORMAL){
			
				clicks[i]=false;
				
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final	View view =  inflater.inflate(R.layout.row_evento, null);
			
			final	TextView evento_titulo =(TextView)view.findViewById(R.id.row_evento_titulo);
			evento_titulo.setText(citasBean.getNombre()[i]);
			
			final LinearLayout ll_row_evento=(LinearLayout)view.findViewById(R.id.ll_row_evento);
			
			//row editar
				
				ll_row_evento.removeAllViews();
				final View view_normal= cargarViewRowNormal(i);
				ll_row_evento.addView(view_normal);
				
				
		
			
			//obtenemos el alto de el linear
			ll_row_evento.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			final int height = ll_row_evento.getMeasuredHeight();
			
		
	
				view.setTag(i+"@"+citasBean.getId()[i].toString()+"@"+citasBean.getNombre()[i].toString()+"@"
				+citasBean.getFecha()[i].toString()+"@"+citasBean.getHora()[i].toString());
				
				view.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
					
						if(linearLongClick!=null){

							linearLongClick= null;
							
						}else{
						ll_row_evento.removeAllViews();
						String[] separated = view.getTag().toString().split("@");
						ll_row_evento.addView(cargarViewRowBorrar(Integer.parseInt(separated[1]),height,Integer.parseInt(separated[0]),
								evento_titulo,ll_row_evento,separated[2],separated[3],separated[4]));
						
						linearLongClick= separated[0];
						view.clearFocus();

						}

						
						
				

						return false;
					}
				});
				view.setOnTouchListener(new OnTouchListener() {

				    @Override
				    public boolean onTouch(View v, MotionEvent event) {
				    	TransitionDrawable transition = (TransitionDrawable) view.getBackground();
				    	String[] separated = view.getTag().toString().split("@");
				    	String tag = separated[0];
				    	
				        switch(event.getAction()) {

				        case MotionEvent.ACTION_DOWN:
				 

				        	if(tag.equals(linearLongClick)){
				        		transition.startTransition(1000);	
				        		linearLongClick=null;

					        	evento_titulo.setBackgroundColor(getResources().getColor(R.color.color_blanco));
					        	evento_titulo.setTextColor(getResources().getColor(R.color.color_base));
					        	try{
					        		((TextView)view_normal.findViewById(R.id.row_cita_tv_hora)).setTextColor(getResources().getColor(R.color.color_blanco));
						        	((TextView)view_normal.findViewById(R.id.row_cita_tv_inicio)).setTextColor(getResources().getColor(R.color.color_blanco));
					        		
						        	((TextView)view_normal.findViewById(R.id.row_cita_tv_hora_base)).setTextColor(getResources().getColor(R.color.color_blanco));
						        	((TextView)view_normal.findViewById(R.id.row_cita_tv_inicio_base)).setTextColor(getResources().getColor(R.color.color_blanco));

					        	}catch(Exception e){
					        		e.printStackTrace();
					        	}
				        	}else{
					        	evento_titulo.setBackgroundColor(getResources().getColor(R.color.color_blanco));
					        	evento_titulo.setTextColor(getResources().getColor(R.color.color_base));
				        		transition.startTransition(1000);	

				        		ll_row_evento.removeAllViews();
								ll_row_evento.addView(cargarViewRowNormal(Integer.parseInt(tag)));
				        	}
				        	return false;

				        case MotionEvent.ACTION_UP:	

				        	if(tag.equals(linearLongClick)){
				        		linearLongClick=null;
					        	transition.reverseTransition(0); 
					        	evento_titulo.setBackgroundColor(getResources().getColor(R.color.color_base));
					        	evento_titulo.setTextColor(getResources().getColor(R.color.color_blanco));
				        	
				        	try{
				        		((TextView)view_normal.findViewById(R.id.row_cita_tv_hora)).setTextColor(getResources().getColor(R.color.color_negro));
					        	((TextView)view_normal.findViewById(R.id.row_cita_tv_inicio)).setTextColor(getResources().getColor(R.color.color_negro));
				        		
				        		
					        	((TextView)view_normal.findViewById(R.id.row_cita_tv_hora_base)).setTextColor(getResources().getColor(R.color.color_negro));
					        	((TextView)view_normal.findViewById(R.id.row_cita_tv_inicio_base)).setTextColor(getResources().getColor(R.color.color_negro));
				        	}catch(Exception e){
				        		e.printStackTrace();
				        	}
				        	}else{
					        	evento_titulo.setBackgroundColor(getResources().getColor(R.color.color_base));
					        	evento_titulo.setTextColor(getResources().getColor(R.color.color_blanco));
				        		transition.reverseTransition(0); 
				        	}
				        	return false;
				        	
				        case MotionEvent.ACTION_CANCEL:		

				        	if(tag.equals(linearLongClick)){
					        	transition.reverseTransition(0); 
				        		linearLongClick=null;
				        	evento_titulo.setBackgroundColor(getResources().getColor(R.color.color_base));
				        	evento_titulo.setTextColor(getResources().getColor(R.color.color_blanco));
				        	
				        	try{
				        		((TextView)view_normal.findViewById(R.id.row_cita_tv_inicio_base)).setTextColor(getResources().getColor(R.color.color_negro));
					        	((TextView)view_normal.findViewById(R.id.row_cita_tv_inicio)).setTextColor(getResources().getColor(R.color.color_negro));
				        		
				        		
					        	((TextView)view_normal.findViewById(R.id.row_cita_tv_hora_base)).setTextColor(getResources().getColor(R.color.color_negro));
					        	((TextView)view_normal.findViewById(R.id.row_cita_tv_inicio_base)).setTextColor(getResources().getColor(R.color.color_negro));
				        	}catch(Exception e){
				        		e.printStackTrace();
				        	}
				        	}else{
					        	evento_titulo.setBackgroundColor(getResources().getColor(R.color.color_base));
					        	evento_titulo.setTextColor(getResources().getColor(R.color.color_blanco));
				        		transition.reverseTransition(0); 
				        	}
				        	return false;
				        	
				            
				        
				        }

				        return true;
				    }

				});
				addView(view);
			}
		}
		
	}
	
	
	
	

	
	
	
	/*******************************************************Termina Metodo de interaccion con la Base de datos*****************************************/
	
	/**
     * valida y elimina los eventos caducos
     * @param id
     * @param fecha_fin
     * @param fecha_cel
     * @param hora_cita
     * @return
     */
    public int eliminaEvento(String id,String fecha_fin,String fecha_cel,String hora_cita){
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			Date	date_telefono = formatter.parse(fecha_cel);
			Date date_fin = formatter.parse(fecha_fin+" "+hora_cita+":59");
			
			if( date_telefono.getTime()>date_fin.getTime()){
				borrarEvento(id);
	    		return ELIMINADO;
	    	}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return NORMAL;
    }
    
    /**
     * carga la informacion de una medicina
     * @param id
     * @return
     */
    public View cargarViewRowNormal(int id){
    	LayoutInflater inflater_normal = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final	View view_normals =  inflater_normal.inflate(R.layout.row_cita_normal, null);

		final TextView tv_inicio =(TextView)view_normals.findViewById(R.id.row_cita_tv_inicio);
		tv_inicio.setText(citasBean.getFecha()[id]);
		
		final TextView tv_fin =(TextView)view_normals.findViewById(R.id.row_cita_tv_hora);
		tv_fin.setText(citasBean.getHora()[id]);
		

		return view_normals;
    }
	
	
   /**
    * Row con opciones de editar, borrar y compartir una medicina
    * @param id
    * @param alto
    * @param id_row
    * @param evento_titulo
    * @param ll_row_evento
    * @param nombre
    * @param fecha
    * @param hora
    * @return
    */
    public View cargarViewRowBorrar(final int id, int alto,final  int id_row,final TextView evento_titulo,
    		final LinearLayout ll_row_evento,final String nombre,final String fecha,final String hora){
    	
    	LayoutInflater inflater_edit = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final	View view_edit =  inflater_edit.inflate(R.layout.row_editar, null);
		view_edit.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,alto));
		ImageView iv_editar= (ImageView)view_edit.findViewById(R.id.row_editar_iv_editar);
		iv_editar.requestFocus();
		iv_editar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
            	for(int i=0;i<citasBean.getId().length;i++){
            		if(citasBean.getId()[i].equals(id+"")){
            			String info[] = new String[]{citasBean.getNombre()[i],
            					citasBean.getFecha()[i],
            					citasBean.getHora()[i]	
            			};
            			id_=id+"";
            			startActivityForResult(new Intent(activity, CitasDialogActivity.class).putExtra("info", info),1);
            		}
            	}
            	
            	;
				evento_titulo.setBackgroundColor(getResources().getColor(R.color.color_base));
	        	evento_titulo.setTextColor(getResources().getColor(R.color.color_blanco));	
        		ll_row_evento.removeAllViews();
				ll_row_evento.addView(cargarViewRowNormal(id_row));
			}
		});
		
		ImageView iv_borrar= (ImageView)view_edit.findViewById(R.id.row_editar_iv_borrar);
		iv_borrar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				borrarEvento(id+"");
				
			}
		});
		
		
		ImageView iv_compartir= (ImageView)view_edit.findViewById(R.id.row_editar_iv_compartir);
		iv_compartir.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				String shareBody = getString(R.string.cita_compartir_uno)+" "+nombre+" "+getString(R.string.cita_compartir_dos)+" "+fecha+
						" "+getString(R.string.ccita_ompartir_tres)+" "+hora+" h.";
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "#Pastillero");
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
				startActivity(Intent.createChooser(sharingIntent, "Share via"));
				
			}
		});
		
		
		
		return view_edit;
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

	
}
