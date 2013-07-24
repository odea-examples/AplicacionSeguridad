package com.odea.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.odea.domain.Usuario;



@Repository
public class UsuarioDAO extends AbstractDAO {
	
    private static final Logger logger = LoggerFactory.getLogger(UsuarioDAO.class);
		
	public Usuario getUsuario(String nombre, String password){
		Usuario usuario = jdbcTemplate.queryForObject("SELECT u_id, u_login, u_password FROM users WHERE u_login=? AND u_password=password(?)", 
				new RowMapperUsuario(), nombre, password);
		
		return usuario;
	}
	
	
	public List<Usuario> getUsuariosConPerfiles() {
		
		List<Usuario> usuarios = jdbcTemplate.query("SELECT u.u_id, u.u_login, u.u_password, u.u_name, u.u_comanager, u.u_grupo, p.u_id, p.u_name, p.u_login FROM users u, SEC_ASIG_PERFIL ap, users p WHERE u.u_id = ap.SEC_USUARIO_ID AND ap.SEC_PERFIL_ID = p.u_id ORDER BY u.u_name ASC", new RowMapperUsuarioConPerfil());
		
		return usuarios;
	}
	
	
	public List<String> getNombresPerfiles() {
		List<String> nombresPerfiles = jdbcTemplate.query("SELECT u_name FROM users WHERE u_tipo = 'P' ORDER BY u_name ASC", new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
			
		});
		
