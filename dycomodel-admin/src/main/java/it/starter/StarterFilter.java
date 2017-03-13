package it.starter;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.classhidra.core.controller.bsController;
import it.classhidra.core.controller.bsFilter;

@WebFilter(
		filterName="StarterFilter",
		value={
				"/actions/*",
				"/*"
		},
		initParams={
				@WebInitParam(name="CharacterEncoding",value="UTF-8"),
				@WebInitParam(name="ExcludedUrl",value="/js/;/css/;/images/;"),
				@WebInitParam(name="ExcludedPattern",value="^(?!.*/neohort/).*\\.jsp$"),
				@WebInitParam(name="RestSupport",value="true")
		},
		
		dispatcherTypes={
				DispatcherType.REQUEST,
				DispatcherType.FORWARD,
				DispatcherType.INCLUDE
		},
		asyncSupported = true
	)
public class StarterFilter extends bsFilter{

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws ServletException, IOException {
	

		if(req!=null && req instanceof HttpServletRequest && resp!=null && resp instanceof HttpServletResponse){
		
/*
			bsController.isInitialized(
					new Properties(){
						private static final long serialVersionUID = 1L;

					{
						put(app_init.id_path,"sijinn-admin");
						put(app_init.id_extention_do,"");
						put(app_init.id_actioncall_separator,"-");
						put(app_init.id_external_loader,"it.sijinn.admin.loaders.ExternalLoader");
						put(app_init.id_package_annotated,"it.sijinn.admin.components");
						put(app_init.id_async_provider_servlet,"/AsyncController");
					}},
					new HashMap<String, Properties>(){
						private static final long serialVersionUID = 1L;

					{
						put(log_init.id_property,
							new Properties(){
								private static final long serialVersionUID = 1L;

							{
								put(log_init.id_Write2Concole,"true");
							}}						
						);
					}}
			);
*/
			bsController.isInitialized(
					StarterInitializer.applicationProperties(),
					StarterInitializer.otherProperties()
			);

			super.doFilter(req, resp, chain);
		}else{
            chain.doFilter(req, resp);
            return;			
		}
	}

}
