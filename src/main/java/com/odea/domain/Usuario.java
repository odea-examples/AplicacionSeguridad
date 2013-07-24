package com.odea.domain;

import java.io.Serializable;

public class Usuario implements Serializable {
	
	private int idUsuario;
	private String nombreLogin;
	private String password;
	private String nombre;
	private Boolean esCoManager;
	private Usuario perfil;
	private String grupo;
	private Integer dedicacion;
	
	public Usuario() {
		
	}
	
	public Usuario(int id, String nombreLogin, String password, String nombre, Boolean coManager, Usuario perfil) {
		this.idUsuario = id;
		this.nombreLogin = nombreLogin;
		this.password = password;
		this.nombre = nombre;
		this.esCoManager = coManager;
		this.perfil = perfil;
	}
	
	public Usuario(int id, String nombreLogin, String password,String nombre,Boolean coManager) {
		this.idUsuario = id;
		this.nombreLogin = nombreLogin;
		this.password = password;
		this.nombre = nombre;
		this.esCoManager = coManager;
	}

	public Usuario(int id, String nombre, String password) {
		this.idUsuario = id;
		this.nombreLogin = nombre;
		this.password = password;
		this.nombre = "default";
		this.esCoManager = false;
	}
	
	public Usuario(int id, String nombre, String login, String password) {
		this.idUsuario = id;
		this.nombreLogin = login;
		this.password = password;
		this.nombre = nombre;
		this.esCoManager = false;
	}
	
	
	public Boolean getEsCoManager() {
		return esCoManager;
	}
	public void setEsCoManager(Boolean esCoManager) {
		this.esCoManager = esCoManager;
	}
	
	
	public Usuario(int id){
		this.idUsuario = id;
	}
	
	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNombreLogin() {
		return nombreLogin;
	}
	public void setNombreLogin(String nombre) {
		this.nombreLogin = nombre;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}	
	
	public Usuario getPerfil() {
		return perfil;
	}

	public void setPerfil(Usuario perfil) {
		this.perfil = perfil;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public Integer getDedicacion() {
		return dedicacion;
	}

	public void setDedicacion(Integer dedicacion) {
		this.dedicacion = dedicacion;
	}

	@Override
	public String toString() {
		return this.nombreLogin;
	}

	@Override
	public boolean equals(Object obj) {
		return this.nombreLogin.equals(obj.toString());
	}
	
	
}
