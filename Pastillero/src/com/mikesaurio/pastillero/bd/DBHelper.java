package com.mikesaurio.pastillero.bd;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mikesaurio.pastillero.bean.CitasBean;
import com.mikesaurio.pastillero.bean.DatosBean;

/**
 * Clase que construya todo el entorno de la base de datos y las conecciones
 * @author mikesaurio
 *
 */
public class DBHelper extends SQLiteOpenHelper {
	/** Path donde se encontrara alojada la BD en el telŽfono **/
	private static String DB_PATH = "";
	/** Nombre de la base de datos **/
	private final static String DB_NAME = "pastillero";

	private SQLiteDatabase myDataBase;
	private final Context myContext;

	/** Constructor **/
	@SuppressLint("SdCardPath")
	public DBHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
		
		DB_PATH = "/data/data/" + myContext.getPackageName() + "/databases/";
	}

	
	/**
	 * Comprueba si ya existe nuestra base de datos
	 * 
	 * @return true si ya existe, false si no
	 **/
	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
			
			
		} catch (Exception e) {
			Log.i("Base de datos", "falla en checkDataBAse");
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return  (checkDB!= null ? true : false);
	}
	

	/**
	 * Crea una base de datos vac’a y escribe en ella nuestra propia Base de
	 * Datos
	 **/
	public void createDataBase() throws IOException {

		/** Comprueba si ya existe la base de datos **/
		boolean dbExist = checkDataBase();

		if (dbExist) {
			/** Si existe la base de datos no hace nada **/
		} else {
			/**
			 * Si no existe se llama a este mŽtodo que crea una nueva base de
			 * datos en la ruta por defecto
			 **/
			this.getReadableDatabase();
			try {
				/**
				 * Copia nuestra database.sqlite en la nueva base de datos
				 * creada
				 **/
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copiando la Base de Datos");
			}
		}
	}

	/**
	 * Copia nuestra base de datos sqlite de la carpeta assets a nuestra nueva
	 * Base de Datos
	 **/
	private void copyDataBase() throws IOException {

		/** Abre nuestra base de datos del fichero **/
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		/** La direcci—n de nuestra nueva Base de Datos **/
		String outFileName = DB_PATH + DB_NAME;

		/** Abre la nueva Base de Datos **/
		OutputStream myOutput = new FileOutputStream(outFileName);

		/** Transfiere bytes desde nuestro archivo a la nueva base de datos **/
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		/** Cierra los stream **/
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	/**
	 * Abre la base de datos
	 * 
	 * @throws SQLException
	 **/
	public void openDataBase() throws SQLException {
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}


	public SQLiteDatabase loadDataBase(DBHelper helper) throws IOException {
		helper.createDataBase( );
	    SQLiteDatabase db = helper.getWritableDatabase();
		return db;
	}


	/**
	 * Obtienes todos los datos de la base de datos
	 * @param bd
	 * @return DatosBean
	 */
	public DatosBean getDatos(SQLiteDatabase bd) {
		DatosBean bean = null;
		
		Cursor c = null;
        c = bd.rawQuery("select * from datos", null);
    	
        if(c!=null&&c.getCount()>0){
        	bean = new DatosBean();
        	
        	 String[] id= new String[c.getCount()];
	         String[] nombre = new String[c.getCount()];
	    	 String[] fecha_inicio= new String[c.getCount()];
	    	 String[] fecha_fin= new String[c.getCount()];
	    	 String[] hora_inicio= new String[c.getCount()];
	    	 String[] frecuencia= new String[c.getCount()];
        
	        c.moveToFirst();
	        int i =0;
	        while (!c.isAfterLast()){
	        	id[i]=(c.getString(c.getColumnIndex("id")));
	        	nombre[i]=(c.getString(c.getColumnIndex("nombre")));
	        	fecha_inicio[i]=(c.getString(c.getColumnIndex("fecha_inicio")));
	        	fecha_fin[i]=(c.getString(c.getColumnIndex("fecha_fin")));
	        	hora_inicio[i]=(c.getString(c.getColumnIndex("hora_inicio")));
	        	frecuencia[i]=(c.getString(c.getColumnIndex("frecuencia")));
	        	c.moveToNext();
	        	i+=1;
	        }
	        if(i>0){
	        	bean.setId(id);
	        	bean.setNombre(nombre);
	        	bean.setFecha_inicio(fecha_inicio);
	        	bean.setFecha_fin(fecha_fin);
	        	bean.setHora_inicio(hora_inicio);
	        	bean.setFrecuencia(frecuencia);
	        	
	        	
	        }
        }
        c.close();
        
		return bean;
	}


	/**
	 * inserta un nuevo row
	 * @param bd
	 * @param valores SQLiteDatabase bd;
	 *  			   String[] valores
	 */
	public void setDatos(SQLiteDatabase bd, String[] valores) {
		try{
			String cons="insert into datos (nombre,fecha_inicio,fecha_fin,hora_inicio,frecuencia)"
					+ " values"
					+ " ('"+valores[0]+"','"+valores[1]+"','"+valores[2]+"','"+valores[3]+"','"+valores[4]+"');";
			
			bd.execSQL(cons);
		}catch(Exception e){
		}
		
	}


	/**
	 * Borras un row en especifico
	 * @param bd
	 * @param id (String) id del Row
	 */
	public void borrarDato(SQLiteDatabase bd, String id) {
		try{
			bd.execSQL("delete from datos where id = "+ id);
		}catch(Exception e){
		}
		
	}

	/**
	 * Actualiza un row de la BD
	 * @param bd
	 * @param valores  (String[]) valores
	 * @param id (String) id
	 */
	public void updateDatos(SQLiteDatabase bd, String[] valores, String id) {
				try{
					String cons="UPDATE  datos set nombre = '"+valores[0]+"',fecha_inicio = '"+valores[1]+"',fecha_fin = '"+valores[2]+
							"', hora_inicio = '"+valores[3]+"', frecuencia = '"+valores[4]+"' WHERE id = "+ id;
					bd.execSQL(cons);
				}catch(Exception e){
				}
		
	}


	public CitasBean getDatosCitas(SQLiteDatabase bd) {
		CitasBean bean = null;
		
		Cursor c = null;
        c = bd.rawQuery("select * from citas", null);
    	
        if(c!=null&&c.getCount()>0){
        	bean = new CitasBean();
        	
        	 String[] id= new String[c.getCount()];
	         String[] nombre = new String[c.getCount()];
	    	 String[] fecha= new String[c.getCount()];
	    	 String[] hora= new String[c.getCount()];
        
	        c.moveToFirst();
	        int i =0;
	        while (!c.isAfterLast()){
	        	id[i]=(c.getString(c.getColumnIndex("id")));
	        	nombre[i]=(c.getString(c.getColumnIndex("nombre")));
	        	fecha[i]=(c.getString(c.getColumnIndex("fecha")));
	        	hora[i]=(c.getString(c.getColumnIndex("hora")));
	        	c.moveToNext();
	        	i+=1;
	        }
	        if(i>0){
	        	bean.setId(id);
	        	bean.setNombre(nombre);
	        	bean.setFecha(fecha);
	        	bean.setHora(hora);
	        	
	        	
	        }
        }
        c.close();
        
		return bean;
	}


	public void updateCitas(SQLiteDatabase bd, String[] valores, String id) {
		try{
			String cons="UPDATE  citas set nombre = '"+valores[0]+"',fecha = '"+valores[1]+"',hora = '"+valores[2]+
					"' WHERE id = "+ id;
			bd.execSQL(cons);
		}catch(Exception e){
		}
		
	}


	public void setCitas(SQLiteDatabase bd, String[] valores) {
		try{
			String cons="insert into citas (nombre,fecha,hora)"
					+ " values"
					+ " ('"+valores[0]+"','"+valores[1]+"','"+valores[2]+"');";
			
			bd.execSQL(cons);
		}catch(Exception e){
		}
		
		
	}


	public void borrarCita(SQLiteDatabase bd, String id) {
		try{
			bd.execSQL("delete from citas where id = "+ id);
		}catch(Exception e){
		}
		
	}
	
	
	
}
