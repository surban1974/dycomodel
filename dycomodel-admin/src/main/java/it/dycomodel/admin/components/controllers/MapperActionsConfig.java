package it.dycomodel.admin.components.controllers;

import it.classhidra.annotation.elements.ActionMapping;


@ActionMapping (
		memoryInContainer_streams="true",
		error=			"/pages/framework/action_mappings_Error.html",
        auth_error=		"/pages/framework/action_mappings_authError.html",
        session_error=	"/pages/framework/action_mappings_sessionError.html"		
	)



public class MapperActionsConfig {

}
