package com.mikesaurio.pastillero.fragments;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikesaurio.pastillero.R;
import com.mikesaurio.pastillero.bd.DBHelper;
import com.mikesaurio.pastillero.bean.DatosBean;
import com.mikesaurio.pastillero.dialogos.DatosDialogActivity;
import com.mikesaurio.pastillero.servicio.servicio_alarma;

/**
 * clase que controla el add de eventos
 * 
 * @author mikesaurio
 * 
 */
public class DashboardFragment extends Fragment  {

	//Button btn_agregar;
	LinearLayout ll_eventos;
	Activity activity;
	private DatosBean datosBean;
	private AlertDialog customDialog;
	public static String id_ =null;
	
	
	public DashboardFragment(Activity activity){
		this.activity=activity;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.activity_capsule_time, container,false);
		
		ll_eventos =(LinearLayout)v.findViewById(R.id.capsule_time_ll_eventos);
		
		
		cargarDatos();
		
		return v;
	}
	
	
	
	
	
	
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if (requestCode == 0) {
		        if(resultCode == DatosDialogActivity.OK){
		            String result=data.getStringExtra("resultado");
		            llenarEvento(result);
		            iniciarServicio();
		        }
		    }
		 if (requestCode == 1) {
		        if(resultCode == DatosDialogActivity.OK){
		            String result=data.getStringExtra("resultado");
		            udateEvento(result,id_);
		            id_= null;
		            iniciarServicio();
		        }
		    }
		super.onActivityResult(requestCode, resultCode, data);
	}


	
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
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void iniciarDatos() {
		
		
		for(int i=0;i<datosBean.getId().length;i++){
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view =  inflater.inflate(R.layout.row_evento, null);
		
			TextView evento_titulo =(TextView)view.findViewById(R.id.row_evento_titulo);
			evento_titulo.setText(datosBean.getNombre()[i]);
			
			TextView tv_inicio =(TextView)view.findViewById(R.id.row_evento_tv_inicio);
			tv_inicio.setText(datosBean.getFecha_inicio()[i]);
			
			TextView tv_fin =(TextView)view.findViewById(R.id.row_evento_tv_fin);
			tv_fin.setText(datosBean.getFecha_fin()[i]);
			
			TextView tv_hora =(TextView)view.findViewById(R.id.row_evento_tv_hora);
			tv_hora.setText(datosBean.getHora_inicio()[i]+" hrs");
			
			TextView tv_tiempo =(TextView)view.findViewById(R.id.row_evento_tv_tiempo);
			tv_tiempo.setText(datosBean.getFrecuencia()[i]+" hrs");

			view.setTag(datosBean.getId()[i]);
			view.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {

					showDialogEdit(v.getTag()+"").show();
					return false;
				}
			});
			addView(view);
		}
		
	}

	
	
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
	

	public void removeView() {
		ll_eventos.removeAllViews();
		
	}

	public void addView(View linear){
		ll_eventos.addView(linear);
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
        return (customDialog=builder.create());// return customDialog;//regresamos el di‡logo
    }  
	
	public void setMensaje(){
		startActivityForResult(new Intent(activity, DatosDialogActivity.class),0);
	}
	
	
	public void iniciarServicio(){
		
		activity.stopService(new Intent(activity, servicio_alarma.class));
		activity.startService(new Intent(activity, servicio_alarma.class));
	}

}
