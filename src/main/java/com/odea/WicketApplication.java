package com.odea;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import com.odea.shiro.AnnotationsShiroAuthorizationStrategy;
import com.odea.shiro.ShiroUnauthorizedComponentListener;

public class WicketApplication extends WebApplication
{    	
	
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return LoginPage.class;
	}

	
	
	@Override
	public void init()
	{
		super.init();
		
		AnnotationsShiroAuthorizationStrategy authz = new AnnotationsShiroAuthorizationStrategy();
		this.getSecuritySettings().setAuthorizationStrategy(authz);
		this.getSecuritySettings().setUnauthorizedComponentInstantiationListener(new ShiroUnauthorizedComponentListener(PermisosPage.class, PermisosPage.class, authz));

		this.mountPage("LoginPage", LoginPage.class);
		
		this.mountPage("PasswordPage", PasswordPage.class);
        this.mountPage("PermisosPage", PermisosPage.class);
        
        this.mountPage("UsuariosPage", UsuariosPage.class);
        this.mountPage("EditarUsuarioPage", EditarUsuarioPage.class);
        
        this.mountPage("PerfilesPage", PerfilesPage.class);
        this.mountPage("EditarPerfilPage", EditarPerfilPage.class);
        
        this.getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        
	}
}
