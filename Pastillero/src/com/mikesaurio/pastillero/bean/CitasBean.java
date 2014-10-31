package com.mikesaurio.pastillero.bean;

/**
 * Bean que contiene todos los datos de una pastilla
 * @author mikesaurio
 *
 */
public class CitasBean {
	
	String id[];
	String nombre[];
	String fecha[];
	String hora[];

	
	/**
	 * Obtener los id del medicamento	
	 * @return (String[]) id de los medicamentos
	 */
	public String[] getId() {
		return id;
	}
	
	/**
	 * asigna id a los medicamentos
	 * @param id (String)
	 */
	public void setId(String[] id) {
		this.id = id;
	}
	/**
	 * Obtener los nombres del medicamento	
	 * @return (String[]) nombres de los medicamentos
	 */
	public String[] getNombre() {
		return nombre;
	}
	
	/**
	 * asigna nombre a los medicamentos
	 * @param nombre (String)
	 */
	public void setNombre(String[] nombre) {
		this.nombre = nombre;
	}
	/**
	 * Obtener los fecha inicio del medicamento	
	 * @return (String[]) fechas inicio de los medicamentos
	 */
	public String[] getFecha() {
		return fecha;
	}
	
	/**
	 * asigna fecha inicio a los medicamentos
	 * @param fecha_inicio (String)
	 */
	public void setFecha(String[] fecha) {
		this.fecha = fecha;
	}
	
	/**
	 * Obtener los hora inicial del medicamento	
	 * @return (String[]) horas iniciales de los medicamentos
	 */
	public String[] getHora() {
		return hora;
	}
	
	/**
	 * asigna hora inicio a los medicamentos
	 * @param hora_inicio (String)
	 */
	public void setHora(String[] hora) {
		this.hora = hora;
	}
	
	
	

}
