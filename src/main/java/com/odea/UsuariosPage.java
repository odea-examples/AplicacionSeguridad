package com.odea;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.odea.behaviour.OnlyNumberBehavior;
import com.odea.components.confirmPanel.ConfirmationLink;
import com.odea.domain.Usuario;
import com.odea.services.DAOService;

public class UsuariosPage extends BasePage {
	
	@SpringBean
	public DAOService daoService;
	
	public IModel<List<Usuario>> lstUsuariosModel;
	public IModel<List<Usuario>> lstPerfilesModel;
	public IModel<List<String>> lstNombresPerfilesModel;
	public IModel<List<String>> lstGruposModel;
	public WebMarkupContainer listViewContainer;
	
	
	public UsuariosPage() {

		this.lstUsuariosModel = new LoadableDetachableModel<List<Usuario>>() {
			
			private static final long serialVersionUID = 1L;

			@Override
            protected List<Usuario> load() {
            	return daoService.getUsuariosConPerfiles();
            }
            
        };
        
        this.lstNombresPerfilesModel = new LoadableDetachableModel<List<String>>() { 

			private static final long serialVersionUID = 1L;

			@Override
            protected List<String> load() {
            	return daoService.getNombresPerfiles();
            }
        };
        
        this.lstGruposModel = new LoadableDetachableModel<List<String>>() { 

			private static final long serialVersionUID = 1L;

			@Override
            protected List<String> load() {
            	return daoService.getGrupos();
            }
        };
        
        BookmarkablePageLink<EditarUsuarioPage> linkAltaUsuario = new BookmarkablePageLink<EditarUsuarioPage>("linkAltaUsuario", EditarUsuarioPage.class);
		this.add(linkAltaUsuario);
        
		this.listViewContainer = new WebMarkupContainer("listViewContainer");
		this.listViewContainer.setOutputMarkupId(true);
		
		//LISTADO
		PageableListView<Usuario> usuariosListView = new PageableListView<Usuario>("usuarios", this.lstUsuariosModel, 20) {
			
			@Override
            protected void populateItem(ListItem<Usuario> item) {
            	final Usuario usuario = item.getModel().getObject();   
            
            	if((item.getIndex() % 2) == 0){
            		item.add(new AttributeModifier("class","odd"));
            	}
            	
            	/*NOMBRE*/
            	
            	item.add(new Label("nombreUsuario", new Model<String>(usuario.getNombre())));
            	
            	
            	/*DEDICACION*/
            	
            	final RequiredTextField<Integer> dedicacionTextField = new RequiredTextField<Integer>("dedicacion", new Model<Integer>(daoService.getDedicacion(usuario)));
            	dedicacionTextField.add(new OnlyNumberBehavior(dedicacionTextField.getMarkupId()));
            	
            	dedicacionTextField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            		
    				@Override
    				protected void onUpdate(AjaxRequestTarget target) {
    					daoService.setDedicacion(usuario, Integer.parseInt(dedicacionTextField.getInput()));
    				}

					@Override
					protected void onError(AjaxRequestTarget target, RuntimeException e) {
						target.add(UsuariosPage.this.listViewContainer);
					}
    				
    			});
            	
            	dedicacionTextField.setOutputMarkupId(true);
            	
            	item.add(dedicacionTextField);
            	
            	
            	/*PERFIL-ROL*/
            	
            	final DropDownChoice<String> dropDownPerfil = new DropDownChoice<String>("dropDownPerfil", Model.of(usuario.getPerfil().getNombreLogin()), UsuariosPage.this.lstNombresPerfilesModel);
            	            	
            	dropDownPerfil.add(new AjaxFormComponentUpdatingBehavior("onchange") {

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						daoService.cambiarPerfil(usuario, dropDownPerfil.getModelObject());
					}
            		
            	});
            	
            	
            	item.add(dropDownPerfil);
            	
            	/*GRUPO*/
            	
            	final DropDownChoice<String> dropDownGrupo = new DropDownChoice<String>("dropDownGrupo", Model.of(usuario.getGrupo()), UsuariosPage.this.lstGruposModel);
            	
            	dropDownGrupo.add(new AjaxFormComponentUpdatingBehavior("onchange") {

					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						daoService.cambiarGrupo(usuario, dropDownGrupo.getModelObject());
					}
            		
            	});
            	
            	
            	item.add(dropDownGrupo);
            	
            	
            	BookmarkablePageLink<EditarUsuarioPage> botonModificarUsuario = new BookmarkablePageLink<EditarUsuarioPage>("modifyLink", EditarUsuarioPage.class, new PageParameters().add("id", usuario.getIdUsuario()).add("nombreUsuario", usuario.getNombre()).add("loginUsuario", usuario.getNombreLogin()).add("grupoDelUsuario", usuario.getGrupo()).add("perfilUsuario", usuario.getPerfil().getNombre()).add("dedicacionUsuario", daoService.getDedicacion(usuario)));
            	
            	item.add(botonModificarUsuario);
            	
            	
            	ConfirmationLink<Usuario> botonBajaUsuario = new ConfirmationLink<Usuario>("deleteLink","\\u00BFEst\\xE1 seguro de que desea borrar este usuario?", new Model<Usuario>(usuario)) {

					private static final long serialVersionUID = 1L;

					@Override
                    public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                        UsuariosPage.this.daoService.bajaUsuario(this.getModelObject());
                        ajaxRequestTarget.add(UsuariosPage.this.listViewContainer);
                    }
                    
                };
            
                item.add(botonBajaUsuario);
            	
            }
        };
        
        usuariosListView.setOutputMarkupId(true);
        
        this.listViewContainer.add(new Label("tituloDedicacion", "Dedicación"));
        this.listViewContainer.add(usuariosListView);
        this.listViewContainer.add(new AjaxPagingNavigator("navigator", usuariosListView));
        this.listViewContainer.setVersioned(false);

        this.add(listViewContainer);
		
	}
	
}
