package it.dycomodel.admin.loaders;

import it.classhidra.core.controller.bsController;
import it.classhidra.core.controller.i_externalloader;
import it.classhidra.core.tool.util.util_provider;
import it.classhidra.scheduler.common.i_batch;
import it.classhidra.scheduler.scheduling.DriverScheduling;
import it.classhidra.scheduler.scheduling.IBatchFactory;
import it.classhidra.scheduler.scheduling.init.batch_init;

public class ExternalLoader implements i_externalloader{


	
	public Object getProperty(String key) {
		return null;
	}

	
	
	public void load() {
		launchScheduler();
	}



	public void setProperty(String key, Object value) {
	
	}
	

	
	private void launchScheduler(){
		try{
			
			DriverScheduling.init(
					bsController.checkSchedulerContainer(),
					
					new IBatchFactory() {
						
						@Override
						public i_batch getInstance(String cd_btch, String cls_batch) {
							try{
								i_batch instance = null;
								if(bsController.getAppInit()!=null && bsController.getAppInit().get_context_provider()!=null && !bsController.getAppInit().get_context_provider().equals("")){
									try{
										instance = (i_batch)util_provider.getBeanFromObjectFactory(bsController.getAppInit().get_context_provider(),  cd_btch, cls_batch, null);
									}catch(Exception e){
									}
								}
								if(instance==null && bsController.getAppInit()!=null && bsController.getAppInit().get_cdi_provider()!=null && !bsController.getAppInit().get_cdi_provider().equals("")){
									try{
										instance = (i_batch)util_provider.getBeanFromObjectFactory(bsController.getAppInit().get_cdi_provider(),  cd_btch, cls_batch, null);
									}catch(Exception e){
									}
								}
								if(instance==null && bsController.getAppInit()!=null && bsController.getAppInit().get_ejb_provider()!=null && !bsController.getAppInit().get_ejb_provider().equals("")){
									try{
										instance = (i_batch)util_provider.getBeanFromObjectFactory(bsController.getAppInit().get_ejb_provider(),  cd_btch, cls_batch, null);
									}catch(Exception e){
									}
								}
								if(instance==null && bsController.getCdiDefaultProvider()!=null){
									try{
										instance = (i_batch)util_provider.getBeanFromObjectFactory(bsController.getCdiDefaultProvider(),   cd_btch, cls_batch, null);
									}catch(Exception e){
									}
								}
								if(instance==null && bsController.getEjbDefaultProvider()!=null){
									try{
										instance = (i_batch)util_provider.getBeanFromObjectFactory(bsController.getEjbDefaultProvider(),   cd_btch, cls_batch, null);
									}catch(Exception e){
									}
								}

								if(instance == null)
									instance = (i_batch)Class.forName(cls_batch).newInstance();

								return instance;
							}catch(Exception e){
							}catch(Throwable t){
							}
							
							return null;
						}
						
					},
					new batch_init(null).set_stub("empty")
			);
			
//			DriverScheduling.reStart();


		}catch(Exception e){
			e.toString();
		}catch(Throwable t){
			t.toString();
		}

	}
	



}
