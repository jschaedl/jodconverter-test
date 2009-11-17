package jodconverter.proof;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public class DeepConverterTest {

    @Test
    public void deepTest() {
        DeepConverter ctx = new DeepConverter(10000, "http://localhost:8080/jodconverter-webapp-2.2.2/service");
        ctx.convert(getUserHome());
    }

    String getUserHome() {
        return System.getProperty("user.home");
    }

    String getResources() {
        URL resource = getClass().getClassLoader().getResource("deed.odt");
        assertNotNull(resource);
        return new File(resource.toExternalForm().substring("file:".length())).getParent();
    }
}
