package com.odea;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Response;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.odea.domain.Funcionalidad;
import com.odea.domain.Usuario;
import com.odea.services.DAOService;

/* 
 *  Los perfiles estan modelados como objetos de clase Usuario.
 *  Esto es para seguir el modelo de datos
 */


public class PermisosPage extends BasePage {

	@SpringBean
	private DAOService daoService;
	
	private IModel<List<Usuario>> lstPerfilesModel;
	private IModel<List<Funcionalidad>> lstFuncionalidadesModel;
	
	
	public PermisosPage() {
		
		this.lstFuncionalidadesModel = new LoadableDetachableModel<List<Funcionalidad>>() {

			@Override
			protected List<Funcionalidad> load() {
				return daoService.getFuncionalidades();
			}
		};
		
		this.lstPerfilesModel = new LoadableDetachableModel<List<Usuario>>() {

			@Override
			protected List<Usuario> load() {
				return daoService.getPerfiles();
			}
		};
		
		
		
		//List View
		
		WebMarkupContainer listViewContainer = new WebMarkupContainer("listViewContainer");
		listViewContainer.setOutputMarkupId(true);
		this.add(listViewContainer);
		
	
		WebComponent titulos = new WebComponent("tituloHtml"){

			@Override
			public void onComponentTagBody(MarkupStream markupStream,ComponentTag openTag) {
				Response response = getRequestCycle().getResponse();
				
				String respuesta ="<th class='skinnyTable' scope='col'>Permisos</th>";
				
				for (Usuario unUsuario : lstPerfilesModel.getObject()) {
					
					respuesta += "<th class='skinnyTable' scope='col' >"+ unUsuario.getNombre() +"</th>";
				}
				
                response.write(respuesta);
			}
	    	
        };
        
        listViewContainer.add(titulos);
        
		
		
		
		ListView<Funcionalidad> listaFuncionalidades = new ListView<Funcionalidad>("listaFuncionalidades", this.lstFuncionalidadesModel) {

			@Override
			protected void populateItem(ListItem<Funcionalidad> item) {
				
				if((item.getIndex() % 2) == 0){
            		item.add(new AttributeModifier("class","odd"));
            	}
				
				final Funcionalidad funcionalidad = item.getModel().getObject();
				final List<Usuario> usuariosConPermiso = PermisosPage.this.daoService.getUsuariosQueTienenUnaFuncionalidad(funcionalidad);
				
				item.add(new Label("nombreFuncionalidad",  funcionalidad.getConcepto()));
				
				
				ListView<Usuario> listaInterna = new ListView<Usuario>("listaUsuarios", PermisosPage.this.lstPerfilesModel) {

					@Override
					protected void populateItem(ListItem<Usuario> item) {
						
						final Usuario usuario = item.getModelObject();
						final CheckBox checkBox = new CheckBox("check", new Model<Boolean>());
					
						Boolean habilitado = this.usuarioEstaHabilitado(usuariosConPermiso, usuario);
						checkBox.getModel().setObject(habilitado);
						
						checkBox.add(new AjaxEventBehavior("onChange") {

							@Override
							protected void onEvent(AjaxRequestTarget target) {
								
								//Cambio el estado del Checkbox manualmente
								Boolean estado = checkBox.getModelObject();
								checkBox.setModelObject(!estado);
								
								PermisosPage.this.daoService.cambiarStatusPermiso(usuario, funcionalidad, checkBox.getModelObject());
							}
						});
						
						
						item.add(checkBox);
			
					}
					
				
					private Boolean usuarioEstaHabilitado(List<Usuario> usuariosConPermiso, Usuario usuario) {
						
						for (Usuario usuarioConPermiso : usuariosConPermiso) {
							if(usuario.getIdUsuario() == usuarioConPermiso.getIdUsuario()) {
								return true;
							}
						}
						
						return false;
					}
					
					
				};
				
				item.add(listaInterna);
			}	
			
		};
		
		
		listViewContainer.add(listaFuncionalidades);
		
	}

	
}

