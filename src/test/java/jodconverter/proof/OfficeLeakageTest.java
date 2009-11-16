package jodconverter.proof;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class OfficeLeakageTest {

    final int TOTAL = 10000;
    final int BACKET = 5;


    final ClassLoader classLoader = getClass().getClassLoader();
    HttpProcessor processor;

    @Before
    public void beforeTest() {
        processor = new HttpProcessor(BACKET);
    }

    @After
    public void afterTest() {
        processor.shutdown();
    }

    /**
     * Very time consuming operation!
     * @throws Throwable on error
     */
    @Test
    public void proofLeakage() throws Throwable {
        //"deed.odt", "small.odt", ""ooo-manual.odt" 
        byte[] doc = getResourceBytes("deed.odt");
        ConverterPostProvider postProvider = new ConverterPostProvider("http://localhost:8080/service", doc, "application/pdf");
        Office office = new Office();
        if (!office.isRunning()) {
            assertTrue(office.start());
            Thread.sleep(30 * 1000); //office is loading...
        }
        Jetty.Instance jetty = Jetty.create(getWarUrl());
        jetty.start();
        try {
            int done = 0, backet = 0, remain = TOTAL;
            while (done < TOTAL) {
                while (remain > 0 && backet < BACKET) {
                    processor.submit(postProvider);
                    backet++;
                    remain--;
                }
                try {
                    Future<byte[]> now = processor.pool(1, TimeUnit.SECONDS);
                    if (now == null) {
                        System.out.println(done);
                    }
                    if (now != null) {
                        done++;
                        backet--;
                        now.get();
                    }
                } catch (ExecutionException e) {
                    throw e.getCause();
                }
            }
        } finally {
            jetty.stop();
        }
    }

    private String getWarUrl() {
        return classLoader.getResource("jodconverter-webapp-2.2.2.war").toExternalForm();
    }

    private byte[] getResourceBytes(String name) {
        try {
            return IOUtils.toByteArray(classLoader.getResourceAsStream(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
