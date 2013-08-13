package com.odea;

import org.apache.shiro.SecurityUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.StringValidator;

import com.odea.components.modalWindow.SelectModalWindow;
import com.odea.domain.Usuario;
import com.odea.services.DAOService;

public class PasswordPage extends BasePage {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private DAOService daoService;
	
	
	public PasswordPage() {

		Usuario usuario = this.daoService.getUsuario(SecurityUtils.getSubject().getPrincipal().toString()); 
		
		final Form<Usuario> form = new Form<Usuario>("form", new CompoundPropertyModel<Usuario>(usuario));
		this.add(form);
		
		
		final FeedbackPanel feedBackPanel = new FeedbackPanel("feedBackPanel");
		feedBackPanel.setOutputMarkupId(true);
		form.add(feedBackPanel);
		
		Label nombreUsuario = new Label("nombreUsuario", usuario.getNombre());
		form.add(nombreUsuario);
		
		final PasswordTextField passActual = new PasswordTextField("contraseniaActual", new Model<String>(new String()));
		passActual.setLabel(Model.of("Password actual"));
		passActual.add(new StringValidator(0,30));
		passActual.setRequired(true);
		passActual.add(new IValidator<String>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void validate(IValidatable<String> validatable) {
				
				Boolean correcto = daoService.validarPassword(form.getModelObject(), validatable.getValue());
				
				if (!correcto) {
					error(validatable, "La password ingresada en 'Password actual' no es correcta.");
				}
				
			}
			
			private void error(IValidatable<String> validatable, String errorKey) {
				ValidationError error = new ValidationError();
				error.addKey(getClass().getSimpleName() + "." + errorKey);
				error.setMessage(errorKey);
				validatable.error(error);
			}
		});
		
		form.add(passActual);
		
		final PasswordTextField password = new PasswordTextField("password");
		password.setLabel(Model.of("Nueva Password"));
		password.add(new StringValidator(0, 30));
		password.setRequired(true);		
		form.add(password);
		
		
		final PasswordTextField passConfirmacion = new PasswordTextField("contraseniaConfirmacion", new Model<String>(new String()));
		passConfirmacion.setLabel(Model.of("Confirmación"));
		passConfirmacion.setRequired(true);
		passConfirmacion.add(new StringValidator(0, 30));
		passConfirmacion.add(new IValidator<String>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void validate(IValidatable<String> validatable) {
				
				if (!validatable.getValue().equals(password.getValue())) {
					error(validatable, "Las passwords ingresadas en 'Nueva Password' y 'Confimación' deben ser iguales.");
				}
			}
			
			private void error(IValidatable<String> validatable, String errorKey) {
				ValidationError error = new ValidationError();
				error.addKey(getClass().getSimpleName() + "." + errorKey);
				error.setMessage(errorKey);
				validatable.error(error);
			}
		});
		
		form.add(passConfirmacion);
		
		
		
		final SelectModalWindow selectModalWindow = new SelectModalWindow("modalWindow"){

			private static final long serialVersionUID = 1L;

			public void onCancel(AjaxRequestTarget target) {
                close(target);
            }
        };
        
        form.add(selectModalWindow);
        
		
		AjaxButton submit = new AjaxButton("submit", form) {

			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				
				Usuario usuario = (Usuario) form.getModelObject();
				
				String nuevaPassword = usuario.getPassword();
				
				Boolean estaHabilitado = PasswordPage.this.daoService.getPermisoCambiarPassword(usuario.getNombreLogin());
				
				if (estaHabilitado) {
					PasswordPage.this.daoService.setPassword(usuario.getNombreLogin(), nuevaPassword);
					selectModalWindow.show(target);
					
				} else {
					this.error("No tiene permiso para cambiar su password");
				}
				
				target.add(feedBackPanel);
				
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedBackPanel);
			}
			
			
		};
		
		form.add(submit);
		
    }
	
	
}
