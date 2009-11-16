package jodconverter.proof;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class Jetty {


    public static class Instance {
        final Server server = new Server();
        
        Instance(int port, String war) {
            SocketConnector connector = new SocketConnector();
            connector.setMaxIdleTime(1000 * 60 * 60);
            connector.setSoLingerTime(-1);
            connector.setPort(port);
            server.setConnectors(new Connector[]{connector});
            WebAppContext context = new WebAppContext();
            context.setServer(server);
            context.setContextPath("/");
            context.setWar(war);
            server.addHandler(context);
            //server.setStopAtShutdown(true);
        }

        public void start() {
            try {
                server.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void stop() {
            try {
                server.stop();
                server.join();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Instance create(int port, String war) {
        return new Instance(port, war);
    }

    public static Instance create(String war) {
        return new Instance(8080, war);
    }

}
