package com.odea;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import com.odea.behaviour.OnlyNumberBehavior;
import com.odea.domain.Usuario;
import com.odea.services.DAOService;


public class AltaUsuarioPage extends BasePage {
	
	
	@SpringBean
	private DAOService daoService;
	

	public AltaUsuarioPage () {
		Usuario usuario = new Usuario();
		usuario.setGrupo("Ninguno");
		
		this.preparePage(usuario);
	}
	
	
	
	private void preparePage(Usuario usuario) {
		
        LoadableDetachableModel<List<Usuario>> lstPerfilesModel = new LoadableDetachableModel<List<Usuario>>() { 

			@Override
            protected List<Usuario> load() {
            	return daoService.getPerfiles();
            }
        };
		
		
        LoadableDetachableModel<List<String>> lstGruposModel = new LoadableDetachableModel<List<String>>() { 

			@Override
            protected List<String> load() {
            	return daoService.getGrupos();
            }
        };
        
        
        AjaxLink<UsuariosPage> volverButton = new AjaxLink<UsuariosPage>("volverButton") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				setResponsePage(UsuariosPage.class);
			}
		};
		this.add(volverButton);


		Form<Usuario> form = new Form<Usuario>("form", new CompoundPropertyModel<Usuario>(usuario));
		this.add(form);
		
		final FeedbackPanel feedbackPanel = new FeedbackPanel("feedbackPanel");
		feedbackPanel.setOutputMarkupId(true);
		form.add(feedbackPanel);
		
		RequiredTextField<String> nombre = new RequiredTextField<String>("nombre");
		nombre.add(new StringValidator(1,30));
		nombre.setLabel(Model.of("Nombre"));
		form.add(nombre);
		
		RequiredTextField<String> login = new RequiredTextField<String>("nombreLogin");
		login.add(new StringValidator(1,30));
		login.setLabel(Model.of("Login"));
		form.add(login);
		
		
		PasswordTextField password = new PasswordTextField("password");
		password.setLabel(Model.of("Password"));
		password.add(new StringValidator(0, 30));
		password.setRequired(true);
		form.add(password);
		
		RequiredTextField<Integer> dedicacion = new RequiredTextField<Integer>("dedicacion");
		dedicacion.add(new OnlyNumberBehavior(dedicacion.getMarkupId()));
		dedicacion.setLabel(Model.of("Dedicaci\363n"));
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

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				Usuario usuario = (Usuario) form.getModelObject();
				
				try {
					AltaUsuarioPage.this.daoService.altaUsuario(usuario);
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

}
