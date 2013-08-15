package com.odea.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.odea.dao.SeguridadDAO;
import com.odea.dao.UsuarioDAO;
import com.odea.domain.Funcionalidad;
import com.odea.domain.Usuario;

@Service
public class DAOService {
	
	@Autowired
	private transient SeguridadDAO seguridadDAO;
	
	@Autowired
	private transient UsuarioDAO usuarioDAO;
	
	
	public void setPassword(String username, String password) {
		seguridadDAO.setPassword(username, password);
	}

	public List<Funcionalidad> getFuncionalidades() {
		return seguridadDAO.getFuncionalidades();
	}
	
	public List<Usuario> getUsuariosQueTienenUnaFuncionalidad(Funcionalidad funcionalidad) {
		return seguridadDAO.getUsuariosQueTienenUnaFuncionalidad(funcionalidad);
	}
	
	public void cambiarStatusPermiso(Usuario usuario, Funcionalidad funcionalidad, Boolean habilitado) {
		seguridadDAO.cambiarStatusPermiso(usuario, funcionalidad, habilitado);
	}
	
	public Boolean getPermisoCambiarPassword(String username) {
		return seguridadDAO.getPermisoCambiarPassword(username);
	}
	
	public List<Usuario> getUsuariosConPerfiles() {
		return usuarioDAO.getUsuariosConPerfiles();
	} 
	
	public void setDedicacion(Usuario usuario, Integer dedicacion) {
		usuarioDAO.setDedicacion(usuario, dedicacion);
	}

	public void cambiarPerfil(Usuario usuario, Usuario perfil) {
		seguridadDAO.cambiarPerfil(usuario, perfil);
	}
	
	public void cambiarGrupo(Usuario usuario, String grupo) {
		seguridadDAO.cambiarGrupo(usuario, grupo);
	}
	
	public List<Usuario> getPerfiles() {
		return usuarioDAO.getPerfiles();
	}
	
	public void altaUsuario(Usuario usuario) {
		usuarioDAO.altaUsuario(usuario);
	}
	
	public void bajaUsuario(Usuario usuario) {
		usuarioDAO.bajaUsuario(usuario);
	}
	
	public void modificarUsuario(Usuario usuario) {
		usuarioDAO.modificarUsuario(usuario);
	}
	
	public void altaPerfil(Usuario perfil) {
		usuarioDAO.altaPerfil(perfil);	
	}
	
	public void bajaPerfil(Usuario perfil) {
		usuarioDAO.bajaPerfil(perfil);
	}
	
	public void modificarPerfil(Usuario perfil) {
		usuarioDAO.modificarPerfil(perfil);
	}
	
	public Usuario getPerfilDeNombre(String nombre) {
		return usuarioDAO.getPerfilDeNombre(nombre);
	}
	
	public Boolean validarPassword(Usuario usuario, String password) {
		return usuarioDAO.validarPassword(usuario, password);
	}
	
	public Boolean validarLogin(Integer id, String login) {
		return usuarioDAO.validarLogin(id, login);
	}
	
	public void modificarPasswordUsuario(Usuario usuario) {
		usuarioDAO.modificarPasswordUsuario(usuario);
	}
	
	public Usuario getUsuario(String username) {
		return usuarioDAO.getUsuario(username);
	}
	
	public List<String> getGrupos() {
		/*Temporalmente hardcodeado ya que no hay tabla de Grupos en el modelo de datos*/
		
		ArrayList<String> grupos = new ArrayList<String>();
		grupos.add("Administración");
		grupos.add("TI");
		grupos.add("E&P");
		grupos.add("ExOdea");
		grupos.add("Ninguno");
		
		return grupos;
	}

	
}



