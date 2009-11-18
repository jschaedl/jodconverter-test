package jodconverter.proof;

import org.junit.Test;

public class DeepConverterTest {

    @Test
    public void deepTest() {
        DeepConverter ctx = new DeepConverter(10000, "http://localhost:8080/jodconverter-webapp-2.2.2/service");
        ctx.convert(getUserHome());
    }

    String getUserHome() {
        return System.getProperty("user.home");
    }
}