		return nombresPerfiles;
	}
	
	public Integer getDedicacion(Usuario usuario){
		return jdbcTemplate.queryForInt("SELECT dedicacion FROM dedicacion_usuario WHERE usuario_id=? AND fecha_hasta is NULL", usuario.getIdUsuario());
	}
	
	public void setDedicacion(Usuario usuario, int dedicacion){
		Date fechaActual = new Date();
		try {
			jdbcTemplate.update("UPDATE dedicacion_usuario SET fecha_hasta=? WHERE fecha_hasta is NULL AND usuario_id=?", fechaActual, usuario.getIdUsuario());
			jdbcTemplate.update("INSERT INTO dedicacion_usuario (usuario_id, fecha_desde, dedicacion) VALUES (?,?,?)", usuario.getIdUsuario(), fechaActual, dedicacion);						
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public List<Usuario> getPerfiles() {
		List<Usuario> listaPerfiles = jdbcTemplate.query("SELECT u_id, u_name, u_login FROM users WHERE u_tipo = 'P' ORDER BY u_name ASC", new RowMapperPerfil());
		return listaPerfiles;
	}
	
	
	public void altaUsuario(Usuario usuario) {
	
		Integer cantidad = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM users WHERE u_login = ? AND u_tipo = 'U'", usuario.getNombreLogin());
		
		if (cantidad.equals(1)) {
			throw new RuntimeException("El nombre de login ya está en uso. Por favor elija otro.");
		}
		
		String sqlUsuario = "INSERT INTO users (u_name, u_login, u_password, u_grupo, u_tipo) VALUES (?, ?, password(?), ?, 'U')";
		jdbcTemplate.update(sqlUsuario, usuario.getNombre(), usuario.getNombreLogin(), usuario.getPassword(), usuario.getGrupo());
		
		Integer idUsuario = jdbcTemplate.queryForInt("SELECT u_id FROM users WHERE u_login = ?", usuario.getNombreLogin());
		Integer idPerfil = usuario.getPerfil().getIdUsuario();
		
		String sqlDedicacion = "INSERT INTO dedicacion_usuario (usuario_id, fecha_desde, dedicacion) VALUES (?,?,?)";
		jdbcTemplate.update(sqlDedicacion, idUsuario, new Date(), usuario.getDedicacion());
				
		String sqlPerfil = "INSERT INTO SEC_ASIG_PERFIL (SEC_USUARIO_ID, SEC_PERFIL_ID) VALUES (?,?)";
		jdbcTemplate.update(sqlPerfil, idUsuario, idPerfil);
		
		logger.debug("NUEVO USUARIO CREADO - USERNAME: " + usuario.getNombreLogin());
		
	}
	
	public void bajaUsuario(Usuario usuario) {
		
		jdbcTemplate.update("DELETE FROM dedicacion_usuario WHERE usuario_id = ?", usuario.getIdUsuario());
		jdbcTemplate.update("DELETE FROM SEC_ASIG_PERFIL WHERE SEC_USUARIO_ID = ?", usuario.getIdUsuario());
		jdbcTemplate.update("DELETE FROM users WHERE u_id = ?", usuario.getIdUsuario());
		
		logger.debug("USUARIO BORRADO - USERNAME: " + usuario.getNombreLogin());
	}
	
	
	public void modificarUsuario(Usuario usuario) {

		jdbcTemplate.update("UPDATE users SET u_name = ?, u_login = ?, u_password = password(?), u_grupo = ? WHERE u_tipo = 'U' AND u_id = ?", usuario.getNombre(), usuario.getNombreLogin(), usuario.getPassword(), usuario.getGrupo(), usuario.getIdUsuario());
		
		jdbcTemplate.update("UPDATE dedicacion_usuario SET fecha_hasta = ? WHERE fecha_hasta is NULL AND usuario_id = ?", new Date(), usuario.getIdUsuario());
		jdbcTemplate.update("INSERT INTO dedicacion_usuario (usuario_id, fecha_desde, dedicacion) VALUES (?,?,?)", usuario.getIdUsuario(), new Date(), usuario.getDedicacion());

		jdbcTemplate.update("UPDATE SEC_ASIG_PERFIL SET SEC_PERFIL_ID = ? WHERE SEC_USUARIO_ID = ?", usuario.getPerfil().getIdUsuario(), usuario.getIdUsuario());
		
		logger.debug("SE HA MODIFICADO EL USUARIO - ID: " + usuario.getIdUsuario());
	}
	
	
	public void altaPerfil(Usuario perfil) {
		
		Integer cantidad = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM users WHERE u_name = ? AND u_tipo = 'P'", perfil.getNombre());
		
		if (cantidad.equals(1)) {
			throw new RuntimeException("El nombre ya está en uso. Por favor elija otro.");
		}
		
		jdbcTemplate.update("INSERT INTO users (u_login, u_name, u_tipo) VALUES (?,?,'P')", perfil.getNombre(), perfil.getNombre());
		
		logger.debug("ALTA DE PERFIL - NOMBRE: " + perfil.getNombre());
	}
	
	public void bajaPerfil(Usuario perfil) {
		
		Integer cantidadUsuarios = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM SEC_ASIG_PERFIL WHERE SEC_PERFIL_ID = ?", perfil.getIdUsuario());
		
		if (cantidadUsuarios > 0) {
			throw new RuntimeException("El perfil no puede ser borrado ya que tiene usuarios asignados.");
		}
		
		jdbcTemplate.update("DELETE FROM SEC_PERMISO WHERE SEC_USUARIO_PERFIL_ID = ?", perfil.getIdUsuario());
		
		jdbcTemplate.update("DELETE FROM users WHERE u_id = ?", perfil.getIdUsuario());
		
		logger.debug("PERFIL BORRADO - NOMBRE: " + perfil.getNombre());
	}
	
	public void modificarPerfil(Usuario perfil) {
		
		Integer cantidad = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM users WHERE u_name = ? AND u_tipo = 'P' AND u_id != ?", perfil.getNombre(), perfil.getIdUsuario());
		
		if (cantidad.equals(1)) {
			throw new RuntimeException("El nombre ya está en uso. Por favor elija otro.");
		}
		
		jdbcTemplate.update("UPDATE users SET u_name = ?, u_login = ? WHERE u_tipo = 'P' AND u_id = ?", perfil.getNombre(), perfil.getNombre(), perfil.getIdUsuario());
			
		logger.debug("SE HA MODIFICADO EL PERFIL - ID: " + perfil.getIdUsuario());
	}
	
	
	public Usuario getPerfilDeNombre(String nombre) {
		Usuario perfil = jdbcTemplate.queryForObject("SELECT u_id, u_name, u_login FROM users WHERE u_tipo = 'P' AND u_name = ?", new RowMapperPerfil(), nombre);
		
		return perfil;
	}
	
	public Boolean validarPassword(String login, String password) {
		Integer cantidadContrasenia = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM users WHERE u_login = ? AND u_password = password(?) AND u_tipo = 'U'", login, password);
		
		Boolean resultado = cantidadContrasenia.equals(1); 
		
		return resultado;
		
	}
	
	public Boolean validarLogin(Integer id, String login) {
		Integer usuariosConMismoLogin = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM users WHERE u_login = ? AND u_tipo = 'U' AND u_id != ?", login, id);
		
		Boolean resultado = usuariosConMismoLogin.equals(0);
		
		return resultado;
	}
	
	
	//RowMappers
	
	private class RowMapperUsuario implements RowMapper<Usuario>{

		@Override
		public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Usuario(rs.getInt(1), rs.getString(2), rs.getString(3));
		}
		
	}
	
	private class RowMapperUsuarioConPerfil implements RowMapper<Usuario>{

		@Override
		public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			Usuario perfil = new Usuario();
			perfil.setIdUsuario(rs.getInt(7));
			perfil.setNombre(rs.getString(8));
			perfil.setNombreLogin(rs.getString(9));
			
			
			Usuario usuario = new Usuario(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), (rs.getInt(5) == 1), perfil);
			usuario.setGrupo(rs.getString(6));
			
			return usuario;
		}
		
	}
	
	private class RowMapperPerfil implements RowMapper<Usuario> {

		@Override
		public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
			Usuario perfil = new Usuario();
			
			perfil.setIdUsuario(rs.getInt(1));
			perfil.setNombre(rs.getString(2));
			perfil.setNombreLogin(rs.getString(3));
			
			return perfil;
		}
		
	}



	

	
}