package com.odea;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
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
	private DAOService daoService;

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
	
	
	private void preparePage(Usuario perfil) {
		/*
        BookmarkablePageLink<PerfilesPage> linkVolver = new BookmarkablePageLink<PerfilesPage>("linkVolver", PerfilesPage.class);
        this.add(linkVolver);
        */
		
        AjaxLink<PerfilesPage> volverButton = new AjaxLink<PerfilesPage>("volverButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				setResponsePage(PerfilesPage.class);
			}
		};
		this.add(volverButton);

		
		Form<Usuario> form = new Form<Usuario>("form", new CompoundPropertyModel<Usuario>(perfil));
		this.add(form);
		
		final FeedbackPanel feedbackPanel = new FeedbackPanel("feedbackPanel");
		feedbackPanel.setOutputMarkupId(true);
		form.add(feedbackPanel);
		
		RequiredTextField<String> nombre = new RequiredTextField<String>("nombre");
		nombre.setLabel(Model.of("Nombre"));
		form.add(nombre);
		
		AjaxButton submit = new AjaxButton("submitButton") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				
				try {
					Usuario perfil = (Usuario)form.getModelObject();
					EditarPerfilPage.this.insertarPerfil(perfil);					
					setResponsePage(PerfilesPage.class);
					
				} catch (Exception ex) {
					error(ex.getMessage());
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

	private void insertarPerfil(Usuario perfil) {
		
		if (perfil.getIdUsuario() == 0) {
			this.daoService.altaPerfil(perfil);
		} else {
			this.daoService.modificarPerfil(perfil);
		}
		
	}
	
}

