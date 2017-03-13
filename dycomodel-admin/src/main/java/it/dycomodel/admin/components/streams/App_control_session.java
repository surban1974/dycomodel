package it.dycomodel.admin.components.streams;


import it.classhidra.annotation.elements.Stream;
import it.classhidra.annotation.elements.Apply_to_action;
import it.classhidra.core.controller.bsController;
import it.classhidra.core.controller.i_stream;
import it.classhidra.core.controller.info_action;
import it.classhidra.core.controller.load_actions;
import it.classhidra.core.controller.redirects;
import it.classhidra.core.controller.stream;
import it.classhidra.core.tool.exception.bsControllerException;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Stream(	
		name="def_control_session",
		applied={
			@Apply_to_action(action="*")
		}
)

public class App_control_session extends stream implements i_stream{

	private static final long serialVersionUID = 1L;


	public redirects streamservice_enter(HttpServletRequest request,HttpServletResponse response) throws bsControllerException {
		String redirectURI=null;
		

		if (!bsController.isSessionValid(request)){
			String id_action = (String)request.getAttribute(bsController.CONST_ID);
			if(id_action!=null){
				info_action i_a = (info_action)load_actions.get_actions().get(id_action);
				if(i_a!=null && i_a.getProperty("allway").equals("public")){
					return super.streamservice_enter(request, response);
				}
			}	

			
			redirectURI = bsController.getAction_config().getSession_error();
			return new redirects(redirectURI);
		}

		
		return super.streamservice_enter(request, response);
	}

	public redirects streamservice_exit(HttpServletRequest request, HttpServletResponse response) throws bsControllerException {


		
		return super.streamservice_exit(request, response);
	}

	

}
