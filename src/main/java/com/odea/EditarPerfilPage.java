package com.odea;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.odea.domain.Usuario;
import com.odea.services.DAOService;

public class EditarPerfilPage extends BasePage {

	private static final long serialVersionUID = 1L;
	
	@SpringBean
	protected DAOService daoService;

	public EditarPerfilPage() {
		
		Usuario perfil = new Usuario();
		perfil.setIdUsuario(0);
		
		this.preparePage(perfil);
	}
	
	public EditarPerfilPage(final PageParameters parameters) {
		
		Usuario perfil = new Usuario();
		
		perfil.setIdUsuario(parameters.get("id").toInt());
		perfil.setNombre(parameters.get("nombrePerfil").toString());
		
		this.preparePage(perfil);
		
	}
	
	
	protected void preparePage(Usuario perfil) {
		
		Form<Usuario> form = new Form<Usuario>("form", new CompoundPropertyModel<Usuario>(perfil));
		this.add(form);
		
		final FeedbackPanel feedbackPanel = new FeedbackPanel("feedbackPanel");
		feedbackPanel.setOutputMarkupId(true);
		form.add(feedbackPanel);
		
		RequiredTextField<String> nombre = new RequiredTextField<String>("nombre");
		nombre.setLabel(Model.of("nombre"));
		form.add(nombre);
		
		AjaxButton submit = new AjaxButton("submitButton") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				
				Usuario perfil = (Usuario)form.getModelObject();
				insertarPerfil(perfil);
				setResponsePage(PerfilesPage.class);
				
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedbackPanel);
			}
			
		};
		
		form.add(submit);

	}

	protected void insertarPerfil(Usuario perfil) {
		
		if (perfil.getIdUsuario() == 0) {
			this.daoService.altaPerfil(perfil);
		} else {
			this.daoService.modificarPerfil(perfil);
		}
		
	}
	
}
