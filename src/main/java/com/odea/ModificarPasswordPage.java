package com.odea;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.StringValidator;

import com.odea.domain.Usuario;
import com.odea.services.DAOService;


public class ModificarPasswordPage extends BasePage {
	
	@SpringBean
	private DAOService daoService;
	
	
	public ModificarPasswordPage (final PageParameters parameters) {
		
		Usuario usuario = new Usuario();
		usuario.setIdUsuario(parameters.get("id").toInteger());
		usuario.setNombre(parameters.get("usuarioNombre").toString());
		usuario.setNombreLogin(parameters.get("usuarioLogin").toString());
		
		this.preparePage(usuario);
		
	}
	
	
	private void preparePage(Usuario usuario) {
		
		final Integer usuarioID = usuario.getIdUsuario(); 
		
        AjaxLink<UsuariosPage> volverButton = new AjaxLink<UsuariosPage>("volverButton") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				setResponsePage(UsuariosPage.class, new PageParameters().add("usuarioID", usuarioID));
			}
			
		};
		this.add(volverButton);
		
		Label usuarioNombre = new Label("usuarioNombre", usuario.getNombre());
		this.add(usuarioNombre);
		
		Label usuarioLogin = new Label("usuarioLogin", usuario.getNombreLogin());
		this.add(usuarioLogin);


		final Form<Usuario> form = new Form<Usuario>("form", new CompoundPropertyModel<Usuario>(usuario));
		this.add(form);
		
		final FeedbackPanel feedbackPanel = new FeedbackPanel("feedbackPanel");
		feedbackPanel.setOutputMarkupId(true);
		form.add(feedbackPanel);
		
		
		final PasswordTextField password = new PasswordTextField("password");
		password.setLabel(Model.of("Password"));
		password.add(new StringValidator(0, 30));
		password.setRequired(true);		
		form.add(password);
		
		
		PasswordTextField passConfirmacion = new PasswordTextField("contraseniaConfirmacion", new Model<String>(new String()));
		passConfirmacion.setLabel(Model.of("Confirmaci\363n"));
		passConfirmacion.setRequired(true);
		form.add(passConfirmacion);
		passConfirmacion.add(new StringValidator(0, 30));
		passConfirmacion.add(new IValidator<String>() {
						
			@Override
			public void validate(IValidatable<String> validatable) {
				
				if (!validatable.getValue().equals(password.getValue())) {
					error(validatable, "Las passwords ingresadas en 'Password' y 'Confimaci\363n de password' deben ser iguales.");
				}
			}
			
			private void error(IValidatable<String> validatable, String errorKey) {
				ValidationError error = new ValidationError();
				error.addKey(getClass().getSimpleName() + "." + errorKey);
				error.setMessage(errorKey);
				validatable.error(error);
			}
		});
		    	
    	
		AjaxButton submit = new AjaxButton("submit", form) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				Usuario usuario = (Usuario) form.getModelObject();
				
				try {
					ModificarPasswordPage.this.daoService.modificarPasswordUsuario(usuario);
					this.setResponsePage(UsuariosPage.class, new PageParameters().add("usuarioID", usuario.getIdUsuario()));
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
