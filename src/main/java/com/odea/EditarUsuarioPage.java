package com.odea;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.odea.behaviour.OnlyNumberBehavior;
import com.odea.domain.Usuario;
import com.odea.services.DAOService;


public class EditarUsuarioPage extends BasePage {
	
	private static final long serialVersionUID = 1L;

	@SpringBean
	private DAOService daoService;
	
	public EditarUsuarioPage () {
		
		Usuario usuario = new Usuario();
		usuario.setIdUsuario(0);
		usuario.setGrupo("Ninguno");
		
		Usuario perfil =  daoService.getPerfilDeNombre("Administrador");
		usuario.setPerfil(perfil);
		
		this.preparePage(usuario);
		
	}
	
	
	public EditarUsuarioPage (final PageParameters parameters) {
		
		Usuario usuario = new Usuario();
		
		usuario.setIdUsuario(parameters.get("id").toInteger());
		usuario.setNombre(parameters.get("nombreUsuario").toString());
		usuario.setNombreLogin(parameters.get("loginUsuario").toString());
		usuario.setGrupo(parameters.get("grupoDelUsuario").toString());
		usuario.setDedicacion(parameters.get("dedicacionUsuario").toInteger());
		
		Usuario perfil =  daoService.getPerfilDeNombre(parameters.get("perfilUsuario").toString());
		usuario.setPerfil(perfil);
		
		this.preparePage(usuario);
		
	}
	
	
	private void preparePage(Usuario usuario) {
		
        LoadableDetachableModel<List<Usuario>> lstPerfilesModel = new LoadableDetachableModel<List<Usuario>>() { 

			private static final long serialVersionUID = 1L;

			@Override
            protected List<Usuario> load() {
            	return daoService.getPerfiles();
            }
        };
		
		
        LoadableDetachableModel<List<String>> lstGruposModel = new LoadableDetachableModel<List<String>>() { 

			private static final long serialVersionUID = 1L;

			@Override
            protected List<String> load() {
            	return daoService.getGrupos();
            }
        };
        

		Form<Usuario> form = new Form<Usuario>("form", new CompoundPropertyModel<Usuario>(usuario));
		this.add(form);
		
		final FeedbackPanel feedbackPanel = new FeedbackPanel("feedbackPanel");
		feedbackPanel.setOutputMarkupId(true);
		form.add(feedbackPanel);
		
		RequiredTextField<String> nombre = new RequiredTextField<String>("nombre");
		nombre.setLabel(Model.of("Nombre"));
		form.add(nombre);
		
		RequiredTextField<String> login = new RequiredTextField<String>("nombreLogin");
		login.setLabel(Model.of("Login"));
		form.add(login);
		
		PasswordTextField password = new PasswordTextField("password");
		password.setLabel(Model.of("Password"));
		password.setRequired(true);
		form.add(password);
		
		RequiredTextField<Integer> dedicacion = new RequiredTextField<Integer>("dedicacion");
		dedicacion.add(new OnlyNumberBehavior(dedicacion.getMarkupId()));
		dedicacion.setLabel(Model.of("Dedicación"));
		form.add(dedicacion);
		
    	DropDownChoice<String> grupo = new DropDownChoice<String>("grupo", lstGruposModel);
    	grupo.setLabel(Model.of("Grupo"));
    	grupo.setRequired(true);
    	form.add(grupo);
		
    	DropDownChoice<Usuario> perfil = new DropDownChoice<Usuario>("perfil", lstPerfilesModel);
    	perfil.setLabel(Model.of("Perfil"));
    	perfil.setRequired(true);
    	form.add(perfil);
    	
    	
		AjaxButton submit = new AjaxButton("submit", form) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				Usuario usuario = (Usuario) form.getModelObject();
				
				try {
					insertarUsuario(usuario);
					this.setResponsePage(UsuariosPage.class);					
				} catch (Exception ex) {
					this.error(ex.getMessage());
					target.add(feedbackPanel);
				}
				
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedbackPanel);
			}
			
			
			
		};
		
		form.add(submit);
		
	}
	
	private void insertarUsuario(Usuario usuario) {

		if (usuario.getIdUsuario() == 0) {
			this.daoService.altaUsuario(usuario);
		} else {
			this.daoService.modificarUsuario(usuario);
		}

	}
	
}
