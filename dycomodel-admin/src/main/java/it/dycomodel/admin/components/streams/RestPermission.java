package it.dycomodel.admin.components.streams;


import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.classhidra.annotation.elements.Apply_to_action;
import it.classhidra.annotation.elements.Stream;
import it.classhidra.annotation.elements.Redirect;
import it.classhidra.core.controller.bsController;
import it.classhidra.core.controller.i_stream;
import it.classhidra.core.controller.redirects;
import it.classhidra.core.controller.stream;
import it.classhidra.core.init.auth_init;
import it.classhidra.core.tool.exception.bsControllerException;
import it.classhidra.core.tool.exception.bsException;
import it.classhidra.core.tool.log.stubs.iStub;
import sun.misc.BASE64Decoder;

/*
@Stream(	
		name="rest_control_permission",
		applied={
			@Apply_to_action(action="restnetworks")
		},
		Redirect=@Redirect(contentType="application/json")
)
*/
public class RestPermission extends stream implements i_stream{

	private static final long serialVersionUID = 1L;


	public redirects streamservice_enter(HttpServletRequest request,HttpServletResponse response) throws bsControllerException {

		auth_init auth = (auth_init)request.getSession().getAttribute(bsController.CONST_REST_$AUTHENTIFICATION); 
		
		if(auth==null || !auth.is_logged()){
			try{
				auth = serviceRest(auth, request, response);
			}catch(Exception e){
				new bsControllerException("REST Authentication -> System error"+e.toString(), iStub.log_ERROR);
			}
		}

		if(auth==null || !auth.is_logged())
			return new redirects("");
		return super.streamservice_enter(request, response);
	}

	public redirects streamservice_exit(HttpServletRequest request, HttpServletResponse response) throws bsControllerException {


		
		return super.streamservice_exit(request, response);
	}

	
	private auth_init serviceRest(auth_init auth,  HttpServletRequest request, HttpServletResponse response)throws Exception {
		PrintWriter out = response.getWriter();
		Enumeration<String> headerNames = request.getHeaderNames();
		
		
		String header_auth = null; 
		
		if(auth==null || !auth.is_logged()){
			while (headerNames.hasMoreElements()) {
				String headerName = (String) headerNames.nextElement();			

				if(headerName.equalsIgnoreCase("authorization")) 
					header_auth = request.getHeader(headerName);
			}

			
		}
		
		 if(header_auth == null){
		        response.setStatus(401);
		        response.setHeader("WWW-Authenticate", "basic realm=\"Auth (" + request.getSession().getCreationTime() + ")\"" );
		        request.getSession().setAttribute("checkauth", Boolean.TRUE);
		        out.println("Login Required");
		        return auth;
		    }
		
		try{			
			if(header_auth!=null && header_auth.indexOf("Basic ")==0){
				header_auth=header_auth.replace("Basic ", "");
				header_auth = new String(new BASE64Decoder().decodeBuffer(header_auth));
				StringTokenizer st = new StringTokenizer(header_auth,":");

				String user = st.nextToken();
				String password = st.nextToken();
				
				if(user.equals("admin") && password.equals("admin")){
					if(auth==null){
						auth = new auth_init();
						try{
							auth.init(request);
							if(auth.get_user().equals("")) auth.set_language("en");
							auth.set_ruolo("guests");
							auth.set_user(user);
							auth.set_matricola("guest");
							auth.set_logged(true);
						}catch(bsException je){}
						request.getSession().setAttribute(bsController.CONST_REST_$AUTHENTIFICATION,auth);
					}else{
						auth = (auth_init)request.getSession().getAttribute(bsController.CONST_REST_$AUTHENTIFICATION);
						if(!auth.is_logged()){
							auth.set_ruolo("guests");
							auth.set_user(user);
							auth.set_matricola("guest");
							request.getSession().setAttribute(bsController.CONST_REST_$AUTHENTIFICATION,auth);
						}else{
						}
					}
				}else{

					request.getSession().removeAttribute("auth");
			        response.setStatus(401);
			        response.setHeader("WWW-Authenticate", "basic realm=\"Auth (" + request.getSession().getCreationTime() + ")\"" );
			        request.getSession().setAttribute("checkauth", Boolean.TRUE);
			        out.println("Login Required");
			        return auth;					
				}
			}
		}catch(Exception e){
			
		}
		

		return auth;
	
	}

}
