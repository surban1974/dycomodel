package it.dycomodel.admin.components.streams; 


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.classhidra.annotation.elements.Apply_to_action;
import it.classhidra.annotation.elements.Stream;
import it.classhidra.core.controller.bsController;
import it.classhidra.core.controller.i_action;
import it.classhidra.core.controller.i_stream;
import it.classhidra.core.controller.info_action;
import it.classhidra.core.controller.load_actions;
import it.classhidra.core.controller.redirects;
import it.classhidra.core.controller.stream;
import it.classhidra.core.init.auth_init;
import it.classhidra.core.tool.exception.bsControllerException;
import it.classhidra.core.tool.exception.bsControllerMessageException;
import it.classhidra.core.tool.exception.bsException;
import it.classhidra.core.tool.log.stubs.iStub;
import it.classhidra.core.tool.util.util_usersInSession;

@Stream(	
		name="def_control_permission",
		applied={
			@Apply_to_action(action="*"),
			@Apply_to_action(action="restnetworks",excluded="true")
		}
)

public class App_control_permission extends stream implements i_stream{

	private static final long serialVersionUID = 1L;
	


	public redirects streamservice_enter(HttpServletRequest request,HttpServletResponse response) throws bsControllerException {
		

	
		String redirectURI=null;
		auth_init auth = (auth_init)request.getSession().getAttribute(bsController.CONST_BEAN_$AUTHENTIFICATION); 
		
	
		if(auth==null){
			auth = new auth_init();
			try{
				auth.init(request);
				if(auth.get_user().equals("")) auth.set_language("en");
				auth.set_ruolo("guests");
				auth.set_user("guest");
				auth.set_matricola("guest");
			}catch(bsException je){}
			request.getSession().setAttribute(bsController.CONST_BEAN_$AUTHENTIFICATION,auth);
		}else{
			auth = (auth_init)request.getSession().getAttribute(bsController.CONST_BEAN_$AUTHENTIFICATION);
			if(!auth.is_logged()){
				auth.set_ruolo("guests");
				auth.set_user("guest");
				auth.set_matricola("guest");
				request.getSession().setAttribute(bsController.CONST_BEAN_$AUTHENTIFICATION,auth);
			}else{
			}
		}

		
		

		String id_action = (String)request.getAttribute(bsController.CONST_ID);
		
		
		info_action i_a = (info_action)load_actions.get_actions().get(id_action);
		if(i_a!=null && i_a.getProperty("allway").equals("public"))
			return super.streamservice_enter(request, response);
		
		String id_comlete = (String)request.getAttribute(bsController.CONST_ID_COMPLETE);
		if(id_comlete!=null && !id_comlete.equals(id_action)){
			if(	auth!=null &&
				auth.get_authentication_filter()!=null &&
				!auth.get_authentication_filter().check_actionIsPermitted(auth,id_comlete)){
				redirectURI = service_AuthRedirect(id_comlete,request.getSession().getServletContext(),request, response);
				return new redirects(redirectURI);
			}	
		}else{		
			if(	auth!=null &&
				auth.get_authentication_filter()!=null &&
				!auth.get_authentication_filter().check_actionIsPermitted(auth,id_action)){
				redirectURI = service_AuthRedirect(id_action,request.getSession().getServletContext(),request, response);
				return new redirects(redirectURI);
			}
		}

	

		if(i_a!=null && !auth.is_logged() && !i_a.getProperty("allway").equals("public")){
			redirectURI = service_ErrorRedirect(id_action,request.getSession().getServletContext(),request, response);
			 new bsControllerMessageException(
						"error_9",
						request,
						null,
						iStub.log_ERROR,
						new Object[]{});
			 return new redirects(redirectURI);
		}
		
		return super.streamservice_enter(request, response);
		
	}

