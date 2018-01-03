package com.zj.https;

import com.zj.servlet.HomeServlet;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.Tomcat.FixContextListener;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class EmbedTomcatHttps {

    private final Log log= LogFactory.getLog(getClass());
    private int port;
    private int ports;
    private static final String CONTEXT_PATH = "/tomcat8";
    private static final String WEB_APP_PATH = "tomcat/webapps";
    private static final String CATALINA_HOME = "tomcat/";
    public static final String DEFAULT_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";

    Tomcat tomcat=new Tomcat();

    public EmbedTomcatHttps(int port, int ports){
        this.port=port;
        this.ports=ports;
    }

    public void start() {
        tomcat.setPort(port);
        tomcat.setBaseDir(CATALINA_HOME);
        tomcat.getHost().setAppBase(WEB_APP_PATH);
        tomcat.getHost().setAutoDeploy(true);

        Connector connector = new Connector(DEFAULT_PROTOCOL);
        connector.setPort(ports);

        Http11NioProtocol protocol = (Http11NioProtocol)connector.getProtocolHandler();
        protocol.setKeystorePass("123456");
        protocol.setKeystoreFile("e:/tmp/ssl/boot.keystore");
        protocol.setKeyAlias("mykey");
        protocol.setSSLEnabled(true);

        tomcat.getService().addConnector(connector);

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
        log.info("Tomcat started");
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
        EmbedTomcatHttps embededTomcat = new EmbedTomcatHttps(9080,9090);
        embededTomcat.start();
    }
}

