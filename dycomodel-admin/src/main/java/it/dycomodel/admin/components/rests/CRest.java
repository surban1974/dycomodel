package it.dycomodel.admin.components.rests; 




import java.io.Serializable;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.classhidra.annotation.elements.Action;
import it.classhidra.annotation.elements.ActionMapping;
import it.classhidra.annotation.elements.Expose;
import it.classhidra.annotation.elements.Redirect;
import it.classhidra.annotation.elements.Rest;
import it.classhidra.annotation.elements.SessionDirective;
import it.classhidra.core.controller.action;
import it.classhidra.core.controller.i_action;
import it.classhidra.core.controller.i_bean;
import it.classhidra.core.controller.redirects;
import it.classhidra.core.tool.exception.bsControllerException;
@ActionMapping (
		actions={
				@Action (
						path="restnetworks",
						name="networks",
						Redirect=@Redirect(contentType="application/json"),
						Expose =@Expose(
									method="GET",
									restmapping={
											@Rest(path="/rest/networks/")
									})
						
				)
		}
)



@SessionDirective
public class CRest extends action implements i_action, i_bean, Serializable{

	private static final long serialVersionUID = 1L;

public CRest(){
	super();
}

@Override
public redirects actionservice(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, UnavailableException, bsControllerException {

	try{
		response.getOutputStream().write(super.modelAsJson(request, response).getBytes());
	}catch(Exception e){
		
	}
	return null;
}




}
