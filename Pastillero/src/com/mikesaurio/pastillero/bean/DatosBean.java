package com.mikesaurio.pastillero.bean;

/**
 * Bean que contiene todos los datos de una pastilla
 * @author mikesaurio
 *
 */
public class DatosBean {
	
	String id[];
	String nombre[];
	String fecha_inicio[];
	String fecha_fin[];
	String hora_inicio[];
	String frecuencia[];
	
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
	public String[] getFecha_inicio() {
		return fecha_inicio;
	}
	
	/**
	 * asigna fecha inicio a los medicamentos
	 * @param fecha_inicio (String)
	 */
	public void setFecha_inicio(String[] fecha_inicio) {
		this.fecha_inicio = fecha_inicio;
	}
	/**
	 * Obtener los fecha final del medicamento	
	 * @return (String[]) fechas finales de los medicamentos
	 */
	public String[] getFecha_fin() {
		return fecha_fin;
	}
	
	/**
	 * asigna fecha fin a los medicamentos
	 * @param fecha_fin (String)
	 */
	public void setFecha_fin(String[] fecha_fin) {
		this.fecha_fin = fecha_fin;
	}
	/**
	 * Obtener los hora inicial del medicamento	
	 * @return (String[]) horas iniciales de los medicamentos
	 */
	public String[] getHora_inicio() {
		return hora_inicio;
	}
	
	/**
	 * asigna hora inicio a los medicamentos
	 * @param hora_inicio (String)
	 */
	public void setHora_inicio(String[] hora_inicio) {
		this.hora_inicio = hora_inicio;
	}
	/**
	 * Obtener las frecuencia del medicamento	
	 * @return (String[]) frecuencas de los medicamentos
	 */
	public String[] getFrecuencia() {
		return frecuencia;
	}
	
	/**
	 * asigna frecuencia a los medicamentos
	 * @param frecuencia (String)
	 */
	public void setFrecuencia(String[] frecuencia) {
		this.frecuencia = frecuencia;
	}
	
	
	

}
