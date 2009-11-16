package jodconverter.proof;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class JettyTest {

    @Test
    public void jetty() throws IOException, ExecutionException, InterruptedException {
        ClassLoader loader = getClass().getClassLoader();
        Jetty.Instance jetty = Jetty.create(loader.getResource("jodconverter-webapp-2.2.2.war").toExternalForm());
        jetty.start();
        jetty.stop();
    }
}
