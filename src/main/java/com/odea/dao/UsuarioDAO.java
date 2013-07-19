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
		
		List<Usuario> usuarios = jdbcTemplate.query("SELECT u.u_id, u.u_login, u.u_password, u.u_name, u.u_comanager, u.u_grupo, p.u_name, p.u_login FROM users u, SEC_ASIG_PERFIL ap, users p WHERE u.u_id = ap.SEC_USUARIO_ID AND ap.SEC_PERFIL_ID = p.u_id ORDER BY u.u_name ASC", new RowMapperUsuario2());
		
		return usuarios;
	}
	
	//RowMappers
	
	private class RowMapperUsuario implements RowMapper<Usuario>{

		@Override
		public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Usuario(rs.getInt(1), rs.getString(2), rs.getString(3));
		}
		
	}
	
	private class RowMapperUsuario2 implements RowMapper<Usuario>{

		@Override
		public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
			Usuario perfil = new Usuario(0, rs.getString(7), rs.getString(8), "PasswordNula");
			Usuario usuario = new Usuario(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), (rs.getInt(5) == 1), perfil);
			usuario.setGrupo(rs.getString(6));
			
			return usuario;
		}
		
	}
	
}