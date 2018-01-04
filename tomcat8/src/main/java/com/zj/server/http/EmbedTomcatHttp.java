package com.zj.server.http;

import com.zj.server.servlet.HomeServlet;
import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.Tomcat.FixContextListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import javax.servlet.ServletException;
import java.io.File;

public class EmbedTomcatHttp {
    private final Log log= LogFactory.getLog(getClass());
    private int port;
    private static final String CONTEXT_PATH = "/test";
    private static final String WEB_APP_PATH = "webapps";
    private static final String CATALINA_HOME = "tomcat";
    Tomcat tomcat=new Tomcat();
    StandardContext context = new StandardContext();

    public EmbedTomcatHttp(int port){
        this.port=port;
    }

    public void init(){


        tomcat.setBaseDir(CATALINA_HOME);
        tomcat.setPort(port);
        //1.
        Server server=tomcat.getServer();
        server.setCatalinaHome(new File(CATALINA_HOME));
        server.setCatalinaBase(new File(CATALINA_HOME));
        //server.addService(service);
        //2.
        Service service=tomcat.getService();
        //service.setContainer(engine);
        //service.addConnector(connector);
        //3.
        Connector connector=tomcat.getConnector();
        //4.
        Engine engine = tomcat.getEngine();
        //engine.setRealm(new MemoryRealm());
        //engine.addChild(host);
        //5.
        Host host=tomcat.getHost();
        host.setAppBase(WEB_APP_PATH);
        //host.setRealm(new MemoryRealm());
        //6.
        context.setPath(CONTEXT_PATH);
        context.addLifecycleListener(new FixContextListener());
        //context.setRealm(new MemoryRealm());
        host.addChild(context);
        //7.
        addAPP();
    }

    public void addAPP(){
        try {
            tomcat.addWebapp("/", new File("tomcat/webapps").getAbsolutePath());
        } catch (ServletException e) {
            e.printStackTrace();
        }

        tomcat.addServlet(CONTEXT_PATH, "homeServlet", new HomeServlet());
        context.addServletMappingDecoded("/home", "homeServlet");
    }


    public void start(){
        init();
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
        tomcat.getServer().await();
        log.info("Tomcat started.");
    }
    public void stop()throws Exception{
        try{
            tomcat.stop();
        }catch(LifecycleException ex){
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw ex;
        }
        log.info("Tomcat stoped");
    }
    public static void main(String[] args) throws Exception {
        EmbedTomcatHttp embededTomcat = new EmbedTomcatHttp(9090);
        embededTomcat.start();
    }
}
