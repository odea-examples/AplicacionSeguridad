package com.odea;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import com.odea.shiro.AnnotationsShiroAuthorizationStrategy;
import com.odea.shiro.ShiroUnauthorizedComponentListener;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see com.odea.Start#main(String[])
 */
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
		
        this.getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        
	}
}
