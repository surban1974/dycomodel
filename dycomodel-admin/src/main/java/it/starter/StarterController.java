package it.starter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.classhidra.core.controller.bsController;

@WebServlet(
		name="StarterController",
		displayName="StarterController",
		urlPatterns = {"/Controller"},
		loadOnStartup=1
		)
public class StarterController extends bsController {
	private static final long serialVersionUID = 1L;

    public StarterController() {
    	super();
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(request, response);
	}

	@Override
	public void init() throws ServletException, UnavailableException {
		super.isInitialized(
				StarterInitializer.applicationProperties(),
				StarterInitializer.otherProperties()
		);
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, UnavailableException {
		super.service(request, response);
	}

}