	public redirects streamservice_exit(HttpServletRequest request, HttpServletResponse response) throws bsControllerException {

		String redirectURI=null;
		auth_init auth = null; 
		try{
			auth = bsController.checkAuth_init(request);
		}catch(Exception e){			
		}
		String id_complete = (String)request.getAttribute(bsController.CONST_ID_COMPLETE);		
		i_action action_instance = (i_action)request.getAttribute(bsController.CONST_BEAN_$INSTANCEACTION);
		if(action_instance!=null && action_instance.get_infoaction()!=null && action_instance.get_infoaction().getProperty("allway").equals("public")){
		}else{
			if(	action_instance!=null &&
				auth!=null &&
				auth.get_authentication_filter()!=null &&
				!auth.get_authentication_filter().check_redirectIsPermitted(auth,action_instance)){
	
				redirectURI = service_AuthRedirect(id_complete,request.getSession().getServletContext(),request, response);
				return new redirects(redirectURI);
			}
		}
		util_usersInSession.addInSession(auth, request, null);
/*		
		if(action_instance!=null && action_instance.get_bean()!=null){
			Iterator it = action_instance.get_bean().getInitErrors().keySet().iterator();
			System.out.println("--- Absent parameters ---");
			while(it.hasNext()){
				String key = it.next().toString();
				System.out.println(key+ " - "+action_instance.get_bean().getInitErrors().get(key));
			}
		}		
*/
		return super.streamservice_exit(request, response);
	}

	
	private String service_AuthRedirect(String id_action,ServletContext servletContext, HttpServletRequest request, HttpServletResponse response){
		String redirectURI="";
		try{
			if(bsController.getAction_config()==null ||
				bsController.getAction_config().getAuth_error()==null ||
				bsController.getAction_config().getAuth_error().equals("")){
				redirectURI="";
			}else redirectURI = bsController.getAction_config().getAuth_error();

		}catch(Exception ex){
			if(request!=null)
				bsController.writeLog(request, "Controller authentication error. Action: ["+id_action+"] URI: ["+redirectURI+"]");		
		}	
		return redirectURI;
	}
	private String service_ErrorRedirect(String id_action,ServletContext servletContext, HttpServletRequest request, HttpServletResponse response){
		String redirectURI="";
		try{
			if(bsController.getAction_config()==null ||
				bsController.getAction_config().getAuth_error()==null ||
				bsController.getAction_config().getAuth_error().equals("")){
				redirectURI="";
			}else redirectURI = bsController.getAction_config().getError();

		}catch(Exception ex){
			if(request!=null)
				bsController.writeLog(request, "Controller authentication error. Action: ["+id_action+"] URI: ["+redirectURI+"]");		
		}	
		return redirectURI;
	}
/*	
	private void chackRestAuthentication(auth_init auth, HttpServletRequest request,HttpServletResponse response){
		
		String h_user = null;
		String h_password = null;
		
		Enumeration headerNames = request.getHeaderNames();
		String h_auth=null;
		boolean checkAgain=true;
		while (headerNames.hasMoreElements() && checkAgain) {
			String headerName = (String) headerNames.nextElement();			
			if(headerName.equalsIgnoreCase("authorization")){
				
				h_auth = request.getHeader(headerName);
				h_auth=h_auth.replace("Basic ", "");
				try{
					h_auth = new String(new BASE64Decoder().decodeBuffer(h_auth));
				}catch(Exception e){
				}
				StringTokenizer st = new StringTokenizer(h_auth,":");
				h_user = st.nextToken();
				h_password = st.nextToken();						
				
				checkAgain=false;
			}
		}
		
		if(h_user!=null && h_password!=null){
			Vector wrapped = new Vector();
			ModuleLogin2 ml = new ModuleLogin2();
			Object result=null;
			bean form = new bean();
			form.put("user",h_user);
			form.put("password",h_password);
			try{
				result = ml.operation(i_module_integration.o_FIND, form);
			}catch(Exception e){			
			}
			
			if(result!=null && result instanceof Vector && ((Vector)result).size()>0){
				wrapped.addAll((Vector)result);
				info_user _user = ((UserWrapper)wrapped.get(0)).getIuser();

			    if(_user!=null){
		    		auth.set_language(_user.getLanguage());

		    		for(int i=0;i<wrapped.size();i++)
		    			((UserWrapper)wrapped.get(i)).translateGroup(auth);
		    		
		    		UserWrapper curr_group = (UserWrapper)wrapped.get(0);

					

		    		reSetAuth(auth, _user, request);
					info_target i_target = curr_group.getItarget();
					if(i_target!=null){
						auth.setInfotarget(i_target);
						auth.set_target(i_target.getName());
						auth.set_target_property(i_target.getProperties());
					}
					info_group i_group = curr_group.getIgroup();
					if(i_group!=null){
						auth.set_ruolo(i_group.getName());	
						auth.setInfogroup(i_group);
					}

		    		
			    }else{	    	
			    	auth.set_logged(false);	

			    }			
				
			}else{	    	
		    	auth.set_logged(false);	

		    }	
			
		}
		
		if(!auth.is_logged()){
	        response.setStatus(401);
	        response.setHeader("WWW-Authenticate", "basic realm=\"Auth (" + request.getSession().getCreationTime() + ")\"" );
		}
	}

	private void reSetAuth(auth_init auth, info_user _user, HttpServletRequest request){
	    if(_user!=null){
	    	auth.set_user(_user.getName());
	    	auth.set_userDesc(_user.getDescription());
	    	auth.set_ruolo(_user.getGroup());

	    		auth.set_language(_user.getLanguage());
	    	
	    	auth.set_matricola(_user.getMatriculation());
	    	auth.set_target(_user.getTarget().replace(';','^'));
	    	auth.set_mail(_user.getEmail());
	    	
	    	auth.get_target_property().put(bsConstants.CONST_AUTH_TARGET_ISTITUTION, auth.get_target());
	    	auth.set_logged(true);
	    	auth.setInfouser(_user);
	    	try{
	    		info_target itarget = (info_target)_user.getV_info_targets().get(0);
	    		auth.setInfotarget(itarget);
	    		auth.get_target_property().putAll(itarget.getProperties());
	    	}catch(Exception e){	    		
	    	}
	    	auth.get_user_property().putAll(_user.getProperties());
	    	util_usersInSession.addInSession(auth, request,  new Date());
	    	
			new bsControllerException(
					auth.get_user()+":"+auth.get_matricola()+":"+auth.get_user_ip()+" is logged ",						
					iStub.log_INFO);	
			
	    }
	}	
*/	
	
}
