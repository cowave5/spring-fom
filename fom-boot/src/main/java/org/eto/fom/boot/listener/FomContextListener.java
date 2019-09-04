package org.eto.fom.boot.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 
 * @author shanhm
 *
 */
public class FomContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		FomInitializer.set(event.getServletContext()); 
	} 
}