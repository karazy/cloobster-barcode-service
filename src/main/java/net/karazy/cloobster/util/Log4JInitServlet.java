package net.karazy.cloobster.util;

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

public class Log4JInitServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final String LOG4J_CONFIG_PROPERTY = "log4j-config-location";

	public void init(ServletConfig config) throws ServletException {
		System.out.println("Log4JInitServlet is initializing log4j");
		String log4jLocation = config.getInitParameter(LOG4J_CONFIG_PROPERTY);

		ServletContext sc = config.getServletContext();

		if (log4jLocation == null) {
			System.err.println("*** No log4j-properties-location init param, so initializing log4j with BasicConfigurator");
			BasicConfigurator.configure();
		} else {
			String webAppPath = sc.getRealPath("/");
			String log4jPath = webAppPath + log4jLocation;
			File file = new File(log4jPath);
			if (file.exists()) {
				System.out.println("Initializing log4j with: " + log4jPath);
				PropertyConfigurator.configure(log4jPath);
			} else {
				System.err.println("*** " + log4jPath + " file not found, so initializing log4j with BasicConfigurator");
				BasicConfigurator.configure();
			}
		}
		super.init(config);
	}
}
