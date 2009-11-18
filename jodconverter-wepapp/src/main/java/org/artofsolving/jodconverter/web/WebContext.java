package org.artofsolving.jodconverter.web;

import javax.servlet.ServletContext;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

public class WebContext {

	private static final String KEY = WebContext.class.getName();
	private final OfficeManager officeManager;
	private final OfficeDocumentConverter documentConverter;

	public WebContext() {
		officeManager = new DefaultOfficeManagerConfiguration().buildOfficeManager();
		documentConverter = new OfficeDocumentConverter(officeManager);
	}

    protected static void init(ServletContext servletContext) {
		WebContext instance = new WebContext();
		servletContext.setAttribute(KEY, instance);
		instance.officeManager.start();
	}

	protected static void destroy(ServletContext servletContext) {
		WebContext instance = get(servletContext);
		instance.officeManager.stop();
	}

	public static WebContext get(ServletContext servletContext) {
		return (WebContext) servletContext.getAttribute(KEY);
	}

	public OfficeDocumentConverter getDocumentConverter() {
        return documentConverter;
    }

}
