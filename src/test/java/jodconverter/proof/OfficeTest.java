package jodconverter.proof;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class OfficeTest {


    @Test
    public void testStartStop() {
        Office office = new Office();
        assertTrue(office.isOsSupported());

        office.kill();
        assertFalse(office.isRunning());

        assertTrue(office.start());
        assertTrue(office.isRunning());

        assertTrue(office.kill());
        assertFalse(office.isRunning());
    }

}
