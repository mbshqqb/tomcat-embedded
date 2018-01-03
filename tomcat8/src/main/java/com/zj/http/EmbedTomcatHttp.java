package com.zj.http;

import com.zj.servlet.HomeServlet;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.Tomcat.FixContextListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class EmbedTomcatHttp {
    private final Log log= LogFactory.getLog(getClass());
    private int port = 9080;
    private static final String CONTEXT_PATH = "/tomcat8";
    private static final String WEB_APP_PATH = "tomcat/webapps";
    private static final String CATALINA_HOME = "tomcat/";
    Tomcat tomcat=new Tomcat();
    public EmbedTomcatHttp(int port){
        this.port=port;
    }

    public void start(){
        tomcat.setPort(port);
        tomcat.setBaseDir(CATALINA_HOME);
        tomcat.getHost().setAppBase(WEB_APP_PATH);
        tomcat.getHost().setAutoDeploy(true);
        StandardContext context = new StandardContext();
        context.setPath(CONTEXT_PATH);
        context.addLifecycleListener(new FixContextListener());
        tomcat.getHost().addChild(context);

        tomcat.addServlet(CONTEXT_PATH, "homeServlet", new HomeServlet());
        context.addServletMappingDecoded("/home", "homeServlet");
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
    public void setPort(int port){
        this.port=port;
    }
    public int getPort(){
        return this.port;
    }
    public static void main(String[] args) throws Exception {
        EmbedTomcatHttp embededTomcat = new EmbedTomcatHttp(7080);
        embededTomcat.start();
    }
}
