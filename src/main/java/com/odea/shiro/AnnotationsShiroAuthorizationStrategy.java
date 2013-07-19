package com.odea.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.request.component.IRequestableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.odea.LoginPage;

public class AnnotationsShiroAuthorizationStrategy implements IAuthorizationStrategy
{
	
	private static final Logger LOG = LoggerFactory.getLogger(AnnotationsShiroAuthorizationStrategy.class);
	
	
	@Override
	public <T extends IRequestableComponent> boolean isInstantiationAuthorized(Class<T> componentClass) {
		return true;
	}

	
	@Override
	public boolean isActionAuthorized(Component component, Action action) {
		
		boolean loggedIn = SecurityUtils.getSubject().getPrincipal() != null;
		String paginaActual = component.getPage().getClass().toString();
		
		if (!loggedIn && !paginaActual.equals("class com.odea.LoginPage")) {
			throw new RestartResponseAtInterceptPageException(new LoginPage());
		}
		
		return true;
	}
	
	
	

}
