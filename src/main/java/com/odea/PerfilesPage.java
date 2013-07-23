package com.odea;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.odea.components.confirmPanel.ConfirmationLink;
import com.odea.domain.Usuario;
import com.odea.services.DAOService;

public class PerfilesPage extends BasePage {

	private static final long serialVersionUID = 1L;

	@SpringBean
	public DAOService daoService;
	
	public IModel<List<Usuario>> lstPerfilesModel;
	public WebMarkupContainer listViewContainer;
	
	
	public PerfilesPage() {
       
        this.lstPerfilesModel = new LoadableDetachableModel<List<Usuario>>() { 

			private static final long serialVersionUID = 1L;

			@Override
            protected List<Usuario> load() {
            	return daoService.getPerfiles();
            }
        };
        
        
        BookmarkablePageLink<EditarPerfilPage> linkAltaPerfil = new BookmarkablePageLink<EditarPerfilPage>("linkAltaPerfil", EditarPerfilPage.class);
		this.add(linkAltaPerfil);
        
        
		this.listViewContainer = new WebMarkupContainer("listViewContainer");
		this.listViewContainer.setOutputMarkupId(true);
		
		
		PageableListView<Usuario> perfilesListView = new PageableListView<Usuario>("perfiles", this.lstPerfilesModel, 20) {

			private static final long serialVersionUID = 1L;

			@Override
            protected void populateItem(ListItem<Usuario> item) {
            	final Usuario perfil = item.getModel().getObject();   
            
            	if((item.getIndex() % 2) == 0){
            		item.add(new AttributeModifier("class","odd"));
            	}
            	
            	
            	
            	item.add(new Label("nombrePerfil", new Model<String>(perfil.getNombre())));
            	
            	
            	
            	BookmarkablePageLink<EditarPerfilPage> botonModificarPerfil = new BookmarkablePageLink<EditarPerfilPage>("modifyLink", EditarPerfilPage.class, new PageParameters().add("id", perfil.getIdUsuario()).add("nombrePerfil", perfil.getNombre()));
            	
            	item.add(botonModificarPerfil);
            	
            	
            	
            	ConfirmationLink<Usuario> botonBajaPerfil = new ConfirmationLink<Usuario>("deleteLink","\\u00BFEst\\xE1 seguro de que desea borrar este perfil?", new Model<Usuario>(perfil)) {

					private static final long serialVersionUID = 1L;

					@Override
                    public void onClick(AjaxRequestTarget ajaxRequestTarget) {
						PerfilesPage.this.daoService.bajaPerfil(this.getModelObject());
                        ajaxRequestTarget.add(PerfilesPage.this.listViewContainer);
                    }
                    
                };
            
                item.add(botonBajaPerfil);
            	
            }
        };
        
        perfilesListView.setOutputMarkupId(true);
        
        
        this.listViewContainer.add(perfilesListView);
        this.listViewContainer.add(new AjaxPagingNavigator("navigator", perfilesListView));
        this.listViewContainer.setVersioned(false);

        this.add(listViewContainer);
		
	}
	
}
