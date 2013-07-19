package com.odea.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.odea.dao.SeguridadDAO;
import com.odea.domain.Funcionalidad;
import com.odea.domain.Usuario;

@Service
public class DAOService {
	
	@Autowired
	public transient SeguridadDAO seguridadDAO;
	
	public Integer getPrueba() {
		return seguridadDAO.getPrueba();
	}
	
	
	public void setPassword(String username, String password) {
		seguridadDAO.setPassword(username, password);
	}
	
	public List<Usuario> getPerfiles() {
		return seguridadDAO.getPerfiles();
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
	
}



