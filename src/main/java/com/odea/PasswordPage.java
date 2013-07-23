package com.odea;

import org.apache.shiro.SecurityUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.odea.components.modalWindow.SelectModalWindow;
import com.odea.services.DAOService;

public class PasswordPage extends BasePage {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private DAOService daoService;
	
	
	public PasswordPage() {

		Form<String> form = new Form<String>("form");
		this.add(form);
		
		
		final FeedbackPanel feedBackPanel = new FeedbackPanel("feedBackPanel");
		feedBackPanel.setOutputMarkupId(true);
		form.add(feedBackPanel);
		
		final PasswordTextField passwordTextField = new PasswordTextField("passwordTextField");
		passwordTextField.setDefaultModel(new Model<String>());
		passwordTextField.setRequired(true);
		passwordTextField.setOutputMarkupId(true);
		form.add(passwordTextField);
		
		
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
				
				String username = SecurityUtils.getSubject().getPrincipal().toString();
				String password = passwordTextField.getModelObject();
				
				Boolean estaHabilitado = PasswordPage.this.daoService.getPermisoCambiarPassword(username);
				
				if (estaHabilitado) {
					PasswordPage.this.daoService.setPassword(username, password);
					passwordTextField.setModelObject(new String());
					selectModalWindow.show(target);
				} else {
					this.error("No tiene permiso para cambiar su password");
				}
				
				target.add(feedBackPanel);
				target.add(passwordTextField);
				
			}
		};
		
		form.add(submit);
		
    }
	
	
}
