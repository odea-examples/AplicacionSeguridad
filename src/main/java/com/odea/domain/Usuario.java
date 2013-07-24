package com.odea.domain;

import java.io.Serializable;

public class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int idUsuario;
	private String nombreLogin;
	private String password;
	private String nombre;
	private Usuario perfil;
	private String grupo;
	private Integer dedicacion;
	
	public Usuario() {
		
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
