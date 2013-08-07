package com.odea;

import org.apache.shiro.SecurityUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class BasePage extends WebPage {
	
	private static final long serialVersionUID = 1L;
		
	
	public BasePage(){
		
	    Form form = new Form("formLogout");
	    this.add(form);	
	    
		AjaxButton botonLogout = new AjaxButton("botonLogout") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				SecurityUtils.getSubject().logout();
				setResponsePage(LoginPage.class);
			}
			
		};

		form.add(botonLogout);
				
		WebMarkupContainer permisosPage = this.armarWmc("PermisosPage");
		WebMarkupContainer usuariosPage = this.armarWmc("UsuariosPage");
		WebMarkupContainer perfilesPage = this.armarWmc("PerfilesPage");
		WebMarkupContainer passwordPage = this.armarWmc("PasswordPage");

		form.add(permisosPage);
		form.add(usuariosPage);
		form.add(perfilesPage);
		form.add(passwordPage);

	}
	
	private WebMarkupContainer armarWmc(String idWmc) {
		
		String page = this.getRequest().getUrl().toString();
		
		WebMarkupContainer wmc = new WebMarkupContainer(idWmc);
		
		if((page.equals(idWmc))){
			wmc.add(new AttributeModifier("class","current"));
		}
		
		return wmc;
	}
	
}
