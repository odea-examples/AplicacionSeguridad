package com.odea;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
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

	private static final long serialVersionUID = 1L;

	@SpringBean
	private DAOService daoService;
	
	private IModel<List<Usuario>> lstUsuariosModel;
	private IModel<List<Usuario>> lstPerfilesModel;
	private IModel<List<String>> lstGruposModel;
	private WebMarkupContainer listViewContainer;
	
	
	public UsuariosPage() {

		this.lstUsuariosModel = new LoadableDetachableModel<List<Usuario>>() {
			
			private static final long serialVersionUID = 1L;

			@Override
            protected List<Usuario> load() {
            	return daoService.getUsuariosConPerfiles();
            }
            
        };
        
        this.lstPerfilesModel = new LoadableDetachableModel<List<Usuario>>() { 

			private static final long serialVersionUID = 1L;

			@Override
            protected List<Usuario> load() {
            	return daoService.getPerfiles();
            }
        };
        
        this.lstGruposModel = new LoadableDetachableModel<List<String>>() { 

			private static final long serialVersionUID = 1L;

			@Override
            protected List<String> load() {
            	return daoService.getGrupos();
            }
        };
        
        AjaxLink<AltaUsuarioPage> altaButton = new AjaxLink<AltaUsuarioPage>("altaButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				setResponsePage(AltaUsuarioPage.class);
			}
		};
		this.add(altaButton);
        

		this.listViewContainer = new WebMarkupContainer("listViewContainer");
		this.listViewContainer.setOutputMarkupId(true);
		
		//LISTADO
		PageableListView<Usuario> usuariosListView = new PageableListView<Usuario>("usuarios", this.lstUsuariosModel, 20) {
			
			private static final long serialVersionUID = 1L;

			@Override
            protected void populateItem(ListItem<Usuario> item) {
            	final Usuario usuario = item.getModel().getObject();   
            
            	if((item.getIndex() % 2) == 0){
            		item.add(new AttributeModifier("class","odd"));
            	}
            	
            	/*NOMBRE*/
            	
            	item.add(new Label("nombreUsuario", new Model<String>(usuario.getNombre())));
            	
            	
            	/*DEDICACION*/
            	
            	final RequiredTextField<Integer> dedicacionTextField = new RequiredTextField<Integer>("dedicacion", new Model<Integer>(usuario.getDedicacion()));
            	dedicacionTextField.add(new OnlyNumberBehavior(dedicacionTextField.getMarkupId()));
            	
            	dedicacionTextField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            		
					private static final long serialVersionUID = 1L;

					@Override
    				protected void onUpdate(AjaxRequestTarget target) {
						Integer dedicacion = Integer.parseInt(dedicacionTextField.getInput());
    					daoService.setDedicacion(usuario, dedicacion);
    					target.add(UsuariosPage.this.listViewContainer);
    				}

					@Override
					protected void onError(AjaxRequestTarget target, RuntimeException e) {
						target.add(UsuariosPage.this.listViewContainer);
					}
    				
    			});
            	
            	dedicacionTextField.setOutputMarkupId(true);
            	
            	item.add(dedicacionTextField);
            	
            	
            	/*PERFIL-ROL*/
            	
            	final DropDownChoice<Usuario> dropDownPerfil = new DropDownChoice<Usuario>("dropDownPerfil", Model.of(usuario.getPerfil()), UsuariosPage.this.lstPerfilesModel);
            	
            	dropDownPerfil.add(new AjaxFormComponentUpdatingBehavior("onchange") {

					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						daoService.cambiarPerfil(usuario, dropDownPerfil.getModelObject());
						target.add(UsuariosPage.this.listViewContainer);
					}
            		
            	});
            	
            	
            	item.add(dropDownPerfil);
            	
            	/*GRUPO*/
            	
            	final DropDownChoice<String> dropDownGrupo = new DropDownChoice<String>("dropDownGrupo", Model.of(usuario.getGrupo()), UsuariosPage.this.lstGruposModel);
            	
            	dropDownGrupo.add(new AjaxFormComponentUpdatingBehavior("onchange") {

					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						UsuariosPage.this.daoService.cambiarGrupo(usuario, dropDownGrupo.getModelObject());
						target.add(UsuariosPage.this.listViewContainer);
					}
            		
            	});
            	
            	
            	item.add(dropDownGrupo);
            	
            	
            	BookmarkablePageLink<ModificarUsuarioPage> botonModificarUsuario = new BookmarkablePageLink<ModificarUsuarioPage>("modifyLink", ModificarUsuarioPage.class, new PageParameters().add("id", usuario.getIdUsuario()).add("nombreUsuario", usuario.getNombre()).add("loginUsuario", usuario.getNombreLogin()).add("grupoDelUsuario", usuario.getGrupo()).add("perfilUsuario", usuario.getPerfil().getNombre()).add("dedicacionUsuario", usuario.getDedicacion()));
            	
            	item.add(botonModificarUsuario);
            	
            	
            	BookmarkablePageLink<ModificarPasswordPage> botonModificarPassword = new BookmarkablePageLink<ModificarPasswordPage>("passwordLink", ModificarPasswordPage.class, new PageParameters().add("id", usuario.getIdUsuario()).add("usuarioNombre", usuario.getNombre()));
            	
            	item.add(botonModificarPassword);
            	
            	
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
