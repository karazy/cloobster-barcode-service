package net.karazy.cloobster;

import static java.lang.Thread.currentThread;

import java.net.URI;
import java.net.URISyntaxException;


//import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.webapp.WebAppContext;

public class App {

    public static void main(String[] args) throws Exception
    {
        Integer serverPort = 8080; //Integer.valueOf(System.getenv("PORT"));
        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(serverPort);

//        ContextHandler context = newContextHandler("/", "template/hello.html");
//        ContextHandler resources = newContextHandler("/static", "static");

//        HandlerList handlers = new HandlerList();
//        handlers.setHandlers(new Handler[] {resources, context});
//        server.setHandler(handlers);
        
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar("BarcodeService.war");

        server.start();
        server.join();
    }

    private static ContextHandler newContextHandler(String contextPath, String resourceBase) throws URISyntaxException {
        ContextHandler context = new ContextHandler(contextPath);
        context.setHandler(new ResourceHandler());
        context.setResourceBase(classpathResource(resourceBase).toString());
        return context;
    }

    private static URI classpathResource(String resourceBase) throws URISyntaxException {
        ClassLoader classLoader = currentThread().getContextClassLoader();
        return classLoader.getResource(resourceBase).toURI();
    }

}
