package groupbase.thn.web.buyandsell.controllers;

import groupbase.thn.web.buyandsell.model.TestModel;
import groupbase.thn.web.libs.ControllerBase;

import groupbase.thn.web.libs.View;
import groupbase.thn.web.libs.ViewAction;
import javax.servlet.annotation.WebServlet;
@WebServlet(urlPatterns ={ "/test.html" })
public class Test extends ControllerBase<TestModel>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected View doGet() {
		// TODO Auto-generated method stub
		return new View("index", ViewAction.FORWARD);
	}

	@Override
	protected View doPost() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
