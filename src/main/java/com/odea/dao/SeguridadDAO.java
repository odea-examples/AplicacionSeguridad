package com.odea.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.odea.domain.Funcionalidad;
import com.odea.domain.Usuario;

@Repository
public class SeguridadDAO extends AbstractDAO {
	
    private static final Logger logger = LoggerFactory.getLogger(SeguridadDAO.class);
	
	public Integer getPrueba() {
		
		logger.debug("SE BUSCA EL DATO");
		
		Integer resultado = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM users");
		
		logger.debug("SE ENCONTRO EL DATO");
		
		return resultado;
	}
	
	
	
	public void setPassword(String username, String password) {
		
		logger.debug("SE PROCEDE A CAMBIAR LA CONTRASEÑA DEL USUARIO - username: " + username);
			
		Integer filasActualizadas = jdbcTemplate.update("UPDATE users SET u_password = password(?) WHERE u_login = ?", password, username);
			
		
		if (filasActualizadas.equals(1)) {
			logger.debug("SE HA CAMBIADO LA CONTRASEÑA DEL USUARIO - username: " + username);
		}
		
	}
	
	public Boolean getPermisoCambiarPassword(String username) {
		Boolean resultado;
		
		String sql = "";
		sql += "SELECT COUNT(*) ";
		sql += "FROM users usuario, SEC_ASIG_PERFIL ap, users perfil, SEC_PERMISO perm, SEC_FUNCIONALIDAD func ";
		sql += "WHERE ";
		sql += "usuario.u_id = ap.SEC_USUARIO_ID AND ap.SEC_PERFIL_ID = perfil.u_id AND perfil.u_id = perm.SEC_USUARIO_PERFIL_ID AND ";
		sql += "perm.SEC_FUNCIONALIDAD_ID = func.SEC_FUNCIONALIDAD_ID AND ";
		sql += "usuario.u_login = ? AND ";
		sql += "func.CONCEPTO = 'Cambiar password'";
		
		Integer cantidad = jdbcTemplate.queryForInt(sql, username);
		
		
		if (cantidad.equals(1)) {
			
			sql = "";
			sql += "SELECT perm.ESTADO ";
			sql += "FROM users usuario, SEC_ASIG_PERFIL ap, users perfil, SEC_PERMISO perm, SEC_FUNCIONALIDAD func ";
			sql += "WHERE ";
			sql += "usuario.u_id = ap.SEC_USUARIO_ID AND ap.SEC_PERFIL_ID = perfil.u_id AND perfil.u_id = perm.SEC_USUARIO_PERFIL_ID AND ";
			sql += "perm.SEC_FUNCIONALIDAD_ID = func.SEC_FUNCIONALIDAD_ID AND ";
			sql += "usuario.u_login = ? AND ";
			sql += "func.CONCEPTO = 'Cambiar password'";
			
			String estado = jdbcTemplate.queryForObject(sql, String.class, username);
			
			if(estado.equals("Habilitado")) {
				resultado = true;
			} else {
				resultado = false;
			}
			
		} else {
			resultado = false;
		}
		
		
		return resultado;
	}
	
	
	
	public List<Usuario> getUsuariosQueTienenUnaFuncionalidad(Funcionalidad funcionalidad) {
		
		List<Usuario> usuarios = jdbcTemplate.query("SELECT u_id, u_login, u_password, u_name FROM users WHERE u_id in (SELECT SEC_USUARIO_PERFIL_ID FROM SEC_PERMISO WHERE SEC_FUNCIONALIDAD_ID = ? AND ESTADO = 'Habilitado')", new RowMapperUsuario(), funcionalidad.getID());
		return usuarios;
	}

	public void cambiarStatusPermiso(Usuario usuario, Funcionalidad funcionalidad, Boolean habilitado) {
		
		String estadoPermiso = habilitado ? "Habilitado" : "Inhabilitado";
		
		Integer filasActualizadas = jdbcTemplate.update("UPDATE SEC_PERMISO SET ESTADO = '"+ estadoPermiso +"' WHERE SEC_USUARIO_PERFIL_ID = ? AND SEC_FUNCIONALIDAD_ID = ? ", usuario.getIdUsuario(),funcionalidad.getID());
		
		if(filasActualizadas.equals(0))
		{
			jdbcTemplate.update("INSERT INTO SEC_PERMISO VALUES(0,?,?,'Habilitado')",funcionalidad.getID(),usuario.getIdUsuario());
		}
		
		logger.debug("Permiso actualizado: Permiso: " + funcionalidad.getID() + " - Usuario: " + usuario.getNombre() + " - Estado: " + estadoPermiso);
	}
	
	public String getPerfil(String loginUsuario) {
		
		logger.debug("Se busca nombre de perfil del usuario: " + loginUsuario);
		
		String sql = "SELECT p.u_name FROM users u, users p, SEC_ASIG_PERFIL ap WHERE u.u_id = ap.SEC_USUARIO_ID AND ap.SEC_PERFIL_ID = p.u_id AND u.u_login = ?";
		
		String nombrePerfil = jdbcTemplate.queryForObject(sql, String.class, loginUsuario);
		
		if (nombrePerfil != null){
			logger.debug("Perfil encontrado: " + nombrePerfil);
		}
		
		return nombrePerfil;
	}
	
public void cambiarGrupo(Usuario usuario, String grupo) {
		
		logger.debug("SE PROCEDE A CAMBIAR AL USUARIO - ID: "+ usuario.getIdUsuario() +" - Login: " + usuario.getNombreLogin() + " - AL GRUPO: " + grupo);
		
		jdbcTemplate.update("UPDATE users SET u_grupo = ? WHERE u_id = ?", grupo, usuario.getIdUsuario());
		
		logger.debug("SE HA CAMBIADO AL USUARIO - ID: "+ usuario.getIdUsuario() +" - Login: " + usuario.getNombreLogin() + " - AL GRUPO: " + grupo);
		
	}
	
	public void cambiarPerfil(Usuario usuario, String nombrePerfil) {

		int perfilID = jdbcTemplate.queryForInt("SELECT u_id FROM users WHERE u_login = ? AND u_tipo = 'P'", nombrePerfil);
		
		String sql = "UPDATE SEC_ASIG_PERFIL SET SEC_PERFIL_ID = ? WHERE SEC_USUARIO_ID = ?";
		
		jdbcTemplate.update(sql, perfilID, usuario.getIdUsuario());
		
		logger.debug("CAMBIO DE ROL REALIZADO");
	}
	
	
	//ROWMAPPERS
	
	private class RowMapperUsuario implements RowMapper<Usuario> {
		@Override
		public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			Usuario unUsuario = new Usuario(rs.getInt(1), rs.getString(2), rs.getString(3));
			
			unUsuario.setNombre(rs.getString(4));
			
			return unUsuario;
		}
	}
	
	
	public List<Funcionalidad> getFuncionalidades() {
		
		List<Funcionalidad> funcionalidades = jdbcTemplate.query("SELECT SEC_FUNCIONALIDAD_ID, GRUPO, CONCEPTO, ESTADO FROM SEC_FUNCIONALIDAD", new RowMapperFuncionalidad());
		
		return funcionalidades;
	}
	
	private class RowMapperFuncionalidad implements RowMapper<Funcionalidad>{
		@Override
		public Funcionalidad mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			Funcionalidad funcionalidad = new Funcionalidad();
			funcionalidad.setID(rs.getInt(1));
			funcionalidad.setGrupo(rs.getString(2));
			funcionalidad.setConcepto(rs.getString(3));
			funcionalidad.setEstado(rs.getString(4));
			
			return funcionalidad;
		}
	}	
	
}
