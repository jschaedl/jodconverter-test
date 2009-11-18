package jodconverter.proof;

import org.junit.Test;

public class DeepConverter3Test {

    @Test
    public void deepTest() {
        DeepConverter3 ctx = new DeepConverter3(10000, "http://localhost:8080/converter/service");
        ctx.convert(System.getProperty("user.home"));
    }
}