package org.artofsolving.jodconverter.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent event) {
		WebContext.init(event.getServletContext());
	}

	public void contextDestroyed(ServletContextEvent event) {
		WebContext.destroy(event.getServletContext());
	}

}
