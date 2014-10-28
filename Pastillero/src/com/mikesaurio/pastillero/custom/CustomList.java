package com.mikesaurio.pastillero.custom;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mikesaurio.pastillero.R;

/**
 * clase que personaliza la lista de eventos
 * Created by mikesaurio on 05/05/14.
 */
public class CustomList extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] nombre;


    /**
     * cosntructor
     * @param context (Activity)
     * @param nombre
     */
    public CustomList(Activity context,String[] nombre) {
        super(context, R.layout.list_simple, nombre);
        this.context = context;
        this.nombre = nombre;
    }
    
    
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_simple, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.list_simple_tv_texto);
        txtTitle.setText(nombre[position]);
        return rowView;
    }
    
   
	
   
}