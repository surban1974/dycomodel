package it.dycomodel.admin.components.streams;

import it.classhidra.annotation.elements.Apply_to_action;
import it.classhidra.annotation.elements.Redirect;
import it.classhidra.annotation.elements.Stream;
import it.classhidra.core.controller.streams.def_basic_authorization;

@Stream(	
		name="rest_control_permission",
		applied={
			@Apply_to_action(action="restnetworks")
		},
		Redirect=@Redirect(contentType="application/json")
)
public class Rest_basic_authorization extends def_basic_authorization{
	private static final long serialVersionUID = 1L;


}
