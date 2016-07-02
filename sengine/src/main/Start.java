package main;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


public class Start {
	/** Logger */
	public static Logger logger = Logger.getLogger(Start.class);
	
	public static FileSystemXmlApplicationContext APP;
	public static DBAPI DB;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Logger
		DOMConfigurator.configure("conf/log/log4j.xml");
		
		try {
			// ETC
			APP = new FileSystemXmlApplicationContext("conf/app/*.xml");
			DB = APP.getBean(DBAPI.class);
			
			Server server = new Server(80);
			
			WebAppContext baseCtx = new WebAppContext();
			baseCtx.setResourceBase("webapp/sengine");
			baseCtx.setDescriptor("webapp/web.xml");
			baseCtx.setContextPath("/");
			baseCtx.setParentLoaderPriority(true);
			
			ContextHandlerCollection contexts = new ContextHandlerCollection();
			contexts.setHandlers(new Handler[] {baseCtx});
			
			server.setHandler(contexts);
			server.start();
			server.join();
			
			
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
