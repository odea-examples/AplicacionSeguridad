package com.odea;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.StringValidator;

import com.odea.behaviour.OnlyNumberBehavior;
import com.odea.domain.Usuario;
import com.odea.services.DAOService;


public class ModificarUsuarioPage extends BasePage {
	
	private static final long serialVersionUID = 1L;

	@SpringBean
	private DAOService daoService;
	
	private String loginActual;
	
	public ModificarUsuarioPage (final PageParameters parameters) {
		
		Usuario usuario = new Usuario();
		
		usuario.setIdUsuario(parameters.get("id").toInteger());
		usuario.setNombre(parameters.get("nombreUsuario").toString());
		usuario.setNombreLogin(parameters.get("loginUsuario").toString());
		usuario.setGrupo(parameters.get("grupoDelUsuario").toString());
		usuario.setDedicacion(parameters.get("dedicacionUsuario").toInteger());
		
		Usuario perfil =  daoService.getPerfilDeNombre(parameters.get("perfilUsuario").toString());
		usuario.setPerfil(perfil);
		
		this.loginActual = parameters.get("loginUsuario").toString();
		
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

        BookmarkablePageLink<UsuariosPage> linkVolver = new BookmarkablePageLink<UsuariosPage>("linkVolver", UsuariosPage.class);
        this.add(linkVolver);

		final Form<Usuario> form = new Form<Usuario>("form", new CompoundPropertyModel<Usuario>(usuario));
		this.add(form);
		
		final FeedbackPanel feedbackPanel = new FeedbackPanel("feedbackPanel");
		feedbackPanel.setOutputMarkupId(true);
		form.add(feedbackPanel);
		
		RequiredTextField<String> nombre = new RequiredTextField<String>("nombre");
		nombre.add(new StringValidator(1,30));
		nombre.setLabel(Model.of("Nombre"));
		form.add(nombre);
		
		RequiredTextField<String> login = new RequiredTextField<String>("nombreLogin");
		login.setLabel(Model.of("Login"));
		form.add(login);
		login.add(new StringValidator(1,30));
		login.add(new IValidator<String>() {
			private static final long serialVersionUID = 1L;
			
			
			@Override
			public void validate(IValidatable<String> validatable) {
				Boolean correcto = daoService.validarLogin(form.getModelObject().getIdUsuario(), validatable.getValue());
				
				if (!correcto) {
					error(validatable, "Ya existe un usuario con el mismo login. Por favor, ingrese otro.");
				}
				
			}
			
			private void error(IValidatable<String> validatable, String errorKey) {
				ValidationError error = new ValidationError();
				error.addKey(getClass().getSimpleName() + "." + errorKey);
				error.setMessage(errorKey);
				validatable.error(error);
			}
			
			
		});
		
		
		
		final PasswordTextField passActual = new PasswordTextField("contraseniaActual", new Model<String>(new String()));
		passActual.setLabel(Model.of("Password anterior"));
		passActual.setRequired(true);
		form.add(passActual);
		passActual.add(new StringValidator(0, 30));
		passActual.add(new IValidator<String>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void validate(IValidatable<String> validatable) {
				Boolean correcto = daoService.validarPassword(ModificarUsuarioPage.this.loginActual, validatable.getValue());
				
				if (!correcto) {
					error(validatable, "La password ingresada en 'Password actual' no es correcta");
				}
				
			}
			
			private void error(IValidatable<String> validatable, String errorKey) {
				ValidationError error = new ValidationError();
				error.addKey(getClass().getSimpleName() + "." + errorKey);
				error.setMessage(errorKey);
				validatable.error(error);
			}
		});
		
		final PasswordTextField password = new PasswordTextField("password");
		password.setLabel(Model.of("Password"));
		password.add(new StringValidator(0, 30));
		password.setRequired(true);
		form.add(password);
		
		PasswordTextField passConfirmacion = new PasswordTextField("contraseniaConfirmacion", new Model<String>(new String()));
		passConfirmacion.setLabel(Model.of("Confirmación de password"));
		passConfirmacion.setRequired(true);
		form.add(passConfirmacion);
		passConfirmacion.add(new StringValidator(0, 30));
		passConfirmacion.add(new IValidator<String>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void validate(IValidatable<String> validatable) {
				
				if (!validatable.getValue().equals(password.getValue())) {
					error(validatable, "Las passwords ingresadas en 'Password' y 'Confimación de password' deben ser iguales");
				}
			}
			
			private void error(IValidatable<String> validatable, String errorKey) {
				ValidationError error = new ValidationError();
				error.addKey(getClass().getSimpleName() + "." + errorKey);
				error.setMessage(errorKey);
				validatable.error(error);
			}
		});
		
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
					ModificarUsuarioPage.this.daoService.modificarUsuario(usuario);
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
