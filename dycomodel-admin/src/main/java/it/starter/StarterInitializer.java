package it.starter;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import it.classhidra.core.init.app_init;
import it.classhidra.core.init.log_init;

public class StarterInitializer {
	public static Properties applicationProperties(){
		return
			new Properties(){
				private static final long serialVersionUID = 1L;
	
			{
				put(app_init.id_path,"dycomodel-admin");
				put(app_init.id_debug,"true");
				put(app_init.id_extention_do,"");
				put(app_init.id_actioncall_separator,"-");
				put(app_init.id_external_loader,"it.dycomodel.admin.loaders.ExternalLoader");
				put(app_init.id_package_annotated,"it.dycomodel.admin.components");
				put(app_init.id_async_provider_servlet,"/AsyncController");
			}};
	}
	public static Map<String, Properties> otherProperties(){
		return
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
		}};
	}	
}
